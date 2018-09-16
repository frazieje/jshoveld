package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.farcommon.model.Profile;

import java.util.List;
import java.util.function.Supplier;

public interface ShovelDaemonController {
    Supplier<List<String>> shovelDescriptors();
    void restartShovels();
    Profile getProfile();
    void setProfile(Profile p);
}
