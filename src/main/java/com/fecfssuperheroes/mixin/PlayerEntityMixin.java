package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.util.FecfsAnimations;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Unique
    private boolean isFalling = false;
    @Unique
    private boolean hasPlayedLandingAnimation = false;
    @Unique
    private double startY = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!player.getWorld().isClient) return;
        if (!player.isOnGround() && player.getVelocity().y < 0) {
            if (!isFalling) {
                isFalling = true;
                hasPlayedLandingAnimation = false;
                startY = player.getY();
            }
        }
        if (player.isOnGround() && isFalling) {
            isFalling = false;
            double fallDistance = Math.max(player.fallDistance, startY - player.getY());
            if (fallDistance >= 3 && !hasPlayedLandingAnimation) {
                hasPlayedLandingAnimation = true;
                FecfsAnimations.playSpiderManLandingAnimation(player);
            }
        }
    }
}
