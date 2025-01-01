package com.fecfssuperheroes.power.custom;

import com.fecfssuperheroes.power.Power;
import net.minecraft.entity.player.PlayerEntity;

public class FallResistance extends Power {
    public FallResistance(int amplifier) {
        super(amplifier);
    }
    public static int currentAmplifier = 0;

    @Override
    public void apply(PlayerEntity player) {
        if (player == null) return;

        if (!player.isOnGround() && player.getVelocity().y < 0) {
            float originalFallDistance = player.fallDistance;
            player.fallDistance = originalFallDistance * (1.0f - (this.getAmplifier() / 100.0f));
            currentAmplifier = this.getAmplifier();
        }
    }

    @Override
    public void remove(PlayerEntity player) {
       currentAmplifier = 0;
    }
}
