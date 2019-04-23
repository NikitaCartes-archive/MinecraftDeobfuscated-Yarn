/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodItemSetting;

public class FoodItemSettings {
    public static final FoodItemSetting APPLE = new FoodItemSetting.Builder().hunger(4).saturationModifier(0.3f).build();
    public static final FoodItemSetting BAKED_POTATO = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6f).build();
    public static final FoodItemSetting BEEF = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.3f).wolfFood().build();
    public static final FoodItemSetting BEETROOT = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.6f).build();
    public static final FoodItemSetting BEETROOT_SOUP = FoodItemSettings.create(6);
    public static final FoodItemSetting BREAD = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6f).build();
    public static final FoodItemSetting CARROT = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.6f).build();
    public static final FoodItemSetting CHICKEN = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.3f).wolfFood().build();
    public static final FoodItemSetting CHORUS_FRUIT = new FoodItemSetting.Builder().hunger(4).saturationModifier(0.3f).alwaysEdible().build();
    public static final FoodItemSetting COD = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1f).build();
    public static final FoodItemSetting COOKED_BEEF = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.8f).wolfFood().build();
    public static final FoodItemSetting COOKED_CHICKEN = new FoodItemSetting.Builder().hunger(6).saturationModifier(0.6f).wolfFood().build();
    public static final FoodItemSetting COOKED_COD = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6f).build();
    public static final FoodItemSetting COOKED_MUTTON = new FoodItemSetting.Builder().hunger(6).saturationModifier(0.8f).wolfFood().build();
    public static final FoodItemSetting COOKED_PORKCHOP = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.8f).wolfFood().build();
    public static final FoodItemSetting COOKED_RABBIT = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6f).wolfFood().build();
    public static final FoodItemSetting COOKED_SALMON = new FoodItemSetting.Builder().hunger(6).saturationModifier(0.8f).build();
    public static final FoodItemSetting COOKIE = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1f).build();
    public static final FoodItemSetting DRIED_KELP = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.3f).eatenFast().build();
    public static final FoodItemSetting ENCHANTED_GOLDEN_APPLE = new FoodItemSetting.Builder().hunger(4).saturationModifier(1.2f).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 3), 1.0f).alwaysEdible().build();
    public static final FoodItemSetting GOLDEN_APPLE = new FoodItemSetting.Builder().hunger(4).saturationModifier(1.2f).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0), 1.0f).alwaysEdible().build();
    public static final FoodItemSetting GOLDEN_CARROT = new FoodItemSetting.Builder().hunger(6).saturationModifier(1.2f).build();
    public static final FoodItemSetting MELON_SLICE = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3f).build();
    public static final FoodItemSetting MUSHROOM_STEW = FoodItemSettings.create(6);
    public static final FoodItemSetting MUTTON = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3f).wolfFood().build();
    public static final FoodItemSetting POISONOUS_POTATO = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3f).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.6f).build();
    public static final FoodItemSetting PORKCHOP = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.3f).wolfFood().build();
    public static final FoodItemSetting POTATO = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.3f).build();
    public static final FoodItemSetting PUFFERFISH = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.1f).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 1200, 3), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 300, 2), 1.0f).statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 1), 1.0f).build();
    public static final FoodItemSetting PUMPKIN_PIE = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.3f).build();
    public static final FoodItemSetting RABBIT = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.3f).wolfFood().build();
    public static final FoodItemSetting RABBIT_STEW = FoodItemSettings.create(10);
    public static final FoodItemSetting ROTTEN_FLESH = new FoodItemSetting.Builder().hunger(4).saturationModifier(0.1f).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8f).wolfFood().build();
    public static final FoodItemSetting SALMON = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1f).build();
    public static final FoodItemSetting SPIDER_EYE = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.8f).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 1.0f).build();
    public static final FoodItemSetting SUSPICIOUS_STEW = FoodItemSettings.create(6);
    public static final FoodItemSetting SWEET_BERRIES = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1f).build();
    public static final FoodItemSetting TROPICAL_FISH = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.1f).build();

    private static FoodItemSetting create(int i) {
        return new FoodItemSetting.Builder().hunger(i).saturationModifier(0.6f).build();
    }
}

