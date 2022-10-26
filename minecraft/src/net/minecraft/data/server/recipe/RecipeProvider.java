package net.minecraft.data.server.recipe;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RecipeProvider implements DataProvider {
	private final DataOutput.PathResolver recipesPathResolver;
	private final DataOutput.PathResolver advancementsPathResolver;
	private static final Map<BlockFamily.Variant, BiFunction<ItemConvertible, ItemConvertible, CraftingRecipeJsonBuilder>> VARIANT_FACTORIES = ImmutableMap.<BlockFamily.Variant, BiFunction<ItemConvertible, ItemConvertible, CraftingRecipeJsonBuilder>>builder()
		.put(BlockFamily.Variant.BUTTON, (output, input) -> createTransmutationRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.CHISELED, (output, input) -> createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItems(input)))
		.put(
			BlockFamily.Variant.CUT,
			(itemConvertible, itemConvertible2) -> createCutCopperRecipe(RecipeCategory.BUILDING_BLOCKS, itemConvertible, Ingredient.ofItems(itemConvertible2))
		)
		.put(BlockFamily.Variant.DOOR, (output, input) -> createDoorRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.CUSTOM_FENCE, (output, input) -> createFenceRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.FENCE, (output, input) -> createFenceRecipe(output, Ingredient.ofItems(input)))
		.put(
			BlockFamily.Variant.CUSTOM_FENCE_GATE, (itemConvertible, itemConvertible2) -> createFenceGateRecipe(itemConvertible, Ingredient.ofItems(itemConvertible2))
		)
		.put(BlockFamily.Variant.FENCE_GATE, (output, input) -> createFenceGateRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.SIGN, (output, input) -> createSignRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.SLAB, (output, input) -> createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.STAIRS, (output, input) -> createStairsRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.PRESSURE_PLATE, (output, input) -> createPressurePlateRecipe(RecipeCategory.REDSTONE, output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.POLISHED, (output, input) -> createCondensingRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.TRAPDOOR, (output, input) -> createTrapdoorRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.WALL, (output, input) -> getWallRecipe(RecipeCategory.DECORATIONS, output, Ingredient.ofItems(input)))
		.build();

	public RecipeProvider(DataOutput output) {
		this.recipesPathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "recipes");
		this.advancementsPathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "advancements");
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Set<Identifier> set = Sets.<Identifier>newHashSet();
		List<CompletableFuture<?>> list = new ArrayList();
		this.generate(jsonProvider -> {
			if (!set.add(jsonProvider.getRecipeId())) {
				throw new IllegalStateException("Duplicate recipe " + jsonProvider.getRecipeId());
			} else {
				list.add(DataProvider.writeToPath(writer, jsonProvider.toJson(), this.recipesPathResolver.resolveJson(jsonProvider.getRecipeId())));
				JsonObject jsonObject = jsonProvider.toAdvancementJson();
				if (jsonObject != null) {
					list.add(DataProvider.writeToPath(writer, jsonObject, this.advancementsPathResolver.resolveJson(jsonProvider.getAdvancementId())));
				}
			}
		});
		return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
	}

	protected CompletableFuture<?> saveRecipeAdvancement(DataWriter cache, Identifier advancementId, Advancement.Builder advancementBuilder) {
		return DataProvider.writeToPath(cache, advancementBuilder.toJson(), this.advancementsPathResolver.resolveJson(advancementId));
	}

	protected abstract void generate(Consumer<RecipeJsonProvider> exporter);

	protected static void generateFamilies(Consumer<RecipeJsonProvider> exporter, FeatureSet enabledFeatures) {
		BlockFamilies.getFamilies().filter(family -> family.shouldGenerateRecipes(enabledFeatures)).forEach(family -> generateFamily(exporter, family));
	}

	protected static void offerSingleOutputShapelessRecipe(
		Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, @Nullable String group
	) {
		offerShapelessRecipe(exporter, output, input, group, 1);
	}

	protected static void offerShapelessRecipe(
		Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, @Nullable String group, int outputCount
	) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, outputCount)
			.input(input)
			.group(group)
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter, convertBetween(output, input));
	}

	protected static void offerSmelting(
		Consumer<RecipeJsonProvider> exporter,
		List<ItemConvertible> inputs,
		RecipeCategory category,
		ItemConvertible output,
		float experience,
		int cookingTime,
		String group
	) {
		offerMultipleOptions(exporter, RecipeSerializer.SMELTING, inputs, category, output, experience, cookingTime, group, "_from_smelting");
	}

	protected static void offerBlasting(
		Consumer<RecipeJsonProvider> exporter,
		List<ItemConvertible> inputs,
		RecipeCategory category,
		ItemConvertible output,
		float experience,
		int cookingTIme,
		String group
	) {
		offerMultipleOptions(exporter, RecipeSerializer.BLASTING, inputs, category, output, experience, cookingTIme, group, "_from_blasting");
	}

	private static void offerMultipleOptions(
		Consumer<RecipeJsonProvider> exporter,
		RecipeSerializer<? extends AbstractCookingRecipe> serializer,
		List<ItemConvertible> inputs,
		RecipeCategory category,
		ItemConvertible output,
		float experience,
		int cookingTIme,
		String group,
		String method
	) {
		for (ItemConvertible itemConvertible : inputs) {
			CookingRecipeJsonBuilder.create(Ingredient.ofItems(itemConvertible), category, output, experience, cookingTIme, serializer)
				.group(group)
				.criterion(hasItem(itemConvertible), conditionsFromItem(itemConvertible))
				.offerTo(exporter, getItemPath(output) + method + "_" + getItemPath(itemConvertible));
		}
	}

	protected static void offerNetheriteUpgradeRecipe(Consumer<RecipeJsonProvider> exporter, Item input, RecipeCategory category, Item result) {
		SmithingRecipeJsonBuilder.create(Ingredient.ofItems(input), Ingredient.ofItems(Items.NETHERITE_INGOT), category, result)
			.criterion("has_netherite_ingot", conditionsFromItem(Items.NETHERITE_INGOT))
			.offerTo(exporter, getItemPath(result) + "_smithing");
	}

	protected static void offer2x2CompactingRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(category, output, 1)
			.input('#', input)
			.pattern("##")
			.pattern("##")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerPlanksRecipe2(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, TagKey<Item> input) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 4)
			.input(input)
			.group("planks")
			.criterion("has_log", conditionsFromTag(input))
			.offerTo(exporter);
	}

	protected static void offerPlanksRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, TagKey<Item> input) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 4)
			.input(input)
			.group("planks")
			.criterion("has_logs", conditionsFromTag(input))
			.offerTo(exporter);
	}

	protected static void offerBarkBlockRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 3)
			.input('#', input)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerBoatRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, output)
			.input('#', input)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", requireEnteringFluid(Blocks.WATER))
			.offerTo(exporter);
	}

	protected static void offerChestBoatRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, output)
			.input(Blocks.CHEST)
			.input(input)
			.group("chest_boat")
			.criterion("has_boat", conditionsFromTag(ItemTags.BOATS))
			.offerTo(exporter);
	}

	private static CraftingRecipeJsonBuilder createTransmutationRecipe(ItemConvertible output, Ingredient input) {
		return ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output).input(input);
	}

	protected static CraftingRecipeJsonBuilder createDoorRecipe(ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output, 3).input('#', input).pattern("##").pattern("##").pattern("##");
	}

	private static CraftingRecipeJsonBuilder createFenceRecipe(ItemConvertible output, Ingredient input) {
		int i = output == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
		Item item = output == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
		return ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, i).input('W', input).input('#', item).pattern("W#W").pattern("W#W");
	}

	private static CraftingRecipeJsonBuilder createFenceGateRecipe(ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output).input('#', Items.STICK).input('W', input).pattern("#W#").pattern("#W#");
	}

	protected static void offerPressurePlateRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		createPressurePlateRecipe(RecipeCategory.REDSTONE, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	private static CraftingRecipeJsonBuilder createPressurePlateRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output).input('#', input).pattern("##");
	}

	protected static void offerSlabRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		createSlabRecipe(category, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	protected static CraftingRecipeJsonBuilder createSlabRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output, 6).input('#', input).pattern("###");
	}

	protected static CraftingRecipeJsonBuilder createStairsRecipe(ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 4).input('#', input).pattern("#  ").pattern("## ").pattern("###");
	}

	private static CraftingRecipeJsonBuilder createTrapdoorRecipe(ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output, 2).input('#', input).pattern("###").pattern("###");
	}

	private static CraftingRecipeJsonBuilder createSignRecipe(ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 3)
			.group("sign")
			.input('#', input)
			.input('X', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" X ");
	}

	protected static void offerHangingSignRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		offerHangingSignRecipe(exporter, output, input, 6);
	}

	protected static void offerHangingSignRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, int count) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, count)
			.group("hanging_sign")
			.input('#', input)
			.input('X', Items.CHAIN)
			.pattern("X X")
			.pattern("###")
			.pattern("###")
			.criterion("has_stripped_logs", conditionsFromTag(ItemTags.STRIPPED_LOGS))
			.offerTo(exporter);
	}

	protected static void offerWoolDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output)
			.input(input)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", conditionsFromItem(Blocks.WHITE_WOOL))
			.offerTo(exporter);
	}

	protected static void offerCarpetRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 3)
			.input('#', input)
			.pattern("##")
			.group("carpet")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerCarpetDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', input)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", conditionsFromItem(Blocks.WHITE_CARPET))
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter, convertBetween(output, Blocks.WHITE_CARPET));
	}

	protected static void offerBedRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output)
			.input('#', input)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerBedDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output)
			.input(Items.WHITE_BED)
			.input(input)
			.group("dyed_bed")
			.criterion("has_bed", conditionsFromItem(Items.WHITE_BED))
			.offerTo(exporter, convertBetween(output, Items.WHITE_BED));
	}

	protected static void offerBannerRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output)
			.input('#', input)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerStainedGlassDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 8)
			.input('#', Blocks.GLASS)
			.input('X', input)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", conditionsFromItem(Blocks.GLASS))
			.offerTo(exporter);
	}

	protected static void offerStainedGlassPaneRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 16)
			.input('#', input)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerStainedGlassPaneDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', input)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", conditionsFromItem(Blocks.GLASS_PANE))
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter, convertBetween(output, Blocks.GLASS_PANE));
	}

	protected static void offerTerracottaDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', input)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", conditionsFromItem(Blocks.TERRACOTTA))
			.offerTo(exporter);
	}

	protected static void offerConcretePowderDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 8)
			.input(input)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", conditionsFromItem(Blocks.SAND))
			.criterion("has_gravel", conditionsFromItem(Blocks.GRAVEL))
			.offerTo(exporter);
	}

	protected static void offerCandleDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output)
			.input(Blocks.CANDLE)
			.input(input)
			.group("dyed_candle")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerWallRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		getWallRecipe(category, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	private static CraftingRecipeJsonBuilder getWallRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output, 6).input('#', input).pattern("###").pattern("###");
	}

	protected static void offerPolishedStoneRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		createCondensingRecipe(category, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	private static CraftingRecipeJsonBuilder createCondensingRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output, 4).input('S', input).pattern("SS").pattern("SS");
	}

	protected static void offerCutCopperRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		createCutCopperRecipe(category, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	private static ShapedRecipeJsonBuilder createCutCopperRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output, 4).input('#', input).pattern("##").pattern("##");
	}

	protected static void offerChiseledBlockRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		createChiseledBlockRecipe(category, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	protected static void offerMosaicRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(category, output)
			.input('#', input)
			.pattern("#")
			.pattern("#")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static ShapedRecipeJsonBuilder createChiseledBlockRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output).input('#', input).pattern("#").pattern("#");
	}

	protected static void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		offerStonecuttingRecipe(exporter, category, output, input, 1);
	}

	protected static void offerStonecuttingRecipe(
		Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input, int count
	) {
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), category, output, count)
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter, convertBetween(output, input) + "_stonecutting");
	}

	/**
	 * Offers a smelting recipe to the exporter that is used to convert the main block of a block family to its cracked variant.
	 */
	private static void offerCrackingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(input), RecipeCategory.BUILDING_BLOCKS, output, 0.1F, 200)
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	/**
	 * Offers two recipes to convert between a normal and compacted form of an item.
	 * 
	 * <p>The shaped recipe converts 9 items in a square to a compacted form of the item.
	 * <p>The shapeless recipe converts the compacted form to 9 of the normal form.
	 */
	protected static void offerReversibleCompactingRecipes(
		Consumer<RecipeJsonProvider> exporter,
		RecipeCategory reverseCategory,
		ItemConvertible baseItem,
		RecipeCategory compactingCategory,
		ItemConvertible compactItem
	) {
		offerReversibleCompactingRecipes(
			exporter, reverseCategory, baseItem, compactingCategory, compactItem, getRecipeName(compactItem), null, getRecipeName(baseItem), null
		);
	}

	protected static void offerReversibleCompactingRecipesWithCompactingRecipeGroup(
		Consumer<RecipeJsonProvider> exporter,
		RecipeCategory reverseCategory,
		ItemConvertible baseItem,
		RecipeCategory compactingCategory,
		ItemConvertible compactItem,
		String compactingId,
		String compactingGroup
	) {
		offerReversibleCompactingRecipes(
			exporter, reverseCategory, baseItem, compactingCategory, compactItem, compactingId, compactingGroup, getRecipeName(baseItem), null
		);
	}

	protected static void offerReversibleCompactingRecipesWithReverseRecipeGroup(
		Consumer<RecipeJsonProvider> exporter,
		RecipeCategory reverseCategory,
		ItemConvertible baseItem,
		RecipeCategory compactingCategory,
		ItemConvertible compactItem,
		String reverseId,
		String reverseGroup
	) {
		offerReversibleCompactingRecipes(
			exporter, reverseCategory, baseItem, compactingCategory, compactItem, getRecipeName(compactItem), null, reverseId, reverseGroup
		);
	}

	private static void offerReversibleCompactingRecipes(
		Consumer<RecipeJsonProvider> exporter,
		RecipeCategory reverseCategory,
		ItemConvertible baseItem,
		RecipeCategory compactingCategory,
		ItemConvertible compactItem,
		String compactingId,
		@Nullable String compactingGroup,
		String reverseId,
		@Nullable String reverseGroup
	) {
		ShapelessRecipeJsonBuilder.create(reverseCategory, baseItem, 9)
			.input(compactItem)
			.group(reverseGroup)
			.criterion(hasItem(compactItem), conditionsFromItem(compactItem))
			.offerTo(exporter, new Identifier(reverseId));
		ShapedRecipeJsonBuilder.create(compactingCategory, compactItem)
			.input('#', baseItem)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group(compactingGroup)
			.criterion(hasItem(baseItem), conditionsFromItem(baseItem))
			.offerTo(exporter, new Identifier(compactingId));
	}

	protected static void generateCookingRecipes(
		Consumer<RecipeJsonProvider> exporter, String cooker, RecipeSerializer<? extends AbstractCookingRecipe> serializer, int cookingTime
	) {
		offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.COD, Items.COOKED_COD, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1F);
		offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
	}

	private static void offerFoodCookingRecipe(
		Consumer<RecipeJsonProvider> exporter,
		String cooker,
		RecipeSerializer<? extends AbstractCookingRecipe> serializer,
		int cookingTime,
		ItemConvertible input,
		ItemConvertible output,
		float experience
	) {
		CookingRecipeJsonBuilder.create(Ingredient.ofItems(input), RecipeCategory.FOOD, output, experience, cookingTime, serializer)
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter, getItemPath(output) + "_from_" + cooker);
	}

	protected static void offerWaxingRecipes(Consumer<RecipeJsonProvider> exporter) {
		((BiMap)HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get())
			.forEach(
				(input, output) -> ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output)
						.input(input)
						.input(Items.HONEYCOMB)
						.group(getItemPath(output))
						.criterion(hasItem(input), conditionsFromItem(input))
						.offerTo(exporter, convertBetween(output, Items.HONEYCOMB))
			);
	}

	protected static void generateFamily(Consumer<RecipeJsonProvider> exporter, BlockFamily family) {
		family.getVariants()
			.forEach(
				(variant, block) -> {
					BiFunction<ItemConvertible, ItemConvertible, CraftingRecipeJsonBuilder> biFunction = (BiFunction<ItemConvertible, ItemConvertible, CraftingRecipeJsonBuilder>)VARIANT_FACTORIES.get(
						variant
					);
					ItemConvertible itemConvertible = getVariantRecipeInput(family, variant);
					if (biFunction != null) {
						CraftingRecipeJsonBuilder craftingRecipeJsonBuilder = (CraftingRecipeJsonBuilder)biFunction.apply(block, itemConvertible);
						family.getGroup().ifPresent(group -> craftingRecipeJsonBuilder.group(group + (variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getName())));
						craftingRecipeJsonBuilder.criterion(
							(String)family.getUnlockCriterionName().orElseGet(() -> hasItem(itemConvertible)), conditionsFromItem(itemConvertible)
						);
						craftingRecipeJsonBuilder.offerTo(exporter);
					}

					if (variant == BlockFamily.Variant.CRACKED) {
						offerCrackingRecipe(exporter, block, itemConvertible);
					}
				}
			);
	}

	/**
	 * Gets the block used to craft a certain {@linkplain net.minecraft.data.family.BlockFamily.Variant variant} of a base block.
	 * 
	 * <p>Normally, the block used to craft a variant is the base block.
	 * For chiseled variants, this is the slab variant of that block.
	 * 
	 * <p>Purpur is handled separately because both purpur and purpur pillars can be used to craft purpur slabs and stairs.
	 */
	private static Block getVariantRecipeInput(BlockFamily family, BlockFamily.Variant variant) {
		if (variant == BlockFamily.Variant.CHISELED) {
			if (!family.getVariants().containsKey(BlockFamily.Variant.SLAB)) {
				throw new IllegalStateException("Slab is not defined for the family.");
			} else {
				return family.getVariant(BlockFamily.Variant.SLAB);
			}
		} else {
			return family.getBaseBlock();
		}
	}

	private static EnterBlockCriterion.Conditions requireEnteringFluid(Block block) {
		return new EnterBlockCriterion.Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
	}

	private static InventoryChangedCriterion.Conditions conditionsFromItem(NumberRange.IntRange count, ItemConvertible item) {
		return conditionsFromItemPredicates(ItemPredicate.Builder.create().items(item).count(count).build());
	}

	protected static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
		return conditionsFromItemPredicates(ItemPredicate.Builder.create().items(item).build());
	}

	protected static InventoryChangedCriterion.Conditions conditionsFromTag(TagKey<Item> tag) {
		return conditionsFromItemPredicates(ItemPredicate.Builder.create().tag(tag).build());
	}

	private static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate... predicates) {
		return new InventoryChangedCriterion.Conditions(
			EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, predicates
		);
	}

	protected static String hasItem(ItemConvertible item) {
		return "has_" + getItemPath(item);
	}

	protected static String getItemPath(ItemConvertible item) {
		return Registry.ITEM.getId(item.asItem()).getPath();
	}

	protected static String getRecipeName(ItemConvertible item) {
		return getItemPath(item);
	}

	protected static String convertBetween(ItemConvertible to, ItemConvertible from) {
		return getItemPath(to) + "_from_" + getItemPath(from);
	}

	protected static String getSmeltingItemPath(ItemConvertible item) {
		return getItemPath(item) + "_from_smelting";
	}

	protected static String getBlastingItemPath(ItemConvertible item) {
		return getItemPath(item) + "_from_blasting";
	}

	@Override
	public final String getName() {
		return "Recipes";
	}
}