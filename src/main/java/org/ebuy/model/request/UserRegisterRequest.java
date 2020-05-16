package org.ebuy.model.request;

import org.ebuy.model.Authority;

import java.util.Set;

/**
 * Created by Ozgur Ustun on May, 2020
 */
public class UserRegisterRequest {

    private String email;
    private String password;

    public UserRegisterRequest(String email, String password, boolean enabled, Set<Authority> authorities) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
