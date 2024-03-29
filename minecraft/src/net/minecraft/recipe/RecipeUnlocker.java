package net.minecraft.recipe;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface RecipeUnlocker {
	void setLastRecipe(@Nullable RecipeEntry<?> recipe);

	@Nullable
	RecipeEntry<?> getLastRecipe();

	default void unlockLastRecipe(PlayerEntity player, List<ItemStack> ingredients) {
		RecipeEntry<?> recipeEntry = this.getLastRecipe();
		if (recipeEntry != null) {
			player.onRecipeCrafted(recipeEntry, ingredients);
			if (!recipeEntry.value().isIgnoredInRecipeBook()) {
				player.unlockRecipes(Collections.singleton(recipeEntry));
				this.setLastRecipe(null);
			}
		}
	}

	default boolean shouldCraftRecipe(World world, ServerPlayerEntity player, RecipeEntry<?> recipe) {
		if (!recipe.value().isIgnoredInRecipeBook() && world.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING) && !player.getRecipeBook().contains(recipe)) {
			return false;
		} else {
			this.setLastRecipe(recipe);
			return true;
		}
	}
}
