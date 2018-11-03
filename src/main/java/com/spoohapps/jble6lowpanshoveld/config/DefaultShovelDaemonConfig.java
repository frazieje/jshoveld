package com.spoohapps.jble6lowpanshoveld.config;

import java.nio.file.Paths;

public class DefaultShovelDaemonConfig implements ShovelDaemonConfig {

    @Override
    public String profileFilePath() {
        return Paths.get(System.getProperty("user.home"), "jble6lowpanshoveld", "profile.conf").toString();
    }

    @Override
    public String apiHost() {
        return "";
    }

    @Override
    public String nodeHost() {
        return "";
    }

    @Override
    public int apiPort() {
        return 5671;
    }

    @Override
    public int nodePort() {
        return 5671;
    }

    @Override
    public int controllerPort() {
        return 8080;
    }

}
