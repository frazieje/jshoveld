package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ShovelManagerImpl implements ShovelManager {

    private final Map<ShovelContext, MessageShovel> shovelMap = new ConcurrentHashMap<>();

    public ShovelManagerImpl() {
    }

    public ShovelManagerImpl(Set<ShovelContext> shovelContexts) {
        setShovels(shovelContexts);
    }

    @Override
    public void start() {
        shovelMap.forEach((context, shovel) -> {
            shovel.start();
        });
    }

    @Override
    public void stop() {

    }

    @Override
    public void setShovels(Set<ShovelContext> shovels) {
        shovels.forEach(context -> shovelMap.putIfAbsent(context, context.createShovel()));
    }

    @Override
    public List<ShovelDescriptor> shovelDescriptors() {
        return null;
    }

}
