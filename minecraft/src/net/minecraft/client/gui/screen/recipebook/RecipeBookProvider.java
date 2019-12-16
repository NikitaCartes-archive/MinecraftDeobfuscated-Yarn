package net.minecraft.client.gui.screen.recipebook;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RecipeBookProvider {
	void refreshRecipeBook();

	RecipeBookWidget getRecipeBookWidget();
}
