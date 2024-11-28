package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.ability.ChargedJump;
import com.fecfssuperheroes.power.custom.Jump;
import com.fecfssuperheroes.util.FecfsAnimations;
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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable{
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    float myJumpBoostVelocityModifier = 0.1f * (Jump.getCurrentAmplifier() + 1f);

    @ModifyReturnValue(method = "getJumpVelocity()F", at = @At("TAIL"))
    private float modifyJumpVelocity(float original) {
        return original + (Jump.jump ? myJumpBoostVelocityModifier : 0);
    }

    @Unique
    private ChargedJump chargedJump = new ChargedJump();

    @Unique
    private boolean prevJumping = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player) {
            if (entity.getWorld().isClient) {
                boolean isSneaking = player.isSneaking();
                boolean isSpacePressed = MinecraftClient.getInstance().options.jumpKey.isPressed();

                // Charged Jump Logic
                if (isSneaking && isSpacePressed) {
                    if (!chargedJump.isCharging()) {
                        chargedJump.startCharging();
                    }
                    chargedJump.tick(player);
                    player.setJumping(false);
                } else if (chargedJump.isCharging()) {
                    chargedJump.stopCharging(player);
                }
            }
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player) {
            // Prevent jumping if charging
            if (chargedJump.isCharging()) {
                ci.cancel();
                return;
            }

            // Existing Jump Boost Logic with Sprinting Check
            if (this.isSprinting() && Jump.jump) {
                float f = this.getYaw() * (float) (Math.PI / 180.0);
                float v = 1.5f;
                this.setVelocity(this.getVelocity().add(
                        (double) (-MathHelper.sin(f) * v),
                        0.0,
                        (double) (MathHelper.cos(f) * v)
                ));
            }

            // Trigger the jump animation
            if (entity.getWorld().isClient) {
                FecfsAnimations.playSpiderManJumpAnimation(player);
            }
        }
    }

}