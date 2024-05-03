package net.minecraft.data.server.recipe;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.Criteria;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public abstract class RecipeProvider implements DataProvider {
	final DataOutput.PathResolver recipesPathResolver;
	final DataOutput.PathResolver advancementsPathResolver;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
	private static final Map<BlockFamily.Variant, BiFunction<ItemConvertible, ItemConvertible, CraftingRecipeJsonBuilder>> VARIANT_FACTORIES = ImmutableMap.<BlockFamily.Variant, BiFunction<ItemConvertible, ItemConvertible, CraftingRecipeJsonBuilder>>builder()
		.put(BlockFamily.Variant.BUTTON, (output, input) -> createTransmutationRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.CHISELED, (output, input) -> createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.CUT, (output, input) -> createCutCopperRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.DOOR, (output, input) -> createDoorRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.CUSTOM_FENCE, (output, input) -> createFenceRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.FENCE, (output, input) -> createFenceRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (output, input) -> createFenceGateRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.FENCE_GATE, (output, input) -> createFenceGateRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.SIGN, (output, input) -> createSignRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.SLAB, (output, input) -> createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.STAIRS, (output, input) -> createStairsRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.PRESSURE_PLATE, (output, input) -> createPressurePlateRecipe(RecipeCategory.REDSTONE, output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.POLISHED, (output, input) -> createCondensingRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.TRAPDOOR, (output, input) -> createTrapdoorRecipe(output, Ingredient.ofItems(input)))
		.put(BlockFamily.Variant.WALL, (output, input) -> getWallRecipe(RecipeCategory.DECORATIONS, output, Ingredient.ofItems(input)))
		.build();

	public RecipeProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		this.recipesPathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "recipes");
		this.advancementsPathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "advancements");
		this.registryLookupFuture = registryLookupFuture;
	}

	@Override
	public final CompletableFuture<?> run(DataWriter writer) {
		return this.registryLookupFuture.thenCompose(registryLookup -> this.run(writer, registryLookup));
	}

	protected CompletableFuture<?> run(DataWriter writer, RegistryWrapper.WrapperLookup registryLookup) {
		final Set<Identifier> set = Sets.<Identifier>newHashSet();
		final List<CompletableFuture<?>> list = new ArrayList();
		this.generate(
			new RecipeExporter() {
				@Override
				public void accept(Identifier recipeId, Recipe<?> recipe, @Nullable AdvancementEntry advancement) {
					if (!set.add(recipeId)) {
						throw new IllegalStateException("Duplicate recipe " + recipeId);
					} else {
						list.add(DataProvider.writeCodecToPath(writer, registryLookup, Recipe.CODEC, recipe, RecipeProvider.this.recipesPathResolver.resolveJson(recipeId)));
						if (advancement != null) {
							list.add(
								DataProvider.writeCodecToPath(
									writer, registryLookup, Advancement.CODEC, advancement.value(), RecipeProvider.this.advancementsPathResolver.resolveJson(advancement.id())
								)
							);
						}
					}
				}

				@Override
				public Advancement.Builder getAdvancementBuilder() {
					return Advancement.Builder.createUntelemetered().parent(CraftingRecipeJsonBuilder.ROOT);
				}
			}
		);
		return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
	}

	protected CompletableFuture<?> saveRecipeAdvancement(DataWriter cache, RegistryWrapper.WrapperLookup registryLookup, AdvancementEntry advancement) {
		return DataProvider.writeCodecToPath(
			cache, registryLookup, Advancement.CODEC, advancement.value(), this.advancementsPathResolver.resolveJson(advancement.id())
		);
	}

	protected abstract void generate(RecipeExporter exporter);

	protected static void generateFamilies(RecipeExporter exporter, FeatureSet enabledFeatures) {
		BlockFamilies.getFamilies().filter(BlockFamily::shouldGenerateRecipes).forEach(family -> generateFamily(exporter, family, enabledFeatures));
	}

	protected static void offerSingleOutputShapelessRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input, @Nullable String group) {
		offerShapelessRecipe(exporter, output, input, group, 1);
	}

	protected static void offerShapelessRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input, @Nullable String group, int outputCount) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, outputCount)
			.input(input)
			.group(group)
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter, convertBetween(output, input));
	}

	protected static void offerSmelting(
		RecipeExporter exporter, List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group
	) {
		offerMultipleOptions(exporter, RecipeSerializer.SMELTING, SmeltingRecipe::new, inputs, category, output, experience, cookingTime, group, "_from_smelting");
	}

	protected static void offerBlasting(
		RecipeExporter exporter, List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group
	) {
		offerMultipleOptions(exporter, RecipeSerializer.BLASTING, BlastingRecipe::new, inputs, category, output, experience, cookingTime, group, "_from_blasting");
	}

	private static <T extends AbstractCookingRecipe> void offerMultipleOptions(
		RecipeExporter exporter,
		RecipeSerializer<T> serializer,
		AbstractCookingRecipe.RecipeFactory<T> recipeFactory,
		List<ItemConvertible> inputs,
		RecipeCategory category,
		ItemConvertible output,
		float experience,
		int cookingTime,
		String group,
		String suffix
	) {
		for (ItemConvertible itemConvertible : inputs) {
			CookingRecipeJsonBuilder.create(Ingredient.ofItems(itemConvertible), category, output, experience, cookingTime, serializer, recipeFactory)
				.group(group)
				.criterion(hasItem(itemConvertible), conditionsFromItem(itemConvertible))
				.offerTo(exporter, getItemPath(output) + suffix + "_" + getItemPath(itemConvertible));
		}
	}

	protected static void offerNetheriteUpgradeRecipe(RecipeExporter exporter, Item input, RecipeCategory category, Item result) {
		SmithingTransformRecipeJsonBuilder.create(
				Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.ofItems(input), Ingredient.ofItems(Items.NETHERITE_INGOT), category, result
			)
			.criterion("has_netherite_ingot", conditionsFromItem(Items.NETHERITE_INGOT))
			.offerTo(exporter, getItemPath(result) + "_smithing");
	}

	protected static void offerSmithingTrimRecipe(RecipeExporter exporter, Item template, Identifier recipeId) {
		SmithingTrimRecipeJsonBuilder.create(
				Ingredient.ofItems(template), Ingredient.fromTag(ItemTags.TRIMMABLE_ARMOR), Ingredient.fromTag(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC
			)
			.criterion("has_smithing_trim_template", conditionsFromItem(template))
			.offerTo(exporter, recipeId);
	}

	protected static void offer2x2CompactingRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(category, output, 1)
			.input('#', input)
			.pattern("##")
			.pattern("##")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerCompactingRecipe(
		RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input, String criterionName
	) {
		ShapelessRecipeJsonBuilder.create(category, output).input(input, 9).criterion(criterionName, conditionsFromItem(input)).offerTo(exporter);
	}

	protected static void offerCompactingRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		offerCompactingRecipe(exporter, category, output, input, hasItem(input));
	}

	protected static void offerPlanksRecipe2(RecipeExporter exporter, ItemConvertible output, TagKey<Item> input, int count) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, count)
			.input(input)
			.group("planks")
			.criterion("has_log", conditionsFromTag(input))
			.offerTo(exporter);
	}

	protected static void offerPlanksRecipe(RecipeExporter exporter, ItemConvertible output, TagKey<Item> input, int count) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, count)
			.input(input)
			.group("planks")
			.criterion("has_logs", conditionsFromTag(input))
			.offerTo(exporter);
	}

	protected static void offerBarkBlockRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 3)
			.input('#', input)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerBoatRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, output)
			.input('#', input)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", requireEnteringFluid(Blocks.WATER))
			.offerTo(exporter);
	}

	protected static void offerChestBoatRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
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

	protected static void offerPressurePlateRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
		createPressurePlateRecipe(RecipeCategory.REDSTONE, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	private static CraftingRecipeJsonBuilder createPressurePlateRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output).input('#', input).pattern("##");
	}

	protected static void offerSlabRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		createSlabRecipe(category, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	protected static CraftingRecipeJsonBuilder createSlabRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output, 6).input('#', input).pattern("###");
	}

	protected static CraftingRecipeJsonBuilder createStairsRecipe(ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 4).input('#', input).pattern("#  ").pattern("## ").pattern("###");
	}

	protected static CraftingRecipeJsonBuilder createTrapdoorRecipe(ItemConvertible output, Ingredient input) {
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

	protected static void offerHangingSignRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 6)
			.group("hanging_sign")
			.input('#', input)
			.input('X', Items.CHAIN)
			.pattern("X X")
			.pattern("###")
			.pattern("###")
			.criterion("has_stripped_logs", conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerDyeableRecipes(RecipeExporter exporter, List<Item> dyes, List<Item> dyeables, String group) {
		for (int i = 0; i < dyes.size(); i++) {
			Item item = (Item)dyes.get(i);
			Item item2 = (Item)dyeables.get(i);
			ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, item2)
				.input(item)
				.input(Ingredient.ofStacks(dyeables.stream().filter(dyeable -> !dyeable.equals(item2)).map(ItemStack::new)))
				.group(group)
				.criterion("has_needed_dye", conditionsFromItem(item))
				.offerTo(exporter, "dye_" + getItemPath(item2));
		}
	}

	protected static void offerCarpetRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 3)
			.input('#', input)
			.pattern("##")
			.group("carpet")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerBedRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output)
			.input('#', input)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerBannerRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
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

	protected static void offerStainedGlassDyeingRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
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

	protected static void offerStainedGlassPaneRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 16)
			.input('#', input)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerStainedGlassPaneDyeingRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
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

	protected static void offerTerracottaDyeingRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
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

	protected static void offerConcretePowderDyeingRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 8)
			.input(input)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", conditionsFromItem(Blocks.SAND))
			.criterion("has_gravel", conditionsFromItem(Blocks.GRAVEL))
			.offerTo(exporter);
	}

	protected static void offerCandleDyeingRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
		ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output)
			.input(Blocks.CANDLE)
			.input(input)
			.group("dyed_candle")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerWallRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		getWallRecipe(category, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	private static CraftingRecipeJsonBuilder getWallRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output, 6).input('#', input).pattern("###").pattern("###");
	}

	protected static void offerPolishedStoneRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		createCondensingRecipe(category, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	private static CraftingRecipeJsonBuilder createCondensingRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output, 4).input('S', input).pattern("SS").pattern("SS");
	}

	protected static void offerCutCopperRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		createCutCopperRecipe(category, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	private static ShapedRecipeJsonBuilder createCutCopperRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return ShapedRecipeJsonBuilder.create(category, output, 4).input('#', input).pattern("##").pattern("##");
	}

	protected static void offerChiseledBlockRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		createChiseledBlockRecipe(category, output, Ingredient.ofItems(input)).criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
	}

	protected static void offerMosaicRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
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

	protected static void offerStonecuttingRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		offerStonecuttingRecipe(exporter, category, output, input, 1);
	}

	protected static void offerStonecuttingRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input, int count) {
		SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), category, output, count)
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter, convertBetween(output, input) + "_stonecutting");
	}

	/**
	 * Offers a smelting recipe to the exporter that is used to convert the main block of a block family to its cracked variant.
	 */
	private static void offerCrackingRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
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
		RecipeExporter exporter, RecipeCategory reverseCategory, ItemConvertible baseItem, RecipeCategory compactingCategory, ItemConvertible compactItem
	) {
		offerReversibleCompactingRecipes(
			exporter, reverseCategory, baseItem, compactingCategory, compactItem, getRecipeName(compactItem), null, getRecipeName(baseItem), null
		);
	}

	protected static void offerReversibleCompactingRecipesWithCompactingRecipeGroup(
		RecipeExporter exporter,
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
		RecipeExporter exporter,
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
		RecipeExporter exporter,
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

	protected static void offerSmithingTemplateCopyingRecipe(RecipeExporter exporter, ItemConvertible template, TagKey<Item> resource) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, template, 2)
			.input('#', Items.DIAMOND)
			.input('C', resource)
			.input('S', template)
			.pattern("#S#")
			.pattern("#C#")
			.pattern("###")
			.criterion(hasItem(template), conditionsFromItem(template))
			.offerTo(exporter);
	}

	protected static void offerSmithingTemplateCopyingRecipe(RecipeExporter exporter, ItemConvertible template, ItemConvertible resource) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, template, 2)
			.input('#', Items.DIAMOND)
			.input('C', resource)
			.input('S', template)
			.pattern("#S#")
			.pattern("#C#")
			.pattern("###")
			.criterion(hasItem(template), conditionsFromItem(template))
			.offerTo(exporter);
	}

	protected static <T extends AbstractCookingRecipe> void generateCookingRecipes(
		RecipeExporter exporter, String cooker, RecipeSerializer<T> serializer, AbstractCookingRecipe.RecipeFactory<T> recipeFactory, int cookingTime
	) {
		offerFoodCookingRecipe(exporter, cooker, serializer, recipeFactory, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, recipeFactory, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, recipeFactory, cookingTime, Items.COD, Items.COOKED_COD, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, recipeFactory, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1F);
		offerFoodCookingRecipe(exporter, cooker, serializer, recipeFactory, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, recipeFactory, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, recipeFactory, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, recipeFactory, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35F);
		offerFoodCookingRecipe(exporter, cooker, serializer, recipeFactory, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
	}

	private static <T extends AbstractCookingRecipe> void offerFoodCookingRecipe(
		RecipeExporter exporter,
		String cooker,
		RecipeSerializer<T> serializer,
		AbstractCookingRecipe.RecipeFactory<T> recipeFactory,
		int cookingTime,
		ItemConvertible items,
		ItemConvertible output,
		float experience
	) {
		CookingRecipeJsonBuilder.create(Ingredient.ofItems(items), RecipeCategory.FOOD, output, experience, cookingTime, serializer, recipeFactory)
			.criterion(hasItem(items), conditionsFromItem(items))
			.offerTo(exporter, getItemPath(output) + "_from_" + cooker);
	}

	protected static void offerWaxingRecipes(RecipeExporter exporter, FeatureSet enabledFeatures) {
		((BiMap)HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get())
			.forEach(
				(unwaxed, waxed) -> {
					if (waxed.getRequiredFeatures().isSubsetOf(enabledFeatures)) {
						ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, waxed)
							.input(unwaxed)
							.input(Items.HONEYCOMB)
							.group(getItemPath(waxed))
							.criterion(hasItem(unwaxed), conditionsFromItem(unwaxed))
							.offerTo(exporter, convertBetween(waxed, Items.HONEYCOMB));
					}
				}
			);
	}

	protected static void offerGrateRecipe(RecipeExporter exporter, Block output, Block input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output, 4)
			.input('M', input)
			.pattern(" M ")
			.pattern("M M")
			.pattern(" M ")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void offerBulbRecipe(RecipeExporter exporter, Block output, Block input) {
		ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output, 4)
			.input('C', input)
			.input('R', Items.REDSTONE)
			.input('B', Items.BLAZE_ROD)
			.pattern(" C ")
			.pattern("CBC")
			.pattern(" R ")
			.criterion(hasItem(input), conditionsFromItem(input))
			.offerTo(exporter);
	}

	protected static void generateFamily(RecipeExporter exporter, BlockFamily family, FeatureSet enabledFeatures) {
		family.getVariants()
			.forEach(
				(variant, block) -> {
					if (block.getRequiredFeatures().isSubsetOf(enabledFeatures)) {
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

	private static AdvancementCriterion<EnterBlockCriterion.Conditions> requireEnteringFluid(Block block) {
		return Criteria.ENTER_BLOCK.create(new EnterBlockCriterion.Conditions(Optional.empty(), Optional.of(block.getRegistryEntry()), Optional.empty()));
	}

	private static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItem(NumberRange.IntRange count, ItemConvertible item) {
		return conditionsFromPredicates(ItemPredicate.Builder.create().items(item).count(count));
	}

	protected static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItem(ItemConvertible item) {
		return conditionsFromPredicates(ItemPredicate.Builder.create().items(item));
	}

	protected static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromTag(TagKey<Item> tag) {
		return conditionsFromPredicates(ItemPredicate.Builder.create().tag(tag));
	}

	private static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromPredicates(ItemPredicate.Builder... predicates) {
		return conditionsFromItemPredicates((ItemPredicate[])Arrays.stream(predicates).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
	}

	private static AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItemPredicates(ItemPredicate... predicates) {
		return Criteria.INVENTORY_CHANGED
			.create(new InventoryChangedCriterion.Conditions(Optional.empty(), InventoryChangedCriterion.Conditions.Slots.ANY, List.of(predicates)));
	}

	protected static String hasItem(ItemConvertible item) {
		return "has_" + getItemPath(item);
	}

	protected static String getItemPath(ItemConvertible item) {
		return Registries.ITEM.getId(item.asItem()).getPath();
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
