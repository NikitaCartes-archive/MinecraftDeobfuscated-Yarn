package net.minecraft.recipe;

@FunctionalInterface
public interface RecipeInputProvider {
	void provideRecipeInputs(RecipeFinder finder);
}
