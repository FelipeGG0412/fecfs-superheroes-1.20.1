package com.fecfssuperheroes.util;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.event.FecfsKeyInputHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ScreenUtils {
    private static final String modId = FecfsSuperheroes.MOD_ID;
    private static Identifier chargeBar;
    private static Identifier keybindingSlot;
    private static Identifier leftClick;
    private static Identifier rightClick;
    public static void drawScreen(DrawContext context, int x, int y, KeyBinding binding, String abilityName) {
        if(binding.isPressed()) {
            context.getMatrices().push();
            context.getMatrices().scale(1.05f, 1.05f, 1.05f);
            keybindingSlot = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/slot_pressed.png");
            context.drawTexture(keybindingSlot, x, y, 0, 0, 20, 20, 20, 20);
            context.drawText(MinecraftClient.getInstance().textRenderer, FecfsKeyInputHandler.getAbilityKey(binding), x + 7, y + 6, 0xFFFFFF, true);
            context.drawText(MinecraftClient.getInstance().textRenderer, abilityName, x + 22, x + 6, 0xFFFFFF, true);
            context.getMatrices().pop();
        } else {
            context.getMatrices().push();
            context.getMatrices().scale(1.05f, 1.05f, 1.05f);
            keybindingSlot = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/slot.png");
            context.drawTexture(keybindingSlot, x, y, 0, 0, 20, 20, 20, 20);
            context.drawText(MinecraftClient.getInstance().textRenderer, FecfsKeyInputHandler.getAbilityKey(binding), x + 7, y + 6, 0xFFFFFF, true);
            context.drawText(MinecraftClient.getInstance().textRenderer, abilityName, x + 22, y + 6, 0xFFFFFF, true);
            context.getMatrices().pop();
        }
    }
}
