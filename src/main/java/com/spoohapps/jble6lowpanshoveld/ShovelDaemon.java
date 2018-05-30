package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.jble6lowpanshoveld.config.Config;
import com.spoohapps.jble6lowpanshoveld.config.ShovelDaemonConfig;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.*;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091ConnectionSupplier;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091ConsumerConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091PublisherConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq.RabbitMqAmqp091ConnectionSupplier;
import com.spoohapps.jble6lowpanshoveld.tasks.shovels.AbstractMessageShovel;
import com.spoohapps.jble6lowpanshoveld.tasks.shovels.MessageShovel;
import com.spoohapps.jble6lowpanshoveld.tasks.profile.FileBasedProfileManager;
import com.spoohapps.jble6lowpanshoveld.tasks.profile.ProfileManager;
import com.spoohapps.jble6lowpanshoveld.tasks.shovels.RemoteMessageRetrievalShovel;
import org.apache.commons.daemon.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ShovelDaemon implements Daemon {

    private ExecutorService executorService;
    private ShovelDaemonConfig shovelDaemonConfig;

    private ProfileManager profileManager;

    private ConnectionFactory nodeConnectionFactory;

    private ConnectionFactory apiConnectionFactory;

    private List<MessageShovel> messageShovels = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(ShovelDaemon.class);

    public ShovelDaemon() {}

    public ShovelDaemon(ShovelDaemonConfig config, ConnectionFactory nodeConnectionFactory, ConnectionFactory apiConnectionFactory, ProfileManager profileManager) {

        shovelDaemonConfig = config;

        this.nodeConnectionFactory = nodeConnectionFactory;

        this.apiConnectionFactory = apiConnectionFactory;

        this.profileManager = profileManager;

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

        profileManager = new FileBasedProfileManager(Paths.get(shovelDaemonConfig.profileFilePath()));

        Amqp091ConnectionSupplier nodeRabbitMqConnectionSupplier = new RabbitMqAmqp091ConnectionSupplier(
                executorService,
                shovelDaemonConfig.nodeHost(),
                shovelDaemonConfig.nodePort(),
                "jble6lowpanshoveld",
                "jble6lowpanshoveld");

        nodeConnectionFactory = new Amqp091ConnectionFactory(nodeRabbitMqConnectionSupplier);

        Amqp091ConnectionSupplier apiRabbitMqConnectionSupplier = new RabbitMqAmqp091ConnectionSupplier(
                executorService,
                shovelDaemonConfig.apiHost(),
                shovelDaemonConfig.apiPort(),
                "jble6lowpanshoveld",
                "jble6lowpanshoveld");

        apiConnectionFactory = new Amqp091ConnectionFactory(apiRabbitMqConnectionSupplier);
    }

    @Override
    public void start() throws Exception {

        logger.info("Starting...");

        profileManager.start();

        addRemoteRetrievalMessageHandler();

        messageShovels.forEach(MessageShovel::start);
    }

    @Override
    public void stop() throws Exception {

        logger.info("Stopping...");

        messageShovels.forEach(MessageShovel::stop);

        profileManager.stop();

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

    private void addRemoteRetrievalMessageHandler() {

        ConnectionSettings sourceSettings = new Amqp091ConsumerConnectionSettings(
                    "far.incoming",
                    AbstractMessageShovel.class.getSimpleName(),
                    profileManager.get() + ".#");

        ConnectionSettings destinationSettings = new Amqp091PublisherConnectionSettings(
                    "far.app");

        messageShovels.add(new RemoteMessageRetrievalShovel(apiConnectionFactory, sourceSettings, nodeConnectionFactory, destinationSettings));
    }
}
