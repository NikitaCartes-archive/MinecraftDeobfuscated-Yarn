package net.minecraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

/**
 * Contains all the default food components used in vanilla food items.
 */
public class FoodComponents {
	public static final FoodComponent APPLE = new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build();
	public static final FoodComponent BAKED_POTATO = new FoodComponent.Builder().hunger(5).saturationModifier(0.6F).build();
	public static final FoodComponent BEEF = new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build();
	public static final FoodComponent BEETROOT = new FoodComponent.Builder().hunger(1).saturationModifier(0.6F).build();
	public static final FoodComponent BEETROOT_SOUP = createStew(6).build();
	public static final FoodComponent BREAD = new FoodComponent.Builder().hunger(5).saturationModifier(0.6F).build();
	public static final FoodComponent CARROT = new FoodComponent.Builder().hunger(3).saturationModifier(0.6F).build();
	public static final FoodComponent CHICKEN = new FoodComponent.Builder()
		.hunger(2)
		.saturationModifier(0.3F)
		.statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.3F)
		.meat()
		.build();
	public static final FoodComponent CHORUS_FRUIT = new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).alwaysEdible().build();
	public static final FoodComponent COD = new FoodComponent.Builder().hunger(2).saturationModifier(0.1F).build();
	public static final FoodComponent COOKED_BEEF = new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build();
	public static final FoodComponent COOKED_CHICKEN = new FoodComponent.Builder().hunger(6).saturationModifier(0.6F).meat().build();
	public static final FoodComponent COOKED_COD = new FoodComponent.Builder().hunger(5).saturationModifier(0.6F).build();
	public static final FoodComponent COOKED_MUTTON = new FoodComponent.Builder().hunger(6).saturationModifier(0.8F).meat().build();
	public static final FoodComponent COOKED_PORKCHOP = new FoodComponent.Builder().hunger(8).saturationModifier(0.8F).meat().build();
	public static final FoodComponent COOKED_RABBIT = new FoodComponent.Builder().hunger(5).saturationModifier(0.6F).meat().build();
	public static final FoodComponent COOKED_SALMON = new FoodComponent.Builder().hunger(6).saturationModifier(0.8F).build();
	public static final FoodComponent COOKIE = new FoodComponent.Builder().hunger(2).saturationModifier(0.1F).build();
	public static final FoodComponent DRIED_KELP = new FoodComponent.Builder().hunger(1).saturationModifier(0.3F).snack().build();
	public static final FoodComponent ENCHANTED_GOLDEN_APPLE = new FoodComponent.Builder()
		.hunger(4)
		.saturationModifier(1.2F)
		.statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 3), 1.0F)
		.alwaysEdible()
		.build();
	public static final FoodComponent GOLDEN_APPLE = new FoodComponent.Builder()
		.hunger(4)
		.saturationModifier(1.2F)
		.statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0), 1.0F)
		.alwaysEdible()
		.build();
	public static final FoodComponent GOLDEN_CARROT = new FoodComponent.Builder().hunger(6).saturationModifier(1.2F).build();
	public static final FoodComponent HONEY_BOTTLE = new FoodComponent.Builder().hunger(6).saturationModifier(0.1F).build();
	public static final FoodComponent MELON_SLICE = new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build();
	public static final FoodComponent MUSHROOM_STEW = createStew(6).build();
	public static final FoodComponent MUTTON = new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).meat().build();
	public static final FoodComponent POISONOUS_POTATO = new FoodComponent.Builder()
		.hunger(2)
		.saturationModifier(0.3F)
		.statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.6F)
		.build();
	public static final FoodComponent PORKCHOP = new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build();
	public static final FoodComponent POTATO = new FoodComponent.Builder().hunger(1).saturationModifier(0.3F).build();
	public static final FoodComponent PUFFERFISH = new FoodComponent.Builder()
		.hunger(1)
		.saturationModifier(0.1F)
		.statusEffect(new StatusEffectInstance(StatusEffects.POISON, 1200, 1), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 300, 2), 1.0F)
		.statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0), 1.0F)
		.build();
	public static final FoodComponent PUMPKIN_PIE = new FoodComponent.Builder().hunger(8).saturationModifier(0.3F).build();
	public static final FoodComponent RABBIT = new FoodComponent.Builder().hunger(3).saturationModifier(0.3F).meat().build();
	public static final FoodComponent RABBIT_STEW = createStew(10).build();
	public static final FoodComponent ROTTEN_FLESH = new FoodComponent.Builder()
		.hunger(4)
		.saturationModifier(0.1F)
		.statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F)
		.meat()
		.build();
	public static final FoodComponent SALMON = new FoodComponent.Builder().hunger(2).saturationModifier(0.1F).build();
	public static final FoodComponent SPIDER_EYE = new FoodComponent.Builder()
		.hunger(2)
		.saturationModifier(0.8F)
		.statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 1.0F)
		.build();
	public static final FoodComponent SUSPICIOUS_STEW = createStew(6).alwaysEdible().build();
	public static final FoodComponent SWEET_BERRIES = new FoodComponent.Builder().hunger(2).saturationModifier(0.1F).build();
	public static final FoodComponent GLOW_BERRIES = new FoodComponent.Builder().hunger(2).saturationModifier(0.1F).build();
	public static final FoodComponent TROPICAL_FISH = new FoodComponent.Builder().hunger(1).saturationModifier(0.1F).build();

	private static FoodComponent.Builder createStew(int hunger) {
		return new FoodComponent.Builder().hunger(hunger).saturationModifier(0.6F);
	}
}
