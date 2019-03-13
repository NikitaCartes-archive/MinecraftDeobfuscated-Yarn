package net.minecraft.recipe;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface RecipeUnlocker {
	void method_7662(@Nullable Recipe<?> recipe);

	@Nullable
	Recipe<?> method_7663();

	default void unlockLastRecipe(PlayerEntity playerEntity) {
		Recipe<?> recipe = this.method_7663();
		if (recipe != null && !recipe.isIgnoredInRecipeBook()) {
			playerEntity.unlockRecipes(Collections.singleton(recipe));
			this.method_7662(null);
		}
	}

	default boolean method_7665(World world, ServerPlayerEntity serverPlayerEntity, Recipe<?> recipe) {
		if (!recipe.isIgnoredInRecipeBook() && world.getGameRules().getBoolean("doLimitedCrafting") && !serverPlayerEntity.method_14253().contains(recipe)) {
			return false;
		} else {
			this.method_7662(recipe);
			return true;
		}
	}
}
