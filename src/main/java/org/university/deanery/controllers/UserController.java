package org.university.deanery.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.university.deanery.dtos.ChangePasswordDto;
import org.university.deanery.dtos.SignUpDto;
import org.university.deanery.exceptions.*;
import org.university.deanery.models.User;
import org.university.deanery.services.UserService;

import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/sign-in")
    public String signIn(Model model) {
        String success = (String) model.getAttribute("succes");
        String error = (String) model.getAttribute("error");
        if (error != null)
            model.addAttribute("error", error);
        if (success != null)
            model.addAttribute("success", success);
        return "users/sign-in";
    }

    @GetMapping("/sign-up")
    public String signUp(Model model) {
        String success = (String) model.getAttribute("succes");
        String error = (String) model.getAttribute("error");
        if (error != null)
            model.addAttribute("error", error);
        if (success != null)
            model.addAttribute("success", success);
        return "users/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute("signUpDto") SignUpDto signUpDto, RedirectAttributes redirectAttributes) {
        User user = (User) userService.loadUserByUsername(signUpDto.getUsername());
        String message = "";
        try {
            if (userService.findUserByEmail(signUpDto.getEmail()) != null)
                throw new UserEmailAlreadyExistsException();
            if (user != null)
                throw new UserUsernameAlreadyExistsException();
            if (!signUpDto.getPassword().equals(signUpDto.getPasswordConfirm()))
                throw new PasswordsMismatchException();
            if (signUpDto.getPassword().length() < UserService.passwordLength)
                throw new PasswordLengthException();
            userService.saveUser(SignUpDto.toUser(signUpDto));
            message = "Пользователь " + signUpDto.getUsername() + " успешно создан!";
            redirectAttributes.addAttribute("success", message);
        } catch (UserUsernameAlreadyExistsException e) {
            message = "Пользователь с именем " + signUpDto.getUsername() + " уже существует!";
            redirectAttributes.addAttribute("error", message);
            return "redirect:/sign-up";
        } catch (PasswordsMismatchException e) {
            message = "Пароли не совпадают!";
            redirectAttributes.addAttribute("error", message);
            return "redirect:/sign-up";
        } catch (PasswordLengthException e) {
            message = "Длина пароля должна быть больше " + UserService.passwordLength + " символов!";
            redirectAttributes.addAttribute("error", message);
            return "redirect:/sign-up";
        } catch (UserEmailAlreadyExistsException e) {
            message = "Пользователь с электронной почтой " + signUpDto.getEmail() + " уже существует!";
            redirectAttributes.addAttribute("error", message);
            return "redirect:/sign-up";
        }

        return "redirect:/sign-in";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "users/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("changePasswordDto") ChangePasswordDto changePasswordDto, RedirectAttributes redirectAttributes) {
        String message;
        try {
            User user = userService.findUserByEmail(changePasswordDto.getEmail());
            if (userService.findUserByEmail(changePasswordDto.getEmail()) == null)
                throw new UserEmailNotFoundException();
            if (user == null)
                throw new UserUsernameNotFoundException();
            if (changePasswordDto.passwordOld().equals(changePasswordDto.passwordNew()))
                throw new PasswordMustBeNewException();
            if (!changePasswordDto.passwordNew().equals(changePasswordDto.passwordConfirm()))
                throw new PasswordsMismatchException();
            if (changePasswordDto.getPasswordNew().length() < UserService.passwordLength)
                throw new PasswordLengthException();
            userService.changeUserPassword(user, changePasswordDto.getPasswordNew());
            message = "Пароль успешно изменен!";
            redirectAttributes.addFlashAttribute("success", message);
        } catch (UserEmailNotFoundException e) {
            message = "Пользователь с электронной почтой " + changePasswordDto.email() + " не найден!";
            redirectAttributes.addFlashAttribute("error", message);
        } catch (UserUsernameNotFoundException e) {
            message = "Пользователь с именем " + changePasswordDto.username() + " не найден!";
            redirectAttributes.addFlashAttribute("error", message);
        } catch (PasswordsMismatchException ex) {
            message = "Пароли не совпадают!";
            redirectAttributes.addFlashAttribute("error", message);
        } catch (PasswordMustBeNewException e) {
            message = "Новый пароль не может быть таким же, как старый!";
            redirectAttributes.addFlashAttribute("error", message);
        } catch (PasswordLengthException e) {
            message = "Длина пароля должна быть больше " + UserService.passwordLength + " символов!";
            redirectAttributes.addFlashAttribute("error", message);
        }
        return "redirect:/change-password";
    }

    @GetMapping("/find-all-blocked")
    public String findAllBlocked(@AuthenticationPrincipal User authenticatedUser, Model model) {
        String error = (String) model.getAttribute("error");
        String warning = (String) model.getAttribute("warning");
        if (warning != null)
            model.addAttribute("warning", warning);
        if (error != null)
            model.addAttribute("error", error);
        model.addAttribute("users", userService.findAll().stream()
                .filter(u -> !u.getUsername().equals(authenticatedUser.getUsername()))
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList()));
        return "users/find-all-blocked";
    }

    @PatchMapping("/lock-account/{id}")
    public String setAccountNonLocked(@PathVariable Long id, @ModelAttribute("status") boolean status, RedirectAttributes redirectAttributes) {
        String message;
        try {
            User user = userService.findUserById(id).orElseThrow(UserNotFoundException::new);
            userService.setAccountNonLocked(user, status);
            message = status
                    ? "Пользователь с id: " + id + " разблокирован"
                    : "Пользователь с id: " + id + " заблокирован!";
            redirectAttributes.addFlashAttribute("warning", message);
        } catch (UserNotFoundException e) {
            message = "Пользователь с id: " + id + " не найден!";
            redirectAttributes.addFlashAttribute("error", message);
        }
        return "redirect:/find-all-blocked";
    }
}
