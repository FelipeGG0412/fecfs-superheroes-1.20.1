package com.fecfssuperheroes.client;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.event.FecfsKeyInputHandler;
import com.fecfssuperheroes.item.FecfsItems;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;

public class RaimiScreen extends InGameHud implements HudRenderCallback {
    public RaimiScreen(MinecraftClient client, ItemRenderer itemRenderer) {
        super(client, itemRenderer);
    }
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if (!HeroUtil.isWearingSuit(MinecraftClient.getInstance().player, FecfsTags.Items.SPIDERMAN)) return;

        Identifier abilityTemplate = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/ability_template.png");
        Identifier abilityExtender = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/ability_extender.png");
        Identifier abilityPressed = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/ability_pressed.png");

        drawContext.drawTexture(abilityExtender, 37, 15, 24, 24, 16, 16, 16, 16,
                16, 16);

        if(FecfsKeyInputHandler.abilityOne.isPressed()) {
            drawContext.drawTexture(abilityPressed, 15, 15, 24, 24, 16, 16, 16, 16,
                    16, 16);
            drawContext.drawText(this.getTextRenderer(), "R", 25, 23, 0xFFFFFF, false);

        } else {
            drawContext.drawTexture(abilityTemplate, 15, 15, 24, 24, 16, 16, 16, 16,
                    16, 16);

            drawContext.getMatrices().push();
            drawContext.getMatrices().scale(1.225f, 1.225f, 1.0f);
            drawContext.drawText(this.getTextRenderer(), "R", 20, 18, 0XFFFFFF,true);
            drawContext.getMatrices().pop();
        }
        drawContext.drawItem(FecfsItems.WEB_SHOOTERS.getDefaultStack(), 42, 18);
    }
}
