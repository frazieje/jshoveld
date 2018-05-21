package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConsumerConnection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RemoteMessageRetrievalShovelTests {

    private RemoteMessageRetrievalShovel shovel;

    private ConsumerConnection mockConsumerConnection;
    private PublisherConnection mockPublisherConnection;

    private ConnectionFactory mockSourceFactory;
    private ConnectionSettings sourceSettings;
    private ConnectionFactory mockDestinationFactory;
    private ConnectionSettings destinationSettings;

    @Captor
    private ArgumentCaptor<Consumer<Message>> messageCaptor;

    @BeforeAll
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        mockSourceFactory = mock(ConnectionFactory.class);
        sourceSettings = mock(ConnectionSettings.class);
        mockDestinationFactory = mock(ConnectionFactory.class);
        destinationSettings = mock(ConnectionSettings.class);
        mockConsumerConnection = mock(ConsumerConnection.class);
        mockPublisherConnection = mock(PublisherConnection.class);

        when(mockSourceFactory.newConsumerConnection(any())).thenReturn(mockConsumerConnection);
        when(mockDestinationFactory.newPublisherConnection(any())).thenReturn(mockPublisherConnection);

        shovel = new RemoteMessageRetrievalShovel(mockSourceFactory, sourceSettings, mockDestinationFactory, destinationSettings);
        shovel.start();
    }

    @AfterEach
    public void cleanup() {
        shovel.stop();
        shovel = null;
    }


    @Test
    public void shouldOpenPublisherConnectionWhenStarted() {
        verify(mockPublisherConnection).open();
    }

    @Test
    public void shouldOpenConsumerConnectionWhenStarted() {
        verify(mockConsumerConnection).open();
    }

    @Test
    public void shouldClosePublisherConnectionWhenStopped() {
        shovel.stop();
        verify(mockPublisherConnection).close();
    }

    @Test
    public void shouldCloseConsumerConnectionWhenStopped() {
        shovel.stop();
        verify(mockConsumerConnection).close();
    }

    @Test
    public void shouldPublishMessageWithEqualTopic() {
        verify(mockConsumerConnection).onConsume(messageCaptor.capture());
        Message originalMessage = new Message("topic", 0, new byte[] { 0, 1});
        messageCaptor.getValue().accept(originalMessage);
        ArgumentCaptor<Message> publishedMessageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockPublisherConnection).publish(publishedMessageCaptor.capture());
        Message publishedMessage = publishedMessageCaptor.getValue();
        assertEquals(originalMessage.getTopic(), publishedMessage.getTopic());
    }

    @Test
    public void shouldPublishMessageWithIncrementedHops() {
        verify(mockConsumerConnection).onConsume(messageCaptor.capture());
        Message originalMessage = new Message("topic", 0, new byte[] { 0, 1});
        messageCaptor.getValue().accept(originalMessage);
        ArgumentCaptor<Message> publishedMessageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockPublisherConnection).publish(publishedMessageCaptor.capture());
        Message publishedMessage = publishedMessageCaptor.getValue();
        assertEquals(originalMessage.getHops()+1, publishedMessage.getHops());
    }

    @Test
    public void shouldPublishMessageWithEqualPayload() {
        verify(mockConsumerConnection).onConsume(messageCaptor.capture());
        Message originalMessage = new Message("topic", 0, new byte[] { 0, 1});
        messageCaptor.getValue().accept(originalMessage);
        ArgumentCaptor<Message> publishedMessageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockPublisherConnection).publish(publishedMessageCaptor.capture());
        Message publishedMessage = publishedMessageCaptor.getValue();
        assertEquals(originalMessage.getPayload(), publishedMessage.getPayload());
    }

    @Test
    public void shouldNotPublishMessageWithHopsGreaterThanZero() {
        verify(mockConsumerConnection).onConsume(messageCaptor.capture());
        Message originalMessage = new Message("topic", 1, new byte[] { 0, 1});
        messageCaptor.getValue().accept(originalMessage);
        verify(mockPublisherConnection, times(0)).publish(any());
    }

}
