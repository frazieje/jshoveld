package com.spoohapps.jble6lowpanshoveld.tasks.connection;

public interface ConnectionFactory {
    PublisherConnection newPublisherConnection(ConnectionSettings settings);
    ConsumerConnection newConsumerConnection(ConnectionSettings settings);
}
