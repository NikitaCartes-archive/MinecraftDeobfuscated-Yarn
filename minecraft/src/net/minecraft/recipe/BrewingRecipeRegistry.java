package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class BrewingRecipeRegistry {
	public static final int field_30942 = 20;
	private static final List<BrewingRecipeRegistry.Recipe<Potion>> POTION_RECIPES = Lists.<BrewingRecipeRegistry.Recipe<Potion>>newArrayList();
	private static final List<BrewingRecipeRegistry.Recipe<Item>> ITEM_RECIPES = Lists.<BrewingRecipeRegistry.Recipe<Item>>newArrayList();
	private static final List<Ingredient> POTION_TYPES = Lists.<Ingredient>newArrayList();
	private static final Predicate<ItemStack> POTION_TYPE_PREDICATE = stack -> {
		for (Ingredient ingredient : POTION_TYPES) {
			if (ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	};

	public static boolean isValidIngredient(ItemStack stack) {
		return isItemRecipeIngredient(stack) || isPotionRecipeIngredient(stack);
	}

	protected static boolean isItemRecipeIngredient(ItemStack stack) {
		for (BrewingRecipeRegistry.Recipe<Item> recipe : ITEM_RECIPES) {
			if (recipe.ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean isPotionRecipeIngredient(ItemStack stack) {
		for (BrewingRecipeRegistry.Recipe<Potion> recipe : POTION_RECIPES) {
			if (recipe.ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isBrewable(RegistryEntry<Potion> potion) {
		for (BrewingRecipeRegistry.Recipe<Potion> recipe : POTION_RECIPES) {
			if (recipe.to.matches(potion)) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasRecipe(ItemStack input, ItemStack ingredient) {
		return !POTION_TYPE_PREDICATE.test(input) ? false : hasItemRecipe(input, ingredient) || hasPotionRecipe(input, ingredient);
	}

	protected static boolean hasItemRecipe(ItemStack input, ItemStack ingredient) {
		for (BrewingRecipeRegistry.Recipe<Item> recipe : ITEM_RECIPES) {
			if (input.itemMatches(recipe.from) && recipe.ingredient.test(ingredient)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean hasPotionRecipe(ItemStack input, ItemStack ingredient) {
		Optional<RegistryEntry<Potion>> optional = input.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion();
		if (optional.isEmpty()) {
			return false;
		} else {
			for (BrewingRecipeRegistry.Recipe<Potion> recipe : POTION_RECIPES) {
				if (recipe.from.matches((RegistryEntry<Potion>)optional.get()) && recipe.ingredient.test(ingredient)) {
					return true;
				}
			}

			return false;
		}
	}

	public static ItemStack craft(ItemStack ingredient, ItemStack input) {
		if (input.isEmpty()) {
			return input;
		} else {
			Optional<RegistryEntry<Potion>> optional = input.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion();
			if (optional.isEmpty()) {
				return input;
			} else {
				for (BrewingRecipeRegistry.Recipe<Item> recipe : ITEM_RECIPES) {
					if (input.itemMatches(recipe.from) && recipe.ingredient.test(ingredient)) {
						return PotionContentsComponent.createStack(recipe.to.value(), (RegistryEntry<Potion>)optional.get());
					}
				}

				for (BrewingRecipeRegistry.Recipe<Potion> recipex : POTION_RECIPES) {
					if (recipex.from.matches((RegistryEntry<Potion>)optional.get()) && recipex.ingredient.test(ingredient)) {
						return PotionContentsComponent.createStack(input.getItem(), recipex.to);
					}
				}

				return input;
			}
		}
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
		registerPotionRecipe(Potions.WATER, Items.BREEZE_ROD, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.SLIME_BLOCK, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.STONE, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.COBWEB, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
		registerPotionRecipe(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
		registerPotionRecipe(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
		registerPotionRecipe(Potions.AWKWARD, Items.BREEZE_ROD, Potions.WIND_CHARGED);
		registerPotionRecipe(Potions.AWKWARD, Items.SLIME_BLOCK, Potions.OOZING);
		registerPotionRecipe(Potions.AWKWARD, Items.STONE, Potions.INFESTED);
		registerPotionRecipe(Potions.AWKWARD, Items.COBWEB, Potions.WEAVING);
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
			throw new IllegalArgumentException("Expected a potion, got: " + Registries.ITEM.getId(input));
		} else if (!(output instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registries.ITEM.getId(output));
		} else {
			ITEM_RECIPES.add(new BrewingRecipeRegistry.Recipe<>(input.getRegistryEntry(), Ingredient.ofItems(ingredient), output.getRegistryEntry()));
		}
	}

	private static void registerPotionType(Item item) {
		if (!(item instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registries.ITEM.getId(item));
		} else {
			POTION_TYPES.add(Ingredient.ofItems(item));
		}
	}

	private static void registerPotionRecipe(RegistryEntry<Potion> input, Item item, RegistryEntry<Potion> output) {
		POTION_RECIPES.add(new BrewingRecipeRegistry.Recipe<>(input, Ingredient.ofItems(item), output));
	}

	static record Recipe<T>(RegistryEntry<T> from, Ingredient ingredient, RegistryEntry<T> to) {
	}
}
