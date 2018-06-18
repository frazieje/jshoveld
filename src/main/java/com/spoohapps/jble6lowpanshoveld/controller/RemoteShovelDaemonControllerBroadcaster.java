package com.spoohapps.jble6lowpanshoveld.controller;

import com.spoohapps.jble6lowpanshoveld.ShovelDaemonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RemoteShovelDaemonControllerBroadcaster implements ShovelDaemonControllerBroadcaster {

    private final ShovelDaemonController controller;

    private Registry rmiRegistry;
    private final int port;

    public static final String ControllerName = "jble6lowpanshoveld";

    private final Logger logger = LoggerFactory.getLogger(RemoteShovelDaemonControllerBroadcaster.class);

    public RemoteShovelDaemonControllerBroadcaster(ShovelDaemonController controller, int port) {
        this.controller = controller;
        this.port = port;
    }

    @Override
    public void start() {
        logger.info("Start broadcasting over RMI...");
        try {
            logger.info("create registry on port " + port);
            rmiRegistry = LocateRegistry.createRegistry(port);
            rmiRegistry.rebind(ControllerName, UnicastRemoteObject.exportObject(controller, 0));
            logger.info("RMI Server ready");
        } catch (Exception e) {
            logger.error("creating RMI broadcaster exception: " + e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        logger.info("Stop broadcasting over RMI...");
        try {
            rmiRegistry.unbind(ControllerName);
            UnicastRemoteObject.unexportObject(controller, true);
        } catch (Exception ex) {
            logger.error("RMI server exception: " + ex.getMessage());
        }
    }
}
