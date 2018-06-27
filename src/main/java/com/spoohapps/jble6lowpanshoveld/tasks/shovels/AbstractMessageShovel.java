package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.farcommon.model.Message;
import com.spoohapps.farcommon.connection.*;
import com.spoohapps.jble6lowpanshoveld.model.ShovelMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractMessageShovel<T extends AbstractMessageShovel> implements MessageShovel {

    ShovelContext context;

    private ConsumerConnection consumer;
    private PublisherConnection publisher;

    private Runnable onStopped;

    private Class<T> subclass;

    final String name;

    private final String type;

    final Logger logger;

    private AtomicBoolean consumerClosed = new AtomicBoolean(false);
    private AtomicBoolean publisherClosed = new AtomicBoolean(false);

    private AtomicBoolean stopped = new AtomicBoolean(false);

    @SuppressWarnings("unchecked")
    AbstractMessageShovel(ShovelContext context, String name) {

        this.subclass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];

        logger = LoggerFactory.getLogger(subclass);

        this.context = context;

        this.name = name;

        type = subclass.getSimpleName();

        consumer = context.getSourceFactory().newConsumerConnection(context.getSourceSettings());
        publisher = context.getDestinationFactory().newPublisherConnection(context.getDestinationSettings());

        publisher.onClosed(this::publisherClosed);

        consumer.onClosed(this::consumerClosed);

        consumer.onConsume(message -> handleMessage(ShovelMessage.from(message), publisher));
    }

    @Override
    public void start() {
        logger.info("starting...");
        if (!stopped.get())
            publisher.open();
        if (!stopped.get() && !publisherClosed.get())
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
        this.onStopped = stopped;
    }

    private synchronized void notifyStopped() {
        if (!stopped.get() && onStopped != null) {
            stopped.set(true);
            onStopped.run();
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public MessageShovel clone() {
        try {
            Constructor<? extends AbstractMessageShovel> constructor =
                    subclass.getConstructor(ShovelContext.class, String.class);
            return constructor.newInstance(context, name);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException("Could not clone shovel", e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, subclass);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!subclass.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final AbstractMessageShovel other = (AbstractMessageShovel) obj;

        if (context == null ? other.context != null : !context.equals(other.context))
            return false;

        return true;
    }

    protected abstract void handleMessage(ShovelMessage message, PublisherConnection publisher);
}
