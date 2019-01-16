package net.minecraft.client.recipe.book;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;

@Environment(EnvType.CLIENT)
public interface RecipeDisplayListener {
	void onRecipesDisplayed(List<Recipe<?>> list);
}
