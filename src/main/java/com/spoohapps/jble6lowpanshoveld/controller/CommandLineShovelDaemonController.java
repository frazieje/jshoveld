package com.spoohapps.jble6lowpanshoveld.controller;

import com.spoohapps.jble6lowpanshoveld.ShovelDaemonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.stream.Collectors;

import static com.spoohapps.jble6lowpanshoveld.controller.RemoteShovelDaemonControllerBroadcaster.ControllerName;

public class CommandLineShovelDaemonController {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineShovelDaemonController.class);

    public static void main(String[] args) {
        String command = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(null);
            ShovelDaemonController stub = (ShovelDaemonController) registry.lookup(ControllerName);

            if (command != null) {
                if (command.toLowerCase().equals("-status")) {
                    System.out.println(stub.shovelDescriptors().stream().collect(Collectors.joining("\n")));
                }
            }
        } catch (Exception e) {
            logger.error("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
