package com.spoohapps.jble6lowpanshoveld.config;

import com.spoohapps.farcommon.config.ConfigFlags;

public interface ShovelDaemonConfig {
    @ConfigFlags({"profileFilePath", "f"})
    String profileFilePath();

    @ConfigFlags({"apiHost", "a"})
    String apiHost();

    @ConfigFlags({"nodeHost", "n"})
    String nodeHost();

    @ConfigFlags({"apiPort", "p"})
    int apiPort();

    @ConfigFlags({"nodePort", "l"})
    int nodePort();

    @ConfigFlags({"controllerPort", "c"})
    int controllerPort();
}
