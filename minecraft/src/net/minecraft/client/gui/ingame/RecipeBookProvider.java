package net.minecraft.client.gui.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.container.RecipeBookGui;

@Environment(EnvType.CLIENT)
public interface RecipeBookProvider {
	void method_16891();

	RecipeBookGui getRecipeBookGui();
}
