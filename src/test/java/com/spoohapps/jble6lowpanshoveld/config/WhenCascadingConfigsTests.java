package com.spoohapps.jble6lowpanshoveld.config;

import com.spoohapps.farcommon.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenCascadingConfigsTests {

    private String expectedProfileFilePath = "./profile.conf";
    private int expectedControllerPort = 8081;

    private ShovelDaemonConfig config;

    @BeforeAll
    public void context() {

        String[] simpleArgs = new String[] {
                "-c", "",
                "-f", "./profile.conf"
        };

        String[] verboseArgs = new String[] {
                "-controllerPort", "" + expectedControllerPort,
                "-profileFilePath", expectedProfileFilePath,
        };

        config = Config.from(ShovelDaemonConfig.class)
                        .apply(simpleArgs)
                        .apply(verboseArgs)
                        .build();
    }

    @Test
    public void shouldSetControllerPort() {
        assertEquals(expectedControllerPort, config.controllerPort());
    }

    @Test
    public void shouldSetProfileFilePath() {
        assertEquals(expectedProfileFilePath, config.profileFilePath());
    }

}


