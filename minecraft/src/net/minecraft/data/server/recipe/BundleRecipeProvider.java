package net.minecraft.data.server.recipe;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

public class BundleRecipeProvider extends RecipeProvider {
	public BundleRecipeProvider(DataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(dataOutput, completableFuture);
	}

	@Override
	protected void generate(RecipeExporter exporter) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.BUNDLE)
			.input('#', Items.RABBIT_HIDE)
			.input('-', Items.STRING)
			.pattern("-#-")
			.pattern("# #")
			.pattern("###")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(exporter);
	}
}
