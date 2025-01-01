package com.fecfssuperheroes.power;

import net.minecraft.entity.player.PlayerEntity;

public abstract class Power {
    protected int amplifier;

    public Power(int amplifier) {
        this.amplifier = amplifier;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public abstract void apply(PlayerEntity player);
    public abstract void remove(PlayerEntity player);

    public Power(Power power) {
        this.amplifier = power.amplifier;
    }
}
