package com.fecfssuperheroes.event;

import com.fecfssuperheroes.util.FecfsAnimations;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class FecfsClientEvents {
    public static void animationTickEvents(MinecraftClient client) {
        PlayerEntity user = client.player;
        if(user != null) {
//            FecfsAnimations.playSpiderManDiveAnimation(user);
        }
    }
}
