package com.fecfssuperheroes.power.custom;

import com.fecfssuperheroes.power.Power;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class Resistance extends Power {

    public Resistance(int amplifier) {
        super(amplifier);
    }

    @Override
    public void apply(PlayerEntity player) {
    }

    @Override
    public void remove(PlayerEntity player) {
    }

    public double getResistancePercentage() {
        return this.getAmplifier();
    }
}