package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.jble6lowpanshoveld.model.Profile;

import java.rmi.Remote;
import java.util.List;

public interface ShovelDaemonController extends Remote {
    List<String> shovelDescriptors();
    void restartShovels();
    Profile getProfile();
    void setProfile(Profile p);
}
