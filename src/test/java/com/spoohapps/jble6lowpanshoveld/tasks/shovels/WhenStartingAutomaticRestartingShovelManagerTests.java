package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenStartingAutomaticRestartingShovelManagerTests {

    private ShovelManager manager;
    private ScheduledExecutorService executor;

    private Set<MessageShovel> shovelMocks;

    @BeforeAll
    public void context() {
        executor = Executors.newScheduledThreadPool(3);
        shovelMocks = new HashSet<>();
        shovelMocks.add(getShovelMock());
        manager = new AutomaticRestartingShovelManager(executor, shovelMocks, 500);
        IntStream.of(2,3,4,5).forEach(i -> shovelMocks.add(getShovelMock()));
        manager.setShovels(shovelMocks);
        manager.start();
    }

    @AfterAll
    public void cleanup() {
        manager.stop();
        executor.shutdownNow();
    }

    private MessageShovel getShovelMock() {
        MessageShovel shovelMock = mock(MessageShovel.class);
        when(shovelMock.getConnectionDescriptions()).thenReturn(
                Arrays.asList("Consuming from [127.0.0.1:5671]", "Publishing to [127.0.0.1:5671]"));
        return shovelMock;
    }

    @Test
    public void shouldStartAllShovels() {
        shovelMocks.forEach(shovel -> {
            verify(shovel).start();
        });
    }

    @Test
    public void shouldSubscribeToStoppedEvents() {
        shovelMocks.forEach(shovel -> {
            verify(shovel).onStopped(notNull());
        });
    }

}
