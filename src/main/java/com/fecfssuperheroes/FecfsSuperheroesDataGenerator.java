package com.fecfssuperheroes;

import com.fecfssuperheroes.datagen.FecfsItemTagProvider;
import com.fecfssuperheroes.datagen.FecfsModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FecfsSuperheroesDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(FecfsModelProvider::new);
		pack.addProvider(FecfsItemTagProvider::new);
	}
}
