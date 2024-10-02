package net.minecraft.client.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

@Environment(EnvType.CLIENT)
public enum RecipeBookType implements RecipeBookCategory {
	CRAFTING(RecipeBookGroup.CRAFTING_EQUIPMENT, RecipeBookGroup.CRAFTING_BUILDING_BLOCKS, RecipeBookGroup.CRAFTING_MISC, RecipeBookGroup.CRAFTING_REDSTONE),
	FURNACE(RecipeBookGroup.FURNACE_FOOD, RecipeBookGroup.FURNACE_BLOCKS, RecipeBookGroup.FURNACE_MISC),
	BLAST_FURNACE(RecipeBookGroup.BLAST_FURNACE_BLOCKS, RecipeBookGroup.BLAST_FURNACE_MISC),
	SMOKER(RecipeBookGroup.SMOKER_FOOD);

	private final List<RecipeBookGroup> groups;

	private RecipeBookType(final RecipeBookGroup... groups) {
		this.groups = List.of(groups);
	}

	public List<RecipeBookGroup> getGroups() {
		return this.groups;
	}
}
