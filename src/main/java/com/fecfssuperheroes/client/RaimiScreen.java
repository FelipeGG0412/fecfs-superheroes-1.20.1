package com.fecfssuperheroes.client;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.ability.WebZip;
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

        Identifier chargeBar = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/charge_bar.png");


        FecfsScreens.renderCooldown(drawContext, WebSwinging.isSwingOnCooldown(), 24, 24, 15,15);
        FecfsScreens.renderHud(drawContext, 5, 4, 24, 24, 15, 15, FecfsKeyInputHandler.abilityOne,
                this.getTextRenderer(), "R");
        FecfsScreens.drawItem(drawContext, 15, 15, FecfsItems.WEB_SHOOTERS.getDefaultStack());



        FecfsScreens.renderCooldown(drawContext, WebZip.isWebZipOnCooldown(), 24, 24, 15,41);
        FecfsScreens.renderHud(drawContext, 5, -1, 24, 24, 15, 41, FecfsKeyInputHandler.abilityTwo,
                this.getTextRenderer(), "G");
        FecfsScreens.drawItem(drawContext, 15, 41, FecfsItems.WEB_SHOOTERS.getDefaultStack());



        drawContext.drawTexture(chargeBar, 615, 215, 10, 100, 10, 100, 10, 100,
                10, 100);

    }
}
