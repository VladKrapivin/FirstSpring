package com.example.FirstSpring.Controllers;

import com.example.FirstSpring.models.*;
import com.example.FirstSpring.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    private ACRepository ACRepository;

    @Autowired
    private UserRepository UserRepository;
    @Autowired
    private UserACRepository UserACRepository;

    @Autowired
    private UserManagerACRepository userManagerACRepository;

    @Autowired
    private TemporaryUserManagerRepository temporaryUserManagerRepository;

    @GetMapping("/")
    public String first( Model model) {
        return "firstPage";
    }

    @GetMapping("/main")
    public String main(@AuthenticationPrincipal User user,
                       Model model) {
        List<UserAC> userACS = UserACRepository.findByUser(user);
        model.addAttribute("userac", userACS);
        model.addAttribute("user",user);
        return "main";
    }

    @GetMapping("/mainCash")
    public String mainCash(@AuthenticationPrincipal User user,
                       Model model) {
        List<UserAC> userACS = UserACRepository.findByUser(user);
        if(user.getCash() == null) {
            user.setCash(5.0);
            UserRepository.save(user);
        }
        else {
            double cur = user.getCash();
            user.setCash(cur + 5.0);
            UserRepository.save(user);
        }
        return "redirect:/main";
    }

    @GetMapping("/market")
    public String market( @AuthenticationPrincipal User user,
                          Model model) {
        Iterable<AC> AC = ACRepository.findAll();
        model.addAttribute("AC",AC);
        model.addAttribute("user",user);
        return "market";
    }

    @GetMapping("/market/{id}")
    public String BuyAC(@AuthenticationPrincipal User user,
                          @PathVariable(value = "id")Long id, Model model) {
        Optional<AC> AC = ACRepository.findById(id);
        ArrayList<AC> res = new ArrayList<>();
        AC.ifPresent(res::add);
        model.addAttribute("AC", res);
        model.addAttribute("user",user);
        return "marketAC";
    }

    @PostMapping("/market/{id}")
    public String BuyChange(@AuthenticationPrincipal User user,
                            @PathVariable(value = "id")Long id,
                            @RequestParam String count,
                            Model model) {
        AC AC = ACRepository.findById(id).orElseThrow();
        UserAC userAC = UserACRepository.findByUserAndAC(user, AC);
        double plus = Double.parseDouble(count); //количество купленных монет
        double val = plus* AC.getCost(); //стоимость покупки
        if(userAC != null && val <= user.getCash()) {
            double cur = userAC.getCount();
            cur += plus;
            userAC.setCount(cur);
            UserACRepository.save(userAC);
            user.setCash(user.getCash()-val);
            UserRepository.save(user);
            return "redirect:/market";
        }
        else if(userAC == null && val <= user.getCash()){
            UserAC newUserAC = new UserAC(user, AC,Double.parseDouble(count));
            UserACRepository.save(newUserAC);
            user.setCash(user.getCash()-val);
            UserRepository.save(user);
            return "redirect:/market";
        }
        else {
            return "redirect:/market";
        }
    }

    @GetMapping("/main/sell/{id}")
    public String SellAC(@AuthenticationPrincipal User user,
                           @PathVariable(value = "id")Long id, Model model) {
        Optional<UserAC> ac = UserACRepository.findById(id);
        ArrayList<UserAC> res = new ArrayList<>();
        ac.ifPresent(res::add);
        model.addAttribute("AC", res);
        model.addAttribute("user",user);
        return "sellAC";
    }

    @PostMapping("/main/sell/{id}")
    public String SellChange(@AuthenticationPrincipal User user,
                            @PathVariable(value = "id")Long id,
                            @RequestParam String count,
                            Model model) {
        UserAC userac = UserACRepository.findById(id).orElseThrow();
        if(userac.getCount()<Double.parseDouble(count)) {
            return "redirect:/main";
        }
        double coinCost = userac.getAC().getCost(); //стоимость продаваемой монеты
        double plus = Double.parseDouble(count); //количество проданных монет
        double val = (plus*coinCost)+user.getCash(); //новый баланс
        if(userac.getCount().equals(plus)) {
            user.setCash(val);
            UserRepository.save(user);
            UserACRepository.delete(userac);
            return "redirect:/main";
        }
        else {
            user.setCash(val);
            UserRepository.save(user);
            userac.setCount(userac.getCount()-plus);
            UserACRepository.save(userac);
            return "redirect:/main";
        }
    }

    @GetMapping("/managerInfo")
    public String manager( @AuthenticationPrincipal User user,
                          @RequestParam Long acId,
                          Model model) {
        Optional<AC> ac = ACRepository.findById(acId);
        UserManagerAC uBC = userManagerACRepository.findByUserAndAC(user,ac.get());
        TemporaryUserManager tUB = temporaryUserManagerRepository.findByUserAndAC(user, ac.get());
        model.addAttribute("user",user);
        model.addAttribute("AC",ac.get());
        if(uBC != null) {
         return "hasManager";
        }
        if(tUB != null) {
            return "hasRequest";
        }
        else {
            return "managerInfo";
        }
    }

    @PostMapping("/managerInfo")
    public String managerRequest( @AuthenticationPrincipal User user,
                                 @RequestParam Long acId,
                                 Model model) {
        Optional<AC> ac = ACRepository.findById(acId);
        TemporaryUserManager temporaryUserManager = new TemporaryUserManager(user,ac.get());
        temporaryUserManagerRepository.save(temporaryUserManager);
            return "redirect:/main";
    }

    @PostMapping("/hasManager")
    public String hasManager( @AuthenticationPrincipal User user,
                                 @RequestParam Long acId,
                                 Model model) {
        Optional<AC> ac = ACRepository.findById(acId);
        UserManagerAC uBC = userManagerACRepository.findByUserAndAC(user,ac.get());
        if(uBC != null) {
            userManagerACRepository.delete(uBC);
        }
        return "redirect:/main";
    }

    @PostMapping("/hasRequest")
    public String hasRequest( @AuthenticationPrincipal User user,
                             @RequestParam Long acId,
                             Model model) {
        Optional<AC> ac = ACRepository.findById(acId);
        TemporaryUserManager temporaryUserManager = temporaryUserManagerRepository.findByUserAndAC(user,ac.get());
        if(temporaryUserManager != null) {
            temporaryUserManagerRepository.delete(temporaryUserManager);
        }
        return "redirect:/main";
    }
}