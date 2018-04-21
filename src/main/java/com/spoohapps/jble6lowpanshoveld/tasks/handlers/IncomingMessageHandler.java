package com.spoohapps.jble6lowpanshoveld.tasks.handlers;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncomingMessageHandler implements MessageHandler {

    private MessageConsumerConnection consumer;
    private MessagePublisherConnection publisher;

    private final Logger logger = LoggerFactory.getLogger(IncomingMessageHandler.class);

    public IncomingMessageHandler(MessageConsumerConnection consumer, MessagePublisherConnection publisher) {
        this.consumer = consumer;
        this.publisher = publisher;
    }

    @Override
    public void start() {
        publisher.open();
        consumer.open();
        consumer.consume(this::handleMessage);
    }

    @Override
    public void stop() {
        consumer.close();
        publisher.close();
    }

    @Override
    public boolean isRunning() {
        return consumer.isOpen() && publisher.isOpen();
    }

    @Override
    public void handleMessage(Message message) {

        logger.info("consumed message with topic {}", message.getTopic());

        String newTopic = message.getTopic();

        Message newMessage = new Message(newTopic, message.getHops(), message.getPayload());

        publisher.publish(newMessage);
    }
}
