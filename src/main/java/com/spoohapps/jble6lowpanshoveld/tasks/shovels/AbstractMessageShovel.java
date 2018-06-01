package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractMessageShovel<T extends AbstractMessageShovel> implements MessageShovel {

    private ConnectionFactory sourceFactory;
    private ConnectionFactory destinationFactory;
    private ConnectionSettings sourceSettings;
    private ConnectionSettings destinationSettings;

    private ConsumerConnection consumer;
    private PublisherConnection publisher;

    private Runnable stopped;

    private Class<T> subclass;

    final Logger logger;

    private AtomicBoolean consumerClosed = new AtomicBoolean(false);
    private AtomicBoolean publisherClosed = new AtomicBoolean(false);

    AbstractMessageShovel(ConnectionFactory sourceFactory, ConnectionSettings sourceSettings, ConnectionFactory destinationFactory, ConnectionSettings destinationSettings) {
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
        publisher.onClosed(this::publisherClosed);
        publisher.open();

        consumer = sourceFactory.newConsumerConnection(sourceSettings);
        consumer.onClosed(this::consumerClosed);
        consumer.onConsume(message -> handleMessage(message, publisher));
        consumer.open();
    }

    @Override
    public void stop() {
        logger.info("stopping...");
        consumer.close();
        publisher.close();
    }

    @Override
    public void onStopped(Runnable stopped) {
        this.stopped = stopped;
    }

    private void notifyStopped() {
        if (stopped != null) {
            stopped.run();
        }
    }

    private void publisherClosed() {
        logger.info("Destination connection closed");
        publisherClosed.set(true);
        if (consumerClosed.get())
            notifyStopped();
        else
            consumer.close();
    }

    private void consumerClosed() {
        logger.info("Source connection closed");
        consumerClosed.set(true);
        if (publisherClosed.get())
            notifyStopped();
        else
            publisher.close();
    }

    @Override
    public List<String> getConnectionDescriptions() {
        List<String> descriptions = new ArrayList<>();
        descriptions.add(consumer.getDescription());
        descriptions.add(publisher.getDescription());
        return descriptions;
    }

    protected abstract void handleMessage(Message message, PublisherConnection publisher);
}
