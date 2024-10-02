package net.minecraft.recipe.display;

import net.minecraft.registry.Registry;

public class RecipeDisplayBootstrap {
	public static RecipeDisplay.Serializer<?> registerAndGetDefault(Registry<RecipeDisplay.Serializer<?>> registry) {
		Registry.register(registry, "crafting_shapeless", ShapelessCraftingRecipeDisplay.SERIALIZER);
		Registry.register(registry, "crafting_shaped", ShapedCraftingRecipeDisplay.SERIALIZER);
		Registry.register(registry, "furnace", FurnaceRecipeDisplay.SERIALIZER);
		Registry.register(registry, "stonecutter", StonecutterRecipeDisplay.SERIALIZER);
		return Registry.register(registry, "smithing", SmithingRecipeDisplay.SERIALIZER);
	}
}
