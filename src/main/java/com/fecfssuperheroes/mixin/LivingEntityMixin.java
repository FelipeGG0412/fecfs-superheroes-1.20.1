package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.ability.ChargeJump;
import com.fecfssuperheroes.ability.WebSwing;
import com.fecfssuperheroes.hero.FecfsHeroes;
import com.fecfssuperheroes.hero.Hero;
import com.fecfssuperheroes.power.Power;
import com.fecfssuperheroes.power.custom.FallResistance;
import com.fecfssuperheroes.power.custom.Jump;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
    @Shadow protected abstract void playBlockFallSound();

    @Shadow protected abstract void playHurtSound(DamageSource source);

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
    @ModifyArg(
            method = "handleFallDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            ),
            index = 1
    )
    private float modifyFallDamageAmount(DamageSource source, float originalAmount) {
        if ((Object) this instanceof PlayerEntity player) {
            Hero currentHero = FecfsHeroes.getCurrentHero(player);
            if (currentHero != null) {
                Power fallResistancePower = currentHero.getPowers().get("Fall resistance");
                if (fallResistancePower != null) {
                    // Calculate reduced damage
                    float reducedAmount = originalAmount * (1 - (fallResistancePower.getAmplifier() / 100.0f));
                    if (reducedAmount < 0.4f) {
                        return 0.0f;
                    }

                    return reducedAmount;
                }
            }
        }

        // Return the original amount if no adjustments are needed
        return originalAmount;
    }
}
