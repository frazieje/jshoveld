package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenSettingShovelsOnRunningAutomaticRestartingShovelManagerTests {

    private ShovelManager manager;
    private ScheduledExecutorService executor;

    private MessageShovel shovelMock;

    private MessageShovel shovelCloneMock;

    @Captor
    private ArgumentCaptor<Runnable> onStoppedCaptor;

    @BeforeAll
    public void context() throws InterruptedException {
        MockitoAnnotations.initMocks(this);
        executor = Executors.newScheduledThreadPool(3);
        shovelMock = mock(MessageShovel.class);
        shovelCloneMock = mock(MessageShovel.class);
        when(shovelMock.getConnectionDescriptions()).thenReturn(
                Arrays.asList("Consuming from [127.0.0.1:5671]", "Publishing to [127.0.0.1:5671]"));
        when(shovelMock.clone()).thenReturn(shovelCloneMock);
        Set<MessageShovel> shovelMocks = new HashSet<>();
        shovelMocks.add(shovelMock);
        manager = new AutomaticRestartingShovelManager(executor, 500);
        manager.start();
        manager.setShovels(shovelMocks);
        Thread.sleep(1000);
    }

    @AfterAll
    public void cleanup() {
        manager.stop();
        executor.shutdownNow();
    }

    @Test
    public void shouldRegisterForOnStoppedEventOnShovels() {
        verify(shovelMock).onStopped(notNull());
    }

    @Test
    public void shouldStartShovels() {
        verify(shovelMock).start();
    }

}
