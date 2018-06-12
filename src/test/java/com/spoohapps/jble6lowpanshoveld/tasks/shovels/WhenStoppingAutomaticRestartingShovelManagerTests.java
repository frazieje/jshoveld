package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenStoppingAutomaticRestartingShovelManagerTests {

    private ShovelManager manager;
    private ScheduledExecutorService executor;

    private Set<MessageShovel> shovelMocks;

    @BeforeAll
    public void context() throws InterruptedException {
        executor = Executors.newScheduledThreadPool(3);
        shovelMocks = new HashSet<>();
        manager = new AutomaticRestartingShovelManager(executor, shovelMocks, 500);
        IntStream.of(1,2,3,4).forEach(i -> shovelMocks.add(getShovelMock()));
        manager.setShovels(shovelMocks);
        manager.start();
        manager.stop();
        Thread.sleep(2000);
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
    public void shouldStopAllShovels() {
        shovelMocks.forEach(shovel -> {
            verify(shovel).stop();
        });
    }

    @Test
    public void shouldNotCloneStoppedShovels() {
        shovelMocks.forEach(shovel -> verify(shovel, times(0)).clone());
    }
}
