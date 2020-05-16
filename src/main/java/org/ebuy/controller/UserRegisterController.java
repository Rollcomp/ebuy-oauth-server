package org.ebuy.controller;

import org.ebuy.dto.UserDto;
import org.ebuy.exception.PasswordNotMatchingException;
import org.ebuy.exception.UserExistException;
import org.ebuy.mapper.UserMapper;
import org.ebuy.model.request.ChangePasswordRequest;
import org.ebuy.model.request.ResetPasswordRequest;
import org.ebuy.model.request.UserRegisterRequest;
import org.ebuy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Ozgur Ustun on May, 2020
 */

@RestController
@RequestMapping("/auth")
public class UserRegisterController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserRegisterController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {
        return ResponseEntity.ok(userMapper.userToUserDto(userService.registerNewUserAccount(userRegisterRequest)));
    }

    @PostMapping("/confirm")
    public ResponseEntity<UserDto> confirmUserAccount(@RequestParam("t") String activationKey) {
        return ResponseEntity.ok(userMapper.userToUserDto(userService.confirmUserAccount(activationKey)));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<UserDto> sendPasswordResetMail(@RequestParam String email) throws UserExistException {
        //Real implementation client's reset password url will be sent to user. Client sent ResetPasswordRequest to server with reset password token as url params t
        return ResponseEntity.ok(userMapper.userToUserDto(userService.resetUserPassword(email)));
    }

    @PostMapping("/reset-password-confirm")
    public ResponseEntity<UserDto> resetUserPassword(@RequestParam("token") String token, @RequestBody ResetPasswordRequest passwordDto) throws PasswordNotMatchingException {
        return ResponseEntity.ok(userMapper.userToUserDto(userService.confirmUserResetPassword(token, passwordDto)));
    }

    @PostMapping("/change-password")
    public ResponseEntity<UserDto> changeUserPassword(@RequestBody ChangePasswordRequest changePasswordRequest) throws PasswordNotMatchingException {
        return ResponseEntity.ok(userMapper.userToUserDto(userService.changeUserPassword(changePasswordRequest)));
    }
}
