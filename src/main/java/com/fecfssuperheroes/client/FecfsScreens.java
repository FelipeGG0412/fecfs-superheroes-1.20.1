package com.fecfssuperheroes.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class FecfsScreens {
    public static void register() {
        HudRenderCallback.EVENT.register(new RaimiScreen(MinecraftClient.getInstance(), MinecraftClient.getInstance().getItemRenderer()));
    }
}
