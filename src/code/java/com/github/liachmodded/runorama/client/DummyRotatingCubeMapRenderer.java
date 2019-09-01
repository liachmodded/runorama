/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama.client;

import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;

public class DummyRotatingCubeMapRenderer extends RotatingCubeMapRenderer {

    public DummyRotatingCubeMapRenderer() {
        super(null);
    }

    @Override
    public void render(float float_1, float float_2) {
    }
}
