package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.jble6lowpanshoveld.config.Config;
import com.spoohapps.jble6lowpanshoveld.config.ShovelDaemonConfig;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.*;
import com.spoohapps.jble6lowpanshoveld.tasks.handlers.IncomingMessageHandler;
import com.spoohapps.jble6lowpanshoveld.tasks.handlers.MessageHandler;
import org.apache.commons.daemon.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ShovelDaemon implements Daemon {

    private ExecutorService executorService;
    private ShovelDaemonConfig shovelDaemonConfig;

    private MessageConnectionFactory sourceConnectionFactory;

    private MessageConnectionFactory apiConnectionFactory;

    private List<MessageHandler> messageHandlers = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(ShovelDaemon.class);

    public ShovelDaemon() {}

    public ShovelDaemon(ShovelDaemonConfig config, MessageConnectionFactory sourceConnectionFactory, MessageConnectionFactory apiConnectionFactory) {

        shovelDaemonConfig = config;

        this.sourceConnectionFactory = sourceConnectionFactory;

        this.apiConnectionFactory = apiConnectionFactory;

        executorService = Executors.newFixedThreadPool(10);
    }

    public ShovelDaemon(String[] args) {

        shovelDaemonConfig = Config.fromDefaults().apply(Config.fromArgs(args));

        initialize();
    }

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {

        shovelDaemonConfig = Config.fromDefaults().apply(Config.fromArgs(context.getArguments()));

        initialize();
    }

    private void initialize() {

        executorService = Executors.newFixedThreadPool(10);

        logger.info("source host: {}", shovelDaemonConfig.nodeHost());
        logger.info("source port: {}", shovelDaemonConfig.nodePort());

        sourceConnectionFactory = new Amqp091MessageConnectionFactory(
                executorService,
                shovelDaemonConfig.nodeHost(),
                shovelDaemonConfig.nodePort(),
                "jble6lowpanshoveld",
                "jble6lowpanshoveld");
    }

    @Override
    public void start() throws Exception {

        logger.info("Starting...");

        addIncomingMessageHandler();

        messageHandlers.forEach(MessageHandler::start);
    }

    @Override
    public void stop() throws Exception {

        logger.info("Stopping...");

        messageHandlers.forEach(MessageHandler::stop);

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.info("Force stopping...");
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS))
                    logger.error("Did not terminate, kill process manually.");
            } else {
                logger.info("Exiting normally...");
            }
        } catch (InterruptedException ie) {
            logger.error(ie.getMessage());
        }
    }

    @Override
    public void destroy() {

    }

    public static void main(String[] args) {
        for (String s : args) {
            System.out.println(s);
        }
        ShovelDaemon daemon = new ShovelDaemon(args);
        try {
            daemon.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addIncomingMessageHandler() {

        MessageConnectionSettings consumerSettings = new Amqp091MessageConsumerConnectionSettings(
                    "far.app",
                    IncomingMessageHandler.class.getSimpleName(),
                    "#");

        MessageConnectionSettings publisherSettings = new Amqp091MessagePublisherConnectionSettings(
                    "far.incoming");

        messageHandlers.add(new IncomingMessageHandler(
                sourceConnectionFactory.newConsumerConnection(consumerSettings),
                sourceConnectionFactory.newPublisherConnection(publisherSettings)));
    }
}
