package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.jble6lowpanshoveld.config.Config;
import com.spoohapps.jble6lowpanshoveld.config.ShovelDaemonConfig;
import com.spoohapps.jble6lowpanshoveld.controller.RemoteShovelDaemonControllerBroadcaster;
import com.spoohapps.jble6lowpanshoveld.controller.ShovelDaemonControllerBroadcaster;
import com.spoohapps.jble6lowpanshoveld.model.Profile;
import com.spoohapps.jble6lowpanshoveld.model.TLSContext;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.*;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091ConnectionFactory;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091ConsumerConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.Amqp091PublisherConnectionSettings;
import com.spoohapps.jble6lowpanshoveld.tasks.connection.amqp091.rabbitmq.RabbitMqAmqp091ConnectionSupplier;
import com.spoohapps.jble6lowpanshoveld.tasks.shovels.*;
import com.spoohapps.jble6lowpanshoveld.tasks.profile.FileBasedProfileManager;
import com.spoohapps.jble6lowpanshoveld.tasks.profile.ProfileManager;
import org.apache.commons.daemon.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ShovelDaemon implements Daemon, ShovelDaemonController {

    private ScheduledExecutorService executorService;
    private ShovelDaemonConfig shovelDaemonConfig;

    private ProfileManager profileManager;

    private ShovelManager shovelManager;

    private ShovelDaemonControllerBroadcaster controllerBroadcaster;

    private final Logger logger = LoggerFactory.getLogger(ShovelDaemon.class);

    public ShovelDaemon() {}

    public ShovelDaemon(ShovelDaemonConfig config, ProfileManager profileManager, ShovelManager shovelManager, ShovelDaemonControllerBroadcaster controllerBroadcaster) {

        shovelDaemonConfig = config;

        this.profileManager = profileManager;

        this.shovelManager = shovelManager;

        executorService = Executors.newScheduledThreadPool(10);

        this.profileManager.onChanged(this::setProfileInternal);

        this.controllerBroadcaster = controllerBroadcaster;
    }

    public ShovelDaemon(String[] args) {

        shovelDaemonConfig = Config.fromDefaults().apply(Config.fromArgs(args));

        initialize();
    }

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {
        String[] daemonArgs = context.getArguments();

        String configFilePath = null;
        try {
            for (int i = 0; i < daemonArgs.length; i++) {
                if (daemonArgs[i].equals("-configFile")) {
                    configFilePath = daemonArgs[i + 1];
                }
            }
        } catch (Exception ignored) {}

        shovelDaemonConfig = Config.fromDefaults();

        try {
            if (configFilePath != null) {
                shovelDaemonConfig = shovelDaemonConfig.apply(Config.fromStream(
                        Files.newInputStream(Paths.get(configFilePath))));
            }
        } catch (Exception ignored) {}

        shovelDaemonConfig = shovelDaemonConfig.apply(Config.fromArgs(context.getArguments()));

        initialize();
    }

    private void initialize() {

        executorService = Executors.newScheduledThreadPool(10);

        logger.info("source host: {}", shovelDaemonConfig.nodeHost());
        logger.info("source port: {}", shovelDaemonConfig.nodePort());

        logger.info("api host: {}", shovelDaemonConfig.apiHost());
        logger.info("api port: {}", shovelDaemonConfig.apiPort());

        shovelManager = new AutomaticRestartingShovelManager(executorService, 5000);

        profileManager = new FileBasedProfileManager(Paths.get(shovelDaemonConfig.profileFilePath()));

        profileManager.onChanged(this::setProfileInternal);

        controllerBroadcaster = new RemoteShovelDaemonControllerBroadcaster(this, shovelDaemonConfig.controllerPort());
    }

    @Override
    public void start() throws Exception {

        logger.info("Starting...");

        shovelManager.start();

        profileManager.start();

        controllerBroadcaster.start();
    }

    @Override
    public void stop() throws Exception {

        logger.info("Stopping...");

        controllerBroadcaster.stop();

        profileManager.stop();

        shovelManager.stop();

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

    @Override
    public List<String> shovelDescriptors() {
        return shovelManager.shovelDescriptors();
    }

    @Override
    public void restartShovels() {
        shovelManager.stop();
        shovelManager.start();
    }

    @Override
    public Profile getProfile() {
        return profileManager.get();
    }

    @Override
    public void setProfile(Profile profile) {
        profileManager.set(profile);
    }

    private void setProfileInternal(Profile newProfile) {
        logger.info("Setting new profile...");
        Set<MessageShovel> shovels = new HashSet<>();

        TLSContext nodeContext = newProfile.getNodeContext();

        if (nodeContext != null && nodeContext.hasValue()) {

            ConnectionFactory nodeFactory = createAmqp091ConnectionFactory(
                    shovelDaemonConfig.nodeHost(),
                    shovelDaemonConfig.nodePort(),
                    nodeContext);

            shovels.add(getDeviceIncomingShovel(nodeFactory, nodeFactory, newProfile.getId()));

            shovels.add(getDeviceOutgoingShovel(nodeFactory, nodeFactory, newProfile.getId()));

            shovels.add(getAppIncomingShovel(nodeFactory, nodeFactory));

            shovels.add(getAppOutgoingShovel(nodeFactory, nodeFactory));

            TLSContext apiContext = newProfile.getApiContext();

            if (apiContext != null && apiContext.hasValue()) {

                ConnectionFactory apiFactory = createAmqp091ConnectionFactory(
                        shovelDaemonConfig.apiHost(),
                        shovelDaemonConfig.apiPort(),
                        apiContext);

                shovels.add(getRemoteShovel(nodeFactory, apiFactory, newProfile.getId()));

                shovels.add(getRemoteShovel(apiFactory, nodeFactory, newProfile.getId()));
            }
        }

        shovelManager.setShovels(shovels);
    }

    private Amqp091ConnectionFactory createAmqp091ConnectionFactory(String host, int port, TLSContext context) {
        return new Amqp091ConnectionFactory(
                new RabbitMqAmqp091ConnectionSupplier(
                        executorService,
                        host,
                        port,
                        context));
    }

    private MessageShovel getDeviceIncomingShovel(ConnectionFactory sourcefactory, ConnectionFactory destinationFactory, String profileId) {
        return new DeviceIncomingMessageShovel(
                new ShovelContext(
                        sourcefactory,
                        new Amqp091ConsumerConnectionSettings(
                                "amq.topic",
                                "DeviceIncomingMessageShovel",
                                "#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                                "far.app"
                        )),
                profileId);
    }

    private MessageShovel getDeviceOutgoingShovel(ConnectionFactory sourceFactory, ConnectionFactory destinationFactory, String profileId) {
        return new DeviceOutgoingMessageShovel(
                new ShovelContext(
                        sourceFactory,
                        new Amqp091ConsumerConnectionSettings(
                                "far.app",
                                "DeviceOutgoingMessageShovel",
                                "#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                            "amq.topic"
                        )),
                profileId);
    }

    private MessageShovel getAppIncomingShovel(ConnectionFactory sourceFactory, ConnectionFactory destinationFactory) {
        return new HopsIncrementingMessageShovel(
                new ShovelContext(
                        sourceFactory,
                        new Amqp091ConsumerConnectionSettings(
                                "far.incoming",
                                "AppIncomingMessageShovel",
                                "#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                                "far.app"
                        )));
    }

    private MessageShovel getAppOutgoingShovel(ConnectionFactory sourceFactory, ConnectionFactory destinationFactory) {
        return new ZeroHopsMessageShovel(
                new ShovelContext(
                        sourceFactory,
                        new Amqp091ConsumerConnectionSettings(
                                "far.app",
                                "AppOutgoingMessageShovel",
                                "#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                                "far.outgoing"
                        )));
    }

    private MessageShovel getRemoteShovel(ConnectionFactory sourceFactory, ConnectionFactory destinationFactory, String profileId) {
        return new SimpleMessageShovel(
                new ShovelContext(
                        sourceFactory,
                        new Amqp091ConsumerConnectionSettings(
                                "far.outgoing",
                                "ApiMessageShovel",
                                profileId + ".#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                                "far.incoming"
                        )));
    }

}
