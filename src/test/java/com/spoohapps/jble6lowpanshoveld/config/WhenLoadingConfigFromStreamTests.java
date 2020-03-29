package com.spoohapps.jble6lowpanshoveld.config;

import com.spoohapps.farcommon.Config;
import com.spoohapps.jble6lowpanshoveld.ShovelDaemon;
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
    private int expectedControllerPort = 9998;

    private ShovelDaemonConfig config;

    @BeforeAll
    public void context() {

        List<String> lines = Arrays.asList(
                "profileFilePath=" + expectedProfileFilePath,
                "controllerPort=" + expectedControllerPort
        );

        String data = lines.stream().map(s -> s + System.lineSeparator()).collect(Collectors.joining());

        InputStream configStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        config = Config.from(ShovelDaemonConfig.class)
                        .apply(configStream)
                        .build();
    }

    @Test
    public void shouldSetProfileFilePath() {
        assertEquals(expectedProfileFilePath, config.profileFilePath());
    }

    @Test
    public void shouldSetControllerPort() {
        assertEquals(expectedControllerPort, config.controllerPort());
    }
}
