package com.example.FirstSpring.Controllers;

import com.example.FirstSpring.models.*;
import com.example.FirstSpring.repository.*;
import com.example.FirstSpring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserManagerACRepository userManagerACRepository;
    @Autowired
    private ACRepository ACRepository;
    @Autowired
    private UserACRepository userACRepository;
    @Autowired
    private TemporaryUserManagerRepository temporaryUserManagerRepository;

    @GetMapping("/admin")
    public String userList(@AuthenticationPrincipal User user,
                           Model model) {
        model.addAttribute("allUsers", userService.allUsers());
        model.addAttribute("user",user);
        return "admin";
    }

    @PostMapping("/admin")
    public String  deleteUser(@AuthenticationPrincipal User user,
                              @RequestParam(required = true, defaultValue = "" ) Long userId,
                              @RequestParam(required = true, defaultValue = "" ) String action,
                              Model model) {
        if (action.equals("delete")){
            userService.deleteUser(userId);
        }
        if (action.equals("set_role")) {
            User usr = userService.findUserById(userId);
            usr.getRoles().add(new Role(3L,"ROLE_MANAGER"));
            userRepository.save(usr);
        }
        if (action.equals("delete_role")) {
            User usr = userService.findUserById(userId);
            List<UserManagerAC> userManagerACS = userManagerACRepository.findByManager(usr);
            for(UserManagerAC ac : userManagerACS){
                userManagerACRepository.delete(ac);
            }
            usr.getRoles().clear();
            usr.getRoles().add(new Role(1L,"ROLE_USER"));
            userRepository.save(usr);
        }
        model.addAttribute("user",user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/acs")
    public String  setCoin(@AuthenticationPrincipal User user,
                           Model model) {
        model.addAttribute("allACs", ACRepository.findAll());
        model.addAttribute("user",user);
        return "adminACs";
    }

    @GetMapping("/admin/castAC")
    public String  makeCoin(@AuthenticationPrincipal User user,
                           Model model) {
        model.addAttribute("user",user);
        return "adminACCast";
    }

    @PostMapping("/admin/castAC")
    public String  addCoin(@AuthenticationPrincipal User user,
                            @RequestParam String name,
                            @RequestParam String cost,
                            Model model) {
        AC AC = new AC(name,Double.parseDouble(cost));
        if(AC != null) {
            ACRepository.save(AC);
        }
        model.addAttribute("user",user);
        return "redirect:/admin/acs";
    }

    @PostMapping("/admin/deleteAC")
    public String  delCoin(@AuthenticationPrincipal User user,
                           @RequestParam Long acId,
                           Model model) {
        AC AC = ACRepository.findById(acId).orElse(new AC());
        List<UserAC> userACS = userACRepository.findByAC(AC);
        List<UserManagerAC> userManagerACS = userManagerACRepository.findByAC(AC);
        List<TemporaryUserManager> temporaryUserManagers = temporaryUserManagerRepository.findByAC(AC);
        for(UserAC val : userACS) {
            userACRepository.delete(val);
        }
        for(UserManagerAC val : userManagerACS) {
            userManagerACRepository.delete(val);
        }
        for(TemporaryUserManager val : temporaryUserManagers) {
            temporaryUserManagerRepository.delete(val);
        }
        ACRepository.delete(AC);
        return "redirect:/admin";
    }

    @GetMapping("/admin/gt/{userId}")
    public String  gtUser(@AuthenticationPrincipal User user,
                          @PathVariable("userId") Long userId, Model model) {
        model.addAttribute("allUsers", userService.usergtList(userId));
        model.addAttribute("user",user);
        return "admin";
    }
}
