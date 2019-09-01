/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama.client;

import com.github.liachmodded.runorama.Runorama;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class RunoramaCubeMapRenderer extends CubeMapRenderer {

    private final CloseableBinder binder;

    public RunoramaCubeMapRenderer(CloseableBinder binder) {
        super(Runorama.name(""));
        this.binder = binder;
    }

    public void draw(MinecraftClient minecraftClient_1, float float_1, float float_2, float float_3) {
        Tessellator tessellator_1 = Tessellator.getInstance();
        BufferBuilder bufferBuilder_1 = tessellator_1.getBufferBuilder();
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(Matrix4f.method_4929(85.0D,
                (float) minecraftClient_1.window.getFramebufferWidth() / (float) minecraftClient_1.window.getFramebufferHeight(), 0.05F, 10.0F));
        RenderSystem.matrixMode(5888);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(class_4493.class_4535.SRC_ALPHA, class_4493.class_4534.ONE_MINUS_SRC_ALPHA, class_4493.class_4535.ONE,
                class_4493.class_4534.ZERO);

        for (int int_2 = 0; int_2 < 4; ++int_2) {
            RenderSystem.pushMatrix();
            float float_4 = ((float) (int_2 % 2) / 2.0F - 0.5F) / 256.0F;
            float float_5 = ((float) (int_2 / 2) / 2.0F - 0.5F) / 256.0F;
            float float_6 = 0.0F;
            RenderSystem.translatef(float_4, float_5, 0.0F);
            RenderSystem.rotatef(float_1, 1.0F, 0.0F, 0.0F);
            RenderSystem.rotatef(float_2, 0.0F, 1.0F, 0.0F);

            for (int int_3 = 0; int_3 < 6; ++int_3) {
                //            minecraftClient_1.getTextureManager().bindTexture(this.faces[int_3]);
                binder.bind(int_3);
                bufferBuilder_1.begin(7, VertexFormats.POSITION_UV_COLOR);
                int int_4 = Math.round(255.0F * float_3) / (int_2 + 1);
                if (int_3 == 0) {
                    bufferBuilder_1.vertex(-1.0D, -1.0D, 1.0D).texture(0.0D, 0.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(-1.0D, 1.0D, 1.0D).texture(0.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, 1.0D).texture(1.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(1.0D, -1.0D, 1.0D).texture(1.0D, 0.0D).color(255, 255, 255, int_4).next();
                }

                if (int_3 == 1) {
                    bufferBuilder_1.vertex(1.0D, -1.0D, 1.0D).texture(0.0D, 0.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, 1.0D).texture(0.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, -1.0D).texture(1.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(1.0D, -1.0D, -1.0D).texture(1.0D, 0.0D).color(255, 255, 255, int_4).next();
                }

                if (int_3 == 2) {
                    bufferBuilder_1.vertex(1.0D, -1.0D, -1.0D).texture(0.0D, 0.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, -1.0D).texture(0.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(-1.0D, 1.0D, -1.0D).texture(1.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(-1.0D, -1.0D, -1.0D).texture(1.0D, 0.0D).color(255, 255, 255, int_4).next();
                }

                if (int_3 == 3) {
                    bufferBuilder_1.vertex(-1.0D, -1.0D, -1.0D).texture(0.0D, 0.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(-1.0D, 1.0D, -1.0D).texture(0.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(-1.0D, 1.0D, 1.0D).texture(1.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(-1.0D, -1.0D, 1.0D).texture(1.0D, 0.0D).color(255, 255, 255, int_4).next();
                }

                if (int_3 == 4) {
                    bufferBuilder_1.vertex(-1.0D, -1.0D, -1.0D).texture(0.0D, 0.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(-1.0D, -1.0D, 1.0D).texture(0.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(1.0D, -1.0D, 1.0D).texture(1.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(1.0D, -1.0D, -1.0D).texture(1.0D, 0.0D).color(255, 255, 255, int_4).next();
                }

                if (int_3 == 5) {
                    bufferBuilder_1.vertex(-1.0D, 1.0D, 1.0D).texture(0.0D, 0.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(-1.0D, 1.0D, -1.0D).texture(0.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, -1.0D).texture(1.0D, 1.0D).color(255, 255, 255, int_4).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, 1.0D).texture(1.0D, 0.0D).color(255, 255, 255, int_4).next();
                }

                tessellator_1.draw();
            }

            RenderSystem.popMatrix();
            RenderSystem.colorMask(true, true, true, false);
        }

        bufferBuilder_1.setOffset(0.0D, 0.0D, 0.0D);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.matrixMode(5889);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }
}
