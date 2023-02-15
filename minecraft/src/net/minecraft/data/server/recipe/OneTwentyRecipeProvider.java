package net.minecraft.data.server.recipe;

import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
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
		offerHangingSignRecipe(exporter, Items.CHERRY_HANGING_SIGN, Blocks.STRIPPED_CHERRY_LOG);
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
		offerSmithingTrimRecipe(exporter, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerSmithingTrimRecipe(exporter, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerSmithingTrimRecipe(exporter, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerSmithingTrimRecipe(exporter, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerSmithingTrimRecipe(exporter, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerSmithingTrimRecipe(exporter, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerSmithingTrimRecipe(exporter, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerSmithingTrimRecipe(exporter, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerSmithingTrimRecipe(exporter, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerSmithingTrimRecipe(exporter, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerSmithingTrimRecipe(exporter, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
		offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_CHESTPLATE, RecipeCategory.COMBAT, Items.NETHERITE_CHESTPLATE);
		offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_LEGGINGS, RecipeCategory.COMBAT, Items.NETHERITE_LEGGINGS);
		offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_HELMET, RecipeCategory.COMBAT, Items.NETHERITE_HELMET);
		offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_BOOTS, RecipeCategory.COMBAT, Items.NETHERITE_BOOTS);
		offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_SWORD, RecipeCategory.COMBAT, Items.NETHERITE_SWORD);
		offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_AXE, RecipeCategory.TOOLS, Items.NETHERITE_AXE);
		offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_PICKAXE, RecipeCategory.TOOLS, Items.NETHERITE_PICKAXE);
		offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_HOE, RecipeCategory.TOOLS, Items.NETHERITE_HOE);
		offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_SHOVEL, RecipeCategory.TOOLS, Items.NETHERITE_SHOVEL);
		offerSmithingTemplateCopyingRecipe(exporter, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Items.NETHERRACK);
		offerSmithingTemplateCopyingRecipe(exporter, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
		offerSmithingTemplateCopyingRecipe(exporter, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SANDSTONE);
		offerSmithingTemplateCopyingRecipe(exporter, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
		offerSmithingTemplateCopyingRecipe(exporter, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.MOSSY_COBBLESTONE);
		offerSmithingTemplateCopyingRecipe(exporter, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
		offerSmithingTemplateCopyingRecipe(exporter, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.END_STONE);
		offerSmithingTemplateCopyingRecipe(exporter, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
		offerSmithingTemplateCopyingRecipe(exporter, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PRISMARINE);
		offerSmithingTemplateCopyingRecipe(exporter, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BLACKSTONE);
		offerSmithingTemplateCopyingRecipe(exporter, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.NETHERRACK);
		offerSmithingTemplateCopyingRecipe(exporter, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PURPUR_BLOCK);
		offerSingleOutputShapelessRecipe(exporter, Items.ORANGE_DYE, Blocks.TORCHFLOWER, "orange_dye");
		offerPlanksRecipe2(exporter, Blocks.CHERRY_PLANKS, ItemTags.CHERRY_LOGS, 4);
		offerBarkBlockRecipe(exporter, Blocks.CHERRY_WOOD, Blocks.CHERRY_LOG);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG);
		offerBoatRecipe(exporter, Items.CHERRY_BOAT, Blocks.CHERRY_PLANKS);
		offerChestBoatRecipe(exporter, Items.CHERRY_CHEST_BOAT, Items.CHERRY_BOAT);
		offerShapelessRecipe(exporter, Items.PINK_DYE, Items.PINK_PETALS, "pink_dye", 1);
		ComplexRecipeJsonBuilder.create(RecipeSerializer.CRAFTING_DECORATED_POT).offerTo(exporter, "decorated_pot");
	}
}
