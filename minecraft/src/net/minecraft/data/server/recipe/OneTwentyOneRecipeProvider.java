package net.minecraft.data.server.recipe;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public class OneTwentyOneRecipeProvider extends RecipeProvider {
	public OneTwentyOneRecipeProvider(DataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(dataOutput, completableFuture);
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
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_SLAB, Blocks.TUFF, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_STAIRS, Blocks.TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.TUFF_WALL, Blocks.TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF, Blocks.TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF, Blocks.TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_SLAB, Blocks.TUFF, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_STAIRS, Blocks.TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_TUFF_WALL, Blocks.TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICKS, Blocks.TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_SLAB, Blocks.TUFF, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_STAIRS, Blocks.TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.TUFF_BRICK_WALL, Blocks.TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_SLAB, Blocks.POLISHED_TUFF, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_STAIRS, Blocks.POLISHED_TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_TUFF_WALL, Blocks.POLISHED_TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICKS, Blocks.POLISHED_TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_SLAB, Blocks.POLISHED_TUFF, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_STAIRS, Blocks.POLISHED_TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.TUFF_BRICK_WALL, Blocks.POLISHED_TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.POLISHED_TUFF);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_SLAB, Blocks.TUFF_BRICKS, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_STAIRS, Blocks.TUFF_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.TUFF_BRICK_WALL, Blocks.TUFF_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.TUFF_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_COPPER, Blocks.COPPER_BLOCK, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CHISELED_COPPER, Blocks.EXPOSED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CHISELED_COPPER, Blocks.WEATHERED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CHISELED_COPPER, Blocks.OXIDIZED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CHISELED_COPPER, Blocks.WAXED_COPPER_BLOCK, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_COPPER, Blocks.CUT_COPPER, 1);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CHISELED_COPPER, Blocks.EXPOSED_CUT_COPPER, 1);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CHISELED_COPPER, Blocks.WEATHERED_CUT_COPPER, 1);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CHISELED_COPPER, Blocks.OXIDIZED_CUT_COPPER, 1);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CHISELED_COPPER, Blocks.WAXED_CUT_COPPER, 1);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, 1);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, 1);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER, 1);
		offerGrateRecipe(exporter, Blocks.COPPER_GRATE, Blocks.COPPER_BLOCK);
		offerGrateRecipe(exporter, Blocks.EXPOSED_COPPER_GRATE, Blocks.EXPOSED_COPPER);
		offerGrateRecipe(exporter, Blocks.WEATHERED_COPPER_GRATE, Blocks.WEATHERED_COPPER);
		offerGrateRecipe(exporter, Blocks.OXIDIZED_COPPER_GRATE, Blocks.OXIDIZED_COPPER);
		offerGrateRecipe(exporter, Blocks.WAXED_COPPER_GRATE, Blocks.WAXED_COPPER_BLOCK);
		offerGrateRecipe(exporter, Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER);
		offerGrateRecipe(exporter, Blocks.WAXED_WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER);
		offerGrateRecipe(exporter, Blocks.WAXED_OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER);
		offerBulbRecipe(exporter, Blocks.COPPER_BULB, Blocks.COPPER_BLOCK);
		offerBulbRecipe(exporter, Blocks.EXPOSED_COPPER_BULB, Blocks.EXPOSED_COPPER);
		offerBulbRecipe(exporter, Blocks.WEATHERED_COPPER_BULB, Blocks.WEATHERED_COPPER);
		offerBulbRecipe(exporter, Blocks.OXIDIZED_COPPER_BULB, Blocks.OXIDIZED_COPPER);
		offerBulbRecipe(exporter, Blocks.WAXED_COPPER_BULB, Blocks.WAXED_COPPER_BLOCK);
		offerBulbRecipe(exporter, Blocks.WAXED_EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER);
		offerBulbRecipe(exporter, Blocks.WAXED_WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER);
		offerBulbRecipe(exporter, Blocks.WAXED_OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COPPER_GRATE, Blocks.COPPER_BLOCK, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_COPPER_GRATE, Blocks.EXPOSED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_COPPER_GRATE, Blocks.WEATHERED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_COPPER_GRATE, Blocks.OXIDIZED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_COPPER_GRATE, Blocks.WAXED_COPPER_BLOCK, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER, 4);
		streamSmithingTemplates().forEach(template -> offerSmithingTrimRecipe(exporter, template.template(), template.id()));
		offerSmithingTemplateCopyingRecipe(exporter, Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BREEZE_ROD);
		offerSmithingTemplateCopyingRecipe(exporter, Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COPPER_BLOCK);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.WIND_CHARGE, 4)
			.input(Items.BREEZE_ROD)
			.criterion("has_breeze_rod", conditionsFromItem(Items.BREEZE_ROD))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.MACE, 1)
			.input('I', Items.BREEZE_ROD)
			.input('#', Blocks.HEAVY_CORE)
			.pattern(" # ")
			.pattern(" I ")
			.criterion("has_breeze_rod", conditionsFromItem(Items.BREEZE_ROD))
			.criterion("has_heavy_core", conditionsFromItem(Blocks.HEAVY_CORE))
			.offerTo(exporter);
		offerWaxingRecipes(exporter, FeatureSet.of(FeatureFlags.UPDATE_1_21));
	}

	public static Stream<VanillaRecipeProvider.SmithingTemplate> streamSmithingTemplates() {
		return Stream.of(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE)
			.map(item -> new VanillaRecipeProvider.SmithingTemplate(item, new Identifier(getItemPath(item) + "_smithing_trim")));
	}
}
