package org.ebuy.model;

/**
 * Created by Ozgur Ustun on May, 2020
 */
public class TokenMail {

    private String email;
    private String token;

    public TokenMail() {
    }

    public TokenMail(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
