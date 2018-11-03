package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.farcommon.model.Message;
import com.spoohapps.farcommon.messaging.PublisherConnection;
import com.spoohapps.jble6lowpanshoveld.model.ShovelMessage;

public class ZeroHopsMessageShovel extends AbstractMessageShovel<ZeroHopsMessageShovel> {

    public ZeroHopsMessageShovel(ShovelContext context, String name) {
        super(context, name);
    }

    @Override
    protected void handleMessage(ShovelMessage message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}", message.getTopic(), message.getHops());

        if (message.getHops() == 0) {
            Message newMessage = new ShovelMessage(message.getTopic(), message.getPayload(), message.getHeaders());

            publisher.publish(newMessage);
        }
    }
}
