package com.fecfssuperheroes.event;

import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.ability.WebZip;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;


public class FecfsKeyInputHandler {
    public static final String KEY_CATEGORY_ABILITIES = "key.category.abilities";

    public static final String KEY_ABILITY_ONE = "key.fecfs-superheroes.abilityone";
    public static final String KEY_ABILITY_TWO = "key.fecfs-superheroes.abilitytwo";

    public static int webCooldown = 0;

    public static KeyBinding abilityOne;
    public static KeyBinding abilityTwo;

    public static void registerKeyInputs() {
       ClientTickEvents.END_CLIENT_TICK.register(client -> {
           PlayerEntity player = MinecraftClient.getInstance().player;
           if(player == null) return;
           spiderManAbilities(player);
       });
    }

    public static void register() {
        abilityOne = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ABILITY_ONE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY_ABILITIES
        ));
        abilityTwo = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ABILITY_TWO,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KEY_CATEGORY_ABILITIES
        ));

        registerKeyInputs();
    }
    private static void spiderManAbilities(PlayerEntity player) {
        if(abilityOne.wasPressed() && (HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER) || HeroUtil.isWearingWebShooter(player))) {
            WebSwinging.startSwing(player);
            webCooldown = HeroUtil.isWearingWebShooter(player) ? 300 : 80;

        }
        if(MinecraftClient.getInstance().options.jumpKey.wasPressed() && (HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER)
                || HeroUtil.isWearingWebShooter(player))) {
            WebSwinging.boost(player);
            WebSwinging.stopSwinging(player);
        }
        if(abilityTwo.wasPressed() && (HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER) || HeroUtil.isWearingWebShooter(player))) {
            WebZip.startZip(player);
        }
    }
}
