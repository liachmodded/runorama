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

    @Override
    public void draw(MinecraftClient client, float float_1, float float_2, float float_3) {
        this.draw(client, float_1, float_2, 0f, float_3);
    }

    public void draw(MinecraftClient client, float xAngle, float yAngle, float zAngle, float alphaF) {
        Tessellator tessellator_1 = Tessellator.getInstance();
        BufferBuilder bufferBuilder_1 = tessellator_1.getBufferBuilder();
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(Matrix4f
                .method_4929(85.0D, (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight(), 0.05F,
                        10.0F));
        RenderSystem.matrixMode(5888);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();

        for (int pass = 0; pass < 4; ++pass) {
            RenderSystem.pushMatrix();
            float float_4 = ((float) (pass % 2) / 2.0F - 0.5F) / 256.0F;
            float float_5 = ((float) (pass / 2) / 2.0F - 0.5F) / 256.0F;
            float float_6 = 0.0F;
            RenderSystem.translatef(float_4, float_5, float_6);
            RenderSystem.rotatef(xAngle, 1.0F, 0.0F, 0.0F);
            RenderSystem.rotatef(yAngle, 0.0F, 1.0F, 0.0F);
            RenderSystem.rotatef(zAngle, 0.0F, 0.0F, 1.0F);

            for (int int_3 = 0; int_3 < 6; ++int_3) {
                //            minecraftClient_1.getTextureManager().bindTexture(this.faces[int_3]);
                binder.bind(int_3);
                bufferBuilder_1.begin(7, VertexFormats.POSITION_UV_COLOR);
                int alpha = Math.round(255.0F * alphaF) / (pass + 1);
                if (int_3 == 0) {
                    bufferBuilder_1.vertex(-1.0D, -1.0D, 1.0D).texture(0.0F, 0.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(-1.0D, 1.0D, 1.0D).texture(0.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, 1.0D).texture(1.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(1.0D, -1.0D, 1.0D).texture(1.0F, 0.0F).color(255, 255, 255, alpha).next();
                }

                if (int_3 == 1) {
                    bufferBuilder_1.vertex(1.0D, -1.0D, 1.0D).texture(0.0F, 0.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, 1.0D).texture(0.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, -1.0D).texture(1.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(1.0D, -1.0D, -1.0D).texture(1.0F, 0.0F).color(255, 255, 255, alpha).next();
                }

                if (int_3 == 2) {
                    bufferBuilder_1.vertex(1.0D, -1.0D, -1.0D).texture(0.0F, 0.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, -1.0D).texture(0.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(-1.0D, 1.0D, -1.0D).texture(1.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(-1.0D, -1.0D, -1.0D).texture(1.0F, 0.0F).color(255, 255, 255, alpha).next();
                }

                if (int_3 == 3) {
                    bufferBuilder_1.vertex(-1.0D, -1.0D, -1.0D).texture(0.0F, 0.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(-1.0D, 1.0D, -1.0D).texture(0.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(-1.0D, 1.0D, 1.0D).texture(1.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(-1.0D, -1.0D, 1.0D).texture(1.0F, 0.0F).color(255, 255, 255, alpha).next();
                }

                if (int_3 == 4) {
                    bufferBuilder_1.vertex(-1.0D, -1.0D, -1.0D).texture(0.0F, 0.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(-1.0D, -1.0D, 1.0D).texture(0.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(1.0D, -1.0D, 1.0D).texture(1.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(1.0D, -1.0D, -1.0D).texture(1.0F, 0.0F).color(255, 255, 255, alpha).next();
                }

                if (int_3 == 5) {
                    bufferBuilder_1.vertex(-1.0D, 1.0D, 1.0D).texture(0.0F, 0.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(-1.0D, 1.0D, -1.0D).texture(0.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, -1.0D).texture(1.0F, 1.0F).color(255, 255, 255, alpha).next();
                    bufferBuilder_1.vertex(1.0D, 1.0D, 1.0D).texture(1.0F, 0.0F).color(255, 255, 255, alpha).next();
                }

                tessellator_1.draw();
            }

            RenderSystem.popMatrix();
            RenderSystem.colorMask(true, true, true, false);
        }

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
