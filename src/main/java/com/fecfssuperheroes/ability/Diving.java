package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.util.FecfsAnimations;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Diving extends Ability {
    public static boolean isDiving = false;
    private static final int FALL_THRESHOLD = 10; // Adjust as necessary
    private static double startY = 0;
    private static boolean wasOnGround = true;
    private static double previousVelocityY = 0;

    public Diving(KeyBinding keyBinding) {
        super(keyBinding);
    }


    public static boolean canDive(PlayerEntity player) {
        return HeroUtil.isWearingSuit(player, FecfsTags.Items.SPIDERMAN)
                && !player.isOnGround()
                && !player.getAbilities().flying
                && player.isAlive()
                && !player.isTouchingWater()
                && !WebSwing.isSwinging
                && !WebZip.isZipping()
                && !WallCrawling.isCrawling;
    }

    public static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;
        PlayerEntity player = client.player;

        if(player.getVelocity() == null) return;


        double currentVelocityY = player.getVelocity().y;

        if (player.isOnGround() || WebSwing.isSwinging || WebZip.isZipping()) {
            wasOnGround = true;
            startY = player.getY();
            previousVelocityY = 0;

            if (isDiving) {
                stopDive(player);
            }
        } else {
            if (wasOnGround) {
                wasOnGround = false;
                startY = player.getY();
            }
            if (currentVelocityY < 0 && previousVelocityY >= 0) {
                startY = player.getY();
            }

            double fallDistance = startY - player.getY();

            if (!isDiving && fallDistance >= FALL_THRESHOLD && canDive(player)) {
                startDive(player);
            }

            if (isDiving) {
                if (!canDive(player)) {
                    stopDive(player);
                } else {
                    dive(player);
                }
            }
        }

        previousVelocityY = currentVelocityY;
    }

    public static void startDive(PlayerEntity player) {
        if (!canDive(player)) return;

        isDiving = true;
        if(isDiving) {
            FecfsAnimations.playDiveLoopAnimation(player);
        } else {
            FecfsAnimations.stopAnimation(player);
        }
    }

    public static void dive(PlayerEntity player) {
        if (!canDive(player)) {
            stopDive(player);
            return;
        }
        if(player.getVelocity() == null) return;

        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.add(0, -0.03, 0));
    }

    public static void stopDive(PlayerEntity player) {
        if (isDiving) {
            isDiving = false;
            FecfsAnimations.stopAnimation(player);
        }
    }
}
