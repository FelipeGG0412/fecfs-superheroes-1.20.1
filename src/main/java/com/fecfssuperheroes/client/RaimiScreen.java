package com.fecfssuperheroes.client;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.ability.ChargeJump;
import com.fecfssuperheroes.ability.WebSwing;
import com.fecfssuperheroes.ability.WebZip;
import com.fecfssuperheroes.event.FecfsKeyInputHandler;
import com.fecfssuperheroes.item.FecfsItems;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import com.fecfssuperheroes.util.ScreenUtils;
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
        ChargeJump chargeJump = new ChargeJump();
        Identifier chargeBar;
        Identifier leftClick;

//        if(MinecraftClient.getInstance().options.attackKey.isPressed()) {
//            leftClick = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/left_click_pressed_gui.png");
//            drawContext.drawTexture(leftClick, 10, 10, 0,0, 16, 16, 16, 16);
//        } else {
//            leftClick = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/left_click_gui.png");
//            drawContext.drawTexture(leftClick, 10, 10, 0,0, 16, 16, 16, 16);
//        }
//        if(MinecraftClient.getInstance().options.useKey.isPressed()) {
//            leftClick = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/right_click_pressed_gui.png");
//            drawContext.drawTexture(leftClick, 10, 40, 0,0, 16, 16, 16, 16);
//        } else {
//            leftClick = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/right_click_gui.png");
//            drawContext.drawTexture(leftClick, 10, 40, 0,0, 16, 16, 16, 16);
//        }


        float chargePercentage = ChargeJump.updateChargeBar(MinecraftClient.getInstance().player);
        if(!chargeJump.isCharging()) {
            chargeBar = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/charge_bar.png");
            drawContext.drawTexture(chargeBar, 615, 216, 0, 0, 10, 100, 10, 100);
        } else {
            chargeBar = new Identifier(FecfsSuperheroes.MOD_ID, "textures/gui/charge_bar_charging.png");
            drawContext.drawTexture(chargeBar, 615, 216, 0, 0, 10, 100, 10, 100);
        }

        int totalHeight = 94;
        int filledHeight = (int)(chargePercentage * totalHeight);
        int fillTop = 313 - filledHeight;
        drawContext.fill(618, fillTop, 622, 313, 0xFFFFC132);

    }
}
