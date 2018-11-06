package com.spoohapps.jble6lowpanshoveld.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Status {

    private List<String> shovelDescriptors;

    public Status(List<String> shovelDescriptors) {
        this.shovelDescriptors = shovelDescriptors;
    }

    @JsonProperty
    public int getShovelCount() {
        return shovelDescriptors.size();
    }

    public List<String> getShovelDescriptors() {
        return shovelDescriptors;
    }

    public void setShovelDescriptors(List<String> shovelDescriptors) {
        this.shovelDescriptors = shovelDescriptors;
    }

}
