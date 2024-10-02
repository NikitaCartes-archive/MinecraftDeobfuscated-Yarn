package net.minecraft.client.gui.screen.recipebook;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.display.RecipeDisplay;

@Environment(EnvType.CLIENT)
public interface RecipeBookProvider {
	void refreshRecipeBook();

	void onCraftFailed(RecipeDisplay display);
}
