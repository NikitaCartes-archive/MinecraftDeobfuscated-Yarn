/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.recipe.ComplexRecipeJsonFactory;
import net.minecraft.data.server.recipe.CookingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonFactory;
import net.minecraft.data.server.recipe.SmithingRecipeJsonFactory;
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
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipesProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator root;

    public RecipesProvider(DataGenerator root) {
        this.root = root;
    }

    @Override
    public void run(DataCache cache) {
        Path path = this.root.getOutput();
        HashSet set = Sets.newHashSet();
        RecipesProvider.generate(recipeJsonProvider -> {
            if (!set.add(recipeJsonProvider.getRecipeId())) {
                throw new IllegalStateException("Duplicate recipe " + recipeJsonProvider.getRecipeId());
            }
            RecipesProvider.saveRecipe(cache, recipeJsonProvider.toJson(), path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/recipes/" + recipeJsonProvider.getRecipeId().getPath() + ".json"));
            JsonObject jsonObject = recipeJsonProvider.toAdvancementJson();
            if (jsonObject != null) {
                RecipesProvider.saveRecipeAdvancement(cache, jsonObject, path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/advancements/" + recipeJsonProvider.getAdvancementId().getPath() + ".json"));
            }
        });
        RecipesProvider.saveRecipeAdvancement(cache, Advancement.Task.create().criterion("impossible", new ImpossibleCriterion.Conditions()).toJson(), path.resolve("data/minecraft/advancements/recipes/root.json"));
    }

    private static void saveRecipe(DataCache cache, JsonObject json, Path path) {
        try {
            String string = GSON.toJson(json);
            String string2 = SHA1.hashUnencodedChars(string).toString();
            if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                    bufferedWriter.write(string);
                }
            }
            cache.updateSha1(path, string2);
        } catch (IOException iOException) {
            LOGGER.error("Couldn't save recipe {}", (Object)path, (Object)iOException);
        }
    }

    private static void saveRecipeAdvancement(DataCache cache, JsonObject json, Path path) {
        try {
            String string = GSON.toJson(json);
            String string2 = SHA1.hashUnencodedChars(string).toString();
            if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                    bufferedWriter.write(string);
                }
            }
            cache.updateSha1(path, string2);
        } catch (IOException iOException) {
            LOGGER.error("Couldn't save recipe advancement {}", (Object)path, (Object)iOException);
        }
    }

    private static void generate(Consumer<RecipeJsonProvider> consumer) {
        RecipesProvider.offerPlanksRecipe2(consumer, Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS);
        RecipesProvider.offerPlanksRecipe(consumer, Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS);
        RecipesProvider.offerPlanksRecipe(consumer, Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS);
        RecipesProvider.offerPlanksRecipe2(consumer, Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS);
        RecipesProvider.offerPlanksRecipe(consumer, Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS);
        RecipesProvider.offerPlanksRecipe(consumer, Blocks.OAK_PLANKS, ItemTags.OAK_LOGS);
        RecipesProvider.offerPlanksRecipe(consumer, Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS);
        RecipesProvider.offerPlanksRecipe(consumer, Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.OAK_WOOD, Blocks.OAK_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
        RecipesProvider.offerBarkBlockRecipe(consumer, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
        RecipesProvider.offerBoatRecipe(consumer, Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
        RecipesProvider.offerBoatRecipe(consumer, Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
        RecipesProvider.offerBoatRecipe(consumer, Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
        RecipesProvider.offerBoatRecipe(consumer, Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
        RecipesProvider.offerBoatRecipe(consumer, Items.OAK_BOAT, Blocks.OAK_PLANKS);
        RecipesProvider.offerBoatRecipe(consumer, Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
        RecipesProvider.offerButtonRecipe(consumer, Blocks.ACACIA_BUTTON, Blocks.ACACIA_PLANKS);
        RecipesProvider.offerDoorRecipe(consumer, Blocks.ACACIA_DOOR, Blocks.ACACIA_PLANKS);
        RecipesProvider.offerFenceRecipe(consumer, Blocks.ACACIA_FENCE, Blocks.ACACIA_PLANKS);
        RecipesProvider.offerFenceGateRecipe(consumer, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_PLANKS);
        RecipesProvider.offerWoodenPressurePlateRecipe(consumer, Blocks.ACACIA_PRESSURE_PLATE, Blocks.ACACIA_PLANKS);
        RecipesProvider.offerWoodenSlabRecipe(consumer, Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS);
        RecipesProvider.offerWoodenStairsRecipe(consumer, Blocks.ACACIA_STAIRS, Blocks.ACACIA_PLANKS);
        RecipesProvider.offerTrapdoorRecipe(consumer, Blocks.ACACIA_TRAPDOOR, Blocks.ACACIA_PLANKS);
        RecipesProvider.offerSignRecipe(consumer, Blocks.ACACIA_SIGN, Blocks.ACACIA_PLANKS);
        RecipesProvider.offerButtonRecipe(consumer, Blocks.BIRCH_BUTTON, Blocks.BIRCH_PLANKS);
        RecipesProvider.offerDoorRecipe(consumer, Blocks.BIRCH_DOOR, Blocks.BIRCH_PLANKS);
        RecipesProvider.offerFenceRecipe(consumer, Blocks.BIRCH_FENCE, Blocks.BIRCH_PLANKS);
        RecipesProvider.offerFenceGateRecipe(consumer, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_PLANKS);
        RecipesProvider.offerWoodenPressurePlateRecipe(consumer, Blocks.BIRCH_PRESSURE_PLATE, Blocks.BIRCH_PLANKS);
        RecipesProvider.offerWoodenSlabRecipe(consumer, Blocks.BIRCH_SLAB, Blocks.BIRCH_PLANKS);
        RecipesProvider.offerWoodenStairsRecipe(consumer, Blocks.BIRCH_STAIRS, Blocks.BIRCH_PLANKS);
        RecipesProvider.offerTrapdoorRecipe(consumer, Blocks.BIRCH_TRAPDOOR, Blocks.BIRCH_PLANKS);
        RecipesProvider.offerSignRecipe(consumer, Blocks.BIRCH_SIGN, Blocks.BIRCH_PLANKS);
        RecipesProvider.offerButtonRecipe(consumer, Blocks.CRIMSON_BUTTON, Blocks.CRIMSON_PLANKS);
        RecipesProvider.offerDoorRecipe(consumer, Blocks.CRIMSON_DOOR, Blocks.CRIMSON_PLANKS);
        RecipesProvider.offerFenceRecipe(consumer, Blocks.CRIMSON_FENCE, Blocks.CRIMSON_PLANKS);
        RecipesProvider.offerFenceGateRecipe(consumer, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_PLANKS);
        RecipesProvider.offerWoodenPressurePlateRecipe(consumer, Blocks.CRIMSON_PRESSURE_PLATE, Blocks.CRIMSON_PLANKS);
        RecipesProvider.offerWoodenSlabRecipe(consumer, Blocks.CRIMSON_SLAB, Blocks.CRIMSON_PLANKS);
        RecipesProvider.offerWoodenStairsRecipe(consumer, Blocks.CRIMSON_STAIRS, Blocks.CRIMSON_PLANKS);
        RecipesProvider.offerTrapdoorRecipe(consumer, Blocks.CRIMSON_TRAPDOOR, Blocks.CRIMSON_PLANKS);
        RecipesProvider.offerSignRecipe(consumer, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_PLANKS);
        RecipesProvider.offerButtonRecipe(consumer, Blocks.DARK_OAK_BUTTON, Blocks.DARK_OAK_PLANKS);
        RecipesProvider.offerDoorRecipe(consumer, Blocks.DARK_OAK_DOOR, Blocks.DARK_OAK_PLANKS);
        RecipesProvider.offerFenceRecipe(consumer, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_PLANKS);
        RecipesProvider.offerFenceGateRecipe(consumer, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_PLANKS);
        RecipesProvider.offerWoodenPressurePlateRecipe(consumer, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.DARK_OAK_PLANKS);
        RecipesProvider.offerWoodenSlabRecipe(consumer, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
        RecipesProvider.offerWoodenStairsRecipe(consumer, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_PLANKS);
        RecipesProvider.offerTrapdoorRecipe(consumer, Blocks.DARK_OAK_TRAPDOOR, Blocks.DARK_OAK_PLANKS);
        RecipesProvider.offerSignRecipe(consumer, Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_PLANKS);
        RecipesProvider.offerButtonRecipe(consumer, Blocks.JUNGLE_BUTTON, Blocks.JUNGLE_PLANKS);
        RecipesProvider.offerDoorRecipe(consumer, Blocks.JUNGLE_DOOR, Blocks.JUNGLE_PLANKS);
        RecipesProvider.offerFenceRecipe(consumer, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_PLANKS);
        RecipesProvider.offerFenceGateRecipe(consumer, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_PLANKS);
        RecipesProvider.offerWoodenPressurePlateRecipe(consumer, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.JUNGLE_PLANKS);
        RecipesProvider.offerWoodenSlabRecipe(consumer, Blocks.JUNGLE_SLAB, Blocks.JUNGLE_PLANKS);
        RecipesProvider.offerWoodenStairsRecipe(consumer, Blocks.JUNGLE_STAIRS, Blocks.JUNGLE_PLANKS);
        RecipesProvider.offerTrapdoorRecipe(consumer, Blocks.JUNGLE_TRAPDOOR, Blocks.JUNGLE_PLANKS);
        RecipesProvider.offerSignRecipe(consumer, Blocks.JUNGLE_SIGN, Blocks.JUNGLE_PLANKS);
        RecipesProvider.offerButtonRecipe(consumer, Blocks.OAK_BUTTON, Blocks.OAK_PLANKS);
        RecipesProvider.offerDoorRecipe(consumer, Blocks.OAK_DOOR, Blocks.OAK_PLANKS);
        RecipesProvider.offerFenceRecipe(consumer, Blocks.OAK_FENCE, Blocks.OAK_PLANKS);
        RecipesProvider.offerFenceGateRecipe(consumer, Blocks.OAK_FENCE_GATE, Blocks.OAK_PLANKS);
        RecipesProvider.offerWoodenPressurePlateRecipe(consumer, Blocks.OAK_PRESSURE_PLATE, Blocks.OAK_PLANKS);
        RecipesProvider.offerWoodenSlabRecipe(consumer, Blocks.OAK_SLAB, Blocks.OAK_PLANKS);
        RecipesProvider.offerWoodenStairsRecipe(consumer, Blocks.OAK_STAIRS, Blocks.OAK_PLANKS);
        RecipesProvider.offerTrapdoorRecipe(consumer, Blocks.OAK_TRAPDOOR, Blocks.OAK_PLANKS);
        RecipesProvider.offerSignRecipe(consumer, Blocks.OAK_SIGN, Blocks.OAK_PLANKS);
        RecipesProvider.offerButtonRecipe(consumer, Blocks.SPRUCE_BUTTON, Blocks.SPRUCE_PLANKS);
        RecipesProvider.offerDoorRecipe(consumer, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_PLANKS);
        RecipesProvider.offerFenceRecipe(consumer, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_PLANKS);
        RecipesProvider.offerFenceGateRecipe(consumer, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_PLANKS);
        RecipesProvider.offerWoodenPressurePlateRecipe(consumer, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.SPRUCE_PLANKS);
        RecipesProvider.offerWoodenSlabRecipe(consumer, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
        RecipesProvider.offerWoodenStairsRecipe(consumer, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_PLANKS);
        RecipesProvider.offerTrapdoorRecipe(consumer, Blocks.SPRUCE_TRAPDOOR, Blocks.SPRUCE_PLANKS);
        RecipesProvider.offerSignRecipe(consumer, Blocks.SPRUCE_SIGN, Blocks.SPRUCE_PLANKS);
        RecipesProvider.offerButtonRecipe(consumer, Blocks.WARPED_BUTTON, Blocks.WARPED_PLANKS);
        RecipesProvider.offerDoorRecipe(consumer, Blocks.WARPED_DOOR, Blocks.WARPED_PLANKS);
        RecipesProvider.offerFenceRecipe(consumer, Blocks.WARPED_FENCE, Blocks.WARPED_PLANKS);
        RecipesProvider.offerFenceGateRecipe(consumer, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_PLANKS);
        RecipesProvider.offerWoodenPressurePlateRecipe(consumer, Blocks.WARPED_PRESSURE_PLATE, Blocks.WARPED_PLANKS);
        RecipesProvider.offerWoodenSlabRecipe(consumer, Blocks.WARPED_SLAB, Blocks.WARPED_PLANKS);
        RecipesProvider.offerWoodenStairsRecipe(consumer, Blocks.WARPED_STAIRS, Blocks.WARPED_PLANKS);
        RecipesProvider.offerTrapdoorRecipe(consumer, Blocks.WARPED_TRAPDOOR, Blocks.WARPED_PLANKS);
        RecipesProvider.offerSignRecipe(consumer, Blocks.WARPED_SIGN, Blocks.WARPED_PLANKS);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.BLACK_WOOL, Items.BLACK_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.BLACK_CARPET, Items.BLACK_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.BLACK_BED, Blocks.BLACK_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.BLACK_BED, Items.BLACK_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.BLACK_BANNER, Blocks.BLACK_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.BLUE_WOOL, Items.BLUE_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.BLUE_CARPET, Items.BLUE_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.BLUE_BED, Blocks.BLUE_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.BLUE_BED, Items.BLUE_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.BLUE_BANNER, Blocks.BLUE_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.BROWN_WOOL, Items.BROWN_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.BROWN_CARPET, Items.BROWN_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.BROWN_BED, Blocks.BROWN_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.BROWN_BED, Items.BROWN_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.BROWN_BANNER, Blocks.BROWN_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.CYAN_WOOL, Items.CYAN_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.CYAN_CARPET, Items.CYAN_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.CYAN_BED, Blocks.CYAN_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.CYAN_BED, Items.CYAN_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.CYAN_BANNER, Blocks.CYAN_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.GRAY_WOOL, Items.GRAY_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.GRAY_CARPET, Items.GRAY_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.GRAY_BED, Blocks.GRAY_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.GRAY_BED, Items.GRAY_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.GRAY_BANNER, Blocks.GRAY_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.GREEN_WOOL, Items.GREEN_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.GREEN_CARPET, Items.GREEN_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.GREEN_BED, Blocks.GREEN_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.GREEN_BED, Items.GREEN_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.GREEN_BANNER, Blocks.GREEN_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.LIGHT_BLUE_CARPET, Items.LIGHT_BLUE_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.LIGHT_BLUE_BED, Items.LIGHT_BLUE_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.LIGHT_GRAY_CARPET, Items.LIGHT_GRAY_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.LIGHT_GRAY_BED, Items.LIGHT_GRAY_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.LIME_WOOL, Items.LIME_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.LIME_CARPET, Blocks.LIME_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.LIME_CARPET, Items.LIME_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.LIME_BED, Blocks.LIME_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.LIME_BED, Items.LIME_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.LIME_BANNER, Blocks.LIME_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.MAGENTA_WOOL, Items.MAGENTA_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.MAGENTA_CARPET, Items.MAGENTA_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.MAGENTA_BED, Items.MAGENTA_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.ORANGE_WOOL, Items.ORANGE_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.ORANGE_CARPET, Items.ORANGE_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.ORANGE_BED, Blocks.ORANGE_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.ORANGE_BED, Items.ORANGE_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.PINK_WOOL, Items.PINK_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.PINK_CARPET, Blocks.PINK_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.PINK_CARPET, Items.PINK_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.PINK_BED, Blocks.PINK_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.PINK_BED, Items.PINK_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.PINK_BANNER, Blocks.PINK_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.PURPLE_WOOL, Items.PURPLE_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.PURPLE_CARPET, Items.PURPLE_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.PURPLE_BED, Blocks.PURPLE_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.PURPLE_BED, Items.PURPLE_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.RED_WOOL, Items.RED_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.RED_CARPET, Blocks.RED_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.RED_CARPET, Items.RED_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.RED_BED, Blocks.RED_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.RED_BED, Items.RED_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.RED_BANNER, Blocks.RED_WOOL);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
        RecipesProvider.offerBedRecipe(consumer, Items.WHITE_BED, Blocks.WHITE_WOOL);
        RecipesProvider.offerBannerRecipe(consumer, Items.WHITE_BANNER, Blocks.WHITE_WOOL);
        RecipesProvider.offerWoolDyeingRecipe(consumer, Blocks.YELLOW_WOOL, Items.YELLOW_DYE);
        RecipesProvider.offerCarpetRecipe(consumer, Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
        RecipesProvider.offerCarpetDyeingRecipe(consumer, Blocks.YELLOW_CARPET, Items.YELLOW_DYE);
        RecipesProvider.offerBedRecipe(consumer, Items.YELLOW_BED, Blocks.YELLOW_WOOL);
        RecipesProvider.offerBedDyeingRecipe(consumer, Items.YELLOW_BED, Items.YELLOW_DYE);
        RecipesProvider.offerBannerRecipe(consumer, Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.BLACK_STAINED_GLASS, Items.BLACK_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.BLACK_STAINED_GLASS_PANE, Items.BLACK_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.BLUE_STAINED_GLASS, Items.BLUE_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.BLUE_STAINED_GLASS_PANE, Items.BLUE_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.BROWN_STAINED_GLASS, Items.BROWN_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.BROWN_STAINED_GLASS_PANE, Items.BROWN_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.CYAN_STAINED_GLASS, Items.CYAN_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.CYAN_STAINED_GLASS_PANE, Items.CYAN_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.GRAY_STAINED_GLASS, Items.GRAY_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.GRAY_STAINED_GLASS_PANE, Items.GRAY_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.GREEN_STAINED_GLASS, Items.GREEN_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.GREEN_STAINED_GLASS_PANE, Items.GREEN_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS, Items.LIGHT_BLUE_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Items.LIGHT_BLUE_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS, Items.LIGHT_GRAY_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.LIME_STAINED_GLASS, Items.LIME_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.LIME_STAINED_GLASS_PANE, Items.LIME_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.MAGENTA_STAINED_GLASS, Items.MAGENTA_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.MAGENTA_STAINED_GLASS_PANE, Items.MAGENTA_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.ORANGE_STAINED_GLASS, Items.ORANGE_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.ORANGE_STAINED_GLASS_PANE, Items.ORANGE_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.PINK_STAINED_GLASS, Items.PINK_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.PINK_STAINED_GLASS_PANE, Items.PINK_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.PURPLE_STAINED_GLASS, Items.PURPLE_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.PURPLE_STAINED_GLASS_PANE, Items.PURPLE_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.RED_STAINED_GLASS, Items.RED_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.RED_STAINED_GLASS_PANE, Items.RED_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.WHITE_STAINED_GLASS, Items.WHITE_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.WHITE_STAINED_GLASS_PANE, Items.WHITE_DYE);
        RecipesProvider.offerStainedGlassDyeingRecipe(consumer, Blocks.YELLOW_STAINED_GLASS, Items.YELLOW_DYE);
        RecipesProvider.offerStainedGlassPaneRecipe(consumer, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS);
        RecipesProvider.offerStainedGlassPaneDyeingRecipe(consumer, Blocks.YELLOW_STAINED_GLASS_PANE, Items.YELLOW_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.BLACK_TERRACOTTA, Items.BLACK_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.BLUE_TERRACOTTA, Items.BLUE_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.BROWN_TERRACOTTA, Items.BROWN_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.CYAN_TERRACOTTA, Items.CYAN_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.GRAY_TERRACOTTA, Items.GRAY_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.GREEN_TERRACOTTA, Items.GREEN_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.LIME_TERRACOTTA, Items.LIME_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.MAGENTA_TERRACOTTA, Items.MAGENTA_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.ORANGE_TERRACOTTA, Items.ORANGE_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.PINK_TERRACOTTA, Items.PINK_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.PURPLE_TERRACOTTA, Items.PURPLE_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.RED_TERRACOTTA, Items.RED_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.WHITE_TERRACOTTA, Items.WHITE_DYE);
        RecipesProvider.offerTerracottaDyeingRecipe(consumer, Blocks.YELLOW_TERRACOTTA, Items.YELLOW_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.BLACK_CONCRETE_POWDER, Items.BLACK_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.BLUE_CONCRETE_POWDER, Items.BLUE_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.BROWN_CONCRETE_POWDER, Items.BROWN_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.RED_CONCRETE_POWDER, Items.RED_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.WHITE_CONCRETE_POWDER, Items.WHITE_DYE);
        RecipesProvider.offerConcretePowderDyeingRecipe(consumer, Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.BLACK_CANDLE, Items.BLACK_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.BLUE_CANDLE, Items.BLUE_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.BROWN_CANDLE, Items.BROWN_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.CYAN_CANDLE, Items.CYAN_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.GRAY_CANDLE, Items.GRAY_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.GREEN_CANDLE, Items.GREEN_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.LIGHT_BLUE_CANDLE, Items.LIGHT_BLUE_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.LIGHT_GRAY_CANDLE, Items.LIGHT_GRAY_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.LIME_CANDLE, Items.LIME_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.MAGENTA_CANDLE, Items.MAGENTA_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.ORANGE_CANDLE, Items.ORANGE_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.PINK_CANDLE, Items.PINK_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.PURPLE_CANDLE, Items.PURPLE_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.RED_CANDLE, Items.RED_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.WHITE_CANDLE, Items.WHITE_DYE);
        RecipesProvider.offerCandleDyeingRecipe(consumer, Blocks.YELLOW_CANDLE, Items.YELLOW_DYE);
        ShapedRecipeJsonFactory.create(Blocks.ACTIVATOR_RAIL, 6).input(Character.valueOf('#'), Blocks.REDSTONE_TORCH).input(Character.valueOf('S'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XSX").pattern("X#X").pattern("XSX").criterion("has_rail", RecipesProvider.conditionsFromItem(Blocks.RAIL)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.ANDESITE, 2).input(Blocks.DIORITE).input(Blocks.COBBLESTONE).criterion("has_stone", RecipesProvider.conditionsFromItem(Blocks.DIORITE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ANVIL).input(Character.valueOf('I'), Blocks.IRON_BLOCK).input(Character.valueOf('i'), Items.IRON_INGOT).pattern("III").pattern(" i ").pattern("iii").criterion("has_iron_block", RecipesProvider.conditionsFromItem(Blocks.IRON_BLOCK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ARMOR_STAND).input(Character.valueOf('/'), Items.STICK).input(Character.valueOf('_'), Blocks.SMOOTH_STONE_SLAB).pattern("///").pattern(" / ").pattern("/_/").criterion("has_stone_slab", RecipesProvider.conditionsFromItem(Blocks.SMOOTH_STONE_SLAB)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ARROW, 4).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.FLINT).input(Character.valueOf('Y'), Items.FEATHER).pattern("X").pattern("#").pattern("Y").criterion("has_feather", RecipesProvider.conditionsFromItem(Items.FEATHER)).criterion("has_flint", RecipesProvider.conditionsFromItem(Items.FLINT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.BARREL, 1).input(Character.valueOf('P'), ItemTags.PLANKS).input(Character.valueOf('S'), ItemTags.WOODEN_SLABS).pattern("PSP").pattern("P P").pattern("PSP").criterion("has_planks", RecipesProvider.conditionsFromTag(ItemTags.PLANKS)).criterion("has_wood_slab", RecipesProvider.conditionsFromTag(ItemTags.WOODEN_SLABS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.BEACON).input(Character.valueOf('S'), Items.NETHER_STAR).input(Character.valueOf('G'), Blocks.GLASS).input(Character.valueOf('O'), Blocks.OBSIDIAN).pattern("GGG").pattern("GSG").pattern("OOO").criterion("has_nether_star", RecipesProvider.conditionsFromItem(Items.NETHER_STAR)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.BEEHIVE).input(Character.valueOf('P'), ItemTags.PLANKS).input(Character.valueOf('H'), Items.HONEYCOMB).pattern("PPP").pattern("HHH").pattern("PPP").criterion("has_honeycomb", RecipesProvider.conditionsFromItem(Items.HONEYCOMB)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.BEETROOT_SOUP).input(Items.BOWL).input(Items.BEETROOT, 6).criterion("has_beetroot", RecipesProvider.conditionsFromItem(Items.BEETROOT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.BLACK_DYE).input(Items.INK_SAC).group("black_dye").criterion("has_ink_sac", RecipesProvider.conditionsFromItem(Items.INK_SAC)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.BLACK_DYE).input(Blocks.WITHER_ROSE).group("black_dye").criterion("has_black_flower", RecipesProvider.conditionsFromItem(Blocks.WITHER_ROSE)).offerTo(consumer, "black_dye_from_wither_rose");
        ShapelessRecipeJsonFactory.create(Items.BLAZE_POWDER, 2).input(Items.BLAZE_ROD).criterion("has_blaze_rod", RecipesProvider.conditionsFromItem(Items.BLAZE_ROD)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.BLUE_DYE).input(Items.LAPIS_LAZULI).group("blue_dye").criterion("has_lapis_lazuli", RecipesProvider.conditionsFromItem(Items.LAPIS_LAZULI)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.BLUE_DYE).input(Blocks.CORNFLOWER).group("blue_dye").criterion("has_blue_flower", RecipesProvider.conditionsFromItem(Blocks.CORNFLOWER)).offerTo(consumer, "blue_dye_from_cornflower");
        ShapedRecipeJsonFactory.create(Blocks.BLUE_ICE).input(Character.valueOf('#'), Blocks.PACKED_ICE).pattern("###").pattern("###").pattern("###").criterion("has_packed_ice", RecipesProvider.conditionsFromItem(Blocks.PACKED_ICE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.BONE_BLOCK).input(Character.valueOf('X'), Items.BONE_MEAL).pattern("XXX").pattern("XXX").pattern("XXX").criterion("has_bonemeal", RecipesProvider.conditionsFromItem(Items.BONE_MEAL)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.BONE_MEAL, 3).input(Items.BONE).group("bonemeal").criterion("has_bone", RecipesProvider.conditionsFromItem(Items.BONE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.BONE_MEAL, 9).input(Blocks.BONE_BLOCK).group("bonemeal").criterion("has_bone_block", RecipesProvider.conditionsFromItem(Blocks.BONE_BLOCK)).offerTo(consumer, "bone_meal_from_bone_block");
        ShapelessRecipeJsonFactory.create(Items.BOOK).input(Items.PAPER, 3).input(Items.LEATHER).criterion("has_paper", RecipesProvider.conditionsFromItem(Items.PAPER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.BOOKSHELF).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('X'), Items.BOOK).pattern("###").pattern("XXX").pattern("###").criterion("has_book", RecipesProvider.conditionsFromItem(Items.BOOK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.BOW).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.STRING).pattern(" #X").pattern("# X").pattern(" #X").criterion("has_string", RecipesProvider.conditionsFromItem(Items.STRING)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.BOWL, 4).input(Character.valueOf('#'), ItemTags.PLANKS).pattern("# #").pattern(" # ").criterion("has_brown_mushroom", RecipesProvider.conditionsFromItem(Blocks.BROWN_MUSHROOM)).criterion("has_red_mushroom", RecipesProvider.conditionsFromItem(Blocks.RED_MUSHROOM)).criterion("has_mushroom_stew", RecipesProvider.conditionsFromItem(Items.MUSHROOM_STEW)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.BREAD).input(Character.valueOf('#'), Items.WHEAT).pattern("###").criterion("has_wheat", RecipesProvider.conditionsFromItem(Items.WHEAT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.BREWING_STAND).input(Character.valueOf('B'), Items.BLAZE_ROD).input(Character.valueOf('#'), ItemTags.STONE_CRAFTING_MATERIALS).pattern(" B ").pattern("###").criterion("has_blaze_rod", RecipesProvider.conditionsFromItem(Items.BLAZE_ROD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.BRICKS).input(Character.valueOf('#'), Items.BRICK).pattern("##").pattern("##").criterion("has_brick", RecipesProvider.conditionsFromItem(Items.BRICK)).offerTo(consumer);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.BRICK_SLAB, Blocks.BRICKS);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.BRICK_STAIRS, Blocks.BRICKS);
        ShapelessRecipeJsonFactory.create(Items.BROWN_DYE).input(Items.COCOA_BEANS).group("brown_dye").criterion("has_cocoa_beans", RecipesProvider.conditionsFromItem(Items.COCOA_BEANS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.BUCKET).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("# #").pattern(" # ").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.CAKE).input(Character.valueOf('A'), Items.MILK_BUCKET).input(Character.valueOf('B'), Items.SUGAR).input(Character.valueOf('C'), Items.WHEAT).input(Character.valueOf('E'), Items.EGG).pattern("AAA").pattern("BEB").pattern("CCC").criterion("has_egg", RecipesProvider.conditionsFromItem(Items.EGG)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.CAMPFIRE).input(Character.valueOf('L'), ItemTags.LOGS).input(Character.valueOf('S'), Items.STICK).input(Character.valueOf('C'), ItemTags.COALS).pattern(" S ").pattern("SCS").pattern("LLL").criterion("has_stick", RecipesProvider.conditionsFromItem(Items.STICK)).criterion("has_coal", RecipesProvider.conditionsFromTag(ItemTags.COALS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.CARROT_ON_A_STICK).input(Character.valueOf('#'), Items.FISHING_ROD).input(Character.valueOf('X'), Items.CARROT).pattern("# ").pattern(" X").criterion("has_carrot", RecipesProvider.conditionsFromItem(Items.CARROT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.WARPED_FUNGUS_ON_A_STICK).input(Character.valueOf('#'), Items.FISHING_ROD).input(Character.valueOf('X'), Items.WARPED_FUNGUS).pattern("# ").pattern(" X").criterion("has_warped_fungus", RecipesProvider.conditionsFromItem(Items.WARPED_FUNGUS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.CAULDRON).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("# #").pattern("# #").pattern("###").criterion("has_water_bucket", RecipesProvider.conditionsFromItem(Items.WATER_BUCKET)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.COMPOSTER).input(Character.valueOf('#'), ItemTags.WOODEN_SLABS).pattern("# #").pattern("# #").pattern("###").criterion("has_wood_slab", RecipesProvider.conditionsFromTag(ItemTags.WOODEN_SLABS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.CHEST).input(Character.valueOf('#'), ItemTags.PLANKS).pattern("###").pattern("# #").pattern("###").criterion("has_lots_of_items", new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.atLeast(10), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new ItemPredicate[0])).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.CHEST_MINECART).input(Character.valueOf('A'), Blocks.CHEST).input(Character.valueOf('B'), Items.MINECART).pattern("A").pattern("B").criterion("has_minecart", RecipesProvider.conditionsFromItem(Items.MINECART)).offerTo(consumer);
        RecipesProvider.offerChiseledBlockRecipe(consumer, Blocks.CHISELED_NETHER_BRICKS, Blocks.NETHER_BRICK_SLAB);
        RecipesProvider.createChiseledBlockRecipe(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_SLAB).criterion("has_chiseled_quartz_block", RecipesProvider.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK)).criterion("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).criterion("has_quartz_pillar", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_PILLAR)).offerTo(consumer);
        RecipesProvider.createChiseledBlockRecipe(Blocks.CHISELED_STONE_BRICKS, Blocks.STONE_BRICK_SLAB).criterion("has_tag", RecipesProvider.conditionsFromTag(ItemTags.STONE_BRICKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.CLAY).input(Character.valueOf('#'), Items.CLAY_BALL).pattern("##").pattern("##").criterion("has_clay_ball", RecipesProvider.conditionsFromItem(Items.CLAY_BALL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.CLOCK).input(Character.valueOf('#'), Items.GOLD_INGOT).input(Character.valueOf('X'), Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_redstone", RecipesProvider.conditionsFromItem(Items.REDSTONE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.COAL, 9).input(Blocks.COAL_BLOCK).criterion("has_coal_block", RecipesProvider.conditionsFromItem(Blocks.COAL_BLOCK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.COAL_BLOCK).input(Character.valueOf('#'), Items.COAL).pattern("###").pattern("###").pattern("###").criterion("has_coal", RecipesProvider.conditionsFromItem(Items.COAL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.COARSE_DIRT, 4).input(Character.valueOf('D'), Blocks.DIRT).input(Character.valueOf('G'), Blocks.GRAVEL).pattern("DG").pattern("GD").criterion("has_gravel", RecipesProvider.conditionsFromItem(Blocks.GRAVEL)).offerTo(consumer);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE);
        RecipesProvider.offerWallRecipe(consumer, Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE);
        ShapedRecipeJsonFactory.create(Blocks.COMPARATOR).input(Character.valueOf('#'), Blocks.REDSTONE_TORCH).input(Character.valueOf('X'), Items.QUARTZ).input(Character.valueOf('I'), Blocks.STONE).pattern(" # ").pattern("#X#").pattern("III").criterion("has_quartz", RecipesProvider.conditionsFromItem(Items.QUARTZ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.COMPASS).input(Character.valueOf('#'), Items.IRON_INGOT).input(Character.valueOf('X'), Items.REDSTONE).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_redstone", RecipesProvider.conditionsFromItem(Items.REDSTONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.COOKIE, 8).input(Character.valueOf('#'), Items.WHEAT).input(Character.valueOf('X'), Items.COCOA_BEANS).pattern("#X#").criterion("has_cocoa", RecipesProvider.conditionsFromItem(Items.COCOA_BEANS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.CRAFTING_TABLE).input(Character.valueOf('#'), ItemTags.PLANKS).pattern("##").pattern("##").criterion("has_planks", RecipesProvider.conditionsFromTag(ItemTags.PLANKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.CROSSBOW).input(Character.valueOf('~'), Items.STRING).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('&'), Items.IRON_INGOT).input(Character.valueOf('$'), Blocks.TRIPWIRE_HOOK).pattern("#&#").pattern("~$~").pattern(" # ").criterion("has_string", RecipesProvider.conditionsFromItem(Items.STRING)).criterion("has_stick", RecipesProvider.conditionsFromItem(Items.STICK)).criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).criterion("has_tripwire_hook", RecipesProvider.conditionsFromItem(Blocks.TRIPWIRE_HOOK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.LOOM).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('@'), Items.STRING).pattern("@@").pattern("##").criterion("has_string", RecipesProvider.conditionsFromItem(Items.STRING)).offerTo(consumer);
        RecipesProvider.createChiseledBlockRecipe(Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE_SLAB).criterion("has_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).criterion("has_chiseled_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE)).criterion("has_cut_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.CUT_RED_SANDSTONE)).offerTo(consumer);
        RecipesProvider.offerChiseledBlockRecipe(consumer, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE_SLAB);
        ShapedRecipeJsonFactory.create(Blocks.COPPER_BLOCK).input(Character.valueOf('#'), Items.COPPER_INGOT).pattern("##").pattern("##").criterion("has_copper_ingot", RecipesProvider.conditionsFromItem(Items.COPPER_INGOT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.COPPER_INGOT, 4).input(Blocks.COPPER_BLOCK).group("copper_ingot").criterion("has_copper_block", RecipesProvider.conditionsFromItem(Blocks.COPPER_BLOCK)).offerTo(consumer, "copper_ingot_from_copper_block");
        RecipesProvider.offerCutCopperRecipe(consumer, Blocks.CUT_COPPER, Blocks.COPPER_BLOCK);
        RecipesProvider.offerCutCopperRecipe(consumer, Blocks.LIGHTLY_WEATHERED_CUT_COPPER, Blocks.LIGHTLY_WEATHERED_COPPER_BLOCK);
        RecipesProvider.offerCutCopperRecipe(consumer, Blocks.SEMI_WEATHERED_CUT_COPPER, Blocks.SEMI_WEATHERED_COPPER_BLOCK);
        RecipesProvider.offerCutCopperRecipe(consumer, Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_COPPER_BLOCK);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.LIGHTLY_WEATHERED_CUT_COPPER_STAIRS, Blocks.LIGHTLY_WEATHERED_CUT_COPPER);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.SEMI_WEATHERED_CUT_COPPER_STAIRS, Blocks.SEMI_WEATHERED_CUT_COPPER);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.LIGHTLY_WEATHERED_CUT_COPPER_SLAB, Blocks.LIGHTLY_WEATHERED_CUT_COPPER);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.SEMI_WEATHERED_CUT_COPPER_SLAB, Blocks.SEMI_WEATHERED_CUT_COPPER);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER);
        ShapelessRecipeJsonFactory.create(Items.WAXED_COPPER).input(Items.COPPER_BLOCK).input(Items.HONEYCOMB).criterion("has_copper_block", RecipesProvider.conditionsFromItem(Items.COPPER_BLOCK)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.WAXED_SEMI_WEATHERED_COPPER).input(Items.SEMI_WEATHERED_COPPER_BLOCK).input(Items.HONEYCOMB).criterion("has_semi_weathered_copper_block", RecipesProvider.conditionsFromItem(Items.SEMI_WEATHERED_COPPER_BLOCK)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.WAXED_LIGHTLY_WEATHERED_COPPER).input(Items.LIGHTLY_WEATHERED_COPPER_BLOCK).input(Items.HONEYCOMB).criterion("has_lightly_weathered_copper_block", RecipesProvider.conditionsFromItem(Items.LIGHTLY_WEATHERED_COPPER_BLOCK)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.WAXED_CUT_COPPER).input(Items.CUT_COPPER).input(Items.HONEYCOMB).criterion("has_cut_copper", RecipesProvider.conditionsFromItem(Items.CUT_COPPER)).offerTo(consumer, "waxed_cut_copper_from_honeycomb");
        ShapelessRecipeJsonFactory.create(Items.WAXED_SEMI_WEATHERED_CUT_COPPER).input(Items.SEMI_WEATHERED_CUT_COPPER).input(Items.HONEYCOMB).criterion("has_semi_weathered_cut_copper", RecipesProvider.conditionsFromItem(Items.SEMI_WEATHERED_CUT_COPPER)).offerTo(consumer, "waxed_semi_weathered_cut_copper_from_honeycomb");
        ShapelessRecipeJsonFactory.create(Items.WAXED_LIGHTLY_WEATHERED_CUT_COPPER).input(Items.LIGHTLY_WEATHERED_CUT_COPPER).input(Items.HONEYCOMB).criterion("has_lightly_weathered_cut_copper", RecipesProvider.conditionsFromItem(Items.LIGHTLY_WEATHERED_CUT_COPPER)).offerTo(consumer, "waxed_lightly_weathered_cut_copper_from_honeycomb");
        ShapelessRecipeJsonFactory.create(Items.WAXED_CUT_COPPER_STAIRS).input(Items.CUT_COPPER_STAIRS).input(Items.HONEYCOMB).criterion("has_copper_cut_stairs", RecipesProvider.conditionsFromItem(Items.CUT_COPPER_STAIRS)).offerTo(consumer, "waxed_copper_cut_stairs_from_honeycomb");
        ShapelessRecipeJsonFactory.create(Items.WAXED_SEMI_WEATHERED_CUT_COPPER_STAIRS).input(Items.SEMI_WEATHERED_CUT_COPPER_STAIRS).input(Items.HONEYCOMB).criterion("has_semi_weathered_cut_copper_stairs", RecipesProvider.conditionsFromItem(Items.SEMI_WEATHERED_CUT_COPPER_STAIRS)).offerTo(consumer, "waxed_semi_weathered_cut_copper_stairs_from_honeycomb");
        ShapelessRecipeJsonFactory.create(Items.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_STAIRS).input(Items.LIGHTLY_WEATHERED_CUT_COPPER_STAIRS).input(Items.HONEYCOMB).criterion("has_lightly_weathered_cut_copper_stairs", RecipesProvider.conditionsFromItem(Items.LIGHTLY_WEATHERED_CUT_COPPER_STAIRS)).offerTo(consumer, "waxed_lightly_weathered_cut_copper_stairs_from_honeycomb");
        ShapelessRecipeJsonFactory.create(Items.WAXED_CUT_COPPER_SLAB).input(Items.CUT_COPPER_SLAB).input(Items.HONEYCOMB).criterion("has_copper_cut_slab", RecipesProvider.conditionsFromItem(Items.CUT_COPPER_SLAB)).offerTo(consumer, "waxed_copper_cut_slab_from_honeycomb");
        ShapelessRecipeJsonFactory.create(Items.WAXED_SEMI_WEATHERED_CUT_COPPER_SLAB).input(Items.SEMI_WEATHERED_CUT_COPPER_SLAB).input(Items.HONEYCOMB).criterion("has_semi_weathered_cut_copper_slab", RecipesProvider.conditionsFromItem(Items.SEMI_WEATHERED_CUT_COPPER_SLAB)).offerTo(consumer, "waxed_semi_weathered_cut_copper_slab_from_honeycomb");
        ShapelessRecipeJsonFactory.create(Items.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_SLAB).input(Items.LIGHTLY_WEATHERED_CUT_COPPER_SLAB).input(Items.HONEYCOMB).criterion("has_lightly_weathered_cut_copper_slab", RecipesProvider.conditionsFromItem(Items.LIGHTLY_WEATHERED_CUT_COPPER_SLAB)).offerTo(consumer, "waxed_lightly_weathered_cut_copper_slab_from_honeycomb");
        ShapedRecipeJsonFactory.create(Blocks.WAXED_CUT_COPPER, 4).input(Character.valueOf('#'), Blocks.WAXED_COPPER).pattern("##").pattern("##").criterion("has_waxed_copper", RecipesProvider.conditionsFromItem(Blocks.WAXED_COPPER)).offerTo(consumer, "waxed_cut_copper_from_waxed_block");
        ShapedRecipeJsonFactory.create(Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER, 4).input(Character.valueOf('#'), Blocks.WAXED_LIGHTLY_WEATHERED_COPPER).pattern("##").pattern("##").criterion("has_waxed_lightly_weathered_copper", RecipesProvider.conditionsFromItem(Blocks.WAXED_LIGHTLY_WEATHERED_COPPER)).offerTo(consumer, "waxed_lightly_weathered_cut_copper_from_waxed_block");
        ShapedRecipeJsonFactory.create(Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER, 4).input(Character.valueOf('#'), Blocks.WAXED_SEMI_WEATHERED_COPPER).pattern("##").pattern("##").criterion("has_waxed_semi_weathered_copper", RecipesProvider.conditionsFromItem(Blocks.WAXED_SEMI_WEATHERED_COPPER)).offerTo(consumer, "waxed_semi_weathered_cut_copper_from_waxed_block");
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER);
        ShapelessRecipeJsonFactory.create(Items.CYAN_DYE, 2).input(Items.BLUE_DYE).input(Items.GREEN_DYE).criterion("has_green_dye", RecipesProvider.conditionsFromItem(Items.GREEN_DYE)).criterion("has_blue_dye", RecipesProvider.conditionsFromItem(Items.BLUE_DYE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.DARK_PRISMARINE).input(Character.valueOf('S'), Items.PRISMARINE_SHARD).input(Character.valueOf('I'), Items.BLACK_DYE).pattern("SSS").pattern("SIS").pattern("SSS").criterion("has_prismarine_shard", RecipesProvider.conditionsFromItem(Items.PRISMARINE_SHARD)).offerTo(consumer);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.PRISMARINE_BRICK_STAIRS, Blocks.PRISMARINE_BRICKS);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE);
        ShapedRecipeJsonFactory.create(Blocks.DAYLIGHT_DETECTOR).input(Character.valueOf('Q'), Items.QUARTZ).input(Character.valueOf('G'), Blocks.GLASS).input(Character.valueOf('W'), Ingredient.fromTag(ItemTags.WOODEN_SLABS)).pattern("GGG").pattern("QQQ").pattern("WWW").criterion("has_quartz", RecipesProvider.conditionsFromItem(Items.QUARTZ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.DETECTOR_RAIL, 6).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Blocks.STONE_PRESSURE_PLATE).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("XRX").criterion("has_rail", RecipesProvider.conditionsFromItem(Blocks.RAIL)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.DIAMOND, 9).input(Blocks.DIAMOND_BLOCK).criterion("has_diamond_block", RecipesProvider.conditionsFromItem(Blocks.DIAMOND_BLOCK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.DIAMOND_AXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.DIAMOND).pattern("XX").pattern("X#").pattern(" #").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.DIAMOND_BLOCK).input(Character.valueOf('#'), Items.DIAMOND).pattern("###").pattern("###").pattern("###").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.DIAMOND_BOOTS).input(Character.valueOf('X'), Items.DIAMOND).pattern("X X").pattern("X X").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.DIAMOND_CHESTPLATE).input(Character.valueOf('X'), Items.DIAMOND).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.DIAMOND_HELMET).input(Character.valueOf('X'), Items.DIAMOND).pattern("XXX").pattern("X X").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.DIAMOND_HOE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.DIAMOND).pattern("XX").pattern(" #").pattern(" #").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.DIAMOND_LEGGINGS).input(Character.valueOf('X'), Items.DIAMOND).pattern("XXX").pattern("X X").pattern("X X").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.DIAMOND_PICKAXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.DIAMOND).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.DIAMOND_SHOVEL).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.DIAMOND).pattern("X").pattern("#").pattern("#").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.DIAMOND_SWORD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.DIAMOND).pattern("X").pattern("X").pattern("#").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.DIORITE, 2).input(Character.valueOf('Q'), Items.QUARTZ).input(Character.valueOf('C'), Blocks.COBBLESTONE).pattern("CQ").pattern("QC").criterion("has_quartz", RecipesProvider.conditionsFromItem(Items.QUARTZ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.DISPENSER).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Blocks.COBBLESTONE).input(Character.valueOf('X'), Items.BOW).pattern("###").pattern("#X#").pattern("#R#").criterion("has_bow", RecipesProvider.conditionsFromItem(Items.BOW)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.DROPPER).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Blocks.COBBLESTONE).pattern("###").pattern("# #").pattern("#R#").criterion("has_redstone", RecipesProvider.conditionsFromItem(Items.REDSTONE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.EMERALD, 9).input(Blocks.EMERALD_BLOCK).criterion("has_emerald_block", RecipesProvider.conditionsFromItem(Blocks.EMERALD_BLOCK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.EMERALD_BLOCK).input(Character.valueOf('#'), Items.EMERALD).pattern("###").pattern("###").pattern("###").criterion("has_emerald", RecipesProvider.conditionsFromItem(Items.EMERALD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ENCHANTING_TABLE).input(Character.valueOf('B'), Items.BOOK).input(Character.valueOf('#'), Blocks.OBSIDIAN).input(Character.valueOf('D'), Items.DIAMOND).pattern(" B ").pattern("D#D").pattern("###").criterion("has_obsidian", RecipesProvider.conditionsFromItem(Blocks.OBSIDIAN)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.ENDER_CHEST).input(Character.valueOf('#'), Blocks.OBSIDIAN).input(Character.valueOf('E'), Items.ENDER_EYE).pattern("###").pattern("#E#").pattern("###").criterion("has_ender_eye", RecipesProvider.conditionsFromItem(Items.ENDER_EYE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.ENDER_EYE).input(Items.ENDER_PEARL).input(Items.BLAZE_POWDER).criterion("has_blaze_powder", RecipesProvider.conditionsFromItem(Items.BLAZE_POWDER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.END_STONE_BRICKS, 4).input(Character.valueOf('#'), Blocks.END_STONE).pattern("##").pattern("##").criterion("has_end_stone", RecipesProvider.conditionsFromItem(Blocks.END_STONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.END_CRYSTAL).input(Character.valueOf('T'), Items.GHAST_TEAR).input(Character.valueOf('E'), Items.ENDER_EYE).input(Character.valueOf('G'), Blocks.GLASS).pattern("GGG").pattern("GEG").pattern("GTG").criterion("has_ender_eye", RecipesProvider.conditionsFromItem(Items.ENDER_EYE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.END_ROD, 4).input(Character.valueOf('#'), Items.POPPED_CHORUS_FRUIT).input(Character.valueOf('/'), Items.BLAZE_ROD).pattern("/").pattern("#").criterion("has_chorus_fruit_popped", RecipesProvider.conditionsFromItem(Items.POPPED_CHORUS_FRUIT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.FERMENTED_SPIDER_EYE).input(Items.SPIDER_EYE).input(Blocks.BROWN_MUSHROOM).input(Items.SUGAR).criterion("has_spider_eye", RecipesProvider.conditionsFromItem(Items.SPIDER_EYE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.FIRE_CHARGE, 3).input(Items.GUNPOWDER).input(Items.BLAZE_POWDER).input(Ingredient.ofItems(Items.COAL, Items.CHARCOAL)).criterion("has_blaze_powder", RecipesProvider.conditionsFromItem(Items.BLAZE_POWDER)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.FIREWORK_ROCKET, 3).input(Items.GUNPOWDER).input(Items.PAPER).criterion("has_gunpowder", RecipesProvider.conditionsFromItem(Items.GUNPOWDER)).offerTo(consumer, "firework_rocket_simple");
        ShapedRecipeJsonFactory.create(Items.FISHING_ROD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.STRING).pattern("  #").pattern(" #X").pattern("# X").criterion("has_string", RecipesProvider.conditionsFromItem(Items.STRING)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.FLINT_AND_STEEL).input(Items.IRON_INGOT).input(Items.FLINT).criterion("has_flint", RecipesProvider.conditionsFromItem(Items.FLINT)).criterion("has_obsidian", RecipesProvider.conditionsFromItem(Blocks.OBSIDIAN)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.FLOWER_POT).input(Character.valueOf('#'), Items.BRICK).pattern("# #").pattern(" # ").criterion("has_brick", RecipesProvider.conditionsFromItem(Items.BRICK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.FURNACE).input(Character.valueOf('#'), ItemTags.STONE_CRAFTING_MATERIALS).pattern("###").pattern("# #").pattern("###").criterion("has_cobblestone", RecipesProvider.conditionsFromTag(ItemTags.STONE_CRAFTING_MATERIALS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.FURNACE_MINECART).input(Character.valueOf('A'), Blocks.FURNACE).input(Character.valueOf('B'), Items.MINECART).pattern("A").pattern("B").criterion("has_minecart", RecipesProvider.conditionsFromItem(Items.MINECART)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GLASS_BOTTLE, 3).input(Character.valueOf('#'), Blocks.GLASS).pattern("# #").pattern(" # ").criterion("has_glass", RecipesProvider.conditionsFromItem(Blocks.GLASS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.GLASS_PANE, 16).input(Character.valueOf('#'), Blocks.GLASS).pattern("###").pattern("###").criterion("has_glass", RecipesProvider.conditionsFromItem(Blocks.GLASS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.GLOWSTONE).input(Character.valueOf('#'), Items.GLOWSTONE_DUST).pattern("##").pattern("##").criterion("has_glowstone_dust", RecipesProvider.conditionsFromItem(Items.GLOWSTONE_DUST)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_APPLE).input(Character.valueOf('#'), Items.GOLD_INGOT).input(Character.valueOf('X'), Items.APPLE).pattern("###").pattern("#X#").pattern("###").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_AXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XX").pattern("X#").pattern(" #").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_BOOTS).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X X").pattern("X X").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_CARROT).input(Character.valueOf('#'), Items.GOLD_NUGGET).input(Character.valueOf('X'), Items.CARROT).pattern("###").pattern("#X#").pattern("###").criterion("has_gold_nugget", RecipesProvider.conditionsFromItem(Items.GOLD_NUGGET)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_CHESTPLATE).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_HELMET).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XXX").pattern("X X").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_HOE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XX").pattern(" #").pattern(" #").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_LEGGINGS).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XXX").pattern("X X").pattern("X X").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_PICKAXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.POWERED_RAIL, 6).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X X").pattern("X#X").pattern("XRX").criterion("has_rail", RecipesProvider.conditionsFromItem(Blocks.RAIL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_SHOVEL).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X").pattern("#").pattern("#").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GOLDEN_SWORD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.GOLD_INGOT).pattern("X").pattern("X").pattern("#").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.GOLD_BLOCK).input(Character.valueOf('#'), Items.GOLD_INGOT).pattern("###").pattern("###").pattern("###").criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.GOLD_INGOT, 9).input(Blocks.GOLD_BLOCK).group("gold_ingot").criterion("has_gold_block", RecipesProvider.conditionsFromItem(Blocks.GOLD_BLOCK)).offerTo(consumer, "gold_ingot_from_gold_block");
        ShapedRecipeJsonFactory.create(Items.GOLD_INGOT).input(Character.valueOf('#'), Items.GOLD_NUGGET).pattern("###").pattern("###").pattern("###").group("gold_ingot").criterion("has_gold_nugget", RecipesProvider.conditionsFromItem(Items.GOLD_NUGGET)).offerTo(consumer, "gold_ingot_from_nuggets");
        ShapelessRecipeJsonFactory.create(Items.GOLD_NUGGET, 9).input(Items.GOLD_INGOT).criterion("has_gold_ingot", RecipesProvider.conditionsFromItem(Items.GOLD_INGOT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.GRANITE).input(Blocks.DIORITE).input(Items.QUARTZ).criterion("has_quartz", RecipesProvider.conditionsFromItem(Items.QUARTZ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.GRAY_DYE, 2).input(Items.BLACK_DYE).input(Items.WHITE_DYE).criterion("has_white_dye", RecipesProvider.conditionsFromItem(Items.WHITE_DYE)).criterion("has_black_dye", RecipesProvider.conditionsFromItem(Items.BLACK_DYE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.HAY_BLOCK).input(Character.valueOf('#'), Items.WHEAT).pattern("###").pattern("###").pattern("###").criterion("has_wheat", RecipesProvider.conditionsFromItem(Items.WHEAT)).offerTo(consumer);
        RecipesProvider.createPressurePlateRecipe(consumer, (ItemConvertible)Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, (ItemConvertible)Items.IRON_INGOT);
        ShapelessRecipeJsonFactory.create(Items.HONEY_BOTTLE, 4).input(Items.HONEY_BLOCK).input(Items.GLASS_BOTTLE, 4).criterion("has_honey_block", RecipesProvider.conditionsFromItem(Blocks.HONEY_BLOCK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.HONEY_BLOCK, 1).input(Character.valueOf('S'), Items.HONEY_BOTTLE).pattern("SS").pattern("SS").criterion("has_honey_bottle", RecipesProvider.conditionsFromItem(Items.HONEY_BOTTLE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.HONEYCOMB_BLOCK).input(Character.valueOf('H'), Items.HONEYCOMB).pattern("HH").pattern("HH").criterion("has_honeycomb", RecipesProvider.conditionsFromItem(Items.HONEYCOMB)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.HOPPER).input(Character.valueOf('C'), Blocks.CHEST).input(Character.valueOf('I'), Items.IRON_INGOT).pattern("I I").pattern("ICI").pattern(" I ").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.HOPPER_MINECART).input(Character.valueOf('A'), Blocks.HOPPER).input(Character.valueOf('B'), Items.MINECART).pattern("A").pattern("B").criterion("has_minecart", RecipesProvider.conditionsFromItem(Items.MINECART)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.IRON_AXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XX").pattern("X#").pattern(" #").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.IRON_BARS, 16).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("###").pattern("###").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.IRON_BLOCK).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("###").pattern("###").pattern("###").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.IRON_BOOTS).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("X X").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.IRON_CHESTPLATE).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.IRON_DOOR, 3).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("##").pattern("##").pattern("##").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.IRON_HELMET).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XXX").pattern("X X").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.IRON_HOE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XX").pattern(" #").pattern(" #").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.IRON_INGOT, 9).input(Blocks.IRON_BLOCK).group("iron_ingot").criterion("has_iron_block", RecipesProvider.conditionsFromItem(Blocks.IRON_BLOCK)).offerTo(consumer, "iron_ingot_from_iron_block");
        ShapedRecipeJsonFactory.create(Items.IRON_INGOT).input(Character.valueOf('#'), Items.IRON_NUGGET).pattern("###").pattern("###").pattern("###").group("iron_ingot").criterion("has_iron_nugget", RecipesProvider.conditionsFromItem(Items.IRON_NUGGET)).offerTo(consumer, "iron_ingot_from_nuggets");
        ShapedRecipeJsonFactory.create(Items.IRON_LEGGINGS).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XXX").pattern("X X").pattern("X X").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.IRON_NUGGET, 9).input(Items.IRON_INGOT).criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.IRON_PICKAXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.IRON_SHOVEL).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X").pattern("#").pattern("#").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.IRON_SWORD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X").pattern("X").pattern("#").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.IRON_TRAPDOOR).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("##").pattern("##").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.ITEM_FRAME).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.LEATHER).pattern("###").pattern("#X#").pattern("###").criterion("has_leather", RecipesProvider.conditionsFromItem(Items.LEATHER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.JUKEBOX).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('X'), Items.DIAMOND).pattern("###").pattern("#X#").pattern("###").criterion("has_diamond", RecipesProvider.conditionsFromItem(Items.DIAMOND)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.LADDER, 3).input(Character.valueOf('#'), Items.STICK).pattern("# #").pattern("###").pattern("# #").criterion("has_stick", RecipesProvider.conditionsFromItem(Items.STICK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.LAPIS_BLOCK).input(Character.valueOf('#'), Items.LAPIS_LAZULI).pattern("###").pattern("###").pattern("###").criterion("has_lapis", RecipesProvider.conditionsFromItem(Items.LAPIS_LAZULI)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.LAPIS_LAZULI, 9).input(Blocks.LAPIS_BLOCK).criterion("has_lapis_block", RecipesProvider.conditionsFromItem(Blocks.LAPIS_BLOCK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.LEAD, 2).input(Character.valueOf('~'), Items.STRING).input(Character.valueOf('O'), Items.SLIME_BALL).pattern("~~ ").pattern("~O ").pattern("  ~").criterion("has_slime_ball", RecipesProvider.conditionsFromItem(Items.SLIME_BALL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.LEATHER).input(Character.valueOf('#'), Items.RABBIT_HIDE).pattern("##").pattern("##").criterion("has_rabbit_hide", RecipesProvider.conditionsFromItem(Items.RABBIT_HIDE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.LEATHER_BOOTS).input(Character.valueOf('X'), Items.LEATHER).pattern("X X").pattern("X X").criterion("has_leather", RecipesProvider.conditionsFromItem(Items.LEATHER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.LEATHER_CHESTPLATE).input(Character.valueOf('X'), Items.LEATHER).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_leather", RecipesProvider.conditionsFromItem(Items.LEATHER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.LEATHER_HELMET).input(Character.valueOf('X'), Items.LEATHER).pattern("XXX").pattern("X X").criterion("has_leather", RecipesProvider.conditionsFromItem(Items.LEATHER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.LEATHER_LEGGINGS).input(Character.valueOf('X'), Items.LEATHER).pattern("XXX").pattern("X X").pattern("X X").criterion("has_leather", RecipesProvider.conditionsFromItem(Items.LEATHER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.LEATHER_HORSE_ARMOR).input(Character.valueOf('X'), Items.LEATHER).pattern("X X").pattern("XXX").pattern("X X").criterion("has_leather", RecipesProvider.conditionsFromItem(Items.LEATHER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.LECTERN).input(Character.valueOf('S'), ItemTags.WOODEN_SLABS).input(Character.valueOf('B'), Blocks.BOOKSHELF).pattern("SSS").pattern(" B ").pattern(" S ").criterion("has_book", RecipesProvider.conditionsFromItem(Items.BOOK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.LEVER).input(Character.valueOf('#'), Blocks.COBBLESTONE).input(Character.valueOf('X'), Items.STICK).pattern("X").pattern("#").criterion("has_cobblestone", RecipesProvider.conditionsFromItem(Blocks.COBBLESTONE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.LIGHT_BLUE_DYE).input(Blocks.BLUE_ORCHID).group("light_blue_dye").criterion("has_red_flower", RecipesProvider.conditionsFromItem(Blocks.BLUE_ORCHID)).offerTo(consumer, "light_blue_dye_from_blue_orchid");
        ShapelessRecipeJsonFactory.create(Items.LIGHT_BLUE_DYE, 2).input(Items.BLUE_DYE).input(Items.WHITE_DYE).group("light_blue_dye").criterion("has_blue_dye", RecipesProvider.conditionsFromItem(Items.BLUE_DYE)).criterion("has_white_dye", RecipesProvider.conditionsFromItem(Items.WHITE_DYE)).offerTo(consumer, "light_blue_dye_from_blue_white_dye");
        ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE).input(Blocks.AZURE_BLUET).group("light_gray_dye").criterion("has_red_flower", RecipesProvider.conditionsFromItem(Blocks.AZURE_BLUET)).offerTo(consumer, "light_gray_dye_from_azure_bluet");
        ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE, 2).input(Items.GRAY_DYE).input(Items.WHITE_DYE).group("light_gray_dye").criterion("has_gray_dye", RecipesProvider.conditionsFromItem(Items.GRAY_DYE)).criterion("has_white_dye", RecipesProvider.conditionsFromItem(Items.WHITE_DYE)).offerTo(consumer, "light_gray_dye_from_gray_white_dye");
        ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE, 3).input(Items.BLACK_DYE).input(Items.WHITE_DYE, 2).group("light_gray_dye").criterion("has_white_dye", RecipesProvider.conditionsFromItem(Items.WHITE_DYE)).criterion("has_black_dye", RecipesProvider.conditionsFromItem(Items.BLACK_DYE)).offerTo(consumer, "light_gray_dye_from_black_white_dye");
        ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE).input(Blocks.OXEYE_DAISY).group("light_gray_dye").criterion("has_red_flower", RecipesProvider.conditionsFromItem(Blocks.OXEYE_DAISY)).offerTo(consumer, "light_gray_dye_from_oxeye_daisy");
        ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE).input(Blocks.WHITE_TULIP).group("light_gray_dye").criterion("has_red_flower", RecipesProvider.conditionsFromItem(Blocks.WHITE_TULIP)).offerTo(consumer, "light_gray_dye_from_white_tulip");
        RecipesProvider.createPressurePlateRecipe(consumer, (ItemConvertible)Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, (ItemConvertible)Items.GOLD_INGOT);
        ShapedRecipeJsonFactory.create(Blocks.LIGHTNING_ROD).input(Character.valueOf('#'), Items.COPPER_INGOT).pattern("#").pattern("#").pattern("#").criterion("has_copper_ingot", RecipesProvider.conditionsFromItem(Items.COPPER_INGOT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.LIME_DYE, 2).input(Items.GREEN_DYE).input(Items.WHITE_DYE).criterion("has_green_dye", RecipesProvider.conditionsFromItem(Items.GREEN_DYE)).criterion("has_white_dye", RecipesProvider.conditionsFromItem(Items.WHITE_DYE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.JACK_O_LANTERN).input(Character.valueOf('A'), Blocks.CARVED_PUMPKIN).input(Character.valueOf('B'), Blocks.TORCH).pattern("A").pattern("B").criterion("has_carved_pumpkin", RecipesProvider.conditionsFromItem(Blocks.CARVED_PUMPKIN)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE).input(Blocks.ALLIUM).group("magenta_dye").criterion("has_red_flower", RecipesProvider.conditionsFromItem(Blocks.ALLIUM)).offerTo(consumer, "magenta_dye_from_allium");
        ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 4).input(Items.BLUE_DYE).input(Items.RED_DYE, 2).input(Items.WHITE_DYE).group("magenta_dye").criterion("has_blue_dye", RecipesProvider.conditionsFromItem(Items.BLUE_DYE)).criterion("has_rose_red", RecipesProvider.conditionsFromItem(Items.RED_DYE)).criterion("has_white_dye", RecipesProvider.conditionsFromItem(Items.WHITE_DYE)).offerTo(consumer, "magenta_dye_from_blue_red_white_dye");
        ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 3).input(Items.BLUE_DYE).input(Items.RED_DYE).input(Items.PINK_DYE).group("magenta_dye").criterion("has_pink_dye", RecipesProvider.conditionsFromItem(Items.PINK_DYE)).criterion("has_blue_dye", RecipesProvider.conditionsFromItem(Items.BLUE_DYE)).criterion("has_red_dye", RecipesProvider.conditionsFromItem(Items.RED_DYE)).offerTo(consumer, "magenta_dye_from_blue_red_pink");
        ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 2).input(Blocks.LILAC).group("magenta_dye").criterion("has_double_plant", RecipesProvider.conditionsFromItem(Blocks.LILAC)).offerTo(consumer, "magenta_dye_from_lilac");
        ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 2).input(Items.PURPLE_DYE).input(Items.PINK_DYE).group("magenta_dye").criterion("has_pink_dye", RecipesProvider.conditionsFromItem(Items.PINK_DYE)).criterion("has_purple_dye", RecipesProvider.conditionsFromItem(Items.PURPLE_DYE)).offerTo(consumer, "magenta_dye_from_purple_and_pink");
        ShapedRecipeJsonFactory.create(Blocks.MAGMA_BLOCK).input(Character.valueOf('#'), Items.MAGMA_CREAM).pattern("##").pattern("##").criterion("has_magma_cream", RecipesProvider.conditionsFromItem(Items.MAGMA_CREAM)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.MAGMA_CREAM).input(Items.BLAZE_POWDER).input(Items.SLIME_BALL).criterion("has_blaze_powder", RecipesProvider.conditionsFromItem(Items.BLAZE_POWDER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.MAP).input(Character.valueOf('#'), Items.PAPER).input(Character.valueOf('X'), Items.COMPASS).pattern("###").pattern("#X#").pattern("###").criterion("has_compass", RecipesProvider.conditionsFromItem(Items.COMPASS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.MELON).input(Character.valueOf('M'), Items.MELON_SLICE).pattern("MMM").pattern("MMM").pattern("MMM").criterion("has_melon", RecipesProvider.conditionsFromItem(Items.MELON_SLICE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.MELON_SEEDS).input(Items.MELON_SLICE).criterion("has_melon", RecipesProvider.conditionsFromItem(Items.MELON_SLICE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.MINECART).input(Character.valueOf('#'), Items.IRON_INGOT).pattern("# #").pattern("###").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.MOSSY_COBBLESTONE).input(Blocks.COBBLESTONE).input(Blocks.VINE).criterion("has_vine", RecipesProvider.conditionsFromItem(Blocks.VINE)).offerTo(consumer);
        RecipesProvider.offerWallRecipe(consumer, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE);
        ShapelessRecipeJsonFactory.create(Blocks.MOSSY_STONE_BRICKS).input(Blocks.STONE_BRICKS).input(Blocks.VINE).criterion("has_mossy_cobblestone", RecipesProvider.conditionsFromItem(Blocks.MOSSY_COBBLESTONE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.MUSHROOM_STEW).input(Blocks.BROWN_MUSHROOM).input(Blocks.RED_MUSHROOM).input(Items.BOWL).criterion("has_mushroom_stew", RecipesProvider.conditionsFromItem(Items.MUSHROOM_STEW)).criterion("has_bowl", RecipesProvider.conditionsFromItem(Items.BOWL)).criterion("has_brown_mushroom", RecipesProvider.conditionsFromItem(Blocks.BROWN_MUSHROOM)).criterion("has_red_mushroom", RecipesProvider.conditionsFromItem(Blocks.RED_MUSHROOM)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.NETHER_BRICKS).input(Character.valueOf('N'), Items.NETHER_BRICK).pattern("NN").pattern("NN").criterion("has_netherbrick", RecipesProvider.conditionsFromItem(Items.NETHER_BRICK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.NETHER_BRICK_FENCE, 6).input(Character.valueOf('#'), Blocks.NETHER_BRICKS).input(Character.valueOf('-'), Items.NETHER_BRICK).pattern("#-#").pattern("#-#").criterion("has_nether_brick", RecipesProvider.conditionsFromItem(Blocks.NETHER_BRICKS)).offerTo(consumer);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICKS);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICKS);
        ShapedRecipeJsonFactory.create(Blocks.NETHER_WART_BLOCK).input(Character.valueOf('#'), Items.NETHER_WART).pattern("###").pattern("###").pattern("###").criterion("has_nether_wart", RecipesProvider.conditionsFromItem(Items.NETHER_WART)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.NOTE_BLOCK).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('X'), Items.REDSTONE).pattern("###").pattern("#X#").pattern("###").criterion("has_redstone", RecipesProvider.conditionsFromItem(Items.REDSTONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.OBSERVER).input(Character.valueOf('Q'), Items.QUARTZ).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Blocks.COBBLESTONE).pattern("###").pattern("RRQ").pattern("###").criterion("has_quartz", RecipesProvider.conditionsFromItem(Items.QUARTZ)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.ORANGE_DYE).input(Blocks.ORANGE_TULIP).group("orange_dye").criterion("has_red_flower", RecipesProvider.conditionsFromItem(Blocks.ORANGE_TULIP)).offerTo(consumer, "orange_dye_from_orange_tulip");
        ShapelessRecipeJsonFactory.create(Items.ORANGE_DYE, 2).input(Items.RED_DYE).input(Items.YELLOW_DYE).group("orange_dye").criterion("has_red_dye", RecipesProvider.conditionsFromItem(Items.RED_DYE)).criterion("has_yellow_dye", RecipesProvider.conditionsFromItem(Items.YELLOW_DYE)).offerTo(consumer, "orange_dye_from_red_yellow");
        ShapedRecipeJsonFactory.create(Items.PAINTING).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Ingredient.fromTag(ItemTags.WOOL)).pattern("###").pattern("#X#").pattern("###").criterion("has_wool", RecipesProvider.conditionsFromTag(ItemTags.WOOL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.PAPER, 3).input(Character.valueOf('#'), Blocks.SUGAR_CANE).pattern("###").criterion("has_reeds", RecipesProvider.conditionsFromItem(Blocks.SUGAR_CANE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.QUARTZ_PILLAR, 2).input(Character.valueOf('#'), Blocks.QUARTZ_BLOCK).pattern("#").pattern("#").criterion("has_chiseled_quartz_block", RecipesProvider.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK)).criterion("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).criterion("has_quartz_pillar", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_PILLAR)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.PACKED_ICE).input(Blocks.ICE, 9).criterion("has_ice", RecipesProvider.conditionsFromItem(Blocks.ICE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.PINK_DYE, 2).input(Blocks.PEONY).group("pink_dye").criterion("has_double_plant", RecipesProvider.conditionsFromItem(Blocks.PEONY)).offerTo(consumer, "pink_dye_from_peony");
        ShapelessRecipeJsonFactory.create(Items.PINK_DYE).input(Blocks.PINK_TULIP).group("pink_dye").criterion("has_red_flower", RecipesProvider.conditionsFromItem(Blocks.PINK_TULIP)).offerTo(consumer, "pink_dye_from_pink_tulip");
        ShapelessRecipeJsonFactory.create(Items.PINK_DYE, 2).input(Items.RED_DYE).input(Items.WHITE_DYE).group("pink_dye").criterion("has_white_dye", RecipesProvider.conditionsFromItem(Items.WHITE_DYE)).criterion("has_red_dye", RecipesProvider.conditionsFromItem(Items.RED_DYE)).offerTo(consumer, "pink_dye_from_red_white_dye");
        ShapedRecipeJsonFactory.create(Blocks.PISTON).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('#'), Blocks.COBBLESTONE).input(Character.valueOf('T'), ItemTags.PLANKS).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("TTT").pattern("#X#").pattern("#R#").criterion("has_redstone", RecipesProvider.conditionsFromItem(Items.REDSTONE)).offerTo(consumer);
        RecipesProvider.offerPolishedStoneRecipe(consumer, Blocks.POLISHED_BASALT, Blocks.BASALT);
        RecipesProvider.offerPolishedStoneRecipe(consumer, Blocks.POLISHED_GRANITE, Blocks.GRANITE);
        RecipesProvider.offerPolishedStoneRecipe(consumer, Blocks.POLISHED_DIORITE, Blocks.DIORITE);
        RecipesProvider.offerPolishedStoneRecipe(consumer, Blocks.POLISHED_ANDESITE, Blocks.ANDESITE);
        ShapedRecipeJsonFactory.create(Blocks.PRISMARINE).input(Character.valueOf('S'), Items.PRISMARINE_SHARD).pattern("SS").pattern("SS").criterion("has_prismarine_shard", RecipesProvider.conditionsFromItem(Items.PRISMARINE_SHARD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.PRISMARINE_BRICKS).input(Character.valueOf('S'), Items.PRISMARINE_SHARD).pattern("SSS").pattern("SSS").pattern("SSS").criterion("has_prismarine_shard", RecipesProvider.conditionsFromItem(Items.PRISMARINE_SHARD)).offerTo(consumer);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.PRISMARINE_BRICK_SLAB, Blocks.PRISMARINE_BRICKS);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE);
        ShapelessRecipeJsonFactory.create(Items.PUMPKIN_PIE).input(Blocks.PUMPKIN).input(Items.SUGAR).input(Items.EGG).criterion("has_carved_pumpkin", RecipesProvider.conditionsFromItem(Blocks.CARVED_PUMPKIN)).criterion("has_pumpkin", RecipesProvider.conditionsFromItem(Blocks.PUMPKIN)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.PUMPKIN_SEEDS, 4).input(Blocks.PUMPKIN).criterion("has_pumpkin", RecipesProvider.conditionsFromItem(Blocks.PUMPKIN)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.PURPLE_DYE, 2).input(Items.BLUE_DYE).input(Items.RED_DYE).criterion("has_blue_dye", RecipesProvider.conditionsFromItem(Items.BLUE_DYE)).criterion("has_red_dye", RecipesProvider.conditionsFromItem(Items.RED_DYE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SHULKER_BOX).input(Character.valueOf('#'), Blocks.CHEST).input(Character.valueOf('-'), Items.SHULKER_SHELL).pattern("-").pattern("#").pattern("-").criterion("has_shulker_shell", RecipesProvider.conditionsFromItem(Items.SHULKER_SHELL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.PURPUR_BLOCK, 4).input(Character.valueOf('F'), Items.POPPED_CHORUS_FRUIT).pattern("FF").pattern("FF").criterion("has_chorus_fruit_popped", RecipesProvider.conditionsFromItem(Items.POPPED_CHORUS_FRUIT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.PURPUR_PILLAR).input(Character.valueOf('#'), Blocks.PURPUR_SLAB).pattern("#").pattern("#").criterion("has_purpur_block", RecipesProvider.conditionsFromItem(Blocks.PURPUR_BLOCK)).offerTo(consumer);
        RecipesProvider.createSlabRecipe(Blocks.PURPUR_SLAB, Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).criterion("has_purpur_block", RecipesProvider.conditionsFromItem(Blocks.PURPUR_BLOCK)).offerTo(consumer);
        RecipesProvider.createStairsRecipe(Blocks.PURPUR_STAIRS, Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR)).criterion("has_purpur_block", RecipesProvider.conditionsFromItem(Blocks.PURPUR_BLOCK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.QUARTZ_BLOCK).input(Character.valueOf('#'), Items.QUARTZ).pattern("##").pattern("##").criterion("has_quartz", RecipesProvider.conditionsFromItem(Items.QUARTZ)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.QUARTZ_BRICKS, 4).input(Character.valueOf('#'), Blocks.QUARTZ_BLOCK).pattern("##").pattern("##").criterion("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).offerTo(consumer);
        RecipesProvider.createSlabRecipe(Blocks.QUARTZ_SLAB, Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).criterion("has_chiseled_quartz_block", RecipesProvider.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK)).criterion("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).criterion("has_quartz_pillar", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_PILLAR)).offerTo(consumer);
        RecipesProvider.createStairsRecipe(Blocks.QUARTZ_STAIRS, Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR)).criterion("has_chiseled_quartz_block", RecipesProvider.conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK)).criterion("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).criterion("has_quartz_pillar", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_PILLAR)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.RABBIT_STEW).input(Items.BAKED_POTATO).input(Items.COOKED_RABBIT).input(Items.BOWL).input(Items.CARROT).input(Blocks.BROWN_MUSHROOM).group("rabbit_stew").criterion("has_cooked_rabbit", RecipesProvider.conditionsFromItem(Items.COOKED_RABBIT)).offerTo(consumer, "rabbit_stew_from_brown_mushroom");
        ShapelessRecipeJsonFactory.create(Items.RABBIT_STEW).input(Items.BAKED_POTATO).input(Items.COOKED_RABBIT).input(Items.BOWL).input(Items.CARROT).input(Blocks.RED_MUSHROOM).group("rabbit_stew").criterion("has_cooked_rabbit", RecipesProvider.conditionsFromItem(Items.COOKED_RABBIT)).offerTo(consumer, "rabbit_stew_from_red_mushroom");
        ShapedRecipeJsonFactory.create(Blocks.RAIL, 16).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.IRON_INGOT).pattern("X X").pattern("X#X").pattern("X X").criterion("has_minecart", RecipesProvider.conditionsFromItem(Items.MINECART)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.REDSTONE, 9).input(Blocks.REDSTONE_BLOCK).criterion("has_redstone_block", RecipesProvider.conditionsFromItem(Blocks.REDSTONE_BLOCK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.REDSTONE_BLOCK).input(Character.valueOf('#'), Items.REDSTONE).pattern("###").pattern("###").pattern("###").criterion("has_redstone", RecipesProvider.conditionsFromItem(Items.REDSTONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.REDSTONE_LAMP).input(Character.valueOf('R'), Items.REDSTONE).input(Character.valueOf('G'), Blocks.GLOWSTONE).pattern(" R ").pattern("RGR").pattern(" R ").criterion("has_glowstone", RecipesProvider.conditionsFromItem(Blocks.GLOWSTONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.REDSTONE_TORCH).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Items.REDSTONE).pattern("X").pattern("#").criterion("has_redstone", RecipesProvider.conditionsFromItem(Items.REDSTONE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.RED_DYE).input(Items.BEETROOT).group("red_dye").criterion("has_beetroot", RecipesProvider.conditionsFromItem(Items.BEETROOT)).offerTo(consumer, "red_dye_from_beetroot");
        ShapelessRecipeJsonFactory.create(Items.RED_DYE).input(Blocks.POPPY).group("red_dye").criterion("has_red_flower", RecipesProvider.conditionsFromItem(Blocks.POPPY)).offerTo(consumer, "red_dye_from_poppy");
        ShapelessRecipeJsonFactory.create(Items.RED_DYE, 2).input(Blocks.ROSE_BUSH).group("red_dye").criterion("has_double_plant", RecipesProvider.conditionsFromItem(Blocks.ROSE_BUSH)).offerTo(consumer, "red_dye_from_rose_bush");
        ShapelessRecipeJsonFactory.create(Items.RED_DYE).input(Blocks.RED_TULIP).group("red_dye").criterion("has_red_flower", RecipesProvider.conditionsFromItem(Blocks.RED_TULIP)).offerTo(consumer, "red_dye_from_tulip");
        ShapedRecipeJsonFactory.create(Blocks.RED_NETHER_BRICKS).input(Character.valueOf('W'), Items.NETHER_WART).input(Character.valueOf('N'), Items.NETHER_BRICK).pattern("NW").pattern("WN").criterion("has_nether_wart", RecipesProvider.conditionsFromItem(Items.NETHER_WART)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.RED_SANDSTONE).input(Character.valueOf('#'), Blocks.RED_SAND).pattern("##").pattern("##").criterion("has_sand", RecipesProvider.conditionsFromItem(Blocks.RED_SAND)).offerTo(consumer);
        RecipesProvider.createSlabRecipe(Blocks.RED_SANDSTONE_SLAB, Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE)).criterion("has_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).criterion("has_chiseled_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE)).offerTo(consumer);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE);
        RecipesProvider.createStairsRecipe(Blocks.RED_SANDSTONE_STAIRS, Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE)).criterion("has_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).criterion("has_chiseled_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE)).criterion("has_cut_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.CUT_RED_SANDSTONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.REPEATER).input(Character.valueOf('#'), Blocks.REDSTONE_TORCH).input(Character.valueOf('X'), Items.REDSTONE).input(Character.valueOf('I'), Blocks.STONE).pattern("#X#").pattern("III").criterion("has_redstone_torch", RecipesProvider.conditionsFromItem(Blocks.REDSTONE_TORCH)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SANDSTONE).input(Character.valueOf('#'), Blocks.SAND).pattern("##").pattern("##").criterion("has_sand", RecipesProvider.conditionsFromItem(Blocks.SAND)).offerTo(consumer);
        RecipesProvider.createSlabRecipe(Blocks.SANDSTONE_SLAB, Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE)).criterion("has_sandstone", RecipesProvider.conditionsFromItem(Blocks.SANDSTONE)).criterion("has_chiseled_sandstone", RecipesProvider.conditionsFromItem(Blocks.CHISELED_SANDSTONE)).offerTo(consumer);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.CUT_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE);
        RecipesProvider.createStairsRecipe(Blocks.SANDSTONE_STAIRS, Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE)).criterion("has_sandstone", RecipesProvider.conditionsFromItem(Blocks.SANDSTONE)).criterion("has_chiseled_sandstone", RecipesProvider.conditionsFromItem(Blocks.CHISELED_SANDSTONE)).criterion("has_cut_sandstone", RecipesProvider.conditionsFromItem(Blocks.CUT_SANDSTONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SEA_LANTERN).input(Character.valueOf('S'), Items.PRISMARINE_SHARD).input(Character.valueOf('C'), Items.PRISMARINE_CRYSTALS).pattern("SCS").pattern("CCC").pattern("SCS").criterion("has_prismarine_crystals", RecipesProvider.conditionsFromItem(Items.PRISMARINE_CRYSTALS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.SHEARS).input(Character.valueOf('#'), Items.IRON_INGOT).pattern(" #").pattern("# ").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.SHIELD).input(Character.valueOf('W'), ItemTags.PLANKS).input(Character.valueOf('o'), Items.IRON_INGOT).pattern("WoW").pattern("WWW").pattern(" W ").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SLIME_BLOCK).input(Character.valueOf('#'), Items.SLIME_BALL).pattern("###").pattern("###").pattern("###").criterion("has_slime_ball", RecipesProvider.conditionsFromItem(Items.SLIME_BALL)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.SLIME_BALL, 9).input(Blocks.SLIME_BLOCK).criterion("has_slime", RecipesProvider.conditionsFromItem(Blocks.SLIME_BLOCK)).offerTo(consumer);
        RecipesProvider.offerCutCopperRecipe(consumer, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
        RecipesProvider.offerCutCopperRecipe(consumer, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
        ShapedRecipeJsonFactory.create(Blocks.SNOW_BLOCK).input(Character.valueOf('#'), Items.SNOWBALL).pattern("##").pattern("##").criterion("has_snowball", RecipesProvider.conditionsFromItem(Items.SNOWBALL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SNOW, 6).input(Character.valueOf('#'), Blocks.SNOW_BLOCK).pattern("###").criterion("has_snowball", RecipesProvider.conditionsFromItem(Items.SNOWBALL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SOUL_CAMPFIRE).input(Character.valueOf('L'), ItemTags.LOGS).input(Character.valueOf('S'), Items.STICK).input(Character.valueOf('#'), ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern(" S ").pattern("S#S").pattern("LLL").criterion("has_stick", RecipesProvider.conditionsFromItem(Items.STICK)).criterion("has_soul_sand", RecipesProvider.conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.GLISTERING_MELON_SLICE).input(Character.valueOf('#'), Items.GOLD_NUGGET).input(Character.valueOf('X'), Items.MELON_SLICE).pattern("###").pattern("#X#").pattern("###").criterion("has_melon", RecipesProvider.conditionsFromItem(Items.MELON_SLICE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.SPECTRAL_ARROW, 2).input(Character.valueOf('#'), Items.GLOWSTONE_DUST).input(Character.valueOf('X'), Items.ARROW).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_glowstone_dust", RecipesProvider.conditionsFromItem(Items.GLOWSTONE_DUST)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.SPYGLASS).input(Character.valueOf('#'), Items.AMETHYST_SHARD).input(Character.valueOf('X'), Items.COPPER_INGOT).pattern(" # ").pattern(" X ").pattern(" X ").criterion("has_amethyst_shard", RecipesProvider.conditionsFromItem(Items.AMETHYST_SHARD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.STICK, 4).input(Character.valueOf('#'), ItemTags.PLANKS).pattern("#").pattern("#").group("sticks").criterion("has_planks", RecipesProvider.conditionsFromTag(ItemTags.PLANKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.STICK, 1).input(Character.valueOf('#'), Blocks.BAMBOO).pattern("#").pattern("#").group("sticks").criterion("has_bamboo", RecipesProvider.conditionsFromItem(Blocks.BAMBOO)).offerTo(consumer, "stick_from_bamboo_item");
        ShapedRecipeJsonFactory.create(Blocks.STICKY_PISTON).input(Character.valueOf('P'), Blocks.PISTON).input(Character.valueOf('S'), Items.SLIME_BALL).pattern("S").pattern("P").criterion("has_slime_ball", RecipesProvider.conditionsFromItem(Items.SLIME_BALL)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.STONE_BRICKS, 4).input(Character.valueOf('#'), Blocks.STONE).pattern("##").pattern("##").criterion("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.STONE_AXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern("X#").pattern(" #").criterion("has_cobblestone", RecipesProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS)).offerTo(consumer);
        RecipesProvider.createSlabRecipe(Blocks.STONE_BRICK_SLAB, Ingredient.ofItems(Blocks.STONE_BRICKS)).criterion("has_stone_bricks", RecipesProvider.conditionsFromTag(ItemTags.STONE_BRICKS)).offerTo(consumer);
        RecipesProvider.createStairsRecipe(Blocks.STONE_BRICK_STAIRS, Ingredient.ofItems(Blocks.STONE_BRICKS)).criterion("has_stone_bricks", RecipesProvider.conditionsFromTag(ItemTags.STONE_BRICKS)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.STONE_BUTTON).input(Blocks.STONE).criterion("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.STONE_HOE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("XX").pattern(" #").pattern(" #").criterion("has_cobblestone", RecipesProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.STONE_PICKAXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_cobblestone", RecipesProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS)).offerTo(consumer);
        RecipesProvider.createPressurePlateRecipe(consumer, (ItemConvertible)Blocks.STONE_PRESSURE_PLATE, (ItemConvertible)Items.STONE);
        ShapedRecipeJsonFactory.create(Items.STONE_SHOVEL).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("#").pattern("#").criterion("has_cobblestone", RecipesProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS)).offerTo(consumer);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.STONE_SLAB, Blocks.STONE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE);
        ShapedRecipeJsonFactory.create(Items.STONE_SWORD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.STONE_TOOL_MATERIALS).pattern("X").pattern("X").pattern("#").criterion("has_cobblestone", RecipesProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.WHITE_WOOL).input(Character.valueOf('#'), Items.STRING).pattern("##").pattern("##").criterion("has_string", RecipesProvider.conditionsFromItem(Items.STRING)).offerTo(consumer, "white_wool_from_string");
        ShapelessRecipeJsonFactory.create(Items.SUGAR).input(Blocks.SUGAR_CANE).group("sugar").criterion("has_reeds", RecipesProvider.conditionsFromItem(Blocks.SUGAR_CANE)).offerTo(consumer, "sugar_from_sugar_cane");
        ShapelessRecipeJsonFactory.create(Items.SUGAR, 3).input(Items.HONEY_BOTTLE).group("sugar").criterion("has_honey_bottle", RecipesProvider.conditionsFromItem(Items.HONEY_BOTTLE)).offerTo(consumer, "sugar_from_honey_bottle");
        ShapedRecipeJsonFactory.create(Blocks.TARGET).input(Character.valueOf('H'), Items.HAY_BLOCK).input(Character.valueOf('R'), Items.REDSTONE).pattern(" R ").pattern("RHR").pattern(" R ").criterion("has_redstone", RecipesProvider.conditionsFromItem(Items.REDSTONE)).criterion("has_hay_block", RecipesProvider.conditionsFromItem(Blocks.HAY_BLOCK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.TNT).input(Character.valueOf('#'), Ingredient.ofItems(Blocks.SAND, Blocks.RED_SAND)).input(Character.valueOf('X'), Items.GUNPOWDER).pattern("X#X").pattern("#X#").pattern("X#X").criterion("has_gunpowder", RecipesProvider.conditionsFromItem(Items.GUNPOWDER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.TNT_MINECART).input(Character.valueOf('A'), Blocks.TNT).input(Character.valueOf('B'), Items.MINECART).pattern("A").pattern("B").criterion("has_minecart", RecipesProvider.conditionsFromItem(Items.MINECART)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.TORCH, 4).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), Ingredient.ofItems(Items.COAL, Items.CHARCOAL)).pattern("X").pattern("#").criterion("has_stone_pickaxe", RecipesProvider.conditionsFromItem(Items.STONE_PICKAXE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SOUL_TORCH, 4).input(Character.valueOf('X'), Ingredient.ofItems(Items.COAL, Items.CHARCOAL)).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('S'), ItemTags.SOUL_FIRE_BASE_BLOCKS).pattern("X").pattern("#").pattern("S").criterion("has_soul_sand", RecipesProvider.conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.LANTERN).input(Character.valueOf('#'), Items.TORCH).input(Character.valueOf('X'), Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").criterion("has_iron_nugget", RecipesProvider.conditionsFromItem(Items.IRON_NUGGET)).criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SOUL_LANTERN).input(Character.valueOf('#'), Items.SOUL_TORCH).input(Character.valueOf('X'), Items.IRON_NUGGET).pattern("XXX").pattern("X#X").pattern("XXX").criterion("has_soul_torch", RecipesProvider.conditionsFromItem(Items.SOUL_TORCH)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.TRAPPED_CHEST).input(Blocks.CHEST).input(Blocks.TRIPWIRE_HOOK).criterion("has_tripwire_hook", RecipesProvider.conditionsFromItem(Blocks.TRIPWIRE_HOOK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.TRIPWIRE_HOOK, 2).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('S'), Items.STICK).input(Character.valueOf('I'), Items.IRON_INGOT).pattern("I").pattern("S").pattern("#").criterion("has_string", RecipesProvider.conditionsFromItem(Items.STRING)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.TURTLE_HELMET).input(Character.valueOf('X'), Items.SCUTE).pattern("XXX").pattern("X X").criterion("has_scute", RecipesProvider.conditionsFromItem(Items.SCUTE)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.WHEAT, 9).input(Blocks.HAY_BLOCK).criterion("has_hay_block", RecipesProvider.conditionsFromItem(Blocks.HAY_BLOCK)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.WHITE_DYE).input(Items.BONE_MEAL).group("white_dye").criterion("has_bone_meal", RecipesProvider.conditionsFromItem(Items.BONE_MEAL)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.WHITE_DYE).input(Blocks.LILY_OF_THE_VALLEY).group("white_dye").criterion("has_white_flower", RecipesProvider.conditionsFromItem(Blocks.LILY_OF_THE_VALLEY)).offerTo(consumer, "white_dye_from_lily_of_the_valley");
        ShapedRecipeJsonFactory.create(Items.WOODEN_AXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("XX").pattern("X#").pattern(" #").criterion("has_stick", RecipesProvider.conditionsFromItem(Items.STICK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.WOODEN_HOE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("XX").pattern(" #").pattern(" #").criterion("has_stick", RecipesProvider.conditionsFromItem(Items.STICK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.WOODEN_PICKAXE).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("XXX").pattern(" # ").pattern(" # ").criterion("has_stick", RecipesProvider.conditionsFromItem(Items.STICK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.WOODEN_SHOVEL).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("X").pattern("#").pattern("#").criterion("has_stick", RecipesProvider.conditionsFromItem(Items.STICK)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.WOODEN_SWORD).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("X").pattern("X").pattern("#").criterion("has_stick", RecipesProvider.conditionsFromItem(Items.STICK)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.WRITABLE_BOOK).input(Items.BOOK).input(Items.INK_SAC).input(Items.FEATHER).criterion("has_book", RecipesProvider.conditionsFromItem(Items.BOOK)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.YELLOW_DYE).input(Blocks.DANDELION).group("yellow_dye").criterion("has_yellow_flower", RecipesProvider.conditionsFromItem(Blocks.DANDELION)).offerTo(consumer, "yellow_dye_from_dandelion");
        ShapelessRecipeJsonFactory.create(Items.YELLOW_DYE, 2).input(Blocks.SUNFLOWER).group("yellow_dye").criterion("has_double_plant", RecipesProvider.conditionsFromItem(Blocks.SUNFLOWER)).offerTo(consumer, "yellow_dye_from_sunflower");
        ShapelessRecipeJsonFactory.create(Items.DRIED_KELP, 9).input(Blocks.DRIED_KELP_BLOCK).criterion("has_dried_kelp_block", RecipesProvider.conditionsFromItem(Blocks.DRIED_KELP_BLOCK)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Blocks.DRIED_KELP_BLOCK).input(Items.DRIED_KELP, 9).criterion("has_dried_kelp", RecipesProvider.conditionsFromItem(Items.DRIED_KELP)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.CONDUIT).input(Character.valueOf('#'), Items.NAUTILUS_SHELL).input(Character.valueOf('X'), Items.HEART_OF_THE_SEA).pattern("###").pattern("#X#").pattern("###").criterion("has_nautilus_core", RecipesProvider.conditionsFromItem(Items.HEART_OF_THE_SEA)).criterion("has_nautilus_shell", RecipesProvider.conditionsFromItem(Items.NAUTILUS_SHELL)).offerTo(consumer);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.POLISHED_GRANITE_STAIRS, Blocks.POLISHED_GRANITE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICKS);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.POLISHED_DIORITE_STAIRS, Blocks.POLISHED_DIORITE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE_BRICKS);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.STONE_STAIRS, Blocks.STONE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.SMOOTH_QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.GRANITE_STAIRS, Blocks.GRANITE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICKS);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.DIORITE_STAIRS, Blocks.DIORITE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICKS);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE_BRICKS);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.GRANITE_SLAB, Blocks.GRANITE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.ANDESITE_SLAB, Blocks.ANDESITE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICKS);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.DIORITE_SLAB, Blocks.DIORITE);
        RecipesProvider.offerWallRecipe(consumer, Blocks.BRICK_WALL, Blocks.BRICKS);
        RecipesProvider.offerWallRecipe(consumer, Blocks.PRISMARINE_WALL, Blocks.PRISMARINE);
        RecipesProvider.offerWallRecipe(consumer, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
        RecipesProvider.offerWallRecipe(consumer, Blocks.MOSSY_STONE_BRICK_WALL, Blocks.MOSSY_STONE_BRICKS);
        RecipesProvider.offerWallRecipe(consumer, Blocks.GRANITE_WALL, Blocks.GRANITE);
        RecipesProvider.offerWallRecipe(consumer, Blocks.STONE_BRICK_WALL, Blocks.STONE_BRICKS);
        RecipesProvider.offerWallRecipe(consumer, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_BRICKS);
        RecipesProvider.offerWallRecipe(consumer, Blocks.ANDESITE_WALL, Blocks.ANDESITE);
        RecipesProvider.offerWallRecipe(consumer, Blocks.RED_NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICKS);
        RecipesProvider.offerWallRecipe(consumer, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
        RecipesProvider.offerWallRecipe(consumer, Blocks.END_STONE_BRICK_WALL, Blocks.END_STONE_BRICKS);
        RecipesProvider.offerWallRecipe(consumer, Blocks.DIORITE_WALL, Blocks.DIORITE);
        ShapelessRecipeJsonFactory.create(Items.CREEPER_BANNER_PATTERN).input(Items.PAPER).input(Items.CREEPER_HEAD).criterion("has_creeper_head", RecipesProvider.conditionsFromItem(Items.CREEPER_HEAD)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.SKULL_BANNER_PATTERN).input(Items.PAPER).input(Items.WITHER_SKELETON_SKULL).criterion("has_wither_skeleton_skull", RecipesProvider.conditionsFromItem(Items.WITHER_SKELETON_SKULL)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.FLOWER_BANNER_PATTERN).input(Items.PAPER).input(Blocks.OXEYE_DAISY).criterion("has_oxeye_daisy", RecipesProvider.conditionsFromItem(Blocks.OXEYE_DAISY)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.MOJANG_BANNER_PATTERN).input(Items.PAPER).input(Items.ENCHANTED_GOLDEN_APPLE).criterion("has_enchanted_golden_apple", RecipesProvider.conditionsFromItem(Items.ENCHANTED_GOLDEN_APPLE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SCAFFOLDING, 6).input(Character.valueOf('~'), Items.STRING).input(Character.valueOf('I'), Blocks.BAMBOO).pattern("I~I").pattern("I I").pattern("I I").criterion("has_bamboo", RecipesProvider.conditionsFromItem(Blocks.BAMBOO)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.GRINDSTONE).input(Character.valueOf('I'), Items.STICK).input(Character.valueOf('-'), Blocks.STONE_SLAB).input(Character.valueOf('#'), ItemTags.PLANKS).pattern("I-I").pattern("# #").criterion("has_stone_slab", RecipesProvider.conditionsFromItem(Blocks.STONE_SLAB)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.BLAST_FURNACE).input(Character.valueOf('#'), Blocks.SMOOTH_STONE).input(Character.valueOf('X'), Blocks.FURNACE).input(Character.valueOf('I'), Items.IRON_INGOT).pattern("III").pattern("IXI").pattern("###").criterion("has_smooth_stone", RecipesProvider.conditionsFromItem(Blocks.SMOOTH_STONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SMOKER).input(Character.valueOf('#'), ItemTags.LOGS).input(Character.valueOf('X'), Blocks.FURNACE).pattern(" # ").pattern("#X#").pattern(" # ").criterion("has_furnace", RecipesProvider.conditionsFromItem(Blocks.FURNACE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.CARTOGRAPHY_TABLE).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('@'), Items.PAPER).pattern("@@").pattern("##").pattern("##").criterion("has_paper", RecipesProvider.conditionsFromItem(Items.PAPER)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.SMITHING_TABLE).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('@'), Items.IRON_INGOT).pattern("@@").pattern("##").pattern("##").criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.FLETCHING_TABLE).input(Character.valueOf('#'), ItemTags.PLANKS).input(Character.valueOf('@'), Items.FLINT).pattern("@@").pattern("##").pattern("##").criterion("has_flint", RecipesProvider.conditionsFromItem(Items.FLINT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.STONECUTTER).input(Character.valueOf('I'), Items.IRON_INGOT).input(Character.valueOf('#'), Blocks.STONE).pattern(" I ").pattern("###").criterion("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.LODESTONE).input(Character.valueOf('S'), Items.CHISELED_STONE_BRICKS).input(Character.valueOf('#'), Items.NETHERITE_INGOT).pattern("SSS").pattern("S#S").pattern("SSS").criterion("has_netherite_ingot", RecipesProvider.conditionsFromItem(Items.NETHERITE_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.NETHERITE_BLOCK).input(Character.valueOf('#'), Items.NETHERITE_INGOT).pattern("###").pattern("###").pattern("###").criterion("has_netherite_ingot", RecipesProvider.conditionsFromItem(Items.NETHERITE_INGOT)).offerTo(consumer);
        ShapelessRecipeJsonFactory.create(Items.NETHERITE_INGOT, 9).input(Blocks.NETHERITE_BLOCK).group("netherite_ingot").criterion("has_netherite_block", RecipesProvider.conditionsFromItem(Blocks.NETHERITE_BLOCK)).offerTo(consumer, "netherite_ingot_from_netherite_block");
        ShapelessRecipeJsonFactory.create(Items.NETHERITE_INGOT).input(Items.NETHERITE_SCRAP, 4).input(Items.GOLD_INGOT, 4).group("netherite_ingot").criterion("has_netherite_scrap", RecipesProvider.conditionsFromItem(Items.NETHERITE_SCRAP)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.RESPAWN_ANCHOR).input(Character.valueOf('O'), Blocks.CRYING_OBSIDIAN).input(Character.valueOf('G'), Blocks.GLOWSTONE).pattern("OOO").pattern("GGG").pattern("OOO").criterion("has_obsidian", RecipesProvider.conditionsFromItem(Blocks.CRYING_OBSIDIAN)).offerTo(consumer);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE);
        RecipesProvider.offerStonecutterRecipe(consumer, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICKS);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE);
        RecipesProvider.offerSlabRecipe(consumer, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICKS);
        RecipesProvider.offerPolishedStoneRecipe(consumer, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
        RecipesProvider.offerPolishedStoneRecipe(consumer, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE);
        RecipesProvider.createChiseledBlockRecipe(Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_SLAB).criterion("has_polished_blackstone", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).offerTo(consumer);
        RecipesProvider.offerWallRecipe(consumer, Blocks.BLACKSTONE_WALL, Blocks.BLACKSTONE);
        RecipesProvider.offerWallRecipe(consumer, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE);
        RecipesProvider.offerWallRecipe(consumer, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS);
        ShapelessRecipeJsonFactory.create(Blocks.POLISHED_BLACKSTONE_BUTTON).input(Blocks.POLISHED_BLACKSTONE).criterion("has_polished_blackstone", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).offerTo(consumer);
        RecipesProvider.createPressurePlateRecipe(consumer, (ItemConvertible)Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, (ItemConvertible)Blocks.POLISHED_BLACKSTONE);
        ShapedRecipeJsonFactory.create(Blocks.CHAIN).input(Character.valueOf('I'), Items.IRON_INGOT).input(Character.valueOf('N'), Items.IRON_NUGGET).pattern("N").pattern("I").pattern("N").criterion("has_iron_nugget", RecipesProvider.conditionsFromItem(Items.IRON_NUGGET)).criterion("has_iron_ingot", RecipesProvider.conditionsFromItem(Items.IRON_INGOT)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Items.CANDLE).input(Character.valueOf('S'), Items.STRING).input(Character.valueOf('H'), Items.HONEYCOMB).pattern("S").pattern("H").criterion("has_string", RecipesProvider.conditionsFromItem(Items.STRING)).criterion("has_honeycomb", RecipesProvider.conditionsFromItem(Items.HONEYCOMB)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.TINTED_GLASS, 2).input(Character.valueOf('G'), Blocks.GLASS).input(Character.valueOf('S'), Items.AMETHYST_SHARD).pattern(" S ").pattern("SGS").pattern(" S ").criterion("has_amethyst_shard", RecipesProvider.conditionsFromItem(Items.AMETHYST_SHARD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(Blocks.AMETHYST_BLOCK).input(Character.valueOf('S'), Items.AMETHYST_SHARD).pattern("SS").pattern("SS").criterion("has_amethyst_shard", RecipesProvider.conditionsFromItem(Items.AMETHYST_SHARD)).offerTo(consumer);
        ComplexRecipeJsonFactory.create(RecipeSerializer.ARMOR_DYE).offerTo(consumer, "armor_dye");
        ComplexRecipeJsonFactory.create(RecipeSerializer.BANNER_DUPLICATE).offerTo(consumer, "banner_duplicate");
        ComplexRecipeJsonFactory.create(RecipeSerializer.BOOK_CLONING).offerTo(consumer, "book_cloning");
        ShapedRecipeJsonFactory.create(Items.BUNDLE).input(Character.valueOf('#'), Items.RABBIT_HIDE).input(Character.valueOf('-'), Items.STRING).pattern("-#-").pattern("# #").pattern("###").criterion("has_string", RecipesProvider.conditionsFromItem(Items.STRING)).offerTo(consumer);
        ComplexRecipeJsonFactory.create(RecipeSerializer.FIREWORK_ROCKET).offerTo(consumer, "firework_rocket");
        ComplexRecipeJsonFactory.create(RecipeSerializer.FIREWORK_STAR).offerTo(consumer, "firework_star");
        ComplexRecipeJsonFactory.create(RecipeSerializer.FIREWORK_STAR_FADE).offerTo(consumer, "firework_star_fade");
        ComplexRecipeJsonFactory.create(RecipeSerializer.MAP_CLONING).offerTo(consumer, "map_cloning");
        ComplexRecipeJsonFactory.create(RecipeSerializer.MAP_EXTENDING).offerTo(consumer, "map_extending");
        ComplexRecipeJsonFactory.create(RecipeSerializer.REPAIR_ITEM).offerTo(consumer, "repair_item");
        ComplexRecipeJsonFactory.create(RecipeSerializer.SHIELD_DECORATION).offerTo(consumer, "shield_decoration");
        ComplexRecipeJsonFactory.create(RecipeSerializer.SHULKER_BOX).offerTo(consumer, "shulker_box_coloring");
        ComplexRecipeJsonFactory.create(RecipeSerializer.TIPPED_ARROW).offerTo(consumer, "tipped_arrow");
        ComplexRecipeJsonFactory.create(RecipeSerializer.SUSPICIOUS_STEW).offerTo(consumer, "suspicious_stew");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.POTATO), Items.BAKED_POTATO, 0.35f, 200).criterion("has_potato", RecipesProvider.conditionsFromItem(Items.POTATO)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.CLAY_BALL), Items.BRICK, 0.3f, 200).criterion("has_clay_ball", RecipesProvider.conditionsFromItem(Items.CLAY_BALL)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.LOGS_THAT_BURN), Items.CHARCOAL, 0.15f, 200).criterion("has_log", RecipesProvider.conditionsFromTag(ItemTags.LOGS_THAT_BURN)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.CHORUS_FRUIT), Items.POPPED_CHORUS_FRUIT, 0.1f, 200).criterion("has_chorus_fruit", RecipesProvider.conditionsFromItem(Items.CHORUS_FRUIT)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1f, 200).criterion("has_coal_ore", RecipesProvider.conditionsFromItem(Blocks.COAL_ORE)).offerTo(consumer, "coal_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.BEEF), Items.COOKED_BEEF, 0.35f, 200).criterion("has_beef", RecipesProvider.conditionsFromItem(Items.BEEF)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35f, 200).criterion("has_chicken", RecipesProvider.conditionsFromItem(Items.CHICKEN)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.COD), Items.COOKED_COD, 0.35f, 200).criterion("has_cod", RecipesProvider.conditionsFromItem(Items.COD)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.KELP), Items.DRIED_KELP, 0.1f, 200).criterion("has_kelp", RecipesProvider.conditionsFromItem(Blocks.KELP)).offerTo(consumer, "dried_kelp_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.SALMON), Items.COOKED_SALMON, 0.35f, 200).criterion("has_salmon", RecipesProvider.conditionsFromItem(Items.SALMON)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.MUTTON), Items.COOKED_MUTTON, 0.35f, 200).criterion("has_mutton", RecipesProvider.conditionsFromItem(Items.MUTTON)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35f, 200).criterion("has_porkchop", RecipesProvider.conditionsFromItem(Items.PORKCHOP)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.RABBIT), Items.COOKED_RABBIT, 0.35f, 200).criterion("has_rabbit", RecipesProvider.conditionsFromItem(Items.RABBIT)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0f, 200).criterion("has_diamond_ore", RecipesProvider.conditionsFromItem(Blocks.DIAMOND_ORE)).offerTo(consumer, "diamond_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2f, 200).criterion("has_lapis_ore", RecipesProvider.conditionsFromItem(Blocks.LAPIS_ORE)).offerTo(consumer, "lapis_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0f, 200).criterion("has_emerald_ore", RecipesProvider.conditionsFromItem(Blocks.EMERALD_ORE)).offerTo(consumer, "emerald_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.SAND), Blocks.GLASS.asItem(), 0.1f, 200).criterion("has_sand", RecipesProvider.conditionsFromTag(ItemTags.SAND)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.GOLD_ORES), Items.GOLD_INGOT, 1.0f, 200).criterion("has_gold_ore", RecipesProvider.conditionsFromTag(ItemTags.GOLD_ORES)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.SEA_PICKLE.asItem()), Items.LIME_DYE, 0.1f, 200).criterion("has_sea_pickle", RecipesProvider.conditionsFromItem(Blocks.SEA_PICKLE)).offerTo(consumer, "lime_dye_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.CACTUS.asItem()), Items.GREEN_DYE, 1.0f, 200).criterion("has_cactus", RecipesProvider.conditionsFromItem(Blocks.CACTUS)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1f, 200).criterion("has_golden_pickaxe", RecipesProvider.conditionsFromItem(Items.GOLDEN_PICKAXE)).criterion("has_golden_shovel", RecipesProvider.conditionsFromItem(Items.GOLDEN_SHOVEL)).criterion("has_golden_axe", RecipesProvider.conditionsFromItem(Items.GOLDEN_AXE)).criterion("has_golden_hoe", RecipesProvider.conditionsFromItem(Items.GOLDEN_HOE)).criterion("has_golden_sword", RecipesProvider.conditionsFromItem(Items.GOLDEN_SWORD)).criterion("has_golden_helmet", RecipesProvider.conditionsFromItem(Items.GOLDEN_HELMET)).criterion("has_golden_chestplate", RecipesProvider.conditionsFromItem(Items.GOLDEN_CHESTPLATE)).criterion("has_golden_leggings", RecipesProvider.conditionsFromItem(Items.GOLDEN_LEGGINGS)).criterion("has_golden_boots", RecipesProvider.conditionsFromItem(Items.GOLDEN_BOOTS)).criterion("has_golden_horse_armor", RecipesProvider.conditionsFromItem(Items.GOLDEN_HORSE_ARMOR)).offerTo(consumer, "gold_nugget_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1f, 200).criterion("has_iron_pickaxe", RecipesProvider.conditionsFromItem(Items.IRON_PICKAXE)).criterion("has_iron_shovel", RecipesProvider.conditionsFromItem(Items.IRON_SHOVEL)).criterion("has_iron_axe", RecipesProvider.conditionsFromItem(Items.IRON_AXE)).criterion("has_iron_hoe", RecipesProvider.conditionsFromItem(Items.IRON_HOE)).criterion("has_iron_sword", RecipesProvider.conditionsFromItem(Items.IRON_SWORD)).criterion("has_iron_helmet", RecipesProvider.conditionsFromItem(Items.IRON_HELMET)).criterion("has_iron_chestplate", RecipesProvider.conditionsFromItem(Items.IRON_CHESTPLATE)).criterion("has_iron_leggings", RecipesProvider.conditionsFromItem(Items.IRON_LEGGINGS)).criterion("has_iron_boots", RecipesProvider.conditionsFromItem(Items.IRON_BOOTS)).criterion("has_iron_horse_armor", RecipesProvider.conditionsFromItem(Items.IRON_HORSE_ARMOR)).criterion("has_chainmail_helmet", RecipesProvider.conditionsFromItem(Items.CHAINMAIL_HELMET)).criterion("has_chainmail_chestplate", RecipesProvider.conditionsFromItem(Items.CHAINMAIL_CHESTPLATE)).criterion("has_chainmail_leggings", RecipesProvider.conditionsFromItem(Items.CHAINMAIL_LEGGINGS)).criterion("has_chainmail_boots", RecipesProvider.conditionsFromItem(Items.CHAINMAIL_BOOTS)).offerTo(consumer, "iron_nugget_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7f, 200).criterion("has_iron_ore", RecipesProvider.conditionsFromItem(Blocks.IRON_ORE.asItem())).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.COPPER_ORE), Items.COPPER_INGOT, 0.7f, 200).criterion("has_copper_ore", RecipesProvider.conditionsFromItem(Blocks.COPPER_ORE.asItem())).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.CLAY), Blocks.TERRACOTTA.asItem(), 0.35f, 200).criterion("has_clay_block", RecipesProvider.conditionsFromItem(Blocks.CLAY)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.NETHERRACK), Items.NETHER_BRICK, 0.1f, 200).criterion("has_netherrack", RecipesProvider.conditionsFromItem(Blocks.NETHERRACK)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2f, 200).criterion("has_nether_quartz_ore", RecipesProvider.conditionsFromItem(Blocks.NETHER_QUARTZ_ORE)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7f, 200).criterion("has_redstone_ore", RecipesProvider.conditionsFromItem(Blocks.REDSTONE_ORE)).offerTo(consumer, "redstone_from_smelting");
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.WET_SPONGE), Blocks.SPONGE.asItem(), 0.15f, 200).criterion("has_wet_sponge", RecipesProvider.conditionsFromItem(Blocks.WET_SPONGE)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.STONE.asItem(), 0.1f, 200).criterion("has_cobblestone", RecipesProvider.conditionsFromItem(Blocks.COBBLESTONE)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.STONE), Blocks.SMOOTH_STONE.asItem(), 0.1f, 200).criterion("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SMOOTH_SANDSTONE.asItem(), 0.1f, 200).criterion("has_sandstone", RecipesProvider.conditionsFromItem(Blocks.SANDSTONE)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1f, 200).criterion("has_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.SMOOTH_QUARTZ.asItem(), 0.1f, 200).criterion("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1f, 200).criterion("has_stone_bricks", RecipesProvider.conditionsFromItem(Blocks.STONE_BRICKS)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.BLACK_TERRACOTTA), Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_black_terracotta", RecipesProvider.conditionsFromItem(Blocks.BLACK_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.BLUE_TERRACOTTA), Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_blue_terracotta", RecipesProvider.conditionsFromItem(Blocks.BLUE_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.BROWN_TERRACOTTA), Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_brown_terracotta", RecipesProvider.conditionsFromItem(Blocks.BROWN_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.CYAN_TERRACOTTA), Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_cyan_terracotta", RecipesProvider.conditionsFromItem(Blocks.CYAN_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.GRAY_TERRACOTTA), Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_gray_terracotta", RecipesProvider.conditionsFromItem(Blocks.GRAY_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.GREEN_TERRACOTTA), Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_green_terracotta", RecipesProvider.conditionsFromItem(Blocks.GREEN_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LIGHT_BLUE_TERRACOTTA), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_light_blue_terracotta", RecipesProvider.conditionsFromItem(Blocks.LIGHT_BLUE_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LIGHT_GRAY_TERRACOTTA), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_light_gray_terracotta", RecipesProvider.conditionsFromItem(Blocks.LIGHT_GRAY_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LIME_TERRACOTTA), Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_lime_terracotta", RecipesProvider.conditionsFromItem(Blocks.LIME_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.MAGENTA_TERRACOTTA), Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_magenta_terracotta", RecipesProvider.conditionsFromItem(Blocks.MAGENTA_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.ORANGE_TERRACOTTA), Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_orange_terracotta", RecipesProvider.conditionsFromItem(Blocks.ORANGE_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.PINK_TERRACOTTA), Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_pink_terracotta", RecipesProvider.conditionsFromItem(Blocks.PINK_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.PURPLE_TERRACOTTA), Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_purple_terracotta", RecipesProvider.conditionsFromItem(Blocks.PURPLE_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.RED_TERRACOTTA), Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_red_terracotta", RecipesProvider.conditionsFromItem(Blocks.RED_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.WHITE_TERRACOTTA), Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_white_terracotta", RecipesProvider.conditionsFromItem(Blocks.WHITE_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.YELLOW_TERRACOTTA), Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1f, 200).criterion("has_yellow_terracotta", RecipesProvider.conditionsFromItem(Blocks.YELLOW_TERRACOTTA)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0f, 200).criterion("has_ancient_debris", RecipesProvider.conditionsFromItem(Blocks.ANCIENT_DEBRIS)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.asItem(), 0.1f, 200).criterion("has_blackstone_bricks", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).offerTo(consumer);
        CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.CRACKED_NETHER_BRICKS.asItem(), 0.1f, 200).criterion("has_nether_bricks", RecipesProvider.conditionsFromItem(Blocks.NETHER_BRICKS)).offerTo(consumer);
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7f, 100).criterion("has_iron_ore", RecipesProvider.conditionsFromItem(Blocks.IRON_ORE.asItem())).offerTo(consumer, "iron_ingot_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.COPPER_ORE), Items.COPPER_INGOT, 0.7f, 100).criterion("has_copper_ore", RecipesProvider.conditionsFromItem(Blocks.COPPER_ORE.asItem())).offerTo(consumer, "copper_ingot_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.fromTag(ItemTags.GOLD_ORES), Items.GOLD_INGOT, 1.0f, 100).criterion("has_gold_ore", RecipesProvider.conditionsFromTag(ItemTags.GOLD_ORES)).offerTo(consumer, "gold_ingot_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0f, 100).criterion("has_diamond_ore", RecipesProvider.conditionsFromItem(Blocks.DIAMOND_ORE)).offerTo(consumer, "diamond_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2f, 100).criterion("has_lapis_ore", RecipesProvider.conditionsFromItem(Blocks.LAPIS_ORE)).offerTo(consumer, "lapis_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7f, 100).criterion("has_redstone_ore", RecipesProvider.conditionsFromItem(Blocks.REDSTONE_ORE)).offerTo(consumer, "redstone_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1f, 100).criterion("has_coal_ore", RecipesProvider.conditionsFromItem(Blocks.COAL_ORE)).offerTo(consumer, "coal_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0f, 100).criterion("has_emerald_ore", RecipesProvider.conditionsFromItem(Blocks.EMERALD_ORE)).offerTo(consumer, "emerald_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2f, 100).criterion("has_nether_quartz_ore", RecipesProvider.conditionsFromItem(Blocks.NETHER_QUARTZ_ORE)).offerTo(consumer, "quartz_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), Items.GOLD_NUGGET, 0.1f, 100).criterion("has_golden_pickaxe", RecipesProvider.conditionsFromItem(Items.GOLDEN_PICKAXE)).criterion("has_golden_shovel", RecipesProvider.conditionsFromItem(Items.GOLDEN_SHOVEL)).criterion("has_golden_axe", RecipesProvider.conditionsFromItem(Items.GOLDEN_AXE)).criterion("has_golden_hoe", RecipesProvider.conditionsFromItem(Items.GOLDEN_HOE)).criterion("has_golden_sword", RecipesProvider.conditionsFromItem(Items.GOLDEN_SWORD)).criterion("has_golden_helmet", RecipesProvider.conditionsFromItem(Items.GOLDEN_HELMET)).criterion("has_golden_chestplate", RecipesProvider.conditionsFromItem(Items.GOLDEN_CHESTPLATE)).criterion("has_golden_leggings", RecipesProvider.conditionsFromItem(Items.GOLDEN_LEGGINGS)).criterion("has_golden_boots", RecipesProvider.conditionsFromItem(Items.GOLDEN_BOOTS)).criterion("has_golden_horse_armor", RecipesProvider.conditionsFromItem(Items.GOLDEN_HORSE_ARMOR)).offerTo(consumer, "gold_nugget_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR, Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS), Items.IRON_NUGGET, 0.1f, 100).criterion("has_iron_pickaxe", RecipesProvider.conditionsFromItem(Items.IRON_PICKAXE)).criterion("has_iron_shovel", RecipesProvider.conditionsFromItem(Items.IRON_SHOVEL)).criterion("has_iron_axe", RecipesProvider.conditionsFromItem(Items.IRON_AXE)).criterion("has_iron_hoe", RecipesProvider.conditionsFromItem(Items.IRON_HOE)).criterion("has_iron_sword", RecipesProvider.conditionsFromItem(Items.IRON_SWORD)).criterion("has_iron_helmet", RecipesProvider.conditionsFromItem(Items.IRON_HELMET)).criterion("has_iron_chestplate", RecipesProvider.conditionsFromItem(Items.IRON_CHESTPLATE)).criterion("has_iron_leggings", RecipesProvider.conditionsFromItem(Items.IRON_LEGGINGS)).criterion("has_iron_boots", RecipesProvider.conditionsFromItem(Items.IRON_BOOTS)).criterion("has_iron_horse_armor", RecipesProvider.conditionsFromItem(Items.IRON_HORSE_ARMOR)).criterion("has_chainmail_helmet", RecipesProvider.conditionsFromItem(Items.CHAINMAIL_HELMET)).criterion("has_chainmail_chestplate", RecipesProvider.conditionsFromItem(Items.CHAINMAIL_CHESTPLATE)).criterion("has_chainmail_leggings", RecipesProvider.conditionsFromItem(Items.CHAINMAIL_LEGGINGS)).criterion("has_chainmail_boots", RecipesProvider.conditionsFromItem(Items.CHAINMAIL_BOOTS)).offerTo(consumer, "iron_nugget_from_blasting");
        CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0f, 100).criterion("has_ancient_debris", RecipesProvider.conditionsFromItem(Blocks.ANCIENT_DEBRIS)).offerTo(consumer, "netherite_scrap_from_blasting");
        RecipesProvider.generateCookingRecipes(consumer, "smoking", RecipeSerializer.SMOKING, 100);
        RecipesProvider.generateCookingRecipes(consumer, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, 600);
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_SLAB, 2).create("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer, "stone_slab_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_STAIRS).create("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer, "stone_stairs_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICKS).create("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer, "stone_bricks_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICK_SLAB, 2).create("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer, "stone_brick_slab_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICK_STAIRS).create("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer, "stone_brick_stairs_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.CHISELED_STONE_BRICKS).create("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer, "chiseled_stone_bricks_stone_from_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICK_WALL).create("has_stone", RecipesProvider.conditionsFromItem(Blocks.STONE)).offerTo(consumer, "stone_brick_walls_from_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE).create("has_sandstone", RecipesProvider.conditionsFromItem(Blocks.SANDSTONE)).offerTo(consumer, "cut_sandstone_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SANDSTONE_SLAB, 2).create("has_sandstone", RecipesProvider.conditionsFromItem(Blocks.SANDSTONE)).offerTo(consumer, "sandstone_slab_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2).create("has_sandstone", RecipesProvider.conditionsFromItem(Blocks.SANDSTONE)).offerTo(consumer, "cut_sandstone_slab_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.CUT_SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2).create("has_cut_sandstone", RecipesProvider.conditionsFromItem(Blocks.SANDSTONE)).offerTo(consumer, "cut_sandstone_slab_from_cut_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SANDSTONE_STAIRS).create("has_sandstone", RecipesProvider.conditionsFromItem(Blocks.SANDSTONE)).offerTo(consumer, "sandstone_stairs_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SANDSTONE_WALL).create("has_sandstone", RecipesProvider.conditionsFromItem(Blocks.SANDSTONE)).offerTo(consumer, "sandstone_wall_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.CHISELED_SANDSTONE).create("has_sandstone", RecipesProvider.conditionsFromItem(Blocks.SANDSTONE)).offerTo(consumer, "chiseled_sandstone_from_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE).create("has_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).offerTo(consumer, "cut_red_sandstone_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_SLAB, 2).create("has_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).offerTo(consumer, "red_sandstone_slab_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2).create("has_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).offerTo(consumer, "cut_red_sandstone_slab_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.CUT_RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2).create("has_cut_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).offerTo(consumer, "cut_red_sandstone_slab_from_cut_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_STAIRS).create("has_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).offerTo(consumer, "red_sandstone_stairs_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_WALL).create("has_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).offerTo(consumer, "red_sandstone_wall_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.CHISELED_RED_SANDSTONE).create("has_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.RED_SANDSTONE)).offerTo(consumer, "chiseled_red_sandstone_from_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_SLAB, 2).create("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).offerTo(consumer, "quartz_slab_from_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_STAIRS).create("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).offerTo(consumer, "quartz_stairs_from_quartz_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_PILLAR).create("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).offerTo(consumer, "quartz_pillar_from_quartz_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.CHISELED_QUARTZ_BLOCK).create("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).offerTo(consumer, "chiseled_quartz_block_from_quartz_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_BRICKS).create("has_quartz_block", RecipesProvider.conditionsFromItem(Blocks.QUARTZ_BLOCK)).offerTo(consumer, "quartz_bricks_from_quartz_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_STAIRS).create("has_cobblestone", RecipesProvider.conditionsFromItem(Blocks.COBBLESTONE)).offerTo(consumer, "cobblestone_stairs_from_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_SLAB, 2).create("has_cobblestone", RecipesProvider.conditionsFromItem(Blocks.COBBLESTONE)).offerTo(consumer, "cobblestone_slab_from_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_WALL).create("has_cobblestone", RecipesProvider.conditionsFromItem(Blocks.COBBLESTONE)).offerTo(consumer, "cobblestone_wall_from_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_SLAB, 2).create("has_stone_bricks", RecipesProvider.conditionsFromItem(Blocks.STONE_BRICKS)).offerTo(consumer, "stone_brick_slab_from_stone_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_STAIRS).create("has_stone_bricks", RecipesProvider.conditionsFromItem(Blocks.STONE_BRICKS)).offerTo(consumer, "stone_brick_stairs_from_stone_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_WALL).create("has_stone_bricks", RecipesProvider.conditionsFromItem(Blocks.STONE_BRICKS)).offerTo(consumer, "stone_brick_wall_from_stone_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.CHISELED_STONE_BRICKS).create("has_stone_bricks", RecipesProvider.conditionsFromItem(Blocks.STONE_BRICKS)).offerTo(consumer, "chiseled_stone_bricks_from_stone_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BRICKS), Blocks.BRICK_SLAB, 2).create("has_bricks", RecipesProvider.conditionsFromItem(Blocks.BRICKS)).offerTo(consumer, "brick_slab_from_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BRICKS), Blocks.BRICK_STAIRS).create("has_bricks", RecipesProvider.conditionsFromItem(Blocks.BRICKS)).offerTo(consumer, "brick_stairs_from_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BRICKS), Blocks.BRICK_WALL).create("has_bricks", RecipesProvider.conditionsFromItem(Blocks.BRICKS)).offerTo(consumer, "brick_wall_from_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_SLAB, 2).create("has_nether_bricks", RecipesProvider.conditionsFromItem(Blocks.NETHER_BRICKS)).offerTo(consumer, "nether_brick_slab_from_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_STAIRS).create("has_nether_bricks", RecipesProvider.conditionsFromItem(Blocks.NETHER_BRICKS)).offerTo(consumer, "nether_brick_stairs_from_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_WALL).create("has_nether_bricks", RecipesProvider.conditionsFromItem(Blocks.NETHER_BRICKS)).offerTo(consumer, "nether_brick_wall_from_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.CHISELED_NETHER_BRICKS).create("has_nether_bricks", RecipesProvider.conditionsFromItem(Blocks.NETHER_BRICKS)).offerTo(consumer, "chiseled_nether_bricks_from_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_SLAB, 2).create("has_nether_bricks", RecipesProvider.conditionsFromItem(Blocks.RED_NETHER_BRICKS)).offerTo(consumer, "red_nether_brick_slab_from_red_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_STAIRS).create("has_nether_bricks", RecipesProvider.conditionsFromItem(Blocks.RED_NETHER_BRICKS)).offerTo(consumer, "red_nether_brick_stairs_from_red_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_WALL).create("has_nether_bricks", RecipesProvider.conditionsFromItem(Blocks.RED_NETHER_BRICKS)).offerTo(consumer, "red_nether_brick_wall_from_red_nether_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_SLAB, 2).create("has_purpur_block", RecipesProvider.conditionsFromItem(Blocks.PURPUR_BLOCK)).offerTo(consumer, "purpur_slab_from_purpur_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_STAIRS).create("has_purpur_block", RecipesProvider.conditionsFromItem(Blocks.PURPUR_BLOCK)).offerTo(consumer, "purpur_stairs_from_purpur_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_PILLAR).create("has_purpur_block", RecipesProvider.conditionsFromItem(Blocks.PURPUR_BLOCK)).offerTo(consumer, "purpur_pillar_from_purpur_block_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE), Blocks.PRISMARINE_SLAB, 2).create("has_prismarine", RecipesProvider.conditionsFromItem(Blocks.PRISMARINE)).offerTo(consumer, "prismarine_slab_from_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE), Blocks.PRISMARINE_STAIRS).create("has_prismarine", RecipesProvider.conditionsFromItem(Blocks.PRISMARINE)).offerTo(consumer, "prismarine_stairs_from_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE), Blocks.PRISMARINE_WALL).create("has_prismarine", RecipesProvider.conditionsFromItem(Blocks.PRISMARINE)).offerTo(consumer, "prismarine_wall_from_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_SLAB, 2).create("has_prismarine_brick", RecipesProvider.conditionsFromItem(Blocks.PRISMARINE_BRICKS)).offerTo(consumer, "prismarine_brick_slab_from_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_STAIRS).create("has_prismarine_brick", RecipesProvider.conditionsFromItem(Blocks.PRISMARINE_BRICKS)).offerTo(consumer, "prismarine_brick_stairs_from_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_SLAB, 2).create("has_dark_prismarine", RecipesProvider.conditionsFromItem(Blocks.DARK_PRISMARINE)).offerTo(consumer, "dark_prismarine_slab_from_dark_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_STAIRS).create("has_dark_prismarine", RecipesProvider.conditionsFromItem(Blocks.DARK_PRISMARINE)).offerTo(consumer, "dark_prismarine_stairs_from_dark_prismarine_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.ANDESITE_SLAB, 2).create("has_andesite", RecipesProvider.conditionsFromItem(Blocks.ANDESITE)).offerTo(consumer, "andesite_slab_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.ANDESITE_STAIRS).create("has_andesite", RecipesProvider.conditionsFromItem(Blocks.ANDESITE)).offerTo(consumer, "andesite_stairs_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.ANDESITE_WALL).create("has_andesite", RecipesProvider.conditionsFromItem(Blocks.ANDESITE)).offerTo(consumer, "andesite_wall_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE).create("has_andesite", RecipesProvider.conditionsFromItem(Blocks.ANDESITE)).offerTo(consumer, "polished_andesite_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2).create("has_andesite", RecipesProvider.conditionsFromItem(Blocks.ANDESITE)).offerTo(consumer, "polished_andesite_slab_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS).create("has_andesite", RecipesProvider.conditionsFromItem(Blocks.ANDESITE)).offerTo(consumer, "polished_andesite_stairs_from_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2).create("has_polished_andesite", RecipesProvider.conditionsFromItem(Blocks.POLISHED_ANDESITE)).offerTo(consumer, "polished_andesite_slab_from_polished_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS).create("has_polished_andesite", RecipesProvider.conditionsFromItem(Blocks.POLISHED_ANDESITE)).offerTo(consumer, "polished_andesite_stairs_from_polished_andesite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BASALT), Blocks.POLISHED_BASALT).create("has_basalt", RecipesProvider.conditionsFromItem(Blocks.BASALT)).offerTo(consumer, "polished_basalt_from_basalt_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.GRANITE_SLAB, 2).create("has_granite", RecipesProvider.conditionsFromItem(Blocks.GRANITE)).offerTo(consumer, "granite_slab_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.GRANITE_STAIRS).create("has_granite", RecipesProvider.conditionsFromItem(Blocks.GRANITE)).offerTo(consumer, "granite_stairs_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.GRANITE_WALL).create("has_granite", RecipesProvider.conditionsFromItem(Blocks.GRANITE)).offerTo(consumer, "granite_wall_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE).create("has_granite", RecipesProvider.conditionsFromItem(Blocks.GRANITE)).offerTo(consumer, "polished_granite_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2).create("has_granite", RecipesProvider.conditionsFromItem(Blocks.GRANITE)).offerTo(consumer, "polished_granite_slab_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE_STAIRS).create("has_granite", RecipesProvider.conditionsFromItem(Blocks.GRANITE)).offerTo(consumer, "polished_granite_stairs_from_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2).create("has_polished_granite", RecipesProvider.conditionsFromItem(Blocks.POLISHED_GRANITE)).offerTo(consumer, "polished_granite_slab_from_polished_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_STAIRS).create("has_polished_granite", RecipesProvider.conditionsFromItem(Blocks.POLISHED_GRANITE)).offerTo(consumer, "polished_granite_stairs_from_polished_granite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.DIORITE_SLAB, 2).create("has_diorite", RecipesProvider.conditionsFromItem(Blocks.DIORITE)).offerTo(consumer, "diorite_slab_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.DIORITE_STAIRS).create("has_diorite", RecipesProvider.conditionsFromItem(Blocks.DIORITE)).offerTo(consumer, "diorite_stairs_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.DIORITE_WALL).create("has_diorite", RecipesProvider.conditionsFromItem(Blocks.DIORITE)).offerTo(consumer, "diorite_wall_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE).create("has_diorite", RecipesProvider.conditionsFromItem(Blocks.DIORITE)).offerTo(consumer, "polished_diorite_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2).create("has_diorite", RecipesProvider.conditionsFromItem(Blocks.POLISHED_DIORITE)).offerTo(consumer, "polished_diorite_slab_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE_STAIRS).create("has_diorite", RecipesProvider.conditionsFromItem(Blocks.POLISHED_DIORITE)).offerTo(consumer, "polished_diorite_stairs_from_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2).create("has_polished_diorite", RecipesProvider.conditionsFromItem(Blocks.POLISHED_DIORITE)).offerTo(consumer, "polished_diorite_slab_from_polished_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_STAIRS).create("has_polished_diorite", RecipesProvider.conditionsFromItem(Blocks.POLISHED_DIORITE)).offerTo(consumer, "polished_diorite_stairs_from_polished_diorite_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_SLAB, 2).create("has_mossy_stone_bricks", RecipesProvider.conditionsFromItem(Blocks.MOSSY_STONE_BRICKS)).offerTo(consumer, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_STAIRS).create("has_mossy_stone_bricks", RecipesProvider.conditionsFromItem(Blocks.MOSSY_STONE_BRICKS)).offerTo(consumer, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_WALL).create("has_mossy_stone_bricks", RecipesProvider.conditionsFromItem(Blocks.MOSSY_STONE_BRICKS)).offerTo(consumer, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_SLAB, 2).create("has_mossy_cobblestone", RecipesProvider.conditionsFromItem(Blocks.MOSSY_COBBLESTONE)).offerTo(consumer, "mossy_cobblestone_slab_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_STAIRS).create("has_mossy_cobblestone", RecipesProvider.conditionsFromItem(Blocks.MOSSY_COBBLESTONE)).offerTo(consumer, "mossy_cobblestone_stairs_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_WALL).create("has_mossy_cobblestone", RecipesProvider.conditionsFromItem(Blocks.MOSSY_COBBLESTONE)).offerTo(consumer, "mossy_cobblestone_wall_from_mossy_cobblestone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_SLAB, 2).create("has_smooth_sandstone", RecipesProvider.conditionsFromItem(Blocks.SMOOTH_SANDSTONE)).offerTo(consumer, "smooth_sandstone_slab_from_smooth_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_STAIRS).create("has_mossy_cobblestone", RecipesProvider.conditionsFromItem(Blocks.SMOOTH_SANDSTONE)).offerTo(consumer, "smooth_sandstone_stairs_from_smooth_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_SLAB, 2).create("has_smooth_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.SMOOTH_RED_SANDSTONE)).offerTo(consumer, "smooth_red_sandstone_slab_from_smooth_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_STAIRS).create("has_smooth_red_sandstone", RecipesProvider.conditionsFromItem(Blocks.SMOOTH_RED_SANDSTONE)).offerTo(consumer, "smooth_red_sandstone_stairs_from_smooth_red_sandstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_SLAB, 2).create("has_smooth_quartz", RecipesProvider.conditionsFromItem(Blocks.SMOOTH_QUARTZ)).offerTo(consumer, "smooth_quartz_slab_from_smooth_quartz_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_STAIRS).create("has_smooth_quartz", RecipesProvider.conditionsFromItem(Blocks.SMOOTH_QUARTZ)).offerTo(consumer, "smooth_quartz_stairs_from_smooth_quartz_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_SLAB, 2).create("has_end_stone_brick", RecipesProvider.conditionsFromItem(Blocks.END_STONE_BRICKS)).offerTo(consumer, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_STAIRS).create("has_end_stone_brick", RecipesProvider.conditionsFromItem(Blocks.END_STONE_BRICKS)).offerTo(consumer, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_WALL).create("has_end_stone_brick", RecipesProvider.conditionsFromItem(Blocks.END_STONE_BRICKS)).offerTo(consumer, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICKS).create("has_end_stone", RecipesProvider.conditionsFromItem(Blocks.END_STONE)).offerTo(consumer, "end_stone_bricks_from_end_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_SLAB, 2).create("has_end_stone", RecipesProvider.conditionsFromItem(Blocks.END_STONE)).offerTo(consumer, "end_stone_brick_slab_from_end_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_STAIRS).create("has_end_stone", RecipesProvider.conditionsFromItem(Blocks.END_STONE)).offerTo(consumer, "end_stone_brick_stairs_from_end_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_WALL).create("has_end_stone", RecipesProvider.conditionsFromItem(Blocks.END_STONE)).offerTo(consumer, "end_stone_brick_wall_from_end_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_STONE), Blocks.SMOOTH_STONE_SLAB, 2).create("has_smooth_stone", RecipesProvider.conditionsFromItem(Blocks.SMOOTH_STONE)).offerTo(consumer, "smooth_stone_slab_from_smooth_stone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.BLACKSTONE_SLAB, 2).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "blackstone_slab_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.BLACKSTONE_STAIRS).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "blackstone_stairs_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.BLACKSTONE_WALL).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "blackstone_wall_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "polished_blackstone_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_WALL).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "polished_blackstone_wall_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_SLAB, 2).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "polished_blackstone_slab_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_STAIRS).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "polished_blackstone_stairs_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.CHISELED_POLISHED_BLACKSTONE).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "chiseled_polished_blackstone_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICKS).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "polished_blackstone_bricks_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "polished_blackstone_brick_slab_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "polished_blackstone_brick_stairs_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).create("has_blackstone", RecipesProvider.conditionsFromItem(Blocks.BLACKSTONE)).offerTo(consumer, "polished_blackstone_brick_wall_from_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_SLAB, 2).create("has_polished_blackstone", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).offerTo(consumer, "polished_blackstone_slab_from_polished_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_STAIRS).create("has_polished_blackstone", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).offerTo(consumer, "polished_blackstone_stairs_from_polished_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICKS).create("has_polished_blackstone", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).offerTo(consumer, "polished_blackstone_bricks_from_polished_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_WALL).create("has_polished_blackstone", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).offerTo(consumer, "polished_blackstone_wall_from_polished_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).create("has_polished_blackstone", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).offerTo(consumer, "polished_blackstone_brick_slab_from_polished_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).create("has_polished_blackstone", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).offerTo(consumer, "polished_blackstone_brick_stairs_from_polished_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).create("has_polished_blackstone", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).offerTo(consumer, "polished_blackstone_brick_wall_from_polished_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.CHISELED_POLISHED_BLACKSTONE).create("has_polished_blackstone", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).offerTo(consumer, "chiseled_polished_blackstone_from_polished_blackstone_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2).create("has_polished_blackstone_bricks", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).offerTo(consumer, "polished_blackstone_brick_slab_from_polished_blackstone_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).create("has_polished_blackstone_bricks", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).offerTo(consumer, "polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_WALL).create("has_polished_blackstone_bricks", RecipesProvider.conditionsFromItem(Blocks.POLISHED_BLACKSTONE_BRICKS)).offerTo(consumer, "polished_blackstone_brick_wall_from_polished_blackstone_bricks_stonecutting");
        RecipesProvider.offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
        RecipesProvider.offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
        RecipesProvider.offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
        RecipesProvider.offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
        RecipesProvider.offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
        RecipesProvider.offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
        RecipesProvider.offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        RecipesProvider.offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_HOE, Items.NETHERITE_HOE);
        RecipesProvider.offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
    }

    private static void offerNetheriteUpgradeRecipe(Consumer<RecipeJsonProvider> consumer, Item outputItem, Item inputItem) {
        SmithingRecipeJsonFactory.create(Ingredient.ofItems(outputItem), Ingredient.ofItems(Items.NETHERITE_INGOT), inputItem).criterion("has_netherite_ingot", RecipesProvider.conditionsFromItem(Items.NETHERITE_INGOT)).offerTo(consumer, Registry.ITEM.getId(inputItem.asItem()).getPath() + "_smithing");
    }

    private static void offerPlanksRecipe2(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, Tag<Item> inputItem) {
        ShapelessRecipeJsonFactory.create(outputItem, 4).input(inputItem).group("planks").criterion("has_log", RecipesProvider.conditionsFromTag(inputItem)).offerTo(consumer);
    }

    private static void offerPlanksRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, Tag<Item> inputItem) {
        ShapelessRecipeJsonFactory.create(outputItem, 4).input(inputItem).group("planks").criterion("has_logs", RecipesProvider.conditionsFromTag(inputItem)).offerTo(consumer);
    }

    private static void offerBarkBlockRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 3).input(Character.valueOf('#'), inputItem).pattern("##").pattern("##").group("bark").criterion("has_log", RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerBoatRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem).input(Character.valueOf('#'), inputItem).pattern("# #").pattern("###").group("boat").criterion("in_water", RecipesProvider.requireEnteringFluid(Blocks.WATER)).offerTo(consumer);
    }

    private static void offerButtonRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapelessRecipeJsonFactory.create(outputItem).input(inputItem).group("wooden_button").criterion("has_planks", RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerDoorRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 3).input(Character.valueOf('#'), inputItem).pattern("##").pattern("##").pattern("##").group("wooden_door").criterion("has_planks", RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerFenceRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 3).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('W'), inputItem).pattern("W#W").pattern("W#W").group("wooden_fence").criterion("has_planks", RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerFenceGateRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem).input(Character.valueOf('#'), Items.STICK).input(Character.valueOf('W'), inputItem).pattern("#W#").pattern("#W#").group("wooden_fence_gate").criterion("has_planks", RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerWoodenPressurePlateRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        RecipesProvider.createPressurePlateRecipe(outputItem, inputItem).group("wooden_pressure_plate").criterion("has_planks", RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void createPressurePlateRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        RecipesProvider.createPressurePlateRecipe(outputItem, inputItem).criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static ShapedRecipeJsonFactory createPressurePlateRecipe(ItemConvertible outputItem, ItemConvertible ... inputItems) {
        return ShapedRecipeJsonFactory.create(outputItem).input(Character.valueOf('#'), Ingredient.ofItems(inputItems)).pattern("##");
    }

    private static void offerWoodenSlabRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        RecipesProvider.createSlabRecipe(outputItem, Ingredient.ofItems(inputItem)).group("wooden_slab").criterion("has_planks", RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerSlabRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        RecipesProvider.createSlabRecipe(outputItem, Ingredient.ofItems(inputItem)).criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static ShapedRecipeJsonFactory createSlabRecipe(ItemConvertible outputItem, Ingredient ingredient) {
        return ShapedRecipeJsonFactory.create(outputItem, 6).input(Character.valueOf('#'), ingredient).pattern("###");
    }

    private static void offerWoodenStairsRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        RecipesProvider.createStairsRecipe(outputItem, Ingredient.ofItems(inputItem)).group("wooden_stairs").criterion("has_planks", RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerStonecutterRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        RecipesProvider.createStairsRecipe(outputItem, Ingredient.ofItems(inputItem)).criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static ShapedRecipeJsonFactory createStairsRecipe(ItemConvertible outputItem, Ingredient inputItem) {
        return ShapedRecipeJsonFactory.create(outputItem, 4).input(Character.valueOf('#'), inputItem).pattern("#  ").pattern("## ").pattern("###");
    }

    private static void offerTrapdoorRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 2).input(Character.valueOf('#'), inputItem).pattern("###").pattern("###").group("wooden_trapdoor").criterion("has_planks", RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerSignRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 3).group("sign").input(Character.valueOf('#'), inputItem).input(Character.valueOf('X'), Items.STICK).pattern("###").pattern("###").pattern(" X ").criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerWoolDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapelessRecipeJsonFactory.create(outputItem).input(inputItem).input(Blocks.WHITE_WOOL).group("wool").criterion("has_white_wool", RecipesProvider.conditionsFromItem(Blocks.WHITE_WOOL)).offerTo(consumer);
    }

    private static void offerCarpetRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 3).input(Character.valueOf('#'), inputItem).pattern("##").group("carpet").criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerCarpetDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible itemConvertible) {
        String string = Registry.ITEM.getId(outputItem.asItem()).getPath();
        ShapedRecipeJsonFactory.create(outputItem, 8).input(Character.valueOf('#'), Blocks.WHITE_CARPET).input(Character.valueOf('$'), itemConvertible).pattern("###").pattern("#$#").pattern("###").group("carpet").criterion("has_white_carpet", RecipesProvider.conditionsFromItem(Blocks.WHITE_CARPET)).criterion(RecipesProvider.hasItem(itemConvertible), RecipesProvider.conditionsFromItem(itemConvertible)).offerTo(consumer, string + "_from_white_carpet");
    }

    private static void offerBedRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible itemConvertible) {
        ShapedRecipeJsonFactory.create(outputItem).input(Character.valueOf('#'), itemConvertible).input(Character.valueOf('X'), ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").criterion(RecipesProvider.hasItem(itemConvertible), RecipesProvider.conditionsFromItem(itemConvertible)).offerTo(consumer);
    }

    private static void offerBedDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        String string = Registry.ITEM.getId(outputItem.asItem()).getPath();
        ShapelessRecipeJsonFactory.create(outputItem).input(Items.WHITE_BED).input(inputItem).group("dyed_bed").criterion("has_bed", RecipesProvider.conditionsFromItem(Items.WHITE_BED)).offerTo(consumer, string + "_from_white_bed");
    }

    private static void offerBannerRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem).input(Character.valueOf('#'), inputItem).input(Character.valueOf('|'), Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerStainedGlassDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 8).input(Character.valueOf('#'), Blocks.GLASS).input(Character.valueOf('X'), inputItem).pattern("###").pattern("#X#").pattern("###").group("stained_glass").criterion("has_glass", RecipesProvider.conditionsFromItem(Blocks.GLASS)).offerTo(consumer);
    }

    private static void offerStainedGlassPaneRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 16).input(Character.valueOf('#'), inputItem).pattern("###").pattern("###").group("stained_glass_pane").criterion("has_glass", RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    private static void offerStainedGlassPaneDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        String string = Registry.ITEM.getId(outputItem.asItem()).getPath();
        ShapedRecipeJsonFactory.create(outputItem, 8).input(Character.valueOf('#'), Blocks.GLASS_PANE).input(Character.valueOf('$'), inputItem).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").criterion("has_glass_pane", RecipesProvider.conditionsFromItem(Blocks.GLASS_PANE)).criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer, string + "_from_glass_pane");
    }

    private static void offerTerracottaDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 8).input(Character.valueOf('#'), Blocks.TERRACOTTA).input(Character.valueOf('X'), inputItem).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").criterion("has_terracotta", RecipesProvider.conditionsFromItem(Blocks.TERRACOTTA)).offerTo(consumer);
    }

    private static void offerConcretePowderDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapelessRecipeJsonFactory.create(outputItem, 8).input(inputItem).input(Blocks.SAND, 4).input(Blocks.GRAVEL, 4).group("concrete_powder").criterion("has_sand", RecipesProvider.conditionsFromItem(Blocks.SAND)).criterion("has_gravel", RecipesProvider.conditionsFromItem(Blocks.GRAVEL)).offerTo(consumer);
    }

    public static void offerCandleDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapelessRecipeJsonFactory.create(outputItem).input(Blocks.CANDLE).input(inputItem).criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    public static void offerWallRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 6).input(Character.valueOf('#'), inputItem).pattern("###").pattern("###").criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    public static void offerPolishedStoneRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 4).input(Character.valueOf('S'), inputItem).pattern("SS").pattern("SS").criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    public static void offerCutCopperRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        ShapedRecipeJsonFactory.create(outputItem, 4).input(Character.valueOf('#'), inputItem).pattern("##").pattern("##").criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    public static void offerChiseledBlockRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
        RecipesProvider.createChiseledBlockRecipe(outputItem, inputItem).criterion(RecipesProvider.hasItem(inputItem), RecipesProvider.conditionsFromItem(inputItem)).offerTo(consumer);
    }

    public static ShapedRecipeJsonFactory createChiseledBlockRecipe(ItemConvertible outputItem, ItemConvertible inputItem) {
        return ShapedRecipeJsonFactory.create(outputItem).input(Character.valueOf('#'), inputItem).pattern("#").pattern("#");
    }

    private static void generateCookingRecipes(Consumer<RecipeJsonProvider> consumer, String cooker, CookingRecipeSerializer<?> serializer, int cookingTime) {
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.BEEF), Items.COOKED_BEEF, 0.35f, cookingTime, serializer).criterion("has_beef", RecipesProvider.conditionsFromItem(Items.BEEF)).offerTo(consumer, "cooked_beef_from_" + cooker);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35f, cookingTime, serializer).criterion("has_chicken", RecipesProvider.conditionsFromItem(Items.CHICKEN)).offerTo(consumer, "cooked_chicken_from_" + cooker);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.COD), Items.COOKED_COD, 0.35f, cookingTime, serializer).criterion("has_cod", RecipesProvider.conditionsFromItem(Items.COD)).offerTo(consumer, "cooked_cod_from_" + cooker);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Blocks.KELP), Items.DRIED_KELP, 0.1f, cookingTime, serializer).criterion("has_kelp", RecipesProvider.conditionsFromItem(Blocks.KELP)).offerTo(consumer, "dried_kelp_from_" + cooker);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.SALMON), Items.COOKED_SALMON, 0.35f, cookingTime, serializer).criterion("has_salmon", RecipesProvider.conditionsFromItem(Items.SALMON)).offerTo(consumer, "cooked_salmon_from_" + cooker);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.MUTTON), Items.COOKED_MUTTON, 0.35f, cookingTime, serializer).criterion("has_mutton", RecipesProvider.conditionsFromItem(Items.MUTTON)).offerTo(consumer, "cooked_mutton_from_" + cooker);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35f, cookingTime, serializer).criterion("has_porkchop", RecipesProvider.conditionsFromItem(Items.PORKCHOP)).offerTo(consumer, "cooked_porkchop_from_" + cooker);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.POTATO), Items.BAKED_POTATO, 0.35f, cookingTime, serializer).criterion("has_potato", RecipesProvider.conditionsFromItem(Items.POTATO)).offerTo(consumer, "baked_potato_from_" + cooker);
        CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.RABBIT), Items.COOKED_RABBIT, 0.35f, cookingTime, serializer).criterion("has_rabbit", RecipesProvider.conditionsFromItem(Items.RABBIT)).offerTo(consumer, "cooked_rabbit_from_" + cooker);
    }

    private static EnterBlockCriterion.Conditions requireEnteringFluid(Block block) {
        return new EnterBlockCriterion.Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
        return RecipesProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().item(item).build());
    }

    private static InventoryChangedCriterion.Conditions conditionsFromTag(Tag<Item> tag) {
        return RecipesProvider.conditionsFromItemPredicates(ItemPredicate.Builder.create().tag(tag).build());
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate ... itemPredicates) {
        return new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, itemPredicates);
    }

    private static String hasItem(ItemConvertible item) {
        return "has_" + Registry.ITEM.getId(item.asItem()).getPath();
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}

