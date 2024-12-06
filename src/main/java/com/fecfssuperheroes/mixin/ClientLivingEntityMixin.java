package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.ability.ChargedJump;
import com.fecfssuperheroes.util.FecfsAnimations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class ClientLivingEntityMixin extends Entity implements Attackable {
    @Unique
    private ChargedJump chargedJump = new ChargedJump();

    public ClientLivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

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
        if(entity instanceof PlayerEntity player) {
            if (entity.getWorld().isClient) {
                FecfsAnimations.playSpiderManJumpAnimation(player);
            }
        }
    }
}
