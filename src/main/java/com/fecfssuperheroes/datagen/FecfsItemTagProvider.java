package com.fecfssuperheroes.datagen;

import com.fecfssuperheroes.item.FecfsItems;
import com.fecfssuperheroes.util.FecfsTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FecfsItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public FecfsItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(FecfsTags.Items.WEB_SLINGER)
                .add(FecfsItems.SMSR_BOOTS)
                .add(FecfsItems.SMSR_LEGGINGS)
                .add(FecfsItems.SMSR_CHESTPLATE)
                .add(FecfsItems.SMSR_HELMET);
        getOrCreateTagBuilder(FecfsTags.Items.HERO)
                .add(FecfsItems.SMSR_BOOTS)
                .add(FecfsItems.SMSR_LEGGINGS)
                .add(FecfsItems.SMSR_CHESTPLATE)
                .add(FecfsItems.SMSR_HELMET);
        getOrCreateTagBuilder(FecfsTags.Items.FULLSUIT)
                .add(FecfsItems.SMSR_BOOTS)
                .add(FecfsItems.SMSR_LEGGINGS)
                .add(FecfsItems.SMSR_CHESTPLATE)
                .add(FecfsItems.SMSR_HELMET);
        getOrCreateTagBuilder(FecfsTags.Items.WALL_CRAWLER)
                .add(FecfsItems.SMSR_BOOTS)
                .add(FecfsItems.SMSR_LEGGINGS)
                .add(FecfsItems.SMSR_CHESTPLATE)
                .add(FecfsItems.SMSR_HELMET);
        getOrCreateTagBuilder(FecfsTags.Items.SPIDERMAN)
                .add(FecfsItems.SMSR_BOOTS)
                .add(FecfsItems.SMSR_LEGGINGS)
                .add(FecfsItems.SMSR_CHESTPLATE)
                .add(FecfsItems.SMSR_HELMET);
        getOrCreateTagBuilder(FecfsTags.Items.RAIMI)
                .add(FecfsItems.SMSR_BOOTS)
                .add(FecfsItems.SMSR_LEGGINGS)
                .add(FecfsItems.SMSR_CHESTPLATE)
                .add(FecfsItems.SMSR_HELMET);
    }
}
