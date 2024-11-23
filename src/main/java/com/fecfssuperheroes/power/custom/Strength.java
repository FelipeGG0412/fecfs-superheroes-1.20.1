package com.fecfssuperheroes.power.custom;

import com.fecfssuperheroes.power.Power;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;
import java.util.UUID;

public class Strength extends Power {
    private final EntityAttributeModifier modifier;

    public Strength(int amplifier) {
        super(amplifier);
        this.modifier = new EntityAttributeModifier(uuid, "strength_modifier", 3.5 * this.getAmplifier(),
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static final UUID uuid = UUID.fromString("44e69373-4d57-4dcd-92b0-4cb0a01f4ef6");

    @Override
    public void apply(PlayerEntity player) {
        if (player != null) {
            if (!player.getAttributes().hasModifierForAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, uuid)) {
                Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                        .addTemporaryModifier(modifier);

            }
        }
    }

    @Override
    public void remove(PlayerEntity player) {
        if (player != null) {
            Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(uuid);
        }
    }
}
