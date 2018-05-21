package com.spoohapps.jble6lowpanshoveld.tasks.handlers;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class IncomingMessageHandler implements MessageHandler {

    private ConnectionFactory sourceFactory;
    private ConnectionFactory destinationFactory;
    private ConnectionSettings sourceSettings;
    private ConnectionSettings destinationSettings;

    private ConsumerConnection consumer;
    private PublisherConnection publisher;

    private final Logger logger = LoggerFactory.getLogger(IncomingMessageHandler.class);

    public IncomingMessageHandler(ConnectionFactory sourceFactory, ConnectionSettings sourceSettings, ConnectionFactory destinationFactory, ConnectionSettings destinationSettings) {
        this.sourceFactory = sourceFactory;
        this.destinationFactory = destinationFactory;
        this.sourceSettings = sourceSettings;
        this.destinationSettings = destinationSettings;
    }

    @Override
    public void start() {
        logger.info("starting incoming message handler...");

        publisher = destinationFactory.newPublisherConnection(destinationSettings);
        publisher.onClosed(() -> logger.info("Destination connection closed"));
        publisher.open();

        consumer = sourceFactory.newConsumerConnection(sourceSettings);
        consumer.onClosed(() -> logger.info("Source connection closed"));
        consumer.onConsume(this::handleMessage);
        consumer.open();
    }

    @Override
    public void stop() {
        consumer.close();
        publisher.close();
    }

    @Override
    public int openConnections() {
        throw new NotImplementedException();
    }

    @Override
    public int totalConnections() {
        throw new NotImplementedException();
    }

    @Override
    public void handleMessage(Message message) {

        logger.info("consumed message with topic {}, hops {}", message.getTopic(), message.getHops());

        if (message.getHops() == 0) {
            Message newMessage = new Message(message.getTopic(), message.getHops()+1, message.getPayload());

            publisher.publish(newMessage);
        }
    }
}
