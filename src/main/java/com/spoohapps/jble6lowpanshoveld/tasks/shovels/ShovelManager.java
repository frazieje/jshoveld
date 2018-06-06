package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import java.util.List;

public interface ShovelManager {
    void start();
    void stop();
    void setShovels(List<ShovelContext> shovels);
    List<ShovelDescriptor> shovelDescriptors();
}
