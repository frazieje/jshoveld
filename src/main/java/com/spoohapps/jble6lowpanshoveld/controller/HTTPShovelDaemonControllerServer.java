package com.spoohapps.jble6lowpanshoveld.controller;

import com.spoohapps.jble6lowpanshoveld.ShovelDaemonController;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class HTTPShovelDaemonControllerServer implements ShovelDaemonControllerServer {

    private final HttpServer httpServer;

    public static final String ControllerName = "jble6lowpanshoveld";

    private final Logger logger = LoggerFactory.getLogger(HTTPShovelDaemonControllerServer.class);

    public HTTPShovelDaemonControllerServer(ShovelDaemonController controller, int port) {

        URI baseUri = UriBuilder.fromPath("/").host("0.0.0.0").port(port).build();

        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();

        ResourceConfig config =
                new ResourceConfig()
                        .register(new ControllerHK2Binder(controller))
                        .packages("com.spoohapps.jble6lowpanshoveld.controller.api");

        httpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri, config, locator);
    }

    @Override
    public void start() {
        logger.info("Starting HTTP Server...");
        try {
            httpServer.start();
            logger.info("HTTP Server Started...");
        } catch (IOException e) {
            logger.error("Error starting HTTP Server", e);
        }
    }

    @Override
    public void stop() {
        logger.info("Stopping HTTP Server...");
        httpServer.shutdownNow();
        logger.info("HTTP Server Stopped.");
    }
}
