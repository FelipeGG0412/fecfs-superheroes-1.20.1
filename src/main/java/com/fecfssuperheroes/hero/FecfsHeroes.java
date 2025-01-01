package com.fecfssuperheroes.hero;

import com.fecfssuperheroes.ability.*;
import com.fecfssuperheroes.event.FecfsKeyInputHandler;
import com.fecfssuperheroes.power.custom.*;
import com.fecfssuperheroes.util.FecfsTags;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class FecfsHeroes {
    private static final Map<String, Hero> HEROES = new HashMap<>();

    public static void registerHeroes() {
        TagKey<Item> spiderManTag = FecfsTags.Items.SPIDERMAN;
        TagKey<Item> raimiTag = FecfsTags.Items.RAIMI;
        TagKey<Item> webSlinger = FecfsTags.Items.WEB_SLINGER;
        TagKey<Item> hero = FecfsTags.Items.HERO;
        TagKey<Item> wallCrawler = FecfsTags.Items.WALL_CRAWLER;

        Ability webSwing = new WebSwing(FecfsKeyInputHandler.abilityOne);
        Ability webZip = new WebZip(FecfsKeyInputHandler.abilityTwo);
        Ability webShoot = new WebShoot(FecfsKeyInputHandler.abilityThree);
        Ability evade = new Evade(FecfsKeyInputHandler.evade);





        Hero spiderManRaimi = Hero.create()
                .name("Spider-Man Raimi")
                .nameKey("smsr")
                .addArmorTag(spiderManTag)
                .addArmorTag(raimiTag)
                .addArmorTag(webSlinger)
                .addArmorTag(hero)
                .addArmorTag(wallCrawler)
                .addPower("Strength", new Strength(7))
                .addPower("Speed", new Speed(4))
                .addPower("Jump", new Jump(3))
                .addPower("Double-Jump", new DoubleJump(1))
                .addPower("Fall resistance", new FallResistance(50))
                .addPower("Resistance", new Resistance(60))
                .setAbility(FecfsKeyInputHandler.abilityOne, webSwing)
                .setAbility(FecfsKeyInputHandler.abilityTwo, webZip)
                .setAbility(FecfsKeyInputHandler.abilityThree, webShoot)
                .setAbility(FecfsKeyInputHandler.evade, evade)
                .setTier(6)
                .build();
        HEROES.put(spiderManRaimi.getName(), spiderManRaimi);





    }
    public static void onClientTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = client.player;
            if (player == null) return;

            Hero currentHero = getCurrentHero(player);

            if (currentHero != null) {
                currentHero.applyPowers(player);
                currentHero.updateAbilities(player);
            } else {
                for (Hero hero : HEROES.values()) {
                    hero.removePowers(player);
                }
            }
        });
    }

    public static Hero getCurrentHero(PlayerEntity player) {
        for (Hero hero : HEROES.values()) {
            if (hero.isWearingFullSet(player)) {
                return hero;
            }
        }
        return null;
    }



    public static void registerServerTick() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player == null) continue;

                Hero currentHero = getCurrentHero(player);

                if (currentHero != null) {
                    currentHero.applyPowers(player);
                } else {
                    for (Hero hero : HEROES.values()) {
                        hero.removePowers(player);
                    }
                }
            }
        });
    }
}
