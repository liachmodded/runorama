/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RunoSettings {

    private final Runorama mod;
    private final RunoConfig config;
    private final Path path;
    public final RunoConfig.Property<Integer> next;
    public final RunoConfig.Property<Integer> poolSize;

    public RunoSettings(Runorama mod, RunoConfig backend, Path path) {
        this.mod = mod;
        this.config = backend;
        this.path = path;
        this.next = config.integerProperty("next", 0);
        this.poolSize = config.integerProperty("pool-size", 1000);
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
