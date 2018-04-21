package com.spoohapps.jble6lowpanshoveld.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenLoadingConfigFromStreamTests {

    private String expectedProfileFilePath = "./profile.conf";
    private String expectedApiUrl = "www.spoohapps.com";
    private String expectedNodeUrl = "localhost";
    private int expectedApiPort = 9999;
    private int expectedNodePort = 9998;

    private ShovelDaemonConfig config;

    @BeforeAll
    public void context() {

        List<String> lines = Arrays.asList(
                "profileFilePath=" + expectedProfileFilePath,
                "apiHost=" + expectedApiUrl,
                "apiPort=" + expectedApiPort,
                "nodeHost=" + expectedNodeUrl,
                "nodePort=" + expectedNodePort
        );

        String data = lines.stream().map(s -> s + System.lineSeparator()).collect(Collectors.joining());

        InputStream configStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        config = Config.fromStream(configStream);
    }

    @Test
    public void shouldSetProfileFilePath() {
        assertEquals(expectedProfileFilePath, config.profileFilePath());
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
        assertEquals(expectedNodePort, config.nodePort());
    }
}
