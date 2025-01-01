package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.networking.FecfsNetworking;
import com.fecfssuperheroes.util.FecfsAnimations;
import com.fecfssuperheroes.util.HeroUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;

public class WebShoot extends Ability {
    private int cooldownTicks = 0;
    private final int maxCooldownTicks = 8;
    private boolean hasPlayedAnimation = false;

    public WebShoot(KeyBinding keyBinding) {
        super(keyBinding);
    }

    @Override
    public void start(PlayerEntity player) {
        if (cooldownTicks > 0) {
            return;
        }
        if (!hasPlayedAnimation) {
            hasPlayedAnimation = true;
            FecfsAnimations.playWebShootAnimation(player);
            HeroUtil.tickAction(8, p -> ClientPlayNetworking.send(FecfsNetworking.ABILITY_THREE_SPIDERMAN, PacketByteBufs.create()));
        } else {
            hasPlayedAnimation = false;
        }
        cooldownTicks = maxCooldownTicks;
    }

    @Override
    public void update(PlayerEntity player) {
        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
    }

}
