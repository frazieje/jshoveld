package com.spoohapps.jble6lowpanshoveld.tasks.handlers;

import com.spoohapps.jble6lowpanshoveld.model.Message;

public interface MessageHandler {
    void start();
    void stop();
    int openConnections();
    int totalConnections();
    void handleMessage(Message message);
}
