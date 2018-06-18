package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class AutomaticRestartingShovelManager implements ShovelManager {

    private final Set<MessageShovel> shovels = ConcurrentHashMap.newKeySet();

    private final Logger logger = LoggerFactory.getLogger(AutomaticRestartingShovelManager.class);

    private final ScheduledExecutorService executor;

    private AtomicBoolean isActive = new AtomicBoolean(false);

    private final long restartDelayMs;

    public AutomaticRestartingShovelManager(ScheduledExecutorService executorService, long restartDelayMs) {
        executor = executorService;
        this.restartDelayMs = restartDelayMs;
    }

    public AutomaticRestartingShovelManager(ScheduledExecutorService executorService, Set<MessageShovel> shovelContexts, long restartDelayMs) {
        this(executorService, restartDelayMs);
        setShovels(shovelContexts);
    }

    @Override
    public void start() {
        logger.info("starting shovel manager...");
        if (!isActive.get()) {
            isActive.set(true);
            shovels.forEach(shovel -> {
                logger.info("starting shovel " + shovelDescriptor(shovel));
                shovel.onStopped(() -> shovelStopped(shovel));
                shovel.start();
            });
            logger.info("shovel manager started");
        } else {
            logger.info("shovel manager already started");
        }
    }

    @Override
    public void stop() {
        logger.info("stopping shovel manager...");
        if (isActive.get()) {
            isActive.set(false);
            shovels.forEach(shovel -> {
                logger.info("stopping shovel " + shovelDescriptor(shovel));
                shovel.stop();
            });
            logger.info("shovel manager stopped");
        } else {
            logger.info("shovel manager not running");
        }
    }

    @Override
    public void setShovels(Set<MessageShovel> newShovels) {
        logger.info("setting shovels to shovel manager...");

        Set<MessageShovel> toRemove = new HashSet<>();
        shovels.forEach(shovel -> {
            if (!newShovels.contains(shovel)) {
                toRemove.add(shovel);
            }
        });

        Set<MessageShovel> toStart = new HashSet<>();

        newShovels.forEach(shovel -> {
            if (shovels.add(shovel)) {
                logger.info("added shovel " + shovelDescriptor(shovel));
                toStart.add(shovel);
            } else {
                logger.info("ignored already present shovel " + shovelDescriptor(shovel));
            }
        });

        toRemove.forEach(shovel -> {
            logger.info("removing shovel " + shovelDescriptor(shovel));
            shovels.remove(shovel);
        });

        if (isActive.get()) {
            toStart.forEach(shovel -> {
                logger.info("starting shovel " + shovelDescriptor(shovel));
                shovel.start();
            });
            toRemove.forEach(shovel -> {
                logger.info("stopping shovel " + shovelDescriptor(shovel));
                shovel.stop();
            });
        }
    }

    @Override
    public List<String> shovelDescriptors() {
        return shovels.stream()
                .map(this::shovelDescriptor)
                .collect(Collectors.toList());
    }

    private String shovelDescriptor(MessageShovel shovel) {
        String description = shovel.getConnectionDescriptions().stream()
                .collect(Collectors.joining(","));
        return shovel.getName() + " (" + shovel.getType() + ")" + " - " + "{" + description + "}";
    }

    private void shovelStopped(MessageShovel shovel) {

        String name = shovelDescriptor(shovel);

        logger.info("shovel " + name + " stopped");

        boolean removed = shovels.remove(shovel);

        if (!removed) {
            logger.info("shovel " + name + " not present, do nothing");
        } else {
            logger.info("shovel " + name + " removed, add clone");
            if (isActive.get()) {
                MessageShovel newShovel = shovel.clone();
                String newName = shovelDescriptor(newShovel);
                newShovel.onStopped(() -> shovelStopped(newShovel));
                shovels.add(newShovel);

                logger.info("scheduling start of new shovel " + newName);

                executor.schedule(() -> {
                            if (isActive.get()) {
                                if (shovels.contains(newShovel)) {
                                    logger.info("starting shovel " + newName);
                                    newShovel.start();
                                } else {
                                    logger.info("shovel not found in collection; cancelling start of shovel " + newName);
                                }
                            } else {
                                logger.info("manager stopped; cancelling start of shovel " + newName);
                            }
                        },
                        restartDelayMs,
                        TimeUnit.MILLISECONDS);
            } else {
                logger.info("manager stopped; not scheduling start of new shovel");
            }
        }
    }

}
