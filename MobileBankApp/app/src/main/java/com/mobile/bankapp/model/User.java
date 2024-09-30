package com.mobile.bankapp.model;

public class User {
    private String email;
    private String password;
    private int securityQNum;
    private String securityAnswer;

    public User() {}

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

    public int getSecurityQNum() {
        return securityQNum;
    }

    public void setSecurityQNum(int securityQNum) {
        this.securityQNum = securityQNum;
    }

    public String getSecuirtyAnswer() {
        return securityAnswer;
    }

    public void setSecuirtyAnswer(String secuirtyAnswer) {
        this.securityAnswer = secuirtyAnswer;
    }
}
