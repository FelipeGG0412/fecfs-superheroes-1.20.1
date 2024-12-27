package com.fecfssuperheroes.client;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.item.FecfsItems;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class FecfsScreens {
    public static void register() {
        HudRenderCallback.EVENT.register(new RaimiScreen(MinecraftClient.getInstance(), MinecraftClient.getInstance().getItemRenderer()));
        HudRenderCallback.EVENT.register(new WebShootersScreen(MinecraftClient.getInstance(), MinecraftClient.getInstance().getItemRenderer()));
    }
}
