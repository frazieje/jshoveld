package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.farcommon.model.Profile;

import java.util.List;

public interface ShovelDaemonController {
    List<String> shovelDescriptors();
    void restartShovels();
    Profile getProfile();
    void setProfile(Profile p);
}
