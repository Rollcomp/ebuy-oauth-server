package org.ebuy.service;

import lombok.extern.slf4j.Slf4j;
import org.ebuy.model.Authority;
import org.ebuy.model.TokenMail;
import org.ebuy.exception.PasswordNotMatchingException;
import org.ebuy.exception.TokenNotValidException;
import org.ebuy.exception.UserExistException;
import org.ebuy.kafka.Sender;
import org.ebuy.mapper.UserMapper;
import org.ebuy.model.request.ChangePasswordRequest;
import org.ebuy.model.request.ResetPasswordRequest;
import org.ebuy.entity.User;
import org.ebuy.model.request.UserRegisterRequest;
import org.ebuy.repository.UserRepository;
import org.ebuy.util.TokenUtil;
import org.ebuy.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Ozgur Ustun on May, 2020
 */

@Service
@Slf4j
public class UserService {

    @Value("${spring.kafka.topic.userRegistered}")
    private String registerUserTopic;

    @Value("${spring.kafka.topic.userPassword}")
    private String resetPasswordTopic;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Sender sender;
    private final UserUtil userUtil;
    private final TokenUtil tokenUtil;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Sender sender, UserUtil userUtil, TokenUtil tokenUtil, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sender = sender;
        this.userUtil = userUtil;
        this.tokenUtil = tokenUtil;
        this.userMapper = userMapper;
    }

    private void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    private User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
        if (!user.isPresent()) {
            throw new UserExistException("User is not exist with this email: " + user.get().getEmail());
        }
        return user.get();
    }

    public User registerNewUserAccount(UserRegisterRequest userRegisterRequest) {

        //Check whether user exist
        userUtil.isEmailExist(userRegisterRequest.getEmail());

        User registeredUser = userMapper.userRegReqToUser(userRegisterRequest);
        registeredUser.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        registeredUser.setEnabled(false);
        registeredUser.setAuthorities(new HashSet<Authority>(Collections.singleton(Authority.REGISTERED_USER)));
        registeredUser.setActivationKey(UUID.randomUUID().toString());
        registeredUser.setActivationKeycreatedTime(LocalDateTime.now());

        TokenMail tokenMail = new TokenMail(registeredUser.getEmail(), registeredUser.getActivationKey());
        sender.sendMail(tokenMail, registerUserTopic).addCallback(new ListenableFutureCallback<SendResult<String, TokenMail>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.debug("New User Register Message has not been sent to kafka with email: " + tokenMail.getEmail());
            }

            @Override
            public void onSuccess(SendResult<String, TokenMail> result) {
                log.debug("New User Register Message has been sent to kafka with mail: " + tokenMail.getEmail());
                userRepository.save(registeredUser);
            }
        });

        return registeredUser;
    }

    public User confirmUserAccount(String activationKey) {
        Optional<User> user = userRepository.findByActivationKey(activationKey);

        if (!user.isPresent() || !tokenUtil.isValidActivationKey(user.get())) {
            throw new TokenNotValidException("Token is not valid!");
        }
        user.get().setEnabled(true);
        saveRegisteredUser(user.get());
        return user.get();
    }

    public User resetUserPassword(String userEmail) {

        User user = findUserByEmail(userEmail);
        user.setResetPasswordKey(UUID.randomUUID().toString());
        user.setResetPasswordKeycreatedTime(LocalDateTime.now());

        TokenMail tokenMail = new TokenMail(user.getEmail(), user.getResetPasswordKey());
        sender.sendMail(tokenMail, resetPasswordTopic).addCallback(new ListenableFutureCallback<SendResult<String, TokenMail>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.debug("Password Reset Message has not been sent to kafka with email: " + tokenMail.getEmail());
            }

            @Override
            public void onSuccess(SendResult<String, TokenMail> result) {
                log.debug("Password Reset Message has been sent to kafka with mail: " + tokenMail.getEmail());
                userRepository.save(user);
            }
        });

        return user;
    }

    public User confirmUserResetPassword(String resetPasswordToken, ResetPasswordRequest passwordDto) throws PasswordNotMatchingException {
        Optional<User> user = userRepository.findByResetPasswordKey(resetPasswordToken);
        if (!user.isPresent() || !tokenUtil.isValidresetPasswordKey(user.get())) {
            throw new TokenNotValidException("Token is not valid!");
        }
        if (!passwordDto.getNewPassword().equals(passwordDto.getNewPasswordAgain())) {
            throw new PasswordNotMatchingException("Password not matching!");
        }
        user.get().setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(user.get());
        return user.get();
    }

    public User changeUserPassword(@RequestBody ChangePasswordRequest changePasswordRequest) throws PasswordNotMatchingException {
        //TODO: User should be logged in to change password. Control will be added
        User user = findUserByEmail(changePasswordRequest.getUserEmail());
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new PasswordNotMatchingException("Old password and new password not matching!");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        return user;
    }
}
