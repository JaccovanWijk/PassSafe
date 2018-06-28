package com.example.jacco.passsave;

import java.io.Serializable;

/*
 Model class for Account object.
 */

public class Account implements Serializable{

    public Account() {
    }

    public String account;
    public String username;
    public String password;

    public Account(String account, String username, String password) {
        this.account = account;
        this.username = username;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
