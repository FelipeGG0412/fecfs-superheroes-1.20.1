package com.fecfssuperheroes.sound;

import com.fecfssuperheroes.FecfsSuperheroes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class FecfsSounds {
    public static final SoundEvent WEB_HIT = registerSoundEvent("web_hit");
    public static final SoundEvent WEB_SHOOT_RAIMI = registerSoundEvent("web_shoot_raimi");
    public static final SoundEvent WEB_SHOOT_PROJECTILE_RAIMI = registerSoundEvent("web_shoot_projectile_raimi");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier identifier = new Identifier(FecfsSuperheroes.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void registerSounds() {
        FecfsSuperheroes.LOGGER.info("Registering sounds for "+FecfsSuperheroes.MOD_ID);
    }
}
