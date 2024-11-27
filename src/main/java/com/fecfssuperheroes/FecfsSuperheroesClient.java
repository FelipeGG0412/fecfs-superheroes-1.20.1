package com.fecfssuperheroes;

import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.client.FecfsScreens;
import com.fecfssuperheroes.event.FecfsClientEvents;
import com.fecfssuperheroes.event.FecfsKeyInputHandler;
import com.fecfssuperheroes.power.FecfsPowers;
import com.fecfssuperheroes.power.custom.DoubleJump;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class FecfsSuperheroesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WebSwinging.register();
        FecfsKeyInputHandler.register();
        FecfsScreens.register();
        FecfsPowers.registerPowers();

        ClientTickEvents.END_CLIENT_TICK.register(WebSwinging::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(DoubleJump::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(FecfsClientEvents::animationTickEvents);
    }
}