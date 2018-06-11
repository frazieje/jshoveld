package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;

public class SimpleMessageShovel extends AbstractMessageShovel<SimpleMessageShovel> {

    public SimpleMessageShovel(ShovelContext context) {
        super(context);
    }

    @Override
    protected void handleMessage(Message message, PublisherConnection publisher) {
        publisher.publish(message);
    }
}
