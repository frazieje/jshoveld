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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Amqp091ConsumerConnectionTests {

    private Amqp091ConsumerConnection connection;
    private Amqp091ChannelSupplier mockChannelSupplier;
    private Amqp091Channel mockChannel;

    private boolean closed;

    private String expectedExchange = "some.exchange";
    private String expectedQueue = "SomeQueue";
    private String expectedRoutingKey = "some.routing.key";

    private Message consumedMessage;

    @BeforeAll
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        mockChannelSupplier = mock(Amqp091ChannelSupplier.class);
        mockChannel = mock(Amqp091Channel.class);
        connection = new Amqp091ConsumerConnection(mockChannelSupplier, expectedExchange, expectedQueue, expectedRoutingKey);
        connection.onConsume(message -> consumedMessage = message);
        connection.onClosed(() -> closed = true);
        consumedMessage = null;
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
    public void shouldDeclareQueueWhenOpened() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        verify(mockChannel).queueDeclare(expectedQueue);
    }

    @Test
    public void shouldBindToQueueWhenOpened() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        verify(mockChannel).queueBind(expectedQueue, expectedExchange, expectedRoutingKey);
    }

    @Test
    public void shouldConsumeFromQueueWhenOpened() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        verify(mockChannel).consume(eq(expectedQueue), any(), any());
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

    @Captor
    private ArgumentCaptor<Consumer<String>> captor;

    @Captor
    private ArgumentCaptor<Consumer<Amqp091Message>> messageCaptor;

    @Test
    public void shouldCloseIfConsumerCancelled() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        verify(mockChannel).consume(eq(expectedQueue), any(), captor.capture());
        captor.getValue().accept("anything");
        assertTrue(closed);
    }

    @Test
    public void shouldConsumeMessageTopic() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        verify(mockChannel).consume(any(), messageCaptor.capture(), any());
        Amqp091Message mockMessage = mock(Amqp091Message.class);
        when(mockMessage.getRoutingKey()).thenReturn(expectedRoutingKey);
        messageCaptor.getValue().accept(mockMessage);
        assertEquals(expectedRoutingKey, consumedMessage.getTopic());
    }

    @Test
    public void shouldConsumeMessageHopsAsInteger() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        verify(mockChannel).consume(any(), messageCaptor.capture(), any());
        Amqp091Message mockMessage = mock(Amqp091Message.class);
        Map<String, Object> headers = new HashMap<>();
        int expectedHops = 1;
        headers.put("x-hops", expectedHops);
        when(mockMessage.getHeaders()).thenReturn(headers);
        messageCaptor.getValue().accept(mockMessage);
        assertEquals(expectedHops, consumedMessage.getHops());
    }


    @Test
    public void shouldConsumeMessageHopsAsString() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        verify(mockChannel).consume(any(), messageCaptor.capture(), any());
        Amqp091Message mockMessage = mock(Amqp091Message.class);
        Map<String, Object> headers = new HashMap<>();
        String expectedHops = "1";
        headers.put("x-hops", expectedHops);
        when(mockMessage.getHeaders()).thenReturn(headers);
        messageCaptor.getValue().accept(mockMessage);
        assertEquals(Integer.parseInt(expectedHops), consumedMessage.getHops());
    }

    @Test
    public void shouldConsumeMessageBody() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        connection.open();
        verify(mockChannel).consume(any(), messageCaptor.capture(), any());
        Amqp091Message mockMessage = mock(Amqp091Message.class);
        byte[] expectedBody = new byte[] { 1, 2, 3};
        when(mockMessage.getBody()).thenReturn(expectedBody);
        messageCaptor.getValue().accept(mockMessage);
        assertEquals(expectedBody, consumedMessage.getPayload());
    }

    @Test
    public void shouldCloseIfConsumeFails() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(mockChannel);
        doThrow(new IOException()).when(mockChannel).consume(any(), any(), any());
        connection.open();
        verify(mockChannel).close();
    }

    @Test
    public void shouldClose() throws IOException, TimeoutException {
        when(mockChannelSupplier.getChannel()).thenReturn(new FakeNoopAmqp091Channel());
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
