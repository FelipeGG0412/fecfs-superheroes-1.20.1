package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.networking.FecfsNetworking;
import com.fecfssuperheroes.util.HeroUtil;
import com.fecfssuperheroes.util.RendererUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class WebZip {
    private static boolean isZipping = false;
    public static boolean play = false;
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
                RendererUtils.renderWebLine(context.matrixStack(), context.consumers(), player, anchorPoint, anchorFacing, context.tickDelta(), true);
                RendererUtils.renderWebHits(context);
            }
        });
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                RendererUtils.renderUsedWebLines(context.matrixStack(), context.consumers(), context.tickDelta());
            }
        });
    }
    public static void startZip(PlayerEntity player) {
        if(WebSwing.isSwinging) return;
        if (HeroUtil.canUseWeb(player, true) && zipCooldown == 0) {
            BlockHitResult hitRes = HeroUtil.raycast(player, (HeroUtil.isWearingWebShooter(player) ? 100 : 150));
            if (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK) {
                anchorPoint = hitRes.getPos();
                anchorFacing = hitRes.getSide();
                RendererUtils.showWebHit(anchorPoint, anchorFacing);
                play = true;
                isZipping = true;
                zipCooldown = HeroUtil.isWearingWebShooter(player) ? 45 : 30;
                canZip = true;
                player.getAbilities().allowFlying = false;
                if(isZipping) {
                    ClientPlayNetworking.send(FecfsNetworking.SOUND, PacketByteBufs.create());
                }
            }
        }
    }
    private static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;
        if (zipCooldown > 0) {
            zipCooldown--;
        }
        if (isZipping && anchorPoint != null) {
            performZip(client.player);
        }
    }
    private static void performZip(PlayerEntity player) {
        if (anchorPoint == null || zipTickCounter >= ZIP_DURATION_TICKS) {
            stopZip();
            return;
        }
        Vec3d toAnchor = anchorPoint.subtract(player.getPos());
        if (toAnchor.length() < 1.5) {
            stopZip();
            return;
        }
        Vec3d pullVelocity = toAnchor.normalize().multiply(2.75);
        player.setVelocity(pullVelocity);
        zipTickCounter++;
    }
    private static void stopZip() {
        if (MinecraftClient.getInstance().player != null && anchorPoint != null) {
            Vec3d webStartPos = RendererUtils.webStartPosition(MinecraftClient.getInstance().player, 0);
            if (webStartPos != null) {
                RendererUtils.addWebLine(webStartPos, anchorPoint);
            }
        }
        isZipping = false;
        anchorPoint = null;
        zipTickCounter = 0;
        MinecraftClient.getInstance().player.getAbilities().allowFlying = true;
    }

    public static boolean isWebZipOnCooldown() {
        return zipCooldown > 0;
    }
    public static boolean isZipping() {
        return zipCooldown > 0;
    }

}