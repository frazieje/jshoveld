package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.jble6lowpanshoveld.config.ShovelDaemonConfig;
import com.spoohapps.jble6lowpanshoveld.controller.ShovelDaemonControllerBroadcaster;
import com.spoohapps.jble6lowpanshoveld.model.Profile;
import com.spoohapps.jble6lowpanshoveld.tasks.profile.ProfileManager;
import com.spoohapps.jble6lowpanshoveld.tasks.shovels.MessageShovel;
import com.spoohapps.jble6lowpanshoveld.tasks.shovels.ShovelManager;
import com.spoohapps.jble6lowpanshoveld.testhelpers.ProfileFileHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WhenStartingShovelDaemonTests {

    private ShovelDaemon daemon;

    private ProfileManager mockProfileManager;
    private ShovelManager mockShovelManager;

    private ShovelDaemonControllerBroadcaster mockControllerBroadcaster;

    private String expectedNodeHost = "localhost";
    private int expectedNodePort = 5671;
    private String expectedApiHost = "www.spoohapps.com";
    private int expectedApiPort = 5671;
    private String expectedProfileFilePath = "/some/path/profile.conf";

    @Captor
    private ArgumentCaptor<Consumer<Profile>> setProfile;

    @Captor
    private ArgumentCaptor<Set<MessageShovel>> shovelsCaptor;

    @BeforeAll
    public void context() throws Exception {

        MockitoAnnotations.initMocks(this);

        ShovelDaemonConfig mockConfig = mock(ShovelDaemonConfig.class);

        mockProfileManager = mock(ProfileManager.class);

        mockShovelManager = mock(ShovelManager.class);

        mockControllerBroadcaster = mock(ShovelDaemonControllerBroadcaster.class);

        when(mockConfig.nodeHost()).thenReturn(expectedNodeHost);
        when(mockConfig.nodePort()).thenReturn(expectedNodePort);

        when(mockConfig.apiHost()).thenReturn(expectedApiHost);
        when(mockConfig.apiPort()).thenReturn(expectedApiPort);

        when(mockConfig.profileFilePath()).thenReturn(expectedProfileFilePath);

        daemon = new ShovelDaemon(mockConfig, mockProfileManager, mockShovelManager, mockControllerBroadcaster);

        verify(mockProfileManager).onChanged(setProfile.capture());

        daemon.start();
    }

    @AfterAll
    public void teardown() throws Exception {
        daemon.stop();
    }

    @Test
    public void shouldStartProfileManager() {
        verify(mockProfileManager).start();
    }

    @Test
    public void shouldStartShovelManager() {
        verify(mockShovelManager).start();
    }

    @Test
    public void shouldRegisterForProfileEvents() {
        verify(mockProfileManager).onChanged(any());
    }

    @Test
    public void shouldStartControllerBroadcaster() {
        verify(mockControllerBroadcaster).start();
    }

    @Test
    public void shouldSetShovels() {
        Profile profile = Profile.from(ProfileFileHelper.streamWithProfileIdAndNode("1234abcd"));
        setProfile.getValue().accept(profile);
        verify(mockShovelManager).setShovels(shovelsCaptor.capture());

        Set<MessageShovel> shovels = shovelsCaptor.getValue();

        assertTrue(shovels.size() > 0);
    }

}
