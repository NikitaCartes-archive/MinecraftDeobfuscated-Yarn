package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
	public CampfireCookingRecipe(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		super(RecipeType.CAMPFIRE_COOKING, identifier, string, ingredient, itemStack, f, i);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(Blocks.field_17350);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.field_17347;
	}
}
