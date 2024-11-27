package com.fecfssuperheroes.util;

import com.fecfssuperheroes.item.FecfsItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

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
    public static boolean isWearingWebShooter(PlayerEntity player) {
        if(player != null) {
            TagKey<Item> tag = FecfsTags.Items.FULLSUIT;
            ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
            return !helmet.isIn(tag) && chestplate.isOf(FecfsItems.WEB_SHOOTERS) && !leggings.isIn(tag) && !boots.isIn(tag);
        }
        return false;
    }
    public static boolean isSlotInTag(PlayerEntity player, EquipmentSlot slot, TagKey<Item> tag) {
        return player.getEquippedStack(slot).isIn(tag);
    }
}
