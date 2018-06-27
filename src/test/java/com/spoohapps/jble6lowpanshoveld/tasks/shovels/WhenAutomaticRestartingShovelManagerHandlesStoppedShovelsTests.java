package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.farcommon.testhelpers.FakeConnectionFactory;
import com.spoohapps.farcommon.testhelpers.TestConnectionSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenAutomaticRestartingShovelManagerHandlesStoppedShovelsTests {

    private ShovelManager manager;
    private ScheduledExecutorService executor;

    private MessageShovel restartingShovelMock;

    private MessageShovel shovelCloneMock;

    private MessageShovel notRestartingShovelMock;

    @Captor
    private ArgumentCaptor<Runnable> restartingOnStoppedCaptor;

    @Captor
    private ArgumentCaptor<Runnable> notRestartingOnStoppedCaptor;

    @BeforeAll
    public void context() throws InterruptedException {
        MockitoAnnotations.initMocks(this);

        executor = Executors.newScheduledThreadPool(3);

        Map<String, String> restartingShovelSettings = new HashMap<>();
        restartingShovelSettings.put("prop", "value");

        Map<String, String> notRestartingShovelSettings = new HashMap<>();
        notRestartingShovelSettings.put("prop1", "value1");

        restartingShovelMock = spy(new SimpleMessageShovel(
                new ShovelContext(
                        new FakeConnectionFactory(),
                        new TestConnectionSettings(restartingShovelSettings),
                        new FakeConnectionFactory(),
                        new TestConnectionSettings(restartingShovelSettings)
                ),
                SimpleMessageShovel.class.getSimpleName()));
        notRestartingShovelMock = spy(new SimpleMessageShovel(
                new ShovelContext(
                        new FakeConnectionFactory(),
                        new TestConnectionSettings(notRestartingShovelSettings),
                        new FakeConnectionFactory(),
                        new TestConnectionSettings(notRestartingShovelSettings)
                ),
                SimpleMessageShovel.class.getSimpleName()));

        shovelCloneMock = spy(new SimpleMessageShovel(
                new ShovelContext(
                        new FakeConnectionFactory(),
                        new TestConnectionSettings(restartingShovelSettings),
                        new FakeConnectionFactory(),
                        new TestConnectionSettings(restartingShovelSettings)
                ),
                SimpleMessageShovel.class.getSimpleName()));

        when(restartingShovelMock.getConnectionDescriptions()).thenReturn(
                Arrays.asList("Consuming from [127.0.0.1:5671]", "Publishing to [127.0.0.1:5671]"));

        when(notRestartingShovelMock.getConnectionDescriptions()).thenReturn(
                Arrays.asList("Consuming from [127.0.0.2:5671]", "Publishing to [127.0.0.2:5671]"));

        when(restartingShovelMock.clone()).thenReturn(shovelCloneMock);

        Set<MessageShovel> shovelMocks = new HashSet<>();
        shovelMocks.add(restartingShovelMock);
        shovelMocks.add(notRestartingShovelMock);

        manager = new AutomaticRestartingShovelManager(executor, shovelMocks, 500);
        manager.start();

        verify(restartingShovelMock).onStopped(restartingOnStoppedCaptor.capture());
        verify(notRestartingShovelMock).onStopped(notRestartingOnStoppedCaptor.capture());

        restartingOnStoppedCaptor.getValue().run();

        shovelMocks.remove(notRestartingShovelMock);

        manager.setShovels(shovelMocks);

        notRestartingOnStoppedCaptor.getValue().run();

        Thread.sleep(1000);
    }

    @AfterAll
    public void cleanup() {
        manager.stop();
        executor.shutdownNow();
    }

    @Test
    public void shouldCloneMessageShovel() {
        verify(restartingShovelMock).clone();
    }

    @Test
    public void shouldNotCloneRemovedMessageShovel() {
        verify(notRestartingShovelMock, times(0)).clone();
    }

}
