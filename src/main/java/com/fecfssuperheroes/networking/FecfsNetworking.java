package com.fecfssuperheroes.networking;

import com.fecfssuperheroes.FecfsSuperheroes;
import com.fecfssuperheroes.networking.packet.AbilityThreeSpidermanS2C;
import com.fecfssuperheroes.networking.packet.WebSoundS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class FecfsNetworking {
    public static final Identifier ABILITY_THREE_SPIDERMAN = new Identifier(FecfsSuperheroes.MOD_ID, "ability_three_spiderman");
    public static final Identifier WEB_SOUND = new Identifier(FecfsSuperheroes.MOD_ID, "web_sound");

    public static void registerS2CPackets() {

    }
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ABILITY_THREE_SPIDERMAN, AbilityThreeSpidermanS2C::receive);
        ServerPlayNetworking.registerGlobalReceiver(WEB_SOUND, WebSoundS2C::receive);
    }
}
