package com.spoohapps.jble6lowpanshoveld.tasks.shovels;

import com.spoohapps.jble6lowpanshoveld.model.Profile;
import com.spoohapps.jble6lowpanshoveld.tasks.profile.ProfileManager;

import java.util.List;

public class ShovelManagerImpl implements ShovelManager {

    private final ProfileManager profileManager;

    private Profile currentProfile;

    public ShovelManagerImpl(ProfileManager profileManager) {
        this.profileManager = profileManager;
        this.currentProfile = profileManager.get();
        this.profileManager.onChanged(this::profileUpdated);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    private synchronized void profileUpdated(Profile newProfile) {

    }

    @Override
    public List<ShovelDescriptor> shovelDescriptors() {
        return null;
    }

}
