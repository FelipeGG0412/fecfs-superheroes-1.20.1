package com.fecfssuperheroes.power.custom;

import com.fecfssuperheroes.ability.WebSwing;
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
        if (!player.isOnGround() && player.getVelocity().y < 0 && !WebSwing.isSwinging && !player.getAbilities().flying) {
            canDoubleJump = true;
        }
    }

    public static void doubleJump(PlayerEntity player) {
        if (canDoubleJump && !hasDoubleJumped) {
            player.setVelocity(player.getVelocity().x, 0.75, player.getVelocity().z);
            canPlayAnimation = true;
            hasDoubleJumped = true;
            canDoubleJump = false;
        }
    }

    private static boolean wasJumpKeyPressed = false; // Keep track of the key's previous state

    public static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;
        if (!HeroUtil.isWearingSuit(client.player, FecfsTags.Items.WEB_SLINGER)) return;

        onTick(client.player);

        boolean isJumpKeyPressed = client.options.jumpKey.isPressed();
        if (isJumpKeyPressed && !wasJumpKeyPressed) { // Detect key press event
            doubleJump(client.player);
        }
        wasJumpKeyPressed = isJumpKeyPressed;

        if (canPlayAnimation) {
            FecfsAnimations.playSpiderManDoubleJumpAnimation(client.player);
            canPlayAnimation = false;
        }
    }

}
