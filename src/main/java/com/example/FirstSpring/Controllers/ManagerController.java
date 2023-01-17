package com.example.FirstSpring.Controllers;

import com.example.FirstSpring.models.*;
import com.example.FirstSpring.repository.*;
import com.example.FirstSpring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ManagerController {
    @Autowired
    UserService userService;
    @Autowired
    TemporaryUserManagerRepository temporaryUserManagerRepository;

    @Autowired
    UserManagerACRepository userManagerACRepository;

    @Autowired
    ACRepository ACRepository;
    @Autowired
    UserACRepository userACRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/manager")
    public String startManagerPage(@AuthenticationPrincipal User user,
                                  Model model) {
        model.addAttribute("AC", temporaryUserManagerRepository.findAll());
        model.addAttribute("user",user);
        return "manager";
    }

    @PostMapping("/manager")
    public String requestOK(@AuthenticationPrincipal User user,
                                  @RequestParam Long clientId,
                                  @RequestParam Long acId,
                                  Model model) {
        User client = userService.findUserById(clientId);
        Optional<AC> ac = ACRepository.findById(acId);
        TemporaryUserManager tUB = temporaryUserManagerRepository.findByUserAndAC(client, ac.get());
        temporaryUserManagerRepository.delete(tUB);
        UserManagerAC UBC = new UserManagerAC(client,user,ac.get());
        userManagerACRepository.save(UBC);
        model.addAttribute("user",user);
        return "redirect:/manager";
    }

    //недоделана
    @GetMapping("/manager/clients")
    public String userList(@AuthenticationPrincipal User user,
                           Model model) {
        List<UserManagerAC> uBCList = userManagerACRepository.findByManager(user);
        List<UserAC> UCLIST = new ArrayList<>();
        for(UserManagerAC ac : uBCList) {
            UserAC UC = userACRepository.findByUserAndAC(ac.getUser(), ac.getAC());
            if(UC != null) {
                UCLIST.add(UC);
            }
        }
        model.addAttribute("allClients", UCLIST);
        model.addAttribute("user",user);
        return "managerClients";
    }

    @PostMapping("/manager/clients/buy")
    public String buyAC(@AuthenticationPrincipal User user,
                            @RequestParam String count,
                            @RequestParam Long userId,
                            @RequestParam Long acId,
                            Model model) {
        if(count.isEmpty()) {
            return "redirect:/manager/clients";
        }
        AC AC = ACRepository.findById(acId).orElse(new AC());
        User client = userService.findUserById(userId);
        UserAC userAC = userACRepository.findByUserAndAC(client, AC);
        double plus = Double.parseDouble(count); //количество купленных монет
        double val = plus* AC.getCost(); //стоимость покупки
        if(userAC != null && val <= client.getCash()) {
            double cur = userAC.getCount();
            cur += plus;
            userAC.setCount(cur);
            userACRepository.save(userAC);
            client.setCash(client.getCash()-val);
            userRepository.save(user);
            return "redirect:/manager/clients";
        }
        else {
            return "redirect:/manager";
        }
    }

    @PostMapping("/manager/clients/off")
    public String clientOff(@AuthenticationPrincipal User user,
                          @RequestParam Long userId,
                          @RequestParam Long acId,
                          Model model) {
        AC AC = ACRepository.findById(acId).orElseThrow();
        User client = userService.findUserById(userId);
        UserManagerAC uBC = userManagerACRepository.findByUserAndAC(client, AC);
        if(uBC != null) {
            userManagerACRepository.delete(uBC);
        }
        return "redirect:/manager/clients";
    }
}
