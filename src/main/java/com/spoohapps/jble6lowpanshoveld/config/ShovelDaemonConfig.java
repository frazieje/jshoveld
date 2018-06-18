package com.spoohapps.jble6lowpanshoveld.config;

public interface ShovelDaemonConfig {
    String profileFilePath();
    String apiHost();
    String nodeHost();
    int apiPort();
    int nodePort();
    int controllerPort();
    ShovelDaemonConfig apply(ShovelDaemonConfig other);
}
