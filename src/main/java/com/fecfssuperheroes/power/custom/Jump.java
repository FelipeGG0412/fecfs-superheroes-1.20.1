package com.fecfssuperheroes.power.custom;

import com.fecfssuperheroes.power.Power;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Jump extends Power {
    public static boolean jump = false;
    private static int currentAmplifier = 0;
    public static float aFloat = 0;

    public Jump(int amplifier) {
        super(amplifier);
    }
    public static int getCurrentAmplifier() {
        return currentAmplifier;
    }
    @Override
    public void apply(PlayerEntity player) {
        if(HeroUtil.isWearingSuit(player, FecfsTags.Items.FULLSUIT)) return;
        jump = true;
        currentAmplifier = this.getAmplifier() + Math.round(aFloat / 1.5f);
    }

    @Override
    public void remove(PlayerEntity player) {
        jump = false;
        currentAmplifier = 0;
    }
}