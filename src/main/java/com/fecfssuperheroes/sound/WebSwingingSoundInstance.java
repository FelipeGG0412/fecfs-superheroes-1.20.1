package com.fecfssuperheroes.sound;

import com.fecfssuperheroes.ability.WebSwinging;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class WebSwingingSoundInstance extends MovingSoundInstance {
    private final MinecraftClient client;
    private final float MAX_VOLUME = 1.0F; // Max volume for the sound
    private final float MIN_VOLUME = 0.1F; // Minimum volume for the sound
    private final float MAX_SPEED = 1.0F; // Speed threshold for max volume
    private final float MIN_SPEED = 0.2F; // Speed threshold for minimum volume

    private float lastSpeed = 0f;
    private float currentSpeed = 0f;

    public WebSwingingSoundInstance(SoundEvent soundEvent, MinecraftClient client) {
        super(soundEvent, SoundCategory.PLAYERS, SoundInstance.createRandom());
        this.client = client;
        this.volume = 0.0F; // Start with 0 volume and adjust dynamically
        this.pitch = 1.0F;
        this.repeat = true; // We want it to loop
    }

    @Override
    public void tick() {
        if (client.player != null && WebSwinging.isSwinging) {
            Vec3d velocity = client.player.getVelocity();
            this.currentSpeed = (float) velocity.length();

            // Adjust the volume based on speed
            if (currentSpeed >= MIN_SPEED) {
                // Scale volume and pitch based on speed
                this.volume = MathHelper.clamp((currentSpeed - MIN_SPEED) / (MAX_SPEED - MIN_SPEED) * (MAX_VOLUME - MIN_VOLUME) + MIN_VOLUME, MIN_VOLUME, MAX_VOLUME);
                this.pitch = 1.0F + (currentSpeed / MAX_SPEED) * 0.5F; // Adjust pitch based on speed
            } else {
                this.volume = 0.0F; // If below the threshold, no sound
            }

            // Update position to follow player
            this.x = client.player.getX();
            this.y = client.player.getY();
            this.z = client.player.getZ();
        } else {
            this.setDone(); // Stop the sound if we're not swinging
        }
    }
}
