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
        logger.info("starting incoming message handler...");
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

        logger.info("consumed message with topic {}, hops {}", message.getTopic(), message.getHops());

        if (message.getHops() == 0) {
            Message newMessage = new Message(message.getTopic(), message.getHops()+1, message.getPayload());

            publisher.publish(newMessage);
        }
    }
}
