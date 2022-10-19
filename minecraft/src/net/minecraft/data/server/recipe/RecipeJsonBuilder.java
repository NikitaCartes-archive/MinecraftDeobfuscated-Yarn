package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;

public abstract class RecipeJsonBuilder {
	protected static CraftingRecipeCategory getCraftingCategory(RecipeCategory category) {
		return switch (category) {
			case BUILDING_BLOCKS -> CraftingRecipeCategory.BUILDING;
			case TOOLS, COMBAT -> CraftingRecipeCategory.EQUIPMENT;
			case REDSTONE -> CraftingRecipeCategory.REDSTONE;
			default -> CraftingRecipeCategory.MISC;
		};
	}

	protected abstract static class CraftingRecipeJsonProvider implements RecipeJsonProvider {
		private final CraftingRecipeCategory craftingCategory;

		protected CraftingRecipeJsonProvider(CraftingRecipeCategory craftingCategory) {
			this.craftingCategory = craftingCategory;
		}

		@Override
		public void serialize(JsonObject json) {
			json.addProperty("category", this.craftingCategory.asString());
		}
	}
}
