package net.minecraft.data.server.recipe;

import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public interface CraftingRecipeJsonBuilder {
	Identifier ROOT = Identifier.method_60656("recipes/root");

	CraftingRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion);

	CraftingRecipeJsonBuilder group(@Nullable String group);

	Item getOutputItem();

	void offerTo(RecipeExporter exporter, Identifier recipeId);

	default void offerTo(RecipeExporter exporter) {
		this.offerTo(exporter, getItemId(this.getOutputItem()));
	}

	default void offerTo(RecipeExporter exporter, String recipePath) {
		Identifier identifier = getItemId(this.getOutputItem());
		Identifier identifier2 = Identifier.method_60654(recipePath);
		if (identifier2.equals(identifier)) {
			throw new IllegalStateException("Recipe " + recipePath + " should remove its 'save' argument as it is equal to default one");
		} else {
			this.offerTo(exporter, identifier2);
		}
	}

	static Identifier getItemId(ItemConvertible item) {
		return Registries.ITEM.getId(item.asItem());
	}

	static CraftingRecipeCategory toCraftingCategory(RecipeCategory category) {
		return switch (category) {
			case BUILDING_BLOCKS -> CraftingRecipeCategory.BUILDING;
			case TOOLS, COMBAT -> CraftingRecipeCategory.EQUIPMENT;
			case REDSTONE -> CraftingRecipeCategory.REDSTONE;
			default -> CraftingRecipeCategory.MISC;
		};
	}
}
