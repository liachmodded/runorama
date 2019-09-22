/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama.mixin;

import com.github.liachmodded.runorama.Runorama;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.NonBlockingThreadExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;

@Mixin(MinecraftClient.class)
public abstract class ClientMixin extends NonBlockingThreadExecutor<Runnable> {

    @Shadow public Window window;
    @Shadow private GlFramebuffer framebuffer;
    @Shadow public GameRenderer gameRenderer;
    @Shadow private boolean paused;
    @Shadow private float pausedTickDelta;
    @Shadow private @Final RenderTickCounter renderTickCounter;
    @Shadow public ClientPlayerEntity player;
    @Shadow public GameOptions options;
    @Shadow public ClientWorld world;
    @Shadow public Screen currentScreen;
    @Shadow public Entity cameraEntity;

    public ClientMixin(String string_1) {
        super(string_1);
    }

    @Inject(method = "render(Z)V",
            at = @At(target = "Lnet/minecraft/client/toast/ToastManager;draw()V", value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void runorama$afterGameRender(boolean boolean_1, CallbackInfo ci, long long_1) {
        if (world == null || player == null) {
            return;
        }
        Runorama runorama = Runorama.getInstance();
        if (runorama.needsScreenshot) {
            Runorama.LOGGER.info("Taking screenshot");
            runorama.needsScreenshot = false;
            // record
            boolean oldHudHidden = options.hudHidden;
            boolean oldFov90 = ((GameRendererAccessor) gameRenderer).isFov90();
            Screen oldScreen = this.currentScreen;
            // set
            options.hudHidden = true;
            ((GameRendererAccessor) gameRenderer).setFov90(true);
            currentScreen = null;
            // take
            float yaw = (cameraEntity == null ? player : cameraEntity).getYaw(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta);
            Path root = runorama.getSettings().getCurrentRunoramaFolder();
            for (int i = 0; i < 4; i++) {
                runorama.setPanoramicRotation(0f, yaw);
                doRender(boolean_1, long_1);
                takeScreenshot(runorama, root, i);
                yaw += 90f;
            }
            runorama.setPanoramicRotation(-90f, yaw);
            doRender(boolean_1, long_1);
            takeScreenshot(runorama, root, 4);
            runorama.setPanoramicRotation(90f, yaw);
            doRender(boolean_1, long_1);
            takeScreenshot(runorama, root, 5);
            runorama.endPanorama();
            // restore
            currentScreen = oldScreen;
            options.hudHidden = oldHudHidden;
            ((GameRendererAccessor) gameRenderer).setFov90(oldFov90);

            player.addChatMessage(new TranslatableText("runorama.shot", new LiteralText(root.toAbsolutePath().toString()).styled(style -> {
                style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, root.toAbsolutePath().toString()));
            })), false);
            runorama.getSettings().nextScreenshot();
        }
    }

    private void doRender(boolean boolean_1, long long_1) {
        // Need to modify so that Camera gets right pitch/yaw for setRotation
        gameRenderer.render(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta, long_1, boolean_1);
    }

    private void takeScreenshot(Runorama runorama, Path folder, int id) {
        NativeImage shot = ScreenshotUtils.takeScreenshot(window.getFramebufferWidth(), window.getFramebufferHeight(),
                framebuffer);
        runorama.saveScreenshot(shot, folder, id);
    }

}
