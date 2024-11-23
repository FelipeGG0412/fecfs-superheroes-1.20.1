package com.fecfssuperheroes.mixin;

import com.fecfssuperheroes.util.PowerUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Unique
    private boolean isApplyingResistance = false;

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (!isApplyingResistance) {
            double resistance = PowerUtil.getResistancePercentage(player);
            float reducedAmount = (float) (amount * (1.0 - resistance / 100.0));

            isApplyingResistance = true;
            boolean result = player.damage(source, reducedAmount);
            isApplyingResistance = false;

            cir.setReturnValue(result);
        }
    }
}