package com.fecfssuperheroes.client;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.ability.WebSwing;
import com.fecfssuperheroes.ability.WebZip;
import com.fecfssuperheroes.event.FecfsKeyInputHandler;
import com.fecfssuperheroes.item.FecfsItems;
import com.fecfssuperheroes.util.HeroUtil;
import com.fecfssuperheroes.util.ScreenUtils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;

public class WebShootersScreen extends InGameHud implements HudRenderCallback {
    public WebShootersScreen(MinecraftClient client, ItemRenderer itemRenderer) {
        super(client, itemRenderer);
    }
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if(!HeroUtil.isWearingWebShooter(MinecraftClient.getInstance().player)) return;
        boolean a = WebSwing.swingModeToggled;
        Identifier chargeBar;
        Identifier keybindingSlot;
        Identifier leftClick;
        Identifier rightClick;
        if(a) {
            ScreenUtils.drawScreen(drawContext, 10, 10, FecfsKeyInputHandler.abilityOne, "Toggle Swing");

            if (MinecraftClient.getInstance().options.useKey.isPressed()) {
                drawContext.getMatrices().push();
                drawContext.getMatrices().scale(1.05f, 1.05f, 1.05f);
                rightClick = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/right_click_pressed_gui.png");
                drawContext.drawTexture(rightClick, 32, 32, 0, 0, 16, 16, 16, 16);
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, "Shoot web", 50, 36, 0xFFFFFF, true);
                drawContext.getMatrices().pop();
            } else {
                drawContext.getMatrices().push();
                drawContext.getMatrices().scale(1.05f, 1.05f, 1.05f);
                rightClick = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/right_click_gui.png");
                drawContext.drawTexture(rightClick, 32, 32, 0, 0, 16, 16, 16, 16);
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, "Shoot web", 50, 36, 0xFFFFFF, true);
                drawContext.getMatrices().pop();
            }

            if (MinecraftClient.getInstance().options.attackKey.isPressed()) {
                drawContext.getMatrices().push();
                drawContext.getMatrices().scale(1.05f, 1.05f, 1.05f);
                leftClick = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/left_click_pressed_gui.png");
                drawContext.drawTexture(leftClick, 32, 52, 0, 0, 16, 16, 16, 16);
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, "Release web", 50, 56, 0xFFFFFF, true);
                drawContext.getMatrices().pop();
            } else {
                drawContext.getMatrices().push();
                drawContext.getMatrices().scale(1.05f, 1.05f, 1.05f);
                leftClick = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/left_click_gui.png");
                drawContext.drawTexture(leftClick, 32, 52, 0, 0, 16, 16, 16, 16);
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, "Release web", 50, 56, 0xFFFFFF, true);
                drawContext.getMatrices().pop();
            }

            ScreenUtils.drawScreen(drawContext, 10, 70, FecfsKeyInputHandler.abilityTwo, "Web-Zip");
            ScreenUtils.drawScreen(drawContext, 10, 94, FecfsKeyInputHandler.abilityThree, "Web Shoot");

        } else {
            ScreenUtils.drawScreen(drawContext, 10, 10, FecfsKeyInputHandler.abilityOne, "Toggle Swing");
            ScreenUtils.drawScreen(drawContext, 10, 34, FecfsKeyInputHandler.abilityTwo, "Web-Zip");
            ScreenUtils.drawScreen(drawContext, 10, 58, FecfsKeyInputHandler.abilityThree, "Web Shoot");
        }

    }
}
