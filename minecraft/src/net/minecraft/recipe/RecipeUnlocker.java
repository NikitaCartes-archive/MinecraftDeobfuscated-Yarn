package net.minecraft.recipe;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface RecipeUnlocker {
	void setLastRecipe(@Nullable Recipe<?> recipe);

	@Nullable
	Recipe<?> getLastRecipe();

	default void unlockLastRecipe(PlayerEntity playerEntity) {
		Recipe<?> recipe = this.getLastRecipe();
		if (recipe != null && !recipe.isIgnoredInRecipeBook()) {
			playerEntity.unlockRecipes(Collections.singleton(recipe));
			this.setLastRecipe(null);
		}
	}

	default boolean shouldCraftRecipe(World world, ServerPlayerEntity serverPlayerEntity, Recipe<?> recipe) {
		if (!recipe.isIgnoredInRecipeBook() && world.getGameRules().getBoolean("doLimitedCrafting") && !serverPlayerEntity.getRecipeBook().contains(recipe)) {
			return false;
		} else {
			this.setLastRecipe(recipe);
			return true;
		}
	}
}
