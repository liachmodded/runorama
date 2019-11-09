/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama.client;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

public final class BoundImage implements CloseableBinder {

    private final NativeImageBackedTexture[] textures;

    public BoundImage(NativeImage[] image) {
        this.textures = new NativeImageBackedTexture[6];
        for (int i = 0; i < 6; i++) {
            this.textures[i] = new NativeImageBackedTexture(image[i]);
        }
    }

    @Override
    public void bind(int i) {
        textures[i].bindTexture();
    }

    @Override
    public void close() {
        for (NativeImageBackedTexture texture : textures) {
            texture.close();
        }
    }
}
