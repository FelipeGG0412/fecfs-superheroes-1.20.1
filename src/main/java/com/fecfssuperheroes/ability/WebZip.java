package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.networking.FecfsNetworking;
import com.fecfssuperheroes.sound.FecfsSounds;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import com.fecfssuperheroes.util.RendererUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class WebZip {
    private static boolean isZipping = false;
    public static boolean canZip = false;
    private static Vec3d anchorPoint = null;
    private static int zipCooldown = 0;
    private static final int ZIP_DURATION_TICKS = 5;
    private static int zipTickCounter = 0;
    public static Direction anchorFacing = null;


    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(WebZip::onClientTick);
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                RendererUtils.renderWebLine(
                        context.matrixStack(),
                        context.consumers(),
                        player,
                        anchorPoint,
                        context.tickDelta(),
                        false
                );
                RendererUtils.renderWebHits(context);
            }
        });
    }
    public static void startZip(PlayerEntity player) {
        if(WebSwinging.isSwinging) return;
        ClientPlayNetworking.send(FecfsNetworking.WEB_SOUND, PacketByteBufs.create());
        if (HeroUtil.canUseWeb(player, true) && zipCooldown == 0) {
            BlockHitResult hitRes = HeroUtil.raycast(player, 80);
            if (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK) {
                anchorPoint = hitRes.getPos();
                anchorFacing = hitRes.getSide();
                RendererUtils.showWebHit(anchorPoint, anchorFacing); // Call the helper method

                isZipping = true;
                zipCooldown = HeroUtil.isWearingWebShooter(player) ? 45 : 30;
                canZip = true;
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