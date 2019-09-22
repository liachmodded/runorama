/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama;

import com.github.liachmodded.runorama.client.BoundImage;
import com.github.liachmodded.runorama.client.CloseableBinder;
import com.github.liachmodded.runorama.client.VanillaPanorama;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Runorama mod class.
 */
public final class Runorama implements ClientModInitializer {

    /**
     * The mod id, or the namespace of Runorama.
     */
    public static final String ID = "runorama";
    /**
     * The logger.
     */
    public static final Logger LOGGER = LogManager.getLogger(ID);
    private static Runorama instance;
    private Path cacheDir;
    private Path settingsFile;
    private RunoSettings settings;
    private boolean takingScreenshot;
    private float desiredPitch;
    private float desiredYaw;
    /**
     * Whether to take a screenshot the next time a frame is rendered.
     */
    public boolean needsScreenshot = false;

    /**
     * Easy way to create an {@link Identifier} with Runorama's namespace.
     *
     * @param name the name
     * @return a new identifier
     */
    public static Identifier name(String name) {
        return new Identifier(ID, name);
    }

    /**
     * Returns Runorama's instance.
     */
    public static Runorama getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        Path configRoot = FabricLoader.getInstance().getConfigDirectory().toPath();
        cacheDir = configRoot.resolve(ID + "-cache");
        settingsFile = configRoot.resolve(ID + ".properties");
        readSettings();

        FabricKeyBinding screenshotKey =
                FabricKeyBinding.Builder.create(name("runorama"), InputUtil.Type.KEYSYM, 'H'/*h*/, "key.categories.misc").build();
        KeyBindingRegistry.INSTANCE.register(screenshotKey);
        ClientTickCallback.EVENT.register(c -> {
            if (screenshotKey.isPressed()) {
                this.needsScreenshot = true;
            }
        });
    }

    void readSettings() {
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException ex) {
            LOGGER.error("Cannot create directory {} for storing auto screenshots!", cacheDir, ex);
        }

        RunoConfig config = Files.exists(settingsFile) ? RunoConfig.load(settingsFile) : new RunoConfig(new Properties());

        settings = new RunoSettings(this, config, settingsFile);

        config.save(settingsFile);
    }

    Path getCacheImagePath(int id) {
        return cacheDir.resolve("panorama-" + id);
    }

    /**
     * Make a list of candidates for panoramas.
     *
     * <p>Each candidate may fail to evaluate when called.
     *
     * @return the list of candidates
     */
    public List<Supplier</* Nullable */CloseableBinder>> makeScreenshotBinders() {
        List<Supplier</* Nullable */CloseableBinder>> ret = new ArrayList<>();

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(cacheDir, eachFolder -> {
            if (!Files.isDirectory(eachFolder)) {
                return false;
            }
            for (int i = 0; i < 6; i++) {
                Path part = eachFolder.resolve("panorama_" + i + ".png");
                if (!Files.isRegularFile(part)) {
                    return false;
                }
            }
            return true;
        })) {
            for (final Path eachFolder : paths) {
                ret.add(() -> {
                    NativeImage[] images = new NativeImage[6];
                    for (int j = 0; j < 6; j++) {
                        Path part = eachFolder.resolve("panorama_" + j + ".png");
                        try (InputStream stream = Files.newInputStream(part)) {
                            images[j] = NativeImage.read(stream);
                        } catch (IOException ex) {
                            Runorama.LOGGER.error("Failed to bind custom screenshot part at {}!", part, ex);
                            return null;
                        }
                    }
                    return new BoundImage(images);
                });
            }
        } catch (IOException ex) {
            return Collections.emptyList();
        }

        if (settings.includeVanillaPanorama.get()) {
            ret.add(VanillaPanorama::new);
        }

        Collections.shuffle(ret);
        return ret;
    }

    /**
     * Save a partial screenshot for a panorama.
     *
     * @param screenshot the image
     * @param folder the panorama folder
     * @param i the face index
     */
    public void saveScreenshot(NativeImage screenshot, Path folder, int i) {
        ResourceImpl.RESOURCE_IO_EXECUTOR.execute(() -> {
            try {
                int width = screenshot.getWidth();
                int height = screenshot.getHeight();
                int int_3 = 0;
                int int_4 = 0;
                if (width > height) {
                    int_3 = (width - height) / 2;
                    width = height;
                } else {
                    int_4 = (height - width) / 2;
                    height = width;
                }
                NativeImage saved = new NativeImage(width, height, false);
                screenshot.resizeSubRectTo(int_3, int_4, width, height, saved);
                saved.writeFile(folder.resolve("panorama_" + i + ".png"));
            } catch (IOException var27) {
                Runorama.LOGGER.warn("Couldn't save screenshot", var27);
            } finally {
                screenshot.close();
            }
        });
    }

    /**
     * Gets the settings of the mod, loaded from the configuration file.
     */
    public RunoSettings getSettings() {
        return this.settings;
    }

    /**
     * Sets the rotation for the camera for the panorama and start taking a screenshot.
     *
     * @param pitch the pitch
     * @param yaw the yaw
     */
    public void setPanoramicRotation(float pitch, float yaw) {
        this.takingScreenshot = true;
        this.desiredPitch = pitch;
        this.desiredYaw = yaw;
    }

    /**
     * Returns true if a screenshot is in progress.
     */
    public boolean isTakingScreenshot() {
        return takingScreenshot;
    }

    /**
     * Returns the desired pitch for the camera.
     */
    public float getDesiredPitch() {
        return desiredPitch;
    }

    /**
     * Returns the desired yaw for the camera.
     */
    public float getDesiredYaw() {
        return desiredYaw;
    }

    /**
     * End the series of screenshot.
     */
    public void endPanorama() {
        this.takingScreenshot = false;
    }
}
