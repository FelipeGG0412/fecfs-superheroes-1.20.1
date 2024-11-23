package com.fecfssuperheroes.datagen;

import com.fecfssuperheroes.item.FecfsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class FecfsModelProvider extends FabricModelProvider {
    public FecfsModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(FecfsItems.SMSR_HELMET, Models.GENERATED);
        itemModelGenerator.register(FecfsItems.SMSR_CHESTPLATE, Models.GENERATED);
        itemModelGenerator.register(FecfsItems.SMSR_LEGGINGS, Models.GENERATED);
        itemModelGenerator.register(FecfsItems.SMSR_BOOTS, Models.GENERATED);
    }
}
