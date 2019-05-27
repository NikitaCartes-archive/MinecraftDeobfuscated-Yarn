/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class FoodComponents {
    public static final FoodComponent APPLE = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f).build();
    public static final FoodComponent BAKED_POTATO = new FoodComponent.Builder().hunger(5).saturationModifier(0.6f).build();
    public static final FoodComponent BEEF = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).wolfFood().build();
    public static final FoodComponent BEETROOT = new FoodComponent.Builder().hunger(1).saturationModifier(0.6f).build();
    public static final FoodComponent BEETROOT_SOUP = FoodComponents.create(6);
    public static final FoodComponent BREAD = new FoodComponent.Builder().hunger(5).saturationModifier(0.6f).build();
    public static final FoodComponent CARROT = new FoodComponent.Builder().hunger(3).saturationModifier(0.6f).build();
    public static final FoodComponent CHICKEN = new FoodComponent.Builder().hunger(2).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.3f).wolfFood().build();
    public static final FoodComponent CHORUS_FRUIT = new FoodComponent.Builder().hunger(4).saturationModifier(0.3f).alwaysEdible().build();
    public static final FoodComponent COD = new FoodComponent.Builder().hunger(2).saturationModifier(0.1f).build();
    public static final FoodComponent COOKED_BEEF = new FoodComponent.Builder().hunger(8).saturationModifier(0.8f).wolfFood().build();
    public static final FoodComponent COOKED_CHICKEN = new FoodComponent.Builder().hunger(6).saturationModifier(0.6f).wolfFood().build();
    public static final FoodComponent COOKED_COD = new FoodComponent.Builder().hunger(5).saturationModifier(0.6f).build();
    public static final FoodComponent COOKED_MUTTON = new FoodComponent.Builder().hunger(6).saturationModifier(0.8f).wolfFood().build();
    public static final FoodComponent COOKED_PORKCHOP = new FoodComponent.Builder().hunger(8).saturationModifier(0.8f).wolfFood().build();
    public static final FoodComponent COOKED_RABBIT = new FoodComponent.Builder().hunger(5).saturationModifier(0.6f).wolfFood().build();
    public static final FoodComponent COOKED_SALMON = new FoodComponent.Builder().hunger(6).saturationModifier(0.8f).build();
    public static final FoodComponent COOKIE = new FoodComponent.Builder().hunger(2).saturationModifier(0.1f).build();
    public static final FoodComponent DRIED_KELP = new FoodComponent.Builder().hunger(1).saturationModifier(0.3f).snack().build();
    public static final FoodComponent ENCHANTED_GOLDEN_APPLE = new FoodComponent.Builder().hunger(4).saturationModifier(1.2f).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 3), 1.0f).alwaysEdible().build();
    public static final FoodComponent GOLDEN_APPLE = new FoodComponent.Builder().hunger(4).saturationModifier(1.2f).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0), 1.0f).alwaysEdible().build();
    public static final FoodComponent GOLDEN_CARROT = new FoodComponent.Builder().hunger(6).saturationModifier(1.2f).build();
    public static final FoodComponent MELON_SLICE = new FoodComponent.Builder().hunger(2).saturationModifier(0.3f).build();
    public static final FoodComponent MUSHROOM_STEW = FoodComponents.create(6);
    public static final FoodComponent MUTTON = new FoodComponent.Builder().hunger(2).saturationModifier(0.3f).wolfFood().build();
    public static final FoodComponent POISONOUS_POTATO = new FoodComponent.Builder().hunger(2).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.6f).build();
    public static final FoodComponent PORKCHOP = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).wolfFood().build();
    public static final FoodComponent POTATO = new FoodComponent.Builder().hunger(1).saturationModifier(0.3f).build();
    public static final FoodComponent PUFFERFISH = new FoodComponent.Builder().hunger(1).saturationModifier(0.1f).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 1200, 3), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 300, 2), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 1), 1.0f).build();
    public static final FoodComponent PUMPKIN_PIE = new FoodComponent.Builder().hunger(8).saturationModifier(0.3f).build();
    public static final FoodComponent RABBIT = new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).wolfFood().build();
    public static final FoodComponent RABBIT_STEW = FoodComponents.create(10);
    public static final FoodComponent ROTTEN_FLESH = new FoodComponent.Builder().hunger(4).saturationModifier(0.1f).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8f).wolfFood().build();
    public static final FoodComponent SALMON = new FoodComponent.Builder().hunger(2).saturationModifier(0.1f).build();
    public static final FoodComponent SPIDER_EYE = new FoodComponent.Builder().hunger(2).saturationModifier(0.8f).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 1.0f).build();
    public static final FoodComponent SUSPICIOUS_STEW = FoodComponents.create(6);
    public static final FoodComponent SWEET_BERRIES = new FoodComponent.Builder().hunger(2).saturationModifier(0.1f).build();
    public static final FoodComponent TROPICAL_FISH = new FoodComponent.Builder().hunger(1).saturationModifier(0.1f).build();

    private static FoodComponent create(int i) {
        return new FoodComponent.Builder().hunger(i).saturationModifier(0.6f).build();
    }
}

