package com.spoohapps.jble6lowpanshoveld.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenCascadingConfigsTests {

    private String expectedProfileFilePath = "./profile.conf";
    private String expectedApiUrl = "www.spoohapps.com/";
    private String expectedNodeHost = "localhost";

    private int expectedApiPort = 9999;
    private int expectedNodePort = 9998;

    private ShovelDaemonConfig config;

    @BeforeAll
    public void context() {

        String[] simpleArgs = new String[] {
                "-a", "",
                "-p", "" + 5672,
                "-l", "" + 5672,
                "-f", "./profile.conf"
        };

        String[] verboseArgs = new String[] {
                "-apiHost", expectedApiUrl,
                "-apiPort", "" + expectedApiPort,
                "-nodeHost", expectedNodeHost,
                "-nodePort", "" + expectedNodePort,
                "-profileFilePath", expectedProfileFilePath,
        };

        config = Config.fromArgs(simpleArgs).apply(Config.fromArgs(verboseArgs));
    }

    @Test
    public void shouldSetApiUrl() {
        assertEquals(expectedApiUrl, config.apiHost());
    }

    @Test
    public void shouldSetProfileFilePath() {
        assertEquals(expectedProfileFilePath, config.profileFilePath());
    }

    @Test
    public void shouldSetApiPort() {
        assertEquals(expectedApiPort, config.apiPort());
    }

    @Test
    public void shouldSetNodeHost() {
        assertEquals(expectedNodeHost, config.nodeHost());
    }

    @Test
    public void shouldSetNodePort() {
        assertEquals(expectedNodePort, config.nodePort());
    }
}


