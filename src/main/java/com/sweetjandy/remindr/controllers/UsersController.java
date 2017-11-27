package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;


@Controller
public class UsersController {
    private UsersRepository usersRepository;
    private ContactsRepository contactsRepository;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UsersController(UsersRepository usersRepository, ContactsRepository contactsRepository
 ,PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.contactsRepository = contactsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model viewModel) {
        viewModel.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    // Errors validation has to be right after the object
    public String registerUser(@Valid User user, Errors validation, Model viewModel) {


        User existingUser = usersRepository.findByUsername(user.getUsername());

        Contact existingPhoneNumber = contactsRepository.findByPhoneNumber(user.getContact().getPhoneNumber());

        if (existingPhoneNumber != null) {
            validation.rejectValue(
                    "contact.phoneNumber",
                    "contact.phoneNumber",
                    "Phone number is already taken"
            );
        }

        if (existingUser != null) {
            validation.rejectValue(
                    "username",
                    "user.username",
                    "This email is already taken!"
            );
        }

        boolean validated = PhoneService.validatePhoneNumber(user.getContact().getPhoneNumber());
        if (!validated) {
            validation.rejectValue(
                    "contact.phoneNumber",
                    "contact.phoneNumber",
                    "Invalid format: (xxx)xxx-xxxx"
            );
        }

        if (user.getPassword().equals("")) {
            validation.rejectValue(
                    "password",
                    "password",
                    "Password cannot be blank"
            );
        }

        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("user", user);
            return "users/register";
        }


        //Brandon's previous code
//        user.setContact(contact);

        user.getContact().setGoogleContact((long) (Math.random() * (double) Long.MAX_VALUE));
        user.getContact().setOutlookContact((long) (Math.random() * (double) Long.MAX_VALUE));

        contactsRepository.save(user.getContact());

//   place the hashing encoder to storing password in a variable
        String hashPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(hashPassword);
        usersRepository.save(user);
        return "redirect:profile";
    }

//    @GetMapping("/login")
//    public String showLoginForm(Model viewModel) {
//
//        viewModel.addAttribute("user", new User());
//        return "users/login";
//    }
//
//    @PostMapping("/login")
//    public String loginUser(@Valid User user, Errors validation, Model viewModel) {
//
//        User existingUser = usersRepository.findByUsername(user.getUsername());
//
//        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
//            validation.rejectValue(
//                    "password",
//                    "password",
//                    "Username and password combination is incorrect"
//            );
//            viewModel.addAttribute("errors", validation);
//            viewModel.addAttribute("user", user);
//            return "users/login";
//        }
//
//        return "redirect:/profile";
//
//    }
//
//    @GetMapping("/logout")
//    public String logout() {
//
//        return "redirect:/login";
//
//    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("user", user);
        return "users/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfile(Model model) {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = usersRepository.findOne(2L);
        model.addAttribute("user", user);

        return "users/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile (@Valid User user, Errors validation, Model viewModel) {

        User currentUser = usersRepository.findOne(2L);

        boolean validated = PhoneService.validatePhoneNumber(user.getContact().getPhoneNumber());
        if (!validated) {
            validation.rejectValue(
                    "contact.phoneNumber",
                    "contact.phoneNumber",
                    "Invalid format: (xxx)xxx-xxxx"
            );
        }


//        PASSWORD VALIDATION
//        If current password field is not empty
        if (!user.getPassword().equals("")) {
//            If current password field does not equal current user's password
            if (!currentUser.getPassword().equals(user.getPassword())){
                validation.rejectValue(
                        "password",
                        "password",
                        "Current password is incorrect"
                );
            } else {

                // If new password field does not equal the confirm password field
                if(user.getNewPassword().equals("")) {
                    validation.rejectValue(
                            "newPassword",
                            "newPassword",
                            "New password cannot be blank"
                    );
                }
                else if (!user.getNewPassword().equals(user.getConfirmNewPassword())) {
                    validation.rejectValue(
                            "confirmNewPassword",
                            "confirmNewPassword",
                            "Password confirmation does not match"
                    );
                // if user is changing password and everything is ok
                } else {
                    // set new password to user
                    user.setPassword(user.getNewPassword());
                }

            }
        // if user does not want to change password
        } else {
            // transfer current user's password to new user object
            user.setPassword(currentUser.getPassword());
        }


        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("user", user);
            return "users/edit-profile";
        }

        contactsRepository.save(user.getContact());
        usersRepository.save(user);

        return "redirect:/profile";
    }
}

