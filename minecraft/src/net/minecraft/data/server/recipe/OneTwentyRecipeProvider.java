package net.minecraft.data.server.recipe;

import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyRecipeProvider extends RecipeProvider {
	public OneTwentyRecipeProvider(DataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	protected void generate(Consumer<RecipeJsonProvider> exporter) {
		generateFamilies(exporter, FeatureSet.of(FeatureFlags.UPDATE_1_20));
		offerCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Items.BAMBOO);
		offerPlanksRecipe(exporter, Blocks.BAMBOO_PLANKS, ItemTags.BAMBOO_BLOCKS, 2);
		offerMosaicRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_SLAB);
		offerBoatRecipe(exporter, Items.BAMBOO_RAFT, Blocks.BAMBOO_PLANKS);
		offerChestBoatRecipe(exporter, Items.BAMBOO_CHEST_RAFT, Items.BAMBOO_RAFT);
		offerHangingSignRecipe(exporter, Items.OAK_HANGING_SIGN, Blocks.STRIPPED_OAK_LOG);
		offerHangingSignRecipe(exporter, Items.SPRUCE_HANGING_SIGN, Blocks.STRIPPED_SPRUCE_LOG);
		offerHangingSignRecipe(exporter, Items.BIRCH_HANGING_SIGN, Blocks.STRIPPED_BIRCH_LOG);
		offerHangingSignRecipe(exporter, Items.JUNGLE_HANGING_SIGN, Blocks.STRIPPED_JUNGLE_LOG);
		offerHangingSignRecipe(exporter, Items.ACACIA_HANGING_SIGN, Blocks.STRIPPED_ACACIA_LOG);
		offerHangingSignRecipe(exporter, Items.DARK_OAK_HANGING_SIGN, Blocks.STRIPPED_DARK_OAK_LOG);
		offerHangingSignRecipe(exporter, Items.MANGROVE_HANGING_SIGN, Blocks.STRIPPED_MANGROVE_LOG);
		offerHangingSignRecipe(exporter, Items.BAMBOO_HANGING_SIGN, Items.STRIPPED_BAMBOO_BLOCK);
		offerHangingSignRecipe(exporter, Items.CRIMSON_HANGING_SIGN, Blocks.STRIPPED_CRIMSON_STEM);
		offerHangingSignRecipe(exporter, Items.WARPED_HANGING_SIGN, Blocks.STRIPPED_WARPED_STEM);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_BOOKSHELF)
			.input('#', ItemTags.PLANKS)
			.input('X', ItemTags.WOODEN_SLABS)
			.pattern("###")
			.pattern("XXX")
			.pattern("###")
			.criterion("has_book", conditionsFromItem(Items.BOOK))
			.offerTo(exporter);
	}
}
