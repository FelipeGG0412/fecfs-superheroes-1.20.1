package com.fecfssuperheroes.ability;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;

public abstract class Ability {
    private final KeyBinding keyBinding;
    private boolean isActive = false;

    public Ability(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public void onKeyPressed(PlayerEntity player) {
        if (!isActive) {
            start(player);
            isActive = true;
        }
    }

    public void onKeyReleased(PlayerEntity player) {
        if (isActive) {
            stop(player);
            isActive = false;
        }
    }

    public void onUpdate(PlayerEntity player) {
        if (isActive) {
            update(player);
        }
    }

    public void start(PlayerEntity player) {}
    public void update(PlayerEntity player) {}
    public void stop(PlayerEntity player) {}
}
