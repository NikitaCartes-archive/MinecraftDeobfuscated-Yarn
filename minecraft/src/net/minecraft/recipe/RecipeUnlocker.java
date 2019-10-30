package net.minecraft.recipe;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public interface RecipeUnlocker {
	void setLastRecipe(@Nullable Recipe<?> recipe);

	@Nullable
	Recipe<?> getLastRecipe();

	default void unlockLastRecipe(PlayerEntity player) {
		Recipe<?> recipe = this.getLastRecipe();
		if (recipe != null && !recipe.isIgnoredInRecipeBook()) {
			player.unlockRecipes(Collections.singleton(recipe));
			this.setLastRecipe(null);
		}
	}

	default boolean shouldCraftRecipe(World world, ServerPlayerEntity player, Recipe<?> recipe) {
		if (!recipe.isIgnoredInRecipeBook() && world.getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING) && !player.getRecipeBook().contains(recipe)) {
			return false;
		} else {
			this.setLastRecipe(recipe);
			return true;
		}
	}
}
