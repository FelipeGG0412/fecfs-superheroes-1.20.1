package com.fecfssuperheroes.ability;

import com.fecfssuperheroes.util.FecfsTags;
import com.fecfssuperheroes.util.HeroUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class ChargedJump {
    public static final int MAX_CHARGE_TIME = 50; // 5 seconds in ticks (20 ticks per second)
    public static final int MIN_CHARGE_TIME = 2;  // 1 second in ticks
    private static final float MAX_JUMP_VELOCITY = 3.5f; // Adjusted for 45 blocks jump height

    public static int chargeTime = 0;
    private boolean isCharging = false;

    private static boolean canChargeJump(PlayerEntity player) {
        return HeroUtil.isWearingSuit(player, FecfsTags.Items.SPIDERMAN) && !player.getAbilities().flying && player.isAlive() && !WebSwinging.isSwinging;
    }

    public void startCharging() {
        this.isCharging = true;
        this.chargeTime = 0;
    }

    public void stopCharging(PlayerEntity player) {
        if (this.isCharging) {
            this.isCharging = false;

            if (chargeTime >= MIN_CHARGE_TIME) {
                float chargePercentage = (float)(chargeTime - MIN_CHARGE_TIME) / (MAX_CHARGE_TIME - MIN_CHARGE_TIME);
                chargePercentage = Math.min(chargePercentage, 1.0f);
                float jumpVelocity = 0.42f + (MAX_JUMP_VELOCITY - 0.42f) * chargePercentage;
                Vec3d velocity = player.getVelocity();
                player.setVelocity(velocity.x, jumpVelocity, velocity.z);
                player.velocityModified = true;
            }
        }
        chargeTime = 0;
    }

    public void tick(PlayerEntity player) {
        if (this.isCharging && canChargeJump(player)) {
            if (chargeTime < MAX_CHARGE_TIME) {
                chargeTime++;
                updateChargeBar(player);
            }
        }
    }

    public boolean isCharging() {
        return this.isCharging;
    }

    private void updateChargeBar(PlayerEntity player) {
        float chargePercentage = (float)(chargeTime - MIN_CHARGE_TIME) / (MAX_CHARGE_TIME - MIN_CHARGE_TIME);
        chargePercentage = Math.max(0, Math.min(chargePercentage, 1.0f));
    }
}

