package com.fecfssuperheroes.client;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.item.FecfsItems;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class FecfsScreens {
    public static void register() {
        HudRenderCallback.EVENT.register(new RaimiScreen(MinecraftClient.getInstance(), MinecraftClient.getInstance().getItemRenderer()));
        HudRenderCallback.EVENT.register(new WebShootersScreen(MinecraftClient.getInstance(), MinecraftClient.getInstance().getItemRenderer()));
    }
    private static final Identifier abilityTemplate = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/ability_template.png");
    private static final Identifier abilityExtender = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/ability_extender.png");
    private static final Identifier abilityExtenderCooldown = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/ability_extender_cooldown.png");
    private static final Identifier abilityPressed = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/ability_pressed.png");

    public static void renderCooldown(DrawContext drawContext, boolean cooldown, int width, int height, int x, int y) {
        drawContext.drawTexture(abilityExtenderCooldown, x + 22, y, width, height, width, height, width, height, width, height);
//        if(!cooldown) {
//            drawContext.drawTexture(abilityExtender,  x + 22, y, width, height, width, height, width, height, width, height);
//        }

    }
    public static void renderHud(DrawContext drawContext, int addX, int addY, int width, int height, int x, int y, KeyBinding ability,
                                 TextRenderer renderer, String name) {
        if(ability.isPressed()) {
            drawContext.drawTexture(abilityPressed, x, y, width, height, 16, 16, 16, 16,
                    16, 16);
            drawContext.drawText(renderer, name, x + 10, y + 8, 0xFFFFFF, false);
        } else {
            drawContext.drawTexture(abilityTemplate, x, y, width, height, 16, 16, 16, 16,
                    16, 16);

            drawContext.getMatrices().push();
            drawContext.getMatrices().scale(1.225f, 1.225f, 1.0f);
            drawContext.drawText(renderer, name, x + addX, y + addY, 0XFFFFFF,true);
            drawContext.getMatrices().pop();
        }
    }
    public static void drawItem(DrawContext drawContext, int x, int y, ItemStack stack) {
        drawContext.drawItem(stack, x + 27, y + 3);
    }
}
