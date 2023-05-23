package net.minecraft.data.server.recipe;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public class VanillaRecipeProvider extends RecipeProvider {
	private static final ImmutableList<ItemConvertible> COAL_ORES = ImmutableList.of(Items.COAL_ORE, Items.DEEPSLATE_COAL_ORE);
	private static final ImmutableList<ItemConvertible> IRON_ORES = ImmutableList.of(Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON);
	private static final ImmutableList<ItemConvertible> COPPER_ORES = ImmutableList.of(Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER);
	private static final ImmutableList<ItemConvertible> GOLD_ORES = ImmutableList.of(
		Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.NETHER_GOLD_ORE, Items.RAW_GOLD
	);
	private static final ImmutableList<ItemConvertible> DIAMOND_ORES = ImmutableList.of(Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE);
	private static final ImmutableList<ItemConvertible> LAPIS_ORES = ImmutableList.of(Items.LAPIS_ORE, Items.DEEPSLATE_LAPIS_ORE);
	private static final ImmutableList<ItemConvertible> REDSTONE_ORES = ImmutableList.of(Items.REDSTONE_ORE, Items.DEEPSLATE_REDSTONE_ORE);
	private static final ImmutableList<ItemConvertible> EMERALD_ORES = ImmutableList.of(Items.EMERALD_ORE, Items.DEEPSLATE_EMERALD_ORE);

	public VanillaRecipeProvider(DataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		return CompletableFuture.allOf(
			super.run(writer),
			this.saveRecipeAdvancement(
				writer, CraftingRecipeJsonBuilder.ROOT, Advancement.Builder.createUntelemetered().criterion("impossible", new ImpossibleCriterion.Conditions())
			)
		);
	}

	@Override
	protected void generate(Consumer<RecipeJsonProvider> exporter) {
		generateFamilies(exporter, FeatureSet.of(FeatureFlags.VANILLA));
		offerPlanksRecipe2(exporter, Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS, 4);
		offerPlanksRecipe(exporter, Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS, 4);
		offerPlanksRecipe(exporter, Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS, 4);
		offerPlanksRecipe2(exporter, Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS, 4);
		offerPlanksRecipe(exporter, Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS, 4);
		offerPlanksRecipe(exporter, Blocks.OAK_PLANKS, ItemTags.OAK_LOGS, 4);
		offerPlanksRecipe(exporter, Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS, 4);
		offerPlanksRecipe(exporter, Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS, 4);
		offerPlanksRecipe(exporter, Blocks.MANGROVE_PLANKS, ItemTags.MANGROVE_LOGS, 4);
		offerBarkBlockRecipe(exporter, Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
		offerBarkBlockRecipe(exporter, Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
		offerBarkBlockRecipe(exporter, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
		offerBarkBlockRecipe(exporter, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
		offerBarkBlockRecipe(exporter, Blocks.OAK_WOOD, Blocks.OAK_LOG);
		offerBarkBlockRecipe(exporter, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
		offerBarkBlockRecipe(exporter, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
		offerBarkBlockRecipe(exporter, Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
		offerBarkBlockRecipe(exporter, Blocks.MANGROVE_WOOD, Blocks.MANGROVE_LOG);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG);
		offerBoatRecipe(exporter, Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
		offerBoatRecipe(exporter, Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
		offerBoatRecipe(exporter, Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
		offerBoatRecipe(exporter, Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
		offerBoatRecipe(exporter, Items.OAK_BOAT, Blocks.OAK_PLANKS);
		offerBoatRecipe(exporter, Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
		offerBoatRecipe(exporter, Items.MANGROVE_BOAT, Blocks.MANGROVE_PLANKS);
		List<Item> list = List.of(
			Items.BLACK_DYE,
			Items.BLUE_DYE,
			Items.BROWN_DYE,
			Items.CYAN_DYE,
			Items.GRAY_DYE,
			Items.GREEN_DYE,
			Items.LIGHT_BLUE_DYE,
			Items.LIGHT_GRAY_DYE,
			Items.LIME_DYE,
			Items.MAGENTA_DYE,
			Items.ORANGE_DYE,
			Items.PINK_DYE,
			Items.PURPLE_DYE,
			Items.RED_DYE,
			Items.YELLOW_DYE,
			Items.WHITE_DYE
		);
		List<Item> list2 = List.of(
			Items.BLACK_WOOL,
			Items.BLUE_WOOL,
			Items.BROWN_WOOL,
			Items.CYAN_WOOL,
			Items.GRAY_WOOL,
			Items.GREEN_WOOL,
			Items.LIGHT_BLUE_WOOL,
			Items.LIGHT_GRAY_WOOL,
			Items.LIME_WOOL,
			Items.MAGENTA_WOOL,
			Items.ORANGE_WOOL,
			Items.PINK_WOOL,
			Items.PURPLE_WOOL,
			Items.RED_WOOL,
			Items.YELLOW_WOOL,
			Items.WHITE_WOOL
		);
		List<Item> list3 = List.of(
			Items.BLACK_BED,
			Items.BLUE_BED,
			Items.BROWN_BED,
			Items.CYAN_BED,
			Items.GRAY_BED,
			Items.GREEN_BED,
			Items.LIGHT_BLUE_BED,
			Items.LIGHT_GRAY_BED,
			Items.LIME_BED,
			Items.MAGENTA_BED,
			Items.ORANGE_BED,
			Items.PINK_BED,
			Items.PURPLE_BED,
			Items.RED_BED,
			Items.YELLOW_BED,
			Items.WHITE_BED
		);
		List<Item> list4 = List.of(
			Items.BLACK_CARPET,
			Items.BLUE_CARPET,
			Items.BROWN_CARPET,
			Items.CYAN_CARPET,
			Items.GRAY_CARPET,
			Items.GREEN_CARPET,
			Items.LIGHT_BLUE_CARPET,
			Items.LIGHT_GRAY_CARPET,
			Items.LIME_CARPET,
			Items.MAGENTA_CARPET,
			Items.ORANGE_CARPET,
			Items.PINK_CARPET,
			Items.PURPLE_CARPET,
			Items.RED_CARPET,
			Items.YELLOW_CARPET,
			Items.WHITE_CARPET
		);
		offerDyeableRecipes(exporter, list, list2, "wool");
		offerDyeableRecipes(exporter, list, list3, "bed");
		offerDyeableRecipes(exporter, list, list4, "carpet");
		offerCarpetRecipe(exporter, Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
		offerBedRecipe(exporter, Items.BLACK_BED, Blocks.BLACK_WOOL);
		offerBannerRecipe(exporter, Items.BLACK_BANNER, Blocks.BLACK_WOOL);
		offerCarpetRecipe(exporter, Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
		offerBedRecipe(exporter, Items.BLUE_BED, Blocks.BLUE_WOOL);
		offerBannerRecipe(exporter, Items.BLUE_BANNER, Blocks.BLUE_WOOL);
		offerCarpetRecipe(exporter, Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
		offerBedRecipe(exporter, Items.BROWN_BED, Blocks.BROWN_WOOL);
		offerBannerRecipe(exporter, Items.BROWN_BANNER, Blocks.BROWN_WOOL);
		offerCarpetRecipe(exporter, Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
		offerBedRecipe(exporter, Items.CYAN_BED, Blocks.CYAN_WOOL);
		offerBannerRecipe(exporter, Items.CYAN_BANNER, Blocks.CYAN_WOOL);
		offerCarpetRecipe(exporter, Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
		offerBedRecipe(exporter, Items.GRAY_BED, Blocks.GRAY_WOOL);
		offerBannerRecipe(exporter, Items.GRAY_BANNER, Blocks.GRAY_WOOL);
		offerCarpetRecipe(exporter, Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
		offerBedRecipe(exporter, Items.GREEN_BED, Blocks.GREEN_WOOL);
		offerBannerRecipe(exporter, Items.GREEN_BANNER, Blocks.GREEN_WOOL);
		offerCarpetRecipe(exporter, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
		offerBedRecipe(exporter, Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
		offerBannerRecipe(exporter, Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
		offerCarpetRecipe(exporter, Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
		offerBedRecipe(exporter, Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
		offerBannerRecipe(exporter, Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
		offerCarpetRecipe(exporter, Blocks.LIME_CARPET, Blocks.LIME_WOOL);
		offerBedRecipe(exporter, Items.LIME_BED, Blocks.LIME_WOOL);
		offerBannerRecipe(exporter, Items.LIME_BANNER, Blocks.LIME_WOOL);
		offerCarpetRecipe(exporter, Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
		offerBedRecipe(exporter, Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
		offerBannerRecipe(exporter, Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
		offerCarpetRecipe(exporter, Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
		offerBedRecipe(exporter, Items.ORANGE_BED, Blocks.ORANGE_WOOL);
		offerBannerRecipe(exporter, Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
		offerCarpetRecipe(exporter, Blocks.PINK_CARPET, Blocks.PINK_WOOL);
		offerBedRecipe(exporter, Items.PINK_BED, Blocks.PINK_WOOL);
		offerBannerRecipe(exporter, Items.PINK_BANNER, Blocks.PINK_WOOL);
		offerCarpetRecipe(exporter, Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
		offerBedRecipe(exporter, Items.PURPLE_BED, Blocks.PURPLE_WOOL);
		offerBannerRecipe(exporter, Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
		offerCarpetRecipe(exporter, Blocks.RED_CARPET, Blocks.RED_WOOL);
		offerBedRecipe(exporter, Items.RED_BED, Blocks.RED_WOOL);
		offerBannerRecipe(exporter, Items.RED_BANNER, Blocks.RED_WOOL);
		offerCarpetRecipe(exporter, Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
		offerBedRecipe(exporter, Items.WHITE_BED, Blocks.WHITE_WOOL);
		offerBannerRecipe(exporter, Items.WHITE_BANNER, Blocks.WHITE_WOOL);
		offerCarpetRecipe(exporter, Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
		offerBedRecipe(exporter, Items.YELLOW_BED, Blocks.YELLOW_WOOL);
		offerBannerRecipe(exporter, Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
		offerCarpetRecipe(exporter, Blocks.MOSS_CARPET, Blocks.MOSS_BLOCK);
		offerStainedGlassDyeingRecipe(exporter, Blocks.BLACK_STAINED_GLASS, Items.BLACK_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.BLACK_STAINED_GLASS_PANE, Items.BLACK_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.BLUE_STAINED_GLASS, Items.BLUE_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.BLUE_STAINED_GLASS_PANE, Items.BLUE_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.BROWN_STAINED_GLASS, Items.BROWN_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.BROWN_STAINED_GLASS_PANE, Items.BROWN_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.CYAN_STAINED_GLASS, Items.CYAN_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.CYAN_STAINED_GLASS_PANE, Items.CYAN_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.GRAY_STAINED_GLASS, Items.GRAY_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.GRAY_STAINED_GLASS_PANE, Items.GRAY_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.GREEN_STAINED_GLASS, Items.GREEN_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.GREEN_STAINED_GLASS_PANE, Items.GREEN_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.LIGHT_BLUE_STAINED_GLASS, Items.LIGHT_BLUE_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Items.LIGHT_BLUE_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.LIGHT_GRAY_STAINED_GLASS, Items.LIGHT_GRAY_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.LIME_STAINED_GLASS, Items.LIME_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.LIME_STAINED_GLASS_PANE, Items.LIME_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.MAGENTA_STAINED_GLASS, Items.MAGENTA_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.MAGENTA_STAINED_GLASS_PANE, Items.MAGENTA_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.ORANGE_STAINED_GLASS, Items.ORANGE_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.ORANGE_STAINED_GLASS_PANE, Items.ORANGE_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.PINK_STAINED_GLASS, Items.PINK_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.PINK_STAINED_GLASS_PANE, Items.PINK_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.PURPLE_STAINED_GLASS, Items.PURPLE_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.PURPLE_STAINED_GLASS_PANE, Items.PURPLE_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.RED_STAINED_GLASS, Items.RED_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.RED_STAINED_GLASS_PANE, Items.RED_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.WHITE_STAINED_GLASS, Items.WHITE_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.WHITE_STAINED_GLASS_PANE, Items.WHITE_DYE);
		offerStainedGlassDyeingRecipe(exporter, Blocks.YELLOW_STAINED_GLASS, Items.YELLOW_DYE);
		offerStainedGlassPaneRecipe(exporter, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(exporter, Blocks.YELLOW_STAINED_GLASS_PANE, Items.YELLOW_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.BLACK_TERRACOTTA, Items.BLACK_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.BLUE_TERRACOTTA, Items.BLUE_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.BROWN_TERRACOTTA, Items.BROWN_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.CYAN_TERRACOTTA, Items.CYAN_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.GRAY_TERRACOTTA, Items.GRAY_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.GREEN_TERRACOTTA, Items.GREEN_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.LIME_TERRACOTTA, Items.LIME_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.MAGENTA_TERRACOTTA, Items.MAGENTA_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.ORANGE_TERRACOTTA, Items.ORANGE_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.PINK_TERRACOTTA, Items.PINK_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.PURPLE_TERRACOTTA, Items.PURPLE_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.RED_TERRACOTTA, Items.RED_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.WHITE_TERRACOTTA, Items.WHITE_DYE);
		offerTerracottaDyeingRecipe(exporter, Blocks.YELLOW_TERRACOTTA, Items.YELLOW_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.BLACK_CONCRETE_POWDER, Items.BLACK_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.BLUE_CONCRETE_POWDER, Items.BLUE_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.BROWN_CONCRETE_POWDER, Items.BROWN_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.RED_CONCRETE_POWDER, Items.RED_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.WHITE_CONCRETE_POWDER, Items.WHITE_DYE);
		offerConcretePowderDyeingRecipe(exporter, Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.CANDLE)
			.input('S', Items.STRING)
			.input('H', Items.HONEYCOMB)
			.pattern("S")
			.pattern("H")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.criterion("has_honeycomb", conditionsFromItem(Items.HONEYCOMB))
			.offerTo(exporter);
		offerCandleDyeingRecipe(exporter, Blocks.BLACK_CANDLE, Items.BLACK_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.BLUE_CANDLE, Items.BLUE_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.BROWN_CANDLE, Items.BROWN_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.CYAN_CANDLE, Items.CYAN_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.GRAY_CANDLE, Items.GRAY_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.GREEN_CANDLE, Items.GREEN_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.LIGHT_BLUE_CANDLE, Items.LIGHT_BLUE_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.LIGHT_GRAY_CANDLE, Items.LIGHT_GRAY_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.LIME_CANDLE, Items.LIME_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.MAGENTA_CANDLE, Items.MAGENTA_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.ORANGE_CANDLE, Items.ORANGE_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.PINK_CANDLE, Items.PINK_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.PURPLE_CANDLE, Items.PURPLE_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.RED_CANDLE, Items.RED_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.WHITE_CANDLE, Items.WHITE_DYE);
		offerCandleDyeingRecipe(exporter, Blocks.YELLOW_CANDLE, Items.YELLOW_DYE);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.PACKED_MUD, 1)
			.input(Blocks.MUD)
			.input(Items.WHEAT)
			.criterion("has_mud", conditionsFromItem(Blocks.MUD))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.MUD_BRICKS, 4)
			.input('#', Blocks.PACKED_MUD)
			.pattern("##")
			.pattern("##")
			.criterion("has_packed_mud", conditionsFromItem(Blocks.PACKED_MUD))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.MUDDY_MANGROVE_ROOTS, 1)
			.input(Blocks.MUD)
			.input(Items.MANGROVE_ROOTS)
			.criterion("has_mangrove_roots", conditionsFromItem(Blocks.MANGROVE_ROOTS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Blocks.ACTIVATOR_RAIL, 6)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('S', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XSX")
			.pattern("X#X")
			.pattern("XSX")
			.criterion("has_rail", conditionsFromItem(Blocks.RAIL))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.ANDESITE, 2)
			.input(Blocks.DIORITE)
			.input(Blocks.COBBLESTONE)
			.criterion("has_stone", conditionsFromItem(Blocks.DIORITE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.ANVIL)
			.input('I', Blocks.IRON_BLOCK)
			.input('i', Items.IRON_INGOT)
			.pattern("III")
			.pattern(" i ")
			.pattern("iii")
			.criterion("has_iron_block", conditionsFromItem(Blocks.IRON_BLOCK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.ARMOR_STAND)
			.input('/', Items.STICK)
			.input('_', Blocks.SMOOTH_STONE_SLAB)
			.pattern("///")
			.pattern(" / ")
			.pattern("/_/")
			.criterion("has_stone_slab", conditionsFromItem(Blocks.SMOOTH_STONE_SLAB))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.ARROW, 4)
			.input('#', Items.STICK)
			.input('X', Items.FLINT)
			.input('Y', Items.FEATHER)
			.pattern("X")
			.pattern("#")
			.pattern("Y")
			.criterion("has_feather", conditionsFromItem(Items.FEATHER))
			.criterion("has_flint", conditionsFromItem(Items.FLINT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.BARREL, 1)
			.input('P', ItemTags.PLANKS)
			.input('S', ItemTags.WOODEN_SLABS)
			.pattern("PSP")
			.pattern("P P")
			.pattern("PSP")
			.criterion("has_planks", conditionsFromTag(ItemTags.PLANKS))
			.criterion("has_wood_slab", conditionsFromTag(ItemTags.WOODEN_SLABS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.BEACON)
			.input('S', Items.NETHER_STAR)
			.input('G', Blocks.GLASS)
			.input('O', Blocks.OBSIDIAN)
			.pattern("GGG")
			.pattern("GSG")
			.pattern("OOO")
			.criterion("has_nether_star", conditionsFromItem(Items.NETHER_STAR))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.BEEHIVE)
			.input('P', ItemTags.PLANKS)
			.input('H', Items.HONEYCOMB)
			.pattern("PPP")
			.pattern("HHH")
			.pattern("PPP")
			.criterion("has_honeycomb", conditionsFromItem(Items.HONEYCOMB))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.BEETROOT_SOUP)
			.input(Items.BOWL)
			.input(Items.BEETROOT, 6)
			.criterion("has_beetroot", conditionsFromItem(Items.BEETROOT))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BLACK_DYE)
			.input(Items.INK_SAC)
			.group("black_dye")
			.criterion("has_ink_sac", conditionsFromItem(Items.INK_SAC))
			.offerTo(exporter);
		offerSingleOutputShapelessRecipe(exporter, Items.BLACK_DYE, Blocks.WITHER_ROSE, "black_dye");
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BREWING, Items.BLAZE_POWDER, 2)
			.input(Items.BLAZE_ROD)
			.criterion("has_blaze_rod", conditionsFromItem(Items.BLAZE_ROD))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BLUE_DYE)
			.input(Items.LAPIS_LAZULI)
			.group("blue_dye")
			.criterion("has_lapis_lazuli", conditionsFromItem(Items.LAPIS_LAZULI))
			.offerTo(exporter);
		offerSingleOutputShapelessRecipe(exporter, Items.BLUE_DYE, Blocks.CORNFLOWER, "blue_dye");
		offerCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BLUE_ICE, Blocks.PACKED_ICE);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BONE_MEAL, 3)
			.input(Items.BONE)
			.group("bonemeal")
			.criterion("has_bone", conditionsFromItem(Items.BONE))
			.offerTo(exporter);
		offerReversibleCompactingRecipesWithReverseRecipeGroup(
			exporter, RecipeCategory.MISC, Items.BONE_MEAL, RecipeCategory.BUILDING_BLOCKS, Items.BONE_BLOCK, "bone_meal_from_bone_block", "bonemeal"
		);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BOOK)
			.input(Items.PAPER, 3)
			.input(Items.LEATHER)
			.criterion("has_paper", conditionsFromItem(Items.PAPER))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.BOOKSHELF)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.BOOK)
			.pattern("###")
			.pattern("XXX")
			.pattern("###")
			.criterion("has_book", conditionsFromItem(Items.BOOK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.BOW)
			.input('#', Items.STICK)
			.input('X', Items.STRING)
			.pattern(" #X")
			.pattern("# X")
			.pattern(" #X")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BOWL, 4)
			.input('#', ItemTags.PLANKS)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brown_mushroom", conditionsFromItem(Blocks.BROWN_MUSHROOM))
			.criterion("has_red_mushroom", conditionsFromItem(Blocks.RED_MUSHROOM))
			.criterion("has_mushroom_stew", conditionsFromItem(Items.MUSHROOM_STEW))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.BREAD)
			.input('#', Items.WHEAT)
			.pattern("###")
			.criterion("has_wheat", conditionsFromItem(Items.WHEAT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BREWING, Blocks.BREWING_STAND)
			.input('B', Items.BLAZE_ROD)
			.input('#', ItemTags.STONE_CRAFTING_MATERIALS)
			.pattern(" B ")
			.pattern("###")
			.criterion("has_blaze_rod", conditionsFromItem(Items.BLAZE_ROD))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.BRICKS)
			.input('#', Items.BRICK)
			.pattern("##")
			.pattern("##")
			.criterion("has_brick", conditionsFromItem(Items.BRICK))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BROWN_DYE)
			.input(Items.COCOA_BEANS)
			.group("brown_dye")
			.criterion("has_cocoa_beans", conditionsFromItem(Items.COCOA_BEANS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BUCKET)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Blocks.CAKE)
			.input('A', Items.MILK_BUCKET)
			.input('B', Items.SUGAR)
			.input('C', Items.WHEAT)
			.input('E', Items.EGG)
			.pattern("AAA")
			.pattern("BEB")
			.pattern("CCC")
			.criterion("has_egg", conditionsFromItem(Items.EGG))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.CAMPFIRE)
			.input('L', ItemTags.LOGS)
			.input('S', Items.STICK)
			.input('C', ItemTags.COALS)
			.pattern(" S ")
			.pattern("SCS")
			.pattern("LLL")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.criterion("has_coal", conditionsFromTag(ItemTags.COALS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Items.CARROT_ON_A_STICK)
			.input('#', Items.FISHING_ROD)
			.input('X', Items.CARROT)
			.pattern("# ")
			.pattern(" X")
			.criterion("has_carrot", conditionsFromItem(Items.CARROT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Items.WARPED_FUNGUS_ON_A_STICK)
			.input('#', Items.FISHING_ROD)
			.input('X', Items.WARPED_FUNGUS)
			.pattern("# ")
			.pattern(" X")
			.criterion("has_warped_fungus", conditionsFromItem(Items.WARPED_FUNGUS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BREWING, Blocks.CAULDRON)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern("# #")
			.pattern("###")
			.criterion("has_water_bucket", conditionsFromItem(Items.WATER_BUCKET))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.COMPOSTER)
			.input('#', ItemTags.WOODEN_SLABS)
			.pattern("# #")
			.pattern("# #")
			.pattern("###")
			.criterion("has_wood_slab", conditionsFromTag(ItemTags.WOODEN_SLABS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.CHEST)
			.input('#', ItemTags.PLANKS)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion(
				"has_lots_of_items",
				new InventoryChangedCriterion.Conditions(
					LootContextPredicate.EMPTY, NumberRange.IntRange.atLeast(10), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new ItemPredicate[0]
				)
			)
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Items.CHEST_MINECART)
			.input(Blocks.CHEST)
			.input(Items.MINECART)
			.criterion("has_minecart", conditionsFromItem(Items.MINECART))
			.offerTo(exporter);
		offerChestBoatRecipe(exporter, Items.ACACIA_CHEST_BOAT, Items.ACACIA_BOAT);
		offerChestBoatRecipe(exporter, Items.BIRCH_CHEST_BOAT, Items.BIRCH_BOAT);
		offerChestBoatRecipe(exporter, Items.DARK_OAK_CHEST_BOAT, Items.DARK_OAK_BOAT);
		offerChestBoatRecipe(exporter, Items.JUNGLE_CHEST_BOAT, Items.JUNGLE_BOAT);
		offerChestBoatRecipe(exporter, Items.OAK_CHEST_BOAT, Items.OAK_BOAT);
		offerChestBoatRecipe(exporter, Items.SPRUCE_CHEST_BOAT, Items.SPRUCE_BOAT);
		offerChestBoatRecipe(exporter, Items.MANGROVE_CHEST_BOAT, Items.MANGROVE_BOAT);
		createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_QUARTZ_BLOCK, Ingredient.ofItems(Blocks.QUARTZ_SLAB))
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(exporter);
		createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_STONE_BRICKS, Ingredient.ofItems(Blocks.STONE_BRICK_SLAB))
			.criterion("has_tag", conditionsFromTag(ItemTags.STONE_BRICKS))
			.offerTo(exporter);
		offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CLAY, Items.CLAY_BALL);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.CLOCK)
			.input('#', Items.GOLD_INGOT)
			.input('X', Items.REDSTONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(exporter);
		offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, Items.COAL, RecipeCategory.BUILDING_BLOCKS, Items.COAL_BLOCK);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.COARSE_DIRT, 4)
			.input('D', Blocks.DIRT)
			.input('G', Blocks.GRAVEL)
			.pattern("DG")
			.pattern("GD")
			.criterion("has_gravel", conditionsFromItem(Blocks.GRAVEL))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.COMPARATOR)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('X', Items.QUARTZ)
			.input('I', Blocks.STONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern("III")
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.COMPASS)
			.input('#', Items.IRON_INGOT)
			.input('X', Items.REDSTONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.COOKIE, 8)
			.input('#', Items.WHEAT)
			.input('X', Items.COCOA_BEANS)
			.pattern("#X#")
			.criterion("has_cocoa", conditionsFromItem(Items.COCOA_BEANS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.CRAFTING_TABLE)
			.input('#', ItemTags.PLANKS)
			.pattern("##")
			.pattern("##")
			.criterion("unlock_right_away", TickCriterion.Conditions.createTick())
			.showNotification(false)
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.CROSSBOW)
			.input('~', Items.STRING)
			.input('#', Items.STICK)
			.input('&', Items.IRON_INGOT)
			.input('$', Blocks.TRIPWIRE_HOOK)
			.pattern("#&#")
			.pattern("~$~")
			.pattern(" # ")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.criterion("has_tripwire_hook", conditionsFromItem(Blocks.TRIPWIRE_HOOK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.LOOM)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.STRING)
			.pattern("@@")
			.pattern("##")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(exporter);
		createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_RED_SANDSTONE, Ingredient.ofItems(Blocks.RED_SANDSTONE_SLAB))
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_cut_red_sandstone", conditionsFromItem(Blocks.CUT_RED_SANDSTONE))
			.offerTo(exporter);
		offerChiseledBlockRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE_SLAB);
		offerReversibleCompactingRecipesWithReverseRecipeGroup(
			exporter,
			RecipeCategory.MISC,
			Items.COPPER_INGOT,
			RecipeCategory.BUILDING_BLOCKS,
			Items.COPPER_BLOCK,
			getRecipeName(Items.COPPER_INGOT),
			getItemPath(Items.COPPER_INGOT)
		);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.COPPER_INGOT, 9)
			.input(Blocks.WAXED_COPPER_BLOCK)
			.group(getItemPath(Items.COPPER_INGOT))
			.criterion(hasItem(Blocks.WAXED_COPPER_BLOCK), conditionsFromItem(Blocks.WAXED_COPPER_BLOCK))
			.offerTo(exporter, convertBetween(Items.COPPER_INGOT, Blocks.WAXED_COPPER_BLOCK));
		offerWaxingRecipes(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.CYAN_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.GREEN_DYE)
			.group("cyan_dye")
			.criterion("has_green_dye", conditionsFromItem(Items.GREEN_DYE))
			.criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.DARK_PRISMARINE)
			.input('S', Items.PRISMARINE_SHARD)
			.input('I', Items.BLACK_DYE)
			.pattern("SSS")
			.pattern("SIS")
			.pattern("SSS")
			.criterion("has_prismarine_shard", conditionsFromItem(Items.PRISMARINE_SHARD))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.DAYLIGHT_DETECTOR)
			.input('Q', Items.QUARTZ)
			.input('G', Blocks.GLASS)
			.input('W', Ingredient.fromTag(ItemTags.WOODEN_SLABS))
			.pattern("GGG")
			.pattern("QQQ")
			.pattern("WWW")
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICKS, 4)
			.input('S', Blocks.POLISHED_DEEPSLATE)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_polished_deepslate", conditionsFromItem(Blocks.POLISHED_DEEPSLATE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, 4)
			.input('S', Blocks.DEEPSLATE_BRICKS)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_deepslate_bricks", conditionsFromItem(Blocks.DEEPSLATE_BRICKS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Blocks.DETECTOR_RAIL, 6)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.STONE_PRESSURE_PLATE)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", conditionsFromItem(Blocks.RAIL))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.DIAMOND_AXE)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(exporter);
		offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, Items.DIAMOND, RecipeCategory.BUILDING_BLOCKS, Items.DIAMOND_BLOCK);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.DIAMOND_BOOTS)
			.input('X', Items.DIAMOND)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.DIAMOND_CHESTPLATE)
			.input('X', Items.DIAMOND)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.DIAMOND_HELMET)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.DIAMOND_HOE)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.DIAMOND_LEGGINGS)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.DIAMOND_PICKAXE)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.DIAMOND_SHOVEL)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.DIAMOND_SWORD)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.DIORITE, 2)
			.input('Q', Items.QUARTZ)
			.input('C', Blocks.COBBLESTONE)
			.pattern("CQ")
			.pattern("QC")
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.DISPENSER)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.input('X', Items.BOW)
			.pattern("###")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_bow", conditionsFromItem(Items.BOW))
			.offerTo(exporter);
		offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DRIPSTONE_BLOCK, Items.POINTED_DRIPSTONE);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.DROPPER)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.pattern("# #")
			.pattern("#R#")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(exporter);
		offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, Items.EMERALD, RecipeCategory.BUILDING_BLOCKS, Items.EMERALD_BLOCK);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.ENCHANTING_TABLE)
			.input('B', Items.BOOK)
			.input('#', Blocks.OBSIDIAN)
			.input('D', Items.DIAMOND)
			.pattern(" B ")
			.pattern("D#D")
			.pattern("###")
			.criterion("has_obsidian", conditionsFromItem(Blocks.OBSIDIAN))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.ENDER_CHEST)
			.input('#', Blocks.OBSIDIAN)
			.input('E', Items.ENDER_EYE)
			.pattern("###")
			.pattern("#E#")
			.pattern("###")
			.criterion("has_ender_eye", conditionsFromItem(Items.ENDER_EYE))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ENDER_EYE)
			.input(Items.ENDER_PEARL)
			.input(Items.BLAZE_POWDER)
			.criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICKS, 4)
			.input('#', Blocks.END_STONE)
			.pattern("##")
			.pattern("##")
			.criterion("has_end_stone", conditionsFromItem(Blocks.END_STONE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.END_CRYSTAL)
			.input('T', Items.GHAST_TEAR)
			.input('E', Items.ENDER_EYE)
			.input('G', Blocks.GLASS)
			.pattern("GGG")
			.pattern("GEG")
			.pattern("GTG")
			.criterion("has_ender_eye", conditionsFromItem(Items.ENDER_EYE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.END_ROD, 4)
			.input('#', Items.POPPED_CHORUS_FRUIT)
			.input('/', Items.BLAZE_ROD)
			.pattern("/")
			.pattern("#")
			.criterion("has_chorus_fruit_popped", conditionsFromItem(Items.POPPED_CHORUS_FRUIT))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BREWING, Items.FERMENTED_SPIDER_EYE)
			.input(Items.SPIDER_EYE)
			.input(Blocks.BROWN_MUSHROOM)
			.input(Items.SUGAR)
			.criterion("has_spider_eye", conditionsFromItem(Items.SPIDER_EYE))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.FIRE_CHARGE, 3)
			.input(Items.GUNPOWDER)
			.input(Items.BLAZE_POWDER)
			.input(Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.FIREWORK_ROCKET, 3)
			.input(Items.GUNPOWDER)
			.input(Items.PAPER)
			.criterion("has_gunpowder", conditionsFromItem(Items.GUNPOWDER))
			.offerTo(exporter, "firework_rocket_simple");
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.FISHING_ROD)
			.input('#', Items.STICK)
			.input('X', Items.STRING)
			.pattern("  #")
			.pattern(" #X")
			.pattern("# X")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.FLINT_AND_STEEL)
			.input(Items.IRON_INGOT)
			.input(Items.FLINT)
			.criterion("has_flint", conditionsFromItem(Items.FLINT))
			.criterion("has_obsidian", conditionsFromItem(Blocks.OBSIDIAN))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.FLOWER_POT)
			.input('#', Items.BRICK)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brick", conditionsFromItem(Items.BRICK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.FURNACE)
			.input('#', ItemTags.STONE_CRAFTING_MATERIALS)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_CRAFTING_MATERIALS))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Items.FURNACE_MINECART)
			.input(Blocks.FURNACE)
			.input(Items.MINECART)
			.criterion("has_minecart", conditionsFromItem(Items.MINECART))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BREWING, Items.GLASS_BOTTLE, 3)
			.input('#', Blocks.GLASS)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_glass", conditionsFromItem(Blocks.GLASS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.GLASS_PANE, 16)
			.input('#', Blocks.GLASS)
			.pattern("###")
			.pattern("###")
			.criterion("has_glass", conditionsFromItem(Blocks.GLASS))
			.offerTo(exporter);
		offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.GLOWSTONE, Items.GLOWSTONE_DUST);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.GLOW_ITEM_FRAME)
			.input(Items.ITEM_FRAME)
			.input(Items.GLOW_INK_SAC)
			.criterion("has_item_frame", conditionsFromItem(Items.ITEM_FRAME))
			.criterion("has_glow_ink_sac", conditionsFromItem(Items.GLOW_INK_SAC))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.GOLDEN_APPLE)
			.input('#', Items.GOLD_INGOT)
			.input('X', Items.APPLE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.GOLDEN_AXE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.GOLDEN_BOOTS)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BREWING, Items.GOLDEN_CARROT)
			.input('#', Items.GOLD_NUGGET)
			.input('X', Items.CARROT)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_nugget", conditionsFromItem(Items.GOLD_NUGGET))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.GOLDEN_CHESTPLATE)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.GOLDEN_HELMET)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.GOLDEN_HOE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.GOLDEN_LEGGINGS)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.GOLDEN_PICKAXE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Blocks.POWERED_RAIL, 6)
			.input('R', Items.REDSTONE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", conditionsFromItem(Blocks.RAIL))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.GOLDEN_SHOVEL)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.GOLDEN_SWORD)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(exporter);
		offerReversibleCompactingRecipesWithReverseRecipeGroup(
			exporter, RecipeCategory.MISC, Items.GOLD_INGOT, RecipeCategory.BUILDING_BLOCKS, Items.GOLD_BLOCK, "gold_ingot_from_gold_block", "gold_ingot"
		);
		offerReversibleCompactingRecipesWithCompactingRecipeGroup(
			exporter, RecipeCategory.MISC, Items.GOLD_NUGGET, RecipeCategory.MISC, Items.GOLD_INGOT, "gold_ingot_from_nuggets", "gold_ingot"
		);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.GRANITE)
			.input(Blocks.DIORITE)
			.input(Items.QUARTZ)
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.GRAY_DYE, 2)
			.input(Items.BLACK_DYE)
			.input(Items.WHITE_DYE)
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.criterion("has_black_dye", conditionsFromItem(Items.BLACK_DYE))
			.offerTo(exporter);
		offerCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.HAY_BLOCK, Items.WHEAT);
		offerPressurePlateRecipe(exporter, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_INGOT);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.HONEY_BOTTLE, 4)
			.input(Items.HONEY_BLOCK)
			.input(Items.GLASS_BOTTLE, 4)
			.criterion("has_honey_block", conditionsFromItem(Blocks.HONEY_BLOCK))
			.offerTo(exporter);
		offer2x2CompactingRecipe(exporter, RecipeCategory.REDSTONE, Blocks.HONEY_BLOCK, Items.HONEY_BOTTLE);
		offer2x2CompactingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.HONEYCOMB_BLOCK, Items.HONEYCOMB);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.HOPPER)
			.input('C', Blocks.CHEST)
			.input('I', Items.IRON_INGOT)
			.pattern("I I")
			.pattern("ICI")
			.pattern(" I ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Items.HOPPER_MINECART)
			.input(Blocks.HOPPER)
			.input(Items.MINECART)
			.criterion("has_minecart", conditionsFromItem(Items.MINECART))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.IRON_AXE)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.IRON_BARS, 16)
			.input('#', Items.IRON_INGOT)
			.pattern("###")
			.pattern("###")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.IRON_BOOTS)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.IRON_CHESTPLATE)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		createDoorRecipe(Blocks.IRON_DOOR, Ingredient.ofItems(Items.IRON_INGOT))
			.criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.IRON_HELMET)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.IRON_HOE)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		offerReversibleCompactingRecipesWithReverseRecipeGroup(
			exporter, RecipeCategory.MISC, Items.IRON_INGOT, RecipeCategory.BUILDING_BLOCKS, Items.IRON_BLOCK, "iron_ingot_from_iron_block", "iron_ingot"
		);
		offerReversibleCompactingRecipesWithCompactingRecipeGroup(
			exporter, RecipeCategory.MISC, Items.IRON_NUGGET, RecipeCategory.MISC, Items.IRON_INGOT, "iron_ingot_from_nuggets", "iron_ingot"
		);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.IRON_LEGGINGS)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.IRON_PICKAXE)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.IRON_SHOVEL)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.IRON_SWORD)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		offer2x2CompactingRecipe(exporter, RecipeCategory.REDSTONE, Blocks.IRON_TRAPDOOR, Items.IRON_INGOT);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.ITEM_FRAME)
			.input('#', Items.STICK)
			.input('X', Items.LEATHER)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.JUKEBOX)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.DIAMOND)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.LADDER, 3)
			.input('#', Items.STICK)
			.pattern("# #")
			.pattern("###")
			.pattern("# #")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(exporter);
		offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, Items.LAPIS_LAZULI, RecipeCategory.BUILDING_BLOCKS, Items.LAPIS_BLOCK);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.LEAD, 2)
			.input('~', Items.STRING)
			.input('O', Items.SLIME_BALL)
			.pattern("~~ ")
			.pattern("~O ")
			.pattern("  ~")
			.criterion("has_slime_ball", conditionsFromItem(Items.SLIME_BALL))
			.offerTo(exporter);
		offer2x2CompactingRecipe(exporter, RecipeCategory.MISC, Items.LEATHER, Items.RABBIT_HIDE);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.LEATHER_BOOTS)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.LEATHER_CHESTPLATE)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.LEATHER_HELMET)
			.input('X', Items.LEATHER)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.LEATHER_LEGGINGS)
			.input('X', Items.LEATHER)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LEATHER_HORSE_ARMOR)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.LECTERN)
			.input('S', ItemTags.WOODEN_SLABS)
			.input('B', Blocks.BOOKSHELF)
			.pattern("SSS")
			.pattern(" B ")
			.pattern(" S ")
			.criterion("has_book", conditionsFromItem(Items.BOOK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.LEVER)
			.input('#', Blocks.COBBLESTONE)
			.input('X', Items.STICK)
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", conditionsFromItem(Blocks.COBBLESTONE))
			.offerTo(exporter);
		offerSingleOutputShapelessRecipe(exporter, Items.LIGHT_BLUE_DYE, Blocks.BLUE_ORCHID, "light_blue_dye");
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.WHITE_DYE)
			.group("light_blue_dye")
			.criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.offerTo(exporter, "light_blue_dye_from_blue_white_dye");
		offerSingleOutputShapelessRecipe(exporter, Items.LIGHT_GRAY_DYE, Blocks.AZURE_BLUET, "light_gray_dye");
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LIGHT_GRAY_DYE, 2)
			.input(Items.GRAY_DYE)
			.input(Items.WHITE_DYE)
			.group("light_gray_dye")
			.criterion("has_gray_dye", conditionsFromItem(Items.GRAY_DYE))
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.offerTo(exporter, "light_gray_dye_from_gray_white_dye");
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LIGHT_GRAY_DYE, 3)
			.input(Items.BLACK_DYE)
			.input(Items.WHITE_DYE, 2)
			.group("light_gray_dye")
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.criterion("has_black_dye", conditionsFromItem(Items.BLACK_DYE))
			.offerTo(exporter, "light_gray_dye_from_black_white_dye");
		offerSingleOutputShapelessRecipe(exporter, Items.LIGHT_GRAY_DYE, Blocks.OXEYE_DAISY, "light_gray_dye");
		offerSingleOutputShapelessRecipe(exporter, Items.LIGHT_GRAY_DYE, Blocks.WHITE_TULIP, "light_gray_dye");
		offerPressurePlateRecipe(exporter, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.LIGHTNING_ROD)
			.input('#', Items.COPPER_INGOT)
			.pattern("#")
			.pattern("#")
			.pattern("#")
			.criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.LIME_DYE, 2)
			.input(Items.GREEN_DYE)
			.input(Items.WHITE_DYE)
			.criterion("has_green_dye", conditionsFromItem(Items.GREEN_DYE))
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.JACK_O_LANTERN)
			.input('A', Blocks.CARVED_PUMPKIN)
			.input('B', Blocks.TORCH)
			.pattern("A")
			.pattern("B")
			.criterion("has_carved_pumpkin", conditionsFromItem(Blocks.CARVED_PUMPKIN))
			.offerTo(exporter);
		offerSingleOutputShapelessRecipe(exporter, Items.MAGENTA_DYE, Blocks.ALLIUM, "magenta_dye");
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.MAGENTA_DYE, 4)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE, 2)
			.input(Items.WHITE_DYE)
			.group("magenta_dye")
			.criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_rose_red", conditionsFromItem(Items.RED_DYE))
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.offerTo(exporter, "magenta_dye_from_blue_red_white_dye");
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.MAGENTA_DYE, 3)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE)
			.input(Items.PINK_DYE)
			.group("magenta_dye")
			.criterion("has_pink_dye", conditionsFromItem(Items.PINK_DYE))
			.criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_red_dye", conditionsFromItem(Items.RED_DYE))
			.offerTo(exporter, "magenta_dye_from_blue_red_pink");
		offerShapelessRecipe(exporter, Items.MAGENTA_DYE, Blocks.LILAC, "magenta_dye", 2);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.MAGENTA_DYE, 2)
			.input(Items.PURPLE_DYE)
			.input(Items.PINK_DYE)
			.group("magenta_dye")
			.criterion("has_pink_dye", conditionsFromItem(Items.PINK_DYE))
			.criterion("has_purple_dye", conditionsFromItem(Items.PURPLE_DYE))
			.offerTo(exporter, "magenta_dye_from_purple_and_pink");
		offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MAGMA_BLOCK, Items.MAGMA_CREAM);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BREWING, Items.MAGMA_CREAM)
			.input(Items.BLAZE_POWDER)
			.input(Items.SLIME_BALL)
			.criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.MAP)
			.input('#', Items.PAPER)
			.input('X', Items.COMPASS)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_compass", conditionsFromItem(Items.COMPASS))
			.offerTo(exporter);
		offerCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MELON, Items.MELON_SLICE, "has_melon");
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.MELON_SEEDS)
			.input(Items.MELON_SLICE)
			.criterion("has_melon", conditionsFromItem(Items.MELON_SLICE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Items.MINECART)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern("###")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE)
			.input(Blocks.COBBLESTONE)
			.input(Blocks.VINE)
			.group("mossy_cobblestone")
			.criterion("has_vine", conditionsFromItem(Blocks.VINE))
			.offerTo(exporter, convertBetween(Blocks.MOSSY_COBBLESTONE, Blocks.VINE));
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICKS)
			.input(Blocks.STONE_BRICKS)
			.input(Blocks.VINE)
			.group("mossy_stone_bricks")
			.criterion("has_vine", conditionsFromItem(Blocks.VINE))
			.offerTo(exporter, convertBetween(Blocks.MOSSY_STONE_BRICKS, Blocks.VINE));
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE)
			.input(Blocks.COBBLESTONE)
			.input(Blocks.MOSS_BLOCK)
			.group("mossy_cobblestone")
			.criterion("has_moss_block", conditionsFromItem(Blocks.MOSS_BLOCK))
			.offerTo(exporter, convertBetween(Blocks.MOSSY_COBBLESTONE, Blocks.MOSS_BLOCK));
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICKS)
			.input(Blocks.STONE_BRICKS)
			.input(Blocks.MOSS_BLOCK)
			.group("mossy_stone_bricks")
			.criterion("has_moss_block", conditionsFromItem(Blocks.MOSS_BLOCK))
			.offerTo(exporter, convertBetween(Blocks.MOSSY_STONE_BRICKS, Blocks.MOSS_BLOCK));
		ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.MUSHROOM_STEW)
			.input(Blocks.BROWN_MUSHROOM)
			.input(Blocks.RED_MUSHROOM)
			.input(Items.BOWL)
			.criterion("has_mushroom_stew", conditionsFromItem(Items.MUSHROOM_STEW))
			.criterion("has_bowl", conditionsFromItem(Items.BOWL))
			.criterion("has_brown_mushroom", conditionsFromItem(Blocks.BROWN_MUSHROOM))
			.criterion("has_red_mushroom", conditionsFromItem(Blocks.RED_MUSHROOM))
			.offerTo(exporter);
		offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_BRICKS, Items.NETHER_BRICK);
		offerCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_WART_BLOCK, Items.NETHER_WART);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.NOTE_BLOCK)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.REDSTONE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.OBSERVER)
			.input('Q', Items.QUARTZ)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.pattern("RRQ")
			.pattern("###")
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(exporter);
		offerSingleOutputShapelessRecipe(exporter, Items.ORANGE_DYE, Blocks.ORANGE_TULIP, "orange_dye");
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.ORANGE_DYE, 2)
			.input(Items.RED_DYE)
			.input(Items.YELLOW_DYE)
			.group("orange_dye")
			.criterion("has_red_dye", conditionsFromItem(Items.RED_DYE))
			.criterion("has_yellow_dye", conditionsFromItem(Items.YELLOW_DYE))
			.offerTo(exporter, "orange_dye_from_red_yellow");
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.PAINTING)
			.input('#', Items.STICK)
			.input('X', Ingredient.fromTag(ItemTags.WOOL))
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_wool", conditionsFromTag(ItemTags.WOOL))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PAPER, 3)
			.input('#', Blocks.SUGAR_CANE)
			.pattern("###")
			.criterion("has_reeds", conditionsFromItem(Blocks.SUGAR_CANE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_PILLAR, 2)
			.input('#', Blocks.QUARTZ_BLOCK)
			.pattern("#")
			.pattern("#")
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(exporter);
		offerCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PACKED_ICE, Blocks.ICE);
		offerShapelessRecipe(exporter, Items.PINK_DYE, Blocks.PEONY, "pink_dye", 2);
		offerSingleOutputShapelessRecipe(exporter, Items.PINK_DYE, Blocks.PINK_TULIP, "pink_dye");
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PINK_DYE, 2)
			.input(Items.RED_DYE)
			.input(Items.WHITE_DYE)
			.group("pink_dye")
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.criterion("has_red_dye", conditionsFromItem(Items.RED_DYE))
			.offerTo(exporter, "pink_dye_from_red_white_dye");
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.PISTON)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.input('T', ItemTags.PLANKS)
			.input('X', Items.IRON_INGOT)
			.pattern("TTT")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(exporter);
		offerPolishedStoneRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BASALT, Blocks.BASALT);
		offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE, Items.PRISMARINE_SHARD);
		offerCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_BRICKS, Items.PRISMARINE_SHARD);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.PUMPKIN_PIE)
			.input(Blocks.PUMPKIN)
			.input(Items.SUGAR)
			.input(Items.EGG)
			.criterion("has_carved_pumpkin", conditionsFromItem(Blocks.CARVED_PUMPKIN))
			.criterion("has_pumpkin", conditionsFromItem(Blocks.PUMPKIN))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PUMPKIN_SEEDS, 4)
			.input(Blocks.PUMPKIN)
			.criterion("has_pumpkin", conditionsFromItem(Blocks.PUMPKIN))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.PURPLE_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE)
			.criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_red_dye", conditionsFromItem(Items.RED_DYE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.SHULKER_BOX)
			.input('#', Blocks.CHEST)
			.input('-', Items.SHULKER_SHELL)
			.pattern("-")
			.pattern("#")
			.pattern("-")
			.criterion("has_shulker_shell", conditionsFromItem(Items.SHULKER_SHELL))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_BLOCK, 4)
			.input('F', Items.POPPED_CHORUS_FRUIT)
			.pattern("FF")
			.pattern("FF")
			.criterion("has_chorus_fruit_popped", conditionsFromItem(Items.POPPED_CHORUS_FRUIT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_PILLAR)
			.input('#', Blocks.PURPUR_SLAB)
			.pattern("#")
			.pattern("#")
			.criterion("has_purpur_block", conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(exporter);
		createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_SLAB, Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR))
			.criterion("has_purpur_block", conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(exporter);
		createStairsRecipe(Blocks.PURPUR_STAIRS, Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR))
			.criterion("has_purpur_block", conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(exporter);
		offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_BLOCK, Items.QUARTZ);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_BRICKS, 4)
			.input('#', Blocks.QUARTZ_BLOCK)
			.pattern("##")
			.pattern("##")
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(exporter);
		createSlabRecipe(
				RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_SLAB, Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)
			)
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(exporter);
		createStairsRecipe(Blocks.QUARTZ_STAIRS, Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR))
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.RABBIT_STEW)
			.input(Items.BAKED_POTATO)
			.input(Items.COOKED_RABBIT)
			.input(Items.BOWL)
			.input(Items.CARROT)
			.input(Blocks.BROWN_MUSHROOM)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", conditionsFromItem(Items.COOKED_RABBIT))
			.offerTo(exporter, convertBetween(Items.RABBIT_STEW, Items.BROWN_MUSHROOM));
		ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, Items.RABBIT_STEW)
			.input(Items.BAKED_POTATO)
			.input(Items.COOKED_RABBIT)
			.input(Items.BOWL)
			.input(Items.CARROT)
			.input(Blocks.RED_MUSHROOM)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", conditionsFromItem(Items.COOKED_RABBIT))
			.offerTo(exporter, convertBetween(Items.RABBIT_STEW, Items.RED_MUSHROOM));
		ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Blocks.RAIL, 16)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("X X")
			.criterion("has_minecart", conditionsFromItem(Items.MINECART))
			.offerTo(exporter);
		offerReversibleCompactingRecipes(exporter, RecipeCategory.REDSTONE, Items.REDSTONE, RecipeCategory.REDSTONE, Items.REDSTONE_BLOCK);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.REDSTONE_LAMP)
			.input('R', Items.REDSTONE)
			.input('G', Blocks.GLOWSTONE)
			.pattern(" R ")
			.pattern("RGR")
			.pattern(" R ")
			.criterion("has_glowstone", conditionsFromItem(Blocks.GLOWSTONE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.REDSTONE_TORCH)
			.input('#', Items.STICK)
			.input('X', Items.REDSTONE)
			.pattern("X")
			.pattern("#")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(exporter);
		offerSingleOutputShapelessRecipe(exporter, Items.RED_DYE, Items.BEETROOT, "red_dye");
		offerSingleOutputShapelessRecipe(exporter, Items.RED_DYE, Blocks.POPPY, "red_dye");
		offerShapelessRecipe(exporter, Items.RED_DYE, Blocks.ROSE_BUSH, "red_dye", 2);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.RED_DYE)
			.input(Blocks.RED_TULIP)
			.group("red_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.RED_TULIP))
			.offerTo(exporter, "red_dye_from_tulip");
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_NETHER_BRICKS)
			.input('W', Items.NETHER_WART)
			.input('N', Items.NETHER_BRICK)
			.pattern("NW")
			.pattern("WN")
			.criterion("has_nether_wart", conditionsFromItem(Items.NETHER_WART))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE)
			.input('#', Blocks.RED_SAND)
			.pattern("##")
			.pattern("##")
			.criterion("has_sand", conditionsFromItem(Blocks.RED_SAND))
			.offerTo(exporter);
		createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE_SLAB, Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE))
			.offerTo(exporter);
		createStairsRecipe(Blocks.RED_SANDSTONE_STAIRS, Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE))
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_cut_red_sandstone", conditionsFromItem(Blocks.CUT_RED_SANDSTONE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.REPEATER)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('X', Items.REDSTONE)
			.input('I', Blocks.STONE)
			.pattern("#X#")
			.pattern("III")
			.criterion("has_redstone_torch", conditionsFromItem(Blocks.REDSTONE_TORCH))
			.offerTo(exporter);
		offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE, Blocks.SAND);
		createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE_SLAB, Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE))
			.criterion("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.criterion("has_chiseled_sandstone", conditionsFromItem(Blocks.CHISELED_SANDSTONE))
			.offerTo(exporter);
		createStairsRecipe(Blocks.SANDSTONE_STAIRS, Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE))
			.criterion("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.criterion("has_chiseled_sandstone", conditionsFromItem(Blocks.CHISELED_SANDSTONE))
			.criterion("has_cut_sandstone", conditionsFromItem(Blocks.CUT_SANDSTONE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.SEA_LANTERN)
			.input('S', Items.PRISMARINE_SHARD)
			.input('C', Items.PRISMARINE_CRYSTALS)
			.pattern("SCS")
			.pattern("CCC")
			.pattern("SCS")
			.criterion("has_prismarine_crystals", conditionsFromItem(Items.PRISMARINE_CRYSTALS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.SHEARS)
			.input('#', Items.IRON_INGOT)
			.pattern(" #")
			.pattern("# ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.SHIELD)
			.input('W', ItemTags.PLANKS)
			.input('o', Items.IRON_INGOT)
			.pattern("WoW")
			.pattern("WWW")
			.pattern(" W ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, Items.SLIME_BALL, RecipeCategory.REDSTONE, Items.SLIME_BLOCK);
		offerCutCopperRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
		offerCutCopperRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
		offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SNOW_BLOCK, Items.SNOWBALL);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.SNOW, 6)
			.input('#', Blocks.SNOW_BLOCK)
			.pattern("###")
			.criterion("has_snowball", conditionsFromItem(Items.SNOWBALL))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.SOUL_CAMPFIRE)
			.input('L', ItemTags.LOGS)
			.input('S', Items.STICK)
			.input('#', ItemTags.SOUL_FIRE_BASE_BLOCKS)
			.pattern(" S ")
			.pattern("S#S")
			.pattern("LLL")
			.criterion("has_soul_sand", conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BREWING, Items.GLISTERING_MELON_SLICE)
			.input('#', Items.GOLD_NUGGET)
			.input('X', Items.MELON_SLICE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_melon", conditionsFromItem(Items.MELON_SLICE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.SPECTRAL_ARROW, 2)
			.input('#', Items.GLOWSTONE_DUST)
			.input('X', Items.ARROW)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_glowstone_dust", conditionsFromItem(Items.GLOWSTONE_DUST))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.SPYGLASS)
			.input('#', Items.AMETHYST_SHARD)
			.input('X', Items.COPPER_INGOT)
			.pattern(" # ")
			.pattern(" X ")
			.pattern(" X ")
			.criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STICK, 4)
			.input('#', ItemTags.PLANKS)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_planks", conditionsFromTag(ItemTags.PLANKS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.STICK, 1)
			.input('#', Blocks.BAMBOO)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_bamboo", conditionsFromItem(Blocks.BAMBOO))
			.offerTo(exporter, "stick_from_bamboo_item");
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.STICKY_PISTON)
			.input('P', Blocks.PISTON)
			.input('S', Items.SLIME_BALL)
			.pattern("S")
			.pattern("P")
			.criterion("has_slime_ball", conditionsFromItem(Items.SLIME_BALL))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICKS, 4)
			.input('#', Blocks.STONE)
			.pattern("##")
			.pattern("##")
			.criterion("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.STONE_AXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(exporter);
		createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_SLAB, Ingredient.ofItems(Blocks.STONE_BRICKS))
			.criterion("has_stone_bricks", conditionsFromTag(ItemTags.STONE_BRICKS))
			.offerTo(exporter);
		createStairsRecipe(Blocks.STONE_BRICK_STAIRS, Ingredient.ofItems(Blocks.STONE_BRICKS))
			.criterion("has_stone_bricks", conditionsFromTag(ItemTags.STONE_BRICKS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.STONE_HOE)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.STONE_PICKAXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.STONE_SHOVEL)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(exporter);
		offerSlabRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.STONE_SWORD)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.WHITE_WOOL)
			.input('#', Items.STRING)
			.pattern("##")
			.pattern("##")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(exporter, convertBetween(Blocks.WHITE_WOOL, Items.STRING));
		offerSingleOutputShapelessRecipe(exporter, Items.SUGAR, Blocks.SUGAR_CANE, "sugar");
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.SUGAR, 3)
			.input(Items.HONEY_BOTTLE)
			.group("sugar")
			.criterion("has_honey_bottle", conditionsFromItem(Items.HONEY_BOTTLE))
			.offerTo(exporter, convertBetween(Items.SUGAR, Items.HONEY_BOTTLE));
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.TARGET)
			.input('H', Items.HAY_BLOCK)
			.input('R', Items.REDSTONE)
			.pattern(" R ")
			.pattern("RHR")
			.pattern(" R ")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.criterion("has_hay_block", conditionsFromItem(Blocks.HAY_BLOCK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.TNT)
			.input('#', Ingredient.ofItems(Blocks.SAND, Blocks.RED_SAND))
			.input('X', Items.GUNPOWDER)
			.pattern("X#X")
			.pattern("#X#")
			.pattern("X#X")
			.criterion("has_gunpowder", conditionsFromItem(Items.GUNPOWDER))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, Items.TNT_MINECART)
			.input(Blocks.TNT)
			.input(Items.MINECART)
			.criterion("has_minecart", conditionsFromItem(Items.MINECART))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.TORCH, 4)
			.input('#', Items.STICK)
			.input('X', Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.pattern("X")
			.pattern("#")
			.criterion("has_stone_pickaxe", conditionsFromItem(Items.STONE_PICKAXE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.SOUL_TORCH, 4)
			.input('X', Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.input('#', Items.STICK)
			.input('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
			.pattern("X")
			.pattern("#")
			.pattern("S")
			.criterion("has_soul_sand", conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.LANTERN)
			.input('#', Items.TORCH)
			.input('X', Items.IRON_NUGGET)
			.pattern("XXX")
			.pattern("X#X")
			.pattern("XXX")
			.criterion("has_iron_nugget", conditionsFromItem(Items.IRON_NUGGET))
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.SOUL_LANTERN)
			.input('#', Items.SOUL_TORCH)
			.input('X', Items.IRON_NUGGET)
			.pattern("XXX")
			.pattern("X#X")
			.pattern("XXX")
			.criterion("has_soul_torch", conditionsFromItem(Items.SOUL_TORCH))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.TRAPPED_CHEST)
			.input(Blocks.CHEST)
			.input(Blocks.TRIPWIRE_HOOK)
			.criterion("has_tripwire_hook", conditionsFromItem(Blocks.TRIPWIRE_HOOK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.TRIPWIRE_HOOK, 2)
			.input('#', ItemTags.PLANKS)
			.input('S', Items.STICK)
			.input('I', Items.IRON_INGOT)
			.pattern("I")
			.pattern("S")
			.pattern("#")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.TURTLE_HELMET)
			.input('X', Items.SCUTE)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_scute", conditionsFromItem(Items.SCUTE))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.WHEAT, 9)
			.input(Blocks.HAY_BLOCK)
			.criterion("has_hay_block", conditionsFromItem(Blocks.HAY_BLOCK))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.WHITE_DYE)
			.input(Items.BONE_MEAL)
			.group("white_dye")
			.criterion("has_bone_meal", conditionsFromItem(Items.BONE_MEAL))
			.offerTo(exporter);
		offerSingleOutputShapelessRecipe(exporter, Items.WHITE_DYE, Blocks.LILY_OF_THE_VALLEY, "white_dye");
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.WOODEN_AXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.WOODEN_HOE)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.WOODEN_PICKAXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.WOODEN_SHOVEL)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Items.WOODEN_SWORD)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.WRITABLE_BOOK)
			.input(Items.BOOK)
			.input(Items.INK_SAC)
			.input(Items.FEATHER)
			.criterion("has_book", conditionsFromItem(Items.BOOK))
			.offerTo(exporter);
		offerSingleOutputShapelessRecipe(exporter, Items.YELLOW_DYE, Blocks.DANDELION, "yellow_dye");
		offerShapelessRecipe(exporter, Items.YELLOW_DYE, Blocks.SUNFLOWER, "yellow_dye", 2);
		offerReversibleCompactingRecipes(exporter, RecipeCategory.FOOD, Items.DRIED_KELP, RecipeCategory.BUILDING_BLOCKS, Items.DRIED_KELP_BLOCK);
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.CONDUIT)
			.input('#', Items.NAUTILUS_SHELL)
			.input('X', Items.HEART_OF_THE_SEA)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_nautilus_core", conditionsFromItem(Items.HEART_OF_THE_SEA))
			.criterion("has_nautilus_shell", conditionsFromItem(Items.NAUTILUS_SHELL))
			.offerTo(exporter);
		offerWallRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
		offerWallRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.STONE_BRICK_WALL, Blocks.STONE_BRICKS);
		offerWallRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.CREEPER_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.CREEPER_HEAD)
			.criterion("has_creeper_head", conditionsFromItem(Items.CREEPER_HEAD))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.SKULL_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.WITHER_SKELETON_SKULL)
			.criterion("has_wither_skeleton_skull", conditionsFromItem(Items.WITHER_SKELETON_SKULL))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.FLOWER_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Blocks.OXEYE_DAISY)
			.criterion("has_oxeye_daisy", conditionsFromItem(Blocks.OXEYE_DAISY))
			.offerTo(exporter);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.MOJANG_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.ENCHANTED_GOLDEN_APPLE)
			.criterion("has_enchanted_golden_apple", conditionsFromItem(Items.ENCHANTED_GOLDEN_APPLE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.SCAFFOLDING, 6)
			.input('~', Items.STRING)
			.input('I', Blocks.BAMBOO)
			.pattern("I~I")
			.pattern("I I")
			.pattern("I I")
			.criterion("has_bamboo", conditionsFromItem(Blocks.BAMBOO))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.GRINDSTONE)
			.input('I', Items.STICK)
			.input('-', Blocks.STONE_SLAB)
			.input('#', ItemTags.PLANKS)
			.pattern("I-I")
			.pattern("# #")
			.criterion("has_stone_slab", conditionsFromItem(Blocks.STONE_SLAB))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.BLAST_FURNACE)
			.input('#', Blocks.SMOOTH_STONE)
			.input('X', Blocks.FURNACE)
			.input('I', Items.IRON_INGOT)
			.pattern("III")
			.pattern("IXI")
			.pattern("###")
			.criterion("has_smooth_stone", conditionsFromItem(Blocks.SMOOTH_STONE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.SMOKER)
			.input('#', ItemTags.LOGS)
			.input('X', Blocks.FURNACE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_furnace", conditionsFromItem(Blocks.FURNACE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.CARTOGRAPHY_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.PAPER)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_paper", conditionsFromItem(Items.PAPER))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.SMITHING_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.IRON_INGOT)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.FLETCHING_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.FLINT)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_flint", conditionsFromItem(Items.FLINT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.STONECUTTER)
			.input('I', Items.IRON_INGOT)
			.input('#', Blocks.STONE)
			.pattern(" I ")
			.pattern("###")
			.criterion("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.LODESTONE)
			.input('S', Items.CHISELED_STONE_BRICKS)
			.input('#', Items.NETHERITE_INGOT)
			.pattern("SSS")
			.pattern("S#S")
			.pattern("SSS")
			.criterion("has_netherite_ingot", conditionsFromItem(Items.NETHERITE_INGOT))
			.offerTo(exporter);
		offerReversibleCompactingRecipesWithReverseRecipeGroup(
			exporter,
			RecipeCategory.MISC,
			Items.NETHERITE_INGOT,
			RecipeCategory.BUILDING_BLOCKS,
			Items.NETHERITE_BLOCK,
			"netherite_ingot_from_netherite_block",
			"netherite_ingot"
		);
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.NETHERITE_INGOT)
			.input(Items.NETHERITE_SCRAP, 4)
			.input(Items.GOLD_INGOT, 4)
			.group("netherite_ingot")
			.criterion("has_netherite_scrap", conditionsFromItem(Items.NETHERITE_SCRAP))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.RESPAWN_ANCHOR)
			.input('O', Blocks.CRYING_OBSIDIAN)
			.input('G', Blocks.GLOWSTONE)
			.pattern("OOO")
			.pattern("GGG")
			.pattern("OOO")
			.criterion("has_obsidian", conditionsFromItem(Blocks.CRYING_OBSIDIAN))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.CHAIN)
			.input('I', Items.IRON_INGOT)
			.input('N', Items.IRON_NUGGET)
			.pattern("N")
			.pattern("I")
			.pattern("N")
			.criterion("has_iron_nugget", conditionsFromItem(Items.IRON_NUGGET))
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Blocks.TINTED_GLASS, 2)
			.input('G', Blocks.GLASS)
			.input('S', Items.AMETHYST_SHARD)
			.pattern(" S ")
			.pattern("SGS")
			.pattern(" S ")
			.criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD))
			.offerTo(exporter);
		offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.AMETHYST_BLOCK, Items.AMETHYST_SHARD);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.RECOVERY_COMPASS)
			.input('C', Items.COMPASS)
			.input('S', Items.ECHO_SHARD)
			.pattern("SSS")
			.pattern("SCS")
			.pattern("SSS")
			.criterion("has_echo_shard", conditionsFromItem(Items.ECHO_SHARD))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Items.CALIBRATED_SCULK_SENSOR)
			.input('#', Items.AMETHYST_SHARD)
			.input('X', Items.SCULK_SENSOR)
			.pattern(" # ")
			.pattern("#X#")
			.criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD))
			.offerTo(exporter);
		offerCompactingRecipe(exporter, RecipeCategory.MISC, Items.MUSIC_DISC_5, Items.DISC_FRAGMENT_5);
		ComplexRecipeJsonBuilder.create(RecipeSerializer.ARMOR_DYE).offerTo(exporter, "armor_dye");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.BANNER_DUPLICATE).offerTo(exporter, "banner_duplicate");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.BOOK_CLONING).offerTo(exporter, "book_cloning");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.FIREWORK_ROCKET).offerTo(exporter, "firework_rocket");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.FIREWORK_STAR).offerTo(exporter, "firework_star");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.FIREWORK_STAR_FADE).offerTo(exporter, "firework_star_fade");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.MAP_CLONING).offerTo(exporter, "map_cloning");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.MAP_EXTENDING).offerTo(exporter, "map_extending");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.REPAIR_ITEM).offerTo(exporter, "repair_item");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.SHIELD_DECORATION).offerTo(exporter, "shield_decoration");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.SHULKER_BOX).offerTo(exporter, "shulker_box_coloring");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.TIPPED_ARROW).offerTo(exporter, "tipped_arrow");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.SUSPICIOUS_STEW).offerTo(exporter, "suspicious_stew");
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.35F, 200)
			.criterion("has_potato", conditionsFromItem(Items.POTATO))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.CLAY_BALL), RecipeCategory.MISC, Items.BRICK, 0.3F, 200)
			.criterion("has_clay_ball", conditionsFromItem(Items.CLAY_BALL))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.fromTag(ItemTags.LOGS_THAT_BURN), RecipeCategory.MISC, Items.CHARCOAL, 0.15F, 200)
			.criterion("has_log", conditionsFromTag(ItemTags.LOGS_THAT_BURN))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.CHORUS_FRUIT), RecipeCategory.MISC, Items.POPPED_CHORUS_FRUIT, 0.1F, 200)
			.criterion("has_chorus_fruit", conditionsFromItem(Items.CHORUS_FRUIT))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.35F, 200)
			.criterion("has_beef", conditionsFromItem(Items.BEEF))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.35F, 200)
			.criterion("has_chicken", conditionsFromItem(Items.CHICKEN))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.35F, 200)
			.criterion("has_cod", conditionsFromItem(Items.COD))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.KELP), RecipeCategory.FOOD, Items.DRIED_KELP, 0.1F, 200)
			.criterion("has_kelp", conditionsFromItem(Blocks.KELP))
			.offerTo(exporter, getSmeltingItemPath(Items.DRIED_KELP));
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.35F, 200)
			.criterion("has_salmon", conditionsFromItem(Items.SALMON))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.35F, 200)
			.criterion("has_mutton", conditionsFromItem(Items.MUTTON))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.35F, 200)
			.criterion("has_porkchop", conditionsFromItem(Items.PORKCHOP))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.35F, 200)
			.criterion("has_rabbit", conditionsFromItem(Items.RABBIT))
			.offerTo(exporter);
		offerSmelting(exporter, COAL_ORES, RecipeCategory.MISC, Items.COAL, 0.1F, 200, "coal");
		offerSmelting(exporter, IRON_ORES, RecipeCategory.MISC, Items.IRON_INGOT, 0.7F, 200, "iron_ingot");
		offerSmelting(exporter, COPPER_ORES, RecipeCategory.MISC, Items.COPPER_INGOT, 0.7F, 200, "copper_ingot");
		offerSmelting(exporter, GOLD_ORES, RecipeCategory.MISC, Items.GOLD_INGOT, 1.0F, 200, "gold_ingot");
		offerSmelting(exporter, DIAMOND_ORES, RecipeCategory.MISC, Items.DIAMOND, 1.0F, 200, "diamond");
		offerSmelting(exporter, LAPIS_ORES, RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.2F, 200, "lapis_lazuli");
		offerSmelting(exporter, REDSTONE_ORES, RecipeCategory.REDSTONE, Items.REDSTONE, 0.7F, 200, "redstone");
		offerSmelting(exporter, EMERALD_ORES, RecipeCategory.MISC, Items.EMERALD, 1.0F, 200, "emerald");
		offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, Items.RAW_IRON, RecipeCategory.BUILDING_BLOCKS, Items.RAW_IRON_BLOCK);
		offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, Items.RAW_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.RAW_COPPER_BLOCK);
		offerReversibleCompactingRecipes(exporter, RecipeCategory.MISC, Items.RAW_GOLD, RecipeCategory.BUILDING_BLOCKS, Items.RAW_GOLD_BLOCK);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.fromTag(ItemTags.SMELTS_TO_GLASS), RecipeCategory.BUILDING_BLOCKS, Blocks.GLASS.asItem(), 0.1F, 200)
			.criterion("has_smelts_to_glass", conditionsFromTag(ItemTags.SMELTS_TO_GLASS))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.SEA_PICKLE), RecipeCategory.MISC, Items.LIME_DYE, 0.1F, 200)
			.criterion("has_sea_pickle", conditionsFromItem(Blocks.SEA_PICKLE))
			.offerTo(exporter, getSmeltingItemPath(Items.LIME_DYE));
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.CACTUS.asItem()), RecipeCategory.MISC, Items.GREEN_DYE, 1.0F, 200)
			.criterion("has_cactus", conditionsFromItem(Blocks.CACTUS))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(
					Items.GOLDEN_PICKAXE,
					Items.GOLDEN_SHOVEL,
					Items.GOLDEN_AXE,
					Items.GOLDEN_HOE,
					Items.GOLDEN_SWORD,
					Items.GOLDEN_HELMET,
					Items.GOLDEN_CHESTPLATE,
					Items.GOLDEN_LEGGINGS,
					Items.GOLDEN_BOOTS,
					Items.GOLDEN_HORSE_ARMOR
				),
				RecipeCategory.MISC,
				Items.GOLD_NUGGET,
				0.1F,
				200
			)
			.criterion("has_golden_pickaxe", conditionsFromItem(Items.GOLDEN_PICKAXE))
			.criterion("has_golden_shovel", conditionsFromItem(Items.GOLDEN_SHOVEL))
			.criterion("has_golden_axe", conditionsFromItem(Items.GOLDEN_AXE))
			.criterion("has_golden_hoe", conditionsFromItem(Items.GOLDEN_HOE))
			.criterion("has_golden_sword", conditionsFromItem(Items.GOLDEN_SWORD))
			.criterion("has_golden_helmet", conditionsFromItem(Items.GOLDEN_HELMET))
			.criterion("has_golden_chestplate", conditionsFromItem(Items.GOLDEN_CHESTPLATE))
			.criterion("has_golden_leggings", conditionsFromItem(Items.GOLDEN_LEGGINGS))
			.criterion("has_golden_boots", conditionsFromItem(Items.GOLDEN_BOOTS))
			.criterion("has_golden_horse_armor", conditionsFromItem(Items.GOLDEN_HORSE_ARMOR))
			.offerTo(exporter, getSmeltingItemPath(Items.GOLD_NUGGET));
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(
					Items.IRON_PICKAXE,
					Items.IRON_SHOVEL,
					Items.IRON_AXE,
					Items.IRON_HOE,
					Items.IRON_SWORD,
					Items.IRON_HELMET,
					Items.IRON_CHESTPLATE,
					Items.IRON_LEGGINGS,
					Items.IRON_BOOTS,
					Items.IRON_HORSE_ARMOR,
					Items.CHAINMAIL_HELMET,
					Items.CHAINMAIL_CHESTPLATE,
					Items.CHAINMAIL_LEGGINGS,
					Items.CHAINMAIL_BOOTS
				),
				RecipeCategory.MISC,
				Items.IRON_NUGGET,
				0.1F,
				200
			)
			.criterion("has_iron_pickaxe", conditionsFromItem(Items.IRON_PICKAXE))
			.criterion("has_iron_shovel", conditionsFromItem(Items.IRON_SHOVEL))
			.criterion("has_iron_axe", conditionsFromItem(Items.IRON_AXE))
			.criterion("has_iron_hoe", conditionsFromItem(Items.IRON_HOE))
			.criterion("has_iron_sword", conditionsFromItem(Items.IRON_SWORD))
			.criterion("has_iron_helmet", conditionsFromItem(Items.IRON_HELMET))
			.criterion("has_iron_chestplate", conditionsFromItem(Items.IRON_CHESTPLATE))
			.criterion("has_iron_leggings", conditionsFromItem(Items.IRON_LEGGINGS))
			.criterion("has_iron_boots", conditionsFromItem(Items.IRON_BOOTS))
			.criterion("has_iron_horse_armor", conditionsFromItem(Items.IRON_HORSE_ARMOR))
			.criterion("has_chainmail_helmet", conditionsFromItem(Items.CHAINMAIL_HELMET))
			.criterion("has_chainmail_chestplate", conditionsFromItem(Items.CHAINMAIL_CHESTPLATE))
			.criterion("has_chainmail_leggings", conditionsFromItem(Items.CHAINMAIL_LEGGINGS))
			.criterion("has_chainmail_boots", conditionsFromItem(Items.CHAINMAIL_BOOTS))
			.offerTo(exporter, getSmeltingItemPath(Items.IRON_NUGGET));
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.CLAY), RecipeCategory.BUILDING_BLOCKS, Blocks.TERRACOTTA.asItem(), 0.35F, 200)
			.criterion("has_clay_block", conditionsFromItem(Blocks.CLAY))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.NETHERRACK), RecipeCategory.MISC, Items.NETHER_BRICK, 0.1F, 200)
			.criterion("has_netherrack", conditionsFromItem(Blocks.NETHERRACK))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.NETHER_QUARTZ_ORE), RecipeCategory.MISC, Items.QUARTZ, 0.2F, 200)
			.criterion("has_nether_quartz_ore", conditionsFromItem(Blocks.NETHER_QUARTZ_ORE))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.WET_SPONGE), RecipeCategory.BUILDING_BLOCKS, Blocks.SPONGE.asItem(), 0.15F, 200)
			.criterion("has_wet_sponge", conditionsFromItem(Blocks.WET_SPONGE))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.COBBLESTONE), RecipeCategory.BUILDING_BLOCKS, Blocks.STONE.asItem(), 0.1F, 200)
			.criterion("has_cobblestone", conditionsFromItem(Blocks.COBBLESTONE))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.STONE), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_STONE.asItem(), 0.1F, 200)
			.criterion("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.SANDSTONE), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_SANDSTONE.asItem(), 0.1F, 200)
			.criterion("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.RED_SANDSTONE), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1F, 200
			)
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_QUARTZ.asItem(), 0.1F, 200)
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1F, 200
			)
			.criterion("has_stone_bricks", conditionsFromItem(Blocks.STONE_BRICKS))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.BLACK_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_black_terracotta", conditionsFromItem(Blocks.BLACK_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.BLUE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_blue_terracotta", conditionsFromItem(Blocks.BLUE_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.BROWN_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_brown_terracotta", conditionsFromItem(Blocks.BROWN_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.CYAN_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_cyan_terracotta", conditionsFromItem(Blocks.CYAN_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.GRAY_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_gray_terracotta", conditionsFromItem(Blocks.GRAY_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.GREEN_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_green_terracotta", conditionsFromItem(Blocks.GREEN_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.LIGHT_BLUE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_light_blue_terracotta", conditionsFromItem(Blocks.LIGHT_BLUE_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.LIGHT_GRAY_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_light_gray_terracotta", conditionsFromItem(Blocks.LIGHT_GRAY_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.LIME_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_lime_terracotta", conditionsFromItem(Blocks.LIME_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.MAGENTA_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_magenta_terracotta", conditionsFromItem(Blocks.MAGENTA_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.ORANGE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_orange_terracotta", conditionsFromItem(Blocks.ORANGE_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.PINK_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_pink_terracotta", conditionsFromItem(Blocks.PINK_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.PURPLE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_purple_terracotta", conditionsFromItem(Blocks.PURPLE_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.RED_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_red_terracotta", conditionsFromItem(Blocks.RED_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.WHITE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_white_terracotta", conditionsFromItem(Blocks.WHITE_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItems(Blocks.YELLOW_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_yellow_terracotta", conditionsFromItem(Blocks.YELLOW_TERRACOTTA))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.ANCIENT_DEBRIS), RecipeCategory.MISC, Items.NETHERITE_SCRAP, 2.0F, 200)
			.criterion("has_ancient_debris", conditionsFromItem(Blocks.ANCIENT_DEBRIS))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.BASALT), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_BASALT, 0.1F, 200)
			.criterion("has_basalt", conditionsFromItem(Blocks.BASALT))
			.offerTo(exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.COBBLED_DEEPSLATE), RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE, 0.1F, 200)
			.criterion("has_cobbled_deepslate", conditionsFromItem(Blocks.COBBLED_DEEPSLATE))
			.offerTo(exporter);
		offerBlasting(exporter, COAL_ORES, RecipeCategory.MISC, Items.COAL, 0.1F, 100, "coal");
		offerBlasting(exporter, IRON_ORES, RecipeCategory.MISC, Items.IRON_INGOT, 0.7F, 100, "iron_ingot");
		offerBlasting(exporter, COPPER_ORES, RecipeCategory.MISC, Items.COPPER_INGOT, 0.7F, 100, "copper_ingot");
		offerBlasting(exporter, GOLD_ORES, RecipeCategory.MISC, Items.GOLD_INGOT, 1.0F, 100, "gold_ingot");
		offerBlasting(exporter, DIAMOND_ORES, RecipeCategory.MISC, Items.DIAMOND, 1.0F, 100, "diamond");
		offerBlasting(exporter, LAPIS_ORES, RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.2F, 100, "lapis_lazuli");
		offerBlasting(exporter, REDSTONE_ORES, RecipeCategory.REDSTONE, Items.REDSTONE, 0.7F, 100, "redstone");
		offerBlasting(exporter, EMERALD_ORES, RecipeCategory.MISC, Items.EMERALD, 1.0F, 100, "emerald");
		CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(Blocks.NETHER_QUARTZ_ORE), RecipeCategory.MISC, Items.QUARTZ, 0.2F, 100)
			.criterion("has_nether_quartz_ore", conditionsFromItem(Blocks.NETHER_QUARTZ_ORE))
			.offerTo(exporter, getBlastingItemPath(Items.QUARTZ));
		CookingRecipeJsonBuilder.createBlasting(
				Ingredient.ofItems(
					Items.GOLDEN_PICKAXE,
					Items.GOLDEN_SHOVEL,
					Items.GOLDEN_AXE,
					Items.GOLDEN_HOE,
					Items.GOLDEN_SWORD,
					Items.GOLDEN_HELMET,
					Items.GOLDEN_CHESTPLATE,
					Items.GOLDEN_LEGGINGS,
					Items.GOLDEN_BOOTS,
					Items.GOLDEN_HORSE_ARMOR
				),
				RecipeCategory.MISC,
				Items.GOLD_NUGGET,
				0.1F,
				100
			)
			.criterion("has_golden_pickaxe", conditionsFromItem(Items.GOLDEN_PICKAXE))
			.criterion("has_golden_shovel", conditionsFromItem(Items.GOLDEN_SHOVEL))
			.criterion("has_golden_axe", conditionsFromItem(Items.GOLDEN_AXE))
			.criterion("has_golden_hoe", conditionsFromItem(Items.GOLDEN_HOE))
			.criterion("has_golden_sword", conditionsFromItem(Items.GOLDEN_SWORD))
			.criterion("has_golden_helmet", conditionsFromItem(Items.GOLDEN_HELMET))
			.criterion("has_golden_chestplate", conditionsFromItem(Items.GOLDEN_CHESTPLATE))
			.criterion("has_golden_leggings", conditionsFromItem(Items.GOLDEN_LEGGINGS))
			.criterion("has_golden_boots", conditionsFromItem(Items.GOLDEN_BOOTS))
			.criterion("has_golden_horse_armor", conditionsFromItem(Items.GOLDEN_HORSE_ARMOR))
			.offerTo(exporter, getBlastingItemPath(Items.GOLD_NUGGET));
		CookingRecipeJsonBuilder.createBlasting(
				Ingredient.ofItems(
					Items.IRON_PICKAXE,
					Items.IRON_SHOVEL,
					Items.IRON_AXE,
					Items.IRON_HOE,
					Items.IRON_SWORD,
					Items.IRON_HELMET,
					Items.IRON_CHESTPLATE,
					Items.IRON_LEGGINGS,
					Items.IRON_BOOTS,
					Items.IRON_HORSE_ARMOR,
					Items.CHAINMAIL_HELMET,
					Items.CHAINMAIL_CHESTPLATE,
					Items.CHAINMAIL_LEGGINGS,
					Items.CHAINMAIL_BOOTS
				),
				RecipeCategory.MISC,
				Items.IRON_NUGGET,
				0.1F,
				100
			)
			.criterion("has_iron_pickaxe", conditionsFromItem(Items.IRON_PICKAXE))
			.criterion("has_iron_shovel", conditionsFromItem(Items.IRON_SHOVEL))
			.criterion("has_iron_axe", conditionsFromItem(Items.IRON_AXE))
			.criterion("has_iron_hoe", conditionsFromItem(Items.IRON_HOE))
			.criterion("has_iron_sword", conditionsFromItem(Items.IRON_SWORD))
			.criterion("has_iron_helmet", conditionsFromItem(Items.IRON_HELMET))
			.criterion("has_iron_chestplate", conditionsFromItem(Items.IRON_CHESTPLATE))
			.criterion("has_iron_leggings", conditionsFromItem(Items.IRON_LEGGINGS))
			.criterion("has_iron_boots", conditionsFromItem(Items.IRON_BOOTS))
			.criterion("has_iron_horse_armor", conditionsFromItem(Items.IRON_HORSE_ARMOR))
			.criterion("has_chainmail_helmet", conditionsFromItem(Items.CHAINMAIL_HELMET))
			.criterion("has_chainmail_chestplate", conditionsFromItem(Items.CHAINMAIL_CHESTPLATE))
			.criterion("has_chainmail_leggings", conditionsFromItem(Items.CHAINMAIL_LEGGINGS))
			.criterion("has_chainmail_boots", conditionsFromItem(Items.CHAINMAIL_BOOTS))
			.offerTo(exporter, getBlastingItemPath(Items.IRON_NUGGET));
		CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(Blocks.ANCIENT_DEBRIS), RecipeCategory.MISC, Items.NETHERITE_SCRAP, 2.0F, 100)
			.criterion("has_ancient_debris", conditionsFromItem(Blocks.ANCIENT_DEBRIS))
			.offerTo(exporter, getBlastingItemPath(Items.NETHERITE_SCRAP));
		generateCookingRecipes(exporter, "smoking", RecipeSerializer.SMOKING, 100);
		generateCookingRecipes(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, 600);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_SLAB, Blocks.STONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_STAIRS, Blocks.STONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICKS, Blocks.STONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_SLAB, Blocks.STONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE);
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.STONE), RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_STONE_BRICKS)
			.criterion("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(exporter, "chiseled_stone_bricks_stone_from_stonecutting");
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.STONE), RecipeCategory.DECORATIONS, Blocks.STONE_BRICK_WALL)
			.criterion("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(exporter, "stone_brick_walls_from_stone_stonecutting");
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE);
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_SLAB, 2)
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(exporter, "quartz_slab_from_stonecutting");
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_STAIRS, Blocks.QUARTZ_BLOCK);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_BLOCK);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_BRICKS, Blocks.QUARTZ_BLOCK);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICKS, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICKS);
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.STONE_BRICKS), RecipeCategory.DECORATIONS, Blocks.STONE_BRICK_WALL)
			.criterion("has_stone_bricks", conditionsFromItem(Blocks.STONE_BRICKS))
			.offerTo(exporter, "stone_brick_wall_from_stone_bricks_stonecutting");
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_STONE_BRICKS, Blocks.STONE_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BRICK_SLAB, Blocks.BRICKS, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BRICK_STAIRS, Blocks.BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.BRICK_WALL, Blocks.BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MUD_BRICK_SLAB, Blocks.MUD_BRICKS, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MUD_BRICK_STAIRS, Blocks.MUD_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.MUD_BRICK_WALL, Blocks.MUD_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICKS, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_NETHER_BRICKS, Blocks.NETHER_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICKS, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.RED_NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_SLAB, Blocks.PURPUR_BLOCK, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_STAIRS, Blocks.PURPUR_BLOCK);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_PILLAR, Blocks.PURPUR_BLOCK);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.PRISMARINE_WALL, Blocks.PRISMARINE);
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_BRICK_SLAB, 2)
			.criterion("has_prismarine_brick", conditionsFromItem(Blocks.PRISMARINE_BRICKS))
			.offerTo(exporter, "prismarine_brick_slab_from_prismarine_stonecutting");
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_BRICK_STAIRS)
			.criterion("has_prismarine_brick", conditionsFromItem(Blocks.PRISMARINE_BRICKS))
			.offerTo(exporter, "prismarine_brick_stairs_from_prismarine_stonecutting");
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.ANDESITE_SLAB, Blocks.ANDESITE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.ANDESITE_WALL, Blocks.ANDESITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE, Blocks.ANDESITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_SLAB, Blocks.ANDESITE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.ANDESITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BASALT, Blocks.BASALT);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.GRANITE_SLAB, Blocks.GRANITE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.GRANITE_STAIRS, Blocks.GRANITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.GRANITE_WALL, Blocks.GRANITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE, Blocks.GRANITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_SLAB, Blocks.GRANITE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_STAIRS, Blocks.GRANITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_STAIRS, Blocks.POLISHED_GRANITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DIORITE_SLAB, Blocks.DIORITE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DIORITE_STAIRS, Blocks.DIORITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.DIORITE_WALL, Blocks.DIORITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE, Blocks.DIORITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_SLAB, Blocks.DIORITE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_STAIRS, Blocks.DIORITE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_STAIRS, Blocks.POLISHED_DIORITE);
		SingleItemRecipeJsonBuilder.createStonecutting(
				Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICK_SLAB, 2
			)
			.criterion("has_mossy_stone_bricks", conditionsFromItem(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(exporter, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICK_STAIRS)
			.criterion("has_mossy_stone_bricks", conditionsFromItem(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(exporter, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), RecipeCategory.DECORATIONS, Blocks.MOSSY_STONE_BRICK_WALL)
			.criterion("has_mossy_stone_bricks", conditionsFromItem(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(exporter, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ);
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.END_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_SLAB, 2)
			.criterion("has_end_stone_brick", conditionsFromItem(Blocks.END_STONE_BRICKS))
			.offerTo(exporter, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.END_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_STAIRS)
			.criterion("has_end_stone_brick", conditionsFromItem(Blocks.END_STONE_BRICKS))
			.offerTo(exporter, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.END_STONE_BRICKS), RecipeCategory.DECORATIONS, Blocks.END_STONE_BRICK_WALL)
			.criterion("has_end_stone_brick", conditionsFromItem(Blocks.END_STONE_BRICKS))
			.offerTo(exporter, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICKS, Blocks.END_STONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.END_STONE_BRICK_WALL, Blocks.END_STONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.BLACKSTONE_WALL, Blocks.BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.BLACKSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICKS, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER, Blocks.COPPER_BLOCK, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_STAIRS, Blocks.COPPER_BLOCK, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_SLAB, Blocks.COPPER_BLOCK, 8);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_COPPER, 8);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_COPPER, 8);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_COPPER, 8);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER, Blocks.WAXED_COPPER_BLOCK, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_COPPER_BLOCK, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_COPPER_BLOCK, 8);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_COPPER, 8);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_COPPER, 8);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_COPPER, 4);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_COPPER, 8);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICKS, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_BRICK_WALL, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.COBBLED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.POLISHED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.POLISHED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICKS, Blocks.POLISHED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.POLISHED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_BRICK_WALL, Blocks.POLISHED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.POLISHED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.POLISHED_DEEPSLATE);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_BRICK_WALL, Blocks.DEEPSLATE_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_BRICKS);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILES, 2);
		offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES);
		offerStonecuttingRecipe(exporter, RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_TILES);
		getTrimSmithingTemplateMap().forEach((template, recipeId) -> offerSmithingTrimRecipe(exporter, template, recipeId));
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
		offerSmithingTemplateCopyingRecipe(exporter, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
		offerSmithingTemplateCopyingRecipe(exporter, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
		offerSmithingTemplateCopyingRecipe(exporter, Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
		offerSmithingTemplateCopyingRecipe(exporter, Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
		offerSmithingTemplateCopyingRecipe(exporter, Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
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
		offerSingleOutputShapelessRecipe(exporter, Items.ORANGE_DYE, Blocks.TORCHFLOWER, "orange_dye");
		offerShapelessRecipe(exporter, Items.CYAN_DYE, Blocks.PITCHER_PLANT, "cyan_dye", 2);
		offerPlanksRecipe2(exporter, Blocks.CHERRY_PLANKS, ItemTags.CHERRY_LOGS, 4);
		offerBarkBlockRecipe(exporter, Blocks.CHERRY_WOOD, Blocks.CHERRY_LOG);
		offerBarkBlockRecipe(exporter, Blocks.STRIPPED_CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG);
		offerBoatRecipe(exporter, Items.CHERRY_BOAT, Blocks.CHERRY_PLANKS);
		offerChestBoatRecipe(exporter, Items.CHERRY_CHEST_BOAT, Items.CHERRY_BOAT);
		offerShapelessRecipe(exporter, Items.PINK_DYE, Items.PINK_PETALS, "pink_dye", 1);
		ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Items.BRUSH)
			.input('X', Items.FEATHER)
			.input('#', Items.COPPER_INGOT)
			.input('I', Items.STICK)
			.pattern("X")
			.pattern("#")
			.pattern("I")
			.criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
			.offerTo(exporter);
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Items.DECORATED_POT)
			.input('#', Items.BRICK)
			.pattern(" # ")
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brick", conditionsFromTag(ItemTags.DECORATED_POT_INGREDIENTS))
			.offerTo(exporter, "decorated_pot_simple");
		ComplexRecipeJsonBuilder.create(RecipeSerializer.CRAFTING_DECORATED_POT).offerTo(exporter, "decorated_pot");
	}

	public static Map<Item, Identifier> getTrimSmithingTemplateMap() {
		return (Map<Item, Identifier>)Stream.of(
				Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE
			)
			.collect(Collectors.toMap(Function.identity(), item -> new Identifier(getItemPath(item) + "_smithing_trim")));
	}
}
