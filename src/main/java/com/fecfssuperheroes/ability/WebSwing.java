package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.networking.FecfsNetworking;
import com.fecfssuperheroes.sound.WebSwingingSoundInstance;
import com.fecfssuperheroes.util.RendererUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import com.fecfssuperheroes.util.HeroUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;

import static com.fecfssuperheroes.util.FecfsAnimations.*;

@Environment(EnvType.CLIENT)
public class WebSwing {
    private static WebSwingingSoundInstance currentSoundInstance;
    public static boolean play = false;
    private static int cooldownTicks = 0;
    private static boolean isCooldownActive = false;
    private static PlayerEntity lastSwingPlayer = null;
    public static boolean isSwinging = false;
    private static int swingTime = 0;
    public static Vec3d anchorPoint = null;
    private static double webLength = 0;
    private static final double MAX_SPEED = 2.5;
    private static Vec3d swingPlaneNormal = Vec3d.ZERO;
    public static int swingDuration = 0;
    public static Direction anchorFacing = null;
    public static boolean swingModeToggled = false;

    public static Arm swingHand(PlayerEntity player) {
        return player.getMainArm();
    }

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(WebSwing::onClientTick);
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            if (isSwinging) {

            }
        });
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && anchorPoint != null) {
                RendererUtils.renderWebLine(context.matrixStack(), context.consumers(), player, anchorPoint, context.tickDelta(), true);
                RendererUtils.renderWebHits(context);
            }
        });
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                RendererUtils.renderPastWebLines(context.matrixStack(), context.consumers(), context.tickDelta());
            }
        });
    }

    public static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;
        client.player.sendMessage(Text.literal("Swing = "+swingModeToggled), true);
        if (isCooldownActive) {
            if (cooldownTicks > 0) cooldownTicks--;
            else isCooldownActive = false;
        }
        if (isSwinging) swing(client.player);
    }

    public static void startSwing(PlayerEntity player) {
        if (HeroUtil.canUseWeb(player, true) && !isCooldownActive) {
            BlockHitResult hitRes = HeroUtil.raycast(player, HeroUtil.isWearingWebShooter(player) ? 100 : 150);
            if (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK && swingHand(player) != null) {
                anchorPoint = hitRes.getPos();
                anchorFacing = hitRes.getSide();
                RendererUtils.showWebHit(anchorPoint, anchorFacing);
                isSwinging = true;
                swingTime = 0;
                player.setNoDrag(true);
                webLength = anchorPoint.distanceTo(player.getPos());
                swingPlaneNormal = player.getVelocity().crossProduct(new Vec3d(0, -1, 0)).normalize();
                play = true;
                swingDuration = 0;
                cooldownTicks = HeroUtil.isWearingWebShooter(player) ? 100 : 40;
                isCooldownActive = true;
                lastSwingPlayer = player;
                player.getAbilities().allowFlying = false;
                player.setJumping(false);
                ClientPlayNetworking.send(FecfsNetworking.SOUND, PacketByteBufs.create());
                if(isSwinging) {
                    MinecraftClient.getInstance().getSoundManager().play(new WebSwingingSoundInstance(MinecraftClient.getInstance().player));
                }
            }
        }
    }


    public static void swing(PlayerEntity player) {
        if (anchorPoint == null || !HeroUtil.canUseWeb(player, true) || !swingModeToggled) {
            stopSwinging(player);
            return;
        }
        swingDuration++;
        swingTime++;
        if (swingTime > 400) {
            stopSwinging(player);
            return;
        }
        Vec3d playerPos = player.getPos();
        Vec3d toAnchor = anchorPoint.subtract(playerPos);
        double distanceToAnchor = toAnchor.length();
        if (distanceToAnchor > webLength) {
            Vec3d radialDirection = toAnchor.normalize();
            Vec3d tangentialVelocity = player.getVelocity().subtract(radialDirection.multiply(player.getVelocity().dotProduct(radialDirection)));
            player.setVelocity(tangentialVelocity.add(new Vec3d(0, -0.08, 0)).multiply(0.9935));
            applyPlayerInput(player);
        } else {
            player.setVelocity(player.getVelocity().add(0, -0.08, 0));
            applyPlayerInput(player);
            if (player.getVelocity().length() > MAX_SPEED)
                player.setVelocity(player.getVelocity().normalize().multiply(MAX_SPEED));
        }
    }

    private static void applyPlayerInput(PlayerEntity player) {
        Vec3d input = getMovementInput(player) != null ? getMovementInput(player) : Vec3d.ZERO;
        if(input == null) return;
        player.setVelocity(player.getVelocity().add(input.multiply(0.045)));
        if (player.getVelocity().length() > MAX_SPEED)
            player.setVelocity(player.getVelocity().normalize().multiply(MAX_SPEED));
    }

    private static Vec3d getMovementInput(PlayerEntity player) {
        ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity != null) {
            float forward = playerEntity.input.movementForward;
            float strafe = playerEntity.input.movementSideways;
            Vec3d lookVec = player.getRotationVector();
            Vec3d strafeVec = lookVec.crossProduct(new Vec3d(0, 1, 0)).normalize();
            return lookVec.multiply(forward).add(strafeVec.multiply(-strafe)).normalize();
        }
        return null;
    }

    public static boolean isSwingOnCooldown() {
        return isCooldownActive;
    }

    public static void stopSwinging(PlayerEntity player) {
        Vec3d webStartPos = RendererUtils.getWebStartPosition(player, 0);
        RendererUtils.addWebLine(webStartPos, anchorPoint);
        if (webStartPos != null && anchorPoint != null) RendererUtils.addWebLine(webStartPos, anchorPoint);
        isSwinging = false;
        anchorPoint = null;
        swingTime = 0;
        player.setNoDrag(false);
        swingPlaneNormal = null;
        swingDuration = 0;
        if (currentSoundInstance != null) {
            MinecraftClient.getInstance().getSoundManager().stop(currentSoundInstance);
            currentSoundInstance = null;
        }
        if(player.getAbilities().creativeMode) {
            player.getAbilities().allowFlying = true;
        }
        player.setJumping(true);

    }

    public static void boost(PlayerEntity player) {
        if (!isSwinging || anchorPoint == null) return;
        Vec3d forwardDirection = player.getRotationVector().normalize();
        Vec3d boostVelocity = forwardDirection.multiply(1.5).add(0, 1.5, 0);
        player.setVelocity(player.getVelocity().add(boostVelocity));
        if (player.getVelocity().length() > MAX_SPEED)
            player.setVelocity(player.getVelocity().normalize().multiply(MAX_SPEED));
    }
}
