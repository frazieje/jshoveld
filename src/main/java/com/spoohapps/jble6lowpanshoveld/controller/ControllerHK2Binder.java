package com.spoohapps.jble6lowpanshoveld.controller;

import com.spoohapps.jble6lowpanshoveld.ShovelDaemonController;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControllerHK2Binder extends AbstractBinder {

    private ShovelDaemonController controller;

    public ControllerHK2Binder(ShovelDaemonController controller) {
        this.controller = controller;
    }

    @Override
    protected void configure() {
        bindFactory(new Factory<ShovelDaemonController>() {
            @Override
            public ShovelDaemonController provide() {
                return controller;
            }

            @Override
            public void dispose(ShovelDaemonController instance) {
                //ignore
            }
        }).to(ShovelDaemonController.class).in(Singleton.class);

        bindFactory(new Factory<ExecutorService>() {
            @Override
            public ExecutorService provide() {
                return Executors.newCachedThreadPool();
            }

            @Override
            public void dispose(ExecutorService instance) {
                instance.shutdownNow();
            }
        }).to(ExecutorService.class).in(Singleton.class);
    }
}
