package com.spoohapps.jble6lowpanshoveld.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenLoadingSimpleConfigTests {

    private String expectedApiUrl = "http://www.spoohapps.com/";
    private int expectedApiPort = 9999;
    private int expectedLocalPort = 9998;

    private ShovelDaemonConfig config;

    @BeforeAll
    public void context() {
        String[] args = new String[] {
            "-a", expectedApiUrl,
            "-p", "" + expectedApiPort,
            "-l", "" + expectedLocalPort
        };

        config = Config.fromArgs(args);
    }

    @Test
    public void shouldSetApiUrl() {
        assertEquals(expectedApiUrl, config.apiUrl());
    }

    @Test
    public void shouldSetApiPort() {
        assertEquals(expectedApiPort, config.apiPort());
    }

    @Test
    public void shouldSetLocalPort() {
        assertEquals(expectedLocalPort, config.localPort());
    }

}

