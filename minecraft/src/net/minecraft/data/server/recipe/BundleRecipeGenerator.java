package net.minecraft.data.server.recipe;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

public class BundleRecipeGenerator extends RecipeGenerator {
	BundleRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
		super(wrapperLookup, recipeExporter);
	}

	@Override
	protected void generate() {
		this.createShaped(RecipeCategory.TOOLS, Items.BUNDLE)
			.input('-', Items.STRING)
			.input('#', Items.LEATHER)
			.pattern("-")
			.pattern("#")
			.criterion("has_string", this.conditionsFromItem(Items.STRING))
			.offerTo(this.exporter);
	}

	public static class Provider extends RecipeGenerator.RecipeProvider {
		public Provider(DataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(dataOutput, completableFuture);
		}

		@Override
		protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
			return new BundleRecipeGenerator(registries, exporter);
		}

		@Override
		public String getName() {
			return "Bundle Recipes";
		}
	}
}
