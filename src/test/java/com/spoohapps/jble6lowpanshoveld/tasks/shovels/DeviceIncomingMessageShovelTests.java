package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.farcommon.model.Message;
import com.spoohapps.farcommon.messaging.ConnectionFactory;
import com.spoohapps.farcommon.messaging.ConnectionSettings;
import com.spoohapps.farcommon.messaging.ConsumerConnection;
import com.spoohapps.farcommon.messaging.PublisherConnection;
import com.spoohapps.jble6lowpanshoveld.model.ShovelMessage;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeviceIncomingMessageShovelTests {

    private MessageShovel shovel;

    private ConnectionFactory mockSourceFactory;
    private ConnectionSettings mockSourceSettings;
    private ConnectionFactory mockDestinationFactory;
    private ConnectionSettings mockDestinationSettings;

    private ConsumerConnection mockConsumerConnection;
    private PublisherConnection mockPublisherConnection;

    private String expectedProfileId = "1a2b3c4d";

    @Captor
    private ArgumentCaptor<Consumer<Message>> messageCaptor;

    @BeforeAll
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {

        mockConsumerConnection = mock(ConsumerConnection.class);
        mockPublisherConnection = mock(PublisherConnection.class);

        mockSourceFactory = mock(ConnectionFactory.class);
        mockSourceSettings = mock(ConnectionSettings.class);
        mockDestinationFactory = mock(ConnectionFactory.class);
        mockDestinationSettings = mock(ConnectionSettings.class);

        when(mockSourceFactory.newConsumerConnection(mockSourceSettings)).thenReturn(mockConsumerConnection);
        when(mockDestinationFactory.newPublisherConnection(mockDestinationSettings)).thenReturn(mockPublisherConnection);

        shovel = new DeviceIncomingMessageShovel(
                new ShovelContext(
                        mockSourceFactory,
                        mockSourceSettings,
                        mockDestinationFactory,
                        mockDestinationSettings),
                DeviceIncomingMessageShovel.class.getSimpleName(),
                expectedProfileId);

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
    public void shouldPublishMessageWithEqualPrependedTopic() {
        verify(mockConsumerConnection).onConsume(messageCaptor.capture());
        Message originalMessage = new ShovelMessage("topic", new byte[] { 0, 1});
        messageCaptor.getValue().accept(originalMessage);
        ArgumentCaptor<Message> publishedMessageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockPublisherConnection).publish(publishedMessageCaptor.capture());
        Message publishedMessage = publishedMessageCaptor.getValue();
        assertEquals(expectedProfileId + "." + originalMessage.getTopic(), publishedMessage.getTopic());
    }

    @Test
    public void shouldPublishMessageWithEqualPayload() {
        verify(mockConsumerConnection).onConsume(messageCaptor.capture());
        Message originalMessage = new ShovelMessage("topic", new byte[] { 0, 1});
        messageCaptor.getValue().accept(originalMessage);
        ArgumentCaptor<Message> publishedMessageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockPublisherConnection).publish(publishedMessageCaptor.capture());
        Message publishedMessage = publishedMessageCaptor.getValue();
        assertEquals(originalMessage.getPayload(), publishedMessage.getPayload());
    }

    @Test
    public void shouldPublishNewMessageWithDeviceHeader() {
        verify(mockConsumerConnection).onConsume(messageCaptor.capture());
        Message originalMessage = new ShovelMessage("topic", new byte[] { 0, 1});
        messageCaptor.getValue().accept(originalMessage);
        ArgumentCaptor<Message> publishedMessageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockPublisherConnection).publish(publishedMessageCaptor.capture());
        Message publishedMessage = publishedMessageCaptor.getValue();
        assertTrue(ShovelMessage.from(publishedMessage).hasDeviceFlag());
    }

    @Test
    public void shouldNotPublishMessageWithDeviceHeader() {
        verify(mockConsumerConnection).onConsume(messageCaptor.capture());
        ShovelMessage originalMessage = new ShovelMessage("topic", new byte[] { 0, 1});
        originalMessage.setDeviceFlag(true);
        messageCaptor.getValue().accept(originalMessage);
        verify(mockPublisherConnection, times(0)).publish(any());
    }

    @Test
    public void shouldNotPublishMessageWithProfileTopic() {
        verify(mockConsumerConnection).onConsume(messageCaptor.capture());
        ShovelMessage originalMessage = new ShovelMessage(expectedProfileId + ".topic", new byte[] { 0, 1});
        messageCaptor.getValue().accept(originalMessage);
        verify(mockPublisherConnection, times(0)).publish(any());
    }
}
