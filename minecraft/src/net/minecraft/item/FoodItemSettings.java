package net.minecraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FoodItemSettings {
	public static final FoodItemSetting APPLE = new FoodItemSetting.Builder().hunger(4).saturationModifier(0.3F).build();
	public static final FoodItemSetting BAKED_POTATO = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6F).build();
	public static final FoodItemSetting BEEF = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.3F).wolfFood().build();
	public static final FoodItemSetting BEETROOT = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.6F).build();
	public static final FoodItemSetting BEETROOT_SOUP = create(6);
	public static final FoodItemSetting BREAD = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6F).build();
	public static final FoodItemSetting CARROT = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.6F).build();
	public static final FoodItemSetting CHICKEN = new FoodItemSetting.Builder()
		.hunger(2)
		.saturationModifier(0.3F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5903, 600, 0), 0.3F)
		.wolfFood()
		.build();
	public static final FoodItemSetting CHORUS_FRUIT = new FoodItemSetting.Builder().hunger(4).saturationModifier(0.3F).alwaysEdible().build();
	public static final FoodItemSetting COD = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1F).build();
	public static final FoodItemSetting COOKED_BEEF = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.8F).wolfFood().build();
	public static final FoodItemSetting COOKED_CHICKEN = new FoodItemSetting.Builder().hunger(6).saturationModifier(0.6F).wolfFood().build();
	public static final FoodItemSetting COOKED_COD = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6F).build();
	public static final FoodItemSetting COOKED_MUTTON = new FoodItemSetting.Builder().hunger(6).saturationModifier(0.8F).wolfFood().build();
	public static final FoodItemSetting COOKED_PORKCHOP = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.8F).wolfFood().build();
	public static final FoodItemSetting COOKED_RABBIT = new FoodItemSetting.Builder().hunger(5).saturationModifier(0.6F).wolfFood().build();
	public static final FoodItemSetting COOKED_SALMON = new FoodItemSetting.Builder().hunger(6).saturationModifier(0.8F).build();
	public static final FoodItemSetting COOKIE = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1F).build();
	public static final FoodItemSetting DRIED_KELP = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.3F).eatenFast().build();
	public static final FoodItemSetting ENCHANTED_GOLDEN_APPLE = new FoodItemSetting.Builder()
		.hunger(4)
		.saturationModifier(1.2F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5924, 400, 1), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5907, 6000, 0), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5918, 6000, 0), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5898, 2400, 3), 1.0F)
		.alwaysEdible()
		.build();
	public static final FoodItemSetting GOLDEN_APPLE = new FoodItemSetting.Builder()
		.hunger(4)
		.saturationModifier(1.2F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5924, 100, 1), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5898, 2400, 0), 1.0F)
		.alwaysEdible()
		.build();
	public static final FoodItemSetting GOLDEN_CARROT = new FoodItemSetting.Builder().hunger(6).saturationModifier(1.2F).build();
	public static final FoodItemSetting MELON_SLICE = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3F).build();
	public static final FoodItemSetting MUSHROOM_STEW = create(6);
	public static final FoodItemSetting MUTTON = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3F).wolfFood().build();
	public static final FoodItemSetting POISONOUS_POTATO = new FoodItemSetting.Builder()
		.hunger(2)
		.saturationModifier(0.3F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5899, 100, 0), 0.6F)
		.build();
	public static final FoodItemSetting PORKCHOP = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.3F).wolfFood().build();
	public static final FoodItemSetting POTATO = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.3F).build();
	public static final FoodItemSetting PUFFERFISH = new FoodItemSetting.Builder()
		.hunger(1)
		.saturationModifier(0.1F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5899, 1200, 3), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5903, 300, 2), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5916, 300, 1), 1.0F)
		.build();
	public static final FoodItemSetting PUMPKIN_PIE = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.3F).build();
	public static final FoodItemSetting RABBIT = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.3F).wolfFood().build();
	public static final FoodItemSetting RABBIT_STEW = create(10);
	public static final FoodItemSetting ROTTEN_FLESH = new FoodItemSetting.Builder()
		.hunger(4)
		.saturationModifier(0.1F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5903, 600, 0), 0.8F)
		.wolfFood()
		.build();
	public static final FoodItemSetting SALMON = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1F).build();
	public static final FoodItemSetting SPIDER_EYE = new FoodItemSetting.Builder()
		.hunger(2)
		.saturationModifier(0.8F)
		.statusEffect(new StatusEffectInstance(StatusEffects.field_5899, 100, 0), 1.0F)
		.build();
	public static final FoodItemSetting SUSPICIOUS_STEW = create(6);
	public static final FoodItemSetting SWEET_BERRIES = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1F).build();
	public static final FoodItemSetting TROPICAL_FISH = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.1F).build();

	private static FoodItemSetting create(int i) {
		return new FoodItemSetting.Builder().hunger(i).saturationModifier(0.6F).build();
	}
}
