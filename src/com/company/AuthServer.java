package com.company;

public interface AuthServer {
    void start();
    String getNickByLoginPass(String login, String pass);
    void stop();
}