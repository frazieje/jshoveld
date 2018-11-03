package com.spoohapps.jble6lowpanshoveld.config;

import com.spoohapps.farcommon.Config;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenLoadingSimpleConfigTests {

    private String expectedApiUrl = "http://www.spoohapps.com/";
    private int expectedApiPort = 9999;
    private int expectedSourcePort = 9998;

    private ShovelDaemonConfig config;

    @BeforeAll
    public void context() {
        String[] args = new String[] {
            "-a", expectedApiUrl,
            "-p", "" + expectedApiPort,
            "-l", "" + expectedSourcePort
        };

        config = Config.from(ShovelDaemonConfig.class).apply(args).build();
    }

    @Test
    public void shouldSetApiUrl() {
        assertEquals(expectedApiUrl, config.apiHost());
    }

    @Test
    public void shouldSetApiPort() {
        assertEquals(expectedApiPort, config.apiPort());
    }

    @Test
    public void shouldSetSourcePort() {
        assertEquals(expectedSourcePort, config.nodePort());
    }

}

