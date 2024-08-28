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
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
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
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public abstract class RecipeGenerator {
	protected final RegistryWrapper.WrapperLookup registries;
	private final RegistryEntryLookup<Item> itemLookup;
	protected final RecipeExporter exporter;
	private static final Map<BlockFamily.Variant, RecipeGenerator.BlockFamilyRecipeFactory> VARIANT_FACTORIES = ImmutableMap.<BlockFamily.Variant, RecipeGenerator.BlockFamilyRecipeFactory>builder()
		.put(BlockFamily.Variant.BUTTON, (generator, output, input) -> generator.createButtonRecipe(output, Ingredient.ofItem(input)))
		.put(
			BlockFamily.Variant.CHISELED,
			(generator, output, input) -> generator.createChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItem(input))
		)
		.put(BlockFamily.Variant.CUT, (generator, output, input) -> generator.createCutCopperRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItem(input)))
		.put(BlockFamily.Variant.DOOR, (generator, output, input) -> generator.createDoorRecipe(output, Ingredient.ofItem(input)))
		.put(BlockFamily.Variant.CUSTOM_FENCE, (generator, output, input) -> generator.createFenceRecipe(output, Ingredient.ofItem(input)))
		.put(BlockFamily.Variant.FENCE, (generator, output, input) -> generator.createFenceRecipe(output, Ingredient.ofItem(input)))
		.put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (generator, output, input) -> generator.createFenceGateRecipe(output, Ingredient.ofItem(input)))
		.put(BlockFamily.Variant.FENCE_GATE, (generator, output, input) -> generator.createFenceGateRecipe(output, Ingredient.ofItem(input)))
		.put(BlockFamily.Variant.SIGN, (generator, output, input) -> generator.createSignRecipe(output, Ingredient.ofItem(input)))
		.put(BlockFamily.Variant.SLAB, (generator, output, input) -> generator.createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItem(input)))
		.put(BlockFamily.Variant.STAIRS, (generator, output, input) -> generator.createStairsRecipe(output, Ingredient.ofItem(input)))
		.put(
			BlockFamily.Variant.PRESSURE_PLATE,
			(generator, output, input) -> generator.createPressurePlateRecipe(RecipeCategory.REDSTONE, output, Ingredient.ofItem(input))
		)
		.put(
			BlockFamily.Variant.POLISHED,
			(generator, output, input) -> generator.createCondensingRecipe(RecipeCategory.BUILDING_BLOCKS, output, Ingredient.ofItem(input))
		)
		.put(BlockFamily.Variant.TRAPDOOR, (generator, output, input) -> generator.createTrapdoorRecipe(output, Ingredient.ofItem(input)))
		.put(BlockFamily.Variant.WALL, (generator, output, input) -> generator.getWallRecipe(RecipeCategory.DECORATIONS, output, Ingredient.ofItem(input)))
		.build();

	protected RecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
		this.registries = registries;
		this.itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
		this.exporter = exporter;
	}

	protected abstract void generate();

	protected void generateFamilies(FeatureSet enabledFeatures) {
		BlockFamilies.getFamilies().filter(BlockFamily::shouldGenerateRecipes).forEach(family -> this.generateFamily(family, enabledFeatures));
	}

	protected void offerSingleOutputShapelessRecipe(ItemConvertible output, ItemConvertible input, @Nullable String group) {
		this.offerShapelessRecipe(output, input, group, 1);
	}

	protected void offerShapelessRecipe(ItemConvertible output, ItemConvertible input, @Nullable String group, int outputCount) {
		this.createShapeless(RecipeCategory.MISC, output, outputCount)
			.input(input)
			.group(group)
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter, convertBetween(output, input));
	}

	protected void offerSmelting(List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
		this.offerMultipleOptions(RecipeSerializer.SMELTING, SmeltingRecipe::new, inputs, category, output, experience, cookingTime, group, "_from_smelting");
	}

	protected void offerBlasting(List<ItemConvertible> inputs, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, String group) {
		this.offerMultipleOptions(RecipeSerializer.BLASTING, BlastingRecipe::new, inputs, category, output, experience, cookingTime, group, "_from_blasting");
	}

	private <T extends AbstractCookingRecipe> void offerMultipleOptions(
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
			CookingRecipeJsonBuilder.create(Ingredient.ofItem(itemConvertible), category, output, experience, cookingTime, serializer, recipeFactory)
				.group(group)
				.criterion(hasItem(itemConvertible), this.conditionsFromItem(itemConvertible))
				.offerTo(this.exporter, getItemPath(output) + suffix + "_" + getItemPath(itemConvertible));
		}
	}

	protected void offerNetheriteUpgradeRecipe(Item input, RecipeCategory category, Item result) {
		SmithingTransformRecipeJsonBuilder.create(
				Ingredient.ofItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
				Ingredient.ofItem(input),
				this.ingredientFromTag(ItemTags.NETHERITE_TOOL_MATERIALS),
				category,
				result
			)
			.criterion("has_netherite_ingot", this.conditionsFromTag(ItemTags.NETHERITE_TOOL_MATERIALS))
			.offerTo(this.exporter, getItemPath(result) + "_smithing");
	}

	protected void offerSmithingTrimRecipe(Item input, Identifier recipeId) {
		SmithingTrimRecipeJsonBuilder.create(
				Ingredient.ofItem(input), this.ingredientFromTag(ItemTags.TRIMMABLE_ARMOR), this.ingredientFromTag(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC
			)
			.criterion("has_smithing_trim_template", this.conditionsFromItem(input))
			.offerTo(this.exporter, recipeId);
	}

	protected void offer2x2CompactingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		this.createShaped(category, output, 1)
			.input('#', input)
			.pattern("##")
			.pattern("##")
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	protected void offerCompactingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input, String criterionName) {
		this.createShapeless(category, output).input(input, 9).criterion(criterionName, this.conditionsFromItem(input)).offerTo(this.exporter);
	}

	protected void offerCompactingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		this.offerCompactingRecipe(category, output, input, hasItem(input));
	}

	protected void offerPlanksRecipe2(ItemConvertible output, TagKey<Item> logTag, int count) {
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, output, count)
			.input(logTag)
			.group("planks")
			.criterion("has_log", this.conditionsFromTag(logTag))
			.offerTo(this.exporter);
	}

	protected void offerPlanksRecipe(ItemConvertible output, TagKey<Item> logTag, int count) {
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, output, count)
			.input(logTag)
			.group("planks")
			.criterion("has_logs", this.conditionsFromTag(logTag))
			.offerTo(this.exporter);
	}

	protected void offerBarkBlockRecipe(ItemConvertible output, ItemConvertible input) {
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 3)
			.input('#', input)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	protected void offerBoatRecipe(ItemConvertible output, ItemConvertible input) {
		this.createShaped(RecipeCategory.TRANSPORTATION, output)
			.input('#', input)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", requireEnteringFluid(Blocks.WATER))
			.offerTo(this.exporter);
	}

	protected void offerChestBoatRecipe(ItemConvertible output, ItemConvertible input) {
		this.createShapeless(RecipeCategory.TRANSPORTATION, output)
			.input(Blocks.CHEST)
			.input(input)
			.group("chest_boat")
			.criterion("has_boat", this.conditionsFromTag(ItemTags.BOATS))
			.offerTo(this.exporter);
	}

	private CraftingRecipeJsonBuilder createButtonRecipe(ItemConvertible output, Ingredient input) {
		return this.createShapeless(RecipeCategory.REDSTONE, output).input(input);
	}

	protected CraftingRecipeJsonBuilder createDoorRecipe(ItemConvertible output, Ingredient input) {
		return this.createShaped(RecipeCategory.REDSTONE, output, 3).input('#', input).pattern("##").pattern("##").pattern("##");
	}

	private CraftingRecipeJsonBuilder createFenceRecipe(ItemConvertible output, Ingredient input) {
		int i = output == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
		Item item = output == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
		return this.createShaped(RecipeCategory.DECORATIONS, output, i).input('W', input).input('#', item).pattern("W#W").pattern("W#W");
	}

	private CraftingRecipeJsonBuilder createFenceGateRecipe(ItemConvertible output, Ingredient input) {
		return this.createShaped(RecipeCategory.REDSTONE, output).input('#', Items.STICK).input('W', input).pattern("#W#").pattern("#W#");
	}

	protected void offerPressurePlateRecipe(ItemConvertible output, ItemConvertible input) {
		this.createPressurePlateRecipe(RecipeCategory.REDSTONE, output, Ingredient.ofItem(input))
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	private CraftingRecipeJsonBuilder createPressurePlateRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return this.createShaped(category, output).input('#', input).pattern("##");
	}

	protected void offerSlabRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		this.createSlabRecipe(category, output, Ingredient.ofItem(input)).criterion(hasItem(input), this.conditionsFromItem(input)).offerTo(this.exporter);
	}

	protected CraftingRecipeJsonBuilder createSlabRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return this.createShaped(category, output, 6).input('#', input).pattern("###");
	}

	protected CraftingRecipeJsonBuilder createStairsRecipe(ItemConvertible output, Ingredient input) {
		return this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 4).input('#', input).pattern("#  ").pattern("## ").pattern("###");
	}

	protected CraftingRecipeJsonBuilder createTrapdoorRecipe(ItemConvertible output, Ingredient input) {
		return this.createShaped(RecipeCategory.REDSTONE, output, 2).input('#', input).pattern("###").pattern("###");
	}

	private CraftingRecipeJsonBuilder createSignRecipe(ItemConvertible output, Ingredient input) {
		return this.createShaped(RecipeCategory.DECORATIONS, output, 3)
			.group("sign")
			.input('#', input)
			.input('X', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" X ");
	}

	protected void offerHangingSignRecipe(ItemConvertible output, ItemConvertible input) {
		this.createShaped(RecipeCategory.DECORATIONS, output, 6)
			.group("hanging_sign")
			.input('#', input)
			.input('X', Items.CHAIN)
			.pattern("X X")
			.pattern("###")
			.pattern("###")
			.criterion("has_stripped_logs", this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	protected void offerDyeableRecipes(List<Item> dyes, List<Item> dyeables, String group) {
		this.offerDyeablesRecipes(dyes, dyeables, null, group, RecipeCategory.BUILDING_BLOCKS);
	}

	protected void offerDyeablesRecipes(List<Item> dyes, List<Item> dyeables, @Nullable Item undyed, String group, RecipeCategory category) {
		for (int i = 0; i < dyes.size(); i++) {
			Item item = (Item)dyes.get(i);
			Item item2 = (Item)dyeables.get(i);
			Stream<Item> stream = dyeables.stream().filter(itemx -> !itemx.equals(item2));
			if (undyed != null) {
				stream = Stream.concat(stream, Stream.of(undyed));
			}

			this.createShapeless(category, item2)
				.input(item)
				.input(Ingredient.ofItems(stream))
				.group(group)
				.criterion("has_needed_dye", this.conditionsFromItem(item))
				.offerTo(this.exporter, "dye_" + getItemPath(item2));
		}
	}

	protected void offerCarpetRecipe(ItemConvertible output, ItemConvertible input) {
		this.createShaped(RecipeCategory.DECORATIONS, output, 3)
			.input('#', input)
			.pattern("##")
			.group("carpet")
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	protected void offerBedRecipe(ItemConvertible output, ItemConvertible inputWool) {
		this.createShaped(RecipeCategory.DECORATIONS, output)
			.input('#', inputWool)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion(hasItem(inputWool), this.conditionsFromItem(inputWool))
			.offerTo(this.exporter);
	}

	protected void offerBannerRecipe(ItemConvertible output, ItemConvertible inputWool) {
		this.createShaped(RecipeCategory.DECORATIONS, output)
			.input('#', inputWool)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion(hasItem(inputWool), this.conditionsFromItem(inputWool))
			.offerTo(this.exporter);
	}

	protected void offerStainedGlassDyeingRecipe(ItemConvertible output, ItemConvertible input) {
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 8)
			.input('#', Blocks.GLASS)
			.input('X', input)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.conditionsFromItem(Blocks.GLASS))
			.offerTo(this.exporter);
	}

	protected void offerStainedGlassPaneRecipe(ItemConvertible output, ItemConvertible input) {
		this.createShaped(RecipeCategory.DECORATIONS, output, 16)
			.input('#', input)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	protected void offerStainedGlassPaneDyeingRecipe(ItemConvertible output, ItemConvertible inputDye) {
		this.createShaped(RecipeCategory.DECORATIONS, output, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', inputDye)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.conditionsFromItem(Blocks.GLASS_PANE))
			.criterion(hasItem(inputDye), this.conditionsFromItem(inputDye))
			.offerTo(this.exporter, convertBetween(output, Blocks.GLASS_PANE));
	}

	protected void offerTerracottaDyeingRecipe(ItemConvertible output, ItemConvertible input) {
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', input)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.conditionsFromItem(Blocks.TERRACOTTA))
			.offerTo(this.exporter);
	}

	protected void offerConcretePowderDyeingRecipe(ItemConvertible output, ItemConvertible input) {
		this.createShapeless(RecipeCategory.BUILDING_BLOCKS, output, 8)
			.input(input)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.conditionsFromItem(Blocks.SAND))
			.criterion("has_gravel", this.conditionsFromItem(Blocks.GRAVEL))
			.offerTo(this.exporter);
	}

	protected void offerCandleDyeingRecipe(ItemConvertible output, ItemConvertible input) {
		this.createShapeless(RecipeCategory.DECORATIONS, output)
			.input(Blocks.CANDLE)
			.input(input)
			.group("dyed_candle")
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	protected void offerWallRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		this.getWallRecipe(category, output, Ingredient.ofItem(input)).criterion(hasItem(input), this.conditionsFromItem(input)).offerTo(this.exporter);
	}

	private CraftingRecipeJsonBuilder getWallRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return this.createShaped(category, output, 6).input('#', input).pattern("###").pattern("###");
	}

	protected void offerPolishedStoneRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		this.createCondensingRecipe(category, output, Ingredient.ofItem(input)).criterion(hasItem(input), this.conditionsFromItem(input)).offerTo(this.exporter);
	}

	private CraftingRecipeJsonBuilder createCondensingRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return this.createShaped(category, output, 4).input('S', input).pattern("SS").pattern("SS");
	}

	protected void offerCutCopperRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		this.createCutCopperRecipe(category, output, Ingredient.ofItem(input)).criterion(hasItem(input), this.conditionsFromItem(input)).offerTo(this.exporter);
	}

	private ShapedRecipeJsonBuilder createCutCopperRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return this.createShaped(category, output, 4).input('#', input).pattern("##").pattern("##");
	}

	protected void offerChiseledBlockRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		this.createChiseledBlockRecipe(category, output, Ingredient.ofItem(input)).criterion(hasItem(input), this.conditionsFromItem(input)).offerTo(this.exporter);
	}

	protected void offerMosaicRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		this.createShaped(category, output)
			.input('#', input)
			.pattern("#")
			.pattern("#")
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	protected ShapedRecipeJsonBuilder createChiseledBlockRecipe(RecipeCategory category, ItemConvertible output, Ingredient input) {
		return this.createShaped(category, output).input('#', input).pattern("#").pattern("#");
	}

	protected void offerStonecuttingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input) {
		this.offerStonecuttingRecipe(category, output, input, 1);
	}

	protected void offerStonecuttingRecipe(RecipeCategory category, ItemConvertible output, ItemConvertible input, int count) {
		StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItem(input), category, output, count)
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter, convertBetween(output, input) + "_stonecutting");
	}

	/**
	 * Offers a smelting recipe to the exporter that is used to convert the main block of a block family to its cracked variant.
	 */
	private void offerCrackingRecipe(ItemConvertible output, ItemConvertible input) {
		CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItem(input), RecipeCategory.BUILDING_BLOCKS, output, 0.1F, 200)
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	/**
	 * Offers two recipes to convert between a normal and compacted form of an item.
	 * 
	 * <p>The shaped recipe converts 9 items in a square to a compacted form of the item.
	 * <p>The shapeless recipe converts the compacted form to 9 of the normal form.
	 */
	protected void offerReversibleCompactingRecipes(
		RecipeCategory reverseCategory, ItemConvertible baseItem, RecipeCategory compactingCategory, ItemConvertible compactItem
	) {
		this.offerReversibleCompactingRecipes(
			reverseCategory, baseItem, compactingCategory, compactItem, getRecipeName(compactItem), null, getRecipeName(baseItem), null
		);
	}

	protected void offerReversibleCompactingRecipesWithCompactingRecipeGroup(
		RecipeCategory reverseCategory,
		ItemConvertible baseItem,
		RecipeCategory compactingCategory,
		ItemConvertible compactItem,
		String compactingId,
		String compactingGroup
	) {
		this.offerReversibleCompactingRecipes(
			reverseCategory, baseItem, compactingCategory, compactItem, compactingId, compactingGroup, getRecipeName(baseItem), null
		);
	}

	protected void offerReversibleCompactingRecipesWithReverseRecipeGroup(
		RecipeCategory reverseCategory,
		ItemConvertible baseItem,
		RecipeCategory compactingCategory,
		ItemConvertible compactItem,
		String reverseId,
		String reverseGroup
	) {
		this.offerReversibleCompactingRecipes(reverseCategory, baseItem, compactingCategory, compactItem, getRecipeName(compactItem), null, reverseId, reverseGroup);
	}

	private void offerReversibleCompactingRecipes(
		RecipeCategory reverseCategory,
		ItemConvertible baseItem,
		RecipeCategory compactingCategory,
		ItemConvertible compactItem,
		String compactingId,
		@Nullable String compactingGroup,
		String reverseId,
		@Nullable String reverseGroup
	) {
		this.createShapeless(reverseCategory, baseItem, 9)
			.input(compactItem)
			.group(reverseGroup)
			.criterion(hasItem(compactItem), this.conditionsFromItem(compactItem))
			.offerTo(this.exporter, Identifier.of(reverseId));
		this.createShaped(compactingCategory, compactItem)
			.input('#', baseItem)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group(compactingGroup)
			.criterion(hasItem(baseItem), this.conditionsFromItem(baseItem))
			.offerTo(this.exporter, Identifier.of(compactingId));
	}

	protected void offerSmithingTemplateCopyingRecipe(ItemConvertible template, ItemConvertible resource) {
		this.createShaped(RecipeCategory.MISC, template, 2)
			.input('#', Items.DIAMOND)
			.input('C', resource)
			.input('S', template)
			.pattern("#S#")
			.pattern("#C#")
			.pattern("###")
			.criterion(hasItem(template), this.conditionsFromItem(template))
			.offerTo(this.exporter);
	}

	protected void offerSmithingTemplateCopyingRecipe(ItemConvertible template, Ingredient resource) {
		this.createShaped(RecipeCategory.MISC, template, 2)
			.input('#', Items.DIAMOND)
			.input('C', resource)
			.input('S', template)
			.pattern("#S#")
			.pattern("#C#")
			.pattern("###")
			.criterion(hasItem(template), this.conditionsFromItem(template))
			.offerTo(this.exporter);
	}

	protected <T extends AbstractCookingRecipe> void generateCookingRecipes(
		String cooker, RecipeSerializer<T> serializer, AbstractCookingRecipe.RecipeFactory<T> recipeFactory, int cookingTime
	) {
		this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35F);
		this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35F);
		this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.COD, Items.COOKED_COD, 0.35F);
		this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1F);
		this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35F);
		this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35F);
		this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35F);
		this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35F);
		this.offerFoodCookingRecipe(cooker, serializer, recipeFactory, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35F);
	}

	private <T extends AbstractCookingRecipe> void offerFoodCookingRecipe(
		String cooker,
		RecipeSerializer<T> serializer,
		AbstractCookingRecipe.RecipeFactory<T> recipeFactory,
		int cookingTime,
		ItemConvertible input,
		ItemConvertible output,
		float experience
	) {
		CookingRecipeJsonBuilder.create(Ingredient.ofItem(input), RecipeCategory.FOOD, output, experience, cookingTime, serializer, recipeFactory)
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter, getItemPath(output) + "_from_" + cooker);
	}

	protected void offerWaxingRecipes(FeatureSet enabledFeatures) {
		((BiMap)HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get())
			.forEach(
				(unwaxed, waxed) -> {
					if (waxed.getRequiredFeatures().isSubsetOf(enabledFeatures)) {
						this.createShapeless(RecipeCategory.BUILDING_BLOCKS, waxed)
							.input(unwaxed)
							.input(Items.HONEYCOMB)
							.group(getItemPath(waxed))
							.criterion(hasItem(unwaxed), this.conditionsFromItem(unwaxed))
							.offerTo(this.exporter, convertBetween(waxed, Items.HONEYCOMB));
					}
				}
			);
	}

	protected void offerGrateRecipe(Block output, Block input) {
		this.createShaped(RecipeCategory.BUILDING_BLOCKS, output, 4)
			.input('M', input)
			.pattern(" M ")
			.pattern("M M")
			.pattern(" M ")
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	protected void offerBulbRecipe(Block output, Block input) {
		this.createShaped(RecipeCategory.REDSTONE, output, 4)
			.input('C', input)
			.input('R', Items.REDSTONE)
			.input('B', Items.BLAZE_ROD)
			.pattern(" C ")
			.pattern("CBC")
			.pattern(" R ")
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter);
	}

	protected void offerSuspiciousStewRecipe(Item input, SuspiciousStewIngredient stewIngredient) {
		ItemStack itemStack = new ItemStack(
			Items.SUSPICIOUS_STEW.getRegistryEntry(),
			1,
			ComponentChanges.builder().add(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, stewIngredient.getStewEffects()).build()
		);
		this.createShapeless(RecipeCategory.FOOD, itemStack)
			.input(Items.BOWL)
			.input(Items.BROWN_MUSHROOM)
			.input(Items.RED_MUSHROOM)
			.input(input)
			.group("suspicious_stew")
			.criterion(hasItem(input), this.conditionsFromItem(input))
			.offerTo(this.exporter, getItemPath(itemStack.getItem()) + "_from_" + getItemPath(input));
	}

	protected void generateFamily(BlockFamily family, FeatureSet enabledFeatures) {
		family.getVariants()
			.forEach(
				(variant, block) -> {
					if (block.getRequiredFeatures().isSubsetOf(enabledFeatures)) {
						RecipeGenerator.BlockFamilyRecipeFactory blockFamilyRecipeFactory = (RecipeGenerator.BlockFamilyRecipeFactory)VARIANT_FACTORIES.get(variant);
						ItemConvertible itemConvertible = this.getVariantRecipeInput(family, variant);
						if (blockFamilyRecipeFactory != null) {
							CraftingRecipeJsonBuilder craftingRecipeJsonBuilder = blockFamilyRecipeFactory.create(this, block, itemConvertible);
							family.getGroup().ifPresent(group -> craftingRecipeJsonBuilder.group(group + (variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getName())));
							craftingRecipeJsonBuilder.criterion(
								(String)family.getUnlockCriterionName().orElseGet(() -> hasItem(itemConvertible)), this.conditionsFromItem(itemConvertible)
							);
							craftingRecipeJsonBuilder.offerTo(this.exporter);
						}

						if (variant == BlockFamily.Variant.CRACKED) {
							this.offerCrackingRecipe(block, itemConvertible);
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
	private Block getVariantRecipeInput(BlockFamily family, BlockFamily.Variant variant) {
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

	private AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItem(NumberRange.IntRange count, ItemConvertible item) {
		return conditionsFromPredicates(ItemPredicate.Builder.create().items(this.itemLookup, item).count(count));
	}

	protected AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromItem(ItemConvertible item) {
		return conditionsFromPredicates(ItemPredicate.Builder.create().items(this.itemLookup, item));
	}

	protected AdvancementCriterion<InventoryChangedCriterion.Conditions> conditionsFromTag(TagKey<Item> tag) {
		return conditionsFromPredicates(ItemPredicate.Builder.create().tag(this.itemLookup, tag));
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

	protected Ingredient ingredientFromTag(TagKey<Item> tag) {
		return Ingredient.fromTag(this.itemLookup.getOrThrow(tag));
	}

	protected ShapedRecipeJsonBuilder createShaped(RecipeCategory category, ItemConvertible output) {
		return ShapedRecipeJsonBuilder.create(this.itemLookup, category, output);
	}

	protected ShapedRecipeJsonBuilder createShaped(RecipeCategory category, ItemConvertible output, int count) {
		return ShapedRecipeJsonBuilder.create(this.itemLookup, category, output, count);
	}

	protected ShapelessRecipeJsonBuilder createShapeless(RecipeCategory category, ItemStack output) {
		return ShapelessRecipeJsonBuilder.create(this.itemLookup, category, output);
	}

	protected ShapelessRecipeJsonBuilder createShapeless(RecipeCategory category, ItemConvertible output) {
		return ShapelessRecipeJsonBuilder.create(this.itemLookup, category, output);
	}

	protected ShapelessRecipeJsonBuilder createShapeless(RecipeCategory category, ItemConvertible output, int count) {
		return ShapelessRecipeJsonBuilder.create(this.itemLookup, category, output, count);
	}

	@FunctionalInterface
	interface BlockFamilyRecipeFactory {
		CraftingRecipeJsonBuilder create(RecipeGenerator generator, ItemConvertible output, ItemConvertible input);
	}

	protected abstract static class RecipeProvider implements DataProvider {
		private final DataOutput output;
		private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

		protected RecipeProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
			this.output = output;
			this.registryLookupFuture = registryLookupFuture;
		}

		@Override
		public final CompletableFuture<?> run(DataWriter writer) {
			return this.registryLookupFuture
				.thenCompose(
					wrapperLookup -> {
						final DataOutput.PathResolver pathResolver = this.output.getResolver(RegistryKeys.RECIPE);
						final DataOutput.PathResolver pathResolver2 = this.output.getResolver(RegistryKeys.ADVANCEMENT);
						final Set<Identifier> set = Sets.<Identifier>newHashSet();
						final List<CompletableFuture<?>> list = new ArrayList();
						RecipeExporter recipeExporter = new RecipeExporter() {
							@Override
							public void accept(Identifier recipeId, Recipe<?> recipe, @Nullable AdvancementEntry advancement) {
								if (!set.add(recipeId)) {
									throw new IllegalStateException("Duplicate recipe " + recipeId);
								} else {
									this.addRecipe(recipeId, recipe);
									if (advancement != null) {
										this.addRecipeAdvancement(advancement);
									}
								}
							}

							@Override
							public Advancement.Builder getAdvancementBuilder() {
								return Advancement.Builder.createUntelemetered().parent(CraftingRecipeJsonBuilder.ROOT);
							}

							@Override
							public void addRootAdvancement() {
								AdvancementEntry advancementEntry = Advancement.Builder.createUntelemetered()
									.criterion("impossible", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions()))
									.build(CraftingRecipeJsonBuilder.ROOT);
								this.addRecipeAdvancement(advancementEntry);
							}

							private void addRecipe(Identifier id, Recipe<?> recipe) {
								list.add(DataProvider.writeCodecToPath(writer, wrapperLookup, Recipe.CODEC, recipe, pathResolver.resolveJson(id)));
							}

							private void addRecipeAdvancement(AdvancementEntry advancementEntry) {
								list.add(
									DataProvider.writeCodecToPath(writer, wrapperLookup, Advancement.CODEC, advancementEntry.value(), pathResolver2.resolveJson(advancementEntry.id()))
								);
							}
						};
						this.getRecipeGenerator(wrapperLookup, recipeExporter).generate();
						return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
					}
				);
		}

		protected abstract RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter);
	}
}
