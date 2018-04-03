package com.spoohapps.jble6lowpanshoveld.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenCascadingConfigsTests {

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

        String[] simpleArgs = new String[] {
                "-a", "",
                "-p", "" + 5672,
                "-l", "" + 5672,
                "-f", "./profile.conf",
                "-x", "amq.app",
                "-i", "amq.incoming",
                "-d", "amq.device",
                "-b", "amq.app",
                "-o", "amq.outgoing"
        };

        String[] verboseArgs = new String[] {
                "-apiUrl", expectedApiUrl,
                "-apiPort", "" + expectedApiPort,
                "-localPort", "" + expectedLocalPort,
                "-profileFilePath", expectedProfileFilePath,
                "-apiExchange", expectedApiExchange,
                "-incomingExchange", expectedIncomingExchange,
                "-deviceExchange", expectedDeviceExchange,
                "-appExchange", expectedAppExchange,
                "-outgoingExchange", expectedOutgoingExchange
        };

        config = Config.fromArgs(simpleArgs).apply(Config.fromArgs(verboseArgs));
    }

    @Test
    public void shouldSetApiUrl() {
        assertEquals(expectedApiUrl, config.apiUrl());
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


