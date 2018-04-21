package com.spoohapps.jble6lowpanshoveld.tasks.connection;

import com.spoohapps.jble6lowpanshoveld.model.Message;

public interface MessagePublisherConnection {
    boolean isOpen();
    void open();
    void close();
    void publish(Message message);
}
