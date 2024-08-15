package net.minecraft.data.server.recipe;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.recipe.ArmorDyeRecipe;
import net.minecraft.recipe.BannerDuplicateRecipe;
import net.minecraft.recipe.BookCloningRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.CraftingDecoratedPotRecipe;
import net.minecraft.recipe.FireworkRocketRecipe;
import net.minecraft.recipe.FireworkStarFadeRecipe;
import net.minecraft.recipe.FireworkStarRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.MapCloningRecipe;
import net.minecraft.recipe.MapExtendingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RepairItemRecipe;
import net.minecraft.recipe.ShieldDecorationRecipe;
import net.minecraft.recipe.ShulkerBoxColoringRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.TippedArrowRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public class VanillaRecipeGenerator extends RecipeGenerator {
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

	VanillaRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
		super(wrapperLookup, recipeExporter);
	}

	@Override
	protected void generate() {
		this.exporter.addRootAdvancement();
		this.generateFamilies(FeatureSet.of(FeatureFlags.VANILLA));
		this.offerPlanksRecipe2(Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS, 4);
		this.offerPlanksRecipe(Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS, 4);
		this.offerPlanksRecipe(Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS, 4);
		this.offerPlanksRecipe2(Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS, 4);
		this.offerPlanksRecipe(Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS, 4);
		this.offerPlanksRecipe(Blocks.OAK_PLANKS, ItemTags.OAK_LOGS, 4);
		this.offerPlanksRecipe(Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS, 4);
		this.offerPlanksRecipe(Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS, 4);
		this.offerPlanksRecipe(Blocks.MANGROVE_PLANKS, ItemTags.MANGROVE_LOGS, 4);
		this.offerBarkBlockRecipe(Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
		this.offerBarkBlockRecipe(Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
		this.offerBarkBlockRecipe(Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
		this.offerBarkBlockRecipe(Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
		this.offerBarkBlockRecipe(Blocks.OAK_WOOD, Blocks.OAK_LOG);
		this.offerBarkBlockRecipe(Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
		this.offerBarkBlockRecipe(Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
		this.offerBarkBlockRecipe(Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
		this.offerBarkBlockRecipe(Blocks.MANGROVE_WOOD, Blocks.MANGROVE_LOG);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG);
		this.offerBoatRecipe(Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
		this.offerBoatRecipe(Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
		this.offerBoatRecipe(Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
		this.offerBoatRecipe(Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
		this.offerBoatRecipe(Items.OAK_BOAT, Blocks.OAK_PLANKS);
		this.offerBoatRecipe(Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
		this.offerBoatRecipe(Items.MANGROVE_BOAT, Blocks.MANGROVE_PLANKS);
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
		this.offerDyeableRecipes(list, list2, "wool");
		this.offerDyeableRecipes(list, list3, "bed");
		this.offerDyeableRecipes(list, list4, "carpet");
		this.offerCarpetRecipe(Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
		this.offerBedRecipe(Items.BLACK_BED, Blocks.BLACK_WOOL);
		this.offerBannerRecipe(Items.BLACK_BANNER, Blocks.BLACK_WOOL);
		this.offerCarpetRecipe(Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
		this.offerBedRecipe(Items.BLUE_BED, Blocks.BLUE_WOOL);
		this.offerBannerRecipe(Items.BLUE_BANNER, Blocks.BLUE_WOOL);
		this.offerCarpetRecipe(Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
		this.offerBedRecipe(Items.BROWN_BED, Blocks.BROWN_WOOL);
		this.offerBannerRecipe(Items.BROWN_BANNER, Blocks.BROWN_WOOL);
		this.offerCarpetRecipe(Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
		this.offerBedRecipe(Items.CYAN_BED, Blocks.CYAN_WOOL);
		this.offerBannerRecipe(Items.CYAN_BANNER, Blocks.CYAN_WOOL);
		this.offerCarpetRecipe(Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
		this.offerBedRecipe(Items.GRAY_BED, Blocks.GRAY_WOOL);
		this.offerBannerRecipe(Items.GRAY_BANNER, Blocks.GRAY_WOOL);
		this.offerCarpetRecipe(Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
		this.offerBedRecipe(Items.GREEN_BED, Blocks.GREEN_WOOL);
		this.offerBannerRecipe(Items.GREEN_BANNER, Blocks.GREEN_WOOL);
		this.offerCarpetRecipe(Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
		this.offerBedRecipe(Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
		this.offerBannerRecipe(Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
		this.offerCarpetRecipe(Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
		this.offerBedRecipe(Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
		this.offerBannerRecipe(Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
		this.offerCarpetRecipe(Blocks.LIME_CARPET, Blocks.LIME_WOOL);
		this.offerBedRecipe(Items.LIME_BED, Blocks.LIME_WOOL);
		this.offerBannerRecipe(Items.LIME_BANNER, Blocks.LIME_WOOL);
		this.offerCarpetRecipe(Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
		this.offerBedRecipe(Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
		this.offerBannerRecipe(Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
		this.offerCarpetRecipe(Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
		this.offerBedRecipe(Items.ORANGE_BED, Blocks.ORANGE_WOOL);
		this.offerBannerRecipe(Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
		this.offerCarpetRecipe(Blocks.PINK_CARPET, Blocks.PINK_WOOL);
		this.offerBedRecipe(Items.PINK_BED, Blocks.PINK_WOOL);
		this.offerBannerRecipe(Items.PINK_BANNER, Blocks.PINK_WOOL);
		this.offerCarpetRecipe(Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
		this.offerBedRecipe(Items.PURPLE_BED, Blocks.PURPLE_WOOL);
		this.offerBannerRecipe(Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
		this.offerCarpetRecipe(Blocks.RED_CARPET, Blocks.RED_WOOL);
		this.offerBedRecipe(Items.RED_BED, Blocks.RED_WOOL);
		this.offerBannerRecipe(Items.RED_BANNER, Blocks.RED_WOOL);
		this.offerCarpetRecipe(Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
		this.offerBedRecipe(Items.WHITE_BED, Blocks.WHITE_WOOL);
		this.offerBannerRecipe(Items.WHITE_BANNER, Blocks.WHITE_WOOL);
		this.offerCarpetRecipe(Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
		this.offerBedRecipe(Items.YELLOW_BED, Blocks.YELLOW_WOOL);
		this.offerBannerRecipe(Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
		this.offerCarpetRecipe(Blocks.MOSS_CARPET, Blocks.MOSS_BLOCK);
		this.offerStainedGlassDyeingRecipe(Blocks.BLACK_STAINED_GLASS, Items.BLACK_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.BLACK_STAINED_GLASS_PANE, Items.BLACK_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.BLUE_STAINED_GLASS, Items.BLUE_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.BLUE_STAINED_GLASS_PANE, Items.BLUE_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.BROWN_STAINED_GLASS, Items.BROWN_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.BROWN_STAINED_GLASS_PANE, Items.BROWN_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.CYAN_STAINED_GLASS, Items.CYAN_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.CYAN_STAINED_GLASS_PANE, Items.CYAN_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.GRAY_STAINED_GLASS, Items.GRAY_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.GRAY_STAINED_GLASS_PANE, Items.GRAY_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.GREEN_STAINED_GLASS, Items.GREEN_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.GREEN_STAINED_GLASS_PANE, Items.GREEN_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.LIGHT_BLUE_STAINED_GLASS, Items.LIGHT_BLUE_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Items.LIGHT_BLUE_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.LIGHT_GRAY_STAINED_GLASS, Items.LIGHT_GRAY_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.LIME_STAINED_GLASS, Items.LIME_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.LIME_STAINED_GLASS_PANE, Items.LIME_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.MAGENTA_STAINED_GLASS, Items.MAGENTA_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.MAGENTA_STAINED_GLASS_PANE, Items.MAGENTA_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.ORANGE_STAINED_GLASS, Items.ORANGE_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.ORANGE_STAINED_GLASS_PANE, Items.ORANGE_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.PINK_STAINED_GLASS, Items.PINK_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.PINK_STAINED_GLASS_PANE, Items.PINK_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.PURPLE_STAINED_GLASS, Items.PURPLE_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.PURPLE_STAINED_GLASS_PANE, Items.PURPLE_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.RED_STAINED_GLASS, Items.RED_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.RED_STAINED_GLASS_PANE, Items.RED_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.WHITE_STAINED_GLASS, Items.WHITE_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.WHITE_STAINED_GLASS_PANE, Items.WHITE_DYE);
		this.offerStainedGlassDyeingRecipe(Blocks.YELLOW_STAINED_GLASS, Items.YELLOW_DYE);
		this.offerStainedGlassPaneRecipe(Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS);
		this.offerStainedGlassPaneDyeingRecipe(Blocks.YELLOW_STAINED_GLASS_PANE, Items.YELLOW_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.BLACK_TERRACOTTA, Items.BLACK_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.BLUE_TERRACOTTA, Items.BLUE_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.BROWN_TERRACOTTA, Items.BROWN_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.CYAN_TERRACOTTA, Items.CYAN_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.GRAY_TERRACOTTA, Items.GRAY_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.GREEN_TERRACOTTA, Items.GREEN_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.LIME_TERRACOTTA, Items.LIME_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.MAGENTA_TERRACOTTA, Items.MAGENTA_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.ORANGE_TERRACOTTA, Items.ORANGE_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.PINK_TERRACOTTA, Items.PINK_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.PURPLE_TERRACOTTA, Items.PURPLE_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.RED_TERRACOTTA, Items.RED_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.WHITE_TERRACOTTA, Items.WHITE_DYE);
		this.offerTerracottaDyeingRecipe(Blocks.YELLOW_TERRACOTTA, Items.YELLOW_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.BLACK_CONCRETE_POWDER, Items.BLACK_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.BLUE_CONCRETE_POWDER, Items.BLUE_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.BROWN_CONCRETE_POWDER, Items.BROWN_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.RED_CONCRETE_POWDER, Items.RED_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.WHITE_CONCRETE_POWDER, Items.WHITE_DYE);
		this.offerConcretePowderDyeingRecipe(Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE);
		this.createShaped(RecipeCategory.DECORATIONS, Items.CANDLE)
			.input('S', Items.STRING)
			.input('H', Items.HONEYCOMB)
			.pattern("S")
			.pattern("H")
			.criterion("has_string", this.conditionsFromItem(Items.STRING))
			.criterion("has_honeycomb", this.conditionsFromItem(Items.HONEYCOMB))
			.offerTo(this.exporter);
		this.offerCandleDyeingRecipe(Blocks.BLACK_CANDLE, Items.BLACK_DYE);
		this.offerCandleDyeingRecipe(Blocks.BLUE_CANDLE, Items.BLUE_DYE);
		this.offerCandleDyeingRecipe(Blocks.BROWN_CANDLE, Items.BROWN_DYE);
		this.offerCandleDyeingRecipe(Blocks.CYAN_CANDLE, Items.CYAN_DYE);
		this.offerCandleDyeingRecipe(Blocks.GRAY_CANDLE, Items.GRAY_DYE);
		this.offerCandleDyeingRecipe(Blocks.GREEN_CANDLE, Items.GREEN_DYE);
		this.offerCandleDyeingRecipe(Blocks.LIGHT_BLUE_CANDLE, Items.LIGHT_BLUE_DYE);
		this.offerCandleDyeingRecipe(Blocks.LIGHT_GRAY_CANDLE, Items.LIGHT_GRAY_DYE);
		this.offerCandleDyeingRecipe(Blocks.LIME_CANDLE, Items.LIME_DYE);
		this.offerCandleDyeingRecipe(Blocks.MAGENTA_CANDLE, Items.MAGENTA_DYE);
		this.offerCandleDyeingRecipe(Blocks.ORANGE_CANDLE, Items.ORANGE_DYE);
		this.offerCandleDyeingRecipe(Blocks.PINK_CANDLE, Items.PINK_DYE);
		this.offerCandleDyeingRecipe(Blocks.PURPLE_CANDLE, Items.PURPLE_DYE);
		this.offerCandleDyeingRecipe(Blocks.RED_CANDLE, Items.RED_DYE);
		this.offerCandleDyeingRecipe(Blocks.WHITE_CANDLE, Items.WHITE_DYE);
		this.offerCandleDyeingRecipe(Blocks.YELLOW_CANDLE, Items.YELLOW_DYE);
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.PACKED_MUD, 1)
			.input(Blocks.MUD)
			.input(Items.WHEAT)
			.criterion("has_mud", this.conditionsFromItem(Blocks.MUD))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.MUD_BRICKS, 4)
			.input('#', Blocks.PACKED_MUD)
			.pattern("##")
			.pattern("##")
			.criterion("has_packed_mud", this.conditionsFromItem(Blocks.PACKED_MUD))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.MUDDY_MANGROVE_ROOTS, 1)
			.input(Blocks.MUD)
			.input(Items.MANGROVE_ROOTS)
			.criterion("has_mangrove_roots", this.conditionsFromItem(Blocks.MANGROVE_ROOTS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TRANSPORTATION, Blocks.ACTIVATOR_RAIL, 6)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('S', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XSX")
			.pattern("X#X")
			.pattern("XSX")
			.criterion("has_rail", this.conditionsFromItem(Blocks.RAIL))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.ANDESITE, 2)
			.input(Blocks.DIORITE)
			.input(Blocks.COBBLESTONE)
			.criterion("has_stone", this.conditionsFromItem(Blocks.DIORITE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.ANVIL)
			.input('I', Blocks.IRON_BLOCK)
			.input('i', Items.IRON_INGOT)
			.pattern("III")
			.pattern(" i ")
			.pattern("iii")
			.criterion("has_iron_block", this.conditionsFromItem(Blocks.IRON_BLOCK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Items.ARMOR_STAND)
			.input('/', Items.STICK)
			.input('_', Blocks.SMOOTH_STONE_SLAB)
			.pattern("///")
			.pattern(" / ")
			.pattern("/_/")
			.criterion("has_stone_slab", this.conditionsFromItem(Blocks.SMOOTH_STONE_SLAB))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.ARROW, 4)
			.input('#', Items.STICK)
			.input('X', Items.FLINT)
			.input('Y', Items.FEATHER)
			.pattern("X")
			.pattern("#")
			.pattern("Y")
			.criterion("has_feather", this.conditionsFromItem(Items.FEATHER))
			.criterion("has_flint", this.conditionsFromItem(Items.FLINT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.BARREL, 1)
			.input('P', ItemTags.PLANKS)
			.input('S', ItemTags.WOODEN_SLABS)
			.pattern("PSP")
			.pattern("P P")
			.pattern("PSP")
			.criterion("has_planks", this.conditionsFromTag(ItemTags.PLANKS))
			.criterion("has_wood_slab", this.conditionsFromTag(ItemTags.WOODEN_SLABS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.MISC, Blocks.BEACON)
			.input('S', Items.NETHER_STAR)
			.input('G', Blocks.GLASS)
			.input('O', Blocks.OBSIDIAN)
			.pattern("GGG")
			.pattern("GSG")
			.pattern("OOO")
			.criterion("has_nether_star", this.conditionsFromItem(Items.NETHER_STAR))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.BEEHIVE)
			.input('P', ItemTags.PLANKS)
			.input('H', Items.HONEYCOMB)
			.pattern("PPP")
			.pattern("HHH")
			.pattern("PPP")
			.criterion("has_honeycomb", this.conditionsFromItem(Items.HONEYCOMB))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.FOOD, Items.BEETROOT_SOUP)
			.input(Items.BOWL)
			.input(Items.BEETROOT, 6)
			.criterion("has_beetroot", this.conditionsFromItem(Items.BEETROOT))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.BLACK_DYE)
			.input(Items.INK_SAC)
			.group("black_dye")
			.criterion("has_ink_sac", this.conditionsFromItem(Items.INK_SAC))
			.offerTo(this.exporter);
		this.offerSingleOutputShapelessRecipe(Items.BLACK_DYE, Blocks.WITHER_ROSE, "black_dye");
		this.createShapeless(RecipeCategory.BREWING, Items.BLAZE_POWDER, 2)
			.input(Items.BLAZE_ROD)
			.criterion("has_blaze_rod", this.conditionsFromItem(Items.BLAZE_ROD))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.BLUE_DYE)
			.input(Items.LAPIS_LAZULI)
			.group("blue_dye")
			.criterion("has_lapis_lazuli", this.conditionsFromItem(Items.LAPIS_LAZULI))
			.offerTo(this.exporter);
		this.offerSingleOutputShapelessRecipe(Items.BLUE_DYE, Blocks.CORNFLOWER, "blue_dye");
		this.offerCompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.BLUE_ICE, Blocks.PACKED_ICE);
		this.createShapeless(RecipeCategory.MISC, Items.BONE_MEAL, 3)
			.input(Items.BONE)
			.group("bonemeal")
			.criterion("has_bone", this.conditionsFromItem(Items.BONE))
			.offerTo(this.exporter);
		this.offerReversibleCompactingRecipesWithReverseRecipeGroup(
			RecipeCategory.MISC, Items.BONE_MEAL, RecipeCategory.BUILDING_BLOCKS, Items.BONE_BLOCK, "bone_meal_from_bone_block", "bonemeal"
		);
		this.createShapeless(RecipeCategory.MISC, Items.BOOK)
			.input(Items.PAPER, 3)
			.input(Items.LEATHER)
			.criterion("has_paper", this.conditionsFromItem(Items.PAPER))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.BOOKSHELF)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.BOOK)
			.pattern("###")
			.pattern("XXX")
			.pattern("###")
			.criterion("has_book", this.conditionsFromItem(Items.BOOK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.BOW)
			.input('#', Items.STICK)
			.input('X', Items.STRING)
			.pattern(" #X")
			.pattern("# X")
			.pattern(" #X")
			.criterion("has_string", this.conditionsFromItem(Items.STRING))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.MISC, Items.BOWL, 4)
			.input('#', ItemTags.PLANKS)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brown_mushroom", this.conditionsFromItem(Blocks.BROWN_MUSHROOM))
			.criterion("has_red_mushroom", this.conditionsFromItem(Blocks.RED_MUSHROOM))
			.criterion("has_mushroom_stew", this.conditionsFromItem(Items.MUSHROOM_STEW))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.FOOD, Items.BREAD)
			.input('#', Items.WHEAT)
			.pattern("###")
			.criterion("has_wheat", this.conditionsFromItem(Items.WHEAT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BREWING, Blocks.BREWING_STAND)
			.input('B', Items.BLAZE_ROD)
			.input('#', ItemTags.STONE_CRAFTING_MATERIALS)
			.pattern(" B ")
			.pattern("###")
			.criterion("has_blaze_rod", this.conditionsFromItem(Items.BLAZE_ROD))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.BRICKS)
			.input('#', Items.BRICK)
			.pattern("##")
			.pattern("##")
			.criterion("has_brick", this.conditionsFromItem(Items.BRICK))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.BROWN_DYE)
			.input(Items.COCOA_BEANS)
			.group("brown_dye")
			.criterion("has_cocoa_beans", this.conditionsFromItem(Items.COCOA_BEANS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.MISC, Items.BUCKET)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.FOOD, Blocks.CAKE)
			.input('A', Items.MILK_BUCKET)
			.input('B', Items.SUGAR)
			.input('C', Items.WHEAT)
			.input('E', Items.EGG)
			.pattern("AAA")
			.pattern("BEB")
			.pattern("CCC")
			.criterion("has_egg", this.conditionsFromItem(Items.EGG))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.CAMPFIRE)
			.input('L', ItemTags.LOGS)
			.input('S', Items.STICK)
			.input('C', ItemTags.COALS)
			.pattern(" S ")
			.pattern("SCS")
			.pattern("LLL")
			.criterion("has_stick", this.conditionsFromItem(Items.STICK))
			.criterion("has_coal", this.conditionsFromTag(ItemTags.COALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TRANSPORTATION, Items.CARROT_ON_A_STICK)
			.input('#', Items.FISHING_ROD)
			.input('X', Items.CARROT)
			.pattern("# ")
			.pattern(" X")
			.criterion("has_carrot", this.conditionsFromItem(Items.CARROT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TRANSPORTATION, Items.WARPED_FUNGUS_ON_A_STICK)
			.input('#', Items.FISHING_ROD)
			.input('X', Items.WARPED_FUNGUS)
			.pattern("# ")
			.pattern(" X")
			.criterion("has_warped_fungus", this.conditionsFromItem(Items.WARPED_FUNGUS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BREWING, Blocks.CAULDRON)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern("# #")
			.pattern("###")
			.criterion("has_water_bucket", this.conditionsFromItem(Items.WATER_BUCKET))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.COMPOSTER)
			.input('#', ItemTags.WOODEN_SLABS)
			.pattern("# #")
			.pattern("# #")
			.pattern("###")
			.criterion("has_wood_slab", this.conditionsFromTag(ItemTags.WOODEN_SLABS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.CHEST)
			.input('#', ItemTags.PLANKS)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion(
				"has_lots_of_items",
				Criteria.INVENTORY_CHANGED
					.create(
						new InventoryChangedCriterion.Conditions(
							Optional.empty(),
							new InventoryChangedCriterion.Conditions.Slots(NumberRange.IntRange.atLeast(10), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY),
							List.of()
						)
					)
			)
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.TRANSPORTATION, Items.CHEST_MINECART)
			.input(Blocks.CHEST)
			.input(Items.MINECART)
			.criterion("has_minecart", this.conditionsFromItem(Items.MINECART))
			.offerTo(this.exporter);
		this.offerChestBoatRecipe(Items.ACACIA_CHEST_BOAT, Items.ACACIA_BOAT);
		this.offerChestBoatRecipe(Items.BIRCH_CHEST_BOAT, Items.BIRCH_BOAT);
		this.offerChestBoatRecipe(Items.DARK_OAK_CHEST_BOAT, Items.DARK_OAK_BOAT);
		this.offerChestBoatRecipe(Items.JUNGLE_CHEST_BOAT, Items.JUNGLE_BOAT);
		this.offerChestBoatRecipe(Items.OAK_CHEST_BOAT, Items.OAK_BOAT);
		this.offerChestBoatRecipe(Items.SPRUCE_CHEST_BOAT, Items.SPRUCE_BOAT);
		this.offerChestBoatRecipe(Items.MANGROVE_CHEST_BOAT, Items.MANGROVE_BOAT);
		this.createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_QUARTZ_BLOCK, Ingredient.ofItem(Blocks.QUARTZ_SLAB))
			.criterion("has_chiseled_quartz_block", this.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", this.conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", this.conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(this.exporter);
		this.createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_STONE_BRICKS, Ingredient.ofItem(Blocks.STONE_BRICK_SLAB))
			.criterion("has_tag", this.conditionsFromTag(ItemTags.STONE_BRICKS))
			.offerTo(this.exporter);
		this.offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CLAY, Items.CLAY_BALL);
		this.createShaped(RecipeCategory.TOOLS, Items.CLOCK)
			.input('#', Items.GOLD_INGOT)
			.input('X', Items.REDSTONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", this.conditionsFromItem(Items.REDSTONE))
			.offerTo(this.exporter);
		this.offerReversibleCompactingRecipes(RecipeCategory.MISC, Items.COAL, RecipeCategory.BUILDING_BLOCKS, Items.COAL_BLOCK);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.COARSE_DIRT, 4)
			.input('D', Blocks.DIRT)
			.input('G', Blocks.GRAVEL)
			.pattern("DG")
			.pattern("GD")
			.criterion("has_gravel", this.conditionsFromItem(Blocks.GRAVEL))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.COMPARATOR)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('X', Items.QUARTZ)
			.input('I', Blocks.STONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern("III")
			.criterion("has_quartz", this.conditionsFromItem(Items.QUARTZ))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.COMPASS)
			.input('#', Items.IRON_INGOT)
			.input('X', Items.REDSTONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", this.conditionsFromItem(Items.REDSTONE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.FOOD, Items.COOKIE, 8)
			.input('#', Items.WHEAT)
			.input('X', Items.COCOA_BEANS)
			.pattern("#X#")
			.criterion("has_cocoa", this.conditionsFromItem(Items.COCOA_BEANS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.CRAFTING_TABLE)
			.input('#', ItemTags.PLANKS)
			.pattern("##")
			.pattern("##")
			.criterion("unlock_right_away", TickCriterion.Conditions.createTick())
			.showNotification(false)
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.CROSSBOW)
			.input('~', Items.STRING)
			.input('#', Items.STICK)
			.input('&', Items.IRON_INGOT)
			.input('$', Blocks.TRIPWIRE_HOOK)
			.pattern("#&#")
			.pattern("~$~")
			.pattern(" # ")
			.criterion("has_string", this.conditionsFromItem(Items.STRING))
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.criterion("has_tripwire_hook", this.conditionsFromItem(Blocks.TRIPWIRE_HOOK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.LOOM)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.STRING)
			.pattern("@@")
			.pattern("##")
			.criterion("has_string", this.conditionsFromItem(Items.STRING))
			.offerTo(this.exporter);
		this.createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_RED_SANDSTONE, Ingredient.ofItem(Blocks.RED_SANDSTONE_SLAB))
			.criterion("has_red_sandstone", this.conditionsFromItem(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", this.conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_cut_red_sandstone", this.conditionsFromItem(Blocks.CUT_RED_SANDSTONE))
			.offerTo(this.exporter);
		this.offerChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE_SLAB);
		this.offerReversibleCompactingRecipesWithReverseRecipeGroup(
			RecipeCategory.MISC,
			Items.COPPER_INGOT,
			RecipeCategory.BUILDING_BLOCKS,
			Items.COPPER_BLOCK,
			getRecipeName(Items.COPPER_INGOT),
			getItemPath(Items.COPPER_INGOT)
		);
		this.createShapeless(RecipeCategory.MISC, Items.COPPER_INGOT, 9)
			.input(Blocks.WAXED_COPPER_BLOCK)
			.group(getItemPath(Items.COPPER_INGOT))
			.criterion(hasItem(Blocks.WAXED_COPPER_BLOCK), this.conditionsFromItem(Blocks.WAXED_COPPER_BLOCK))
			.offerTo(this.exporter, convertBetween(Items.COPPER_INGOT, Blocks.WAXED_COPPER_BLOCK));
		this.offerWaxingRecipes(FeatureSet.of(FeatureFlags.VANILLA));
		this.createShapeless(RecipeCategory.MISC, Items.CYAN_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.GREEN_DYE)
			.group("cyan_dye")
			.criterion("has_green_dye", this.conditionsFromItem(Items.GREEN_DYE))
			.criterion("has_blue_dye", this.conditionsFromItem(Items.BLUE_DYE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.DARK_PRISMARINE)
			.input('S', Items.PRISMARINE_SHARD)
			.input('I', Items.BLACK_DYE)
			.pattern("SSS")
			.pattern("SIS")
			.pattern("SSS")
			.criterion("has_prismarine_shard", this.conditionsFromItem(Items.PRISMARINE_SHARD))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.DAYLIGHT_DETECTOR)
			.input('Q', Items.QUARTZ)
			.input('G', Blocks.GLASS)
			.input('W', ItemTags.WOODEN_SLABS)
			.pattern("GGG")
			.pattern("QQQ")
			.pattern("WWW")
			.criterion("has_quartz", this.conditionsFromItem(Items.QUARTZ))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICKS, 4)
			.input('S', Blocks.POLISHED_DEEPSLATE)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_polished_deepslate", this.conditionsFromItem(Blocks.POLISHED_DEEPSLATE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, 4)
			.input('S', Blocks.DEEPSLATE_BRICKS)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_deepslate_bricks", this.conditionsFromItem(Blocks.DEEPSLATE_BRICKS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TRANSPORTATION, Blocks.DETECTOR_RAIL, 6)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.STONE_PRESSURE_PLATE)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", this.conditionsFromItem(Blocks.RAIL))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.DIAMOND_AXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.DIAMOND_TOOL_MATERIALS)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_diamond", this.conditionsFromTag(ItemTags.DIAMOND_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.offerReversibleCompactingRecipes(RecipeCategory.MISC, Items.DIAMOND, RecipeCategory.BUILDING_BLOCKS, Items.DIAMOND_BLOCK);
		this.createShaped(RecipeCategory.COMBAT, Items.DIAMOND_BOOTS)
			.input('X', Items.DIAMOND)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", this.conditionsFromItem(Items.DIAMOND))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.DIAMOND_CHESTPLATE)
			.input('X', Items.DIAMOND)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_diamond", this.conditionsFromItem(Items.DIAMOND))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.DIAMOND_HELMET)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_diamond", this.conditionsFromItem(Items.DIAMOND))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.DIAMOND_HOE)
			.input('#', Items.STICK)
			.input('X', ItemTags.DIAMOND_TOOL_MATERIALS)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_diamond", this.conditionsFromTag(ItemTags.DIAMOND_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.DIAMOND_LEGGINGS)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", this.conditionsFromItem(Items.DIAMOND))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.DIAMOND_PICKAXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.DIAMOND_TOOL_MATERIALS)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_diamond", this.conditionsFromTag(ItemTags.DIAMOND_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.DIAMOND_SHOVEL)
			.input('#', Items.STICK)
			.input('X', ItemTags.DIAMOND_TOOL_MATERIALS)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_diamond", this.conditionsFromTag(ItemTags.DIAMOND_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.DIAMOND_SWORD)
			.input('#', Items.STICK)
			.input('X', ItemTags.DIAMOND_TOOL_MATERIALS)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_diamond", this.conditionsFromTag(ItemTags.DIAMOND_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.DIORITE, 2)
			.input('Q', Items.QUARTZ)
			.input('C', Blocks.COBBLESTONE)
			.pattern("CQ")
			.pattern("QC")
			.criterion("has_quartz", this.conditionsFromItem(Items.QUARTZ))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.DISPENSER)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.input('X', Items.BOW)
			.pattern("###")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_bow", this.conditionsFromItem(Items.BOW))
			.offerTo(this.exporter);
		this.offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DRIPSTONE_BLOCK, Items.POINTED_DRIPSTONE);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.DROPPER)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.pattern("# #")
			.pattern("#R#")
			.criterion("has_redstone", this.conditionsFromItem(Items.REDSTONE))
			.offerTo(this.exporter);
		this.offerReversibleCompactingRecipes(RecipeCategory.MISC, Items.EMERALD, RecipeCategory.BUILDING_BLOCKS, Items.EMERALD_BLOCK);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.ENCHANTING_TABLE)
			.input('B', Items.BOOK)
			.input('#', Blocks.OBSIDIAN)
			.input('D', Items.DIAMOND)
			.pattern(" B ")
			.pattern("D#D")
			.pattern("###")
			.criterion("has_obsidian", this.conditionsFromItem(Blocks.OBSIDIAN))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.ENDER_CHEST)
			.input('#', Blocks.OBSIDIAN)
			.input('E', Items.ENDER_EYE)
			.pattern("###")
			.pattern("#E#")
			.pattern("###")
			.criterion("has_ender_eye", this.conditionsFromItem(Items.ENDER_EYE))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.ENDER_EYE)
			.input(Items.ENDER_PEARL)
			.input(Items.BLAZE_POWDER)
			.criterion("has_blaze_powder", this.conditionsFromItem(Items.BLAZE_POWDER))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICKS, 4)
			.input('#', Blocks.END_STONE)
			.pattern("##")
			.pattern("##")
			.criterion("has_end_stone", this.conditionsFromItem(Blocks.END_STONE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Items.END_CRYSTAL)
			.input('T', Items.GHAST_TEAR)
			.input('E', Items.ENDER_EYE)
			.input('G', Blocks.GLASS)
			.pattern("GGG")
			.pattern("GEG")
			.pattern("GTG")
			.criterion("has_ender_eye", this.conditionsFromItem(Items.ENDER_EYE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.END_ROD, 4)
			.input('#', Items.POPPED_CHORUS_FRUIT)
			.input('/', Items.BLAZE_ROD)
			.pattern("/")
			.pattern("#")
			.criterion("has_chorus_fruit_popped", this.conditionsFromItem(Items.POPPED_CHORUS_FRUIT))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.BREWING, Items.FERMENTED_SPIDER_EYE)
			.input(Items.SPIDER_EYE)
			.input(Blocks.BROWN_MUSHROOM)
			.input(Items.SUGAR)
			.criterion("has_spider_eye", this.conditionsFromItem(Items.SPIDER_EYE))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.FIRE_CHARGE, 3)
			.input(Items.GUNPOWDER)
			.input(Items.BLAZE_POWDER)
			.input(Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.criterion("has_blaze_powder", this.conditionsFromItem(Items.BLAZE_POWDER))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.FIREWORK_ROCKET, 3)
			.input(Items.GUNPOWDER)
			.input(Items.PAPER)
			.criterion("has_gunpowder", this.conditionsFromItem(Items.GUNPOWDER))
			.offerTo(this.exporter, "firework_rocket_simple");
		this.createShaped(RecipeCategory.TOOLS, Items.FISHING_ROD)
			.input('#', Items.STICK)
			.input('X', Items.STRING)
			.pattern("  #")
			.pattern(" #X")
			.pattern("# X")
			.criterion("has_string", this.conditionsFromItem(Items.STRING))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.TOOLS, Items.FLINT_AND_STEEL)
			.input(Items.IRON_INGOT)
			.input(Items.FLINT)
			.criterion("has_flint", this.conditionsFromItem(Items.FLINT))
			.criterion("has_obsidian", this.conditionsFromItem(Blocks.OBSIDIAN))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.FLOWER_POT)
			.input('#', Items.BRICK)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brick", this.conditionsFromItem(Items.BRICK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.FURNACE)
			.input('#', ItemTags.STONE_CRAFTING_MATERIALS)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion("has_cobblestone", this.conditionsFromTag(ItemTags.STONE_CRAFTING_MATERIALS))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.TRANSPORTATION, Items.FURNACE_MINECART)
			.input(Blocks.FURNACE)
			.input(Items.MINECART)
			.criterion("has_minecart", this.conditionsFromItem(Items.MINECART))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BREWING, Items.GLASS_BOTTLE, 3)
			.input('#', Blocks.GLASS)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_glass", this.conditionsFromItem(Blocks.GLASS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.GLASS_PANE, 16)
			.input('#', Blocks.GLASS)
			.pattern("###")
			.pattern("###")
			.criterion("has_glass", this.conditionsFromItem(Blocks.GLASS))
			.offerTo(this.exporter);
		this.offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.GLOWSTONE, Items.GLOWSTONE_DUST);
		this.createShapeless(RecipeCategory.DECORATIONS, Items.GLOW_ITEM_FRAME)
			.input(Items.ITEM_FRAME)
			.input(Items.GLOW_INK_SAC)
			.criterion("has_item_frame", this.conditionsFromItem(Items.ITEM_FRAME))
			.criterion("has_glow_ink_sac", this.conditionsFromItem(Items.GLOW_INK_SAC))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.FOOD, Items.GOLDEN_APPLE)
			.input('#', Items.GOLD_INGOT)
			.input('X', Items.APPLE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_ingot", this.conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.GOLDEN_AXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.GOLD_TOOL_MATERIALS)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_gold_ingot", this.conditionsFromTag(ItemTags.GOLD_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.GOLDEN_BOOTS)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", this.conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BREWING, Items.GOLDEN_CARROT)
			.input('#', Items.GOLD_NUGGET)
			.input('X', Items.CARROT)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_nugget", this.conditionsFromItem(Items.GOLD_NUGGET))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.GOLDEN_CHESTPLATE)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_gold_ingot", this.conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.GOLDEN_HELMET)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_gold_ingot", this.conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.GOLDEN_HOE)
			.input('#', Items.STICK)
			.input('X', ItemTags.GOLD_TOOL_MATERIALS)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_gold_ingot", this.conditionsFromTag(ItemTags.GOLD_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.GOLDEN_LEGGINGS)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", this.conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.GOLDEN_PICKAXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.GOLD_TOOL_MATERIALS)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_gold_ingot", this.conditionsFromTag(ItemTags.GOLD_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TRANSPORTATION, Blocks.POWERED_RAIL, 6)
			.input('R', Items.REDSTONE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", this.conditionsFromItem(Blocks.RAIL))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.GOLDEN_SHOVEL)
			.input('#', Items.STICK)
			.input('X', ItemTags.GOLD_TOOL_MATERIALS)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_gold_ingot", this.conditionsFromTag(ItemTags.GOLD_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.GOLDEN_SWORD)
			.input('#', Items.STICK)
			.input('X', ItemTags.GOLD_TOOL_MATERIALS)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_gold_ingot", this.conditionsFromTag(ItemTags.GOLD_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.offerReversibleCompactingRecipesWithReverseRecipeGroup(
			RecipeCategory.MISC, Items.GOLD_INGOT, RecipeCategory.BUILDING_BLOCKS, Items.GOLD_BLOCK, "gold_ingot_from_gold_block", "gold_ingot"
		);
		this.offerReversibleCompactingRecipesWithCompactingRecipeGroup(
			RecipeCategory.MISC, Items.GOLD_NUGGET, RecipeCategory.MISC, Items.GOLD_INGOT, "gold_ingot_from_nuggets", "gold_ingot"
		);
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.GRANITE)
			.input(Blocks.DIORITE)
			.input(Items.QUARTZ)
			.criterion("has_quartz", this.conditionsFromItem(Items.QUARTZ))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.GRAY_DYE, 2)
			.input(Items.BLACK_DYE)
			.input(Items.WHITE_DYE)
			.criterion("has_white_dye", this.conditionsFromItem(Items.WHITE_DYE))
			.criterion("has_black_dye", this.conditionsFromItem(Items.BLACK_DYE))
			.offerTo(this.exporter);
		this.offerCompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.HAY_BLOCK, Items.WHEAT);
		this.offerPressurePlateRecipe(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_INGOT);
		this.createShapeless(RecipeCategory.FOOD, Items.HONEY_BOTTLE, 4)
			.input(Items.HONEY_BLOCK)
			.input(Items.GLASS_BOTTLE, 4)
			.criterion("has_honey_block", this.conditionsFromItem(Blocks.HONEY_BLOCK))
			.offerTo(this.exporter);
		this.offer2x2CompactingRecipe(RecipeCategory.REDSTONE, Blocks.HONEY_BLOCK, Items.HONEY_BOTTLE);
		this.offer2x2CompactingRecipe(RecipeCategory.DECORATIONS, Blocks.HONEYCOMB_BLOCK, Items.HONEYCOMB);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.HOPPER)
			.input('C', Blocks.CHEST)
			.input('I', Items.IRON_INGOT)
			.pattern("I I")
			.pattern("ICI")
			.pattern(" I ")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.TRANSPORTATION, Items.HOPPER_MINECART)
			.input(Blocks.HOPPER)
			.input(Items.MINECART)
			.criterion("has_minecart", this.conditionsFromItem(Items.MINECART))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.IRON_AXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.IRON_TOOL_MATERIALS)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_iron_ingot", this.conditionsFromTag(ItemTags.IRON_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.IRON_BARS, 16)
			.input('#', Items.IRON_INGOT)
			.pattern("###")
			.pattern("###")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.IRON_BOOTS)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.IRON_CHESTPLATE)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createDoorRecipe(Blocks.IRON_DOOR, Ingredient.ofItem(Items.IRON_INGOT))
			.criterion(hasItem(Items.IRON_INGOT), this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.IRON_HELMET)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.IRON_HOE)
			.input('#', Items.STICK)
			.input('X', ItemTags.IRON_TOOL_MATERIALS)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_iron_ingot", this.conditionsFromTag(ItemTags.IRON_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.offerReversibleCompactingRecipesWithReverseRecipeGroup(
			RecipeCategory.MISC, Items.IRON_INGOT, RecipeCategory.BUILDING_BLOCKS, Items.IRON_BLOCK, "iron_ingot_from_iron_block", "iron_ingot"
		);
		this.offerReversibleCompactingRecipesWithCompactingRecipeGroup(
			RecipeCategory.MISC, Items.IRON_NUGGET, RecipeCategory.MISC, Items.IRON_INGOT, "iron_ingot_from_nuggets", "iron_ingot"
		);
		this.createShaped(RecipeCategory.COMBAT, Items.IRON_LEGGINGS)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.IRON_PICKAXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.IRON_TOOL_MATERIALS)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_iron_ingot", this.conditionsFromTag(ItemTags.IRON_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.IRON_SHOVEL)
			.input('#', Items.STICK)
			.input('X', ItemTags.IRON_TOOL_MATERIALS)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_iron_ingot", this.conditionsFromTag(ItemTags.IRON_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.IRON_SWORD)
			.input('#', Items.STICK)
			.input('X', ItemTags.IRON_TOOL_MATERIALS)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_iron_ingot", this.conditionsFromTag(ItemTags.IRON_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.offer2x2CompactingRecipe(RecipeCategory.REDSTONE, Blocks.IRON_TRAPDOOR, Items.IRON_INGOT);
		this.createShaped(RecipeCategory.DECORATIONS, Items.ITEM_FRAME)
			.input('#', Items.STICK)
			.input('X', Items.LEATHER)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_leather", this.conditionsFromItem(Items.LEATHER))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.JUKEBOX)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.DIAMOND)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_diamond", this.conditionsFromItem(Items.DIAMOND))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.LADDER, 3)
			.input('#', Items.STICK)
			.pattern("# #")
			.pattern("###")
			.pattern("# #")
			.criterion("has_stick", this.conditionsFromItem(Items.STICK))
			.offerTo(this.exporter);
		this.offerReversibleCompactingRecipes(RecipeCategory.MISC, Items.LAPIS_LAZULI, RecipeCategory.BUILDING_BLOCKS, Items.LAPIS_BLOCK);
		this.createShaped(RecipeCategory.TOOLS, Items.LEAD, 2)
			.input('~', Items.STRING)
			.input('O', Items.SLIME_BALL)
			.pattern("~~ ")
			.pattern("~O ")
			.pattern("  ~")
			.criterion("has_slime_ball", this.conditionsFromItem(Items.SLIME_BALL))
			.offerTo(this.exporter);
		this.offer2x2CompactingRecipe(RecipeCategory.MISC, Items.LEATHER, Items.RABBIT_HIDE);
		this.createShaped(RecipeCategory.COMBAT, Items.LEATHER_BOOTS)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", this.conditionsFromItem(Items.LEATHER))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.LEATHER_CHESTPLATE)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_leather", this.conditionsFromItem(Items.LEATHER))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.LEATHER_HELMET)
			.input('X', Items.LEATHER)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", this.conditionsFromItem(Items.LEATHER))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.LEATHER_LEGGINGS)
			.input('X', Items.LEATHER)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", this.conditionsFromItem(Items.LEATHER))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.MISC, Items.LEATHER_HORSE_ARMOR)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", this.conditionsFromItem(Items.LEATHER))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.LECTERN)
			.input('S', ItemTags.WOODEN_SLABS)
			.input('B', Blocks.BOOKSHELF)
			.pattern("SSS")
			.pattern(" B ")
			.pattern(" S ")
			.criterion("has_book", this.conditionsFromItem(Items.BOOK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.LEVER)
			.input('#', Blocks.COBBLESTONE)
			.input('X', Items.STICK)
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", this.conditionsFromItem(Blocks.COBBLESTONE))
			.offerTo(this.exporter);
		this.offerSingleOutputShapelessRecipe(Items.LIGHT_BLUE_DYE, Blocks.BLUE_ORCHID, "light_blue_dye");
		this.createShapeless(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.WHITE_DYE)
			.group("light_blue_dye")
			.criterion("has_blue_dye", this.conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_white_dye", this.conditionsFromItem(Items.WHITE_DYE))
			.offerTo(this.exporter, "light_blue_dye_from_blue_white_dye");
		this.offerSingleOutputShapelessRecipe(Items.LIGHT_GRAY_DYE, Blocks.AZURE_BLUET, "light_gray_dye");
		this.createShapeless(RecipeCategory.MISC, Items.LIGHT_GRAY_DYE, 2)
			.input(Items.GRAY_DYE)
			.input(Items.WHITE_DYE)
			.group("light_gray_dye")
			.criterion("has_gray_dye", this.conditionsFromItem(Items.GRAY_DYE))
			.criterion("has_white_dye", this.conditionsFromItem(Items.WHITE_DYE))
			.offerTo(this.exporter, "light_gray_dye_from_gray_white_dye");
		this.createShapeless(RecipeCategory.MISC, Items.LIGHT_GRAY_DYE, 3)
			.input(Items.BLACK_DYE)
			.input(Items.WHITE_DYE, 2)
			.group("light_gray_dye")
			.criterion("has_white_dye", this.conditionsFromItem(Items.WHITE_DYE))
			.criterion("has_black_dye", this.conditionsFromItem(Items.BLACK_DYE))
			.offerTo(this.exporter, "light_gray_dye_from_black_white_dye");
		this.offerSingleOutputShapelessRecipe(Items.LIGHT_GRAY_DYE, Blocks.OXEYE_DAISY, "light_gray_dye");
		this.offerSingleOutputShapelessRecipe(Items.LIGHT_GRAY_DYE, Blocks.WHITE_TULIP, "light_gray_dye");
		this.offerPressurePlateRecipe(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.LIGHTNING_ROD)
			.input('#', Items.COPPER_INGOT)
			.pattern("#")
			.pattern("#")
			.pattern("#")
			.criterion("has_copper_ingot", this.conditionsFromItem(Items.COPPER_INGOT))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.LIME_DYE, 2)
			.input(Items.GREEN_DYE)
			.input(Items.WHITE_DYE)
			.criterion("has_green_dye", this.conditionsFromItem(Items.GREEN_DYE))
			.criterion("has_white_dye", this.conditionsFromItem(Items.WHITE_DYE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.JACK_O_LANTERN)
			.input('A', Blocks.CARVED_PUMPKIN)
			.input('B', Blocks.TORCH)
			.pattern("A")
			.pattern("B")
			.criterion("has_carved_pumpkin", this.conditionsFromItem(Blocks.CARVED_PUMPKIN))
			.offerTo(this.exporter);
		this.offerSingleOutputShapelessRecipe(Items.MAGENTA_DYE, Blocks.ALLIUM, "magenta_dye");
		this.createShapeless(RecipeCategory.MISC, Items.MAGENTA_DYE, 4)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE, 2)
			.input(Items.WHITE_DYE)
			.group("magenta_dye")
			.criterion("has_blue_dye", this.conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_rose_red", this.conditionsFromItem(Items.RED_DYE))
			.criterion("has_white_dye", this.conditionsFromItem(Items.WHITE_DYE))
			.offerTo(this.exporter, "magenta_dye_from_blue_red_white_dye");
		this.createShapeless(RecipeCategory.MISC, Items.MAGENTA_DYE, 3)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE)
			.input(Items.PINK_DYE)
			.group("magenta_dye")
			.criterion("has_pink_dye", this.conditionsFromItem(Items.PINK_DYE))
			.criterion("has_blue_dye", this.conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_red_dye", this.conditionsFromItem(Items.RED_DYE))
			.offerTo(this.exporter, "magenta_dye_from_blue_red_pink");
		this.offerShapelessRecipe(Items.MAGENTA_DYE, Blocks.LILAC, "magenta_dye", 2);
		this.createShapeless(RecipeCategory.MISC, Items.MAGENTA_DYE, 2)
			.input(Items.PURPLE_DYE)
			.input(Items.PINK_DYE)
			.group("magenta_dye")
			.criterion("has_pink_dye", this.conditionsFromItem(Items.PINK_DYE))
			.criterion("has_purple_dye", this.conditionsFromItem(Items.PURPLE_DYE))
			.offerTo(this.exporter, "magenta_dye_from_purple_and_pink");
		this.offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.MAGMA_BLOCK, Items.MAGMA_CREAM);
		this.createShapeless(RecipeCategory.BREWING, Items.MAGMA_CREAM)
			.input(Items.BLAZE_POWDER)
			.input(Items.SLIME_BALL)
			.criterion("has_blaze_powder", this.conditionsFromItem(Items.BLAZE_POWDER))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.MISC, Items.MAP)
			.input('#', Items.PAPER)
			.input('X', Items.COMPASS)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_compass", this.conditionsFromItem(Items.COMPASS))
			.offerTo(this.exporter);
		this.offerCompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.MELON, Items.MELON_SLICE, "has_melon");
		this.createShapeless(RecipeCategory.MISC, Items.MELON_SEEDS)
			.input(Items.MELON_SLICE)
			.criterion("has_melon", this.conditionsFromItem(Items.MELON_SLICE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TRANSPORTATION, Items.MINECART)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern("###")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE)
			.input(Blocks.COBBLESTONE)
			.input(Blocks.VINE)
			.group("mossy_cobblestone")
			.criterion("has_vine", this.conditionsFromItem(Blocks.VINE))
			.offerTo(this.exporter, convertBetween(Blocks.MOSSY_COBBLESTONE, Blocks.VINE));
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICKS)
			.input(Blocks.STONE_BRICKS)
			.input(Blocks.VINE)
			.group("mossy_stone_bricks")
			.criterion("has_vine", this.conditionsFromItem(Blocks.VINE))
			.offerTo(this.exporter, convertBetween(Blocks.MOSSY_STONE_BRICKS, Blocks.VINE));
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE)
			.input(Blocks.COBBLESTONE)
			.input(Blocks.MOSS_BLOCK)
			.group("mossy_cobblestone")
			.criterion("has_moss_block", this.conditionsFromItem(Blocks.MOSS_BLOCK))
			.offerTo(this.exporter, convertBetween(Blocks.MOSSY_COBBLESTONE, Blocks.MOSS_BLOCK));
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICKS)
			.input(Blocks.STONE_BRICKS)
			.input(Blocks.MOSS_BLOCK)
			.group("mossy_stone_bricks")
			.criterion("has_moss_block", this.conditionsFromItem(Blocks.MOSS_BLOCK))
			.offerTo(this.exporter, convertBetween(Blocks.MOSSY_STONE_BRICKS, Blocks.MOSS_BLOCK));
		this.createShapeless(RecipeCategory.FOOD, Items.MUSHROOM_STEW)
			.input(Blocks.BROWN_MUSHROOM)
			.input(Blocks.RED_MUSHROOM)
			.input(Items.BOWL)
			.criterion("has_mushroom_stew", this.conditionsFromItem(Items.MUSHROOM_STEW))
			.criterion("has_bowl", this.conditionsFromItem(Items.BOWL))
			.criterion("has_brown_mushroom", this.conditionsFromItem(Blocks.BROWN_MUSHROOM))
			.criterion("has_red_mushroom", this.conditionsFromItem(Blocks.RED_MUSHROOM))
			.offerTo(this.exporter);
		Registries.ITEM.stream().forEach(item -> {
			SuspiciousStewIngredient suspiciousStewIngredient = SuspiciousStewIngredient.of(item);
			if (suspiciousStewIngredient != null) {
				this.offerSuspiciousStewRecipe(item, suspiciousStewIngredient);
			}
		});
		this.offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_BRICKS, Items.NETHER_BRICK);
		this.offerCompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_WART_BLOCK, Items.NETHER_WART);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.NOTE_BLOCK)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.REDSTONE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_redstone", this.conditionsFromItem(Items.REDSTONE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.OBSERVER)
			.input('Q', Items.QUARTZ)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.pattern("RRQ")
			.pattern("###")
			.criterion("has_quartz", this.conditionsFromItem(Items.QUARTZ))
			.offerTo(this.exporter);
		this.offerSingleOutputShapelessRecipe(Items.ORANGE_DYE, Blocks.ORANGE_TULIP, "orange_dye");
		this.createShapeless(RecipeCategory.MISC, Items.ORANGE_DYE, 2)
			.input(Items.RED_DYE)
			.input(Items.YELLOW_DYE)
			.group("orange_dye")
			.criterion("has_red_dye", this.conditionsFromItem(Items.RED_DYE))
			.criterion("has_yellow_dye", this.conditionsFromItem(Items.YELLOW_DYE))
			.offerTo(this.exporter, "orange_dye_from_red_yellow");
		this.createShaped(RecipeCategory.DECORATIONS, Items.PAINTING)
			.input('#', Items.STICK)
			.input('X', ItemTags.WOOL)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_wool", this.conditionsFromTag(ItemTags.WOOL))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.MISC, Items.PAPER, 3)
			.input('#', Blocks.SUGAR_CANE)
			.pattern("###")
			.criterion("has_reeds", this.conditionsFromItem(Blocks.SUGAR_CANE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_PILLAR, 2)
			.input('#', Blocks.QUARTZ_BLOCK)
			.pattern("#")
			.pattern("#")
			.criterion("has_chiseled_quartz_block", this.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", this.conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", this.conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(this.exporter);
		this.offerCompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.PACKED_ICE, Blocks.ICE);
		this.offerShapelessRecipe(Items.PINK_DYE, Blocks.PEONY, "pink_dye", 2);
		this.offerSingleOutputShapelessRecipe(Items.PINK_DYE, Blocks.PINK_TULIP, "pink_dye");
		this.createShapeless(RecipeCategory.MISC, Items.PINK_DYE, 2)
			.input(Items.RED_DYE)
			.input(Items.WHITE_DYE)
			.group("pink_dye")
			.criterion("has_white_dye", this.conditionsFromItem(Items.WHITE_DYE))
			.criterion("has_red_dye", this.conditionsFromItem(Items.RED_DYE))
			.offerTo(this.exporter, "pink_dye_from_red_white_dye");
		this.createShaped(RecipeCategory.REDSTONE, Blocks.PISTON)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.input('T', ItemTags.PLANKS)
			.input('X', Items.IRON_INGOT)
			.pattern("TTT")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_redstone", this.conditionsFromItem(Items.REDSTONE))
			.offerTo(this.exporter);
		this.offerPolishedStoneRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BASALT, Blocks.BASALT);
		this.offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE, Items.PRISMARINE_SHARD);
		this.offerCompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_BRICKS, Items.PRISMARINE_SHARD);
		this.createShapeless(RecipeCategory.FOOD, Items.PUMPKIN_PIE)
			.input(Blocks.PUMPKIN)
			.input(Items.SUGAR)
			.input(Items.EGG)
			.criterion("has_carved_pumpkin", this.conditionsFromItem(Blocks.CARVED_PUMPKIN))
			.criterion("has_pumpkin", this.conditionsFromItem(Blocks.PUMPKIN))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.PUMPKIN_SEEDS, 4)
			.input(Blocks.PUMPKIN)
			.criterion("has_pumpkin", this.conditionsFromItem(Blocks.PUMPKIN))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.PURPLE_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE)
			.criterion("has_blue_dye", this.conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_red_dye", this.conditionsFromItem(Items.RED_DYE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.SHULKER_BOX)
			.input('#', Blocks.CHEST)
			.input('-', Items.SHULKER_SHELL)
			.pattern("-")
			.pattern("#")
			.pattern("-")
			.criterion("has_shulker_shell", this.conditionsFromItem(Items.SHULKER_SHELL))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_BLOCK, 4)
			.input('F', Items.POPPED_CHORUS_FRUIT)
			.pattern("FF")
			.pattern("FF")
			.criterion("has_chorus_fruit_popped", this.conditionsFromItem(Items.POPPED_CHORUS_FRUIT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_PILLAR)
			.input('#', Blocks.PURPUR_SLAB)
			.pattern("#")
			.pattern("#")
			.criterion("has_purpur_block", this.conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(this.exporter);
		this.createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_SLAB, Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR))
			.criterion("has_purpur_block", this.conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(this.exporter);
		this.createStairsRecipe(Blocks.PURPUR_STAIRS, Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR))
			.criterion("has_purpur_block", this.conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(this.exporter);
		this.offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_BLOCK, Items.QUARTZ);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_BRICKS, 4)
			.input('#', Blocks.QUARTZ_BLOCK)
			.pattern("##")
			.pattern("##")
			.criterion("has_quartz_block", this.conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(this.exporter);
		this.createSlabRecipe(
				RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_SLAB, Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)
			)
			.criterion("has_chiseled_quartz_block", this.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", this.conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", this.conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(this.exporter);
		this.createStairsRecipe(Blocks.QUARTZ_STAIRS, Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR))
			.criterion("has_chiseled_quartz_block", this.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", this.conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", this.conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.FOOD, Items.RABBIT_STEW)
			.input(Items.BAKED_POTATO)
			.input(Items.COOKED_RABBIT)
			.input(Items.BOWL)
			.input(Items.CARROT)
			.input(Blocks.BROWN_MUSHROOM)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", this.conditionsFromItem(Items.COOKED_RABBIT))
			.offerTo(this.exporter, convertBetween(Items.RABBIT_STEW, Items.BROWN_MUSHROOM));
		this.createShapeless(RecipeCategory.FOOD, Items.RABBIT_STEW)
			.input(Items.BAKED_POTATO)
			.input(Items.COOKED_RABBIT)
			.input(Items.BOWL)
			.input(Items.CARROT)
			.input(Blocks.RED_MUSHROOM)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", this.conditionsFromItem(Items.COOKED_RABBIT))
			.offerTo(this.exporter, convertBetween(Items.RABBIT_STEW, Items.RED_MUSHROOM));
		this.createShaped(RecipeCategory.TRANSPORTATION, Blocks.RAIL, 16)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("X X")
			.criterion("has_minecart", this.conditionsFromItem(Items.MINECART))
			.offerTo(this.exporter);
		this.offerReversibleCompactingRecipes(RecipeCategory.REDSTONE, Items.REDSTONE, RecipeCategory.REDSTONE, Items.REDSTONE_BLOCK);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.REDSTONE_LAMP)
			.input('R', Items.REDSTONE)
			.input('G', Blocks.GLOWSTONE)
			.pattern(" R ")
			.pattern("RGR")
			.pattern(" R ")
			.criterion("has_glowstone", this.conditionsFromItem(Blocks.GLOWSTONE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.REDSTONE_TORCH)
			.input('#', Items.STICK)
			.input('X', Items.REDSTONE)
			.pattern("X")
			.pattern("#")
			.criterion("has_redstone", this.conditionsFromItem(Items.REDSTONE))
			.offerTo(this.exporter);
		this.offerSingleOutputShapelessRecipe(Items.RED_DYE, Items.BEETROOT, "red_dye");
		this.offerSingleOutputShapelessRecipe(Items.RED_DYE, Blocks.POPPY, "red_dye");
		this.offerShapelessRecipe(Items.RED_DYE, Blocks.ROSE_BUSH, "red_dye", 2);
		this.createShapeless(RecipeCategory.MISC, Items.RED_DYE)
			.input(Blocks.RED_TULIP)
			.group("red_dye")
			.criterion("has_red_flower", this.conditionsFromItem(Blocks.RED_TULIP))
			.offerTo(this.exporter, "red_dye_from_tulip");
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_NETHER_BRICKS)
			.input('W', Items.NETHER_WART)
			.input('N', Items.NETHER_BRICK)
			.pattern("NW")
			.pattern("WN")
			.criterion("has_nether_wart", this.conditionsFromItem(Items.NETHER_WART))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE)
			.input('#', Blocks.RED_SAND)
			.pattern("##")
			.pattern("##")
			.criterion("has_sand", this.conditionsFromItem(Blocks.RED_SAND))
			.offerTo(this.exporter);
		this.createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE_SLAB, Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_red_sandstone", this.conditionsFromItem(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", this.conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE))
			.offerTo(this.exporter);
		this.createStairsRecipe(Blocks.RED_SANDSTONE_STAIRS, Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE))
			.criterion("has_red_sandstone", this.conditionsFromItem(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", this.conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_cut_red_sandstone", this.conditionsFromItem(Blocks.CUT_RED_SANDSTONE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.REPEATER)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('X', Items.REDSTONE)
			.input('I', Blocks.STONE)
			.pattern("#X#")
			.pattern("III")
			.criterion("has_redstone_torch", this.conditionsFromItem(Blocks.REDSTONE_TORCH))
			.offerTo(this.exporter);
		this.offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE, Blocks.SAND);
		this.createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE_SLAB, Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE))
			.criterion("has_sandstone", this.conditionsFromItem(Blocks.SANDSTONE))
			.criterion("has_chiseled_sandstone", this.conditionsFromItem(Blocks.CHISELED_SANDSTONE))
			.offerTo(this.exporter);
		this.createStairsRecipe(Blocks.SANDSTONE_STAIRS, Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE))
			.criterion("has_sandstone", this.conditionsFromItem(Blocks.SANDSTONE))
			.criterion("has_chiseled_sandstone", this.conditionsFromItem(Blocks.CHISELED_SANDSTONE))
			.criterion("has_cut_sandstone", this.conditionsFromItem(Blocks.CUT_SANDSTONE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.SEA_LANTERN)
			.input('S', Items.PRISMARINE_SHARD)
			.input('C', Items.PRISMARINE_CRYSTALS)
			.pattern("SCS")
			.pattern("CCC")
			.pattern("SCS")
			.criterion("has_prismarine_crystals", this.conditionsFromItem(Items.PRISMARINE_CRYSTALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.SHEARS)
			.input('#', Items.IRON_INGOT)
			.pattern(" #")
			.pattern("# ")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.SHIELD)
			.input('W', ItemTags.WOODEN_TOOL_MATERIALS)
			.input('o', Items.IRON_INGOT)
			.pattern("WoW")
			.pattern("WWW")
			.pattern(" W ")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.offerReversibleCompactingRecipes(RecipeCategory.MISC, Items.SLIME_BALL, RecipeCategory.REDSTONE, Items.SLIME_BLOCK);
		this.offerCutCopperRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
		this.offerCutCopperRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
		this.offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SNOW_BLOCK, Items.SNOWBALL);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.SNOW, 6)
			.input('#', Blocks.SNOW_BLOCK)
			.pattern("###")
			.criterion("has_snowball", this.conditionsFromItem(Items.SNOWBALL))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.SOUL_CAMPFIRE)
			.input('L', ItemTags.LOGS)
			.input('S', Items.STICK)
			.input('#', ItemTags.SOUL_FIRE_BASE_BLOCKS)
			.pattern(" S ")
			.pattern("S#S")
			.pattern("LLL")
			.criterion("has_soul_sand", this.conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BREWING, Items.GLISTERING_MELON_SLICE)
			.input('#', Items.GOLD_NUGGET)
			.input('X', Items.MELON_SLICE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_melon", this.conditionsFromItem(Items.MELON_SLICE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.SPECTRAL_ARROW, 2)
			.input('#', Items.GLOWSTONE_DUST)
			.input('X', Items.ARROW)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_glowstone_dust", this.conditionsFromItem(Items.GLOWSTONE_DUST))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.SPYGLASS)
			.input('#', Items.AMETHYST_SHARD)
			.input('X', Items.COPPER_INGOT)
			.pattern(" # ")
			.pattern(" X ")
			.pattern(" X ")
			.criterion("has_amethyst_shard", this.conditionsFromItem(Items.AMETHYST_SHARD))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.MISC, Items.STICK, 4)
			.input('#', ItemTags.PLANKS)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_planks", this.conditionsFromTag(ItemTags.PLANKS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.MISC, Items.STICK, 1)
			.input('#', Blocks.BAMBOO)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_bamboo", this.conditionsFromItem(Blocks.BAMBOO))
			.offerTo(this.exporter, "stick_from_bamboo_item");
		this.createShaped(RecipeCategory.REDSTONE, Blocks.STICKY_PISTON)
			.input('P', Blocks.PISTON)
			.input('S', Items.SLIME_BALL)
			.pattern("S")
			.pattern("P")
			.criterion("has_slime_ball", this.conditionsFromItem(Items.SLIME_BALL))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICKS, 4)
			.input('#', Blocks.STONE)
			.pattern("##")
			.pattern("##")
			.criterion("has_stone", this.conditionsFromItem(Blocks.STONE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.STONE_AXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_cobblestone", this.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_SLAB, Ingredient.ofItem(Blocks.STONE_BRICKS))
			.criterion("has_stone_bricks", this.conditionsFromTag(ItemTags.STONE_BRICKS))
			.offerTo(this.exporter);
		this.createStairsRecipe(Blocks.STONE_BRICK_STAIRS, Ingredient.ofItem(Blocks.STONE_BRICKS))
			.criterion("has_stone_bricks", this.conditionsFromTag(ItemTags.STONE_BRICKS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.STONE_HOE)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_cobblestone", this.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.STONE_PICKAXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_cobblestone", this.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.STONE_SHOVEL)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_cobblestone", this.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.offerSlabRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);
		this.createShaped(RecipeCategory.COMBAT, Items.STONE_SWORD)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", this.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.WHITE_WOOL)
			.input('#', Items.STRING)
			.pattern("##")
			.pattern("##")
			.criterion("has_string", this.conditionsFromItem(Items.STRING))
			.offerTo(this.exporter, convertBetween(Blocks.WHITE_WOOL, Items.STRING));
		this.offerSingleOutputShapelessRecipe(Items.SUGAR, Blocks.SUGAR_CANE, "sugar");
		this.createShapeless(RecipeCategory.MISC, Items.SUGAR, 3)
			.input(Items.HONEY_BOTTLE)
			.group("sugar")
			.criterion("has_honey_bottle", this.conditionsFromItem(Items.HONEY_BOTTLE))
			.offerTo(this.exporter, convertBetween(Items.SUGAR, Items.HONEY_BOTTLE));
		this.createShaped(RecipeCategory.REDSTONE, Blocks.TARGET)
			.input('H', Items.HAY_BLOCK)
			.input('R', Items.REDSTONE)
			.pattern(" R ")
			.pattern("RHR")
			.pattern(" R ")
			.criterion("has_redstone", this.conditionsFromItem(Items.REDSTONE))
			.criterion("has_hay_block", this.conditionsFromItem(Blocks.HAY_BLOCK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.TNT)
			.input('#', Ingredient.ofItems(Blocks.SAND, Blocks.RED_SAND))
			.input('X', Items.GUNPOWDER)
			.pattern("X#X")
			.pattern("#X#")
			.pattern("X#X")
			.criterion("has_gunpowder", this.conditionsFromItem(Items.GUNPOWDER))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.TRANSPORTATION, Items.TNT_MINECART)
			.input(Blocks.TNT)
			.input(Items.MINECART)
			.criterion("has_minecart", this.conditionsFromItem(Items.MINECART))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.TORCH, 4)
			.input('#', Items.STICK)
			.input('X', Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.pattern("X")
			.pattern("#")
			.criterion("has_stone_pickaxe", this.conditionsFromItem(Items.STONE_PICKAXE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.SOUL_TORCH, 4)
			.input('X', Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.input('#', Items.STICK)
			.input('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
			.pattern("X")
			.pattern("#")
			.pattern("S")
			.criterion("has_soul_sand", this.conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.LANTERN)
			.input('#', Items.TORCH)
			.input('X', Items.IRON_NUGGET)
			.pattern("XXX")
			.pattern("X#X")
			.pattern("XXX")
			.criterion("has_iron_nugget", this.conditionsFromItem(Items.IRON_NUGGET))
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.SOUL_LANTERN)
			.input('#', Items.SOUL_TORCH)
			.input('X', Items.IRON_NUGGET)
			.pattern("XXX")
			.pattern("X#X")
			.pattern("XXX")
			.criterion("has_soul_torch", this.conditionsFromItem(Items.SOUL_TORCH))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.REDSTONE, Blocks.TRAPPED_CHEST)
			.input(Blocks.CHEST)
			.input(Blocks.TRIPWIRE_HOOK)
			.criterion("has_tripwire_hook", this.conditionsFromItem(Blocks.TRIPWIRE_HOOK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Blocks.TRIPWIRE_HOOK, 2)
			.input('#', ItemTags.PLANKS)
			.input('S', Items.STICK)
			.input('I', Items.IRON_INGOT)
			.pattern("I")
			.pattern("S")
			.pattern("#")
			.criterion("has_string", this.conditionsFromItem(Items.STRING))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.TURTLE_HELMET)
			.input('X', Items.TURTLE_SCUTE)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_turtle_scute", this.conditionsFromItem(Items.TURTLE_SCUTE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.WOLF_ARMOR)
			.input('X', Items.ARMADILLO_SCUTE)
			.pattern("X  ")
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_armadillo_scute", this.conditionsFromItem(Items.ARMADILLO_SCUTE))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.WHEAT, 9)
			.input(Blocks.HAY_BLOCK)
			.criterion("has_hay_block", this.conditionsFromItem(Blocks.HAY_BLOCK))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.WHITE_DYE)
			.input(Items.BONE_MEAL)
			.group("white_dye")
			.criterion("has_bone_meal", this.conditionsFromItem(Items.BONE_MEAL))
			.offerTo(this.exporter);
		this.offerSingleOutputShapelessRecipe(Items.WHITE_DYE, Blocks.LILY_OF_THE_VALLEY, "white_dye");
		this.createShaped(RecipeCategory.TOOLS, Items.WOODEN_AXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.WOODEN_TOOL_MATERIALS)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_stick", this.conditionsFromItem(Items.STICK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.WOODEN_HOE)
			.input('#', Items.STICK)
			.input('X', ItemTags.WOODEN_TOOL_MATERIALS)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_stick", this.conditionsFromItem(Items.STICK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.WOODEN_PICKAXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.WOODEN_TOOL_MATERIALS)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_stick", this.conditionsFromItem(Items.STICK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.TOOLS, Items.WOODEN_SHOVEL)
			.input('#', Items.STICK)
			.input('X', ItemTags.WOODEN_TOOL_MATERIALS)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_stick", this.conditionsFromItem(Items.STICK))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.WOODEN_SWORD)
			.input('#', Items.STICK)
			.input('X', ItemTags.WOODEN_TOOL_MATERIALS)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_stick", this.conditionsFromItem(Items.STICK))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.WRITABLE_BOOK)
			.input(Items.BOOK)
			.input(Items.INK_SAC)
			.input(Items.FEATHER)
			.criterion("has_book", this.conditionsFromItem(Items.BOOK))
			.offerTo(this.exporter);
		this.offerSingleOutputShapelessRecipe(Items.YELLOW_DYE, Blocks.DANDELION, "yellow_dye");
		this.offerShapelessRecipe(Items.YELLOW_DYE, Blocks.SUNFLOWER, "yellow_dye", 2);
		this.offerReversibleCompactingRecipes(RecipeCategory.FOOD, Items.DRIED_KELP, RecipeCategory.BUILDING_BLOCKS, Items.DRIED_KELP_BLOCK);
		this.createShaped(RecipeCategory.MISC, Blocks.CONDUIT)
			.input('#', Items.NAUTILUS_SHELL)
			.input('X', Items.HEART_OF_THE_SEA)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_nautilus_core", this.conditionsFromItem(Items.HEART_OF_THE_SEA))
			.criterion("has_nautilus_shell", this.conditionsFromItem(Items.NAUTILUS_SHELL))
			.offerTo(this.exporter);
		this.offerWallRecipe(RecipeCategory.DECORATIONS, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
		this.offerWallRecipe(RecipeCategory.DECORATIONS, Blocks.STONE_BRICK_WALL, Blocks.STONE_BRICKS);
		this.offerWallRecipe(RecipeCategory.DECORATIONS, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
		this.createShapeless(RecipeCategory.MISC, Items.FIELD_MASONED_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Blocks.BRICKS)
			.criterion("has_bricks", this.conditionsFromItem(Blocks.BRICKS))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.BORDURE_INDENTED_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Blocks.VINE)
			.criterion("has_vines", this.conditionsFromItem(Blocks.VINE))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.CREEPER_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.CREEPER_HEAD)
			.criterion("has_creeper_head", this.conditionsFromItem(Items.CREEPER_HEAD))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.SKULL_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.WITHER_SKELETON_SKULL)
			.criterion("has_wither_skeleton_skull", this.conditionsFromItem(Items.WITHER_SKELETON_SKULL))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.FLOWER_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Blocks.OXEYE_DAISY)
			.criterion("has_oxeye_daisy", this.conditionsFromItem(Blocks.OXEYE_DAISY))
			.offerTo(this.exporter);
		this.createShapeless(RecipeCategory.MISC, Items.MOJANG_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.ENCHANTED_GOLDEN_APPLE)
			.criterion("has_enchanted_golden_apple", this.conditionsFromItem(Items.ENCHANTED_GOLDEN_APPLE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.SCAFFOLDING, 6)
			.input('~', Items.STRING)
			.input('I', Blocks.BAMBOO)
			.pattern("I~I")
			.pattern("I I")
			.pattern("I I")
			.criterion("has_bamboo", this.conditionsFromItem(Blocks.BAMBOO))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.GRINDSTONE)
			.input('I', Items.STICK)
			.input('-', Blocks.STONE_SLAB)
			.input('#', ItemTags.PLANKS)
			.pattern("I-I")
			.pattern("# #")
			.criterion("has_stone_slab", this.conditionsFromItem(Blocks.STONE_SLAB))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.BLAST_FURNACE)
			.input('#', Blocks.SMOOTH_STONE)
			.input('X', Blocks.FURNACE)
			.input('I', Items.IRON_INGOT)
			.pattern("III")
			.pattern("IXI")
			.pattern("###")
			.criterion("has_smooth_stone", this.conditionsFromItem(Blocks.SMOOTH_STONE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.SMOKER)
			.input('#', ItemTags.LOGS)
			.input('X', Blocks.FURNACE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_furnace", this.conditionsFromItem(Blocks.FURNACE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.CARTOGRAPHY_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.PAPER)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_paper", this.conditionsFromItem(Items.PAPER))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.SMITHING_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.IRON_INGOT)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.FLETCHING_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.FLINT)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_flint", this.conditionsFromItem(Items.FLINT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.STONECUTTER)
			.input('I', Items.IRON_INGOT)
			.input('#', Blocks.STONE)
			.pattern(" I ")
			.pattern("###")
			.criterion("has_stone", this.conditionsFromItem(Blocks.STONE))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.LODESTONE)
			.input('S', Items.CHISELED_STONE_BRICKS)
			.input('#', Items.NETHERITE_INGOT)
			.pattern("SSS")
			.pattern("S#S")
			.pattern("SSS")
			.criterion("has_netherite_ingot", this.conditionsFromItem(Items.NETHERITE_INGOT))
			.offerTo(this.exporter);
		this.offerReversibleCompactingRecipesWithReverseRecipeGroup(
			RecipeCategory.MISC, Items.NETHERITE_INGOT, RecipeCategory.BUILDING_BLOCKS, Items.NETHERITE_BLOCK, "netherite_ingot_from_netherite_block", "netherite_ingot"
		);
		this.createShapeless(RecipeCategory.MISC, Items.NETHERITE_INGOT)
			.input(Items.NETHERITE_SCRAP, 4)
			.input(Items.GOLD_INGOT, 4)
			.group("netherite_ingot")
			.criterion("has_netherite_scrap", this.conditionsFromItem(Items.NETHERITE_SCRAP))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.RESPAWN_ANCHOR)
			.input('O', Blocks.CRYING_OBSIDIAN)
			.input('G', Blocks.GLOWSTONE)
			.pattern("OOO")
			.pattern("GGG")
			.pattern("OOO")
			.criterion("has_obsidian", this.conditionsFromItem(Blocks.CRYING_OBSIDIAN))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Blocks.CHAIN)
			.input('I', Items.IRON_INGOT)
			.input('N', Items.IRON_NUGGET)
			.pattern("N")
			.pattern("I")
			.pattern("N")
			.criterion("has_iron_nugget", this.conditionsFromItem(Items.IRON_NUGGET))
			.criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.TINTED_GLASS, 2)
			.input('G', Blocks.GLASS)
			.input('S', Items.AMETHYST_SHARD)
			.pattern(" S ")
			.pattern("SGS")
			.pattern(" S ")
			.criterion("has_amethyst_shard", this.conditionsFromItem(Items.AMETHYST_SHARD))
			.offerTo(this.exporter);
		this.offer2x2CompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.AMETHYST_BLOCK, Items.AMETHYST_SHARD);
		this.createShaped(RecipeCategory.TOOLS, Items.RECOVERY_COMPASS)
			.input('C', Items.COMPASS)
			.input('S', Items.ECHO_SHARD)
			.pattern("SSS")
			.pattern("SCS")
			.pattern("SSS")
			.criterion("has_echo_shard", this.conditionsFromItem(Items.ECHO_SHARD))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.REDSTONE, Items.CALIBRATED_SCULK_SENSOR)
			.input('#', Items.AMETHYST_SHARD)
			.input('X', Items.SCULK_SENSOR)
			.pattern(" # ")
			.pattern("#X#")
			.criterion("has_amethyst_shard", this.conditionsFromItem(Items.AMETHYST_SHARD))
			.offerTo(this.exporter);
		this.offerCompactingRecipe(RecipeCategory.MISC, Items.MUSIC_DISC_5, Items.DISC_FRAGMENT_5);
		ComplexRecipeJsonBuilder.create(ArmorDyeRecipe::new).offerTo(this.exporter, "armor_dye");
		ComplexRecipeJsonBuilder.create(BannerDuplicateRecipe::new).offerTo(this.exporter, "banner_duplicate");
		ComplexRecipeJsonBuilder.create(BookCloningRecipe::new).offerTo(this.exporter, "book_cloning");
		ComplexRecipeJsonBuilder.create(FireworkRocketRecipe::new).offerTo(this.exporter, "firework_rocket");
		ComplexRecipeJsonBuilder.create(FireworkStarRecipe::new).offerTo(this.exporter, "firework_star");
		ComplexRecipeJsonBuilder.create(FireworkStarFadeRecipe::new).offerTo(this.exporter, "firework_star_fade");
		ComplexRecipeJsonBuilder.create(MapCloningRecipe::new).offerTo(this.exporter, "map_cloning");
		ComplexRecipeJsonBuilder.create(MapExtendingRecipe::new).offerTo(this.exporter, "map_extending");
		ComplexRecipeJsonBuilder.create(RepairItemRecipe::new).offerTo(this.exporter, "repair_item");
		ComplexRecipeJsonBuilder.create(ShieldDecorationRecipe::new).offerTo(this.exporter, "shield_decoration");
		ComplexRecipeJsonBuilder.create(ShulkerBoxColoringRecipe::new).offerTo(this.exporter, "shulker_box_coloring");
		ComplexRecipeJsonBuilder.create(TippedArrowRecipe::new).offerTo(this.exporter, "tipped_arrow");
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.35F, 200)
			.criterion("has_potato", this.conditionsFromItem(Items.POTATO))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Items.CLAY_BALL), RecipeCategory.MISC, Items.BRICK, 0.3F, 200)
			.criterion("has_clay_ball", this.conditionsFromItem(Items.CLAY_BALL))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(this.ingredientFromTag(ItemTags.LOGS_THAT_BURN), RecipeCategory.MISC, Items.CHARCOAL, 0.15F, 200)
			.criterion("has_log", this.conditionsFromTag(ItemTags.LOGS_THAT_BURN))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Items.CHORUS_FRUIT), RecipeCategory.MISC, Items.POPPED_CHORUS_FRUIT, 0.1F, 200)
			.criterion("has_chorus_fruit", this.conditionsFromItem(Items.CHORUS_FRUIT))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.35F, 200)
			.criterion("has_beef", this.conditionsFromItem(Items.BEEF))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.35F, 200)
			.criterion("has_chicken", this.conditionsFromItem(Items.CHICKEN))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.35F, 200)
			.criterion("has_cod", this.conditionsFromItem(Items.COD))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.KELP), RecipeCategory.FOOD, Items.DRIED_KELP, 0.1F, 200)
			.criterion("has_kelp", this.conditionsFromItem(Blocks.KELP))
			.offerTo(this.exporter, getSmeltingItemPath(Items.DRIED_KELP));
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.35F, 200)
			.criterion("has_salmon", this.conditionsFromItem(Items.SALMON))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.35F, 200)
			.criterion("has_mutton", this.conditionsFromItem(Items.MUTTON))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.35F, 200)
			.criterion("has_porkchop", this.conditionsFromItem(Items.PORKCHOP))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.35F, 200)
			.criterion("has_rabbit", this.conditionsFromItem(Items.RABBIT))
			.offerTo(this.exporter);
		this.offerSmelting(COAL_ORES, RecipeCategory.MISC, Items.COAL, 0.1F, 200, "coal");
		this.offerSmelting(IRON_ORES, RecipeCategory.MISC, Items.IRON_INGOT, 0.7F, 200, "iron_ingot");
		this.offerSmelting(COPPER_ORES, RecipeCategory.MISC, Items.COPPER_INGOT, 0.7F, 200, "copper_ingot");
		this.offerSmelting(GOLD_ORES, RecipeCategory.MISC, Items.GOLD_INGOT, 1.0F, 200, "gold_ingot");
		this.offerSmelting(DIAMOND_ORES, RecipeCategory.MISC, Items.DIAMOND, 1.0F, 200, "diamond");
		this.offerSmelting(LAPIS_ORES, RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.2F, 200, "lapis_lazuli");
		this.offerSmelting(REDSTONE_ORES, RecipeCategory.REDSTONE, Items.REDSTONE, 0.7F, 200, "redstone");
		this.offerSmelting(EMERALD_ORES, RecipeCategory.MISC, Items.EMERALD, 1.0F, 200, "emerald");
		this.offerReversibleCompactingRecipes(RecipeCategory.MISC, Items.RAW_IRON, RecipeCategory.BUILDING_BLOCKS, Items.RAW_IRON_BLOCK);
		this.offerReversibleCompactingRecipes(RecipeCategory.MISC, Items.RAW_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.RAW_COPPER_BLOCK);
		this.offerReversibleCompactingRecipes(RecipeCategory.MISC, Items.RAW_GOLD, RecipeCategory.BUILDING_BLOCKS, Items.RAW_GOLD_BLOCK);
		CookingRecipeJsonBuilder.createSmelting(this.ingredientFromTag(ItemTags.SMELTS_TO_GLASS), RecipeCategory.BUILDING_BLOCKS, Blocks.GLASS.asItem(), 0.1F, 200)
			.criterion("has_smelts_to_glass", this.conditionsFromTag(ItemTags.SMELTS_TO_GLASS))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.SEA_PICKLE), RecipeCategory.MISC, Items.LIME_DYE, 0.1F, 200)
			.criterion("has_sea_pickle", this.conditionsFromItem(Blocks.SEA_PICKLE))
			.offerTo(this.exporter, getSmeltingItemPath(Items.LIME_DYE));
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.CACTUS.asItem()), RecipeCategory.MISC, Items.GREEN_DYE, 1.0F, 200)
			.criterion("has_cactus", this.conditionsFromItem(Blocks.CACTUS))
			.offerTo(this.exporter);
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
			.criterion("has_golden_pickaxe", this.conditionsFromItem(Items.GOLDEN_PICKAXE))
			.criterion("has_golden_shovel", this.conditionsFromItem(Items.GOLDEN_SHOVEL))
			.criterion("has_golden_axe", this.conditionsFromItem(Items.GOLDEN_AXE))
			.criterion("has_golden_hoe", this.conditionsFromItem(Items.GOLDEN_HOE))
			.criterion("has_golden_sword", this.conditionsFromItem(Items.GOLDEN_SWORD))
			.criterion("has_golden_helmet", this.conditionsFromItem(Items.GOLDEN_HELMET))
			.criterion("has_golden_chestplate", this.conditionsFromItem(Items.GOLDEN_CHESTPLATE))
			.criterion("has_golden_leggings", this.conditionsFromItem(Items.GOLDEN_LEGGINGS))
			.criterion("has_golden_boots", this.conditionsFromItem(Items.GOLDEN_BOOTS))
			.criterion("has_golden_horse_armor", this.conditionsFromItem(Items.GOLDEN_HORSE_ARMOR))
			.offerTo(this.exporter, getSmeltingItemPath(Items.GOLD_NUGGET));
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
			.criterion("has_iron_pickaxe", this.conditionsFromItem(Items.IRON_PICKAXE))
			.criterion("has_iron_shovel", this.conditionsFromItem(Items.IRON_SHOVEL))
			.criterion("has_iron_axe", this.conditionsFromItem(Items.IRON_AXE))
			.criterion("has_iron_hoe", this.conditionsFromItem(Items.IRON_HOE))
			.criterion("has_iron_sword", this.conditionsFromItem(Items.IRON_SWORD))
			.criterion("has_iron_helmet", this.conditionsFromItem(Items.IRON_HELMET))
			.criterion("has_iron_chestplate", this.conditionsFromItem(Items.IRON_CHESTPLATE))
			.criterion("has_iron_leggings", this.conditionsFromItem(Items.IRON_LEGGINGS))
			.criterion("has_iron_boots", this.conditionsFromItem(Items.IRON_BOOTS))
			.criterion("has_iron_horse_armor", this.conditionsFromItem(Items.IRON_HORSE_ARMOR))
			.criterion("has_chainmail_helmet", this.conditionsFromItem(Items.CHAINMAIL_HELMET))
			.criterion("has_chainmail_chestplate", this.conditionsFromItem(Items.CHAINMAIL_CHESTPLATE))
			.criterion("has_chainmail_leggings", this.conditionsFromItem(Items.CHAINMAIL_LEGGINGS))
			.criterion("has_chainmail_boots", this.conditionsFromItem(Items.CHAINMAIL_BOOTS))
			.offerTo(this.exporter, getSmeltingItemPath(Items.IRON_NUGGET));
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.CLAY), RecipeCategory.BUILDING_BLOCKS, Blocks.TERRACOTTA.asItem(), 0.35F, 200)
			.criterion("has_clay_block", this.conditionsFromItem(Blocks.CLAY))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.NETHERRACK), RecipeCategory.MISC, Items.NETHER_BRICK, 0.1F, 200)
			.criterion("has_netherrack", this.conditionsFromItem(Blocks.NETHERRACK))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.NETHER_QUARTZ_ORE), RecipeCategory.MISC, Items.QUARTZ, 0.2F, 200)
			.criterion("has_nether_quartz_ore", this.conditionsFromItem(Blocks.NETHER_QUARTZ_ORE))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.WET_SPONGE), RecipeCategory.BUILDING_BLOCKS, Blocks.SPONGE.asItem(), 0.15F, 200)
			.criterion("has_wet_sponge", this.conditionsFromItem(Blocks.WET_SPONGE))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.COBBLESTONE), RecipeCategory.BUILDING_BLOCKS, Blocks.STONE.asItem(), 0.1F, 200)
			.criterion("has_cobblestone", this.conditionsFromItem(Blocks.COBBLESTONE))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.STONE), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_STONE.asItem(), 0.1F, 200)
			.criterion("has_stone", this.conditionsFromItem(Blocks.STONE))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.SANDSTONE), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_SANDSTONE.asItem(), 0.1F, 200)
			.criterion("has_sandstone", this.conditionsFromItem(Blocks.SANDSTONE))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.RED_SANDSTONE), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1F, 200
			)
			.criterion("has_red_sandstone", this.conditionsFromItem(Blocks.RED_SANDSTONE))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.QUARTZ_BLOCK), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_QUARTZ.asItem(), 0.1F, 200)
			.criterion("has_quartz_block", this.conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1F, 200
			)
			.criterion("has_stone_bricks", this.conditionsFromItem(Blocks.STONE_BRICKS))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.BLACK_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_black_terracotta", this.conditionsFromItem(Blocks.BLACK_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.BLUE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_blue_terracotta", this.conditionsFromItem(Blocks.BLUE_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.BROWN_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_brown_terracotta", this.conditionsFromItem(Blocks.BROWN_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.CYAN_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_cyan_terracotta", this.conditionsFromItem(Blocks.CYAN_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.GRAY_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_gray_terracotta", this.conditionsFromItem(Blocks.GRAY_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.GREEN_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_green_terracotta", this.conditionsFromItem(Blocks.GREEN_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.LIGHT_BLUE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_light_blue_terracotta", this.conditionsFromItem(Blocks.LIGHT_BLUE_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.LIGHT_GRAY_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_light_gray_terracotta", this.conditionsFromItem(Blocks.LIGHT_GRAY_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.LIME_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_lime_terracotta", this.conditionsFromItem(Blocks.LIME_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.MAGENTA_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_magenta_terracotta", this.conditionsFromItem(Blocks.MAGENTA_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.ORANGE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_orange_terracotta", this.conditionsFromItem(Blocks.ORANGE_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.PINK_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_pink_terracotta", this.conditionsFromItem(Blocks.PINK_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.PURPLE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_purple_terracotta", this.conditionsFromItem(Blocks.PURPLE_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.RED_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_red_terracotta", this.conditionsFromItem(Blocks.RED_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.WHITE_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_white_terracotta", this.conditionsFromItem(Blocks.WHITE_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(
				Ingredient.ofItem(Blocks.YELLOW_TERRACOTTA), RecipeCategory.DECORATIONS, Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1F, 200
			)
			.criterion("has_yellow_terracotta", this.conditionsFromItem(Blocks.YELLOW_TERRACOTTA))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.ANCIENT_DEBRIS), RecipeCategory.MISC, Items.NETHERITE_SCRAP, 2.0F, 200)
			.criterion("has_ancient_debris", this.conditionsFromItem(Blocks.ANCIENT_DEBRIS))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.BASALT), RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_BASALT, 0.1F, 200)
			.criterion("has_basalt", this.conditionsFromItem(Blocks.BASALT))
			.offerTo(this.exporter);
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(Blocks.COBBLED_DEEPSLATE), RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE, 0.1F, 200)
			.criterion("has_cobbled_deepslate", this.conditionsFromItem(Blocks.COBBLED_DEEPSLATE))
			.offerTo(this.exporter);
		this.offerBlasting(COAL_ORES, RecipeCategory.MISC, Items.COAL, 0.1F, 100, "coal");
		this.offerBlasting(IRON_ORES, RecipeCategory.MISC, Items.IRON_INGOT, 0.7F, 100, "iron_ingot");
		this.offerBlasting(COPPER_ORES, RecipeCategory.MISC, Items.COPPER_INGOT, 0.7F, 100, "copper_ingot");
		this.offerBlasting(GOLD_ORES, RecipeCategory.MISC, Items.GOLD_INGOT, 1.0F, 100, "gold_ingot");
		this.offerBlasting(DIAMOND_ORES, RecipeCategory.MISC, Items.DIAMOND, 1.0F, 100, "diamond");
		this.offerBlasting(LAPIS_ORES, RecipeCategory.MISC, Items.LAPIS_LAZULI, 0.2F, 100, "lapis_lazuli");
		this.offerBlasting(REDSTONE_ORES, RecipeCategory.REDSTONE, Items.REDSTONE, 0.7F, 100, "redstone");
		this.offerBlasting(EMERALD_ORES, RecipeCategory.MISC, Items.EMERALD, 1.0F, 100, "emerald");
		CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItem(Blocks.NETHER_QUARTZ_ORE), RecipeCategory.MISC, Items.QUARTZ, 0.2F, 100)
			.criterion("has_nether_quartz_ore", this.conditionsFromItem(Blocks.NETHER_QUARTZ_ORE))
			.offerTo(this.exporter, getBlastingItemPath(Items.QUARTZ));
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
			.criterion("has_golden_pickaxe", this.conditionsFromItem(Items.GOLDEN_PICKAXE))
			.criterion("has_golden_shovel", this.conditionsFromItem(Items.GOLDEN_SHOVEL))
			.criterion("has_golden_axe", this.conditionsFromItem(Items.GOLDEN_AXE))
			.criterion("has_golden_hoe", this.conditionsFromItem(Items.GOLDEN_HOE))
			.criterion("has_golden_sword", this.conditionsFromItem(Items.GOLDEN_SWORD))
			.criterion("has_golden_helmet", this.conditionsFromItem(Items.GOLDEN_HELMET))
			.criterion("has_golden_chestplate", this.conditionsFromItem(Items.GOLDEN_CHESTPLATE))
			.criterion("has_golden_leggings", this.conditionsFromItem(Items.GOLDEN_LEGGINGS))
			.criterion("has_golden_boots", this.conditionsFromItem(Items.GOLDEN_BOOTS))
			.criterion("has_golden_horse_armor", this.conditionsFromItem(Items.GOLDEN_HORSE_ARMOR))
			.offerTo(this.exporter, getBlastingItemPath(Items.GOLD_NUGGET));
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
			.criterion("has_iron_pickaxe", this.conditionsFromItem(Items.IRON_PICKAXE))
			.criterion("has_iron_shovel", this.conditionsFromItem(Items.IRON_SHOVEL))
			.criterion("has_iron_axe", this.conditionsFromItem(Items.IRON_AXE))
			.criterion("has_iron_hoe", this.conditionsFromItem(Items.IRON_HOE))
			.criterion("has_iron_sword", this.conditionsFromItem(Items.IRON_SWORD))
			.criterion("has_iron_helmet", this.conditionsFromItem(Items.IRON_HELMET))
			.criterion("has_iron_chestplate", this.conditionsFromItem(Items.IRON_CHESTPLATE))
			.criterion("has_iron_leggings", this.conditionsFromItem(Items.IRON_LEGGINGS))
			.criterion("has_iron_boots", this.conditionsFromItem(Items.IRON_BOOTS))
			.criterion("has_iron_horse_armor", this.conditionsFromItem(Items.IRON_HORSE_ARMOR))
			.criterion("has_chainmail_helmet", this.conditionsFromItem(Items.CHAINMAIL_HELMET))
			.criterion("has_chainmail_chestplate", this.conditionsFromItem(Items.CHAINMAIL_CHESTPLATE))
			.criterion("has_chainmail_leggings", this.conditionsFromItem(Items.CHAINMAIL_LEGGINGS))
			.criterion("has_chainmail_boots", this.conditionsFromItem(Items.CHAINMAIL_BOOTS))
			.offerTo(this.exporter, getBlastingItemPath(Items.IRON_NUGGET));
		CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItem(Blocks.ANCIENT_DEBRIS), RecipeCategory.MISC, Items.NETHERITE_SCRAP, 2.0F, 100)
			.criterion("has_ancient_debris", this.conditionsFromItem(Blocks.ANCIENT_DEBRIS))
			.offerTo(this.exporter, getBlastingItemPath(Items.NETHERITE_SCRAP));
		this.generateCookingRecipes("smoking", RecipeSerializer.SMOKING, SmokingRecipe::new, 100);
		this.generateCookingRecipes("campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, CampfireCookingRecipe::new, 600);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_SLAB, Blocks.STONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_STAIRS, Blocks.STONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICKS, Blocks.STONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_SLAB, Blocks.STONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE);
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(Blocks.STONE), RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_STONE_BRICKS)
			.criterion("has_stone", this.conditionsFromItem(Blocks.STONE))
			.offerTo(this.exporter, "chiseled_stone_bricks_stone_from_stonecutting");
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(Blocks.STONE), RecipeCategory.DECORATIONS, Blocks.STONE_BRICK_WALL)
			.criterion("has_stone", this.conditionsFromItem(Blocks.STONE))
			.offerTo(this.exporter, "stone_brick_walls_from_stone_stonecutting");
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE);
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(Blocks.QUARTZ_BLOCK), RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_SLAB, 2)
			.criterion("has_quartz_block", this.conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(this.exporter, "quartz_slab_from_stonecutting");
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_STAIRS, Blocks.QUARTZ_BLOCK);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_BLOCK);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.QUARTZ_BRICKS, Blocks.QUARTZ_BLOCK);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICKS, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICKS);
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(Blocks.STONE_BRICKS), RecipeCategory.DECORATIONS, Blocks.STONE_BRICK_WALL)
			.criterion("has_stone_bricks", this.conditionsFromItem(Blocks.STONE_BRICKS))
			.offerTo(this.exporter, "stone_brick_wall_from_stone_bricks_stonecutting");
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_STONE_BRICKS, Blocks.STONE_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.BRICK_SLAB, Blocks.BRICKS, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.BRICK_STAIRS, Blocks.BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.BRICK_WALL, Blocks.BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.MUD_BRICK_SLAB, Blocks.MUD_BRICKS, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.MUD_BRICK_STAIRS, Blocks.MUD_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.MUD_BRICK_WALL, Blocks.MUD_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICKS, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_NETHER_BRICKS, Blocks.NETHER_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICKS, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.RED_NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_SLAB, Blocks.PURPUR_BLOCK, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_STAIRS, Blocks.PURPUR_BLOCK);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.PURPUR_PILLAR, Blocks.PURPUR_BLOCK);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.PRISMARINE_WALL, Blocks.PRISMARINE);
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(Blocks.PRISMARINE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_BRICK_SLAB, 2)
			.criterion("has_prismarine_brick", this.conditionsFromItem(Blocks.PRISMARINE_BRICKS))
			.offerTo(this.exporter, "prismarine_brick_slab_from_prismarine_stonecutting");
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(Blocks.PRISMARINE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.PRISMARINE_BRICK_STAIRS)
			.criterion("has_prismarine_brick", this.conditionsFromItem(Blocks.PRISMARINE_BRICKS))
			.offerTo(this.exporter, "prismarine_brick_stairs_from_prismarine_stonecutting");
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.ANDESITE_SLAB, Blocks.ANDESITE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.ANDESITE_WALL, Blocks.ANDESITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE, Blocks.ANDESITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_SLAB, Blocks.ANDESITE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.ANDESITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BASALT, Blocks.BASALT);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.GRANITE_SLAB, Blocks.GRANITE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.GRANITE_STAIRS, Blocks.GRANITE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.GRANITE_WALL, Blocks.GRANITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE, Blocks.GRANITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_SLAB, Blocks.GRANITE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_STAIRS, Blocks.GRANITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_GRANITE_STAIRS, Blocks.POLISHED_GRANITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DIORITE_SLAB, Blocks.DIORITE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DIORITE_STAIRS, Blocks.DIORITE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.DIORITE_WALL, Blocks.DIORITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE, Blocks.DIORITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_SLAB, Blocks.DIORITE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_STAIRS, Blocks.DIORITE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DIORITE_STAIRS, Blocks.POLISHED_DIORITE);
		StonecuttingRecipeJsonBuilder.createStonecutting(
				Ingredient.ofItem(Blocks.MOSSY_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICK_SLAB, 2
			)
			.criterion("has_mossy_stone_bricks", this.conditionsFromItem(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(this.exporter, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
		StonecuttingRecipeJsonBuilder.createStonecutting(
				Ingredient.ofItem(Blocks.MOSSY_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_STONE_BRICK_STAIRS
			)
			.criterion("has_mossy_stone_bricks", this.conditionsFromItem(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(this.exporter, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(Blocks.MOSSY_STONE_BRICKS), RecipeCategory.DECORATIONS, Blocks.MOSSY_STONE_BRICK_WALL)
			.criterion("has_mossy_stone_bricks", this.conditionsFromItem(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(this.exporter, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ);
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(Blocks.END_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_SLAB, 2)
			.criterion("has_end_stone_brick", this.conditionsFromItem(Blocks.END_STONE_BRICKS))
			.offerTo(this.exporter, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(Blocks.END_STONE_BRICKS), RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_STAIRS)
			.criterion("has_end_stone_brick", this.conditionsFromItem(Blocks.END_STONE_BRICKS))
			.offerTo(this.exporter, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(Blocks.END_STONE_BRICKS), RecipeCategory.DECORATIONS, Blocks.END_STONE_BRICK_WALL)
			.criterion("has_end_stone_brick", this.conditionsFromItem(Blocks.END_STONE_BRICKS))
			.offerTo(this.exporter, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICKS, Blocks.END_STONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.END_STONE_BRICK_WALL, Blocks.END_STONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.BLACKSTONE_WALL, Blocks.BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.BLACKSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICKS, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER, Blocks.COPPER_BLOCK, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_STAIRS, Blocks.COPPER_BLOCK, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER_SLAB, Blocks.COPPER_BLOCK, 8);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_COPPER, 8);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_COPPER, 8);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_COPPER, 8);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER, Blocks.WAXED_COPPER_BLOCK, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_COPPER_BLOCK, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_COPPER_BLOCK, 8);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_COPPER, 8);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_COPPER, 8);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_COPPER, 8);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.COBBLED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICKS, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_BRICK_WALL, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.COBBLED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.POLISHED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.POLISHED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICKS, Blocks.POLISHED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.POLISHED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_BRICK_WALL, Blocks.POLISHED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.POLISHED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.POLISHED_DEEPSLATE);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_BRICK_WALL, Blocks.DEEPSLATE_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILES, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_TILES);
		streamSmithingTemplates().forEach(template -> this.offerSmithingTrimRecipe(template.template(), template.id()));
		this.offerNetheriteUpgradeRecipe(Items.DIAMOND_CHESTPLATE, RecipeCategory.COMBAT, Items.NETHERITE_CHESTPLATE);
		this.offerNetheriteUpgradeRecipe(Items.DIAMOND_LEGGINGS, RecipeCategory.COMBAT, Items.NETHERITE_LEGGINGS);
		this.offerNetheriteUpgradeRecipe(Items.DIAMOND_HELMET, RecipeCategory.COMBAT, Items.NETHERITE_HELMET);
		this.offerNetheriteUpgradeRecipe(Items.DIAMOND_BOOTS, RecipeCategory.COMBAT, Items.NETHERITE_BOOTS);
		this.offerNetheriteUpgradeRecipe(Items.DIAMOND_SWORD, RecipeCategory.COMBAT, Items.NETHERITE_SWORD);
		this.offerNetheriteUpgradeRecipe(Items.DIAMOND_AXE, RecipeCategory.TOOLS, Items.NETHERITE_AXE);
		this.offerNetheriteUpgradeRecipe(Items.DIAMOND_PICKAXE, RecipeCategory.TOOLS, Items.NETHERITE_PICKAXE);
		this.offerNetheriteUpgradeRecipe(Items.DIAMOND_HOE, RecipeCategory.TOOLS, Items.NETHERITE_HOE);
		this.offerNetheriteUpgradeRecipe(Items.DIAMOND_SHOVEL, RecipeCategory.TOOLS, Items.NETHERITE_SHOVEL);
		this.offerSmithingTemplateCopyingRecipe(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Items.NETHERRACK);
		this.offerSmithingTemplateCopyingRecipe(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
		this.offerSmithingTemplateCopyingRecipe(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SANDSTONE);
		this.offerSmithingTemplateCopyingRecipe(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
		this.offerSmithingTemplateCopyingRecipe(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.MOSSY_COBBLESTONE);
		this.offerSmithingTemplateCopyingRecipe(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
		this.offerSmithingTemplateCopyingRecipe(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.END_STONE);
		this.offerSmithingTemplateCopyingRecipe(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
		this.offerSmithingTemplateCopyingRecipe(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PRISMARINE);
		this.offerSmithingTemplateCopyingRecipe(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BLACKSTONE);
		this.offerSmithingTemplateCopyingRecipe(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.NETHERRACK);
		this.offerSmithingTemplateCopyingRecipe(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PURPUR_BLOCK);
		this.offerSmithingTemplateCopyingRecipe(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
		this.offerSmithingTemplateCopyingRecipe(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
		this.offerSmithingTemplateCopyingRecipe(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
		this.offerSmithingTemplateCopyingRecipe(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
		this.offerSmithingTemplateCopyingRecipe(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TERRACOTTA);
		this.offerSmithingTemplateCopyingRecipe(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BREEZE_ROD);
		this.offerSmithingTemplateCopyingRecipe(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Ingredient.ofItems(Items.COPPER_BLOCK, Items.WAXED_COPPER_BLOCK));
		this.offerCompactingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Items.BAMBOO);
		this.offerPlanksRecipe(Blocks.BAMBOO_PLANKS, ItemTags.BAMBOO_BLOCKS, 2);
		this.offerMosaicRecipe(RecipeCategory.DECORATIONS, Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_SLAB);
		this.offerBoatRecipe(Items.BAMBOO_RAFT, Blocks.BAMBOO_PLANKS);
		this.offerChestBoatRecipe(Items.BAMBOO_CHEST_RAFT, Items.BAMBOO_RAFT);
		this.offerHangingSignRecipe(Items.OAK_HANGING_SIGN, Blocks.STRIPPED_OAK_LOG);
		this.offerHangingSignRecipe(Items.SPRUCE_HANGING_SIGN, Blocks.STRIPPED_SPRUCE_LOG);
		this.offerHangingSignRecipe(Items.BIRCH_HANGING_SIGN, Blocks.STRIPPED_BIRCH_LOG);
		this.offerHangingSignRecipe(Items.JUNGLE_HANGING_SIGN, Blocks.STRIPPED_JUNGLE_LOG);
		this.offerHangingSignRecipe(Items.ACACIA_HANGING_SIGN, Blocks.STRIPPED_ACACIA_LOG);
		this.offerHangingSignRecipe(Items.CHERRY_HANGING_SIGN, Blocks.STRIPPED_CHERRY_LOG);
		this.offerHangingSignRecipe(Items.DARK_OAK_HANGING_SIGN, Blocks.STRIPPED_DARK_OAK_LOG);
		this.offerHangingSignRecipe(Items.MANGROVE_HANGING_SIGN, Blocks.STRIPPED_MANGROVE_LOG);
		this.offerHangingSignRecipe(Items.BAMBOO_HANGING_SIGN, Items.STRIPPED_BAMBOO_BLOCK);
		this.offerHangingSignRecipe(Items.CRIMSON_HANGING_SIGN, Blocks.STRIPPED_CRIMSON_STEM);
		this.offerHangingSignRecipe(Items.WARPED_HANGING_SIGN, Blocks.STRIPPED_WARPED_STEM);
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_BOOKSHELF)
			.input('#', ItemTags.PLANKS)
			.input('X', ItemTags.WOODEN_SLABS)
			.pattern("###")
			.pattern("XXX")
			.pattern("###")
			.criterion("has_book", this.conditionsFromItem(Items.BOOK))
			.offerTo(this.exporter);
		this.offerSingleOutputShapelessRecipe(Items.ORANGE_DYE, Blocks.TORCHFLOWER, "orange_dye");
		this.offerShapelessRecipe(Items.CYAN_DYE, Blocks.PITCHER_PLANT, "cyan_dye", 2);
		this.offerPlanksRecipe2(Blocks.CHERRY_PLANKS, ItemTags.CHERRY_LOGS, 4);
		this.offerBarkBlockRecipe(Blocks.CHERRY_WOOD, Blocks.CHERRY_LOG);
		this.offerBarkBlockRecipe(Blocks.STRIPPED_CHERRY_WOOD, Blocks.STRIPPED_CHERRY_LOG);
		this.offerBoatRecipe(Items.CHERRY_BOAT, Blocks.CHERRY_PLANKS);
		this.offerChestBoatRecipe(Items.CHERRY_CHEST_BOAT, Items.CHERRY_BOAT);
		this.offerShapelessRecipe(Items.PINK_DYE, Items.PINK_PETALS, "pink_dye", 1);
		this.createShaped(RecipeCategory.TOOLS, Items.BRUSH)
			.input('X', Items.FEATHER)
			.input('#', Items.COPPER_INGOT)
			.input('I', Items.STICK)
			.pattern("X")
			.pattern("#")
			.pattern("I")
			.criterion("has_copper_ingot", this.conditionsFromItem(Items.COPPER_INGOT))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.DECORATIONS, Items.DECORATED_POT)
			.input('#', Items.BRICK)
			.pattern(" # ")
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brick", this.conditionsFromTag(ItemTags.DECORATED_POT_INGREDIENTS))
			.offerTo(this.exporter, "decorated_pot_simple");
		ComplexRecipeJsonBuilder.create(CraftingDecoratedPotRecipe::new).offerTo(this.exporter, "decorated_pot");
		this.createShaped(RecipeCategory.REDSTONE, Blocks.CRAFTER)
			.input('#', Items.IRON_INGOT)
			.input('C', Items.CRAFTING_TABLE)
			.input('R', Items.REDSTONE)
			.input('D', Items.DROPPER)
			.pattern("###")
			.pattern("#C#")
			.pattern("RDR")
			.criterion("has_dropper", this.conditionsFromItem(Items.DROPPER))
			.offerTo(this.exporter);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_SLAB, Blocks.TUFF, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_STAIRS, Blocks.TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.TUFF_WALL, Blocks.TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF, Blocks.TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF, Blocks.TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_SLAB, Blocks.TUFF, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_STAIRS, Blocks.TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.POLISHED_TUFF_WALL, Blocks.TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICKS, Blocks.TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_SLAB, Blocks.TUFF, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_STAIRS, Blocks.TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.TUFF_BRICK_WALL, Blocks.TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_SLAB, Blocks.POLISHED_TUFF, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_TUFF_STAIRS, Blocks.POLISHED_TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.POLISHED_TUFF_WALL, Blocks.POLISHED_TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICKS, Blocks.POLISHED_TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_SLAB, Blocks.POLISHED_TUFF, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_STAIRS, Blocks.POLISHED_TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.TUFF_BRICK_WALL, Blocks.POLISHED_TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.POLISHED_TUFF);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_SLAB, Blocks.TUFF_BRICKS, 2);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF_BRICK_STAIRS, Blocks.TUFF_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.DECORATIONS, Blocks.TUFF_BRICK_WALL, Blocks.TUFF_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_TUFF_BRICKS, Blocks.TUFF_BRICKS);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_COPPER, Blocks.COPPER_BLOCK, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CHISELED_COPPER, Blocks.EXPOSED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CHISELED_COPPER, Blocks.WEATHERED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CHISELED_COPPER, Blocks.OXIDIZED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CHISELED_COPPER, Blocks.WAXED_COPPER_BLOCK, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_COPPER, Blocks.CUT_COPPER, 1);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CHISELED_COPPER, Blocks.EXPOSED_CUT_COPPER, 1);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CHISELED_COPPER, Blocks.WEATHERED_CUT_COPPER, 1);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CHISELED_COPPER, Blocks.OXIDIZED_CUT_COPPER, 1);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CHISELED_COPPER, Blocks.WAXED_CUT_COPPER, 1);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, 1);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, 1);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER, 1);
		this.offerGrateRecipe(Blocks.COPPER_GRATE, Blocks.COPPER_BLOCK);
		this.offerGrateRecipe(Blocks.EXPOSED_COPPER_GRATE, Blocks.EXPOSED_COPPER);
		this.offerGrateRecipe(Blocks.WEATHERED_COPPER_GRATE, Blocks.WEATHERED_COPPER);
		this.offerGrateRecipe(Blocks.OXIDIZED_COPPER_GRATE, Blocks.OXIDIZED_COPPER);
		this.offerGrateRecipe(Blocks.WAXED_COPPER_GRATE, Blocks.WAXED_COPPER_BLOCK);
		this.offerGrateRecipe(Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER);
		this.offerGrateRecipe(Blocks.WAXED_WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER);
		this.offerGrateRecipe(Blocks.WAXED_OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER);
		this.offerBulbRecipe(Blocks.COPPER_BULB, Blocks.COPPER_BLOCK);
		this.offerBulbRecipe(Blocks.EXPOSED_COPPER_BULB, Blocks.EXPOSED_COPPER);
		this.offerBulbRecipe(Blocks.WEATHERED_COPPER_BULB, Blocks.WEATHERED_COPPER);
		this.offerBulbRecipe(Blocks.OXIDIZED_COPPER_BULB, Blocks.OXIDIZED_COPPER);
		this.offerBulbRecipe(Blocks.WAXED_COPPER_BULB, Blocks.WAXED_COPPER_BLOCK);
		this.offerBulbRecipe(Blocks.WAXED_EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER);
		this.offerBulbRecipe(Blocks.WAXED_WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER);
		this.offerBulbRecipe(Blocks.WAXED_OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.COPPER_GRATE, Blocks.COPPER_BLOCK, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_COPPER_GRATE, Blocks.EXPOSED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_COPPER_GRATE, Blocks.WEATHERED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_COPPER_GRATE, Blocks.OXIDIZED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_COPPER_GRATE, Blocks.WAXED_COPPER_BLOCK, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER, 4);
		this.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER, 4);
		this.createShapeless(RecipeCategory.MISC, Items.WIND_CHARGE, 4)
			.input(Items.BREEZE_ROD)
			.criterion("has_breeze_rod", this.conditionsFromItem(Items.BREEZE_ROD))
			.offerTo(this.exporter);
		this.createShaped(RecipeCategory.COMBAT, Items.MACE, 1)
			.input('I', Items.BREEZE_ROD)
			.input('#', Blocks.HEAVY_CORE)
			.pattern(" # ")
			.pattern(" I ")
			.criterion("has_breeze_rod", this.conditionsFromItem(Items.BREEZE_ROD))
			.criterion("has_heavy_core", this.conditionsFromItem(Blocks.HEAVY_CORE))
			.offerTo(this.exporter);
		this.createDoorRecipe(Blocks.COPPER_DOOR, Ingredient.ofItem(Items.COPPER_INGOT))
			.criterion(hasItem(Items.COPPER_INGOT), this.conditionsFromItem(Items.COPPER_INGOT))
			.offerTo(this.exporter);
		this.createTrapdoorRecipe(Blocks.COPPER_TRAPDOOR, Ingredient.ofItem(Items.COPPER_INGOT))
			.criterion(hasItem(Items.COPPER_INGOT), this.conditionsFromItem(Items.COPPER_INGOT))
			.offerTo(this.exporter);
	}

	public static Stream<VanillaRecipeGenerator.SmithingTemplate> streamSmithingTemplates() {
		return Stream.of(
				Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
				Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE
			)
			.map(template -> new VanillaRecipeGenerator.SmithingTemplate(template, Identifier.ofVanilla(getItemPath(template) + "_smithing_trim")));
	}

	public static class Provider extends RecipeGenerator.RecipeProvider {
		public Provider(DataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
			super(dataOutput, completableFuture);
		}

		@Override
		protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
			return new VanillaRecipeGenerator(registryLookup, exporter);
		}

		@Override
		public String getName() {
			return "Vanilla Recipes";
		}
	}

	public static record SmithingTemplate(Item template, Identifier id) {
	}
}
