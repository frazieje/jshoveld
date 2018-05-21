package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

public interface MessageShovel {
    void start();
    void stop();
    int openConnections();
    int totalConnections();
}
