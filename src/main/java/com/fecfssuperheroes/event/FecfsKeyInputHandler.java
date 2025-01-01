package com.fecfssuperheroes.event;

import com.fecfssuperheroes.ability.Ability;
import com.fecfssuperheroes.hero.FecfsHeroes;
import com.fecfssuperheroes.hero.Hero;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class FecfsKeyInputHandler {
    public static boolean playProjectile = false;
    public static boolean webSwingingKey = false;
    public static boolean webZipKey = false;


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

    public static String getAbilityKey(KeyBinding binding) {
        if(binding != null) {
            return binding.getBoundKeyLocalizedText().getString();
        } else {
            return "";
        }
    }

    private static Map<KeyBinding, Boolean> previousKeyStates = new HashMap<>();
    private static Set<KeyBinding> keyBindings;

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

        keyBindings = Set.of(abilityOne, abilityTwo, abilityThree, evade);

        registerKeyInputs();
    }

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = client.player;
            if (player == null) return;

            Hero hero = FecfsHeroes.getCurrentHero(player);
            if (hero == null) return;

            Map<KeyBinding, Ability> heroAbilities = hero.getAbilities();
            for (Map.Entry<KeyBinding, Ability> entry : heroAbilities.entrySet()) {
                KeyBinding keyBinding = entry.getKey();
                Ability ability = entry.getValue();

                boolean isKeyPressed = keyBinding.isPressed();
                boolean wasKeyPressed = previousKeyStates.getOrDefault(keyBinding, false);

                if (isKeyPressed && !wasKeyPressed) {
                    ability.onKeyPressed(player);
                }
                if (!isKeyPressed && wasKeyPressed) {
                    ability.onKeyReleased(player);
                }
                previousKeyStates.put(keyBinding, isKeyPressed);
            }
        });
    }


}
