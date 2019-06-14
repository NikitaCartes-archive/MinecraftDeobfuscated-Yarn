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
			if (ingredient.method_8093(itemStack)) {
				return true;
			}
		}

		return false;
	};

	public static boolean isValidIngredient(ItemStack itemStack) {
		return isItemRecipeIngredient(itemStack) || isPotionRecipeIngredient(itemStack);
	}

	protected static boolean isItemRecipeIngredient(ItemStack itemStack) {
		int i = 0;

		for (int j = ITEM_RECIPES.size(); i < j; i++) {
			if (((BrewingRecipeRegistry.Recipe)ITEM_RECIPES.get(i)).ingredient.method_8093(itemStack)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean isPotionRecipeIngredient(ItemStack itemStack) {
		int i = 0;

		for (int j = POTION_RECIPES.size(); i < j; i++) {
			if (((BrewingRecipeRegistry.Recipe)POTION_RECIPES.get(i)).ingredient.method_8093(itemStack)) {
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

	public static boolean hasRecipe(ItemStack itemStack, ItemStack itemStack2) {
		return !POTION_TYPE_PREDICATE.test(itemStack) ? false : hasItemRecipe(itemStack, itemStack2) || hasPotionRecipe(itemStack, itemStack2);
	}

	protected static boolean hasItemRecipe(ItemStack itemStack, ItemStack itemStack2) {
		Item item = itemStack.getItem();
		int i = 0;

		for (int j = ITEM_RECIPES.size(); i < j; i++) {
			BrewingRecipeRegistry.Recipe<Item> recipe = (BrewingRecipeRegistry.Recipe<Item>)ITEM_RECIPES.get(i);
			if (recipe.input == item && recipe.ingredient.method_8093(itemStack2)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean hasPotionRecipe(ItemStack itemStack, ItemStack itemStack2) {
		Potion potion = PotionUtil.getPotion(itemStack);
		int i = 0;

		for (int j = POTION_RECIPES.size(); i < j; i++) {
			BrewingRecipeRegistry.Recipe<Potion> recipe = (BrewingRecipeRegistry.Recipe<Potion>)POTION_RECIPES.get(i);
			if (recipe.input == potion && recipe.ingredient.method_8093(itemStack2)) {
				return true;
			}
		}

		return false;
	}

	public static ItemStack craft(ItemStack itemStack, ItemStack itemStack2) {
		if (!itemStack2.isEmpty()) {
			Potion potion = PotionUtil.getPotion(itemStack2);
			Item item = itemStack2.getItem();
			int i = 0;

			for (int j = ITEM_RECIPES.size(); i < j; i++) {
				BrewingRecipeRegistry.Recipe<Item> recipe = (BrewingRecipeRegistry.Recipe<Item>)ITEM_RECIPES.get(i);
				if (recipe.input == item && recipe.ingredient.method_8093(itemStack)) {
					return PotionUtil.setPotion(new ItemStack(recipe.output), potion);
				}
			}

			i = 0;

			for (int jx = POTION_RECIPES.size(); i < jx; i++) {
				BrewingRecipeRegistry.Recipe<Potion> recipe = (BrewingRecipeRegistry.Recipe<Potion>)POTION_RECIPES.get(i);
				if (recipe.input == potion && recipe.ingredient.method_8093(itemStack)) {
					return PotionUtil.setPotion(new ItemStack(item), recipe.output);
				}
			}
		}

		return itemStack2;
	}

	public static void registerDefaults() {
		registerPotionType(Items.field_8574);
		registerPotionType(Items.field_8436);
		registerPotionType(Items.field_8150);
		registerItemRecipe(Items.field_8574, Items.field_8054, Items.field_8436);
		registerItemRecipe(Items.field_8436, Items.field_8613, Items.field_8150);
		registerPotionRecipe(Potions.field_8991, Items.field_8597, Potions.field_8967);
		registerPotionRecipe(Potions.field_8991, Items.field_8070, Potions.field_8967);
		registerPotionRecipe(Potions.field_8991, Items.field_8073, Potions.field_8967);
		registerPotionRecipe(Potions.field_8991, Items.field_8183, Potions.field_8967);
		registerPotionRecipe(Potions.field_8991, Items.field_8680, Potions.field_8967);
		registerPotionRecipe(Potions.field_8991, Items.field_8479, Potions.field_8967);
		registerPotionRecipe(Potions.field_8991, Items.field_8135, Potions.field_8967);
		registerPotionRecipe(Potions.field_8991, Items.field_8601, Potions.field_8985);
		registerPotionRecipe(Potions.field_8991, Items.field_8725, Potions.field_8967);
		registerPotionRecipe(Potions.field_8991, Items.field_8790, Potions.field_8999);
		registerPotionRecipe(Potions.field_8999, Items.field_8071, Potions.field_8968);
		registerPotionRecipe(Potions.field_8968, Items.field_8725, Potions.field_8981);
		registerPotionRecipe(Potions.field_8968, Items.field_8711, Potions.field_8997);
		registerPotionRecipe(Potions.field_8981, Items.field_8711, Potions.field_9000);
		registerPotionRecipe(Potions.field_8997, Items.field_8725, Potions.field_9000);
		registerPotionRecipe(Potions.field_8999, Items.field_8135, Potions.field_8987);
		registerPotionRecipe(Potions.field_8987, Items.field_8725, Potions.field_8969);
		registerPotionRecipe(Potions.field_8999, Items.field_8073, Potions.field_8979);
		registerPotionRecipe(Potions.field_8979, Items.field_8725, Potions.field_8971);
		registerPotionRecipe(Potions.field_8979, Items.field_8601, Potions.field_8998);
		registerPotionRecipe(Potions.field_8979, Items.field_8711, Potions.field_8996);
		registerPotionRecipe(Potions.field_8971, Items.field_8711, Potions.field_8989);
		registerPotionRecipe(Potions.field_8996, Items.field_8725, Potions.field_8989);
		registerPotionRecipe(Potions.field_8996, Items.field_8601, Potions.field_8976);
		registerPotionRecipe(Potions.field_8999, Items.field_8090, Potions.field_8990);
		registerPotionRecipe(Potions.field_8990, Items.field_8725, Potions.field_8988);
		registerPotionRecipe(Potions.field_8990, Items.field_8601, Potions.field_8977);
		registerPotionRecipe(Potions.field_9005, Items.field_8711, Potions.field_8996);
		registerPotionRecipe(Potions.field_8983, Items.field_8711, Potions.field_8989);
		registerPotionRecipe(Potions.field_8999, Items.field_8479, Potions.field_9005);
		registerPotionRecipe(Potions.field_9005, Items.field_8725, Potions.field_8983);
		registerPotionRecipe(Potions.field_9005, Items.field_8601, Potions.field_8966);
		registerPotionRecipe(Potions.field_8999, Items.field_8323, Potions.field_8994);
		registerPotionRecipe(Potions.field_8994, Items.field_8725, Potions.field_9001);
		registerPotionRecipe(Potions.field_8999, Items.field_8597, Potions.field_8963);
		registerPotionRecipe(Potions.field_8963, Items.field_8601, Potions.field_8980);
		registerPotionRecipe(Potions.field_8963, Items.field_8711, Potions.field_9004);
		registerPotionRecipe(Potions.field_8980, Items.field_8711, Potions.field_8973);
		registerPotionRecipe(Potions.field_9004, Items.field_8601, Potions.field_8973);
		registerPotionRecipe(Potions.field_8982, Items.field_8711, Potions.field_9004);
		registerPotionRecipe(Potions.field_9002, Items.field_8711, Potions.field_9004);
		registerPotionRecipe(Potions.field_8972, Items.field_8711, Potions.field_8973);
		registerPotionRecipe(Potions.field_8999, Items.field_8680, Potions.field_8982);
		registerPotionRecipe(Potions.field_8982, Items.field_8725, Potions.field_9002);
		registerPotionRecipe(Potions.field_8982, Items.field_8601, Potions.field_8972);
		registerPotionRecipe(Potions.field_8999, Items.field_8070, Potions.field_8986);
		registerPotionRecipe(Potions.field_8986, Items.field_8725, Potions.field_9003);
		registerPotionRecipe(Potions.field_8986, Items.field_8601, Potions.field_8992);
		registerPotionRecipe(Potions.field_8999, Items.field_8183, Potions.field_8978);
		registerPotionRecipe(Potions.field_8978, Items.field_8725, Potions.field_8965);
		registerPotionRecipe(Potions.field_8978, Items.field_8601, Potions.field_8993);
		registerPotionRecipe(Potions.field_8991, Items.field_8711, Potions.field_8975);
		registerPotionRecipe(Potions.field_8975, Items.field_8725, Potions.field_8970);
		registerPotionRecipe(Potions.field_8999, Items.field_8614, Potions.field_8974);
		registerPotionRecipe(Potions.field_8974, Items.field_8725, Potions.field_8964);
	}

	private static void registerItemRecipe(Item item, Item item2, Item item3) {
		if (!(item instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(item));
		} else if (!(item3 instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(item3));
		} else {
			ITEM_RECIPES.add(new BrewingRecipeRegistry.Recipe<>(item, Ingredient.method_8091(item2), item3));
		}
	}

	private static void registerPotionType(Item item) {
		if (!(item instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(item));
		} else {
			POTION_TYPES.add(Ingredient.method_8091(item));
		}
	}

	private static void registerPotionRecipe(Potion potion, Item item, Potion potion2) {
		POTION_RECIPES.add(new BrewingRecipeRegistry.Recipe<>(potion, Ingredient.method_8091(item), potion2));
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
