package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractMessageShovel<T extends AbstractMessageShovel> implements MessageShovel {

    private ConnectionFactory sourceFactory;
    private ConnectionFactory destinationFactory;
    private ConnectionSettings sourceSettings;
    private ConnectionSettings destinationSettings;

    private ConsumerConnection consumer;
    protected PublisherConnection publisher;

    private Class<T> subclass;

    protected final Logger logger;

    protected AbstractMessageShovel(ConnectionFactory sourceFactory, ConnectionSettings sourceSettings, ConnectionFactory destinationFactory, ConnectionSettings destinationSettings) {
        this.sourceFactory = sourceFactory;
        this.destinationFactory = destinationFactory;
        this.sourceSettings = sourceSettings;
        this.destinationSettings = destinationSettings;
        this.subclass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];

        logger = LoggerFactory.getLogger(subclass);
    }

    @Override
    public void start() {
        logger.info("starting...");

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
        logger.info("stopping...");
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

    protected abstract void handleMessage(Message message);
}
