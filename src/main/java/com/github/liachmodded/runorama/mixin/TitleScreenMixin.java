/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama.mixin;

import com.github.liachmodded.runorama.Runorama;
import com.github.liachmodded.runorama.client.CloseableBinder;
import com.github.liachmodded.runorama.client.RunoramaCubeMapRenderer;
import com.github.liachmodded.runorama.client.VanillaPanorama;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Shadow private @Final @Mutable RotatingCubeMapRenderer backgroundRenderer;
    private CloseableBinder binder;

    protected TitleScreenMixin(Text text_1) {
        super(text_1);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    public void onOpenScreen(CallbackInfo ci) {
        CloseableBinder[] binders = new CloseableBinder[6];
        for (Supplier</* Nullable */CloseableBinder> supplier : Runorama.getInstance().makeScreenshotBinders()) {
            CloseableBinder binder = supplier.get();
            if (binder != null) {
                this.binder = binder;
                break;
            }
        }

        if (this.binder == null) {
            this.binder = new VanillaPanorama();
        }

        this.backgroundRenderer = new RotatingCubeMapRenderer(new RunoramaCubeMapRenderer(binder));
    }

    @Inject(method = "removed()V", at = @At("RETURN"))
    public void onDiscard(CallbackInfo ci) {
        binder.close();
    }
}
