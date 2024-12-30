package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.ability.ChargeJump;
import com.fecfssuperheroes.ability.WebSwing;
import com.fecfssuperheroes.power.custom.Jump;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyReturnValue(method = "getJumpVelocity()F", at = @At("TAIL"))
    private float modifyJumpVelocity(float original) {
        return original + (Jump.jump ? 0.1f * (Jump.getCurrentAmplifier() + 1f) : 0);
    }


    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player && entity.getWorld().isClient) {
            boolean isSprinting = player.isSprinting();
            boolean isSneaking = player.isSneaking();
            boolean isSpacePressed = MinecraftClient.getInstance().options.jumpKey.isPressed();

            if ((WebSwing.isSwinging && isSpacePressed) ||
                    (isSneaking && isSpacePressed) ||
                    (isSprinting && isSpacePressed)) {

                if (!ChargeJump.charging) {
                    ChargeJump.startCharging(player, isSprinting);
                }
                ChargeJump.tick(player);
                player.setJumping(false);
            } else if (ChargeJump.isCharging) {
                ChargeJump.stopCharging(player);
            }
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof PlayerEntity player) {
            if (ChargeJump.isCharging) {
                ci.cancel();
                return;
            }
            if (this.isSprinting() && Jump.jump) {
                float f = this.getYaw() * (float) (Math.PI / 180.0);
                float v = 1.5f;
                this.setVelocity(this.getVelocity().add(-MathHelper.sin(f) * v, 0.0, MathHelper.cos(f) * v));
            }
            if (entity.getWorld().isClient) {
                // FecfsAnimations.playSpiderManJumpAnimation(player);
            }
        }
    }
}
