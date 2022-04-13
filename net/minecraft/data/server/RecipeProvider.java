/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingRecipeJsonBuilder;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class RecipeProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ImmutableList<ItemConvertible> COAL_ORES = ImmutableList.of(Items.COAL_ORE, Items.DEEPSLATE_COAL_ORE);
    private static final ImmutableList<ItemConvertible> IRON_ORES = ImmutableList.of(Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON);
    private static final ImmutableList<ItemConvertible> COPPER_ORES = ImmutableList.of(Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE, Items.RAW_COPPER);
    private static final ImmutableList<ItemConvertible> GOLD_ORES = ImmutableList.of(Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.NETHER_GOLD_ORE, Items.RAW_GOLD);
    private static final ImmutableList<ItemConvertible> DIAMOND_ORES = ImmutableList.of(Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE);
    private static final ImmutableList<ItemConvertible> LAPIS_ORES = ImmutableList.of(Items.LAPIS_ORE, Items.DEEPSLATE_LAPIS_ORE);
    private static final ImmutableList<ItemConvertible> REDSTONE_ORES = ImmutableList.of(Items.REDSTONE_ORE, Items.DEEPSLATE_REDSTONE_ORE);
    private static final ImmutableList<ItemConvertible> EMERALD_ORES = ImmutableList.of(Items.EMERALD_ORE, Items.DEEPSLATE_EMERALD_ORE);
    private final DataGenerator root;
    private static final Map<BlockFamily.Variant, BiFunction<ItemConvertible, ItemConvertible, CraftingRecipeJsonBuilder>> VARIANT_FACTORIES = ImmutableMap.builder().put(BlockFamily.Variant.BUTTON, (output, input) -> RecipeProvider.createTransmutationRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.CHISELED, (output, input) -> RecipeProvider.createChiseledBlockRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.CUT, (output, input) -> RecipeProvider.createCutCopperRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.DOOR, (output, input) -> RecipeProvider.createDoorRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.FENCE, (output, input) -> RecipeProvider.createFenceRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.FENCE_GATE, (output, input) -> RecipeProvider.createFenceGateRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.SIGN, (output, input) -> RecipeProvider.createSignRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.SLAB, (output, input) -> RecipeProvider.createSlabRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.STAIRS, (output, input) -> RecipeProvider.createStairsRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.PRESSURE_PLATE, (output, input) -> RecipeProvider.createPressurePlateRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.POLISHED, (output, input) -> RecipeProvider.createCondensingRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.TRAPDOOR, (output, input) -> RecipeProvider.createTrapdoorRecipe(output, Ingredient.ofItems(input))).put(BlockFamily.Variant.WALL, (output, input) -> RecipeProvider.getWallRecipe(output, Ingredient.ofItems(input))).build();

    public RecipeProvider(DataGenerator root) {
        this.root = root;
    }

    @Override
    public void run(DataWriter cache) {
        Path path = this.root.getOutput();
        HashSet set = Sets.newHashSet();
        RecipeProvider.generate(provider -> {
            if (!set.add(provider.getRecipeId())) {
                throw new IllegalStateException("Duplicate recipe " + provider.getRecipeId());
            }
            RecipeProvider.saveRecipe(cache, provider.toJson(), path.resolve("data/" + provider.getRecipeId().getNamespace() + "/recipes/" + provider.getRecipeId().getPath() + ".json"));
            JsonObject jsonObject = provider.toAdvancementJson();
            if (jsonObject != null) {
                RecipeProvider.saveRecipeAdvancement(cache, jsonObject, path.resolve("data/" + provider.getRecipeId().getNamespace() + "/advancements/" + provider.getAdvancementId().getPath() + ".json"));
            }
        });
        RecipeProvider.saveRecipeAdvancement(cache, Advancement.Builder.create().criterion("impossible", new ImpossibleCriterion.Conditions()).toJson(), path.resolve("data/minecraft/advancements/recipes/root.json"));
    }

    private static void saveRecipe(DataWriter cache, JsonObject json, Path path) {
        try {
            String string = GSON.toJson(json);
            cache.write(path, string);
        } catch (IOException iOException) {
            LOGGER.error("Couldn't save recipe {}", (Object)path, (Object)iOException);
        }
    }

    private static void saveRecipeAdvancement(DataWriter cache, JsonObject json, Path path) {
        try {
            String string = GSON.toJson(json);
            cache.write(path, string);
        } catch (IOException iOException) {
            LOGGER.error("Couldn't save recipe advancement {}", (Object)path, (Object)iOException);
        }
    }

    private static void generate(Consumer<RecipeJsonProvider> exporter) {
        BlockFamilies.getFamilies().filter(BlockFamily::shouldGenerateRecipes).forEach(family -> RecipeProvider.generateFamily(exporter, family));
        RecipeProvider.offerPlanksRecipe2(exporter, Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS);
        RecipeProvider.offerPlanksRecipe(exporter, Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS);
        RecipeProvider.offerPlanksRecipe(exporter, Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS);
        RecipeProvider.offerPlanksRecipe2(exporter, Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS);
        RecipeProvider.offerPlanksRecipe(exporter, Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS);
        RecipeProvider.offerPlanksRecipe(exporter, Blocks.OAK_PLANKS, ItemTags.OAK_LOGS);
        RecipeProvider.offerPlanksRecipe(exporter, Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS);
        RecipeProvider.offerPlanksRecipe(exporter, Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS);
        RecipeProvider.offerPlanksRecipe(exporter, Blocks.MANGROVE_PLANKS, ItemTags.MANGROVE_LOGS);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.OAK_WOOD, Blocks.OAK_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.MANGROVE_WOOD, Blocks.MANGROVE_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
        RecipeProvider.offerBarkBlockRecipe(exporter, Blocks.STRIPPED_MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_LOG);
        RecipeProvider.offerBoatRecipe(exporter, Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
        RecipeProvider.offerBoatRecipe(exporter, Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
        RecipeProvider.offerBoatRecipe(exporter, Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
        RecipeProvider.offerBoatRecipe(exporter, Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
        RecipeProvider.offerBoatRecipe(exporter, Items.OAK_BOAT, Blocks.OAK_PLANKS);
        RecipeProvider.offerBoatRecipe(exporter, Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
        RecipeProvider.offerBoatRecipe(exporter, Items.MANGROVE_BOAT, Blocks.MANGROVE_PLANKS);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.BLACK_WOOL, Items.BLACK_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.BLACK_CARPET, Items.BLACK_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.BLACK_BED, Blocks.BLACK_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.BLACK_BED, Items.BLACK_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.BLACK_BANNER, Blocks.BLACK_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.BLUE_WOOL, Items.BLUE_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.BLUE_CARPET, Items.BLUE_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.BLUE_BED, Blocks.BLUE_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.BLUE_BED, Items.BLUE_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.BLUE_BANNER, Blocks.BLUE_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.BROWN_WOOL, Items.BROWN_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.BROWN_CARPET, Items.BROWN_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.BROWN_BED, Blocks.BROWN_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.BROWN_BED, Items.BROWN_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.BROWN_BANNER, Blocks.BROWN_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.CYAN_WOOL, Items.CYAN_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.CYAN_CARPET, Items.CYAN_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.CYAN_BED, Blocks.CYAN_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.CYAN_BED, Items.CYAN_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.CYAN_BANNER, Blocks.CYAN_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.GRAY_WOOL, Items.GRAY_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.GRAY_CARPET, Items.GRAY_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.GRAY_BED, Blocks.GRAY_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.GRAY_BED, Items.GRAY_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.GRAY_BANNER, Blocks.GRAY_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.GREEN_WOOL, Items.GREEN_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.GREEN_CARPET, Items.GREEN_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.GREEN_BED, Blocks.GREEN_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.GREEN_BED, Items.GREEN_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.GREEN_BANNER, Blocks.GREEN_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.LIGHT_BLUE_CARPET, Items.LIGHT_BLUE_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.LIGHT_BLUE_BED, Items.LIGHT_BLUE_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.LIGHT_GRAY_CARPET, Items.LIGHT_GRAY_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.LIGHT_GRAY_BED, Items.LIGHT_GRAY_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.LIME_WOOL, Items.LIME_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.LIME_CARPET, Blocks.LIME_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.LIME_CARPET, Items.LIME_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.LIME_BED, Blocks.LIME_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.LIME_BED, Items.LIME_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.LIME_BANNER, Blocks.LIME_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.MAGENTA_WOOL, Items.MAGENTA_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.MAGENTA_CARPET, Items.MAGENTA_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.MAGENTA_BED, Items.MAGENTA_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.ORANGE_WOOL, Items.ORANGE_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.ORANGE_CARPET, Items.ORANGE_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.ORANGE_BED, Blocks.ORANGE_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.ORANGE_BED, Items.ORANGE_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.PINK_WOOL, Items.PINK_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.PINK_CARPET, Blocks.PINK_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.PINK_CARPET, Items.PINK_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.PINK_BED, Blocks.PINK_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.PINK_BED, Items.PINK_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.PINK_BANNER, Blocks.PINK_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.PURPLE_WOOL, Items.PURPLE_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.PURPLE_CARPET, Items.PURPLE_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.PURPLE_BED, Blocks.PURPLE_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.PURPLE_BED, Items.PURPLE_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.RED_WOOL, Items.RED_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.RED_CARPET, Blocks.RED_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.RED_CARPET, Items.RED_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.RED_BED, Blocks.RED_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.RED_BED, Items.RED_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.RED_BANNER, Blocks.RED_WOOL);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
        RecipeProvider.offerBedRecipe(exporter, Items.WHITE_BED, Blocks.WHITE_WOOL);
        RecipeProvider.offerBannerRecipe(exporter, Items.WHITE_BANNER, Blocks.WHITE_WOOL);
        RecipeProvider.offerWoolDyeingRecipe(exporter, Blocks.YELLOW_WOOL, Items.YELLOW_DYE);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
        RecipeProvider.offerCarpetDyeingRecipe(exporter, Blocks.YELLOW_CARPET, Items.YELLOW_DYE);
        RecipeProvider.offerBedRecipe(exporter, Items.YELLOW_BED, Blocks.YELLOW_WOOL);
        RecipeProvider.offerBedDyeingRecipe(exporter, Items.YELLOW_BED, Items.YELLOW_DYE);
        RecipeProvider.offerBannerRecipe(exporter, Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
        RecipeProvider.offerCarpetRecipe(exporter, Blocks.MOSS_CARPET, Blocks.MOSS_BLOCK);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.BLACK_STAINED_GLASS, Items.BLACK_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.BLACK_STAINED_GLASS_PANE, Items.BLACK_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.BLUE_STAINED_GLASS, Items.BLUE_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.BLUE_STAINED_GLASS_PANE, Items.BLUE_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.BROWN_STAINED_GLASS, Items.BROWN_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.BROWN_STAINED_GLASS_PANE, Items.BROWN_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.CYAN_STAINED_GLASS, Items.CYAN_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.CYAN_STAINED_GLASS_PANE, Items.CYAN_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.GRAY_STAINED_GLASS, Items.GRAY_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.GRAY_STAINED_GLASS_PANE, Items.GRAY_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.GREEN_STAINED_GLASS, Items.GREEN_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.GREEN_STAINED_GLASS_PANE, Items.GREEN_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.LIGHT_BLUE_STAINED_GLASS, Items.LIGHT_BLUE_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Items.LIGHT_BLUE_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.LIGHT_GRAY_STAINED_GLASS, Items.LIGHT_GRAY_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.LIME_STAINED_GLASS, Items.LIME_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.LIME_STAINED_GLASS_PANE, Items.LIME_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.MAGENTA_STAINED_GLASS, Items.MAGENTA_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.MAGENTA_STAINED_GLASS_PANE, Items.MAGENTA_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.ORANGE_STAINED_GLASS, Items.ORANGE_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.ORANGE_STAINED_GLASS_PANE, Items.ORANGE_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.PINK_STAINED_GLASS, Items.PINK_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.PINK_STAINED_GLASS_PANE, Items.PINK_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.PURPLE_STAINED_GLASS, Items.PURPLE_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.PURPLE_STAINED_GLASS_PANE, Items.PURPLE_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.RED_STAINED_GLASS, Items.RED_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.RED_STAINED_GLASS_PANE, Items.RED_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.WHITE_STAINED_GLASS, Items.WHITE_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.WHITE_STAINED_GLASS_PANE, Items.WHITE_DYE);
        RecipeProvider.offerStainedGlassDyeingRecipe(exporter, Blocks.YELLOW_STAINED_GLASS, Items.YELLOW_DYE);
        RecipeProvider.offerStainedGlassPaneRecipe(exporter, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS);
        RecipeProvider.offerStainedGlassPaneDyeingRecipe(exporter, Blocks.YELLOW_STAINED_GLASS_PANE, Items.YELLOW_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.BLACK_TERRACOTTA, Items.BLACK_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.BLUE_TERRACOTTA, Items.BLUE_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.BROWN_TERRACOTTA, Items.BROWN_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.CYAN_TERRACOTTA, Items.CYAN_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.GRAY_TERRACOTTA, Items.GRAY_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.GREEN_TERRACOTTA, Items.GREEN_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.LIME_TERRACOTTA, Items.LIME_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.MAGENTA_TERRACOTTA, Items.MAGENTA_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.ORANGE_TERRACOTTA, Items.ORANGE_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.PINK_TERRACOTTA, Items.PINK_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.PURPLE_TERRACOTTA, Items.PURPLE_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.RED_TERRACOTTA, Items.RED_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.WHITE_TERRACOTTA, Items.WHITE_DYE);
        RecipeProvider.offerTerracottaDyeingRecipe(exporter, Blocks.YELLOW_TERRACOTTA, Items.YELLOW_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.BLACK_CONCRETE_POWDER, Items.BLACK_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.BLUE_CONCRETE_POWDER, Items.BLUE_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.BROWN_CONCRETE_POWDER, Items.BROWN_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.RED_CONCRETE_POWDER, Items.RED_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.WHITE_CONCRETE_POWDER, Items.WHITE_DYE);
        RecipeProvider.offerConcretePowderDyeingRecipe(exporter, Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE);
        ShapedRecipeJsonBuilder.create(Items.CANDLE).input(Character.valueOf('S'), Items.STRING).input(Character.valueOf('H'), Items.HONEYCOMB).pattern("S").pattern("H").criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING)).criterion("has_honeycomb", RecipeProvider.conditionsFromItem(Items.HONEYCOMB)).offerTo(exporter);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.BLACK_CANDLE, Items.BLACK_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.BLUE_CANDLE, Items.BLUE_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.BROWN_CANDLE, Items.BROWN_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.CYAN_CANDLE, Items.CYAN_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.GRAY_CANDLE, Items.GRAY_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.GREEN_CANDLE, Items.GREEN_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.LIGHT_BLUE_CANDLE, Items.LIGHT_BLUE_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.LIGHT_GRAY_CANDLE, Items.LIGHT_GRAY_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.LIME_CANDLE, Items.LIME_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.MAGENTA_CANDLE, Items.MAGENTA_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.ORANGE_CANDLE, Items.ORANGE_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.PINK_CANDLE, Items.PINK_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.PURPLE_CANDLE, Items.PURPLE_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.RED_CANDLE, Items.RED_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.WHITE_CANDLE, Items.WHITE_DYE);
        RecipeProvider.offerCandleDyeingRecipe(exporter, Blocks.YELLOW_CANDLE, Items.YELLOW_DYE);
        ShapelessRecipeJsonBuilder.create(Blocks.PACKED_MUD, 1).input(Blocks.MUD).input(Items.WHEAT).criterion("has_mud", RecipeProvider.conditionsFromItem(Blocks.MUD)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.MUD_BRICKS, 4).input(Character.valueOf('#'), Blocks.PACKED_MUD).pattern("##").pattern("##").criterion("has_packed_mud", RecipeProvider.conditionsFromItem(Blocks.PACKED_MUD)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Blocks.MUDDY_MANGROVE_ROOTS, 1).input(Blocks.MUD).input(Items.MANGROVE_ROOTS).criterion("has_mangrove_roots", RecipeProvider.conditionsFromItem(Blocks.MANGROVE_ROOTS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.ACTIVATOR_RAIL, 6).input(Character.valueOf('#'), Blocks.REDSTONE_TORCH).input(Character.valueOf('S'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XSX").pattern("X#X").pattern("XSX").criterion("has_rail", RecipeProvider.conditionsFromItem(Blocks.RAIL)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Blocks.ANDESITE, 2).input(Blocks.DIORITE).input(Blocks.COBBLESTONE).criterion("has_stone", RecipeProvider.conditionsFromItem(Blocks.DIORITE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.ANVIL).input(Character.valueOf('I'), Blocks.IRON_BLOCK).input(Character.valueOf('i'), Items.IRON_INGOT).pattern("III").pattern(" i ").pattern("iii").criterion("has_iron_block", RecipeProvider.conditionsFromItem(Blocks.IRON_BLOCK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.ARMOR_STAND).input(Character.valueOf('/'), Items.STICK).input(Character.valueOf('_'), Blocks.SMOOTH_STONE_SLAB).pattern("///").pattern(" / ").pattern("/_/").criterion("has_stone_slab", RecipeProvider.conditionsFromItem(Blocks.SMOOTH_STONE_SLAB)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.ARROW, 4).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.FLINT).input(Character.valueOf('Y'), Items.FEATHER).pattern("X").pattern("#").pattern("Y").criterion("has_feather", RecipeProvider.conditionsFromItem(Items.FEATHER)).criterion("has_flint", RecipeProvider.conditionsFromItem(Items.FLINT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.BARREL, 1).input(Character.valueOf('P'), ItemTags.PLANKS).input(Character.valueOf('S'), ItemTags.WOODEN_SLABS).pattern("PSP").pattern("P P").pattern("PSP").criterion("has_planks", RecipeProvider.conditionsFromTag(ItemTags.PLANKS)).criterion("has_wood_slab", RecipeProvider.conditionsFromTag(ItemTags.WOODEN_SLABS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.BEACON).input(Character.valueOf('S'), Items.NETHER_STAR).input(Character.valueOf('G'), Blocks.GLASS).input(Character.valueOf('O'), Blocks.OBSIDIAN).pattern("GGG").pattern("GSG").pattern("OOO").criterion("has_nether_star", RecipeProvider.conditionsFromItem(Items.NETHER_STAR)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.BEEHIVE).input(Character.valueOf('P'), ItemTags.PLANKS).input(Character.valueOf('H'), Items.HONEYCOMB).pattern("PPP").pattern("HHH").pattern("PPP").criterion("has_honeycomb", RecipeProvider.conditionsFromItem(Items.HONEYCOMB)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.BEETROOT_SOUP).input(Items.BOWL).input(Items.BEETROOT, 6).criterion("has_beetroot", RecipeProvider.conditionsFromItem(Items.BEETROOT)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.BLACK_DYE).input(Items.INK_SAC).group("black_dye").criterion("has_ink_sac", RecipeProvider.conditionsFromItem(Items.INK_SAC)).offerTo(exporter);
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.BLACK_DYE, Blocks.WITHER_ROSE, "black_dye");
        ShapelessRecipeJsonBuilder.create(Items.BLAZE_POWDER, 2).input(Items.BLAZE_ROD).criterion("has_blaze_rod", RecipeProvider.conditionsFromItem(Items.BLAZE_ROD)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.BLUE_DYE).input(Items.LAPIS_LAZULI).group("blue_dye").criterion("has_lapis_lazuli", RecipeProvider.conditionsFromItem(Items.LAPIS_LAZULI)).offerTo(exporter);
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.BLUE_DYE, Blocks.CORNFLOWER, "blue_dye");
        ShapedRecipeJsonBuilder.create(Blocks.BLUE_ICE).input(Character.valueOf('#'), Blocks.PACKED_ICE).pattern("###").pattern("###").pattern("###").criterion("has_packed_ice", RecipeProvider.conditionsFromItem(Blocks.PACKED_ICE)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.BONE_MEAL, 3).input(Items.BONE).group("bonemeal").criterion("has_bone", RecipeProvider.conditionsFromItem(Items.BONE)).offerTo(exporter);
        RecipeProvider.offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, Items.BONE_MEAL, Items.BONE_BLOCK, "bone_meal_from_bone_block", "bonemeal");
        ShapelessRecipeJsonBuilder.create(Items.BOOK).input(Items.PAPER, 3).input(Items.LEATHER).criterion("has_paper", RecipeProvider.conditionsFromItem(Items.PAPER)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.BOOKSHELF).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('X'), Items.BOOK).pattern("###").pattern("XXX").pattern("###").criterion("has_book", RecipeProvider.conditionsFromItem(Items.BOOK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.BOW).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.STRING).pattern(" #X").pattern("# X").pattern(" #X").criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.BOWL, 4).input(Character.valueOf('#'), ItemTags.PLANKS).pattern("# #").pattern(" # ").criterion("has_brown_mushroom", RecipeProvider.conditionsFromItem(Blocks.BROWN_MUSHROOM)).criterion("has_red_mushroom", RecipeProvider.conditionsFromItem(Blocks.RED_MUSHROOM)).criterion("has_mushroom_stew", RecipeProvider.conditionsFromItem(Items.MUSHROOM_STEW)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.BREAD).input(Character.valueOf('#'), Items.WHEAT).pattern("###").criterion("has_wheat", RecipeProvider.conditionsFromItem(Items.WHEAT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.BREWING_STAND).input(Character.valueOf('B'), Items.BLAZE_ROD).input(Character.valueOf('#'), ItemTags.STONE_CRAFTING_MATERIALS).pattern(" B ").pattern("###").criterion("has_blaze_rod", RecipeProvider.conditionsFromItem(Items.BLAZE_ROD)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.BRICKS).input(Character.valueOf('#'), Items.BRICK).pattern("##").pattern("##").criterion("has_brick", RecipeProvider.conditionsFromItem(Items.BRICK)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.BROWN_DYE).input(Items.COCOA_BEANS).group("brown_dye").criterion("has_cocoa_beans", RecipeProvider.conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.BUCKET).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("# #").pattern(" # ").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.CAKE).input(Character.valueOf('A'), Items.MILK_BUCKET).input(Character.valueOf('B'), Items.SUGAR).input(Character.valueOf('C'), Items.WHEAT).input(Character.valueOf('E'), Items.EGG).pattern("AAA").pattern("BEB").pattern("CCC").criterion("has_egg", RecipeProvider.conditionsFromItem(Items.EGG)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.CAMPFIRE).input(Character.valueOf('L'), ItemTags.LOGS).input(Character.valueOf('S'), Items.STICK).input(Character.valueOf('C'), ItemTags.COALS).pattern(" S ").pattern("SCS").pattern("LLL").criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).criterion("has_coal", RecipeProvider.conditionsFromTag(ItemTags.COALS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.CARROT_ON_A_STICK).input(Character.valueOf('#'), Items.FISHING_ROD).input(Character.valueOf('X'), Items.CARROT).pattern("# ").pattern(" X").criterion("has_carrot", RecipeProvider.conditionsFromItem(Items.CARROT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.WARPED_FUNGUS_ON_A_STICK).input(Character.valueOf('#'), Items.FISHING_ROD).input(Character.valueOf('X'), Items.WARPED_FUNGUS).pattern("# ").pattern(" X").criterion("has_warped_fungus", RecipeProvider.conditionsFromItem(Items.WARPED_FUNGUS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.CAULDRON).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("# #").pattern("# #").pattern("###").criterion("has_water_bucket", RecipeProvider.conditionsFromItem(Items.WATER_BUCKET)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.COMPOSTER).input(Character.valueOf('#'), ItemTags.WOODEN_SLABS).pattern("# #").pattern("# #").pattern("###").criterion("has_wood_slab", RecipeProvider.conditionsFromTag(ItemTags.WOODEN_SLABS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.CHEST).input(Character.valueOf('#'), ItemTags.PLANKS).pattern("###").pattern("# #").pattern("###").criterion("has_lots_of_items", new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.atLeast(10), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new ItemPredicate[0])).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.CHEST_MINECART).input(Blocks.CHEST).input(Items.MINECART).criterion("has_minecart", RecipeProvider.conditionsFromItem(Items.MINECART)).offerTo(exporter);
        RecipeProvider.offerChestBoatRecipe(exporter, Items.ACACIA_CHEST_BOAT, Items.ACACIA_BOAT);
        RecipeProvider.offerChestBoatRecipe(exporter, Items.BIRCH_CHEST_BOAT, Items.BIRCH_BOAT);
        RecipeProvider.offerChestBoatRecipe(exporter, Items.DARK_OAK_CHEST_BOAT, Items.DARK_OAK_BOAT);
        RecipeProvider.offerChestBoatRecipe(exporter, Items.JUNGLE_CHEST_BOAT, Items.JUNGLE_BOAT);
        RecipeProvider.offerChestBoatRecipe(exporter, Items.OAK_CHEST_BOAT, Items.OAK_BOAT);
        RecipeProvider.offerChestBoatRecipe(exporter, Items.SPRUCE_CHEST_BOAT, Items.SPRUCE_BOAT);
        RecipeProvider.offerChestBoatRecipe(exporter, Items.MANGROVE_CHEST_BOAT, Items.MANGROVE_BOAT);
        RecipeProvider.createChiseledBlockRecipe(Blocks.CHISELED_QUARTZ_BLOCK, Ingredient.ofItems(Blocks.QUARTZ_SLAB)).criterion("has_chiseled_quartz_block", RecipeProvider.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK)).criterion("has_quartz_block", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).criterion("has_quartz_pillar", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_PILLAR)).offerTo(exporter);
        RecipeProvider.createChiseledBlockRecipe(Blocks.CHISELED_STONE_BRICKS, Ingredient.ofItems(Blocks.STONE_BRICK_SLAB)).criterion("has_tag", RecipeProvider.conditionsFromTag(ItemTags.STONE_BRICKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.CLAY).input(Character.valueOf('#'), Items.CLAY_BALL).pattern("##").pattern("##").criterion("has_clay_ball", RecipeProvider.conditionsFromItem(Items.CLAY_BALL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.CLOCK).input(Character.valueOf('#'), Items.GOLD_INGOT).input(Character.valueOf('X'), Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_redstone", RecipeProvider.conditionsFromItem(Items.REDSTONE)).offerTo(exporter);
        RecipeProvider.offerReversibleCompactingRecipes(exporter, Items.COAL, Items.COAL_BLOCK);
        ShapedRecipeJsonBuilder.create(Blocks.COARSE_DIRT, 4).input(Character.valueOf('D'), Blocks.DIRT).input(Character.valueOf('G'), Blocks.GRAVEL).pattern("DG").pattern("GD").criterion("has_gravel", RecipeProvider.conditionsFromItem(Blocks.GRAVEL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.COMPARATOR).input(Character.valueOf('#'), Blocks.REDSTONE_TORCH).input(Character.valueOf('X'), Items.QUARTZ).input(Character.valueOf('I'), Blocks.STONE).pattern(" # ").pattern("#X#").pattern("III").criterion("has_quartz", RecipeProvider.conditionsFromItem(Items.QUARTZ)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.COMPASS).input(Character.valueOf('#'), Items.IRON_INGOT).input(Character.valueOf('X'), Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_redstone", RecipeProvider.conditionsFromItem(Items.REDSTONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.COOKIE, 8).input(Character.valueOf('#'), Items.WHEAT).input(Character.valueOf('X'), Items.COCOA_BEANS).pattern("#X#").criterion("has_cocoa", RecipeProvider.conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.CRAFTING_TABLE).input(Character.valueOf('#'), ItemTags.PLANKS).pattern("##").pattern("##").criterion("has_planks", RecipeProvider.conditionsFromTag(ItemTags.PLANKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.CROSSBOW).input(Character.valueOf('~'), Items.STRING).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('&'), Items.IRON_INGOT).input(Character.valueOf('$'), Blocks.TRIPWIRE_HOOK).pattern("#&#").pattern("~$~").pattern(" # ").criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING)).criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).criterion("has_tripwire_hook", RecipeProvider.conditionsFromItem(Blocks.TRIPWIRE_HOOK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.LOOM).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('@'), Items.STRING).pattern("@@").pattern("##").criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING)).offerTo(exporter);
        RecipeProvider.createChiseledBlockRecipe(Blocks.CHISELED_RED_SANDSTONE, Ingredient.ofItems(Blocks.RED_SANDSTONE_SLAB)).criterion("has_red_sandstone", RecipeProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).criterion("has_chiseled_red_sandstone", RecipeProvider.conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE)).criterion("has_cut_red_sandstone", RecipeProvider.conditionsFromItem(Blocks.CUT_RED_SANDSTONE)).offerTo(exporter);
        RecipeProvider.offerChiseledBlockRecipe(exporter, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE_SLAB);
        RecipeProvider.offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, Items.COPPER_INGOT, Items.COPPER_BLOCK, RecipeProvider.getRecipeName(Items.COPPER_INGOT), RecipeProvider.getItemPath(Items.COPPER_INGOT));
        ShapelessRecipeJsonBuilder.create(Items.COPPER_INGOT, 9).input(Blocks.WAXED_COPPER_BLOCK).group(RecipeProvider.getItemPath(Items.COPPER_INGOT)).criterion(RecipeProvider.hasItem(Blocks.WAXED_COPPER_BLOCK), RecipeProvider.conditionsFromItem(Blocks.WAXED_COPPER_BLOCK)).offerTo(exporter, RecipeProvider.convertBetween(Items.COPPER_INGOT, Blocks.WAXED_COPPER_BLOCK));
        RecipeProvider.offerWaxingRecipes(exporter);
        ShapelessRecipeJsonBuilder.create(Items.CYAN_DYE, 2).input(Items.BLUE_DYE).input(Items.GREEN_DYE).criterion("has_green_dye", RecipeProvider.conditionsFromItem(Items.GREEN_DYE)).criterion("has_blue_dye", RecipeProvider.conditionsFromItem(Items.BLUE_DYE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.DARK_PRISMARINE).input(Character.valueOf('S'), Items.PRISMARINE_SHARD).input(Character.valueOf('I'), Items.BLACK_DYE).pattern("SSS").pattern("SIS").pattern("SSS").criterion("has_prismarine_shard", RecipeProvider.conditionsFromItem(Items.PRISMARINE_SHARD)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.DAYLIGHT_DETECTOR).input(Character.valueOf('Q'), Items.QUARTZ).input(Character.valueOf('G'), Blocks.GLASS).input(Character.valueOf('W'), Ingredient.fromTag(ItemTags.WOODEN_SLABS)).pattern("GGG").pattern("QQQ").pattern("WWW").criterion("has_quartz", RecipeProvider.conditionsFromItem(Items.QUARTZ)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.DEEPSLATE_BRICKS, 4).input(Character.valueOf('S'), Blocks.POLISHED_DEEPSLATE).pattern("SS").pattern("SS").criterion("has_polished_deepslate", RecipeProvider.conditionsFromItem(Blocks.POLISHED_DEEPSLATE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.DEEPSLATE_TILES, 4).input(Character.valueOf('S'), Blocks.DEEPSLATE_BRICKS).pattern("SS").pattern("SS").criterion("has_deepslate_bricks", RecipeProvider.conditionsFromItem(Blocks.DEEPSLATE_BRICKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.DETECTOR_RAIL, 6).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Blocks.STONE_PRESSURE_PLATE).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("XRX").criterion("has_rail", RecipeProvider.conditionsFromItem(Blocks.RAIL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.DIAMOND_AXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.DIAMOND).pattern("XX").pattern("X#").pattern(" #").criterion("has_diamond", RecipeProvider.conditionsFromItem(Items.DIAMOND)).offerTo(exporter);
        RecipeProvider.offerReversibleCompactingRecipes(exporter, Items.DIAMOND, Items.DIAMOND_BLOCK);
        ShapedRecipeJsonBuilder.create(Items.DIAMOND_BOOTS).input(Character.valueOf('X'), Items.DIAMOND).pattern("X X").pattern("X X").criterion("has_diamond", RecipeProvider.conditionsFromItem(Items.DIAMOND)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.DIAMOND_CHESTPLATE).input(Character.valueOf('X'), Items.DIAMOND).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_diamond", RecipeProvider.conditionsFromItem(Items.DIAMOND)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.DIAMOND_HELMET).input(Character.valueOf('X'), Items.DIAMOND).pattern("XXX").pattern("X X").criterion("has_diamond", RecipeProvider.conditionsFromItem(Items.DIAMOND)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.DIAMOND_HOE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.DIAMOND).pattern("XX").pattern(" #").pattern(" #").criterion("has_diamond", RecipeProvider.conditionsFromItem(Items.DIAMOND)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.DIAMOND_LEGGINGS).input(Character.valueOf('X'), Items.DIAMOND).pattern("XXX").pattern("X X").pattern("X X").criterion("has_diamond", RecipeProvider.conditionsFromItem(Items.DIAMOND)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.DIAMOND_PICKAXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.DIAMOND).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_diamond", RecipeProvider.conditionsFromItem(Items.DIAMOND)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.DIAMOND_SHOVEL).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.DIAMOND).pattern("X").pattern("#").pattern("#").criterion("has_diamond", RecipeProvider.conditionsFromItem(Items.DIAMOND)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.DIAMOND_SWORD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.DIAMOND).pattern("X").pattern("X").pattern("#").criterion("has_diamond", RecipeProvider.conditionsFromItem(Items.DIAMOND)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.DIORITE, 2).input(Character.valueOf('Q'), Items.QUARTZ).input(Character.valueOf('C'), Blocks.COBBLESTONE).pattern("CQ").pattern("QC").criterion("has_quartz", RecipeProvider.conditionsFromItem(Items.QUARTZ)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.DISPENSER).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Blocks.COBBLESTONE).input(Character.valueOf('X'), Items.BOW).pattern("###").pattern("#X#").pattern("#R#").criterion("has_bow", RecipeProvider.conditionsFromItem(Items.BOW)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.DRIPSTONE_BLOCK).input(Character.valueOf('#'), Items.POINTED_DRIPSTONE).pattern("##").pattern("##").group("pointed_dripstone").criterion("has_pointed_dripstone", RecipeProvider.conditionsFromItem(Items.POINTED_DRIPSTONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.DROPPER).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Blocks.COBBLESTONE).pattern("###").pattern("# #").pattern("#R#").criterion("has_redstone", RecipeProvider.conditionsFromItem(Items.REDSTONE)).offerTo(exporter);
        RecipeProvider.offerReversibleCompactingRecipes(exporter, Items.EMERALD, Items.EMERALD_BLOCK);
        ShapedRecipeJsonBuilder.create(Blocks.ENCHANTING_TABLE).input(Character.valueOf('B'), Items.BOOK).input(Character.valueOf('#'), Blocks.OBSIDIAN).input(Character.valueOf('D'), Items.DIAMOND).pattern(" B ").pattern("D#D").pattern("###").criterion("has_obsidian", RecipeProvider.conditionsFromItem(Blocks.OBSIDIAN)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.ENDER_CHEST).input(Character.valueOf('#'), Blocks.OBSIDIAN).input(Character.valueOf('E'), Items.ENDER_EYE).pattern("###").pattern("#E#").pattern("###").criterion("has_ender_eye", RecipeProvider.conditionsFromItem(Items.ENDER_EYE)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.ENDER_EYE).input(Items.ENDER_PEARL).input(Items.BLAZE_POWDER).criterion("has_blaze_powder", RecipeProvider.conditionsFromItem(Items.BLAZE_POWDER)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.END_STONE_BRICKS, 4).input(Character.valueOf('#'), Blocks.END_STONE).pattern("##").pattern("##").criterion("has_end_stone", RecipeProvider.conditionsFromItem(Blocks.END_STONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.END_CRYSTAL).input(Character.valueOf('T'), Items.GHAST_TEAR).input(Character.valueOf('E'), Items.ENDER_EYE).input(Character.valueOf('G'), Blocks.GLASS).pattern("GGG").pattern("GEG").pattern("GTG").criterion("has_ender_eye", RecipeProvider.conditionsFromItem(Items.ENDER_EYE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.END_ROD, 4).input(Character.valueOf('#'), Items.POPPED_CHORUS_FRUIT).input(Character.valueOf('/'), Items.BLAZE_ROD).pattern("/").pattern("#").criterion("has_chorus_fruit_popped", RecipeProvider.conditionsFromItem(Items.POPPED_CHORUS_FRUIT)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.FERMENTED_SPIDER_EYE).input(Items.SPIDER_EYE).input(Blocks.BROWN_MUSHROOM).input(Items.SUGAR).criterion("has_spider_eye", RecipeProvider.conditionsFromItem(Items.SPIDER_EYE)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.FIRE_CHARGE, 3).input(Items.GUNPOWDER).input(Items.BLAZE_POWDER).input(Ingredient.ofItems(Items.COAL, Items.CHARCOAL)).criterion("has_blaze_powder", RecipeProvider.conditionsFromItem(Items.BLAZE_POWDER)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.FIREWORK_ROCKET, 3).input(Items.GUNPOWDER).input(Items.PAPER).criterion("has_gunpowder", RecipeProvider.conditionsFromItem(Items.GUNPOWDER)).offerTo(exporter, "firework_rocket_simple");
        ShapedRecipeJsonBuilder.create(Items.FISHING_ROD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.STRING).pattern("  #").pattern(" #X").pattern("# X").criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.FLINT_AND_STEEL).input(Items.IRON_INGOT).input(Items.FLINT).criterion("has_flint", RecipeProvider.conditionsFromItem(Items.FLINT)).criterion("has_obsidian", RecipeProvider.conditionsFromItem(Blocks.OBSIDIAN)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.FLOWER_POT).input(Character.valueOf('#'), Items.BRICK).pattern("# #").pattern(" # ").criterion("has_brick", RecipeProvider.conditionsFromItem(Items.BRICK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.FURNACE).input(Character.valueOf('#'), ItemTags.STONE_CRAFTING_MATERIALS).pattern("###").pattern("# #").pattern("###").criterion("has_cobblestone", RecipeProvider.conditionsFromTag(ItemTags.STONE_CRAFTING_MATERIALS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.FURNACE_MINECART).input(Blocks.FURNACE).input(Items.MINECART).criterion("has_minecart", RecipeProvider.conditionsFromItem(Items.MINECART)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GLASS_BOTTLE, 3).input(Character.valueOf('#'), Blocks.GLASS).pattern("# #").pattern(" # ").criterion("has_glass", RecipeProvider.conditionsFromItem(Blocks.GLASS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.GLASS_PANE, 16).input(Character.valueOf('#'), Blocks.GLASS).pattern("###").pattern("###").criterion("has_glass", RecipeProvider.conditionsFromItem(Blocks.GLASS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.GLOWSTONE).input(Character.valueOf('#'), Items.GLOWSTONE_DUST).pattern("##").pattern("##").criterion("has_glowstone_dust", RecipeProvider.conditionsFromItem(Items.GLOWSTONE_DUST)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.GLOW_ITEM_FRAME).input(Items.ITEM_FRAME).input(Items.GLOW_INK_SAC).criterion("has_item_frame", RecipeProvider.conditionsFromItem(Items.ITEM_FRAME)).criterion("has_glow_ink_sac", RecipeProvider.conditionsFromItem(Items.GLOW_INK_SAC)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_APPLE).input(Character.valueOf('#'), Items.GOLD_INGOT).input(Character.valueOf('X'), Items.APPLE).pattern("###").pattern("#X#").pattern("###").criterion("has_gold_ingot", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_AXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XX").pattern("X#").pattern(" #").criterion("has_gold_ingot", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_BOOTS).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X X").pattern("X X").criterion("has_gold_ingot", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_CARROT).input(Character.valueOf('#'), Items.GOLD_NUGGET).input(Character.valueOf('X'), Items.CARROT).pattern("###").pattern("#X#").pattern("###").criterion("has_gold_nugget", RecipeProvider.conditionsFromItem(Items.GOLD_NUGGET)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_CHESTPLATE).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_gold_ingot", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_HELMET).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XXX").pattern("X X").criterion("has_gold_ingot", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_HOE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XX").pattern(" #").pattern(" #").criterion("has_gold_ingot", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_LEGGINGS).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XXX").pattern("X X").pattern("X X").criterion("has_gold_ingot", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_PICKAXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_gold_ingot", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.POWERED_RAIL, 6).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X X").pattern("X#X").pattern("XRX").criterion("has_rail", RecipeProvider.conditionsFromItem(Blocks.RAIL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_SHOVEL).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X").pattern("#").pattern("#").criterion("has_gold_ingot", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GOLDEN_SWORD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X").pattern("X").pattern("#").criterion("has_gold_ingot", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(exporter);
        RecipeProvider.offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, Items.GOLD_INGOT, Items.GOLD_BLOCK, "gold_ingot_from_gold_block", "gold_ingot");
        RecipeProvider.offerReversibleCompactingRecipesWithCompactingRecipeGroup(exporter, Items.GOLD_NUGGET, Items.GOLD_INGOT, "gold_ingot_from_nuggets", "gold_ingot");
        ShapelessRecipeJsonBuilder.create(Blocks.GRANITE).input(Blocks.DIORITE).input(Items.QUARTZ).criterion("has_quartz", RecipeProvider.conditionsFromItem(Items.QUARTZ)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.GRAY_DYE, 2).input(Items.BLACK_DYE).input(Items.WHITE_DYE).criterion("has_white_dye", RecipeProvider.conditionsFromItem(Items.WHITE_DYE)).criterion("has_black_dye", RecipeProvider.conditionsFromItem(Items.BLACK_DYE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.HAY_BLOCK).input(Character.valueOf('#'), Items.WHEAT).pattern("###").pattern("###").pattern("###").criterion("has_wheat", RecipeProvider.conditionsFromItem(Items.WHEAT)).offerTo(exporter);
        RecipeProvider.createPressurePlateRecipe(exporter, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_INGOT);
        ShapelessRecipeJsonBuilder.create(Items.HONEY_BOTTLE, 4).input(Items.HONEY_BLOCK).input(Items.GLASS_BOTTLE, 4).criterion("has_honey_block", RecipeProvider.conditionsFromItem(Blocks.HONEY_BLOCK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.HONEY_BLOCK, 1).input(Character.valueOf('S'), Items.HONEY_BOTTLE).pattern("SS").pattern("SS").criterion("has_honey_bottle", RecipeProvider.conditionsFromItem(Items.HONEY_BOTTLE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.HONEYCOMB_BLOCK).input(Character.valueOf('H'), Items.HONEYCOMB).pattern("HH").pattern("HH").criterion("has_honeycomb", RecipeProvider.conditionsFromItem(Items.HONEYCOMB)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.HOPPER).input(Character.valueOf('C'), Blocks.CHEST).input(Character.valueOf('I'), Items.IRON_INGOT).pattern("I I").pattern("ICI").pattern(" I ").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.HOPPER_MINECART).input(Blocks.HOPPER).input(Items.MINECART).criterion("has_minecart", RecipeProvider.conditionsFromItem(Items.MINECART)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.IRON_AXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XX").pattern("X#").pattern(" #").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.IRON_BARS, 16).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("###").pattern("###").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.IRON_BOOTS).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("X X").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.IRON_CHESTPLATE).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        RecipeProvider.createDoorRecipe(Blocks.IRON_DOOR, Ingredient.ofItems(Items.IRON_INGOT)).criterion(RecipeProvider.hasItem(Items.IRON_INGOT), RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.IRON_HELMET).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XXX").pattern("X X").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.IRON_HOE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XX").pattern(" #").pattern(" #").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        RecipeProvider.offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, Items.IRON_INGOT, Items.IRON_BLOCK, "iron_ingot_from_iron_block", "iron_ingot");
        RecipeProvider.offerReversibleCompactingRecipesWithCompactingRecipeGroup(exporter, Items.IRON_NUGGET, Items.IRON_INGOT, "iron_ingot_from_nuggets", "iron_ingot");
        ShapedRecipeJsonBuilder.create(Items.IRON_LEGGINGS).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XXX").pattern("X X").pattern("X X").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.IRON_PICKAXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.IRON_SHOVEL).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X").pattern("#").pattern("#").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.IRON_SWORD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X").pattern("X").pattern("#").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.IRON_TRAPDOOR).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("##").pattern("##").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.ITEM_FRAME).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.LEATHER).pattern("###").pattern("#X#").pattern("###").criterion("has_leather", RecipeProvider.conditionsFromItem(Items.LEATHER)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.JUKEBOX).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('X'), Items.DIAMOND).pattern("###").pattern("#X#").pattern("###").criterion("has_diamond", RecipeProvider.conditionsFromItem(Items.DIAMOND)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.LADDER, 3).input(Character.valueOf('#'), Items.STICK).pattern("# #").pattern("###").pattern("# #").criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).offerTo(exporter);
        RecipeProvider.offerReversibleCompactingRecipes(exporter, Items.LAPIS_LAZULI, Items.LAPIS_BLOCK);
        ShapedRecipeJsonBuilder.create(Items.LEAD, 2).input(Character.valueOf('~'), Items.STRING).input(Character.valueOf('O'), Items.SLIME_BALL).pattern("~~ ").pattern("~O ").pattern("  ~").criterion("has_slime_ball", RecipeProvider.conditionsFromItem(Items.SLIME_BALL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.LEATHER).input(Character.valueOf('#'), Items.RABBIT_HIDE).pattern("##").pattern("##").criterion("has_rabbit_hide", RecipeProvider.conditionsFromItem(Items.RABBIT_HIDE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.LEATHER_BOOTS).input(Character.valueOf('X'), Items.LEATHER).pattern("X X").pattern("X X").criterion("has_leather", RecipeProvider.conditionsFromItem(Items.LEATHER)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.LEATHER_CHESTPLATE).input(Character.valueOf('X'), Items.LEATHER).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_leather", RecipeProvider.conditionsFromItem(Items.LEATHER)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.LEATHER_HELMET).input(Character.valueOf('X'), Items.LEATHER).pattern("XXX").pattern("X X").criterion("has_leather", RecipeProvider.conditionsFromItem(Items.LEATHER)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.LEATHER_LEGGINGS).input(Character.valueOf('X'), Items.LEATHER).pattern("XXX").pattern("X X").pattern("X X").criterion("has_leather", RecipeProvider.conditionsFromItem(Items.LEATHER)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.LEATHER_HORSE_ARMOR).input(Character.valueOf('X'), Items.LEATHER).pattern("X X").pattern("XXX").pattern("X X").criterion("has_leather", RecipeProvider.conditionsFromItem(Items.LEATHER)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.LECTERN).input(Character.valueOf('S'), ItemTags.WOODEN_SLABS).input(Character.valueOf('B'), Blocks.BOOKSHELF).pattern("SSS").pattern(" B ").pattern(" S ").criterion("has_book", RecipeProvider.conditionsFromItem(Items.BOOK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.LEVER).input(Character.valueOf('#'), Blocks.COBBLESTONE).input(Character.valueOf('X'), Items.STICK).pattern("X").pattern("#").criterion("has_cobblestone", RecipeProvider.conditionsFromItem(Blocks.COBBLESTONE)).offerTo(exporter);
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.LIGHT_BLUE_DYE, Blocks.BLUE_ORCHID, "light_blue_dye");
        ShapelessRecipeJsonBuilder.create(Items.LIGHT_BLUE_DYE, 2).input(Items.BLUE_DYE).input(Items.WHITE_DYE).group("light_blue_dye").criterion("has_blue_dye", RecipeProvider.conditionsFromItem(Items.BLUE_DYE)).criterion("has_white_dye", RecipeProvider.conditionsFromItem(Items.WHITE_DYE)).offerTo(exporter, "light_blue_dye_from_blue_white_dye");
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.LIGHT_GRAY_DYE, Blocks.AZURE_BLUET, "light_gray_dye");
        ShapelessRecipeJsonBuilder.create(Items.LIGHT_GRAY_DYE, 2).input(Items.GRAY_DYE).input(Items.WHITE_DYE).group("light_gray_dye").criterion("has_gray_dye", RecipeProvider.conditionsFromItem(Items.GRAY_DYE)).criterion("has_white_dye", RecipeProvider.conditionsFromItem(Items.WHITE_DYE)).offerTo(exporter, "light_gray_dye_from_gray_white_dye");
        ShapelessRecipeJsonBuilder.create(Items.LIGHT_GRAY_DYE, 3).input(Items.BLACK_DYE).input(Items.WHITE_DYE, 2).group("light_gray_dye").criterion("has_white_dye", RecipeProvider.conditionsFromItem(Items.WHITE_DYE)).criterion("has_black_dye", RecipeProvider.conditionsFromItem(Items.BLACK_DYE)).offerTo(exporter, "light_gray_dye_from_black_white_dye");
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.LIGHT_GRAY_DYE, Blocks.OXEYE_DAISY, "light_gray_dye");
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.LIGHT_GRAY_DYE, Blocks.WHITE_TULIP, "light_gray_dye");
        RecipeProvider.createPressurePlateRecipe(exporter, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT);
        ShapedRecipeJsonBuilder.create(Blocks.LIGHTNING_ROD).input(Character.valueOf('#'), Items.COPPER_INGOT).pattern("#").pattern("#").pattern("#").criterion("has_copper_ingot", RecipeProvider.conditionsFromItem(Items.COPPER_INGOT)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.LIME_DYE, 2).input(Items.GREEN_DYE).input(Items.WHITE_DYE).criterion("has_green_dye", RecipeProvider.conditionsFromItem(Items.GREEN_DYE)).criterion("has_white_dye", RecipeProvider.conditionsFromItem(Items.WHITE_DYE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.JACK_O_LANTERN).input(Character.valueOf('A'), Blocks.CARVED_PUMPKIN).input(Character.valueOf('B'), Blocks.TORCH).pattern("A").pattern("B").criterion("has_carved_pumpkin", RecipeProvider.conditionsFromItem(Blocks.CARVED_PUMPKIN)).offerTo(exporter);
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.MAGENTA_DYE, Blocks.ALLIUM, "magenta_dye");
        ShapelessRecipeJsonBuilder.create(Items.MAGENTA_DYE, 4).input(Items.BLUE_DYE).input(Items.RED_DYE, 2).input(Items.WHITE_DYE).group("magenta_dye").criterion("has_blue_dye", RecipeProvider.conditionsFromItem(Items.BLUE_DYE)).criterion("has_rose_red", RecipeProvider.conditionsFromItem(Items.RED_DYE)).criterion("has_white_dye", RecipeProvider.conditionsFromItem(Items.WHITE_DYE)).offerTo(exporter, "magenta_dye_from_blue_red_white_dye");
        ShapelessRecipeJsonBuilder.create(Items.MAGENTA_DYE, 3).input(Items.BLUE_DYE).input(Items.RED_DYE).input(Items.PINK_DYE).group("magenta_dye").criterion("has_pink_dye", RecipeProvider.conditionsFromItem(Items.PINK_DYE)).criterion("has_blue_dye", RecipeProvider.conditionsFromItem(Items.BLUE_DYE)).criterion("has_red_dye", RecipeProvider.conditionsFromItem(Items.RED_DYE)).offerTo(exporter, "magenta_dye_from_blue_red_pink");
        RecipeProvider.offerShapelessRecipe(exporter, Items.MAGENTA_DYE, Blocks.LILAC, "magenta_dye", 2);
        ShapelessRecipeJsonBuilder.create(Items.MAGENTA_DYE, 2).input(Items.PURPLE_DYE).input(Items.PINK_DYE).group("magenta_dye").criterion("has_pink_dye", RecipeProvider.conditionsFromItem(Items.PINK_DYE)).criterion("has_purple_dye", RecipeProvider.conditionsFromItem(Items.PURPLE_DYE)).offerTo(exporter, "magenta_dye_from_purple_and_pink");
        ShapedRecipeJsonBuilder.create(Blocks.MAGMA_BLOCK).input(Character.valueOf('#'), Items.MAGMA_CREAM).pattern("##").pattern("##").criterion("has_magma_cream", RecipeProvider.conditionsFromItem(Items.MAGMA_CREAM)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.MAGMA_CREAM).input(Items.BLAZE_POWDER).input(Items.SLIME_BALL).criterion("has_blaze_powder", RecipeProvider.conditionsFromItem(Items.BLAZE_POWDER)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.MAP).input(Character.valueOf('#'), Items.PAPER).input(Character.valueOf('X'), Items.COMPASS).pattern("###").pattern("#X#").pattern("###").criterion("has_compass", RecipeProvider.conditionsFromItem(Items.COMPASS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.MELON).input(Character.valueOf('M'), Items.MELON_SLICE).pattern("MMM").pattern("MMM").pattern("MMM").criterion("has_melon", RecipeProvider.conditionsFromItem(Items.MELON_SLICE)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.MELON_SEEDS).input(Items.MELON_SLICE).criterion("has_melon", RecipeProvider.conditionsFromItem(Items.MELON_SLICE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.MINECART).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("# #").pattern("###").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Blocks.MOSSY_COBBLESTONE).input(Blocks.COBBLESTONE).input(Blocks.VINE).group("mossy_cobblestone").criterion("has_vine", RecipeProvider.conditionsFromItem(Blocks.VINE)).offerTo(exporter, RecipeProvider.convertBetween(Blocks.MOSSY_COBBLESTONE, Blocks.VINE));
        ShapelessRecipeJsonBuilder.create(Blocks.MOSSY_STONE_BRICKS).input(Blocks.STONE_BRICKS).input(Blocks.VINE).group("mossy_stone_bricks").criterion("has_vine", RecipeProvider.conditionsFromItem(Blocks.VINE)).offerTo(exporter, RecipeProvider.convertBetween(Blocks.MOSSY_STONE_BRICKS, Blocks.VINE));
        ShapelessRecipeJsonBuilder.create(Blocks.MOSSY_COBBLESTONE).input(Blocks.COBBLESTONE).input(Blocks.MOSS_BLOCK).group("mossy_cobblestone").criterion("has_moss_block", RecipeProvider.conditionsFromItem(Blocks.MOSS_BLOCK)).offerTo(exporter, RecipeProvider.convertBetween(Blocks.MOSSY_COBBLESTONE, Blocks.MOSS_BLOCK));
        ShapelessRecipeJsonBuilder.create(Blocks.MOSSY_STONE_BRICKS).input(Blocks.STONE_BRICKS).input(Blocks.MOSS_BLOCK).group("mossy_stone_bricks").criterion("has_moss_block", RecipeProvider.conditionsFromItem(Blocks.MOSS_BLOCK)).offerTo(exporter, RecipeProvider.convertBetween(Blocks.MOSSY_STONE_BRICKS, Blocks.MOSS_BLOCK));
        ShapelessRecipeJsonBuilder.create(Items.MUSHROOM_STEW).input(Blocks.BROWN_MUSHROOM).input(Blocks.RED_MUSHROOM).input(Items.BOWL).criterion("has_mushroom_stew", RecipeProvider.conditionsFromItem(Items.MUSHROOM_STEW)).criterion("has_bowl", RecipeProvider.conditionsFromItem(Items.BOWL)).criterion("has_brown_mushroom", RecipeProvider.conditionsFromItem(Blocks.BROWN_MUSHROOM)).criterion("has_red_mushroom", RecipeProvider.conditionsFromItem(Blocks.RED_MUSHROOM)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.NETHER_BRICKS).input(Character.valueOf('N'), Items.NETHER_BRICK).pattern("NN").pattern("NN").criterion("has_netherbrick", RecipeProvider.conditionsFromItem(Items.NETHER_BRICK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.NETHER_WART_BLOCK).input(Character.valueOf('#'), Items.NETHER_WART).pattern("###").pattern("###").pattern("###").criterion("has_nether_wart", RecipeProvider.conditionsFromItem(Items.NETHER_WART)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.NOTE_BLOCK).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('X'), Items.REDSTONE).pattern("###").pattern("#X#").pattern("###").criterion("has_redstone", RecipeProvider.conditionsFromItem(Items.REDSTONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.OBSERVER).input(Character.valueOf('Q'), Items.QUARTZ).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Blocks.COBBLESTONE).pattern("###").pattern("RRQ").pattern("###").criterion("has_quartz", RecipeProvider.conditionsFromItem(Items.QUARTZ)).offerTo(exporter);
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.ORANGE_DYE, Blocks.ORANGE_TULIP, "orange_dye");
        ShapelessRecipeJsonBuilder.create(Items.ORANGE_DYE, 2).input(Items.RED_DYE).input(Items.YELLOW_DYE).group("orange_dye").criterion("has_red_dye", RecipeProvider.conditionsFromItem(Items.RED_DYE)).criterion("has_yellow_dye", RecipeProvider.conditionsFromItem(Items.YELLOW_DYE)).offerTo(exporter, "orange_dye_from_red_yellow");
        ShapedRecipeJsonBuilder.create(Items.PAINTING).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Ingredient.fromTag(ItemTags.WOOL)).pattern("###").pattern("#X#").pattern("###").criterion("has_wool", RecipeProvider.conditionsFromTag(ItemTags.WOOL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.PAPER, 3).input(Character.valueOf('#'), Blocks.SUGAR_CANE).pattern("###").criterion("has_reeds", RecipeProvider.conditionsFromItem(Blocks.SUGAR_CANE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.QUARTZ_PILLAR, 2).input(Character.valueOf('#'), Blocks.QUARTZ_BLOCK).pattern("#").pattern("#").criterion("has_chiseled_quartz_block", RecipeProvider.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK)).criterion("has_quartz_block", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).criterion("has_quartz_pillar", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_PILLAR)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Blocks.PACKED_ICE).input(Blocks.ICE, 9).criterion("has_ice", RecipeProvider.conditionsFromItem(Blocks.ICE)).offerTo(exporter);
        RecipeProvider.offerShapelessRecipe(exporter, Items.PINK_DYE, Blocks.PEONY, "pink_dye", 2);
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.PINK_DYE, Blocks.PINK_TULIP, "pink_dye");
        ShapelessRecipeJsonBuilder.create(Items.PINK_DYE, 2).input(Items.RED_DYE).input(Items.WHITE_DYE).group("pink_dye").criterion("has_white_dye", RecipeProvider.conditionsFromItem(Items.WHITE_DYE)).criterion("has_red_dye", RecipeProvider.conditionsFromItem(Items.RED_DYE)).offerTo(exporter, "pink_dye_from_red_white_dye");
        ShapedRecipeJsonBuilder.create(Blocks.PISTON).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Blocks.COBBLESTONE).input(Character.valueOf('T'), ItemTags.PLANKS).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("TTT").pattern("#X#").pattern("#R#").criterion("has_redstone", RecipeProvider.conditionsFromItem(Items.REDSTONE)).offerTo(exporter);
        RecipeProvider.offerPolishedStoneRecipe(exporter, Blocks.POLISHED_BASALT, Blocks.BASALT);
        ShapedRecipeJsonBuilder.create(Blocks.PRISMARINE).input(Character.valueOf('S'), Items.PRISMARINE_SHARD).pattern("SS").pattern("SS").criterion("has_prismarine_shard", RecipeProvider.conditionsFromItem(Items.PRISMARINE_SHARD)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.PRISMARINE_BRICKS).input(Character.valueOf('S'), Items.PRISMARINE_SHARD).pattern("SSS").pattern("SSS").pattern("SSS").criterion("has_prismarine_shard", RecipeProvider.conditionsFromItem(Items.PRISMARINE_SHARD)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.PUMPKIN_PIE).input(Blocks.PUMPKIN).input(Items.SUGAR).input(Items.EGG).criterion("has_carved_pumpkin", RecipeProvider.conditionsFromItem(Blocks.CARVED_PUMPKIN)).criterion("has_pumpkin", RecipeProvider.conditionsFromItem(Blocks.PUMPKIN)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.PUMPKIN_SEEDS, 4).input(Blocks.PUMPKIN).criterion("has_pumpkin", RecipeProvider.conditionsFromItem(Blocks.PUMPKIN)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.PURPLE_DYE, 2).input(Items.BLUE_DYE).input(Items.RED_DYE).criterion("has_blue_dye", RecipeProvider.conditionsFromItem(Items.BLUE_DYE)).criterion("has_red_dye", RecipeProvider.conditionsFromItem(Items.RED_DYE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.SHULKER_BOX).input(Character.valueOf('#'), Blocks.CHEST).input(Character.valueOf('-'), Items.SHULKER_SHELL).pattern("-").pattern("#").pattern("-").criterion("has_shulker_shell", RecipeProvider.conditionsFromItem(Items.SHULKER_SHELL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.PURPUR_BLOCK, 4).input(Character.valueOf('F'), Items.POPPED_CHORUS_FRUIT).pattern("FF").pattern("FF").criterion("has_chorus_fruit_popped", RecipeProvider.conditionsFromItem(Items.POPPED_CHORUS_FRUIT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.PURPUR_PILLAR).input(Character.valueOf('#'), Blocks.PURPUR_SLAB).pattern("#").pattern("#").criterion("has_purpur_block", RecipeProvider.conditionsFromItem(Blocks.PURPUR_BLOCK)).offerTo(exporter);
        RecipeProvider.createSlabRecipe(Blocks.PURPUR_SLAB, Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).criterion("has_purpur_block", RecipeProvider.conditionsFromItem(Blocks.PURPUR_BLOCK)).offerTo(exporter);
        RecipeProvider.createStairsRecipe(Blocks.PURPUR_STAIRS, Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).criterion("has_purpur_block", RecipeProvider.conditionsFromItem(Blocks.PURPUR_BLOCK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.QUARTZ_BLOCK).input(Character.valueOf('#'), Items.QUARTZ).pattern("##").pattern("##").criterion("has_quartz", RecipeProvider.conditionsFromItem(Items.QUARTZ)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.QUARTZ_BRICKS, 4).input(Character.valueOf('#'), Blocks.QUARTZ_BLOCK).pattern("##").pattern("##").criterion("has_quartz_block", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).offerTo(exporter);
        RecipeProvider.createSlabRecipe(Blocks.QUARTZ_SLAB, Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).criterion("has_chiseled_quartz_block", RecipeProvider.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK)).criterion("has_quartz_block", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).criterion("has_quartz_pillar", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_PILLAR)).offerTo(exporter);
        RecipeProvider.createStairsRecipe(Blocks.QUARTZ_STAIRS, Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).criterion("has_chiseled_quartz_block", RecipeProvider.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK)).criterion("has_quartz_block", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).criterion("has_quartz_pillar", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_PILLAR)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.RABBIT_STEW).input(Items.BAKED_POTATO).input(Items.COOKED_RABBIT).input(Items.BOWL).input(Items.CARROT).input(Blocks.BROWN_MUSHROOM).group("rabbit_stew").criterion("has_cooked_rabbit", RecipeProvider.conditionsFromItem(Items.COOKED_RABBIT)).offerTo(exporter, RecipeProvider.convertBetween(Items.RABBIT_STEW, Items.BROWN_MUSHROOM));
        ShapelessRecipeJsonBuilder.create(Items.RABBIT_STEW).input(Items.BAKED_POTATO).input(Items.COOKED_RABBIT).input(Items.BOWL).input(Items.CARROT).input(Blocks.RED_MUSHROOM).group("rabbit_stew").criterion("has_cooked_rabbit", RecipeProvider.conditionsFromItem(Items.COOKED_RABBIT)).offerTo(exporter, RecipeProvider.convertBetween(Items.RABBIT_STEW, Items.RED_MUSHROOM));
        ShapedRecipeJsonBuilder.create(Blocks.RAIL, 16).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("X X").criterion("has_minecart", RecipeProvider.conditionsFromItem(Items.MINECART)).offerTo(exporter);
        RecipeProvider.offerReversibleCompactingRecipes(exporter, Items.REDSTONE, Items.REDSTONE_BLOCK);
        ShapedRecipeJsonBuilder.create(Blocks.REDSTONE_LAMP).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('G'), Blocks.GLOWSTONE).pattern(" R ").pattern("RGR").pattern(" R ").criterion("has_glowstone", RecipeProvider.conditionsFromItem(Blocks.GLOWSTONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.REDSTONE_TORCH).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.REDSTONE).pattern("X").pattern("#").criterion("has_redstone", RecipeProvider.conditionsFromItem(Items.REDSTONE)).offerTo(exporter);
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.RED_DYE, Items.BEETROOT, "red_dye");
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.RED_DYE, Blocks.POPPY, "red_dye");
        RecipeProvider.offerShapelessRecipe(exporter, Items.RED_DYE, Blocks.ROSE_BUSH, "red_dye", 2);
        ShapelessRecipeJsonBuilder.create(Items.RED_DYE).input(Blocks.RED_TULIP).group("red_dye").criterion("has_red_flower", RecipeProvider.conditionsFromItem(Blocks.RED_TULIP)).offerTo(exporter, "red_dye_from_tulip");
        ShapedRecipeJsonBuilder.create(Blocks.RED_NETHER_BRICKS).input(Character.valueOf('W'), Items.NETHER_WART).input(Character.valueOf('N'), Items.NETHER_BRICK).pattern("NW").pattern("WN").criterion("has_nether_wart", RecipeProvider.conditionsFromItem(Items.NETHER_WART)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.RED_SANDSTONE).input(Character.valueOf('#'), Blocks.RED_SAND).pattern("##").pattern("##").criterion("has_sand", RecipeProvider.conditionsFromItem(Blocks.RED_SAND)).offerTo(exporter);
        RecipeProvider.createSlabRecipe(Blocks.RED_SANDSTONE_SLAB, Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE)).criterion("has_red_sandstone", RecipeProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).criterion("has_chiseled_red_sandstone", RecipeProvider.conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE)).offerTo(exporter);
        RecipeProvider.createStairsRecipe(Blocks.RED_SANDSTONE_STAIRS, Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE)).criterion("has_red_sandstone", RecipeProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).criterion("has_chiseled_red_sandstone", RecipeProvider.conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE)).criterion("has_cut_red_sandstone", RecipeProvider.conditionsFromItem(Blocks.CUT_RED_SANDSTONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.REPEATER).input(Character.valueOf('#'), Blocks.REDSTONE_TORCH).input(Character.valueOf('X'), Items.REDSTONE).input(Character.valueOf('I'), Blocks.STONE).pattern("#X#").pattern("III").criterion("has_redstone_torch", RecipeProvider.conditionsFromItem(Blocks.REDSTONE_TORCH)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.SANDSTONE).input(Character.valueOf('#'), Blocks.SAND).pattern("##").pattern("##").criterion("has_sand", RecipeProvider.conditionsFromItem(Blocks.SAND)).offerTo(exporter);
        RecipeProvider.createSlabRecipe(Blocks.SANDSTONE_SLAB, Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE)).criterion("has_sandstone", RecipeProvider.conditionsFromItem(Blocks.SANDSTONE)).criterion("has_chiseled_sandstone", RecipeProvider.conditionsFromItem(Blocks.CHISELED_SANDSTONE)).offerTo(exporter);
        RecipeProvider.createStairsRecipe(Blocks.SANDSTONE_STAIRS, Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE)).criterion("has_sandstone", RecipeProvider.conditionsFromItem(Blocks.SANDSTONE)).criterion("has_chiseled_sandstone", RecipeProvider.conditionsFromItem(Blocks.CHISELED_SANDSTONE)).criterion("has_cut_sandstone", RecipeProvider.conditionsFromItem(Blocks.CUT_SANDSTONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.SEA_LANTERN).input(Character.valueOf('S'), Items.PRISMARINE_SHARD).input(Character.valueOf('C'), Items.PRISMARINE_CRYSTALS).pattern("SCS").pattern("CCC").pattern("SCS").criterion("has_prismarine_crystals", RecipeProvider.conditionsFromItem(Items.PRISMARINE_CRYSTALS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.SHEARS).input(Character.valueOf('#'), Items.IRON_INGOT).pattern(" #").pattern("# ").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.SHIELD).input(Character.valueOf('W'), ItemTags.PLANKS).input(Character.valueOf('o'), Items.IRON_INGOT).pattern("WoW").pattern("WWW").pattern(" W ").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        RecipeProvider.offerReversibleCompactingRecipes(exporter, Items.SLIME_BALL, Items.SLIME_BLOCK);
        RecipeProvider.offerCutCopperRecipe(exporter, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
        RecipeProvider.offerCutCopperRecipe(exporter, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
        ShapedRecipeJsonBuilder.create(Blocks.SNOW_BLOCK).input(Character.valueOf('#'), Items.SNOWBALL).pattern("##").pattern("##").criterion("has_snowball", RecipeProvider.conditionsFromItem(Items.SNOWBALL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.SNOW, 6).input(Character.valueOf('#'), Blocks.SNOW_BLOCK).pattern("###").criterion("has_snowball", RecipeProvider.conditionsFromItem(Items.SNOWBALL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.SOUL_CAMPFIRE).input(Character.valueOf('L'), ItemTags.LOGS).input(Character.valueOf('S'), Items.STICK).input(Character.valueOf('#'), ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern(" S ").pattern("S#S").pattern("LLL").criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).criterion("has_soul_sand", RecipeProvider.conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.GLISTERING_MELON_SLICE).input(Character.valueOf('#'), Items.GOLD_NUGGET).input(Character.valueOf('X'), Items.MELON_SLICE).pattern("###").pattern("#X#").pattern("###").criterion("has_melon", RecipeProvider.conditionsFromItem(Items.MELON_SLICE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.SPECTRAL_ARROW, 2).input(Character.valueOf('#'), Items.GLOWSTONE_DUST).input(Character.valueOf('X'), Items.ARROW).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_glowstone_dust", RecipeProvider.conditionsFromItem(Items.GLOWSTONE_DUST)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.SPYGLASS).input(Character.valueOf('#'), Items.AMETHYST_SHARD).input(Character.valueOf('X'), Items.COPPER_INGOT).pattern(" # ").pattern(" X ").pattern(" X ").criterion("has_amethyst_shard", RecipeProvider.conditionsFromItem(Items.AMETHYST_SHARD)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.STICK, 4).input(Character.valueOf('#'), ItemTags.PLANKS).pattern("#").pattern("#").group("sticks").criterion("has_planks", RecipeProvider.conditionsFromTag(ItemTags.PLANKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.STICK, 1).input(Character.valueOf('#'), Blocks.BAMBOO).pattern("#").pattern("#").group("sticks").criterion("has_bamboo", RecipeProvider.conditionsFromItem(Blocks.BAMBOO)).offerTo(exporter, "stick_from_bamboo_item");
        ShapedRecipeJsonBuilder.create(Blocks.STICKY_PISTON).input(Character.valueOf('P'), Blocks.PISTON).input(Character.valueOf('S'), Items.SLIME_BALL).pattern("S").pattern("P").criterion("has_slime_ball", RecipeProvider.conditionsFromItem(Items.SLIME_BALL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.STONE_BRICKS, 4).input(Character.valueOf('#'), Blocks.STONE).pattern("##").pattern("##").criterion("has_stone", RecipeProvider.conditionsFromItem(Blocks.STONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.STONE_AXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern("X#").pattern(" #").criterion("has_cobblestone", RecipeProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS)).offerTo(exporter);
        RecipeProvider.createSlabRecipe(Blocks.STONE_BRICK_SLAB, Ingredient.ofItems(Blocks.STONE_BRICKS)).criterion("has_stone_bricks", RecipeProvider.conditionsFromTag(ItemTags.STONE_BRICKS)).offerTo(exporter);
        RecipeProvider.createStairsRecipe(Blocks.STONE_BRICK_STAIRS, Ingredient.ofItems(Blocks.STONE_BRICKS)).criterion("has_stone_bricks", RecipeProvider.conditionsFromTag(ItemTags.STONE_BRICKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.STONE_HOE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern(" #").pattern(" #").criterion("has_cobblestone", RecipeProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.STONE_PICKAXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_cobblestone", RecipeProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.STONE_SHOVEL).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("#").pattern("#").criterion("has_cobblestone", RecipeProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS)).offerTo(exporter);
        RecipeProvider.offerSlabRecipe(exporter, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);
        ShapedRecipeJsonBuilder.create(Items.STONE_SWORD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("X").pattern("#").criterion("has_cobblestone", RecipeProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.WHITE_WOOL).input(Character.valueOf('#'), Items.STRING).pattern("##").pattern("##").criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING)).offerTo(exporter, RecipeProvider.convertBetween(Blocks.WHITE_WOOL, Items.STRING));
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.SUGAR, Blocks.SUGAR_CANE, "sugar");
        ShapelessRecipeJsonBuilder.create(Items.SUGAR, 3).input(Items.HONEY_BOTTLE).group("sugar").criterion("has_honey_bottle", RecipeProvider.conditionsFromItem(Items.HONEY_BOTTLE)).offerTo(exporter, RecipeProvider.convertBetween(Items.SUGAR, Items.HONEY_BOTTLE));
        ShapedRecipeJsonBuilder.create(Blocks.TARGET).input(Character.valueOf('H'), Items.HAY_BLOCK).input(Character.valueOf('R'), Items.REDSTONE).pattern(" R ").pattern("RHR").pattern(" R ").criterion("has_redstone", RecipeProvider.conditionsFromItem(Items.REDSTONE)).criterion("has_hay_block", RecipeProvider.conditionsFromItem(Blocks.HAY_BLOCK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.TNT).input(Character.valueOf('#'), Ingredient.ofItems(Blocks.SAND, Blocks.RED_SAND)).input(Character.valueOf('X'), Items.GUNPOWDER).pattern("X#X").pattern("#X#").pattern("X#X").criterion("has_gunpowder", RecipeProvider.conditionsFromItem(Items.GUNPOWDER)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.TNT_MINECART).input(Blocks.TNT).input(Items.MINECART).criterion("has_minecart", RecipeProvider.conditionsFromItem(Items.MINECART)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.TORCH, 4).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Ingredient.ofItems(Items.COAL, Items.CHARCOAL)).pattern("X").pattern("#").criterion("has_stone_pickaxe", RecipeProvider.conditionsFromItem(Items.STONE_PICKAXE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.SOUL_TORCH, 4).input(Character.valueOf('X'), Ingredient.ofItems(Items.COAL, Items.CHARCOAL)).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('S'), ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern("X").pattern("#").pattern("S").criterion("has_soul_sand", RecipeProvider.conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.LANTERN).input(Character.valueOf('#'), Items.TORCH).input(Character.valueOf('X'), Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").criterion("has_iron_nugget", RecipeProvider.conditionsFromItem(Items.IRON_NUGGET)).criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.SOUL_LANTERN).input(Character.valueOf('#'), Items.SOUL_TORCH).input(Character.valueOf('X'), Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").criterion("has_soul_torch", RecipeProvider.conditionsFromItem(Items.SOUL_TORCH)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Blocks.TRAPPED_CHEST).input(Blocks.CHEST).input(Blocks.TRIPWIRE_HOOK).criterion("has_tripwire_hook", RecipeProvider.conditionsFromItem(Blocks.TRIPWIRE_HOOK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.TRIPWIRE_HOOK, 2).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('S'), Items.STICK).input(Character.valueOf('I'), Items.IRON_INGOT).pattern("I").pattern("S").pattern("#").criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.TURTLE_HELMET).input(Character.valueOf('X'), Items.SCUTE).pattern("XXX").pattern("X X").criterion("has_scute", RecipeProvider.conditionsFromItem(Items.SCUTE)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.WHEAT, 9).input(Blocks.HAY_BLOCK).criterion("has_hay_block", RecipeProvider.conditionsFromItem(Blocks.HAY_BLOCK)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.WHITE_DYE).input(Items.BONE_MEAL).group("white_dye").criterion("has_bone_meal", RecipeProvider.conditionsFromItem(Items.BONE_MEAL)).offerTo(exporter);
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.WHITE_DYE, Blocks.LILY_OF_THE_VALLEY, "white_dye");
        ShapedRecipeJsonBuilder.create(Items.WOODEN_AXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("XX").pattern("X#").pattern(" #").criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.WOODEN_HOE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("XX").pattern(" #").pattern(" #").criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.WOODEN_PICKAXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.WOODEN_SHOVEL).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("X").pattern("#").pattern("#").criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.WOODEN_SWORD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("X").pattern("X").pattern("#").criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.WRITABLE_BOOK).input(Items.BOOK).input(Items.INK_SAC).input(Items.FEATHER).criterion("has_book", RecipeProvider.conditionsFromItem(Items.BOOK)).offerTo(exporter);
        RecipeProvider.offerSingleOutputShapelessRecipe(exporter, Items.YELLOW_DYE, Blocks.DANDELION, "yellow_dye");
        RecipeProvider.offerShapelessRecipe(exporter, Items.YELLOW_DYE, Blocks.SUNFLOWER, "yellow_dye", 2);
        RecipeProvider.offerReversibleCompactingRecipes(exporter, Items.DRIED_KELP, Items.DRIED_KELP_BLOCK);
        ShapedRecipeJsonBuilder.create(Blocks.CONDUIT).input(Character.valueOf('#'), Items.NAUTILUS_SHELL).input(Character.valueOf('X'), Items.HEART_OF_THE_SEA).pattern("###").pattern("#X#").pattern("###").criterion("has_nautilus_core", RecipeProvider.conditionsFromItem(Items.HEART_OF_THE_SEA)).criterion("has_nautilus_shell", RecipeProvider.conditionsFromItem(Items.NAUTILUS_SHELL)).offerTo(exporter);
        RecipeProvider.offerWallRecipe(exporter, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
        RecipeProvider.offerWallRecipe(exporter, Blocks.STONE_BRICK_WALL, Blocks.STONE_BRICKS);
        RecipeProvider.offerWallRecipe(exporter, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
        ShapelessRecipeJsonBuilder.create(Items.CREEPER_BANNER_PATTERN).input(Items.PAPER).input(Items.CREEPER_HEAD).criterion("has_creeper_head", RecipeProvider.conditionsFromItem(Items.CREEPER_HEAD)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.SKULL_BANNER_PATTERN).input(Items.PAPER).input(Items.WITHER_SKELETON_SKULL).criterion("has_wither_skeleton_skull", RecipeProvider.conditionsFromItem(Items.WITHER_SKELETON_SKULL)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.FLOWER_BANNER_PATTERN).input(Items.PAPER).input(Blocks.OXEYE_DAISY).criterion("has_oxeye_daisy", RecipeProvider.conditionsFromItem(Blocks.OXEYE_DAISY)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(Items.MOJANG_BANNER_PATTERN).input(Items.PAPER).input(Items.ENCHANTED_GOLDEN_APPLE).criterion("has_enchanted_golden_apple", RecipeProvider.conditionsFromItem(Items.ENCHANTED_GOLDEN_APPLE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.SCAFFOLDING, 6).input(Character.valueOf('~'), Items.STRING).input(Character.valueOf('I'), Blocks.BAMBOO).pattern("I~I").pattern("I I").pattern("I I").criterion("has_bamboo", RecipeProvider.conditionsFromItem(Blocks.BAMBOO)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.GRINDSTONE).input(Character.valueOf('I'), Items.STICK).input(Character.valueOf('-'), Blocks.STONE_SLAB).input(Character.valueOf('#'), ItemTags.PLANKS).pattern("I-I").pattern("# #").criterion("has_stone_slab", RecipeProvider.conditionsFromItem(Blocks.STONE_SLAB)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.BLAST_FURNACE).input(Character.valueOf('#'), Blocks.SMOOTH_STONE).input(Character.valueOf('X'), Blocks.FURNACE).input(Character.valueOf('I'), Items.IRON_INGOT).pattern("III").pattern("IXI").pattern("###").criterion("has_smooth_stone", RecipeProvider.conditionsFromItem(Blocks.SMOOTH_STONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.SMOKER).input(Character.valueOf('#'), ItemTags.LOGS).input(Character.valueOf('X'), Blocks.FURNACE).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_furnace", RecipeProvider.conditionsFromItem(Blocks.FURNACE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.CARTOGRAPHY_TABLE).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('@'), Items.PAPER).pattern("@@").pattern("##").pattern("##").criterion("has_paper", RecipeProvider.conditionsFromItem(Items.PAPER)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.SMITHING_TABLE).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('@'), Items.IRON_INGOT).pattern("@@").pattern("##").pattern("##").criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.FLETCHING_TABLE).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('@'), Items.FLINT).pattern("@@").pattern("##").pattern("##").criterion("has_flint", RecipeProvider.conditionsFromItem(Items.FLINT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.STONECUTTER).input(Character.valueOf('I'), Items.IRON_INGOT).input(Character.valueOf('#'), Blocks.STONE).pattern(" I ").pattern("###").criterion("has_stone", RecipeProvider.conditionsFromItem(Blocks.STONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.LODESTONE).input(Character.valueOf('S'), Items.CHISELED_STONE_BRICKS).input(Character.valueOf('#'), Items.NETHERITE_INGOT).pattern("SSS").pattern("S#S").pattern("SSS").criterion("has_netherite_ingot", RecipeProvider.conditionsFromItem(Items.NETHERITE_INGOT)).offerTo(exporter);
        RecipeProvider.offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, Items.NETHERITE_INGOT, Items.NETHERITE_BLOCK, "netherite_ingot_from_netherite_block", "netherite_ingot");
        ShapelessRecipeJsonBuilder.create(Items.NETHERITE_INGOT).input(Items.NETHERITE_SCRAP, 4).input(Items.GOLD_INGOT, 4).group("netherite_ingot").criterion("has_netherite_scrap", RecipeProvider.conditionsFromItem(Items.NETHERITE_SCRAP)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.RESPAWN_ANCHOR).input(Character.valueOf('O'), Blocks.CRYING_OBSIDIAN).input(Character.valueOf('G'), Blocks.GLOWSTONE).pattern("OOO").pattern("GGG").pattern("OOO").criterion("has_obsidian", RecipeProvider.conditionsFromItem(Blocks.CRYING_OBSIDIAN)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.CHAIN).input(Character.valueOf('I'), Items.IRON_INGOT).input(Character.valueOf('N'), Items.IRON_NUGGET).pattern("N").pattern("I").pattern("N").criterion("has_iron_nugget", RecipeProvider.conditionsFromItem(Items.IRON_NUGGET)).criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.TINTED_GLASS, 2).input(Character.valueOf('G'), Blocks.GLASS).input(Character.valueOf('S'), Items.AMETHYST_SHARD).pattern(" S ").pattern("SGS").pattern(" S ").criterion("has_amethyst_shard", RecipeProvider.conditionsFromItem(Items.AMETHYST_SHARD)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Blocks.AMETHYST_BLOCK).input(Character.valueOf('S'), Items.AMETHYST_SHARD).pattern("SS").pattern("SS").criterion("has_amethyst_shard", RecipeProvider.conditionsFromItem(Items.AMETHYST_SHARD)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(Items.RECOVERY_COMPASS).input(Character.valueOf('C'), Items.COMPASS).input(Character.valueOf('S'), Items.ECHO_SHARD).pattern("SSS").pattern("SCS").pattern("SSS").criterion("has_echo_shard", RecipeProvider.conditionsFromItem(Items.ECHO_SHARD)).offerTo(exporter);
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
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.POTATO), Items.BAKED_POTATO, 0.35f, 200).criterion("has_potato", RecipeProvider.conditionsFromItem(Items.POTATO)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.CLAY_BALL), Items.BRICK, 0.3f, 200).criterion("has_clay_ball", RecipeProvider.conditionsFromItem(Items.CLAY_BALL)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.fromTag(ItemTags.LOGS_THAT_BURN), Items.CHARCOAL, 0.15f, 200).criterion("has_log", RecipeProvider.conditionsFromTag(ItemTags.LOGS_THAT_BURN)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.CHORUS_FRUIT), Items.POPPED_CHORUS_FRUIT, 0.1f, 200).criterion("has_chorus_fruit", RecipeProvider.conditionsFromItem(Items.CHORUS_FRUIT)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.BEEF), Items.COOKED_BEEF, 0.35f, 200).criterion("has_beef", RecipeProvider.conditionsFromItem(Items.BEEF)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35f, 200).criterion("has_chicken", RecipeProvider.conditionsFromItem(Items.CHICKEN)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.COD), Items.COOKED_COD, 0.35f, 200).criterion("has_cod", RecipeProvider.conditionsFromItem(Items.COD)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.KELP), Items.DRIED_KELP, 0.1f, 200).criterion("has_kelp", RecipeProvider.conditionsFromItem(Blocks.KELP)).offerTo(exporter, RecipeProvider.getSmeltingItemPath(Items.DRIED_KELP));
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.SALMON), Items.COOKED_SALMON, 0.35f, 200).criterion("has_salmon", RecipeProvider.conditionsFromItem(Items.SALMON)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.MUTTON), Items.COOKED_MUTTON, 0.35f, 200).criterion("has_mutton", RecipeProvider.conditionsFromItem(Items.MUTTON)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35f, 200).criterion("has_porkchop", RecipeProvider.conditionsFromItem(Items.PORKCHOP)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.RABBIT), Items.COOKED_RABBIT, 0.35f, 200).criterion("has_rabbit", RecipeProvider.conditionsFromItem(Items.RABBIT)).offerTo(exporter);
        RecipeProvider.offerSmelting(exporter, COAL_ORES, Items.COAL, 0.1f, 200, "coal");
        RecipeProvider.offerSmelting(exporter, IRON_ORES, Items.IRON_INGOT, 0.7f, 200, "iron_ingot");
        RecipeProvider.offerSmelting(exporter, COPPER_ORES, Items.COPPER_INGOT, 0.7f, 200, "copper_ingot");
        RecipeProvider.offerSmelting(exporter, GOLD_ORES, Items.GOLD_INGOT, 1.0f, 200, "gold_ingot");
        RecipeProvider.offerSmelting(exporter, DIAMOND_ORES, Items.DIAMOND, 1.0f, 200, "diamond");
        RecipeProvider.offerSmelting(exporter, LAPIS_ORES, Items.LAPIS_LAZULI, 0.2f, 200, "lapis_lazuli");
        RecipeProvider.offerSmelting(exporter, REDSTONE_ORES, Items.REDSTONE, 0.7f, 200, "redstone");
        RecipeProvider.offerSmelting(exporter, EMERALD_ORES, Items.EMERALD, 1.0f, 200, "emerald");
        RecipeProvider.offerReversibleCompactingRecipes(exporter, Items.RAW_IRON, Items.RAW_IRON_BLOCK);
        RecipeProvider.offerReversibleCompactingRecipes(exporter, Items.RAW_COPPER, Items.RAW_COPPER_BLOCK);
        RecipeProvider.offerReversibleCompactingRecipes(exporter, Items.RAW_GOLD, Items.RAW_GOLD_BLOCK);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.fromTag(ItemTags.SAND), Blocks.GLASS.asItem(), 0.1f, 200).criterion("has_sand", RecipeProvider.conditionsFromTag(ItemTags.SAND)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.SEA_PICKLE), Items.LIME_DYE, 0.1f, 200).criterion("has_sea_pickle", RecipeProvider.conditionsFromItem(Blocks.SEA_PICKLE)).offerTo(exporter, RecipeProvider.getSmeltingItemPath(Items.LIME_DYE));
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.CACTUS.asItem()), Items.GREEN_DYE, 1.0f, 200).criterion("has_cactus", RecipeProvider.conditionsFromItem(Blocks.CACTUS)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1f, 200).criterion("has_golden_pickaxe", RecipeProvider.conditionsFromItem(Items.GOLDEN_PICKAXE)).criterion("has_golden_shovel", RecipeProvider.conditionsFromItem(Items.GOLDEN_SHOVEL)).criterion("has_golden_axe", RecipeProvider.conditionsFromItem(Items.GOLDEN_AXE)).criterion("has_golden_hoe", RecipeProvider.conditionsFromItem(Items.GOLDEN_HOE)).criterion("has_golden_sword", RecipeProvider.conditionsFromItem(Items.GOLDEN_SWORD)).criterion("has_golden_helmet", RecipeProvider.conditionsFromItem(Items.GOLDEN_HELMET)).criterion("has_golden_chestplate", RecipeProvider.conditionsFromItem(Items.GOLDEN_CHESTPLATE)).criterion("has_golden_leggings", RecipeProvider.conditionsFromItem(Items.GOLDEN_LEGGINGS)).criterion("has_golden_boots", RecipeProvider.conditionsFromItem(Items.GOLDEN_BOOTS)).criterion("has_golden_horse_armor", RecipeProvider.conditionsFromItem(Items.GOLDEN_HORSE_ARMOR)).offerTo(exporter, RecipeProvider.getSmeltingItemPath(Items.GOLD_NUGGET));
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1f, 200).criterion("has_iron_pickaxe", RecipeProvider.conditionsFromItem(Items.IRON_PICKAXE)).criterion("has_iron_shovel", RecipeProvider.conditionsFromItem(Items.IRON_SHOVEL)).criterion("has_iron_axe", RecipeProvider.conditionsFromItem(Items.IRON_AXE)).criterion("has_iron_hoe", RecipeProvider.conditionsFromItem(Items.IRON_HOE)).criterion("has_iron_sword", RecipeProvider.conditionsFromItem(Items.IRON_SWORD)).criterion("has_iron_helmet", RecipeProvider.conditionsFromItem(Items.IRON_HELMET)).criterion("has_iron_chestplate", RecipeProvider.conditionsFromItem(Items.IRON_CHESTPLATE)).criterion("has_iron_leggings", RecipeProvider.conditionsFromItem(Items.IRON_LEGGINGS)).criterion("has_iron_boots", RecipeProvider.conditionsFromItem(Items.IRON_BOOTS)).criterion("has_iron_horse_armor", RecipeProvider.conditionsFromItem(Items.IRON_HORSE_ARMOR)).criterion("has_chainmail_helmet", RecipeProvider.conditionsFromItem(Items.CHAINMAIL_HELMET)).criterion("has_chainmail_chestplate", RecipeProvider.conditionsFromItem(Items.CHAINMAIL_CHESTPLATE)).criterion("has_chainmail_leggings", RecipeProvider.conditionsFromItem(Items.CHAINMAIL_LEGGINGS)).criterion("has_chainmail_boots", RecipeProvider.conditionsFromItem(Items.CHAINMAIL_BOOTS)).offerTo(exporter, RecipeProvider.getSmeltingItemPath(Items.IRON_NUGGET));
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.CLAY), Blocks.TERRACOTTA.asItem(), 0.35f, 200).criterion("has_clay_block", RecipeProvider.conditionsFromItem(Blocks.CLAY)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.NETHERRACK), Items.NETHER_BRICK, 0.1f, 200).criterion("has_netherrack", RecipeProvider.conditionsFromItem(Blocks.NETHERRACK)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2f, 200).criterion("has_nether_quartz_ore", RecipeProvider.conditionsFromItem(Blocks.NETHER_QUARTZ_ORE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.WET_SPONGE), Blocks.SPONGE.asItem(), 0.15f, 200).criterion("has_wet_sponge", RecipeProvider.conditionsFromItem(Blocks.WET_SPONGE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.STONE.asItem(), 0.1f, 200).criterion("has_cobblestone", RecipeProvider.conditionsFromItem(Blocks.COBBLESTONE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.STONE), Blocks.SMOOTH_STONE.asItem(), 0.1f, 200).criterion("has_stone", RecipeProvider.conditionsFromItem(Blocks.STONE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SMOOTH_SANDSTONE.asItem(), 0.1f, 200).criterion("has_sandstone", RecipeProvider.conditionsFromItem(Blocks.SANDSTONE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1f, 200).criterion("has_red_sandstone", RecipeProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.SMOOTH_QUARTZ.asItem(), 0.1f, 200).criterion("has_quartz_block", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1f, 200).criterion("has_stone_bricks", RecipeProvider.conditionsFromItem(Blocks.STONE_BRICKS)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.BLACK_TERRACOTTA), Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_black_terracotta", RecipeProvider.conditionsFromItem(Blocks.BLACK_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.BLUE_TERRACOTTA), Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_blue_terracotta", RecipeProvider.conditionsFromItem(Blocks.BLUE_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.BROWN_TERRACOTTA), Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_brown_terracotta", RecipeProvider.conditionsFromItem(Blocks.BROWN_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.CYAN_TERRACOTTA), Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_cyan_terracotta", RecipeProvider.conditionsFromItem(Blocks.CYAN_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.GRAY_TERRACOTTA), Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_gray_terracotta", RecipeProvider.conditionsFromItem(Blocks.GRAY_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.GREEN_TERRACOTTA), Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_green_terracotta", RecipeProvider.conditionsFromItem(Blocks.GREEN_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.LIGHT_BLUE_TERRACOTTA), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_light_blue_terracotta", RecipeProvider.conditionsFromItem(Blocks.LIGHT_BLUE_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.LIGHT_GRAY_TERRACOTTA), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_light_gray_terracotta", RecipeProvider.conditionsFromItem(Blocks.LIGHT_GRAY_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.LIME_TERRACOTTA), Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_lime_terracotta", RecipeProvider.conditionsFromItem(Blocks.LIME_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.MAGENTA_TERRACOTTA), Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_magenta_terracotta", RecipeProvider.conditionsFromItem(Blocks.MAGENTA_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.ORANGE_TERRACOTTA), Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_orange_terracotta", RecipeProvider.conditionsFromItem(Blocks.ORANGE_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.PINK_TERRACOTTA), Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_pink_terracotta", RecipeProvider.conditionsFromItem(Blocks.PINK_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.PURPLE_TERRACOTTA), Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_purple_terracotta", RecipeProvider.conditionsFromItem(Blocks.PURPLE_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.RED_TERRACOTTA), Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_red_terracotta", RecipeProvider.conditionsFromItem(Blocks.RED_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.WHITE_TERRACOTTA), Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_white_terracotta", RecipeProvider.conditionsFromItem(Blocks.WHITE_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.YELLOW_TERRACOTTA), Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_yellow_terracotta", RecipeProvider.conditionsFromItem(Blocks.YELLOW_TERRACOTTA)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0f, 200).criterion("has_ancient_debris", RecipeProvider.conditionsFromItem(Blocks.ANCIENT_DEBRIS)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.BASALT), Blocks.SMOOTH_BASALT, 0.1f, 200).criterion("has_basalt", RecipeProvider.conditionsFromItem(Blocks.BASALT)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(Blocks.COBBLED_DEEPSLATE), Blocks.DEEPSLATE, 0.1f, 200).criterion("has_cobbled_deepslate", RecipeProvider.conditionsFromItem(Blocks.COBBLED_DEEPSLATE)).offerTo(exporter);
        RecipeProvider.offerBlasting(exporter, COAL_ORES, Items.COAL, 0.1f, 100, "coal");
        RecipeProvider.offerBlasting(exporter, IRON_ORES, Items.IRON_INGOT, 0.7f, 100, "iron_ingot");
        RecipeProvider.offerBlasting(exporter, COPPER_ORES, Items.COPPER_INGOT, 0.7f, 100, "copper_ingot");
        RecipeProvider.offerBlasting(exporter, GOLD_ORES, Items.GOLD_INGOT, 1.0f, 100, "gold_ingot");
        RecipeProvider.offerBlasting(exporter, DIAMOND_ORES, Items.DIAMOND, 1.0f, 100, "diamond");
        RecipeProvider.offerBlasting(exporter, LAPIS_ORES, Items.LAPIS_LAZULI, 0.2f, 100, "lapis_lazuli");
        RecipeProvider.offerBlasting(exporter, REDSTONE_ORES, Items.REDSTONE, 0.7f, 100, "redstone");
        RecipeProvider.offerBlasting(exporter, EMERALD_ORES, Items.EMERALD, 1.0f, 100, "emerald");
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2f, 100).criterion("has_nether_quartz_ore", RecipeProvider.conditionsFromItem(Blocks.NETHER_QUARTZ_ORE)).offerTo(exporter, RecipeProvider.getBlastingItemPath(Items.QUARTZ));
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1f, 100).criterion("has_golden_pickaxe", RecipeProvider.conditionsFromItem(Items.GOLDEN_PICKAXE)).criterion("has_golden_shovel", RecipeProvider.conditionsFromItem(Items.GOLDEN_SHOVEL)).criterion("has_golden_axe", RecipeProvider.conditionsFromItem(Items.GOLDEN_AXE)).criterion("has_golden_hoe", RecipeProvider.conditionsFromItem(Items.GOLDEN_HOE)).criterion("has_golden_sword", RecipeProvider.conditionsFromItem(Items.GOLDEN_SWORD)).criterion("has_golden_helmet", RecipeProvider.conditionsFromItem(Items.GOLDEN_HELMET)).criterion("has_golden_chestplate", RecipeProvider.conditionsFromItem(Items.GOLDEN_CHESTPLATE)).criterion("has_golden_leggings", RecipeProvider.conditionsFromItem(Items.GOLDEN_LEGGINGS)).criterion("has_golden_boots", RecipeProvider.conditionsFromItem(Items.GOLDEN_BOOTS)).criterion("has_golden_horse_armor", RecipeProvider.conditionsFromItem(Items.GOLDEN_HORSE_ARMOR)).offerTo(exporter, RecipeProvider.getBlastingItemPath(Items.GOLD_NUGGET));
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1f, 100).criterion("has_iron_pickaxe", RecipeProvider.conditionsFromItem(Items.IRON_PICKAXE)).criterion("has_iron_shovel", RecipeProvider.conditionsFromItem(Items.IRON_SHOVEL)).criterion("has_iron_axe", RecipeProvider.conditionsFromItem(Items.IRON_AXE)).criterion("has_iron_hoe", RecipeProvider.conditionsFromItem(Items.IRON_HOE)).criterion("has_iron_sword", RecipeProvider.conditionsFromItem(Items.IRON_SWORD)).criterion("has_iron_helmet", RecipeProvider.conditionsFromItem(Items.IRON_HELMET)).criterion("has_iron_chestplate", RecipeProvider.conditionsFromItem(Items.IRON_CHESTPLATE)).criterion("has_iron_leggings", RecipeProvider.conditionsFromItem(Items.IRON_LEGGINGS)).criterion("has_iron_boots", RecipeProvider.conditionsFromItem(Items.IRON_BOOTS)).criterion("has_iron_horse_armor", RecipeProvider.conditionsFromItem(Items.IRON_HORSE_ARMOR)).criterion("has_chainmail_helmet", RecipeProvider.conditionsFromItem(Items.CHAINMAIL_HELMET)).criterion("has_chainmail_chestplate", RecipeProvider.conditionsFromItem(Items.CHAINMAIL_CHESTPLATE)).criterion("has_chainmail_leggings", RecipeProvider.conditionsFromItem(Items.CHAINMAIL_LEGGINGS)).criterion("has_chainmail_boots", RecipeProvider.conditionsFromItem(Items.CHAINMAIL_BOOTS)).offerTo(exporter, RecipeProvider.getBlastingItemPath(Items.IRON_NUGGET));
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0f, 100).criterion("has_ancient_debris", RecipeProvider.conditionsFromItem(Blocks.ANCIENT_DEBRIS)).offerTo(exporter, RecipeProvider.getBlastingItemPath(Items.NETHERITE_SCRAP));
        RecipeProvider.generateCookingRecipes(exporter, "smoking", RecipeSerializer.SMOKING, 100);
        RecipeProvider.generateCookingRecipes(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, 600);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.STONE_SLAB, Blocks.STONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.STONE_STAIRS, Blocks.STONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.STONE_BRICKS, Blocks.STONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.STONE_BRICK_SLAB, Blocks.STONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.STONE_BRICK_STAIRS, Blocks.STONE);
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.STONE), Blocks.CHISELED_STONE_BRICKS).criterion("has_stone", RecipeProvider.conditionsFromItem(Blocks.STONE)).offerTo(exporter, "chiseled_stone_bricks_stone_from_stonecutting");
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICK_WALL).criterion("has_stone", RecipeProvider.conditionsFromItem(Blocks.STONE)).offerTo(exporter, "stone_brick_walls_from_stone_stonecutting");
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_SANDSTONE_SLAB, Blocks.SANDSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE);
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_SLAB, 2).criterion("has_quartz_block", RecipeProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).offerTo(exporter, "quartz_slab_from_stonecutting");
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.QUARTZ_STAIRS, Blocks.QUARTZ_BLOCK);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_BLOCK);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.QUARTZ_BRICKS, Blocks.QUARTZ_BLOCK);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICKS, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICKS);
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_WALL).criterion("has_stone_bricks", RecipeProvider.conditionsFromItem(Blocks.STONE_BRICKS)).offerTo(exporter, "stone_brick_wall_from_stone_bricks_stonecutting");
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CHISELED_STONE_BRICKS, Blocks.STONE_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.BRICK_SLAB, Blocks.BRICKS, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.BRICK_STAIRS, Blocks.BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.BRICK_WALL, Blocks.BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.MUD_BRICK_SLAB, Blocks.MUD_BRICKS, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.MUD_BRICK_STAIRS, Blocks.MUD_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.MUD_BRICK_WALL, Blocks.MUD_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICKS, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CHISELED_NETHER_BRICKS, Blocks.NETHER_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICKS, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.RED_NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.PURPUR_SLAB, Blocks.PURPUR_BLOCK, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.PURPUR_STAIRS, Blocks.PURPUR_BLOCK);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.PURPUR_PILLAR, Blocks.PURPUR_BLOCK);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.PRISMARINE_WALL, Blocks.PRISMARINE);
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_SLAB, 2).criterion("has_prismarine_brick", RecipeProvider.conditionsFromItem(Blocks.PRISMARINE_BRICKS)).offerTo(exporter, "prismarine_brick_slab_from_prismarine_stonecutting");
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_STAIRS).criterion("has_prismarine_brick", RecipeProvider.conditionsFromItem(Blocks.PRISMARINE_BRICKS)).offerTo(exporter, "prismarine_brick_stairs_from_prismarine_stonecutting");
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.ANDESITE_SLAB, Blocks.ANDESITE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.ANDESITE_WALL, Blocks.ANDESITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_ANDESITE, Blocks.ANDESITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_ANDESITE_SLAB, Blocks.ANDESITE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.ANDESITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BASALT, Blocks.BASALT);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.GRANITE_SLAB, Blocks.GRANITE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.GRANITE_STAIRS, Blocks.GRANITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.GRANITE_WALL, Blocks.GRANITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_GRANITE, Blocks.GRANITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_GRANITE_SLAB, Blocks.GRANITE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_GRANITE_STAIRS, Blocks.GRANITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_GRANITE_STAIRS, Blocks.POLISHED_GRANITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DIORITE_SLAB, Blocks.DIORITE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DIORITE_STAIRS, Blocks.DIORITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DIORITE_WALL, Blocks.DIORITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DIORITE, Blocks.DIORITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DIORITE_SLAB, Blocks.DIORITE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DIORITE_STAIRS, Blocks.DIORITE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DIORITE_STAIRS, Blocks.POLISHED_DIORITE);
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_SLAB, 2).criterion("has_mossy_stone_bricks", RecipeProvider.conditionsFromItem(Blocks.MOSSY_STONE_BRICKS)).offerTo(exporter, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_STAIRS).criterion("has_mossy_stone_bricks", RecipeProvider.conditionsFromItem(Blocks.MOSSY_STONE_BRICKS)).offerTo(exporter, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_WALL).criterion("has_mossy_stone_bricks", RecipeProvider.conditionsFromItem(Blocks.MOSSY_STONE_BRICKS)).offerTo(exporter, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.SMOOTH_QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ);
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_SLAB, 2).criterion("has_end_stone_brick", RecipeProvider.conditionsFromItem(Blocks.END_STONE_BRICKS)).offerTo(exporter, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_STAIRS).criterion("has_end_stone_brick", RecipeProvider.conditionsFromItem(Blocks.END_STONE_BRICKS)).offerTo(exporter, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_WALL).criterion("has_end_stone_brick", RecipeProvider.conditionsFromItem(Blocks.END_STONE_BRICKS)).offerTo(exporter, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.END_STONE_BRICKS, Blocks.END_STONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.END_STONE_BRICK_WALL, Blocks.END_STONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.BLACKSTONE_WALL, Blocks.BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.BLACKSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.BLACKSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICKS, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_COPPER, Blocks.COPPER_BLOCK, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_COPPER_STAIRS, Blocks.COPPER_BLOCK, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CUT_COPPER_SLAB, Blocks.COPPER_BLOCK, 8);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.EXPOSED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.EXPOSED_COPPER, 8);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_COPPER, 8);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.OXIDIZED_COPPER, 8);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_CUT_COPPER, Blocks.WAXED_COPPER_BLOCK, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_COPPER_BLOCK, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_COPPER_BLOCK, 8);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_COPPER, 8);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_COPPER, 8);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_COPPER, 4);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_COPPER, 8);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.COBBLED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.CHISELED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICKS, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICK_WALL, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILES, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_SLAB, Blocks.COBBLED_DEEPSLATE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_WALL, Blocks.COBBLED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.POLISHED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.POLISHED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICKS, Blocks.POLISHED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.POLISHED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICK_WALL, Blocks.POLISHED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_SLAB, Blocks.POLISHED_DEEPSLATE, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.POLISHED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_WALL, Blocks.POLISHED_DEEPSLATE);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICK_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_BRICK_WALL, Blocks.DEEPSLATE_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_BRICKS, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_BRICKS);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILES, 2);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_STAIRS, Blocks.DEEPSLATE_TILES);
        RecipeProvider.offerStonecuttingRecipe(exporter, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_TILES);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_HOE, Items.NETHERITE_HOE);
        RecipeProvider.offerNetheriteUpgradeRecipe(exporter, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
    }

    private static void offerSingleOutputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, @Nullable String group) {
        RecipeProvider.offerShapelessRecipe(exporter, output, input, group, 1);
    }

    private static void offerShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(output, outputCount).input(input).group(group).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.convertBetween(output, input));
    }

    private static void offerSmelting(Consumer<RecipeJsonProvider> exporter, List<ItemConvertible> inputs, ItemConvertible output, float experience, int cookingTime, String group) {
        RecipeProvider.offerMultipleOptions(exporter, RecipeSerializer.SMELTING, inputs, output, experience, cookingTime, group, "_from_smelting");
    }

    private static void offerBlasting(Consumer<RecipeJsonProvider> exporter, List<ItemConvertible> inputs, ItemConvertible output, float experience, int cookingTime, String group) {
        RecipeProvider.offerMultipleOptions(exporter, RecipeSerializer.BLASTING, inputs, output, experience, cookingTime, group, "_from_blasting");
    }

    private static void offerMultipleOptions(Consumer<RecipeJsonProvider> exporter, CookingRecipeSerializer<?> serializer, List<ItemConvertible> inputs, ItemConvertible output, float experience, int cookingTime, String group, String baseIdString) {
        for (ItemConvertible itemConvertible : inputs) {
            CookingRecipeJsonBuilder.create(Ingredient.ofItems(itemConvertible), output, experience, cookingTime, serializer).group(group).criterion(RecipeProvider.hasItem(itemConvertible), RecipeProvider.conditionsFromItem(itemConvertible)).offerTo(exporter, RecipeProvider.getItemPath(output) + baseIdString + "_" + RecipeProvider.getItemPath(itemConvertible));
        }
    }

    private static void offerNetheriteUpgradeRecipe(Consumer<RecipeJsonProvider> exporter, Item input, Item output) {
        SmithingRecipeJsonBuilder.create(Ingredient.ofItems(input), Ingredient.ofItems(Items.NETHERITE_INGOT), output).criterion("has_netherite_ingot", RecipeProvider.conditionsFromItem(Items.NETHERITE_INGOT)).offerTo(exporter, RecipeProvider.getItemPath(output) + "_smithing");
    }

    private static void offerPlanksRecipe2(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, TagKey<Item> input) {
        ShapelessRecipeJsonBuilder.create(output, 4).input(input).group("planks").criterion("has_log", RecipeProvider.conditionsFromTag(input)).offerTo(exporter);
    }

    private static void offerPlanksRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, TagKey<Item> input) {
        ShapelessRecipeJsonBuilder.create(output, 4).input(input).group("planks").criterion("has_logs", RecipeProvider.conditionsFromTag(input)).offerTo(exporter);
    }

    private static void offerBarkBlockRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(output, 3).input(Character.valueOf('#'), input).pattern("##").pattern("##").group("bark").criterion("has_log", RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    private static void offerBoatRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(output).input(Character.valueOf('#'), input).pattern("# #").pattern("###").group("boat").criterion("in_water", RecipeProvider.requireEnteringFluid(Blocks.WATER)).offerTo(exporter);
    }

    private static void offerChestBoatRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(output).input(Blocks.CHEST).input(input).group("chest_boat").criterion("has_boat", RecipeProvider.conditionsFromTag(ItemTags.BOATS)).offerTo(exporter);
    }

    private static CraftingRecipeJsonBuilder createTransmutationRecipe(ItemConvertible output, Ingredient input) {
        return ShapelessRecipeJsonBuilder.create(output).input(input);
    }

    private static CraftingRecipeJsonBuilder createDoorRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output, 3).input(Character.valueOf('#'), input).pattern("##").pattern("##").pattern("##");
    }

    private static CraftingRecipeJsonBuilder createFenceRecipe(ItemConvertible output, Ingredient input) {
        int i = output == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
        Item item = output == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
        return ShapedRecipeJsonBuilder.create(output, i).input(Character.valueOf('W'), input).input(Character.valueOf('#'), item).pattern("W#W").pattern("W#W");
    }

    private static CraftingRecipeJsonBuilder createFenceGateRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('W'), input).pattern("#W#").pattern("#W#");
    }

    private static void createPressurePlateRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.createPressurePlateRecipe(output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    private static CraftingRecipeJsonBuilder createPressurePlateRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output).input(Character.valueOf('#'), input).pattern("##");
    }

    private static void offerSlabRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.createSlabRecipe(output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    private static CraftingRecipeJsonBuilder createSlabRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output, 6).input(Character.valueOf('#'), input).pattern("###");
    }

    private static CraftingRecipeJsonBuilder createStairsRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output, 4).input(Character.valueOf('#'), input).pattern("#  ").pattern("## ").pattern("###");
    }

    private static CraftingRecipeJsonBuilder createTrapdoorRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output, 2).input(Character.valueOf('#'), input).pattern("###").pattern("###");
    }

    private static CraftingRecipeJsonBuilder createSignRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output, 3).group("sign").input(Character.valueOf('#'), input).input(Character.valueOf('X'), Items.STICK).pattern("###").pattern("###").pattern(" X ");
    }

    private static void offerWoolDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(output).input(input).input(Blocks.WHITE_WOOL).group("wool").criterion("has_white_wool", RecipeProvider.conditionsFromItem(Blocks.WHITE_WOOL)).offerTo(exporter);
    }

    private static void offerCarpetRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(output, 3).input(Character.valueOf('#'), input).pattern("##").group("carpet").criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    private static void offerCarpetDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(output, 8).input(Character.valueOf('#'), Blocks.WHITE_CARPET).input(Character.valueOf('$'), input).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", RecipeProvider.conditionsFromItem(Blocks.WHITE_CARPET)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.convertBetween(output, Blocks.WHITE_CARPET));
    }

    private static void offerBedRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(output).input(Character.valueOf('#'), input).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    private static void offerBedDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(output).input(Items.WHITE_BED).input(input).group("dyed_bed").criterion("has_bed", RecipeProvider.conditionsFromItem(Items.WHITE_BED)).offerTo(exporter, RecipeProvider.convertBetween(output, Items.WHITE_BED));
    }

    private static void offerBannerRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(output).input(Character.valueOf('#'), input).input(Character.valueOf('|'), Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    private static void offerStainedGlassDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(output, 8).input(Character.valueOf('#'), Blocks.GLASS).input(Character.valueOf('X'), input).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", RecipeProvider.conditionsFromItem(Blocks.GLASS)).offerTo(exporter);
    }

    private static void offerStainedGlassPaneRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(output, 16).input(Character.valueOf('#'), input).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    private static void offerStainedGlassPaneDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(output, 8).input(Character.valueOf('#'), Blocks.GLASS_PANE).input(Character.valueOf('$'), input).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", RecipeProvider.conditionsFromItem(Blocks.GLASS_PANE)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.convertBetween(output, Blocks.GLASS_PANE));
    }

    private static void offerTerracottaDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapedRecipeJsonBuilder.create(output, 8).input(Character.valueOf('#'), Blocks.TERRACOTTA).input(Character.valueOf('X'), input).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", RecipeProvider.conditionsFromItem(Blocks.TERRACOTTA)).offerTo(exporter);
    }

    private static void offerConcretePowderDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(output, 8).input(input).input(Blocks.SAND, 4).input(Blocks.GRAVEL, 4).group("concrete_powder").criterion("has_sand", RecipeProvider.conditionsFromItem(Blocks.SAND)).criterion("has_gravel", RecipeProvider.conditionsFromItem(Blocks.GRAVEL)).offerTo(exporter);
    }

    public static void offerCandleDyeingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(output).input(Blocks.CANDLE).input(input).group("dyed_candle").criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static void offerWallRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.getWallRecipe(output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static CraftingRecipeJsonBuilder getWallRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output, 6).input(Character.valueOf('#'), input).pattern("###").pattern("###");
    }

    public static void offerPolishedStoneRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.createCondensingRecipe(output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static CraftingRecipeJsonBuilder createCondensingRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output, 4).input(Character.valueOf('S'), input).pattern("SS").pattern("SS");
    }

    public static void offerCutCopperRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.createCutCopperRecipe(output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static ShapedRecipeJsonBuilder createCutCopperRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output, 4).input(Character.valueOf('#'), input).pattern("##").pattern("##");
    }

    public static void offerChiseledBlockRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.createChiseledBlockRecipe(output, Ingredient.ofItems(input)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    public static ShapedRecipeJsonBuilder createChiseledBlockRecipe(ItemConvertible output, Ingredient input) {
        return ShapedRecipeJsonBuilder.create(output).input(Character.valueOf('#'), input).pattern("#").pattern("#");
    }

    private static void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        RecipeProvider.offerStonecuttingRecipe(exporter, output, input, 1);
    }

    private static void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, int count) {
        SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), output, count).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.convertBetween(output, input) + "_stonecutting");
    }

    /**
     * Offers a smelting recipe to the exporter that is used to convert the main block of a block family to its cracked variant.
     */
    private static void offerCrackingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(input), output, 0.1f, 200).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter);
    }

    /**
     * Offers two recipes to convert between a normal and compacted form of an item.
     * 
     * <p>The shaped recipe converts 9 items in a square to a compacted form of the item.
     * <p>The shapeless recipe converts the compacted form to 9 of the normal form.
     * 
     * @param compacted compacted output item, e.g. block of copper
     * @param input input item used to craft compacted item, e.g. copper ingot
     */
    private static void offerReversibleCompactingRecipes(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, ItemConvertible compacted) {
        RecipeProvider.offerReversibleCompactingRecipes(exporter, input, compacted, RecipeProvider.getRecipeName(compacted), null, RecipeProvider.getRecipeName(input), null);
    }

    private static void offerReversibleCompactingRecipesWithCompactingRecipeGroup(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, ItemConvertible compacted, String compactingRecipeName, String compactingRecipeGroup) {
        RecipeProvider.offerReversibleCompactingRecipes(exporter, input, compacted, compactingRecipeName, compactingRecipeGroup, RecipeProvider.getRecipeName(input), null);
    }

    private static void offerReversibleCompactingRecipesWithReverseRecipeGroup(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, ItemConvertible compacted, String reverseRecipeName, String reverseRecipeGroup) {
        RecipeProvider.offerReversibleCompactingRecipes(exporter, input, compacted, RecipeProvider.getRecipeName(compacted), null, reverseRecipeName, reverseRecipeGroup);
    }

    private static void offerReversibleCompactingRecipes(Consumer<RecipeJsonProvider> exporter, ItemConvertible input, ItemConvertible compacted, String compactingRecipeName, @Nullable String compactingRecipeGroup, String reverseRecipeName, @Nullable String reverseRecipeGroup) {
        ShapelessRecipeJsonBuilder.create(input, 9).input(compacted).group(reverseRecipeGroup).criterion(RecipeProvider.hasItem(compacted), RecipeProvider.conditionsFromItem(compacted)).offerTo(exporter, new Identifier(reverseRecipeName));
        ShapedRecipeJsonBuilder.create(compacted).input(Character.valueOf('#'), input).pattern("###").pattern("###").pattern("###").group(compactingRecipeGroup).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, new Identifier(compactingRecipeName));
    }

    private static void generateCookingRecipes(Consumer<RecipeJsonProvider> exporter, String cooker, CookingRecipeSerializer<?> serializer, int cookingTime) {
        RecipeProvider.offerCookingRecipe(exporter, cooker, serializer, cookingTime, Items.BEEF, Items.COOKED_BEEF, 0.35f);
        RecipeProvider.offerCookingRecipe(exporter, cooker, serializer, cookingTime, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35f);
        RecipeProvider.offerCookingRecipe(exporter, cooker, serializer, cookingTime, Items.COD, Items.COOKED_COD, 0.35f);
        RecipeProvider.offerCookingRecipe(exporter, cooker, serializer, cookingTime, Items.KELP, Items.DRIED_KELP, 0.1f);
        RecipeProvider.offerCookingRecipe(exporter, cooker, serializer, cookingTime, Items.SALMON, Items.COOKED_SALMON, 0.35f);
        RecipeProvider.offerCookingRecipe(exporter, cooker, serializer, cookingTime, Items.MUTTON, Items.COOKED_MUTTON, 0.35f);
        RecipeProvider.offerCookingRecipe(exporter, cooker, serializer, cookingTime, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35f);
        RecipeProvider.offerCookingRecipe(exporter, cooker, serializer, cookingTime, Items.POTATO, Items.BAKED_POTATO, 0.35f);
        RecipeProvider.offerCookingRecipe(exporter, cooker, serializer, cookingTime, Items.RABBIT, Items.COOKED_RABBIT, 0.35f);
    }

    private static void offerCookingRecipe(Consumer<RecipeJsonProvider> exporter, String cooker, CookingRecipeSerializer<?> serializer, int cookingTime, ItemConvertible input, ItemConvertible output, float experience) {
        CookingRecipeJsonBuilder.create(Ingredient.ofItems(input), output, experience, cookingTime, serializer).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.getItemPath(output) + "_from_" + cooker);
    }

    private static void offerWaxingRecipes(Consumer<RecipeJsonProvider> exporter) {
        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().forEach((input, output) -> ShapelessRecipeJsonBuilder.create(output).input((ItemConvertible)input).input(Items.HONEYCOMB).group(RecipeProvider.getItemPath(output)).criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input)).offerTo(exporter, RecipeProvider.convertBetween(output, Items.HONEYCOMB)));
    }

    private static void generateFamily(Consumer<RecipeJsonProvider> exporter, BlockFamily family) {
        family.getVariants().forEach((variant, block) -> {
            BiFunction<ItemConvertible, ItemConvertible, CraftingRecipeJsonBuilder> biFunction = VARIANT_FACTORIES.get(variant);
            Block itemConvertible = RecipeProvider.getVariantRecipeInput(family, variant);
            if (biFunction != null) {
                CraftingRecipeJsonBuilder craftingRecipeJsonBuilder = biFunction.apply((ItemConvertible)block, itemConvertible);
                family.getGroup().ifPresent(group -> craftingRecipeJsonBuilder.group(group + (String)(variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getName())));
                craftingRecipeJsonBuilder.criterion(family.getUnlockCriterionName().orElseGet(() -> RecipeProvider.hasItem(itemConvertible)), RecipeProvider.conditionsFromItem(itemConvertible));
                craftingRecipeJsonBuilder.offerTo(exporter);
            }
            if (variant == BlockFamily.Variant.CRACKED) {
                RecipeProvider.offerCrackingRecipe(exporter, block, itemConvertible);
            }
        });
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
            if (!family.getVariants().containsKey((Object)BlockFamily.Variant.SLAB)) {
                throw new IllegalStateException("Slab is not defined for the family.");
            }
            return family.getVariant(BlockFamily.Variant.SLAB);
        }
        return family.getBaseBlock();
    }

    private static EnterBlockCriterion.Conditions requireEnteringFluid(Block block) {
        return new EnterBlockCriterion.Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItem(NumberRange.IntRange count, ItemConvertible item) {
        return RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(item).count(count).build());
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
        return RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().items(item).build());
    }

    private static InventoryChangedCriterion.Conditions conditionsFromTag(TagKey<Item> tag) {
        return RecipeProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().tag(tag).build());
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate ... predicates) {
        return new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, predicates);
    }

    private static String hasItem(ItemConvertible item) {
        return "has_" + RecipeProvider.getItemPath(item);
    }

    private static String getItemPath(ItemConvertible item) {
        return Registry.ITEM.getId(item.asItem()).getPath();
    }

    private static String getRecipeName(ItemConvertible item) {
        return RecipeProvider.getItemPath(item);
    }

    private static String convertBetween(ItemConvertible to, ItemConvertible from) {
        return RecipeProvider.getItemPath(to) + "_from_" + RecipeProvider.getItemPath(from);
    }

    private static String getSmeltingItemPath(ItemConvertible item) {
        return RecipeProvider.getItemPath(item) + "_from_smelting";
    }

    private static String getBlastingItemPath(ItemConvertible item) {
        return RecipeProvider.getItemPath(item) + "_from_blasting";
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}

