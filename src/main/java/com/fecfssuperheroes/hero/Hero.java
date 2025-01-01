package com.fecfssuperheroes.hero;

import com.fecfssuperheroes.ability.Ability;
import com.fecfssuperheroes.power.Power;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

import java.util.*;

public class Hero {
    private final String name;
    private final String nameKey;
    private final Map<String, Power> powers;
    private final Map<KeyBinding, Ability> abilities;
    private final Set<TagKey<Item>> armorTags;
    private final List<Ability> passiveAbilities = new ArrayList<>();

    private final int tier;
    private final boolean isAlt;
    private final List<Hero> alternates;

    private Hero(String name, String nameKey, int tier, boolean isAlt, List<Hero> alternates,
                 Map<String, Power> powers, Map<KeyBinding, Ability> abilities, Set<TagKey<Item>> armorTags) {
        this.name = name;
        this.nameKey = nameKey;
        this.powers = powers;
        this.abilities = abilities;
        this.armorTags = armorTags != null ? armorTags : new HashSet<>();
        this.tier = tier;
        this.isAlt = isAlt;
        this.alternates = alternates != null ? alternates : new ArrayList<>();

        if (isAlt && alternates != null && !alternates.isEmpty()) {
            Hero mainHero = alternates.get(0);
            copyMainPowersAndAbilities(mainHero);
        }
    }
    public void addPassiveAbility(Ability ability) {
        this.passiveAbilities.add(ability);
    }

    public List<Ability> getPassiveAbilities() {
        return passiveAbilities;
    }

    // Apply all abilities (active and passive)
    public void updateAbilities(PlayerEntity player) {
        // Update active abilities
        for (Ability ability : abilities.values()) {
            ability.update(player);
        }
        // Update passive abilities
        for (Ability ability : passiveAbilities) {
            ability.update(player);
        }
    }

    private void copyMainPowersAndAbilities(Hero mainHero) {
        this.powers.putAll(mainHero.powers);
        this.abilities.putAll(mainHero.abilities);
    }

    public static Builder create() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Map<KeyBinding, Ability> getAbilities() {
        return abilities;
    }

    public int getTier() {
        return tier;
    }

    public Map<String, Power> getPowers() {
        return powers;
    }

    public List<Hero> getAlternates() {
        return alternates;
    }

    public Set<TagKey<Item>> getArmorTags() {
        return armorTags;
    }

    public String getNameKey() {
        return nameKey;
    }

    public boolean isWearingFullSet(PlayerEntity player) {
        if (player == null) return false;
        Iterable<ItemStack> armorItems = player.getArmorItems();
        int piecesWorn = 0;

        for (ItemStack armorItem : armorItems) {
            boolean pieceMatches = false;
            for (TagKey<Item> tag : armorTags) {
                if (armorItem.isIn(tag)) {
                    pieceMatches = true;
                    break;
                }
            }
            if (pieceMatches) {
                piecesWorn++;
            }
        }
        return piecesWorn == 4;
    }

    public void applyPowers(PlayerEntity player) {
        for (Power power : powers.values()) {
            power.apply(player);
        }
    }


    public void removePowers(PlayerEntity player) {
        for (Power power : powers.values()) {
            power.remove(player);
        }
    }


    public static class Builder {
        private String name;
        private String nameKey;
        private int tier;
        private boolean isAlt;
        private List<Hero> alternates = new ArrayList<>();
        private final Map<String, Power> powers = new HashMap<>();
        private final Map<KeyBinding, Ability> abilities = new HashMap<>();
        private final Set<TagKey<Item>> armorTags = new HashSet<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder nameKey(String nameKey) {
            this.nameKey = nameKey;
            return this;
        }

        public Builder addPower(String key, Power power) {
            this.powers.put(key, power);
            return this;
        }

        public Builder setAbility(KeyBinding keyBinding, Ability ability) {
            this.abilities.put(keyBinding, ability);
            return this; // Ensure the builder is returned
        }

        public Builder addArmorTag(TagKey<Item> tag) {
            this.armorTags.add(tag);
            return this;
        }

        public Builder setTier(int tier) {
            this.tier = tier;
            return this;
        }

        public Builder setAlts(boolean hasAlts, List<Hero> alternates) {
            if (hasAlts) {
                this.alternates = alternates;
            }
            return this;
        }

        public Builder isAlt() {
            this.isAlt = true;
            return this;
        }

        public Hero build() {
            return new Hero(name, nameKey, tier, isAlt, alternates, powers, abilities, armorTags);
        }
    }
}
