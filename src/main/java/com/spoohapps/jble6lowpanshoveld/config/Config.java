package com.spoohapps.jble6lowpanshoveld.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config implements ShovelDaemonConfig {

    private static final List<String> profileFilePathKeys = Arrays.asList("profileFilePath", "f");
    private static final List<String> apiUrlKeys = Arrays.asList("apiUrl", "a");
    private static final List<String> apiExchangeKeys = Arrays.asList("apiExchange", "x");
    private static final List<String> apiPortKeys = Arrays.asList("apiPort", "p");
    private static final List<String> incomingExchangeKeys = Arrays.asList("incomingExchange", "i");
    private static final List<String> deviceExchangeKeys = Arrays.asList("deviceExchange", "d");
    private static final List<String> appExchangeKeys = Arrays.asList("appExchange", "b");
    private static final List<String> outgoingExchangeKeys = Arrays.asList("outgoingExchange", "o");
    private static final List<String> localPortKeys = Arrays.asList("localPort", "l");

    private static final Set<String> keys =
            Stream.of(profileFilePathKeys,
                        apiUrlKeys,
                        apiExchangeKeys,
                        apiPortKeys,
                        incomingExchangeKeys,
                        deviceExchangeKeys,
                        appExchangeKeys,
                        outgoingExchangeKeys,
                        localPortKeys)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

    private String profileFilePath;
    private String apiUrl;
    private String apiExchange;
    private int apiPort;
    private int localPort;
    private String incomingExchange;
    private String deviceExchange;
    private String appExchange;
    private String outgoingExchange;

    private Config() {}

    public static Config fromDefaults() {
        Config config = new Config();
        config.profileFilePath = "./profile.conf";
        config.apiUrl = "";
        config.apiExchange = "amq.app";
        config.apiPort = 5672;
        config.incomingExchange = "amq.incoming";
        config.deviceExchange = "amq.device";
        config.appExchange = "amq.app";
        config.outgoingExchange = "amq.outgoing";
        config.localPort = 5672;
        return config;
    }

    public static ShovelDaemonConfig fromStream(InputStream stream) {
        Properties prop = new Properties();

        try {
            Config config = new Config();

            if (stream == null)
                return config;

            prop.load(stream);

            prop.stringPropertyNames().forEach(p -> config.setArg(p, prop.getProperty(p)));

            return config;

        } catch (IOException ex) {
            ex.printStackTrace();
            return new Config();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ShovelDaemonConfig apply(ShovelDaemonConfig other) {

        Arrays.stream(ShovelDaemonConfig.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0)
                .forEach(method -> {
                    try {
                        Object result = method.invoke(other);
                        if (result != null) {
                            if (result.getClass().isAssignableFrom(String.class)) {
                                Field declaredField = Config.class.getDeclaredField(method.getName());
                                declaredField.setAccessible(true);
                                declaredField.set(this, result);
                            } else if (result.getClass().isAssignableFrom(Integer.class)) {
                                if (((int) result) > 0) {
                                    Field declaredField = Config.class.getDeclaredField(method.getName());
                                    declaredField.setAccessible(true);
                                    declaredField.set(this, result);
                                }
                            }
                        }
                    } catch (IllegalAccessException
                            | InvocationTargetException
                            | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                });
        return this;

    }

    private void setArg(String propertyName, String propertyValue) {
        if (keys.contains(propertyName)) {
            try {
                Field declaredField = Config.class.getDeclaredField(propertyName);
                declaredField.setAccessible(true);

                if (declaredField.getType().isAssignableFrom(String.class))
                    declaredField.set(this, propertyValue);
                else if (declaredField.getType().isAssignableFrom(int.class))
                    declaredField.set(this, Integer.parseInt(propertyValue));

            } catch (NoSuchFieldException
                    | SecurityException
                    | IllegalArgumentException
                    | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Config fromArgs(String[] args) {
        Config config = new Config();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && i != (args.length - 1)) {
                String arg = args[i].substring(1);
                String val = args[i + 1];
                if (keys.contains(arg)) {
                    Arrays.stream(Config.class.getDeclaredFields())
                            .filter(declaredField -> declaredField.getName()
                            .endsWith("Keys")).forEach(declaredField -> {
                        try {
                            declaredField.setAccessible(true);
                            List<String> keys = ((List<String>)declaredField.get(config));
                            if (keys.contains(arg)) {
                                String fieldName = declaredField.getName()
                                        .substring(0, declaredField.getName().length()-4);
                                config.setArg(fieldName, val);
                            }
                        } catch (IllegalAccessException iae) {
                            iae.printStackTrace();
                        }
                    });
                }
            }
        }
        return config;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!Config.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final ShovelDaemonConfig other = (ShovelDaemonConfig) obj;

        return Arrays.stream(Config.class.getDeclaredFields()).allMatch(declaredField -> {
            try {
                declaredField.setAccessible(true);
                if (declaredField.getType().isAssignableFrom(String.class)) {
                    if ((declaredField.get(this) == null) ? (declaredField.get(other) != null) : !declaredField.get(this).equals(declaredField.get(other))) {
                        return false;
                    }
                } else if (declaredField.getType().isAssignableFrom(int.class)) {
                    if (((int)declaredField.get(this)) != ((int)declaredField.get(other))) {
                        return false;
                    }
                }
            } catch (IllegalAccessException iae) {
                return false;
            }
            return true;
        });
    }

    @Override
    public String profileFilePath() {
        return profileFilePath;
    }

    @Override
    public String apiUrl() {
        return apiUrl;
    }

    @Override
    public String apiExchange() {
        return apiExchange;
    }

    @Override
    public int apiPort() {
        return apiPort;
    }

    @Override
    public int localPort() {
        return localPort;
    }

    @Override
    public String incomingExchange() {
        return incomingExchange;
    }

    @Override
    public String deviceExchange() {
        return deviceExchange;
    }

    @Override
    public String appExchange() {
        return appExchange;
    }

    @Override
    public String outgoingExchange() {
        return outgoingExchange;
    }
}
