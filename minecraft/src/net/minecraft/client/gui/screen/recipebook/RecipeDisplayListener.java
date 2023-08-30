package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeEntry;

@Environment(EnvType.CLIENT)
public interface RecipeDisplayListener {
	void onRecipesDisplayed(List<RecipeEntry<?>> recipes);
}
