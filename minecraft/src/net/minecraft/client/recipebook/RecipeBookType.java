package net.minecraft.client.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

@Environment(EnvType.CLIENT)
public enum RecipeBookType implements RecipeBookGroup {
	CRAFTING(
		RecipeBookCategories.CRAFTING_EQUIPMENT,
		RecipeBookCategories.CRAFTING_BUILDING_BLOCKS,
		RecipeBookCategories.CRAFTING_MISC,
		RecipeBookCategories.CRAFTING_REDSTONE
	),
	FURNACE(RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC),
	BLAST_FURNACE(RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC),
	SMOKER(RecipeBookCategories.SMOKER_FOOD);

	private final List<RecipeBookCategory> categories;

	private RecipeBookType(final RecipeBookCategory... categories) {
		this.categories = List.of(categories);
	}

	public List<RecipeBookCategory> getCategories() {
		return this.categories;
	}
}
