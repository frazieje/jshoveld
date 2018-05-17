package com.spoohapps.jble6lowpanshoveld.tasks.profile;

import com.spoohapps.jble6lowpanshoveld.model.Profile;

import java.util.function.Consumer;

public interface ProfileManager {
    void set(Profile profile);
    Profile get();
    void start();
    void stop();
    void onChanged(Consumer<Profile> profileFunction);
}
