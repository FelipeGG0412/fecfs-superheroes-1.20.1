package com.fecfssuperheroes.util;

import com.fecfssuperheroes.FecfsSuperheroes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ScreenUtils {
    private static final String modId = FecfsSuperheroes.MOD_ID;
    private static Identifier keyBindingSlot;
//    Identifier keyBindingCooldown;
//    Identifier keyBindingPressed;
    private static Identifier abilitySlot;
    public static void renderAbilities(DrawContext ctx, int x, int y, int textureWidth, int textureHeight, String abilityName, Item abilityIcon,
                                       KeyBinding ability, boolean cooldown) {
        if(cooldown) {
            keyBindingSlot = new Identifier(modId, "textures/gui/keybinding_cooldown.png");
            ctx.drawTexture(keyBindingSlot, x, y, textureWidth, textureHeight, textureWidth, textureHeight, textureWidth, textureHeight);
        } else {
            keyBindingSlot = ability.isPressed() ? new Identifier(modId, "textures/gui/keybinding_pressed.png")
                    : new Identifier(modId, "textures/gui/keybinding_slot.png");
            ctx.drawTexture(keyBindingSlot, x, y, textureWidth, textureHeight, textureWidth, textureHeight, textureWidth, textureHeight);
        }
        abilitySlot = new Identifier(modId, "textures/gui/ability_slot.png");
        ctx.drawTexture(abilitySlot, x + 24, y, textureWidth+1, textureHeight, textureWidth+1, textureHeight, textureWidth+1, textureHeight);
        drawIcon(ctx, x, y, abilityIcon.getDefaultStack());
        drawPressableText(ctx, MinecraftClient.getInstance().textRenderer, abilityName, x, y, ability.isPressed(), cooldown);

    }
    private static void drawPressableText(DrawContext context, TextRenderer textRenderer, String name, int x, int y, boolean isPressed, boolean cooldown) {
        if (isPressed) {
            context.drawText(textRenderer, name, x + 10, y + 8, 0xFFFFFF, true);
        } else {
            context.getMatrices().push();
            context.getMatrices().scale(1.15f, 1.15f, 1.15f);
            context.drawText(textRenderer, name, (int) ((x + 10) / 1.15f), (int) ((y + 8) / 1.15f), 0xFFFFFF, true);
            context.getMatrices().pop();
        }
    }
    private static void drawIcon(DrawContext context, int x, int y, ItemStack stack) {
        context.drawItem(stack, x + 27, (y + 4));
    }
}
