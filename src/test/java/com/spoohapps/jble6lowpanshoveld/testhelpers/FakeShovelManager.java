package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.tasks.shovels.MessageShovel;
import com.spoohapps.jble6lowpanshoveld.tasks.shovels.ShovelManager;

import java.util.List;
import java.util.Set;

public class FakeShovelManager implements ShovelManager {

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void setShovels(Set<MessageShovel> shovels) {

    }

    @Override
    public List<String> shovelDescriptors() {
        return null;
    }
}
