package ru.apolyakov.social_network.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.apolyakov.social_network.dto.ProfileDto;
import ru.apolyakov.social_network.dto.ProfileValidator;
import ru.apolyakov.social_network.service.UserServiceImpl;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final ProfileValidator appUserValidator;

    // Set a form validator
    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        // Form target
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        System.out.println("Target=" + target);

        if (target.getClass() == ProfileValidator.class) {
            dataBinder.setValidator(appUserValidator);
        }
        // ...
    }


    @GetMapping("/")
    public String root(Model model) {
        ProfileDto profileDto = userService.loadProfile();
        model.addAttribute("appUser", profileDto);
        return "/index";
    }

    @GetMapping("/index")
    public String home(Model model) {
        ProfileDto profileDto = userService.loadProfile();
        model.addAttribute("appUser", profileDto);
        return "/index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "/admin";
    }

    @GetMapping("/user")
    public String user() {
        return "/user";
    }

    @GetMapping("/about")
    public String about() {
        return "/about";
    }

    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @GetMapping("/registerSuccessful")
    public String viewRegisterSuccessful(Model model) {
        return "registerSuccessfulPage";
    }

    // Show Register page.
    @GetMapping("/register")
    public String viewRegister(Model model) {
        ProfileDto profileDto = new ProfileDto();
//        List<Country> countries = countryDAO.getCountries();
        model.addAttribute("appUser", profileDto);
//        model.addAttribute("countries", countries);
        return "register";
    }

    // This method is called to save the registration information.
    // @Validated: To ensure that this Form
    // has been Validated before this method is invoked.
    @PostMapping("/register")
    public String saveRegister(Model model, //
                               @ModelAttribute("appUser") @Validated ProfileDto appUserForm, //
                               BindingResult result, //
                               final RedirectAttributes redirectAttributes) {

        // Validate result
        if (result.hasErrors()) {
//            List<Country> countries = countryDAO.getCountries();
//            model.addAttribute("countries", countries);
            return "register";
        }
        ProfileDto newUser = null;
        try {
            newUser = userService.register(appUserForm);
            newUser.setPassword(Strings.EMPTY);
        }
        // Other error!!
        catch (Exception e) {
            //List<Country> countries = countryDAO.getCountries();
            //model.addAttribute("countries", countries);
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "register";
        }

        redirectAttributes.addFlashAttribute("flashUser", newUser);

        return "redirect:/registerSuccessful";
    }

    @GetMapping("/403")
    public String error403() {
        return "/error/access-denied";
    }

}
