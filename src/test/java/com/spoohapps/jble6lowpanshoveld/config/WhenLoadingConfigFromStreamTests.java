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
    private String expectedApiUrl = "http://www.spoohapps.com/";
    private int expectedApiPort = 9999;
    private int expectedLocalPort = 9998;
    private String expectedApiExchange = "some exchange";
    private String expectedIncomingExchange = "an exchange";
    private String expectedDeviceExchange = "device exchange";
    private String expectedAppExchange = "the app exchange";
    private String expectedOutgoingExchange = "outgoing exchange";

    private ShovelDaemonConfig config;

    @BeforeAll
    public void context() {

        List<String> lines = Arrays.asList(
                "profileFilePath=" + expectedProfileFilePath,
                "apiUrl=" + expectedApiUrl,
                "apiPort=" + expectedApiPort,
                "localPort=" + expectedLocalPort,
                "apiExchange=" + expectedApiExchange,
                "incomingExchange=" + expectedIncomingExchange,
                "deviceExchange=" + expectedDeviceExchange,
                "appExchange=" + expectedAppExchange,
                "outgoingExchange=" + expectedOutgoingExchange
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

    @Test
    public void shouldSetApiExchange() {
        assertEquals(expectedApiExchange, config.apiExchange());
    }

    @Test
    public void shouldSetIncomingExchange() {
        assertEquals(expectedIncomingExchange, config.incomingExchange());
    }

    @Test
    public void shouldSetDeviceExchange() {
        assertEquals(expectedDeviceExchange, config.deviceExchange());
    }

    @Test
    public void shouldSetAppExchange() {
        assertEquals(expectedAppExchange, config.appExchange());
    }

    @Test
    public void shouldSetOutgoingExchange() {
        assertEquals(expectedOutgoingExchange, config.outgoingExchange());
    }
}
