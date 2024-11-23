package com.fecfssuperheroes;

import com.fecfssuperheroes.event.FecfsEventHandler;
import com.fecfssuperheroes.item.FecfsItems;
import com.fecfssuperheroes.power.FecfsPowers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class FecfsSuperheroes implements ModInitializer {
	public static final String MOD_ID = "fecfs-superheroes";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		FecfsPowers.registerPowers();
		FecfsItems.registerItems();
		FecfsEventHandler.register();
		GeckoLib.initialize();

	}
}