package com.spoohapps.jble6lowpanshoveld.config;

import com.spoohapps.farcommon.config.ConfigFlags;

public interface ShovelDaemonConfig {
    @ConfigFlags({"profileFilePath", "f"})
    String profileFilePath();

    @ConfigFlags({"controllerPort", "c"})
    int controllerPort();
}
