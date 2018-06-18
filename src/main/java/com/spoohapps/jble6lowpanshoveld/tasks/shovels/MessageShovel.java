package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import java.util.List;

public interface MessageShovel {
    void start();
    void stop();
    void onStopped(Runnable stopped);
    List<String> getConnectionDescriptions();
    String getName();
    String getType();
    MessageShovel clone();
}
