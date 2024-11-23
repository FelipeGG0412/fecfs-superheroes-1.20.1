package com.fecfssuperheroes.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HeroUtil {
    public static boolean canPlayJumpAnimation = false;

    public static boolean isWearingSuit(PlayerEntity player, TagKey<Item> tag) {
        if (player != null && player.getInventory().armor.toArray().length == 4) {
            ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
            return helmet.isIn(tag) && chestplate.isIn(tag) && leggings.isIn(tag) && boots.isIn(tag);
        }
        return false;
    }
    public static boolean isSlotInTag(PlayerEntity player, EquipmentSlot slot, TagKey<Item> tag) {
        return player.getEquippedStack(slot).isIn(tag);
    }
}
