package net.minecraft.component.type;

/**
 * Contains all the default food components used in vanilla food items.
 */
public class FoodComponents {
	public static final FoodComponent APPLE = new FoodComponent.Builder().nutrition(4).saturationModifier(0.3F).build();
	public static final FoodComponent BAKED_POTATO = new FoodComponent.Builder().nutrition(5).saturationModifier(0.6F).build();
	public static final FoodComponent BEEF = new FoodComponent.Builder().nutrition(3).saturationModifier(0.3F).build();
	public static final FoodComponent BEETROOT = new FoodComponent.Builder().nutrition(1).saturationModifier(0.6F).build();
	public static final FoodComponent BEETROOT_SOUP = createStew(6).build();
	public static final FoodComponent BREAD = new FoodComponent.Builder().nutrition(5).saturationModifier(0.6F).build();
	public static final FoodComponent CARROT = new FoodComponent.Builder().nutrition(3).saturationModifier(0.6F).build();
	public static final FoodComponent CHICKEN = new FoodComponent.Builder().nutrition(2).saturationModifier(0.3F).build();
	public static final FoodComponent CHORUS_FRUIT = new FoodComponent.Builder().nutrition(4).saturationModifier(0.3F).alwaysEdible().build();
	public static final FoodComponent COD = new FoodComponent.Builder().nutrition(2).saturationModifier(0.1F).build();
	public static final FoodComponent COOKED_BEEF = new FoodComponent.Builder().nutrition(8).saturationModifier(0.8F).build();
	public static final FoodComponent COOKED_CHICKEN = new FoodComponent.Builder().nutrition(6).saturationModifier(0.6F).build();
	public static final FoodComponent COOKED_COD = new FoodComponent.Builder().nutrition(5).saturationModifier(0.6F).build();
	public static final FoodComponent COOKED_MUTTON = new FoodComponent.Builder().nutrition(6).saturationModifier(0.8F).build();
	public static final FoodComponent COOKED_PORKCHOP = new FoodComponent.Builder().nutrition(8).saturationModifier(0.8F).build();
	public static final FoodComponent COOKED_RABBIT = new FoodComponent.Builder().nutrition(5).saturationModifier(0.6F).build();
	public static final FoodComponent COOKED_SALMON = new FoodComponent.Builder().nutrition(6).saturationModifier(0.8F).build();
	public static final FoodComponent COOKIE = new FoodComponent.Builder().nutrition(2).saturationModifier(0.1F).build();
	public static final FoodComponent DRIED_KELP = new FoodComponent.Builder().nutrition(1).saturationModifier(0.3F).build();
	public static final FoodComponent ENCHANTED_GOLDEN_APPLE = new FoodComponent.Builder().nutrition(4).saturationModifier(1.2F).alwaysEdible().build();
	public static final FoodComponent GOLDEN_APPLE = new FoodComponent.Builder().nutrition(4).saturationModifier(1.2F).alwaysEdible().build();
	public static final FoodComponent GOLDEN_CARROT = new FoodComponent.Builder().nutrition(6).saturationModifier(1.2F).build();
	public static final FoodComponent HONEY_BOTTLE = new FoodComponent.Builder().nutrition(6).saturationModifier(0.1F).alwaysEdible().build();
	public static final FoodComponent MELON_SLICE = new FoodComponent.Builder().nutrition(2).saturationModifier(0.3F).build();
	public static final FoodComponent MUSHROOM_STEW = createStew(6).build();
	public static final FoodComponent MUTTON = new FoodComponent.Builder().nutrition(2).saturationModifier(0.3F).build();
	public static final FoodComponent POISONOUS_POTATO = new FoodComponent.Builder().nutrition(2).saturationModifier(0.3F).build();
	public static final FoodComponent PORKCHOP = new FoodComponent.Builder().nutrition(3).saturationModifier(0.3F).build();
	public static final FoodComponent POTATO = new FoodComponent.Builder().nutrition(1).saturationModifier(0.3F).build();
	public static final FoodComponent PUFFERFISH = new FoodComponent.Builder().nutrition(1).saturationModifier(0.1F).build();
	public static final FoodComponent PUMPKIN_PIE = new FoodComponent.Builder().nutrition(8).saturationModifier(0.3F).build();
	public static final FoodComponent RABBIT = new FoodComponent.Builder().nutrition(3).saturationModifier(0.3F).build();
	public static final FoodComponent RABBIT_STEW = createStew(10).build();
	public static final FoodComponent ROTTEN_FLESH = new FoodComponent.Builder().nutrition(4).saturationModifier(0.1F).build();
	public static final FoodComponent SALMON = new FoodComponent.Builder().nutrition(2).saturationModifier(0.1F).build();
	public static final FoodComponent SPIDER_EYE = new FoodComponent.Builder().nutrition(2).saturationModifier(0.8F).build();
	public static final FoodComponent SUSPICIOUS_STEW = createStew(6).alwaysEdible().build();
	public static final FoodComponent SWEET_BERRIES = new FoodComponent.Builder().nutrition(2).saturationModifier(0.1F).build();
	public static final FoodComponent GLOW_BERRIES = new FoodComponent.Builder().nutrition(2).saturationModifier(0.1F).build();
	public static final FoodComponent TROPICAL_FISH = new FoodComponent.Builder().nutrition(1).saturationModifier(0.1F).build();

	private static FoodComponent.Builder createStew(int hunger) {
		return new FoodComponent.Builder().nutrition(hunger).saturationModifier(0.6F);
	}
}
