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
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenAutomaticRestartingShovelManagerShovelsUpdatedWithPendingShovelRestartsTests {

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
        manager = new AutomaticRestartingShovelManager(executor, shovelMocks, 500);
        manager.start();
        verify(shovelMock).onStopped(onStoppedCaptor.capture());
        onStoppedCaptor.getValue().run();
        shovelMocks.clear();
        manager.setShovels(shovelMocks);
        Thread.sleep(1000);
    }

    @AfterAll
    public void cleanup() {
        manager.stop();
        executor.shutdownNow();
    }

    @Test
    public void shouldCloneMessageShovel() {
        verify(shovelMock).clone();
    }

    @Test
    public void shouldRegisterForOnStoppedEventOnClonedShovel() {
        verify(shovelCloneMock).onStopped(notNull());
    }

    @Test
    public void shouldNotActuallyStartClonedShovel() {
        verify(shovelCloneMock, times(0)).start();
    }

}
