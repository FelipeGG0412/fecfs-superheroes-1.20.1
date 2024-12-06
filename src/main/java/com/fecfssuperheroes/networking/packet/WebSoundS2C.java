package com.fecfssuperheroes.networking.packet;

import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.ability.WebZip;
import com.fecfssuperheroes.event.FecfsKeyInputHandler;
import com.fecfssuperheroes.sound.FecfsSounds;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

import java.util.HashMap;
import java.util.UUID;

public class WebSoundS2C {
    private static final HashMap<UUID, Long> soundCooldownMap = new HashMap<>();
    private static final long SOUND_COOLDOWN = 1000;

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID playerId = player.getUuid();
        long currentTime = System.currentTimeMillis();
        if (soundCooldownMap.containsKey(playerId) && (currentTime - soundCooldownMap.get(playerId)) < SOUND_COOLDOWN) {
            return;
        }
        soundCooldownMap.put(playerId, currentTime);
        if (FecfsKeyInputHandler.webSwingingKey && HeroUtil.canUseWeb(player, false)) {
            player.getWorld().playSound(null,
                    player.getBlockPos(), (HeroUtil.isWearingSuit(player, FecfsTags.Items.RAIMI) ? FecfsSounds.WEB_RAIMI : FecfsSounds.WEB_SHOOT),
                    SoundCategory.PLAYERS, 1f, 1f);
        } else if(FecfsKeyInputHandler.webZipKey && HeroUtil.canUseWeb(player, true)) {
            player.getWorld().playSound(null,
                    player.getBlockPos(), (HeroUtil.isWearingSuit(player, FecfsTags.Items.RAIMI) ? FecfsSounds.WEB_RAIMI : FecfsSounds.WEB_SHOOT),
                    SoundCategory.PLAYERS, 1f, 1f);
        } else {
            player.getWorld().playSound(null, player.getBlockPos(), FecfsSounds.WEB_SHOOT_FAIL, SoundCategory.PLAYERS, 1f, 1f);
        }
    }
}
