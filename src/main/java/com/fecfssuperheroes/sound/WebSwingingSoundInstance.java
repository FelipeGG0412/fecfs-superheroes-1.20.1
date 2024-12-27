package com.fecfssuperheroes.sound;

import com.fecfssuperheroes.ability.WebSwing;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class WebSwingingSoundInstance extends MovingSoundInstance {
    private static final int FADE_IN_DURATION = (int) (20 * 1.35);
    private static final int FADE_OUT_DURATION = (int) (20 * 1.5);
    private static final float VOLUME_MULTIPLIER = 0.6F;
    private final ClientPlayerEntity player;
    private int tickCount;

    public WebSwingingSoundInstance(ClientPlayerEntity player) {
        super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1F;
    }

    @Override
    public void tick() {
        this.tickCount++;
        if (!this.player.isRemoved() && (this.tickCount <= FADE_IN_DURATION || WebSwing.isSwinging)) {
            this.x = (double) ((float) this.player.getX());
            this.y = (double) ((float) this.player.getY());
            this.z = (double) ((float) this.player.getZ());
            float speedFactor = (float) this.player.getVelocity().lengthSquared() * 3;
            if ((double) speedFactor >= 1.0E-7) {
                this.volume = MathHelper.clamp(speedFactor / 4.0F, 0.0F, 1.0F) * VOLUME_MULTIPLIER;
            } else {
                this.volume = 0.0F;
            }

            if (this.tickCount < FADE_IN_DURATION) {
                this.volume = this.volume * ((float) (this.tickCount) / FADE_IN_DURATION);
            }

            float maxPitchFactor = 0.8F;
            if (this.volume > maxPitchFactor) {
                this.pitch = 1.0F + (this.volume - maxPitchFactor);
            } else {
                this.pitch = 1.0F;
            }
        } else {
            if (this.volume > 0) {
                this.volume -= (1.0F / FADE_OUT_DURATION) * VOLUME_MULTIPLIER;
            } else {
                this.setDone();
            }
        }
    }
}
