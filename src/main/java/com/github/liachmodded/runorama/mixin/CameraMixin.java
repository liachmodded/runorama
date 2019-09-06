/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.runorama.mixin;

import com.github.liachmodded.runorama.Runorama;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPitch(F)F"))
    public float runorama$onGetPitch(Entity entity, float partial) {
        Runorama runorama = Runorama.getInstance();
        if (runorama.isTakingScreenshot()) {
            return runorama.getDesiredPitch();
        }
        return entity.getPitch(partial);
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getYaw(F)F"))
    public float runorama$onGetYaw(Entity entity, float partial) {
        Runorama runorama = Runorama.getInstance();
        if (runorama.isTakingScreenshot()) {
            return runorama.getDesiredYaw();
        }
        return entity.getYaw(partial);
    }
}
