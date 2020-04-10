package ru.apolyakov.social_network.dto;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.apolyakov.social_network.service.UserServiceImpl;

@Component
public class ProfileValidator  implements Validator {
    // common-validator library.
    private EmailValidator emailValidator = EmailValidator.getInstance();

    @Autowired
    private UserServiceImpl userService;

    // The classes are supported by this validator.
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == ProfileDto.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProfileDto appUserForm = (ProfileDto) target;

        // Check the fields of AppUserForm.
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "NotEmpty.appUser.login");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty.appUser.firstName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secondName", "NotEmpty.appUser.secondName");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.appUser.email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.appUser.password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotEmpty.appUser.confirmPassword");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "NotEmpty.appUser.gender");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "countryCode", "NotEmpty.appUser.countryCode");

        if (!this.emailValidator.isValid(appUserForm.getLogin())) {
            // Invalid email.
            errors.rejectValue("login", "Pattern.appUser.email");
        }
//        else if (appUserForm.getUserId() == null) {
//            AppUser dbUser = appUserDAO.findAppUserByEmail(appUserForm.getEmail());
//            if (dbUser != null) {
//                // Email has been used by another account.
//                errors.rejectValue("email", "Duplicate.appUserForm.email");
//            }
//        }

        if (!errors.hasFieldErrors("userName")) {
            UserDto dbUser = userService.findUserByLogin(appUserForm.getLogin());
            if (dbUser != null) {
                // Username is not available.
                errors.rejectValue("userName", "Duplicate.appUser.userName");
            }
        }

        if (!errors.hasErrors()) {
            if (!appUserForm.getConfirmPassword().equals(appUserForm.getPassword())) {
                errors.rejectValue("confirmPassword", "Match.appUser.confirmPassword");
            }
        }
    }
}
