package com.spoohapps.jble6lowpanshoveld.tasks.connection;

import com.spoohapps.jble6lowpanshoveld.model.Message;

import java.util.function.Consumer;

public interface ConsumerConnection {
    void open();
    void close();
    void onClosed(Runnable closed);
    void onConsume(Consumer<Message> messageConsumer);
}
