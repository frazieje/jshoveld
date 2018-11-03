package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.farcommon.messaging.PublisherConnection;
import com.spoohapps.jble6lowpanshoveld.model.ShovelMessage;

public class SimpleMessageShovel extends AbstractMessageShovel<SimpleMessageShovel> {

    public SimpleMessageShovel(ShovelContext context, String name) {
        super(context, name);
    }

    @Override
    protected void handleMessage(ShovelMessage message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}", message.getTopic(), message.getHops());
        ShovelMessage copy = new ShovelMessage(message.getTopic(), message.getPayload(), message.getHeaders());
        publisher.publish(copy);
    }
}
