package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.farcommon.Config;
import com.spoohapps.farcommon.config.ConfigBuilder;
import com.spoohapps.farcommon.messaging.amqp091.Exchanges;
import com.spoohapps.jble6lowpanshoveld.config.DefaultShovelDaemonConfig;
import com.spoohapps.jble6lowpanshoveld.config.ShovelDaemonConfig;
import com.spoohapps.jble6lowpanshoveld.controller.HTTPShovelDaemonControllerServer;
import com.spoohapps.jble6lowpanshoveld.controller.ShovelDaemonControllerServer;
import com.spoohapps.farcommon.model.Profile;
import com.spoohapps.farcommon.model.TLSContext;
import com.spoohapps.farcommon.messaging.*;
import com.spoohapps.farcommon.messaging.amqp091.Amqp091ConnectionFactory;
import com.spoohapps.farcommon.messaging.amqp091.Amqp091ConsumerConnectionSettings;
import com.spoohapps.farcommon.messaging.amqp091.Amqp091PublisherConnectionSettings;
import com.spoohapps.farcommon.messaging.amqp091.rabbitmq.RabbitMqAmqp091ConnectionSupplier;
import com.spoohapps.jble6lowpanshoveld.tasks.shovels.*;
import com.spoohapps.jble6lowpanshoveld.tasks.profile.FileBasedProfileManager;
import com.spoohapps.jble6lowpanshoveld.tasks.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ShovelDaemon implements ShovelDaemonController {

    private ScheduledExecutorService executorService;

    private ShovelDaemonConfig shovelDaemonConfig;

    private ProfileManager profileManager;

    private ShovelManager shovelManager;

    private ShovelDaemonControllerServer controllerBroadcaster;

    private Set<ConnectionFactory> connectionFactories;

    private final Logger logger = LoggerFactory.getLogger(ShovelDaemon.class);

    public ShovelDaemon() {}

    public ShovelDaemon(ShovelDaemonConfig config, ProfileManager profileManager, ShovelManager shovelManager, ShovelDaemonControllerServer controllerBroadcaster) {

        shovelDaemonConfig = config;

        this.profileManager = profileManager;

        this.shovelManager = shovelManager;

        executorService = Executors.newScheduledThreadPool(10);

        this.profileManager.onChanged(this::setProfileInternal);

        this.controllerBroadcaster = controllerBroadcaster;

        this.connectionFactories = new HashSet<>();
    }

    public ShovelDaemon(String[] args) {

        String configFilePath = null;
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-configFile")) {
                    configFilePath = args[i + 1];
                }
            }
        } catch (Exception ignored) {}

        ConfigBuilder<ShovelDaemonConfig> configBuilder = Config.from(ShovelDaemonConfig.class);

        configBuilder.apply(new DefaultShovelDaemonConfig());

        try {
            if (configFilePath != null) {
                configBuilder.apply(Files.newInputStream(Paths.get(configFilePath)));
            }
        } catch (Exception ignored) {}

        configBuilder.apply(args);

        shovelDaemonConfig = configBuilder.build();

        initialize();
    }

    private void initialize() {

        executorService = Executors.newScheduledThreadPool(16);

        logger.info("source host: {}", shovelDaemonConfig.nodeHost());
        logger.info("source port: {}", shovelDaemonConfig.nodePort());

        logger.info("api host: {}", shovelDaemonConfig.apiHost());
        logger.info("api port: {}", shovelDaemonConfig.apiPort());

        logger.info("controller port: {}", shovelDaemonConfig.controllerPort());

        connectionFactories = ConcurrentHashMap.newKeySet();

        shovelManager = new AutomaticRestartingShovelManager(executorService, 5000);

        profileManager = new FileBasedProfileManager(Paths.get(shovelDaemonConfig.profileFilePath()));

        profileManager.onChanged(this::setProfileInternal);

        controllerBroadcaster = new HTTPShovelDaemonControllerServer(this, shovelDaemonConfig.controllerPort());
    }

    public void start() throws Exception {

        logger.info("Starting...");

        shovelManager.start();

        profileManager.start();

        controllerBroadcaster.start();
    }

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

    public static void main(String[] args) {
        ShovelDaemon daemon = new ShovelDaemon(args);
        try {
            daemon.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Supplier<List<String>> shovelDescriptors() {
        return shovelManager::shovelDescriptors;
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

        String profileId = newProfile.getId();

        TLSContext nodeContext = newProfile.getNodeContext();

        if (nodeContext != null && nodeContext.hasValue()) {

            ConnectionFactory nodeFactory = getOrCreateAmqp091ConnectionFactory(
                    shovelDaemonConfig.nodeHost(),
                    shovelDaemonConfig.nodePort(),
                    nodeContext);

            shovels.add(getAppIncomingShovel(nodeFactory, nodeFactory));

            shovels.add(getAppOutgoingShovel(nodeFactory, nodeFactory));

            if (profileId != null) {

                shovels.add(getDeviceIncomingShovel(nodeFactory, nodeFactory, profileId));

                shovels.add(getDeviceOutgoingShovel(nodeFactory, nodeFactory, profileId));

                TLSContext messageContext = newProfile.getRemoteMessageContext();

                if (messageContext != null && messageContext.hasValue()) {

                    ConnectionFactory apiFactory = getOrCreateAmqp091ConnectionFactory(
                            shovelDaemonConfig.apiHost(),
                            shovelDaemonConfig.apiPort(),
                            messageContext);

                    shovels.add(getApiIncomingShovel(apiFactory, nodeFactory, profileId));

                    shovels.add(getApiOutgoingShovel(nodeFactory, apiFactory, profileId));
                }
            }
        }

        shovelManager.setShovels(shovels);
    }

    private ConnectionFactory getOrCreateAmqp091ConnectionFactory(String host, int port, TLSContext context) {
        ConnectionFactory newFactory = new Amqp091ConnectionFactory(
                new RabbitMqAmqp091ConnectionSupplier(
                        executorService,
                        host,
                        port,
                        context));
        for (ConnectionFactory factory : connectionFactories) {
            if (factory.equals(newFactory))
                return factory;
        }
        connectionFactories.add(newFactory);
        return newFactory;
    }

    private MessageShovel getDeviceIncomingShovel(ConnectionFactory sourcefactory, ConnectionFactory destinationFactory, String profileId) {
        String name = "DeviceIncomingShovel";
        return new DeviceIncomingMessageShovel(
                new ShovelContext(
                        sourcefactory,
                        new Amqp091ConsumerConnectionSettings(
                                Exchanges.DEVICE,
                                name + "Queue",
                                "#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                                Exchanges.FAR_APP
                        )),
                name,
                profileId);
    }

    private MessageShovel getDeviceOutgoingShovel(ConnectionFactory sourceFactory, ConnectionFactory destinationFactory, String profileId) {
        String name = "DeviceOutgoingShovel";
        return new DeviceOutgoingMessageShovel(
                new ShovelContext(
                        sourceFactory,
                        new Amqp091ConsumerConnectionSettings(
                                Exchanges.FAR_APP,
                                name + "Queue",
                                "#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                                Exchanges.DEVICE
                        )),
                name,
                profileId);
    }

    private MessageShovel getAppIncomingShovel(ConnectionFactory sourceFactory, ConnectionFactory destinationFactory) {
        String name = "AppIncomingShovel";
        return new HopsIncrementingMessageShovel(
                new ShovelContext(
                        sourceFactory,
                        new Amqp091ConsumerConnectionSettings(
                                Exchanges.FAR_INCOMING,
                                name + "Queue",
                                "#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                                Exchanges.FAR_APP
                        )),
                        name);
    }

    private MessageShovel getAppOutgoingShovel(ConnectionFactory sourceFactory, ConnectionFactory destinationFactory) {
        String name = "AppOutgoingShovel";
        return new ZeroHopsMessageShovel(
                new ShovelContext(
                        sourceFactory,
                        new Amqp091ConsumerConnectionSettings(
                                Exchanges.FAR_APP,
                                name + "Queue",
                                "#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                                Exchanges.FAR_OUTGOING
                        )),
                        name);
    }

    private MessageShovel getApiIncomingShovel(ConnectionFactory sourceFactory, ConnectionFactory destinationFactory, String profileId) {
        String name = "ApiIncomingShovel";
        return new SimpleMessageShovel(
                new ShovelContext(
                        sourceFactory,
                        new Amqp091ConsumerConnectionSettings(
                                Exchanges.FAR_OUTGOING,
                                profileId,
                                profileId + ".#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                                Exchanges.FAR_INCOMING
                        )),
                        name);
    }

    private MessageShovel getApiOutgoingShovel(ConnectionFactory sourceFactory, ConnectionFactory destinationFactory, String profileId) {
        String name = "ApiOutgoingShovel";
        return new SimpleMessageShovel(
                new ShovelContext(
                        sourceFactory,
                        new Amqp091ConsumerConnectionSettings(
                                Exchanges.FAR_OUTGOING,
                                name + "Queue",
                                "#"),
                        destinationFactory,
                        new Amqp091PublisherConnectionSettings(
                                Exchanges.FAR_INCOMING
                        )),
                name);
    }

}
