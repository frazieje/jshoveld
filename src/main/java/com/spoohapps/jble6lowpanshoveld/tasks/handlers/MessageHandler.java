package com.spoohapps.jble6lowpanshoveld.tasks.handlers;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.MessageConnectionSettings;

public interface MessageHandler {
    void start();
    void stop();
    boolean isRunning();
    void handleMessage(Message message);
}
