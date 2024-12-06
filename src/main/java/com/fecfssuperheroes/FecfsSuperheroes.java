package com.fecfssuperheroes;

import com.fecfssuperheroes.entity.FecfsEntities;
import com.fecfssuperheroes.event.FecfsEventHandler;
import com.fecfssuperheroes.item.FecfsItems;
import com.fecfssuperheroes.networking.FecfsNetworking;
import com.fecfssuperheroes.sound.FecfsSounds;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class FecfsSuperheroes implements ModInitializer {
	public static final String MOD_ID = "fecfs-superheroes";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		FecfsItems.registerItems();
		FecfsEntities.registerEntities();
		FecfsEventHandler.register();
		FecfsNetworking.registerC2SPackets();
		FecfsSounds.registerSounds();

		GeckoLib.initialize();

	}
}