package com.fecfssuperheroes.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Evade {
    private static boolean isDodging = false;
    private static boolean cooldown = false;
    public static boolean hasDodged = false;
    private static int dodgeCounter = 0;
    private static int lastDodgeTime = 0;
    private static final int DODGE_RESET_TIME = 40;
    private static final int DODGE_COOLDOWN = 20;
    private static final int DODGE_DURATION = 20;


    public static void performEvade(PlayerEntity player) {
        if (player == null) return;
        if(!player.isOnGround() && !player.getAbilities().flying && player.isAlive() && !WebSwing.isSwinging && !WebZip.isZipping()) return;
        int currentTime = player.age;
        if (currentTime - lastDodgeTime < DODGE_COOLDOWN) {
            return;
        }
        Vec3d dodgeDirection = getMovementInputDirection(player);
        if (dodgeDirection.lengthSquared() == 0) {
            return;
        }
        double dodgeSpeed = 1.75;
        Vec3d dodgeVelocity = dodgeDirection.normalize().multiply(dodgeSpeed);
        player.addVelocity(dodgeVelocity.x, 0, dodgeVelocity.z);
        player.velocityModified = true;
        isDodging = true;
        hasDodged = true;

        if (currentTime - lastDodgeTime > DODGE_RESET_TIME) {
            dodgeCounter = 1;
        } else {
            dodgeCounter++;
        }
        lastDodgeTime = currentTime;
        scheduleDodgeEnd();
    }

    private static Vec3d getMovementInputDirection(PlayerEntity player) {
        Vec3d velocity = player.getVelocity();
        if (velocity.lengthSquared() > 0.0001) {
            return new Vec3d(velocity.x, 0, velocity.z);
        } else {
            float yaw = player.getYaw();
            double radians = Math.toRadians(yaw);
            double x = -Math.sin(radians);
            double z = Math.cos(radians);
            return new Vec3d(x, 0, z);
        }
    }

    private static void scheduleDodgeEnd() {
        new Thread(() -> {
            try {
                Thread.sleep(DODGE_DURATION * 50);
                isDodging = false;
                cooldown = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean isDodging() {
        return isDodging;
    }

    public boolean hasDodged() {
        return hasDodged;
    }

    public int getDodgeCounter() {
        return dodgeCounter;
    }
}
