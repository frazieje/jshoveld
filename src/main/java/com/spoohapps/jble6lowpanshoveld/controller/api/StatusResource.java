package com.spoohapps.jble6lowpanshoveld.controller.api;

import com.spoohapps.jble6lowpanshoveld.ShovelDaemonController;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Path("/")
public class StatusResource {

    private final ShovelDaemonController controller;

    private final ExecutorService httpHandler;

    @Inject
    public StatusResource(ShovelDaemonController controller, ExecutorService httpHandler) {
        this.controller = controller;
        this.httpHandler = httpHandler;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public void getSomething(@Suspended AsyncResponse asyncResponse) {
        CompletableFuture
                .supplyAsync(controller.shovelDescriptors(), httpHandler)
                .thenApply(descriptors -> asyncResponse.resume(Response.ok().entity(descriptors).build()))
                .exceptionally(e -> asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()));
    }

}
