package net.minecraft.data.server.recipe;

import java.util.function.Consumer;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

public class BundleRecipeProvider extends RecipeProvider {
	public BundleRecipeProvider(DataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	protected void generate(Consumer<RecipeJsonProvider> exporter) {
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
