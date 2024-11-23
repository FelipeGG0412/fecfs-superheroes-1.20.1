package com.fecfssuperheroes.util;

import com.fecfssuperheroes.FecfsSuperheroes;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class FecfsTags {
    public static class Blocks {
        private static TagKey<Block> createBlockTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(FecfsSuperheroes.MOD_ID, name));
        }
        private static TagKey<Block> createCommonBlockTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier("c", name));
        }
    }
    public static class Items {
        public static final TagKey<Item> WALL_CRAWLER = createCommonItemTag("wall_crawler");
        public static final TagKey<Item> WEB_SLINGER = createItemTag("web_slinger");
        public static final TagKey<Item> HERO = createItemTag("hero");
        public static final TagKey<Item> FULLSUIT = createItemTag("fullsuit");
        public static final TagKey<Item> SPIDERMAN = createItemTag("spiderman");


        private static TagKey<Item> createItemTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(FecfsSuperheroes.MOD_ID, name));
        }
        private static TagKey<Item> createCommonItemTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier("c", name));
        }
    }
}
