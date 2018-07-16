package com.spoohapps.jble6lowpanshoveld.controller.model;

import java.util.List;

public class Status {

    private int shovelCount;

    private List<String> shovelDescriptors;

    public Status(List<String> shovelDescriptors) {
        this.shovelDescriptors = shovelDescriptors;
    }

    public int getShovelCount() {
        return shovelCount;
    }

    public void setShovelCount(int shovelCount) {
        this.shovelCount = shovelCount;
    }

    public List<String> getShovelDescriptors() {
        return shovelDescriptors;
    }

    public void setShovelDescriptors(List<String> shovelDescriptors) {
        this.shovelDescriptors = shovelDescriptors;
    }

}
