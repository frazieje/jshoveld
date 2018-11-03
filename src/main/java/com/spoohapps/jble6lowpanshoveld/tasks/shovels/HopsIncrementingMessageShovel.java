package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.farcommon.messaging.PublisherConnection;
import com.spoohapps.jble6lowpanshoveld.model.ShovelMessage;

public class HopsIncrementingMessageShovel extends AbstractMessageShovel<HopsIncrementingMessageShovel> {

    public HopsIncrementingMessageShovel(ShovelContext context, String name) {
        super(context, name);
    }

    @Override
    protected void handleMessage(ShovelMessage message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}", message.getTopic(), message.getHops());
        ShovelMessage newMessage = new ShovelMessage(message.getTopic(), message.getPayload(), message.getHeaders());
        newMessage.setHops(message.getHops()+1);
        publisher.publish(newMessage);
    }
}
