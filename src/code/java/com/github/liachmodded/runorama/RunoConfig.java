/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Function;

/**
 * The configuration format for Runorama, backed by Java's builtin {@link Properties}.
 */
public final class RunoConfig {

    final Properties properties;

    /**
     * Create a configuration from a {@link Properties}.
     *
     * @param properties the backing properties
     */
    public RunoConfig(Properties properties) {
        this.properties = properties;
    }

    /**
     * Load the configuration from a property file.
     *
     * <p>The file should be encoded in UTF-8.
     *
     * @param file the configuration file
     * @return the loaded configuration
     */
    public static RunoConfig load(Path file) {
        Properties props = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            props.load(reader);
        } catch (IOException ex) {
            Runorama.LOGGER.error("Failed to load config file from {}!", file, ex);
        }
        return new RunoConfig(props);
    }

    public Property<Boolean> booleanProperty(String key, boolean value) {
        return property(key, value, Boolean::valueOf);
    }

    public Property<Integer> integerProperty(String key, int value) {
        return property(key, value, st -> {
            try {
                return Integer.parseInt(st);
            } catch (NumberFormatException ex) {
                return value;
            }
        });
    }

    public Property<Double> doubleProperty(String key, double value) {
        return property(key, value, st -> {
            try {
                return Double.parseDouble(st);
            } catch (NumberFormatException ex) {
                return value;
            }
        });
    }

    public Property<String> stringProperty(String key, String value) {
        return property(key, value, Function.identity(), Function.identity());
    }

    public <T> Property<T> property(String key, T value, Function<String, T> reader) {
        return property(key, value, reader, String::valueOf);
    }

    public <T> Property<T> property(String key, T value, Function<String, T> reader, Function<T, String> saver) {
        T t;
        if (properties.containsKey(key)) {
            t = reader.apply(properties.getProperty(key));
        } else {
            properties.put(key, saver.apply(value));
            t = value;
        }
        return new Property<>(key, t, saver);
    }

    public void save(Path file) {
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                properties.store(writer, "runorama config");
            }
        } catch (IOException ex) {
            Runorama.LOGGER.error("Failed to save runorama config to {}!", file, ex);
        }
    }

    /**
     * A property in the configuration.
     * @param <T> the value's type
     */
    public final class Property<T> {

        private final String key;
        private final Function<T, String> saver;
        private T value;

        Property(String key, T value, Function<T, String> saver) {
            this.key = key;
            this.value = value;
            this.saver = saver;
        }

        public String getKey() {
            return key;
        }

        public void set(T value) {
            this.value = value;
            RunoConfig.this.properties.put(key, saver.apply(value));
        }

        public T get() {
            return value;
        }
    }

}
