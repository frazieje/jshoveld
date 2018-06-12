package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ShovelManagerImpl implements ShovelManager {

    private final Set<MessageShovel> shovels = ConcurrentHashMap.newKeySet();

    private final Logger logger = LoggerFactory.getLogger(ShovelManagerImpl.class);

    private final ScheduledExecutorService executor;

    private AtomicBoolean isActive = new AtomicBoolean(false);

    public ShovelManagerImpl(ScheduledExecutorService executorService) {
        executor = executorService;
    }

    public ShovelManagerImpl(ScheduledExecutorService executorService, Set<MessageShovel> shovelContexts) {
        this(executorService);
        setShovels(shovelContexts);
    }

    @Override
    public void start() {
        logger.info("starting shovel manager...");
        isActive.set(true);
        shovels.forEach(shovel -> {
            logger.info("starting shovel " + shovel.getConnectionDescriptions().stream().collect(Collectors.joining(",")));
            shovel.onStopped(() -> shovelStopped(shovel));
            shovel.start();
        });
        logger.info("shovel manager started");
    }

    @Override
    public void stop() {
        logger.info("stopping shovel manager...");
        isActive.set(false);
        shovels.forEach(shovel -> {
            logger.info("stopping shovel " + shovel.getConnectionDescriptions().stream().collect(Collectors.joining(",")));
            shovel.stop();
        });
        logger.info("shovel manager stopped");
    }

    @Override
    public void setShovels(Set<MessageShovel> newShovels) {
        shovels.addAll(newShovels);
        shovels.forEach(shovel -> {
            if (!newShovels.contains(shovel)) {
                logger.info("stopping and removing shovel " + shovel.getConnectionDescriptions().stream().collect(Collectors.joining(",")));
                shovel.stop();
            }
        });

    }

    @Override
    public List<ShovelDescriptor> shovelDescriptors() {
        return null;
    }

    private void shovelStopped(MessageShovel shovel) {

        String name = shovel.getConnectionDescriptions().stream().collect(Collectors.joining(","));
        logger.info("shovel " + name + " stopped");

        boolean removed = shovels.remove(shovel);

        if (!removed) {
            logger.info("shovel " + name + " not present, do nothing");
        } else {
            logger.info("shovel " + name + " removed");
        }

        if (isActive.get()) {
            MessageShovel newShovel = shovel.clone();
            String newName = newShovel.getConnectionDescriptions().stream().collect(Collectors.joining(","));
            newShovel.onStopped(() -> shovelStopped(newShovel));
            shovels.add(newShovel);

            logger.info("scheduling start of new shovel " + newName);

            executor.schedule(() -> {
                        if (isActive.get()) {
                            logger.info("starting shovel " + newName);
                            newShovel.start();
                        } else {
                            logger.info("manager stopped; cancelling start of shovel " + newName);
                        }
                    },
                    5,
                    TimeUnit.SECONDS);
        } else {
            logger.info("manager stopped; not scheduling start of new shovel");
        }
    }

}
