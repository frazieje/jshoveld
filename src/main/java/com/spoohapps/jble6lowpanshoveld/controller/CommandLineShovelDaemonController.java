package com.spoohapps.jble6lowpanshoveld.controller;

import com.spoohapps.jble6lowpanshoveld.ShovelDaemonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.stream.Collectors;

import static com.spoohapps.jble6lowpanshoveld.controller.RemoteShovelDaemonControllerBroadcaster.ControllerName;

public class CommandLineShovelDaemonController {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineShovelDaemonController.class);

    public static void main(String[] args) {
        String portStr = (args.length < 1) ? null : args[0];
        String command = (args.length < 2) ? null : args[1];
        try {
            Registry registry = LocateRegistry.getRegistry(portStr != null ? Integer.parseInt(portStr) : 0);
            ShovelDaemonController stub = (ShovelDaemonController) registry.lookup(ControllerName);

            if (command != null) {
                if (command.toLowerCase().equals("-status")) {
                    List<String> shovelDescriptors = stub.shovelDescriptors();
                    System.out.println("Shovels: " + shovelDescriptors.size());
                    System.out.println(shovelDescriptors.stream().collect(Collectors.joining("\n")));
                }
            }
        } catch (Exception e) {
            logger.error("Client exception: " + e.toString(), e);
        }
    }
}
