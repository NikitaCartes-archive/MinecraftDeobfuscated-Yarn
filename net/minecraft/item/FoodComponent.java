/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;

/**
 * Represents the components that make up the properties of a food item.
 */
public class FoodComponent {
    private final int hunger;
    private final float saturationModifier;
    private final boolean meat;
    private final boolean alwaysEdible;
    private final boolean snack;
    private final List<Pair<StatusEffectInstance, Float>> statusEffects;

    FoodComponent(int hunger, float saturationModifier, boolean meat, boolean alwaysEdible, boolean snack, List<Pair<StatusEffectInstance, Float>> statusEffects) {
        this.hunger = hunger;
        this.saturationModifier = saturationModifier;
        this.meat = meat;
        this.alwaysEdible = alwaysEdible;
        this.snack = snack;
        this.statusEffects = statusEffects;
    }

    /**
     * Gets the amount of hunger a food item will fill.
     * 
     * <p>One hunger is equivalent to half of a hunger bar icon.
     */
    public int getHunger() {
        return this.hunger;
    }

    /**
     * Gets the saturation modifier of a food item.
     * 
     * <p>This value is typically used to determine how long a player can sustain the current hunger value before the hunger is used.
     */
    public float getSaturationModifier() {
        return this.saturationModifier;
    }

    /**
     * Checks if a food item can be fed to dogs.
     */
    public boolean isMeat() {
        return this.meat;
    }

    /**
     * Checks if a food item can be eaten when the current hunger bar is full.
     */
    public boolean isAlwaysEdible() {
        return this.alwaysEdible;
    }

    /**
     * Checks if a food item is snack-like and is eaten quickly.
     */
    public boolean isSnack() {
        return this.snack;
    }

    /**
     * Gets a list of all status effect instances that may be applied when a food item is consumed.
     * 
     * <p>The first value in the pair is the status effect instance to be applied.
     * <p>The second value is the chance the status effect gets applied, on a scale between {@code 0.0F} and {@code 1.0F}.
     */
    public List<Pair<StatusEffectInstance, Float>> getStatusEffects() {
        return this.statusEffects;
    }

    public static class Builder {
        private int hunger;
        private float saturationModifier;
        private boolean meat;
        private boolean alwaysEdible;
        private boolean snack;
        private final List<Pair<StatusEffectInstance, Float>> statusEffects = Lists.newArrayList();

        public Builder hunger(int hunger) {
            this.hunger = hunger;
            return this;
        }

        public Builder saturationModifier(float saturationModifier) {
            this.saturationModifier = saturationModifier;
            return this;
        }

        public Builder meat() {
            this.meat = true;
            return this;
        }

        public Builder alwaysEdible() {
            this.alwaysEdible = true;
            return this;
        }

        public Builder snack() {
            this.snack = true;
            return this;
        }

        public Builder statusEffect(StatusEffectInstance effect, float chance) {
            this.statusEffects.add(Pair.of(effect, Float.valueOf(chance)));
            return this;
        }

        public FoodComponent build() {
            return new FoodComponent(this.hunger, this.saturationModifier, this.meat, this.alwaysEdible, this.snack, this.statusEffects);
        }
    }
}

