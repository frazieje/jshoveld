package com.spoohapps.jble6lowpanshoveld.tasks.connection;

import com.spoohapps.jble6lowpanshoveld.model.Message;

import java.util.function.Consumer;

public interface MessageConsumerConnection {
    boolean isOpen();
    void open();
    void close();
    void consume(Consumer<Message> messageConsumer);
}
