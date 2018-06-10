package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ShovelManagerImpl implements ShovelManager {

    private final Set<MessageShovel> shovels = ConcurrentHashMap.newKeySet();

    public ShovelManagerImpl() {
    }

    public ShovelManagerImpl(Set<MessageShovel> shovelContexts) {
        setShovels(shovelContexts);
    }

    @Override
    public void start() {
        shovels.forEach(shovel -> {
            shovel.onStopped(() -> shovelStopped(shovel));
            shovel.start();
        });
    }

    @Override
    public void stop() {
        shovels.forEach(MessageShovel::stop);
    }

    @Override
    public void setShovels(Set<MessageShovel> shovels) {
        this.shovels.retainAll(shovels);
        this.shovels.addAll(shovels);
    }

    @Override
    public List<ShovelDescriptor> shovelDescriptors() {
        return null;
    }

    private void shovelStopped(MessageShovel shovel) {
        MessageShovel newShovel = shovel.clone();
        newShovel.onStopped(() -> shovelStopped(newShovel));
        this.shovels.remove(shovel);
        this.shovels.add(newShovel);
        newShovel.start();
    }

}
