package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.recipebook.RecipeBookGui;

@Environment(EnvType.CLIENT)
public interface RecipeBookProvider {
	void refreshRecipeBook();

	RecipeBookGui getRecipeBookGui();
}
