package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class ChargeJump {
    public static final int maxChargeTime(PlayerEntity player) {
        if(player == null) return 0;
        else {
            return MinecraftClient.getInstance().player.isSprinting() || WebSwing.isSwinging ? 30 : 50;
        }
    }
    public static final int MIN_CHARGE_TIME = 2;
    private static final float MAX_JUMP_VELOCITY = 3.5f;

    public static int chargeTime = 0;
    public static boolean isCharging = false;
    public static boolean charging = false;
    private static boolean startedWhileSprinting = false;

    private static boolean canChargeJump(PlayerEntity player) {
        return HeroUtil.isWearingSuit(player, FecfsTags.Items.SPIDERMAN)
                && !player.getAbilities().flying && !player.isFallFlying()
                && (player.isOnGround() || WebSwing.isSwinging) && player.isAlive();
    }

    public static void startCharging(PlayerEntity player, boolean isSprinting) {
        if(!canChargeJump(player)) return;
        isCharging = true;
        charging = true;
        chargeTime = 0;
        startedWhileSprinting = isSprinting;
    }

    public static void stopCharging(PlayerEntity player) {
        if(!canChargeJump(player)) return;
        if (isCharging) {
            isCharging = false;
            charging = false;

            if (chargeTime < 5) {
                player.jump();
            } else if (chargeTime >= MIN_CHARGE_TIME) {
                float chargePercentage = updateChargeBar(player);

                if (startedWhileSprinting) {
                    float jumpVelocity = 0.42f + (MAX_JUMP_VELOCITY - 0.42f) * chargePercentage * 0.75f;
                    Vec3d forward = player.getRotationVector().normalize();
                    Vec3d velocity = player.getVelocity();
                    Vec3d boost = forward.multiply(chargePercentage * 2.8f);

                    player.setVelocity(velocity.x + boost.x, jumpVelocity, velocity.z + boost.z);
                } else {
                    float jumpVelocity = 0.42f + (MAX_JUMP_VELOCITY - 0.42f) * chargePercentage;
                    Vec3d velocity = player.getVelocity();
                    player.setVelocity(velocity.x, jumpVelocity, velocity.z);
                }
                player.velocityModified = true;
            } else if (chargeTime >= 5 && WebSwing.isSwinging) {
                WebSwing.stopSwinging(player);
                float chargePercentage = updateChargeBar(player);

                float jumpVelocity = 0.42f + (MAX_JUMP_VELOCITY - 0.42f) * chargePercentage * 0.45f;
                Vec3d forward = player.getRotationVector().normalize();
                Vec3d velocity = player.getVelocity();
                Vec3d boost = forward.multiply(chargePercentage * 0.9);

                player.setVelocity(velocity.x + boost.x, jumpVelocity, velocity.z + boost.z);
            }
        }
        chargeTime = 0;
    }
    public static void cancelCharge(PlayerEntity player) {
        if(!canChargeJump(player) || !player.isOnGround()) {
            isCharging = false;
            charging = false;
        }
    }

    public static void tick(PlayerEntity player) {
        if (isCharging && canChargeJump(player)) {
            if (chargeTime < maxChargeTime(player)) {
                chargeTime++;
                updateChargeBar(player);
            }
        }
        if((!canChargeJump(player) || !player.isOnGround()) && isCharging) {
            cancelCharge(player);
        }
    }

    public boolean isCharging() {
        return isCharging;
    }

    public static float updateChargeBar(PlayerEntity player) {
        float chargePercentage = (float)(chargeTime - MIN_CHARGE_TIME) / (maxChargeTime(player) - MIN_CHARGE_TIME);
        return chargePercentage = Math.max(0, Math.min(chargePercentage, 1.0f));
    }

}

