package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.ability.WebSwinging;
import com.fecfssuperheroes.ability.WebZip;
import com.fecfssuperheroes.util.FecfsAnimations;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @ModifyReturnValue(method = "isBlockBreakingRestricted", at = @At("TAIL"))
    private boolean isBlockBreakingRestricted(boolean original) {
        return original || WebSwinging.isSwinging || WebZip.isZipping();
    }
}
