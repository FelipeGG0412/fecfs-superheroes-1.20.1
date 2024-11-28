package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.util.FecfsAnimations;
import com.fecfssuperheroes.util.WebRendererUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

@Environment(EnvType.CLIENT)
public class WebSwinging {
    private static int cooldownTicks = 0;
    private static boolean isCooldownActive = false;
    private static PlayerEntity lastSwingPlayer = null;
    public static boolean isSwinging = false;
    private static int swingTime = 0;
    public static Vec3d anchorPoint = null;
    private static double initialWebLength = 0;
    private static final double SPRING_CONSTANT = 0.05;
    private static final double DAMPING_COEFFICIENT = 0.000125;
    private static Vec3d initialToPlayer = null;
    private static Vec3d swingPlaneNormal = null;
    public static int swingDuration = 0;
    public static Arm swingHand(PlayerEntity player) {
        return player.getMainArm();
    }

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(WebSwinging::onClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && isSwinging) {
                FecfsAnimations.playSpiderManSwingingAnimations(client.player);
            }});
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                WebRendererUtil.renderWebLine(
                        context.matrixStack(),
                        context.consumers(),
                        player,
                        anchorPoint,
                        context.tickDelta(),
                        true
                );
            }
        });
    }
    public static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;
        if (isCooldownActive) {
            if (cooldownTicks > 0) {
                cooldownTicks--;
            } else {
                isCooldownActive = false;
            }
        }
        if (isSwinging) {
            swing(client.player);
            FecfsAnimations.playSpiderManSwingingAnimations(client.player);
        }
    }
    public static void startSwing(PlayerEntity player) {
        if (HeroUtil.canUseWeb(player) && !isCooldownActive) {
            BlockHitResult hitRes = HeroUtil.raycast(player, 150);
            if (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK && swingHand(player) != null) {
                anchorPoint = hitRes.getPos();
                isSwinging = true;
                swingTime = 0;
                initialWebLength = HeroUtil.isWearingSuit(player, FecfsTags.Items.SPIDERMAN)
                        ? Math.min(anchorPoint.subtract(player.getPos()).length(), 120.0)
                        : Math.min(anchorPoint.subtract(player.getPos()).length(), 70.0);
                player.setNoDrag(true);
                initialToPlayer = player.getPos().subtract(anchorPoint).normalize();
                swingPlaneNormal = initialToPlayer.crossProduct(new Vec3d(0, -1, 0)).normalize();
                swingDuration = 0;
                if (HeroUtil.isWearingWebShooter(player)) {
                    cooldownTicks = 100;
                } else if (HeroUtil.isWearingSuit(player, FecfsTags.Items.SPIDERMAN)) {
                    cooldownTicks = 40;
                }
                isCooldownActive = true;
                lastSwingPlayer = player;
            }
        }
    }
    public static void swing(PlayerEntity player) {
        if (anchorPoint == null || !HeroUtil.canUseWeb(player)) {
            stopSwinging(player);
            return;
        }
        swingDuration++;
        swingTime++;
        if (player.isOnGround()) {
            player.setNoDrag(false);
            return;
        } else {
            player.setNoDrag(true);
        }

        if (swingTime > 400) {
            stopSwinging(player);
        }

        if(anchorPoint != null) {
            Vec3d toAnchor = anchorPoint.subtract(player.getPos());
            Vec3d acceleration = new Vec3d(0, -0.08, 0);
            double stretch = toAnchor.length() - initialWebLength;

            if (stretch > 0) {
                Vec3d springForce = toAnchor.normalize().multiply(SPRING_CONSTANT * stretch);
                acceleration = acceleration.add(springForce);
            }
            if (stretch < 0) {
                Vec3d additionalDamping = player.getVelocity().multiply(-DAMPING_COEFFICIENT * 2);
                acceleration = acceleration.add(additionalDamping);
            }
            Vec3d dampingForce = player.getVelocity().multiply(-DAMPING_COEFFICIENT);
            acceleration = acceleration.add(dampingForce);
            double minDistance = 2;
            if (toAnchor.length() < minDistance) {
                double compression = minDistance - toAnchor.length();
                Vec3d repulsiveForce = toAnchor.normalize().multiply(-SPRING_CONSTANT * compression * 2);
                acceleration = acceleration.add(repulsiveForce);
            }

            Vec3d newVelocity = player.getVelocity().add(acceleration);
            double maxSpeed = HeroUtil.isWearingSuit(player, FecfsTags.Items.SPIDERMAN) ? 3 : 2.3;
            if (newVelocity.length() > maxSpeed) {
                newVelocity = newVelocity.normalize().multiply(maxSpeed);
            }

            player.setVelocity(newVelocity);
        }

    }
    public static boolean isSwingOnCooldown() {
        return isCooldownActive;
    }
    public static int getRemainingCooldownTicks() {
        return cooldownTicks;
    }
    public static void stopSwinging(PlayerEntity player) {
        isSwinging = false;
        anchorPoint = null;
        swingTime = 0;
        player.setNoDrag(false);
        initialToPlayer = null;
        swingPlaneNormal = null;
        swingDuration = 0;
    }
    public static void boost(PlayerEntity player) {
        if (!isSwinging || anchorPoint == null) return;
        Vec3d boostedVelocity = HeroUtil.isWearingSuit(player, FecfsTags.Items.SPIDERMAN) ? player.getVelocity().multiply(1.75)
                .add(new Vec3d(0, 1.25, 0)) : player.getVelocity().multiply(1.45).add(new Vec3d(0, 1.1, 0));
        player.setVelocity(boostedVelocity);
    }
    public static double getSwingingAngle(Vec3d playerPos, Vec3d anchorPos) {
        if (playerPos == null || anchorPos == null || swingPlaneNormal == null || initialToPlayer == null) {
            return 0;
        }
        Vec3d toPlayer = playerPos.subtract(anchorPos).normalize();
        Vec3d toPlayerProjected = toPlayer.subtract(swingPlaneNormal.multiply(toPlayer.dotProduct(swingPlaneNormal))).normalize();
        Vec3d initialToPlayerProjected = initialToPlayer.subtract(swingPlaneNormal.multiply(initialToPlayer.dotProduct(swingPlaneNormal))).normalize();
        double angleRadians = Math.acos(initialToPlayerProjected.dotProduct(toPlayerProjected));
        double angleSign = swingPlaneNormal.dotProduct(initialToPlayerProjected.crossProduct(toPlayerProjected));
        double angleDegrees = Math.toDegrees(angleRadians);
        if (angleSign < 0) {
            angleDegrees = -angleDegrees;
        }
        return angleDegrees;
    }
}
