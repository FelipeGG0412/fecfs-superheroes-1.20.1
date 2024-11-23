package com.fecfssuperheroes.power.custom;

import com.fecfssuperheroes.power.Power;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;
import java.util.UUID;

public class Speed extends Power {
    private final EntityAttributeModifier modifier;

    public Speed(int amplifier) {
        super(amplifier);
        this.modifier = new EntityAttributeModifier(uuid, "speed_modifier", 0.1 * this.getAmplifier(),
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static final UUID uuid = UUID.fromString("8c7bc76e-0b4e-4bfd-963a-7e806dcd4b4e");

    @Override
    public void apply(PlayerEntity player) {
        if (player != null) {
            if (!player.getAttributes().hasModifierForAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, uuid) && player.isSprinting()) {
                Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                        .addTemporaryModifier(modifier);
            }
        }
    }

    @Override
    public void remove(PlayerEntity player) {
        if (player != null) {
            Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).removeModifier(uuid);
        }
    }
}
