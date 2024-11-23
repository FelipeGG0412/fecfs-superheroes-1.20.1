package com.fecfssuperheroes.power.custom;

import com.fecfssuperheroes.power.Power;
import net.minecraft.entity.player.PlayerEntity;

public class FallResistance extends Power {
    public FallResistance(int amplifier) {
        super(amplifier);
    }

    @Override
    public void apply(PlayerEntity player) {
        if (player == null) return;
        if (player.fallDistance > 0) {
            player.fallDistance = player.fallDistance * (this.getAmplifier() / 100.0f);
        }
    }

    @Override
    public void remove(PlayerEntity player) {
    }
}