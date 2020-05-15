package org.ebuy.controller;

import org.ebuy.dto.ChangePasswordDto;
import org.ebuy.dto.ResetPasswordDto;
import org.ebuy.dto.UserDto;
import org.ebuy.exception.PasswordNotMatchingException;
import org.ebuy.exception.UserExistException;
import org.ebuy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Ozgur Ustun on May, 2020
 */

@RestController
@RequestMapping("/auth")
public class UserRegisterController {

    private final UserService userService;

    @Autowired
    public UserRegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Message<String> registerUser(@RequestBody UserDto userDto) {
        return userService.registerNewUserAccount(userDto);
    }

    @PostMapping("/confirm")
    public Message<String> confirmUserAccount(@RequestParam("t") String confirmationToken) {
        return userService.confirmUserAccount(confirmationToken);
    }

    @PostMapping("/reset-password")
    public Message<String> sendPasswordResetMail(@RequestParam String email) throws UserExistException {
        return userService.resetUserPassword(email);
    }

    @PostMapping("/reset-password-confirm")
    public Message<String> resetUserPassword(@RequestParam("token") String token, @RequestBody ResetPasswordDto passwordDto) throws PasswordNotMatchingException {
        return userService.confirmUserResetPassword(token, passwordDto);
    }

    @PostMapping("/change-password")
    public Message<String> changeUserPassword(@RequestBody ChangePasswordDto changePasswordDto) throws PasswordNotMatchingException {
        return userService.changeUserPassword(changePasswordDto);
    }
}
