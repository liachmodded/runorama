/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama.client;

import com.github.liachmodded.runorama.RunoSettings;
import com.github.liachmodded.runorama.Runorama;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.util.math.MathHelper;

public class RunoramaRotatingCubeMapRenderer extends RotatingCubeMapRenderer {

    private final MinecraftClient client;
    private final RunoramaCubeMapRenderer cubeMap;
    private final RunoSettings settings;
    private float time;

    public RunoramaRotatingCubeMapRenderer(RunoramaCubeMapRenderer cubeMap) {
        super(cubeMap);
        this.client = MinecraftClient.getInstance();
        this.cubeMap = cubeMap;
        this.settings = Runorama.getInstance().getSettings();
    }

    @Override
    public void render(float float_1, float float_2) {
        this.time += float_1;
        // param: client, xAngle, yAngle, alphaF
        // x: up and down; y: rotate clockwise; z: left and right
        this.cubeMap.draw(this.client,
                MathHelper.sin(this.time * 0.001F) * 5.0F + 25.0F,
                (float) (-this.time * 0.1F * settings.rotationSpeed.get()),
                0f,
                float_2
        );
    }
}
