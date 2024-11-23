package com.fecfssuperheroes.util;

import com.fecfssuperheroes.power.Power;
import com.fecfssuperheroes.power.custom.Resistance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerUtil {
    private static final Map<TagKey<Item>, List<Power>> ARMOR_POWERS = new HashMap<>();
    private static final Map<PlayerEntity, Resistance> ACTIVE_RESISTANCE = new HashMap<>();

    public static void registerArmorPowers(TagKey<Item> tag, List<Power> powers) {
        ARMOR_POWERS.put(tag, powers);
    }

    public static void handle(PlayerEntity player) {
        if(player == null) return;
        for (Map.Entry<TagKey<Item>, List<Power>> entry : ARMOR_POWERS.entrySet()) {
            TagKey<Item> armorTag = entry.getKey();
             List<Power> powers = entry.getValue();
            if (HeroUtil.isWearingSuit(player, armorTag)) {
                applyPowers(player, powers);
            } else {
                removePowers(player, powers);
            }
        }
    }


    private static void applyPowers(PlayerEntity player, List<Power> powers) {
        for (Power power : powers) {
            power.apply(player);
            if(power instanceof Resistance) {
                ACTIVE_RESISTANCE.put(player, ((Resistance) power));
            }
        }
    }

    private static void removePowers(PlayerEntity player, List<Power> powers) {
        for (Power power : powers) {
            power.remove(player);
            if(power instanceof Resistance) {
                ACTIVE_RESISTANCE.remove(player);
            }
        }
    }
    public static double getResistancePercentage(PlayerEntity player) {
        Resistance resistanceAbility = ACTIVE_RESISTANCE.get(player);
        return resistanceAbility != null ? resistanceAbility.getResistancePercentage() : 0.0;
    }
}
