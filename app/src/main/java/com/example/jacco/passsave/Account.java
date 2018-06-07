package com.example.jacco.passsave;

/**
 * Created by Jacco on 7-6-2018.
 */

public class Account {

    public String username;
    public String password;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
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
