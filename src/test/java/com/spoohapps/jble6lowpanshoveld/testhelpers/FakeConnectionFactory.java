package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.ConsumerConnection;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.PublisherConnection;

public class FakeConnectionFactory implements ConnectionFactory {
    @Override
    public ConsumerConnection newConsumerConnection(ConnectionSettings settings) {
        return new FakeConsumerConnection();
    }

    @Override
    public PublisherConnection newPublisherConnection(ConnectionSettings settings) {
        return new FakePublisherConnection();
    }
}
