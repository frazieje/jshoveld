package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.farcommon.model.Profile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ShovelDaemonController extends Remote {
    List<String> shovelDescriptors() throws RemoteException;
    void restartShovels() throws RemoteException;
    Profile getProfile() throws RemoteException;
    void setProfile(Profile p) throws RemoteException;
}
