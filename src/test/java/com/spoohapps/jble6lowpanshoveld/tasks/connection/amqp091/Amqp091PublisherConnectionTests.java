package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.testhelpers.FakeIOExceptionThrowingAmqp091Channel;
import com.spoohapps.jble6lowpanshoveld.testhelpers.FakeNoopAmqp091Channel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Amqp091PublisherConnectionTests {

    private Amqp091PublisherConnection connection;
    private Amqp091ChannelSupplier mockChannelSupplier;
    private Amqp091Channel mockChannel;

    private boolean closed;

    private String expectedExchange = "some.exchange";

    @BeforeAll
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        mockChannelSupplier = mock(Amqp091ChannelSupplier.class);
        mockChannel = mock(Amqp091Channel.class);
        connection = new Amqp091PublisherConnection(mockChannelSupplier, expectedExchange);
        connection.onClosed(() -> closed = true);
        closed = false;
    }

    @Test
    public void shouldGetChannelWhenOpened() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        verify(mockChannelSupplier).getChannel();
    }

    @Test
    public void shouldDeclareExchangeWhenOpened() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        verify(mockChannel).exchangeDeclare(expectedExchange);
    }

    @Test
    public void shouldCloseWhenGetChannelTimeout() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenThrow(new TimeoutException());
        connection.open();
        assertTrue(closed);
    }

    @Test
    public void shouldCloseWhenGetChannelThrowsIOException() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenThrow(new IOException());
        connection.open();
        assertTrue(closed);
    }

    @Test
    public void shouldCloseWhenChannelThrowsIOException() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(new FakeIOExceptionThrowingAmqp091Channel());
        connection.open();
        assertTrue(closed);
    }

    @Test
    public void shouldClose() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(new FakeNoopAmqp091Channel());
        connection.open();
        connection.close();
        assertTrue(closed);
    }

    @Test
    public void shouldPublishMessageToChannel() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        Message testMessage = new Message("some.topic", 0, new byte[] { 1, 0 });
        connection.publish(testMessage);
        verify(mockChannel).publish(eq(expectedExchange), eq(testMessage.getTopic()), anyMap(), eq(testMessage.getPayload()));
    }

    @Captor
    private ArgumentCaptor<Map<String, Object>> captor;

    @Test
    public void shouldPublishMessageWithHopsAsHeader() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        Message testMessage = new Message("some.topic", 1, new byte[] { 1, 0 });
        connection.publish(testMessage);
        verify(mockChannel).publish(eq(expectedExchange), eq(testMessage.getTopic()), captor.capture(), eq(testMessage.getPayload()));
        assertEquals(captor.getValue().get("x-hops"), testMessage.getHops());
    }

    @Test
    public void shouldCloseIfPublishFails() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        doThrow(new IOException()).when(mockChannel).publish(any(), any(), any(), any());
        connection.open();
        Message testMessage = new Message("some.topic", 1, new byte[] { 1, 0 });
        connection.publish(testMessage);
        verify(mockChannel).close();
    }

    @Test
    public void shouldNotifyClosedIfChannelCloseThrowsIOException() throws IOException, TimeoutException {
        doThrow(new IOException()).when(mockChannel).close();
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        connection.close();
        assertTrue(closed);
    }

    @Test
    public void shouldNotifyClosedIfChannelCloseThrowsTimeoutException() throws IOException, TimeoutException {
        doThrow(new TimeoutException()).when(mockChannel).close();
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        connection.close();
        assertTrue(closed);
    }

    @Test
    public void shouldCloseChannel() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        connection.close();
        verify(mockChannel).close();
    }
}
