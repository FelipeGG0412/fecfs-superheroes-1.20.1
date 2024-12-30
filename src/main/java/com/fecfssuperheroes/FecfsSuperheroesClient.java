package com.fecfssuperheroes;

import com.fecfssuperheroes.ability.Diving;
import com.fecfssuperheroes.ability.Evade;
import com.fecfssuperheroes.ability.WebSwing;
import com.fecfssuperheroes.ability.WebZip;
import com.fecfssuperheroes.client.FecfsScreens;
import com.fecfssuperheroes.client.model.WebHitModel;
import com.fecfssuperheroes.entity.FecfsEntities;
import com.fecfssuperheroes.entity.client.WebProjectileModel;
import com.fecfssuperheroes.entity.client.WebProjectileRenderer;
import com.fecfssuperheroes.entity.layer.FecfsModelLayers;
import com.fecfssuperheroes.event.FecfsClientEvents;
import com.fecfssuperheroes.event.FecfsKeyInputHandler;
import com.fecfssuperheroes.networking.FecfsNetworking;
import com.fecfssuperheroes.power.FecfsPowers;
import com.fecfssuperheroes.power.custom.DoubleJump;
import com.fecfssuperheroes.util.RendererUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class FecfsSuperheroesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_line"), "main"),
                RendererUtils.WebLineModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(new EntityModelLayer(new Identifier(FecfsSuperheroes.MOD_ID, "web_hit"), "main"),
                WebHitModel::getTexturedModelData);

        WebSwing.register();
        WebZip.register();
        FecfsKeyInputHandler.register();
        FecfsScreens.register();
        FecfsPowers.registerPowers();
        FecfsNetworking.registerS2CPackets();

        ClientTickEvents.END_CLIENT_TICK.register(WebSwing::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(DoubleJump::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(Diving::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(FecfsClientEvents::animationTickEvents);

        EntityModelLayerRegistry.registerModelLayer(FecfsModelLayers.WEB_PROJECTILE, WebProjectileModel::getTexturedModelData);

        EntityRendererRegistry.register(FecfsEntities.WEB_PROJECTILE_ENTITY, WebProjectileRenderer::new);
    }
}