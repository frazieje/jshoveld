package com.spoohapps.jble6lowpanshoveld;

import com.spoohapps.jble6lowpanshoveld.config.Config;
import com.spoohapps.jble6lowpanshoveld.config.ShovelDaemonConfig;
import org.apache.commons.daemon.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShovelDaemon implements Daemon {

    private ExecutorService executorService;
    private ShovelDaemonConfig shovelDaemonConfig;

    private void iniitialize(String[] args) {

    }

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {
        executorService = Executors.newFixedThreadPool(10);
        shovelDaemonConfig = Config.fromDefaults();
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public void destroy() {

    }
}
