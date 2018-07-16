package com.spoohapps.jble6lowpanshoveld.controller.api;

import com.spoohapps.jble6lowpanshoveld.ShovelDaemonController;
import com.spoohapps.jble6lowpanshoveld.controller.model.Status;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class StatusResource {

    private final ShovelDaemonController controller;

    @Inject
    public StatusResource(ShovelDaemonController controller) {
        this.controller = controller;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Status getSomething() {
        return new Status(controller.shovelDescriptors());
    }

}
