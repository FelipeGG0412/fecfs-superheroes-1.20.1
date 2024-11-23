package com.fecfssuperheroes;

import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.event.FecfsClientEvents;
import com.fecfssuperheroes.event.FecfsKeyInputHandler;
import com.fecfssuperheroes.power.custom.DoubleJump;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class FecfsSuperheroesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FecfsKeyInputHandler.register();

        ClientTickEvents.END_CLIENT_TICK.register(WebSwinging::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(DoubleJump::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(FecfsClientEvents::animationTickEvents);

        new WebSwinging();
        WebSwinging.register();
    }
}