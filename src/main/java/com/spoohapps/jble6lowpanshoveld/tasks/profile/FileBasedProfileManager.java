package com.spoohapps.jble6lowpanshoveld.tasks.profile;

import com.spoohapps.jble6lowpanshoveld.model.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class FileBasedProfileManager implements ProfileManager {

    private final FileWatcher watcher;
    private final Path filePath;

    private final Logger logger = LoggerFactory.getLogger(FileBasedProfileManager.class);

    private Profile profile;

    private Consumer<Profile> profileConsumer;

    public FileBasedProfileManager(Path filePath) {
        this.watcher = new FileWatcher(filePath, this::onFileChanged);
        this.filePath = filePath;
    }

    @Override
    public void start() {
        logger.info("starting profile manager...");
        init();
        watcher.start();
    }

    @Override
    public void stop() {
        watcher.stop();
    }

    private void init() {
        profile = getStoredProfile();
        if (profile == null) {
            logger.info("profile null");
            setStoredProfile(profile);
        } else {
            notifyConsumer(profile);
        }
    }

    private synchronized void onFileChanged() {
        Profile newProfile = getStoredProfile();
        Profile current = currentProfile();

        if (current != null) {
            if (!current.equals(newProfile)) {
                if (newProfile != null) {
                    profile = newProfile;
                    notifyConsumer(newProfile);
                }
            }
        } else if (newProfile != null) {
            profile = newProfile;
            notifyConsumer(newProfile);
        }
    }

    private void notifyConsumer(Profile newProfile) {
        if (profileConsumer != null) {
            profileConsumer.accept(newProfile);
        }
    }

    private synchronized Profile currentProfile() {
        return profile;
    }

    private synchronized void setStoredProfile(Profile profile) {
        try {
            byte[] bytes;
            if (profile == null) {
                bytes = "".getBytes();
            } else {
                bytes = profile.toByteArray();
            }
            Files.write(filePath, bytes);
        } catch (Exception e) {
            logger.error("error writing profile file: " + e.getMessage());
        }
    }

    private synchronized Profile getStoredProfile() {
        try {
            return Profile.from(Files.newInputStream(filePath));
        } catch (Exception e) {
            logger.error("error reading profile file: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void set(Profile profile) {
        setStoredProfile(profile);
    }

    @Override
    public Profile get() {
        Profile current = currentProfile();
        if (current == null)
            return null;
        return Profile.from(new ByteArrayInputStream(current.toByteArray()));
    }

    @Override
    public void onChanged(Consumer<Profile> profile) {
        profileConsumer = profile;
    }

    private class FileWatcher {

        private final Path filePath;
        private AtomicBoolean stopped = new AtomicBoolean(false);
        private final Runnable fileChanged;

        FileWatcher(Path filePath, Runnable fileChanged) {
            this.filePath = filePath;
            this.fileChanged = fileChanged;
        }

        void start() {
            new Thread(this::run).start();
        }

        void stop() {
            stopped.set(true);
        }

        void run() {
            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                Path path = filePath.getParent();
                path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
                while (!stopped.get()) {
                    WatchKey key;
                    try { key = watcher.poll(25, TimeUnit.MILLISECONDS); }
                    catch (InterruptedException e) { return; }
                    if (key == null) { Thread.yield(); continue; }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();

                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            Thread.yield();
                            continue;
                        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY
                                && filename.toString().equals(filePath.getFileName().toString())) {
                            fileChanged.run();
                        }
                        boolean valid = key.reset();
                        if (!valid) { break; }
                    }
                    Thread.yield();
                }
            } catch (Throwable e) {
                logger.error("error starting file watcher");
            }
        }
    }
}
