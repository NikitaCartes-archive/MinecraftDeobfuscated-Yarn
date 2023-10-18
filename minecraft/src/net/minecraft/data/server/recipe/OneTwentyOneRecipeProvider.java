package net.minecraft.data.server.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyOneRecipeProvider extends RecipeProvider {
	public OneTwentyOneRecipeProvider(DataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	protected void generate(RecipeExporter exporter) {
		generateFamilies(exporter, FeatureSet.of(FeatureFlags.UPDATE_1_21));
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.CRAFTER)
			.input('#', Items.IRON_INGOT)
			.input('C', Items.CRAFTING_TABLE)
			.input('R', Items.REDSTONE)
			.input('D', Items.DROPPER)
			.pattern("###")
			.pattern("#C#")
			.pattern("RDR")
			.criterion("has_dropper", conditionsFromItem(Items.DROPPER))
			.offerTo(exporter);
	}
}
