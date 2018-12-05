package net.minecraft;

import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public interface class_1732 {
	void method_7662(@Nullable Recipe recipe);

	@Nullable
	Recipe method_7663();

	default void method_7664(PlayerEntity playerEntity) {
		Recipe recipe = this.method_7663();
		if (recipe != null && !recipe.isIgnoredInRecipeBook()) {
			playerEntity.method_7254(Lists.<Recipe>newArrayList(recipe));
			this.method_7662(null);
		}
	}

	default boolean method_7665(World world, ServerPlayerEntity serverPlayerEntity, @Nullable Recipe recipe) {
		if (recipe == null
			|| !recipe.isIgnoredInRecipeBook() && world.getGameRules().getBoolean("doLimitedCrafting") && !serverPlayerEntity.getRecipeBook().method_14878(recipe)) {
			return false;
		} else {
			this.method_7662(recipe);
			return true;
		}
	}
}
