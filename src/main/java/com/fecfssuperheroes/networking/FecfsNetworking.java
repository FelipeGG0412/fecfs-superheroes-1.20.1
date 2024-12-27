package com.fecfssuperheroes.networking;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.networking.packet.AbilityThreeSpidermanS2C;
import com.fecfssuperheroes.networking.packet.JumpAnimationC2S;
import com.fecfssuperheroes.networking.packet.SoundS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class FecfsNetworking {
    public static final Identifier ABILITY_THREE_SPIDERMAN = new Identifier(FecfsSuperheroes.MOD_ID, "ability_three_spiderman");
    public static final Identifier JUMP_ANIMATION = new Identifier(FecfsSuperheroes.MOD_ID, "jump_animation");
    public static final Identifier SOUND = new Identifier(FecfsSuperheroes.MOD_ID, "sound");
    public static final Identifier SWING_SOUND = new Identifier(FecfsSuperheroes.MOD_ID, "swing_sound");


    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(JUMP_ANIMATION, JumpAnimationC2S::receive);


    }
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ABILITY_THREE_SPIDERMAN, AbilityThreeSpidermanS2C::receive);
        ServerPlayNetworking.registerGlobalReceiver(SOUND, SoundS2C::receive);
    }
}
