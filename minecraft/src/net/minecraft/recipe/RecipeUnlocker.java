package net.minecraft.recipe;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface RecipeUnlocker {
	void setLastRecipe(@Nullable Recipe recipe);

	@Nullable
	Recipe getLastRecipe();

	default void unlockLastRecipe(PlayerEntity playerEntity) {
		Recipe recipe = this.getLastRecipe();
		if (recipe != null && !recipe.isIgnoredInRecipeBook()) {
			playerEntity.unlockRecipes(Lists.<Recipe>newArrayList(recipe));
			this.setLastRecipe(null);
		}
	}

	default boolean shouldCraftRecipe(World world, ServerPlayerEntity serverPlayerEntity, @Nullable Recipe recipe) {
		if (recipe == null
			|| !recipe.isIgnoredInRecipeBook() && world.getGameRules().getBoolean("doLimitedCrafting") && !serverPlayerEntity.getRecipeBook().contains(recipe)) {
			return false;
		} else {
			this.setLastRecipe(recipe);
			return true;
		}
	}
}
