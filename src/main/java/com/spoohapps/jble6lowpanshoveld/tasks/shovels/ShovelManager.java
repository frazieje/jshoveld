package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import java.util.List;

public interface ShovelManager {
    void start();
    void stop();
    List<ShovelDescriptor> shovelDescriptors();
}
