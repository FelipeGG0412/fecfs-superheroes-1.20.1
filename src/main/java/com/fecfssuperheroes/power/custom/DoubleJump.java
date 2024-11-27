package com.fecfssuperheroes.power.custom;

import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.power.Power;
import com.fecfssuperheroes.util.FecfsAnimations;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class DoubleJump extends Power {
    public DoubleJump(int amplifier) {
        super(amplifier);
    }

    @Override
    public void apply(PlayerEntity player) {}

    @Override
    public void remove(PlayerEntity player) {}

    public static boolean hasDoubleJumped = false;
    public static boolean canDoubleJump = false;
    public static boolean canPlayAnimation = false;

    public static void onTick(PlayerEntity player) {
        if (player == null) return;


        if (player.isOnGround()) {
            hasDoubleJumped = false;
            canDoubleJump = false;
        }
        if (!player.isOnGround() && player.getVelocity().y < 0 && !WebSwinging.isSwinging && !player.getAbilities().flying) {
            canDoubleJump = true;
        }
    }

    public static void doubleJump(PlayerEntity player) {
        if (player == null) return;

        if (canDoubleJump && !hasDoubleJumped && MinecraftClient.getInstance().options.jumpKey.isPressed()) {
            player.setVelocity(player.getVelocity().x, .75, player.getVelocity().z);
            canPlayAnimation = true;
            hasDoubleJumped = true;
            canDoubleJump = false;
        }
    }
    public static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;
        if(!HeroUtil.isWearingSuit(client.player, FecfsTags.Items.WEB_SLINGER)) return;

        onTick(client.player);
        doubleJump(client.player);
        if(canPlayAnimation) {
            FecfsAnimations.playSpiderManDoubleJumpAnimation(client.player);
            canPlayAnimation = false;
        }
    }
}
