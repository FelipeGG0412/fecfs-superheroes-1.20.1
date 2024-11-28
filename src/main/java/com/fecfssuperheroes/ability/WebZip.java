package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.util.HeroUtil;
import com.fecfssuperheroes.util.WebRendererUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class WebZip {
    private static boolean isZipping = false;
    private static Vec3d anchorPoint = null;
    private static int zipCooldown = 0;
    private static final int ZIP_DURATION_TICKS = 5;
    private static int zipTickCounter = 0;

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(WebZip::onClientTick);
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                WebRendererUtil.renderWebLine(
                        context.matrixStack(),
                        context.consumers(),
                        player,
                        anchorPoint,
                        context.tickDelta(),
                        false
                );
            }
        });
    }
    public static void startZip(PlayerEntity player) {
        if(WebSwinging.isSwinging) return;
        if (HeroUtil.canUseWeb(player, true) && zipCooldown == 0) {
            BlockHitResult hitRes = HeroUtil.raycast(player, 80);
            if (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK) {
                anchorPoint = hitRes.getPos();
                isZipping = true;
                zipCooldown = HeroUtil.isWearingWebShooter(player) ? 45 : 30;
            }
        }
    }
    private static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;

        // Handle cooldown
        if (zipCooldown > 0) {
            zipCooldown--;
        }

        // Handle active zip
        if (isZipping && anchorPoint != null) {
            performZip(client.player);
        }
    }
    private static void performZip(PlayerEntity player) {
        if (anchorPoint == null || zipTickCounter >= ZIP_DURATION_TICKS) {
            stopZip();
            return;
        }

        // Calculate the direction vector to the anchor
        Vec3d toAnchor = anchorPoint.subtract(player.getPos());

        // Stop if the player is too close to the anchor point
        if (toAnchor.length() < 1.5) {
            stopZip();
            return;
        }

        // Apply constant velocity toward the anchor
        Vec3d pullVelocity = toAnchor.normalize().multiply(2.75); // Adjust the speed as needed
        player.setVelocity(pullVelocity);

        // Increment the zip tick counter
        zipTickCounter++;
    }
    private static void stopZip() {
        isZipping = false;
        anchorPoint = null;
        zipTickCounter = 0;
    }
    public static boolean isWebZipOnCooldown() {
        return zipCooldown > 0;
    }
    public static boolean isZipping() {
        return zipCooldown > 0;
    }

}