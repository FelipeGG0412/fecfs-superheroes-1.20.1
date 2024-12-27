package com.fecfssuperheroes.event;

import com.eliotlash.mclib.math.functions.limit.Min;
import com.fecfssuperheroes.ability.Evade;
import com.fecfssuperheroes.ability.WebSwing;
import com.fecfssuperheroes.ability.WebZip;
import com.fecfssuperheroes.networking.FecfsNetworking;
import com.fecfssuperheroes.sound.FecfsSounds;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
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
    private static boolean wasUseKeyPressed = false;
    private static boolean wasAttackKeyPressed = false;


    public static final String KEY_CATEGORY_ABILITIES = "key.category.abilities";

    public static final String KEY_ABILITY_ONE = "key.fecfs-superheroes.abilityone";
    public static final String KEY_ABILITY_TWO = "key.fecfs-superheroes.abilitytwo";
    public static final String KEY_ABILITY_THREE = "key.fecfs-superheroes.abilitythree";
    public static final String KEY_EVADE = "key.fecfs-superheroes.abilityevade";

    public static int webCooldown = 0;

    public static KeyBinding abilityOne;
    public static KeyBinding abilityTwo;
    public static KeyBinding abilityThree;
    public static KeyBinding evade;

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
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if(player == null) return;
            spiderManAbilities(player, minecraftClient);
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
        evade = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_EVADE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                KEY_CATEGORY_ABILITIES
        ));

        registerKeyInputs();
    }

    private static void spiderManAbilities(PlayerEntity player, MinecraftClient client) {
        boolean hasWebGear = HeroUtil.isWearingSuit(player, FecfsTags.Items.WEB_SLINGER)
                || HeroUtil.isWearingWebShooter(player);
        if (!hasWebGear) return;
        if (abilityOne.wasPressed()) {
            WebSwing.swingModeToggled = !WebSwing.swingModeToggled;
        }
        boolean useKeyPressed = client.options.useKey.isPressed();
        if (WebSwing.swingModeToggled) {
            if (useKeyPressed && !wasUseKeyPressed) {
                WebSwing.startSwing(player);
                webCooldown = HeroUtil.isWearingWebShooter(player) ? 300 : 80;
                webSwingingKey = true;
            }
            wasUseKeyPressed = useKeyPressed;
            boolean attackKeyPressed = client.options.attackKey.isPressed();
            if (WebSwing.isSwinging && attackKeyPressed && !wasAttackKeyPressed) {
                WebSwing.boost(player);
                WebSwing.stopSwinging(player);
            }
            wasAttackKeyPressed = attackKeyPressed;
        }
        if (abilityTwo.wasPressed()) {
            WebZip.startZip(player);
            webZipKey = true;
        }
        if (abilityThree.wasPressed()) {
            playProjectile = true;
            ClientPlayNetworking.send(FecfsNetworking.ABILITY_THREE_SPIDERMAN, PacketByteBufs.create());
        }
        if(evade.wasPressed()) {
            Evade.performEvade(player);
        }
    }


}
