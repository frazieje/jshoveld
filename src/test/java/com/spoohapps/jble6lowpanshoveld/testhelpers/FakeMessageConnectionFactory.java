package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.MessageConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.MessageConsumerConnection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.MessagePublisherConnection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FakeMessageConnectionFactory implements MessageConnectionFactory{
    @Override
    public MessageConsumerConnection newConsumerConnection(String exchange, String queue, String routingKey) throws IOException, TimeoutException {
        return new FakeMessageConsumerConnection();
    }

    @Override
    public MessagePublisherConnection newPublisherConnection(String exchange) throws IOException, TimeoutException {
        return new FakeMessagePublisherConnection();
    }
}
