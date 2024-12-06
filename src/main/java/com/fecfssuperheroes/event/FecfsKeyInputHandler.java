package com.fecfssuperheroes.event;

import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.ability.WebZip;
import com.fecfssuperheroes.entity.custom.WebProjectile;
import com.fecfssuperheroes.networking.FecfsNetworking;
import com.fecfssuperheroes.sound.FecfsSounds;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import com.fecfssuperheroes.util.RendererUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import org.lwjgl.glfw.GLFW;


public class FecfsKeyInputHandler {
    public static boolean playProjectile = false;
    public static boolean webSwingingKey = false;
    public static boolean webZipKey = false;

    public static final String KEY_CATEGORY_ABILITIES = "key.category.abilities";

    public static final String KEY_ABILITY_ONE = "key.fecfs-superheroes.abilityone";
    public static final String KEY_ABILITY_TWO = "key.fecfs-superheroes.abilitytwo";
    public static final String KEY_ABILITY_THREE = "key.fecfs-superheroes.abilitythree";

    public static int webCooldown = 0;

    public static KeyBinding abilityOne;
    public static KeyBinding abilityTwo;
    public static KeyBinding abilityThree;

    public static String getKeyAbilityOne() {
        if(abilityOne != null) {
            return abilityOne.getBoundKeyLocalizedText().getString();
        } else {
            return "";
        }
    }
    public static String getKeyAbilityTwo() {
        if(abilityOne != null) {
            return abilityTwo.getBoundKeyLocalizedText().getString();
        } else {
            return "";
        }
    }
    public static String getKeyAbilityThree() {
        if(abilityOne != null) {
            return abilityThree.getBoundKeyLocalizedText().getString();
        } else {
            return "";
        }
    }

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
                GLFW.GLFW_KEY_V,
                KEY_CATEGORY_ABILITIES
        ));
        abilityThree = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ABILITY_THREE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KEY_CATEGORY_ABILITIES
        ));

        registerKeyInputs();
    }
    private static void spiderManAbilities(PlayerEntity player) {
        if(abilityOne.wasPressed() && (HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER) || HeroUtil.isWearingWebShooter(player))) {
            WebSwinging.startSwing(player);
            ClientPlayNetworking.send(FecfsNetworking.WEB_SOUND, PacketByteBufs.create());
            webCooldown = HeroUtil.isWearingWebShooter(player) ? 300 : 80;
            webSwingingKey = true;

        }
        if( MinecraftClient.getInstance().mouse.wasLeftButtonClicked() && (HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER)
                || HeroUtil.isWearingWebShooter(player))) {
            WebSwinging.boost(player);
            WebSwinging.stopSwinging(player);
        }
        if(abilityTwo.wasPressed() && (HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER) || HeroUtil.isWearingWebShooter(player))) {
            WebZip.startZip(player);
            webZipKey = true;
        }
        if(abilityThree.wasPressed() &&  (HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER) || HeroUtil.isWearingWebShooter(player))) {
            playProjectile = true;
            ClientPlayNetworking.send(FecfsNetworking.ABILITY_THREE_SPIDERMAN, PacketByteBufs.create());
            player.getWorld().playSound(null,
                    player.getX(), player.getY(), player.getZ(), FecfsSounds.WEB_PROJECTILE, SoundCategory.PLAYERS, 1f, 1f);
        }
    }
}
