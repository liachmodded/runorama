/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The settings for Runorama, loaded from the configuration.
 */
public final class RunoSettings {

    /**
     * Whether the title screen's panorama is replaced by the taken ones.
     */
    public final RunoConfig.Property<Boolean> replaceTitleScreen;
    /**
     * The integer ID of the next panorama that will be taken.
     */
    public final RunoConfig.Property<Integer> next;
    /**
     * The max ID possible for panoramas. When the next ID exceeds this size, old panoramas will be removed.
     */
    public final RunoConfig.Property<Integer> poolSize;
    /**
     * The rotation speed of the panorama in the {@link net.minecraft.client.gui.screen.TitleScreen title screen}.
     *
     * <p>Negative values will make the rotation counterclockwise.
     *
     * <p>A speed that's too high may make viewers dizzy!
     */
    public final RunoConfig.Property<Double> rotationSpeed;
    /**
     * Whether to include vanilla panorama for the panoramas Runorama mod would display.
     */
    public final RunoConfig.Property<Boolean> includeVanillaPanorama;
    private final Runorama mod;
    private final RunoConfig config;
    private final Path path;

    public RunoSettings(Runorama mod, RunoConfig backend, Path path) {
        this.mod = mod;
        this.config = backend;
        this.path = path;
        this.replaceTitleScreen = config.booleanProperty("replace-title-screen", true);
        this.next = config.integerProperty("next", 0);
        this.poolSize = config.integerProperty("pool-size", 1000);
        this.rotationSpeed = config.doubleProperty("clockwise-rotation-speed", 1.0D);
        this.includeVanillaPanorama = config.booleanProperty("include-vanilla-panorama", false);
    }

    public Path getCurrentRunoramaFolder() {
        Path parent = mod.getCacheImagePath(next.get());
        if (!Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException ex) {
                Runorama.LOGGER.error("Cannot create folder {} for runorama!", parent, ex);
            }
        }
        return parent;
    }

    public void nextScreenshot() {
        next.set((next.get() + 1) % poolSize.get());
        save();
    }

    public void save() {
        this.config.save(path);
    }

}
