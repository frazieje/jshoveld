package com.spoohapps.jble6lowpanshoveld.controller;

import com.spoohapps.jble6lowpanshoveld.ShovelDaemonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.stream.Collectors;

import static com.spoohapps.jble6lowpanshoveld.controller.HTTPShovelDaemonControllerServer.ControllerName;

public class CommandLineShovelDaemonController {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineShovelDaemonController.class);

    public static void main(String[] args) {
    }
}
