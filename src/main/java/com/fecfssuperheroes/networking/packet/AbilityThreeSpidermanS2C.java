package com.fecfssuperheroes.networking.packet;

import com.fecfssuperheroes.entity.custom.WebProjectile;
import com.fecfssuperheroes.sound.FecfsSounds;
import com.fecfssuperheroes.util.HeroUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

import java.util.HashMap;
import java.util.UUID;

public class AbilityThreeSpidermanS2C {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        WebProjectile webProjectile = new WebProjectile(player.getWorld(), player);
        webProjectile.setVelocity(player, player.getPitch(), player.getYaw(), (HeroUtil.isWearingWebShooter(player) ? 1f : 0f),
                (HeroUtil.isWearingWebShooter(player) ? 0.7f : 1.25f), (HeroUtil.isWearingWebShooter(player) ? 1f : 0f));
        player.getWorld().spawnEntity(webProjectile);
        player.getWorld().playSound(null, player.getBlockPos(), FecfsSounds.WEB_SHOOT_PROJECTILE_RAIMI, SoundCategory.PLAYERS, 1f, 1f);
    }
}
