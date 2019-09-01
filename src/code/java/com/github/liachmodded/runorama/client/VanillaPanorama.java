/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public final class VanillaPanorama implements CloseableBinder {

    @Override
    public void bind(int i) {
        MinecraftClient.getInstance()
                .getTextureManager()
                .bindTexture(new Identifier("minecraft", "textures/gui/title/background/panorama_" + i + ".png"));
    }

    @Override
    public void close() {
    }
}
