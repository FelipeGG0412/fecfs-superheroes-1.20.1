package com.fecfssuperheroes.power;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.power.custom.*;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.PowerUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FecfsPowers {
    private static final Map<String, Power> registeredPowers = new HashMap<>();
    public static void registerPower(String name, Power power) {
        registeredPowers.put(name, power);
    }
    public static void registerPowers() {
        FecfsSuperheroes.LOGGER.info("Power registers for " + FecfsSuperheroes.MOD_ID);

        registerPower("strength", new Strength(1));

        TagKey<Item> spiderManTag = FecfsTags.Items.SPIDERMAN;

        List<Power> spiderManPowers = spiderManPowers();

        PowerUtil.registerArmorPowers(spiderManTag, spiderManPowers);
    }
    public static List<Power> spiderManPowers() {
        return List.of(
                new Strength(2),
                new Speed(3),
                new Jump(3),
                new DoubleJump(1),

                new FallResistance(75),
                new Resistance(60)
        );
    }
}
