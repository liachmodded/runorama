package com.github.liachmodded.runorama.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public final class VanillaPanorama implements CloseableBinder {

    @Override
    public void bind(int i) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("minecraft", "textures/gui/title/background/panorama_" + i + ".png"));
    }

    @Override
    public void close() {
    }
}
