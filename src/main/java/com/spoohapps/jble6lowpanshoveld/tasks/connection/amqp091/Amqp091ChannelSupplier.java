package com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface Amqp091ChannelSupplier {
    Amqp091Channel getChannel() throws IOException, TimeoutException;
}
