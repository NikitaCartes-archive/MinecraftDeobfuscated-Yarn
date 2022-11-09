/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.recipe;

import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyRecipeProvider
extends RecipeProvider {
    public OneTwentyRecipeProvider(DataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    protected void generate(Consumer<RecipeJsonProvider> exporter) {
        OneTwentyRecipeProvider.generateFamilies(exporter, FeatureSet.of(FeatureFlags.UPDATE_1_20));
        OneTwentyRecipeProvider.offerCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Items.BAMBOO);
        OneTwentyRecipeProvider.offerPlanksRecipe(exporter, Blocks.BAMBOO_PLANKS, ItemTags.BAMBOO_BLOCKS, 2);
        OneTwentyRecipeProvider.offerMosaicRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_SLAB);
        OneTwentyRecipeProvider.offerBoatRecipe(exporter, Items.BAMBOO_RAFT, Blocks.BAMBOO_PLANKS);
        OneTwentyRecipeProvider.offerChestBoatRecipe(exporter, Items.BAMBOO_CHEST_RAFT, Items.BAMBOO_RAFT);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.OAK_HANGING_SIGN, Blocks.STRIPPED_OAK_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.SPRUCE_HANGING_SIGN, Blocks.STRIPPED_SPRUCE_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.BIRCH_HANGING_SIGN, Blocks.STRIPPED_BIRCH_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.JUNGLE_HANGING_SIGN, Blocks.STRIPPED_JUNGLE_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.ACACIA_HANGING_SIGN, Blocks.STRIPPED_ACACIA_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.DARK_OAK_HANGING_SIGN, Blocks.STRIPPED_DARK_OAK_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.MANGROVE_HANGING_SIGN, Blocks.STRIPPED_MANGROVE_LOG);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.BAMBOO_HANGING_SIGN, Items.STRIPPED_BAMBOO_BLOCK);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.CRIMSON_HANGING_SIGN, Blocks.STRIPPED_CRIMSON_STEM);
        OneTwentyRecipeProvider.offerHangingSignRecipe(exporter, Items.WARPED_HANGING_SIGN, Blocks.STRIPPED_WARPED_STEM);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_BOOKSHELF).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('X'), ItemTags.WOODEN_SLABS).pattern("###").pattern("XXX").pattern("###").criterion("has_book", OneTwentyRecipeProvider.conditionsFromItem(Items.BOOK)).offerTo(exporter);
    }
}

