package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Message;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConsumerConnection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;

public class RemoteMessageRetrievalShovel extends AbstractMessageShovel<RemoteMessageRetrievalShovel> {

    public RemoteMessageRetrievalShovel(ConsumerConnection consumer, PublisherConnection publisher) {
        super(consumer, publisher);
    }

    @Override
    protected void handleMessage(Message message, PublisherConnection publisher) {
        logger.info("consumed message with topic {}, hops {}", message.getTopic(), message.getHops());

        if (message.getHops() == 0) {
            Message newMessage = new Message(message.getTopic(), message.getHops()+1, message.getPayload());

            publisher.publish(newMessage);
        }
    }
}
