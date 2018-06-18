package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;

public class HopsIncrementingMessageShovel extends AbstractMessageShovel<HopsIncrementingMessageShovel> {

    public HopsIncrementingMessageShovel(ShovelContext context, String name) {
        super(context, name);
    }

    @Override
    protected void handleMessage(Message message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}", message.getTopic(), message.getHops());

        Message newMessage = new Message(message.getTopic(), message.getHops()+1, message.hasDeviceFlag(), message.getPayload());

        publisher.publish(newMessage);
    }
}
