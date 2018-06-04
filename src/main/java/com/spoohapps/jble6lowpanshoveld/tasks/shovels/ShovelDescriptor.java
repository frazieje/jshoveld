package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import java.util.List;

public interface ShovelDescriptor {
    boolean isActive();
    String getName();
    List<String> getConnectionDescriptions();
}
