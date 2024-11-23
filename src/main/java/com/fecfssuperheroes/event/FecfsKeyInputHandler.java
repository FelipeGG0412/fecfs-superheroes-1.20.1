package com.fecfssuperheroes.event;

import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.util.FecfsAnimations;
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

    public static KeyBinding abilityOne;

    public static void registerKeyInputs() {
       ClientTickEvents.END_CLIENT_TICK.register(client -> {
           PlayerEntity player = MinecraftClient.getInstance().player;
           if(player == null) return;
           if(abilityOne.wasPressed()) {
               WebSwinging.startSwing(player);
           }
           if(MinecraftClient.getInstance().options.jumpKey.wasPressed()) {
               WebSwinging.boost(player);
               WebSwinging.stopSwinging(player);
           }
       });
    }

    public static void register() {
        abilityOne = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ABILITY_ONE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY_ABILITIES
        ));
        registerKeyInputs();
    }
}
