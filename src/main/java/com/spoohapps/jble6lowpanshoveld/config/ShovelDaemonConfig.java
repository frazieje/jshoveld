package com.spoohapps.jble6lowpanshoveld.config;

public interface ShovelDaemonConfig {
    String profileFilePath();
    String apiUrl();
    String apiExchange();
    int apiPort();
    int localPort();
    String incomingExchange();
    String deviceExchange();
    String appExchange();
    String outgoingExchange();
    ShovelDaemonConfig apply(ShovelDaemonConfig other);
}
