package com.fecfssuperheroes.networking.packet;

import com.fecfssuperheroes.ability.WebSwing;
import com.fecfssuperheroes.ability.WebZip;
import com.fecfssuperheroes.sound.FecfsSounds;
import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoundS2C {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf,
                               PacketSender responseSender) {
        if((WebSwing.play && !player.isOnGround()) || WebZip.play) {
            player.getWorld().playSound(null, player.getBlockPos(), HeroUtil.isWearingSuit(player, FecfsTags.Items.RAIMI) ? FecfsSounds.WEB_SHOOT_RAIMI :
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1f, 1f);
        }

    }
}
