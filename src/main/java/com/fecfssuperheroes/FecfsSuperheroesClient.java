package com.fecfssuperheroes;

import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.ability.WebZip;
import com.fecfssuperheroes.client.FecfsScreens;
import com.fecfssuperheroes.event.FecfsClientEvents;
import com.fecfssuperheroes.event.FecfsKeyInputHandler;
import com.fecfssuperheroes.power.FecfsPowers;
import com.fecfssuperheroes.power.custom.DoubleJump;
import com.fecfssuperheroes.util.WebRendererUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class FecfsSuperheroesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_line"), "main"),
                WebRendererUtil.WebLineModel::getTexturedModelData);
        WebSwinging.register();
        WebZip.register();
        FecfsKeyInputHandler.register();
        FecfsScreens.register();
        FecfsPowers.registerPowers();

        ClientTickEvents.END_CLIENT_TICK.register(WebSwinging::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(DoubleJump::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(FecfsClientEvents::animationTickEvents);
    }
}