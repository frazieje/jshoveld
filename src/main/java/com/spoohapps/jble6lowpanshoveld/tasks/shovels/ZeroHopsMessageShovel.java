package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;

public class ZeroHopsMessageShovel extends AbstractMessageShovel<ZeroHopsMessageShovel> {

    public ZeroHopsMessageShovel(ShovelContext context, String name) {
        super(context, name);
    }

    @Override
    protected void handleMessage(Message message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}", message.getTopic(), message.getHops());

        if (message.getHops() == 0) {
            Message newMessage = new Message(message.getTopic(), message.getHops(), message.hasDeviceFlag(), message.getPayload());

            publisher.publish(newMessage);
        }
    }
}
