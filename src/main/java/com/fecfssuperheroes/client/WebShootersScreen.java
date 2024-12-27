package com.fecfssuperheroes.client;

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

public class WebShootersScreen extends InGameHud implements HudRenderCallback {
    public WebShootersScreen(MinecraftClient client, ItemRenderer itemRenderer) {
        super(client, itemRenderer);
    }
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if(!HeroUtil.isWearingWebShooter(MinecraftClient.getInstance().player)) return;
        ScreenUtils.renderAbilities(drawContext, 10, 10, 24, 24, FecfsKeyInputHandler.getKeyAbilityOne(),
                FecfsItems.WEB_SHOOTERS, FecfsKeyInputHandler.abilityOne, WebSwing.isSwingOnCooldown());
        ScreenUtils.renderAbilities(drawContext, 10, 36, 24, 24, FecfsKeyInputHandler.getKeyAbilityTwo(),
                FecfsItems.WEB_ZIP_ICON, FecfsKeyInputHandler.abilityTwo, WebZip.isWebZipOnCooldown());
        ScreenUtils.renderAbilities(drawContext, 10, 62, 24, 24, FecfsKeyInputHandler.getKeyAbilityThree(),
                FecfsItems.WEB_SHOOTERS, FecfsKeyInputHandler.abilityThree, false);

    }
}
