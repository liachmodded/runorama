/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Function;

public final class RunoConfig {

    final Properties properties;

    public static RunoConfig load(Path file) {
        Properties props = new Properties();
        try (InputStreamReader stream = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
            props.load(stream);
        } catch (IOException ex) {
            Runorama.LOGGER.error("Failed to load config file from {}!", file, ex);
        }
        return new RunoConfig(props);
    }

    public RunoConfig(Properties properties) {
        this.properties = properties;
    }

    public Property<Boolean> booleanProperty(String key, boolean value) {
        return property(key, value, Boolean::valueOf, b -> b ? "true" : "false");
    }

    public Property<Integer> integerProperty(String key, int value) {
        return property(key, value, st -> {
            try {
                return Integer.parseInt(st);
            } catch (NumberFormatException ex) {
                return value;
            }
        }, i -> i.toString());
    }

    public Property<String> stringProperty(String key, String value) {
        return property(key, value, Function.identity(), Function.identity());
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
            try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8)) {
                properties.store(writer, "runorama config");
            }
        } catch (IOException ex) {
            Runorama.LOGGER.error("Failed to save runorama config to {}!", file, ex);
        }
    }

    public final class Property<T> {

        private final String key;
        private T value;
        private final Function<T, String> saver;

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
