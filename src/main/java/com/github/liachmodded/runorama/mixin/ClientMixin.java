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
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

    @Shadow public abstract Entity getCameraEntity();

    @Shadow public WorldRenderer worldRenderer;

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
            Screen oldScreen = this.currentScreen;
            Entity watcher = getCameraEntity();
            if (watcher == null) {
                watcher = player;
            }
            LivingEntity living = watcher instanceof LivingEntity ? (LivingEntity) watcher : null;
            boolean oldHudHidden = options.hudHidden;
            float oldPrevPitch = watcher.prevPitch;
            float oldPrevYaw = watcher.prevYaw;
            float oldPrevHeadYaw;
            float oldPitch = watcher.pitch;
            float oldYaw = watcher.yaw;
            float oldHeadYaw;
            if (living != null) {
                oldPrevHeadYaw = living.prevHeadYaw;
                oldHeadYaw = living.headYaw;
            } else {
                oldPrevHeadYaw = 0;
                oldHeadYaw = 0;
            }
            boolean oldField_4001 = ((GameRendererAccessor) gameRenderer).getField_4001();
            currentScreen = null;

            watcher.pitch = 0;
            // leave the yaw as-is
            watcher.prevPitch = 0;
            watcher.prevYaw = watcher.yaw;
            if (living != null) {
                living.prevHeadYaw = living.headYaw;
            }
            options.hudHidden = true;
            ((GameRendererAccessor) gameRenderer).setField_4001(true);
            // start
            Path root = runorama.getSettings().getCurrentRunoramaFolder();
            for (int i = 0; i < 4; i++) {
                doRender(boolean_1, long_1);
                takeScreenshot(runorama, root, i);
                rotate(watcher, 0f, 90f);
            }
            rotate(watcher, -90f, 0f);
            doRender(boolean_1, long_1);
            takeScreenshot(runorama, root, 4);
            rotate(watcher, 180f, 0f);
            doRender(boolean_1, long_1);
            takeScreenshot(runorama, root, 5);
            // end
            currentScreen = oldScreen;
            options.hudHidden = oldHudHidden;
            watcher.prevYaw = oldPrevYaw;
            watcher.prevPitch = oldPrevPitch;
            watcher.pitch = oldPitch;
            watcher.yaw = oldYaw;
            if (living != null) {
                living.headYaw = oldHeadYaw;
                living.prevHeadYaw = oldPrevHeadYaw;
            }
            ((GameRendererAccessor) gameRenderer).setField_4001(oldField_4001);
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

    private void rotate(Entity watcher, float pitch, float yaw) {
        watcher.pitch += pitch;
        watcher.yaw += yaw;
        watcher.prevPitch = watcher.pitch;
        watcher.prevYaw = watcher.yaw;
        if (watcher instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) watcher;
            living.headYaw = watcher.yaw;
            living.prevHeadYaw = watcher.yaw;
        }
    }

    private void takeScreenshot(Runorama runorama, Path folder, int id) {
        NativeImage shot = ScreenshotUtils.takeScreenshot(window.getFramebufferWidth(), window.getFramebufferHeight(),
                framebuffer);
        runorama.saveScreenshot(shot, folder, id);
    }

}
