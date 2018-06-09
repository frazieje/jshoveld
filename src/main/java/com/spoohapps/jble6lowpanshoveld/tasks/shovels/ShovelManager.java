package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import java.util.List;
import java.util.Set;

public interface ShovelManager {
    void start();
    void stop();
    void setShovels(Set<ShovelContext> shovels);
    List<ShovelDescriptor> shovelDescriptors();
}
