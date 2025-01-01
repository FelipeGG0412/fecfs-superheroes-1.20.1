package com.fecfssuperheroes.power.custom;

import com.fecfssuperheroes.power.Power;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class Strength extends Power {
    private static final UUID STRENGTH_MODIFIER_UUID = UUID.fromString("44e69373-4d57-4dcd-92b0-4cb0a01f4ef6");

    public Strength(int amplifier) {
        super(amplifier);
    }

    @Override
    public void apply(PlayerEntity player) {
        if (player != null && !player.getWorld().isClient) {
            EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (attributeInstance != null) {
                EntityAttributeModifier existingModifier = attributeInstance.getModifier(STRENGTH_MODIFIER_UUID);
                if (existingModifier != null) {
                    attributeInstance.removeModifier(existingModifier);
                }
                double additionalDamage = this.getAmplifier();
                EntityAttributeModifier modifier = new EntityAttributeModifier(
                        STRENGTH_MODIFIER_UUID,
                        "strength_modifier",
                        additionalDamage,
                        EntityAttributeModifier.Operation.ADDITION
                );
                attributeInstance.addPersistentModifier(modifier);
            }
        }
    }

    @Override
    public void remove(PlayerEntity player) {
        if (player != null && !player.getWorld().isClient) {
            EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (attributeInstance != null) {
                EntityAttributeModifier existingModifier = attributeInstance.getModifier(STRENGTH_MODIFIER_UUID);
                if (existingModifier != null) {
                    attributeInstance.removeModifier(existingModifier);
                    // Debug message
                    System.out.println("Removed strength modifier");
                }
            }
        }
    }
}
