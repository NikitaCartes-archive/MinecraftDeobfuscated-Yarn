package net.minecraft.data.server.recipe;

import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class WinterDropRecipeGenerator extends RecipeGenerator {
	public WinterDropRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
		super(wrapperLookup, recipeExporter);
	}

	@Override
	protected void generate() {
		this.generateFamilies(FeatureSet.of(FeatureFlags.WINTER_DROP));
		this.offerHangingSignRecipe(Items.PALE_OAK_HANGING_SIGN, Blocks.STRIPPED_PALE_OAK_LOG);
		this.offerPlanksRecipe2(Blocks.PALE_OAK_PLANKS, ItemTags.PALE_OAK_LOGS, 4);
		this.offerBarkBlockRecipe(Blocks.PALE_OAK_WOOD, Blocks.PALE_OAK_LOG);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_LOG);
		this.offerBoatRecipe(Items.PALE_OAK_BOAT, Blocks.PALE_OAK_PLANKS);
		this.offerChestBoatRecipe(Items.PALE_OAK_CHEST_BOAT, Items.PALE_OAK_BOAT);
		this.offerCarpetRecipe(Blocks.PALE_MOSS_CARPET, Blocks.PALE_MOSS_BLOCK);
	}

	public static class Provider extends RecipeGenerator.RecipeProvider {
		public Provider(DataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(dataOutput, completableFuture);
		}

		@Override
		protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
			return new WinterDropRecipeGenerator(registries, exporter);
		}

		@Override
		public String getName() {
			return "Winter Drop Recipes";
		}
	}
}
