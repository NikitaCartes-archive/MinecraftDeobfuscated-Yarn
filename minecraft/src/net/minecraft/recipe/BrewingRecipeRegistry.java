package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;

public class BrewingRecipeRegistry {
	private static final List<BrewingRecipeRegistry.Recipe<Potion>> POTION_RECIPES = Lists.<BrewingRecipeRegistry.Recipe<Potion>>newArrayList();
	private static final List<BrewingRecipeRegistry.Recipe<Item>> ITEM_RECIPES = Lists.<BrewingRecipeRegistry.Recipe<Item>>newArrayList();
	private static final List<Ingredient> POTION_TYPES = Lists.<Ingredient>newArrayList();
	private static final Predicate<ItemStack> POTION_TYPE_PREDICATE = itemStack -> {
		for (Ingredient ingredient : POTION_TYPES) {
			if (ingredient.test(itemStack)) {
				return true;
			}
		}

		return false;
	};

	public static boolean isValidIngredient(ItemStack stack) {
		return isItemRecipeIngredient(stack) || isPotionRecipeIngredient(stack);
	}

	protected static boolean isItemRecipeIngredient(ItemStack stack) {
		int i = 0;

		for (int j = ITEM_RECIPES.size(); i < j; i++) {
			if (((BrewingRecipeRegistry.Recipe)ITEM_RECIPES.get(i)).ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean isPotionRecipeIngredient(ItemStack stack) {
		int i = 0;

		for (int j = POTION_RECIPES.size(); i < j; i++) {
			if (((BrewingRecipeRegistry.Recipe)POTION_RECIPES.get(i)).ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isBrewable(Potion potion) {
		int i = 0;

		for (int j = POTION_RECIPES.size(); i < j; i++) {
			if (((BrewingRecipeRegistry.Recipe)POTION_RECIPES.get(i)).output == potion) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasRecipe(ItemStack input, ItemStack ingredient) {
		return !POTION_TYPE_PREDICATE.test(input) ? false : hasItemRecipe(input, ingredient) || hasPotionRecipe(input, ingredient);
	}

	protected static boolean hasItemRecipe(ItemStack input, ItemStack ingredient) {
		Item item = input.getItem();
		int i = 0;

		for (int j = ITEM_RECIPES.size(); i < j; i++) {
			BrewingRecipeRegistry.Recipe<Item> recipe = (BrewingRecipeRegistry.Recipe<Item>)ITEM_RECIPES.get(i);
			if (recipe.input == item && recipe.ingredient.test(ingredient)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean hasPotionRecipe(ItemStack input, ItemStack ingredient) {
		Potion potion = PotionUtil.getPotion(input);
		int i = 0;

		for (int j = POTION_RECIPES.size(); i < j; i++) {
			BrewingRecipeRegistry.Recipe<Potion> recipe = (BrewingRecipeRegistry.Recipe<Potion>)POTION_RECIPES.get(i);
			if (recipe.input == potion && recipe.ingredient.test(ingredient)) {
				return true;
			}
		}

		return false;
	}

	public static ItemStack craft(ItemStack input, ItemStack ingredient) {
		if (!ingredient.isEmpty()) {
			Potion potion = PotionUtil.getPotion(ingredient);
			Item item = ingredient.getItem();
			int i = 0;

			for (int j = ITEM_RECIPES.size(); i < j; i++) {
				BrewingRecipeRegistry.Recipe<Item> recipe = (BrewingRecipeRegistry.Recipe<Item>)ITEM_RECIPES.get(i);
				if (recipe.input == item && recipe.ingredient.test(input)) {
					return PotionUtil.setPotion(new ItemStack(recipe.output), potion);
				}
			}

			i = 0;

			for (int jx = POTION_RECIPES.size(); i < jx; i++) {
				BrewingRecipeRegistry.Recipe<Potion> recipe = (BrewingRecipeRegistry.Recipe<Potion>)POTION_RECIPES.get(i);
				if (recipe.input == potion && recipe.ingredient.test(input)) {
					return PotionUtil.setPotion(new ItemStack(item), recipe.output);
				}
			}
		}

		return ingredient;
	}

	public static void registerDefaults() {
		registerPotionType(Items.POTION);
		registerPotionType(Items.SPLASH_POTION);
		registerPotionType(Items.LINGERING_POTION);
		registerItemRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
		registerItemRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
		registerPotionRecipe(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
		registerPotionRecipe(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
		registerPotionRecipe(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
		registerPotionRecipe(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
		registerPotionRecipe(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
		registerPotionRecipe(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
		registerPotionRecipe(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
		registerPotionRecipe(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
		registerPotionRecipe(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
		registerPotionRecipe(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
		registerPotionRecipe(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
		registerPotionRecipe(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
		registerPotionRecipe(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
		registerPotionRecipe(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
		registerPotionRecipe(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
		registerPotionRecipe(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
		registerPotionRecipe(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
		registerPotionRecipe(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
		registerPotionRecipe(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
		registerPotionRecipe(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
		registerPotionRecipe(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
		registerPotionRecipe(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
		registerPotionRecipe(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
		registerPotionRecipe(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
		registerPotionRecipe(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
		registerPotionRecipe(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
		registerPotionRecipe(Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
		registerPotionRecipe(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
		registerPotionRecipe(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
		registerPotionRecipe(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
		registerPotionRecipe(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
		registerPotionRecipe(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
		registerPotionRecipe(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
		registerPotionRecipe(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
		registerPotionRecipe(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
		registerPotionRecipe(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
		registerPotionRecipe(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
		registerPotionRecipe(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
		registerPotionRecipe(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
		registerPotionRecipe(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
		registerPotionRecipe(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
		registerPotionRecipe(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
		registerPotionRecipe(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
		registerPotionRecipe(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
		registerPotionRecipe(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
		registerPotionRecipe(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
		registerPotionRecipe(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
	}

	private static void registerItemRecipe(Item input, Item ingredient, Item output) {
		if (!(input instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(input));
		} else if (!(output instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(output));
		} else {
			ITEM_RECIPES.add(new BrewingRecipeRegistry.Recipe<>(input, Ingredient.ofItems(ingredient), output));
		}
	}

	private static void registerPotionType(Item item) {
		if (!(item instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(item));
		} else {
			POTION_TYPES.add(Ingredient.ofItems(item));
		}
	}

	private static void registerPotionRecipe(Potion input, Item item, Potion output) {
		POTION_RECIPES.add(new BrewingRecipeRegistry.Recipe<>(input, Ingredient.ofItems(item), output));
	}

	static class Recipe<T> {
		private final T input;
		private final Ingredient ingredient;
		private final T output;

		public Recipe(T object, Ingredient ingredient, T object2) {
			this.input = object;
			this.ingredient = ingredient;
			this.output = object2;
		}
	}
}
