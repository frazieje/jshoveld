package com.spoohapps.jble6lowpanshoveld.testhelpers;

import com.spoohapps.jble6lowpanshoveld.model.Profile;
import com.spoohapps.jble6lowpanshoveld.tasks.profile.ProfileManager;

import java.util.function.Consumer;

public class FakeProfileManager implements ProfileManager {

    private Profile profile;
    private Consumer<Profile> profileConsumer;

    public FakeProfileManager() {
        try {
            profile = Profile.generate();
        } catch (Exception e) {
            e.printStackTrace();
            profile = null;
        }
    }

    @Override
    public void set(Profile profile) {
        this.profile = profile;
        if (profileConsumer != null)
            profileConsumer.accept(profile);
    }

    @Override
    public Profile get() {
        return profile;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void onChanged(Consumer<Profile> profileFunction) {
        this.profileConsumer = profileFunction;
    }
}
