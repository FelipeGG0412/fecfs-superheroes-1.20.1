package com.fecfssuperheroes.item;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.item.custom.SMSRArmorItem;
import com.fecfssuperheroes.item.custom.WebShootersArmorItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class FecfsItems {
    public static final Item SMSR_HELMET = registerItem("smsr_helmet", new SMSRArmorItem(FecfsArmorMaterials.SMSR, ArmorItem.Type.HELMET,
            new FabricItemSettings()));
    public static final Item SMSR_CHESTPLATE = registerItem("smsr_chestplate", new SMSRArmorItem(FecfsArmorMaterials.SMSR, ArmorItem.Type.CHESTPLATE,
            new FabricItemSettings()));
    public static final Item SMSR_LEGGINGS = registerItem("smsr_leggings", new SMSRArmorItem(FecfsArmorMaterials.SMSR, ArmorItem.Type.LEGGINGS,
            new FabricItemSettings()));
    public static final Item SMSR_BOOTS = registerItem("smsr_boots", new SMSRArmorItem(FecfsArmorMaterials.SMSR, ArmorItem.Type.BOOTS,
            new FabricItemSettings()));

    public static Item WEB_SHOOTERS = registerItem("web_shooters", new WebShootersArmorItem(FecfsArmorMaterials.WEB_SHOOTERS, ArmorItem.Type.CHESTPLATE,
            new FabricItemSettings()));

    public static Item WEB_ZIP_ICON = registerItem("web_zip_icon", new Item(new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(FecfsSuperheroes.MOD_ID, name), item);
    }
    private static void itemGroupCombat(FabricItemGroupEntries entries) {
        entries.add(SMSR_BOOTS);
        entries.add(SMSR_LEGGINGS);
        entries.add(SMSR_CHESTPLATE);
        entries.add(SMSR_HELMET);
        entries.add(WEB_SHOOTERS);
    }
    public static void registerItems() {
        FecfsSuperheroes.LOGGER.info("Item registers for " + FecfsSuperheroes.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(FecfsItems::itemGroupCombat);
    }
}
