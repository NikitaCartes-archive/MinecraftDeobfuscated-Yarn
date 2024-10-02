package net.minecraft.client.recipebook;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.registry.RegistryKey;

@Environment(EnvType.CLIENT)
public class ClientRecipeManager implements RecipeManager {
	private final Map<RegistryKey<RecipePropertySet>, RecipePropertySet> propertySets;
	private final CuttingRecipeDisplay.Grouping<StonecuttingRecipe> recipes;

	public ClientRecipeManager(Map<RegistryKey<RecipePropertySet>, RecipePropertySet> propertySets, CuttingRecipeDisplay.Grouping<StonecuttingRecipe> recipes) {
		this.propertySets = propertySets;
		this.recipes = recipes;
	}

	@Override
	public RecipePropertySet getPropertySet(RegistryKey<RecipePropertySet> key) {
		return (RecipePropertySet)this.propertySets.getOrDefault(key, RecipePropertySet.EMPTY);
	}

	@Override
	public CuttingRecipeDisplay.Grouping<StonecuttingRecipe> getStonecutterRecipes() {
		return this.recipes;
	}
}
