package net.minecraft.data.server;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipesProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator root;

	public RecipesProvider(DataGenerator root) {
		this.root = root;
	}

	@Override
	public void run(DataCache cache) {
		Path path = this.root.getOutput();
		Set<Identifier> set = Sets.<Identifier>newHashSet();
		generate(
			recipeJsonProvider -> {
				if (!set.add(recipeJsonProvider.getRecipeId())) {
					throw new IllegalStateException("Duplicate recipe " + recipeJsonProvider.getRecipeId());
				} else {
					saveRecipe(
						cache,
						recipeJsonProvider.toJson(),
						path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/recipes/" + recipeJsonProvider.getRecipeId().getPath() + ".json")
					);
					JsonObject jsonObject = recipeJsonProvider.toAdvancementJson();
					if (jsonObject != null) {
						saveRecipeAdvancement(
							cache,
							jsonObject,
							path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/advancements/" + recipeJsonProvider.getAdvancementId().getPath() + ".json")
						);
					}
				}
			}
		);
		saveRecipeAdvancement(
			cache,
			Advancement.Task.create().criterion("impossible", new ImpossibleCriterion.Conditions()).toJson(),
			path.resolve("data/minecraft/advancements/recipes/root.json")
		);
	}

	private static void saveRecipe(DataCache cache, JsonObject json, Path path) {
		try {
			String string = GSON.toJson((JsonElement)json);
			String string2 = SHA1.hashUnencodedChars(string).toString();
			if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
				Files.createDirectories(path.getParent());
				BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
				Throwable var6 = null;

				try {
					bufferedWriter.write(string);
				} catch (Throwable var16) {
					var6 = var16;
					throw var16;
				} finally {
					if (bufferedWriter != null) {
						if (var6 != null) {
							try {
								bufferedWriter.close();
							} catch (Throwable var15) {
								var6.addSuppressed(var15);
							}
						} else {
							bufferedWriter.close();
						}
					}
				}
			}

			cache.updateSha1(path, string2);
		} catch (IOException var18) {
			LOGGER.error("Couldn't save recipe {}", path, var18);
		}
	}

	private static void saveRecipeAdvancement(DataCache cache, JsonObject json, Path path) {
		try {
			String string = GSON.toJson((JsonElement)json);
			String string2 = SHA1.hashUnencodedChars(string).toString();
			if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
				Files.createDirectories(path.getParent());
				BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
				Throwable var6 = null;

				try {
					bufferedWriter.write(string);
				} catch (Throwable var16) {
					var6 = var16;
					throw var16;
				} finally {
					if (bufferedWriter != null) {
						if (var6 != null) {
							try {
								bufferedWriter.close();
							} catch (Throwable var15) {
								var6.addSuppressed(var15);
							}
						} else {
							bufferedWriter.close();
						}
					}
				}
			}

			cache.updateSha1(path, string2);
		} catch (IOException var18) {
			LOGGER.error("Couldn't save recipe advancement {}", path, var18);
		}
	}

	private static void generate(Consumer<RecipeJsonProvider> consumer) {
		offerPlanksRecipe2(consumer, Blocks.ACACIA_PLANKS, ItemTags.ACACIA_LOGS);
		offerPlanksRecipe(consumer, Blocks.BIRCH_PLANKS, ItemTags.BIRCH_LOGS);
		offerPlanksRecipe(consumer, Blocks.CRIMSON_PLANKS, ItemTags.CRIMSON_STEMS);
		offerPlanksRecipe2(consumer, Blocks.DARK_OAK_PLANKS, ItemTags.DARK_OAK_LOGS);
		offerPlanksRecipe(consumer, Blocks.JUNGLE_PLANKS, ItemTags.JUNGLE_LOGS);
		offerPlanksRecipe(consumer, Blocks.OAK_PLANKS, ItemTags.OAK_LOGS);
		offerPlanksRecipe(consumer, Blocks.SPRUCE_PLANKS, ItemTags.SPRUCE_LOGS);
		offerPlanksRecipe(consumer, Blocks.WARPED_PLANKS, ItemTags.WARPED_STEMS);
		offerBarkBlockRecipe(consumer, Blocks.ACACIA_WOOD, Blocks.ACACIA_LOG);
		offerBarkBlockRecipe(consumer, Blocks.BIRCH_WOOD, Blocks.BIRCH_LOG);
		offerBarkBlockRecipe(consumer, Blocks.DARK_OAK_WOOD, Blocks.DARK_OAK_LOG);
		offerBarkBlockRecipe(consumer, Blocks.JUNGLE_WOOD, Blocks.JUNGLE_LOG);
		offerBarkBlockRecipe(consumer, Blocks.OAK_WOOD, Blocks.OAK_LOG);
		offerBarkBlockRecipe(consumer, Blocks.SPRUCE_WOOD, Blocks.SPRUCE_LOG);
		offerBarkBlockRecipe(consumer, Blocks.CRIMSON_HYPHAE, Blocks.CRIMSON_STEM);
		offerBarkBlockRecipe(consumer, Blocks.WARPED_HYPHAE, Blocks.WARPED_STEM);
		offerBarkBlockRecipe(consumer, Blocks.STRIPPED_ACACIA_WOOD, Blocks.STRIPPED_ACACIA_LOG);
		offerBarkBlockRecipe(consumer, Blocks.STRIPPED_BIRCH_WOOD, Blocks.STRIPPED_BIRCH_LOG);
		offerBarkBlockRecipe(consumer, Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_LOG);
		offerBarkBlockRecipe(consumer, Blocks.STRIPPED_JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_LOG);
		offerBarkBlockRecipe(consumer, Blocks.STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_LOG);
		offerBarkBlockRecipe(consumer, Blocks.STRIPPED_SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_LOG);
		offerBarkBlockRecipe(consumer, Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_STEM);
		offerBarkBlockRecipe(consumer, Blocks.STRIPPED_WARPED_HYPHAE, Blocks.STRIPPED_WARPED_STEM);
		offerBoatRecipe(consumer, Items.ACACIA_BOAT, Blocks.ACACIA_PLANKS);
		offerBoatRecipe(consumer, Items.BIRCH_BOAT, Blocks.BIRCH_PLANKS);
		offerBoatRecipe(consumer, Items.DARK_OAK_BOAT, Blocks.DARK_OAK_PLANKS);
		offerBoatRecipe(consumer, Items.JUNGLE_BOAT, Blocks.JUNGLE_PLANKS);
		offerBoatRecipe(consumer, Items.OAK_BOAT, Blocks.OAK_PLANKS);
		offerBoatRecipe(consumer, Items.SPRUCE_BOAT, Blocks.SPRUCE_PLANKS);
		offerButtonRecipe(consumer, Blocks.ACACIA_BUTTON, Blocks.ACACIA_PLANKS);
		offerDoorRecipe(consumer, Blocks.ACACIA_DOOR, Blocks.ACACIA_PLANKS);
		offerFenceRecipe(consumer, Blocks.ACACIA_FENCE, Blocks.ACACIA_PLANKS);
		offerFenceGateRecipe(consumer, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_PLANKS);
		offerWoodenPressurePlateRecipe(consumer, Blocks.ACACIA_PRESSURE_PLATE, Blocks.ACACIA_PLANKS);
		offerWoodenSlabRecipe(consumer, Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS);
		offerWoodenStairsRecipe(consumer, Blocks.ACACIA_STAIRS, Blocks.ACACIA_PLANKS);
		offerTrapdoorRecipe(consumer, Blocks.ACACIA_TRAPDOOR, Blocks.ACACIA_PLANKS);
		offerSignRecipe(consumer, Blocks.ACACIA_SIGN, Blocks.ACACIA_PLANKS);
		offerButtonRecipe(consumer, Blocks.BIRCH_BUTTON, Blocks.BIRCH_PLANKS);
		offerDoorRecipe(consumer, Blocks.BIRCH_DOOR, Blocks.BIRCH_PLANKS);
		offerFenceRecipe(consumer, Blocks.BIRCH_FENCE, Blocks.BIRCH_PLANKS);
		offerFenceGateRecipe(consumer, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_PLANKS);
		offerWoodenPressurePlateRecipe(consumer, Blocks.BIRCH_PRESSURE_PLATE, Blocks.BIRCH_PLANKS);
		offerWoodenSlabRecipe(consumer, Blocks.BIRCH_SLAB, Blocks.BIRCH_PLANKS);
		offerWoodenStairsRecipe(consumer, Blocks.BIRCH_STAIRS, Blocks.BIRCH_PLANKS);
		offerTrapdoorRecipe(consumer, Blocks.BIRCH_TRAPDOOR, Blocks.BIRCH_PLANKS);
		offerSignRecipe(consumer, Blocks.BIRCH_SIGN, Blocks.BIRCH_PLANKS);
		offerButtonRecipe(consumer, Blocks.CRIMSON_BUTTON, Blocks.CRIMSON_PLANKS);
		offerDoorRecipe(consumer, Blocks.CRIMSON_DOOR, Blocks.CRIMSON_PLANKS);
		offerFenceRecipe(consumer, Blocks.CRIMSON_FENCE, Blocks.CRIMSON_PLANKS);
		offerFenceGateRecipe(consumer, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_PLANKS);
		offerWoodenPressurePlateRecipe(consumer, Blocks.CRIMSON_PRESSURE_PLATE, Blocks.CRIMSON_PLANKS);
		offerWoodenSlabRecipe(consumer, Blocks.CRIMSON_SLAB, Blocks.CRIMSON_PLANKS);
		offerWoodenStairsRecipe(consumer, Blocks.CRIMSON_STAIRS, Blocks.CRIMSON_PLANKS);
		offerTrapdoorRecipe(consumer, Blocks.CRIMSON_TRAPDOOR, Blocks.CRIMSON_PLANKS);
		offerSignRecipe(consumer, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_PLANKS);
		offerButtonRecipe(consumer, Blocks.DARK_OAK_BUTTON, Blocks.DARK_OAK_PLANKS);
		offerDoorRecipe(consumer, Blocks.DARK_OAK_DOOR, Blocks.DARK_OAK_PLANKS);
		offerFenceRecipe(consumer, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_PLANKS);
		offerFenceGateRecipe(consumer, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_PLANKS);
		offerWoodenPressurePlateRecipe(consumer, Blocks.DARK_OAK_PRESSURE_PLATE, Blocks.DARK_OAK_PLANKS);
		offerWoodenSlabRecipe(consumer, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
		offerWoodenStairsRecipe(consumer, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_PLANKS);
		offerTrapdoorRecipe(consumer, Blocks.DARK_OAK_TRAPDOOR, Blocks.DARK_OAK_PLANKS);
		offerSignRecipe(consumer, Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_PLANKS);
		offerButtonRecipe(consumer, Blocks.JUNGLE_BUTTON, Blocks.JUNGLE_PLANKS);
		offerDoorRecipe(consumer, Blocks.JUNGLE_DOOR, Blocks.JUNGLE_PLANKS);
		offerFenceRecipe(consumer, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_PLANKS);
		offerFenceGateRecipe(consumer, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_PLANKS);
		offerWoodenPressurePlateRecipe(consumer, Blocks.JUNGLE_PRESSURE_PLATE, Blocks.JUNGLE_PLANKS);
		offerWoodenSlabRecipe(consumer, Blocks.JUNGLE_SLAB, Blocks.JUNGLE_PLANKS);
		offerWoodenStairsRecipe(consumer, Blocks.JUNGLE_STAIRS, Blocks.JUNGLE_PLANKS);
		offerTrapdoorRecipe(consumer, Blocks.JUNGLE_TRAPDOOR, Blocks.JUNGLE_PLANKS);
		offerSignRecipe(consumer, Blocks.JUNGLE_SIGN, Blocks.JUNGLE_PLANKS);
		offerButtonRecipe(consumer, Blocks.OAK_BUTTON, Blocks.OAK_PLANKS);
		offerDoorRecipe(consumer, Blocks.OAK_DOOR, Blocks.OAK_PLANKS);
		offerFenceRecipe(consumer, Blocks.OAK_FENCE, Blocks.OAK_PLANKS);
		offerFenceGateRecipe(consumer, Blocks.OAK_FENCE_GATE, Blocks.OAK_PLANKS);
		offerWoodenPressurePlateRecipe(consumer, Blocks.OAK_PRESSURE_PLATE, Blocks.OAK_PLANKS);
		offerWoodenSlabRecipe(consumer, Blocks.OAK_SLAB, Blocks.OAK_PLANKS);
		offerWoodenStairsRecipe(consumer, Blocks.OAK_STAIRS, Blocks.OAK_PLANKS);
		offerTrapdoorRecipe(consumer, Blocks.OAK_TRAPDOOR, Blocks.OAK_PLANKS);
		offerSignRecipe(consumer, Blocks.OAK_SIGN, Blocks.OAK_PLANKS);
		offerButtonRecipe(consumer, Blocks.SPRUCE_BUTTON, Blocks.SPRUCE_PLANKS);
		offerDoorRecipe(consumer, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_PLANKS);
		offerFenceRecipe(consumer, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_PLANKS);
		offerFenceGateRecipe(consumer, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_PLANKS);
		offerWoodenPressurePlateRecipe(consumer, Blocks.SPRUCE_PRESSURE_PLATE, Blocks.SPRUCE_PLANKS);
		offerWoodenSlabRecipe(consumer, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
		offerWoodenStairsRecipe(consumer, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_PLANKS);
		offerTrapdoorRecipe(consumer, Blocks.SPRUCE_TRAPDOOR, Blocks.SPRUCE_PLANKS);
		offerSignRecipe(consumer, Blocks.SPRUCE_SIGN, Blocks.SPRUCE_PLANKS);
		offerButtonRecipe(consumer, Blocks.WARPED_BUTTON, Blocks.WARPED_PLANKS);
		offerDoorRecipe(consumer, Blocks.WARPED_DOOR, Blocks.WARPED_PLANKS);
		offerFenceRecipe(consumer, Blocks.WARPED_FENCE, Blocks.WARPED_PLANKS);
		offerFenceGateRecipe(consumer, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_PLANKS);
		offerWoodenPressurePlateRecipe(consumer, Blocks.WARPED_PRESSURE_PLATE, Blocks.WARPED_PLANKS);
		offerWoodenSlabRecipe(consumer, Blocks.WARPED_SLAB, Blocks.WARPED_PLANKS);
		offerWoodenStairsRecipe(consumer, Blocks.WARPED_STAIRS, Blocks.WARPED_PLANKS);
		offerTrapdoorRecipe(consumer, Blocks.WARPED_TRAPDOOR, Blocks.WARPED_PLANKS);
		offerSignRecipe(consumer, Blocks.WARPED_SIGN, Blocks.WARPED_PLANKS);
		offerWoolDyeingRecipe(consumer, Blocks.BLACK_WOOL, Items.BLACK_DYE);
		offerCarpetRecipe(consumer, Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.BLACK_CARPET, Items.BLACK_DYE);
		offerBedRecipe(consumer, Items.BLACK_BED, Blocks.BLACK_WOOL);
		offerBedDyeingRecipe(consumer, Items.BLACK_BED, Items.BLACK_DYE);
		offerBannerRecipe(consumer, Items.BLACK_BANNER, Blocks.BLACK_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.BLUE_WOOL, Items.BLUE_DYE);
		offerCarpetRecipe(consumer, Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.BLUE_CARPET, Items.BLUE_DYE);
		offerBedRecipe(consumer, Items.BLUE_BED, Blocks.BLUE_WOOL);
		offerBedDyeingRecipe(consumer, Items.BLUE_BED, Items.BLUE_DYE);
		offerBannerRecipe(consumer, Items.BLUE_BANNER, Blocks.BLUE_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.BROWN_WOOL, Items.BROWN_DYE);
		offerCarpetRecipe(consumer, Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.BROWN_CARPET, Items.BROWN_DYE);
		offerBedRecipe(consumer, Items.BROWN_BED, Blocks.BROWN_WOOL);
		offerBedDyeingRecipe(consumer, Items.BROWN_BED, Items.BROWN_DYE);
		offerBannerRecipe(consumer, Items.BROWN_BANNER, Blocks.BROWN_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.CYAN_WOOL, Items.CYAN_DYE);
		offerCarpetRecipe(consumer, Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.CYAN_CARPET, Items.CYAN_DYE);
		offerBedRecipe(consumer, Items.CYAN_BED, Blocks.CYAN_WOOL);
		offerBedDyeingRecipe(consumer, Items.CYAN_BED, Items.CYAN_DYE);
		offerBannerRecipe(consumer, Items.CYAN_BANNER, Blocks.CYAN_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.GRAY_WOOL, Items.GRAY_DYE);
		offerCarpetRecipe(consumer, Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.GRAY_CARPET, Items.GRAY_DYE);
		offerBedRecipe(consumer, Items.GRAY_BED, Blocks.GRAY_WOOL);
		offerBedDyeingRecipe(consumer, Items.GRAY_BED, Items.GRAY_DYE);
		offerBannerRecipe(consumer, Items.GRAY_BANNER, Blocks.GRAY_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.GREEN_WOOL, Items.GREEN_DYE);
		offerCarpetRecipe(consumer, Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.GREEN_CARPET, Items.GREEN_DYE);
		offerBedRecipe(consumer, Items.GREEN_BED, Blocks.GREEN_WOOL);
		offerBedDyeingRecipe(consumer, Items.GREEN_BED, Items.GREEN_DYE);
		offerBannerRecipe(consumer, Items.GREEN_BANNER, Blocks.GREEN_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE);
		offerCarpetRecipe(consumer, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.LIGHT_BLUE_CARPET, Items.LIGHT_BLUE_DYE);
		offerBedRecipe(consumer, Items.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
		offerBedDyeingRecipe(consumer, Items.LIGHT_BLUE_BED, Items.LIGHT_BLUE_DYE);
		offerBannerRecipe(consumer, Items.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE);
		offerCarpetRecipe(consumer, Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.LIGHT_GRAY_CARPET, Items.LIGHT_GRAY_DYE);
		offerBedRecipe(consumer, Items.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
		offerBedDyeingRecipe(consumer, Items.LIGHT_GRAY_BED, Items.LIGHT_GRAY_DYE);
		offerBannerRecipe(consumer, Items.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.LIME_WOOL, Items.LIME_DYE);
		offerCarpetRecipe(consumer, Blocks.LIME_CARPET, Blocks.LIME_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.LIME_CARPET, Items.LIME_DYE);
		offerBedRecipe(consumer, Items.LIME_BED, Blocks.LIME_WOOL);
		offerBedDyeingRecipe(consumer, Items.LIME_BED, Items.LIME_DYE);
		offerBannerRecipe(consumer, Items.LIME_BANNER, Blocks.LIME_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.MAGENTA_WOOL, Items.MAGENTA_DYE);
		offerCarpetRecipe(consumer, Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.MAGENTA_CARPET, Items.MAGENTA_DYE);
		offerBedRecipe(consumer, Items.MAGENTA_BED, Blocks.MAGENTA_WOOL);
		offerBedDyeingRecipe(consumer, Items.MAGENTA_BED, Items.MAGENTA_DYE);
		offerBannerRecipe(consumer, Items.MAGENTA_BANNER, Blocks.MAGENTA_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.ORANGE_WOOL, Items.ORANGE_DYE);
		offerCarpetRecipe(consumer, Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.ORANGE_CARPET, Items.ORANGE_DYE);
		offerBedRecipe(consumer, Items.ORANGE_BED, Blocks.ORANGE_WOOL);
		offerBedDyeingRecipe(consumer, Items.ORANGE_BED, Items.ORANGE_DYE);
		offerBannerRecipe(consumer, Items.ORANGE_BANNER, Blocks.ORANGE_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.PINK_WOOL, Items.PINK_DYE);
		offerCarpetRecipe(consumer, Blocks.PINK_CARPET, Blocks.PINK_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.PINK_CARPET, Items.PINK_DYE);
		offerBedRecipe(consumer, Items.PINK_BED, Blocks.PINK_WOOL);
		offerBedDyeingRecipe(consumer, Items.PINK_BED, Items.PINK_DYE);
		offerBannerRecipe(consumer, Items.PINK_BANNER, Blocks.PINK_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.PURPLE_WOOL, Items.PURPLE_DYE);
		offerCarpetRecipe(consumer, Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.PURPLE_CARPET, Items.PURPLE_DYE);
		offerBedRecipe(consumer, Items.PURPLE_BED, Blocks.PURPLE_WOOL);
		offerBedDyeingRecipe(consumer, Items.PURPLE_BED, Items.PURPLE_DYE);
		offerBannerRecipe(consumer, Items.PURPLE_BANNER, Blocks.PURPLE_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.RED_WOOL, Items.RED_DYE);
		offerCarpetRecipe(consumer, Blocks.RED_CARPET, Blocks.RED_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.RED_CARPET, Items.RED_DYE);
		offerBedRecipe(consumer, Items.RED_BED, Blocks.RED_WOOL);
		offerBedDyeingRecipe(consumer, Items.RED_BED, Items.RED_DYE);
		offerBannerRecipe(consumer, Items.RED_BANNER, Blocks.RED_WOOL);
		offerCarpetRecipe(consumer, Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
		offerBedRecipe(consumer, Items.WHITE_BED, Blocks.WHITE_WOOL);
		offerBannerRecipe(consumer, Items.WHITE_BANNER, Blocks.WHITE_WOOL);
		offerWoolDyeingRecipe(consumer, Blocks.YELLOW_WOOL, Items.YELLOW_DYE);
		offerCarpetRecipe(consumer, Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
		offerCarpetDyeingRecipe(consumer, Blocks.YELLOW_CARPET, Items.YELLOW_DYE);
		offerBedRecipe(consumer, Items.YELLOW_BED, Blocks.YELLOW_WOOL);
		offerBedDyeingRecipe(consumer, Items.YELLOW_BED, Items.YELLOW_DYE);
		offerBannerRecipe(consumer, Items.YELLOW_BANNER, Blocks.YELLOW_WOOL);
		offerStainedGlassDyeingRecipe(consumer, Blocks.BLACK_STAINED_GLASS, Items.BLACK_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.BLACK_STAINED_GLASS_PANE, Items.BLACK_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.BLUE_STAINED_GLASS, Items.BLUE_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.BLUE_STAINED_GLASS_PANE, Items.BLUE_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.BROWN_STAINED_GLASS, Items.BROWN_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.BROWN_STAINED_GLASS_PANE, Items.BROWN_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.CYAN_STAINED_GLASS, Items.CYAN_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.CYAN_STAINED_GLASS_PANE, Items.CYAN_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.GRAY_STAINED_GLASS, Items.GRAY_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.GRAY_STAINED_GLASS_PANE, Items.GRAY_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.GREEN_STAINED_GLASS, Items.GREEN_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.GREEN_STAINED_GLASS_PANE, Items.GREEN_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS, Items.LIGHT_BLUE_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Items.LIGHT_BLUE_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS, Items.LIGHT_GRAY_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.LIME_STAINED_GLASS, Items.LIME_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.LIME_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.LIME_STAINED_GLASS_PANE, Items.LIME_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.MAGENTA_STAINED_GLASS, Items.MAGENTA_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.MAGENTA_STAINED_GLASS_PANE, Items.MAGENTA_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.ORANGE_STAINED_GLASS, Items.ORANGE_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.ORANGE_STAINED_GLASS_PANE, Items.ORANGE_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.PINK_STAINED_GLASS, Items.PINK_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.PINK_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.PINK_STAINED_GLASS_PANE, Items.PINK_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.PURPLE_STAINED_GLASS, Items.PURPLE_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.PURPLE_STAINED_GLASS_PANE, Items.PURPLE_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.RED_STAINED_GLASS, Items.RED_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.RED_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.RED_STAINED_GLASS_PANE, Items.RED_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.WHITE_STAINED_GLASS, Items.WHITE_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.WHITE_STAINED_GLASS_PANE, Items.WHITE_DYE);
		offerStainedGlassDyeingRecipe(consumer, Blocks.YELLOW_STAINED_GLASS, Items.YELLOW_DYE);
		offerStainedGlassPaneRecipe(consumer, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS);
		offerStainedGlassPaneDyeingRecipe(consumer, Blocks.YELLOW_STAINED_GLASS_PANE, Items.YELLOW_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.BLACK_TERRACOTTA, Items.BLACK_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.BLUE_TERRACOTTA, Items.BLUE_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.BROWN_TERRACOTTA, Items.BROWN_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.CYAN_TERRACOTTA, Items.CYAN_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.GRAY_TERRACOTTA, Items.GRAY_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.GREEN_TERRACOTTA, Items.GREEN_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.LIME_TERRACOTTA, Items.LIME_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.MAGENTA_TERRACOTTA, Items.MAGENTA_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.ORANGE_TERRACOTTA, Items.ORANGE_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.PINK_TERRACOTTA, Items.PINK_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.PURPLE_TERRACOTTA, Items.PURPLE_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.RED_TERRACOTTA, Items.RED_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.WHITE_TERRACOTTA, Items.WHITE_DYE);
		offerTerracottaDyeingRecipe(consumer, Blocks.YELLOW_TERRACOTTA, Items.YELLOW_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.BLACK_CONCRETE_POWDER, Items.BLACK_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.BLUE_CONCRETE_POWDER, Items.BLUE_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.BROWN_CONCRETE_POWDER, Items.BROWN_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.RED_CONCRETE_POWDER, Items.RED_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.WHITE_CONCRETE_POWDER, Items.WHITE_DYE);
		offerConcretePowderDyeingRecipe(consumer, Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.BLACK_CANDLE, Items.BLACK_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.BLUE_CANDLE, Items.BLUE_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.BROWN_CANDLE, Items.BROWN_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.CYAN_CANDLE, Items.CYAN_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.GRAY_CANDLE, Items.GRAY_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.GREEN_CANDLE, Items.GREEN_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.LIGHT_BLUE_CANDLE, Items.LIGHT_BLUE_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.LIGHT_GRAY_CANDLE, Items.LIGHT_GRAY_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.LIME_CANDLE, Items.LIME_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.MAGENTA_CANDLE, Items.MAGENTA_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.ORANGE_CANDLE, Items.ORANGE_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.PINK_CANDLE, Items.PINK_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.PURPLE_CANDLE, Items.PURPLE_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.RED_CANDLE, Items.RED_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.WHITE_CANDLE, Items.WHITE_DYE);
		offerCandleDyeingRecipe(consumer, Blocks.YELLOW_CANDLE, Items.YELLOW_DYE);
		ShapedRecipeJsonFactory.create(Blocks.ACTIVATOR_RAIL, 6)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('S', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XSX")
			.pattern("X#X")
			.pattern("XSX")
			.criterion("has_rail", conditionsFromItem(Blocks.RAIL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.ANDESITE, 2)
			.input(Blocks.DIORITE)
			.input(Blocks.COBBLESTONE)
			.criterion("has_stone", conditionsFromItem(Blocks.DIORITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ANVIL)
			.input('I', Blocks.IRON_BLOCK)
			.input('i', Items.IRON_INGOT)
			.pattern("III")
			.pattern(" i ")
			.pattern("iii")
			.criterion("has_iron_block", conditionsFromItem(Blocks.IRON_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ARMOR_STAND)
			.input('/', Items.STICK)
			.input('_', Blocks.SMOOTH_STONE_SLAB)
			.pattern("///")
			.pattern(" / ")
			.pattern("/_/")
			.criterion("has_stone_slab", conditionsFromItem(Blocks.SMOOTH_STONE_SLAB))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ARROW, 4)
			.input('#', Items.STICK)
			.input('X', Items.FLINT)
			.input('Y', Items.FEATHER)
			.pattern("X")
			.pattern("#")
			.pattern("Y")
			.criterion("has_feather", conditionsFromItem(Items.FEATHER))
			.criterion("has_flint", conditionsFromItem(Items.FLINT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BARREL, 1)
			.input('P', ItemTags.PLANKS)
			.input('S', ItemTags.WOODEN_SLABS)
			.pattern("PSP")
			.pattern("P P")
			.pattern("PSP")
			.criterion("has_planks", conditionsFromTag(ItemTags.PLANKS))
			.criterion("has_wood_slab", conditionsFromTag(ItemTags.WOODEN_SLABS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BEACON)
			.input('S', Items.NETHER_STAR)
			.input('G', Blocks.GLASS)
			.input('O', Blocks.OBSIDIAN)
			.pattern("GGG")
			.pattern("GSG")
			.pattern("OOO")
			.criterion("has_nether_star", conditionsFromItem(Items.NETHER_STAR))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BEEHIVE)
			.input('P', ItemTags.PLANKS)
			.input('H', Items.HONEYCOMB)
			.pattern("PPP")
			.pattern("HHH")
			.pattern("PPP")
			.criterion("has_honeycomb", conditionsFromItem(Items.HONEYCOMB))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BEETROOT_SOUP)
			.input(Items.BOWL)
			.input(Items.BEETROOT, 6)
			.criterion("has_beetroot", conditionsFromItem(Items.BEETROOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLACK_DYE)
			.input(Items.INK_SAC)
			.group("black_dye")
			.criterion("has_ink_sac", conditionsFromItem(Items.INK_SAC))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLACK_DYE)
			.input(Blocks.WITHER_ROSE)
			.group("black_dye")
			.criterion("has_black_flower", conditionsFromItem(Blocks.WITHER_ROSE))
			.offerTo(consumer, "black_dye_from_wither_rose");
		ShapelessRecipeJsonFactory.create(Items.BLAZE_POWDER, 2)
			.input(Items.BLAZE_ROD)
			.criterion("has_blaze_rod", conditionsFromItem(Items.BLAZE_ROD))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLUE_DYE)
			.input(Items.LAPIS_LAZULI)
			.group("blue_dye")
			.criterion("has_lapis_lazuli", conditionsFromItem(Items.LAPIS_LAZULI))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLUE_DYE)
			.input(Blocks.CORNFLOWER)
			.group("blue_dye")
			.criterion("has_blue_flower", conditionsFromItem(Blocks.CORNFLOWER))
			.offerTo(consumer, "blue_dye_from_cornflower");
		ShapedRecipeJsonFactory.create(Blocks.BLUE_ICE)
			.input('#', Blocks.PACKED_ICE)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_packed_ice", conditionsFromItem(Blocks.PACKED_ICE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BONE_BLOCK)
			.input('X', Items.BONE_MEAL)
			.pattern("XXX")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_bonemeal", conditionsFromItem(Items.BONE_MEAL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BONE_MEAL, 3)
			.input(Items.BONE)
			.group("bonemeal")
			.criterion("has_bone", conditionsFromItem(Items.BONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BONE_MEAL, 9)
			.input(Blocks.BONE_BLOCK)
			.group("bonemeal")
			.criterion("has_bone_block", conditionsFromItem(Blocks.BONE_BLOCK))
			.offerTo(consumer, "bone_meal_from_bone_block");
		ShapelessRecipeJsonFactory.create(Items.BOOK)
			.input(Items.PAPER, 3)
			.input(Items.LEATHER)
			.criterion("has_paper", conditionsFromItem(Items.PAPER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BOOKSHELF)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.BOOK)
			.pattern("###")
			.pattern("XXX")
			.pattern("###")
			.criterion("has_book", conditionsFromItem(Items.BOOK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BOW)
			.input('#', Items.STICK)
			.input('X', Items.STRING)
			.pattern(" #X")
			.pattern("# X")
			.pattern(" #X")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BOWL, 4)
			.input('#', ItemTags.PLANKS)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brown_mushroom", conditionsFromItem(Blocks.BROWN_MUSHROOM))
			.criterion("has_red_mushroom", conditionsFromItem(Blocks.RED_MUSHROOM))
			.criterion("has_mushroom_stew", conditionsFromItem(Items.MUSHROOM_STEW))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BREAD).input('#', Items.WHEAT).pattern("###").criterion("has_wheat", conditionsFromItem(Items.WHEAT)).offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BREWING_STAND)
			.input('B', Items.BLAZE_ROD)
			.input('#', ItemTags.STONE_CRAFTING_MATERIALS)
			.pattern(" B ")
			.pattern("###")
			.criterion("has_blaze_rod", conditionsFromItem(Items.BLAZE_ROD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BRICKS)
			.input('#', Items.BRICK)
			.pattern("##")
			.pattern("##")
			.criterion("has_brick", conditionsFromItem(Items.BRICK))
			.offerTo(consumer);
		offerSlabRecipe(consumer, Blocks.BRICK_SLAB, Blocks.BRICKS);
		offerStonecutterRecipe(consumer, Blocks.BRICK_STAIRS, Blocks.BRICKS);
		ShapelessRecipeJsonFactory.create(Items.BROWN_DYE)
			.input(Items.COCOA_BEANS)
			.group("brown_dye")
			.criterion("has_cocoa_beans", conditionsFromItem(Items.COCOA_BEANS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BUCKET)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CAKE)
			.input('A', Items.MILK_BUCKET)
			.input('B', Items.SUGAR)
			.input('C', Items.WHEAT)
			.input('E', Items.EGG)
			.pattern("AAA")
			.pattern("BEB")
			.pattern("CCC")
			.criterion("has_egg", conditionsFromItem(Items.EGG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CAMPFIRE)
			.input('L', ItemTags.LOGS)
			.input('S', Items.STICK)
			.input('C', ItemTags.COALS)
			.pattern(" S ")
			.pattern("SCS")
			.pattern("LLL")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.criterion("has_coal", conditionsFromTag(ItemTags.COALS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CARROT_ON_A_STICK)
			.input('#', Items.FISHING_ROD)
			.input('X', Items.CARROT)
			.pattern("# ")
			.pattern(" X")
			.criterion("has_carrot", conditionsFromItem(Items.CARROT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WARPED_FUNGUS_ON_A_STICK)
			.input('#', Items.FISHING_ROD)
			.input('X', Items.WARPED_FUNGUS)
			.pattern("# ")
			.pattern(" X")
			.criterion("has_warped_fungus", conditionsFromItem(Items.WARPED_FUNGUS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CAULDRON)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern("# #")
			.pattern("###")
			.criterion("has_water_bucket", conditionsFromItem(Items.WATER_BUCKET))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.COMPOSTER)
			.input('#', ItemTags.WOODEN_SLABS)
			.pattern("# #")
			.pattern("# #")
			.pattern("###")
			.criterion("has_wood_slab", conditionsFromTag(ItemTags.WOODEN_SLABS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CHEST)
			.input('#', ItemTags.PLANKS)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion(
				"has_lots_of_items",
				new InventoryChangedCriterion.Conditions(
					EntityPredicate.Extended.EMPTY, NumberRange.IntRange.atLeast(10), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new ItemPredicate[0]
				)
			)
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CHEST_MINECART)
			.input('A', Blocks.CHEST)
			.input('B', Items.MINECART)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", conditionsFromItem(Items.MINECART))
			.offerTo(consumer);
		offerChiseledBlockRecipe(consumer, Blocks.CHISELED_NETHER_BRICKS, Blocks.NETHER_BRICK_SLAB);
		createChiseledBlockRecipe(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_SLAB)
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(consumer);
		createChiseledBlockRecipe(Blocks.CHISELED_STONE_BRICKS, Blocks.STONE_BRICK_SLAB)
			.criterion("has_tag", conditionsFromTag(ItemTags.STONE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CLAY)
			.input('#', Items.CLAY_BALL)
			.pattern("##")
			.pattern("##")
			.criterion("has_clay_ball", conditionsFromItem(Items.CLAY_BALL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CLOCK)
			.input('#', Items.GOLD_INGOT)
			.input('X', Items.REDSTONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.COAL, 9)
			.input(Blocks.COAL_BLOCK)
			.criterion("has_coal_block", conditionsFromItem(Blocks.COAL_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.COAL_BLOCK)
			.input('#', Items.COAL)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_coal", conditionsFromItem(Items.COAL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.COARSE_DIRT, 4)
			.input('D', Blocks.DIRT)
			.input('G', Blocks.GRAVEL)
			.pattern("DG")
			.pattern("GD")
			.criterion("has_gravel", conditionsFromItem(Blocks.GRAVEL))
			.offerTo(consumer);
		offerSlabRecipe(consumer, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE);
		offerWallRecipe(consumer, Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE);
		ShapedRecipeJsonFactory.create(Blocks.COMPARATOR)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('X', Items.QUARTZ)
			.input('I', Blocks.STONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern("III")
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.COMPASS)
			.input('#', Items.IRON_INGOT)
			.input('X', Items.REDSTONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.COOKIE, 8)
			.input('#', Items.WHEAT)
			.input('X', Items.COCOA_BEANS)
			.pattern("#X#")
			.criterion("has_cocoa", conditionsFromItem(Items.COCOA_BEANS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CRAFTING_TABLE)
			.input('#', ItemTags.PLANKS)
			.pattern("##")
			.pattern("##")
			.criterion("has_planks", conditionsFromTag(ItemTags.PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CROSSBOW)
			.input('~', Items.STRING)
			.input('#', Items.STICK)
			.input('&', Items.IRON_INGOT)
			.input('$', Blocks.TRIPWIRE_HOOK)
			.pattern("#&#")
			.pattern("~$~")
			.pattern(" # ")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.criterion("has_tripwire_hook", conditionsFromItem(Blocks.TRIPWIRE_HOOK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LOOM)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.STRING)
			.pattern("@@")
			.pattern("##")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(consumer);
		createChiseledBlockRecipe(Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE_SLAB)
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_cut_red_sandstone", conditionsFromItem(Blocks.CUT_RED_SANDSTONE))
			.offerTo(consumer);
		offerChiseledBlockRecipe(consumer, Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE_SLAB);
		ShapedRecipeJsonFactory.create(Blocks.COPPER_BLOCK)
			.input('#', Items.COPPER_INGOT)
			.pattern("##")
			.pattern("##")
			.criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.COPPER_INGOT, 4)
			.input(Blocks.COPPER_BLOCK)
			.group("copper_ingot")
			.criterion("has_copper_block", conditionsFromItem(Blocks.COPPER_BLOCK))
			.offerTo(consumer, "copper_ingot_from_copper_block");
		offerCutCopperRecipe(consumer, Blocks.CUT_COPPER, Blocks.COPPER_BLOCK);
		offerCutCopperRecipe(consumer, Blocks.LIGHTLY_WEATHERED_CUT_COPPER, Blocks.LIGHTLY_WEATHERED_COPPER_BLOCK);
		offerCutCopperRecipe(consumer, Blocks.SEMI_WEATHERED_CUT_COPPER, Blocks.SEMI_WEATHERED_COPPER_BLOCK);
		offerCutCopperRecipe(consumer, Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_COPPER_BLOCK);
		offerStonecutterRecipe(consumer, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER);
		offerStonecutterRecipe(consumer, Blocks.LIGHTLY_WEATHERED_CUT_COPPER_STAIRS, Blocks.LIGHTLY_WEATHERED_CUT_COPPER);
		offerStonecutterRecipe(consumer, Blocks.SEMI_WEATHERED_CUT_COPPER_STAIRS, Blocks.SEMI_WEATHERED_CUT_COPPER);
		offerStonecutterRecipe(consumer, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER);
		offerSlabRecipe(consumer, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER);
		offerSlabRecipe(consumer, Blocks.LIGHTLY_WEATHERED_CUT_COPPER_SLAB, Blocks.LIGHTLY_WEATHERED_CUT_COPPER);
		offerSlabRecipe(consumer, Blocks.SEMI_WEATHERED_CUT_COPPER_SLAB, Blocks.SEMI_WEATHERED_CUT_COPPER);
		offerSlabRecipe(consumer, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER);
		ShapelessRecipeJsonFactory.create(Items.WAXED_COPPER)
			.input(Items.COPPER_BLOCK)
			.input(Items.HONEYCOMB)
			.criterion("has_copper_block", conditionsFromItem(Items.COPPER_BLOCK))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WAXED_SEMI_WEATHERED_COPPER)
			.input(Items.SEMI_WEATHERED_COPPER_BLOCK)
			.input(Items.HONEYCOMB)
			.criterion("has_semi_weathered_copper_block", conditionsFromItem(Items.SEMI_WEATHERED_COPPER_BLOCK))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WAXED_LIGHTLY_WEATHERED_COPPER)
			.input(Items.LIGHTLY_WEATHERED_COPPER_BLOCK)
			.input(Items.HONEYCOMB)
			.criterion("has_lightly_weathered_copper_block", conditionsFromItem(Items.LIGHTLY_WEATHERED_COPPER_BLOCK))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WAXED_CUT_COPPER)
			.input(Items.CUT_COPPER)
			.input(Items.HONEYCOMB)
			.criterion("has_cut_copper", conditionsFromItem(Items.CUT_COPPER))
			.offerTo(consumer, "waxed_cut_copper_from_honeycomb");
		ShapelessRecipeJsonFactory.create(Items.WAXED_SEMI_WEATHERED_CUT_COPPER)
			.input(Items.SEMI_WEATHERED_CUT_COPPER)
			.input(Items.HONEYCOMB)
			.criterion("has_semi_weathered_cut_copper", conditionsFromItem(Items.SEMI_WEATHERED_CUT_COPPER))
			.offerTo(consumer, "waxed_semi_weathered_cut_copper_from_honeycomb");
		ShapelessRecipeJsonFactory.create(Items.WAXED_LIGHTLY_WEATHERED_CUT_COPPER)
			.input(Items.LIGHTLY_WEATHERED_CUT_COPPER)
			.input(Items.HONEYCOMB)
			.criterion("has_lightly_weathered_cut_copper", conditionsFromItem(Items.LIGHTLY_WEATHERED_CUT_COPPER))
			.offerTo(consumer, "waxed_lightly_weathered_cut_copper_from_honeycomb");
		ShapelessRecipeJsonFactory.create(Items.WAXED_CUT_COPPER_STAIRS)
			.input(Items.CUT_COPPER_STAIRS)
			.input(Items.HONEYCOMB)
			.criterion("has_copper_cut_stairs", conditionsFromItem(Items.CUT_COPPER_STAIRS))
			.offerTo(consumer, "waxed_copper_cut_stairs_from_honeycomb");
		ShapelessRecipeJsonFactory.create(Items.WAXED_SEMI_WEATHERED_CUT_COPPER_STAIRS)
			.input(Items.SEMI_WEATHERED_CUT_COPPER_STAIRS)
			.input(Items.HONEYCOMB)
			.criterion("has_semi_weathered_cut_copper_stairs", conditionsFromItem(Items.SEMI_WEATHERED_CUT_COPPER_STAIRS))
			.offerTo(consumer, "waxed_semi_weathered_cut_copper_stairs_from_honeycomb");
		ShapelessRecipeJsonFactory.create(Items.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_STAIRS)
			.input(Items.LIGHTLY_WEATHERED_CUT_COPPER_STAIRS)
			.input(Items.HONEYCOMB)
			.criterion("has_lightly_weathered_cut_copper_stairs", conditionsFromItem(Items.LIGHTLY_WEATHERED_CUT_COPPER_STAIRS))
			.offerTo(consumer, "waxed_lightly_weathered_cut_copper_stairs_from_honeycomb");
		ShapelessRecipeJsonFactory.create(Items.WAXED_CUT_COPPER_SLAB)
			.input(Items.CUT_COPPER_SLAB)
			.input(Items.HONEYCOMB)
			.criterion("has_copper_cut_slab", conditionsFromItem(Items.CUT_COPPER_SLAB))
			.offerTo(consumer, "waxed_copper_cut_slab_from_honeycomb");
		ShapelessRecipeJsonFactory.create(Items.WAXED_SEMI_WEATHERED_CUT_COPPER_SLAB)
			.input(Items.SEMI_WEATHERED_CUT_COPPER_SLAB)
			.input(Items.HONEYCOMB)
			.criterion("has_semi_weathered_cut_copper_slab", conditionsFromItem(Items.SEMI_WEATHERED_CUT_COPPER_SLAB))
			.offerTo(consumer, "waxed_semi_weathered_cut_copper_slab_from_honeycomb");
		ShapelessRecipeJsonFactory.create(Items.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_SLAB)
			.input(Items.LIGHTLY_WEATHERED_CUT_COPPER_SLAB)
			.input(Items.HONEYCOMB)
			.criterion("has_lightly_weathered_cut_copper_slab", conditionsFromItem(Items.LIGHTLY_WEATHERED_CUT_COPPER_SLAB))
			.offerTo(consumer, "waxed_lightly_weathered_cut_copper_slab_from_honeycomb");
		ShapedRecipeJsonFactory.create(Blocks.WAXED_CUT_COPPER, 4)
			.input('#', Blocks.WAXED_COPPER)
			.pattern("##")
			.pattern("##")
			.criterion("has_waxed_copper", conditionsFromItem(Blocks.WAXED_COPPER))
			.offerTo(consumer, "waxed_cut_copper_from_waxed_block");
		ShapedRecipeJsonFactory.create(Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER, 4)
			.input('#', Blocks.WAXED_LIGHTLY_WEATHERED_COPPER)
			.pattern("##")
			.pattern("##")
			.criterion("has_waxed_lightly_weathered_copper", conditionsFromItem(Blocks.WAXED_LIGHTLY_WEATHERED_COPPER))
			.offerTo(consumer, "waxed_lightly_weathered_cut_copper_from_waxed_block");
		ShapedRecipeJsonFactory.create(Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER, 4)
			.input('#', Blocks.WAXED_SEMI_WEATHERED_COPPER)
			.pattern("##")
			.pattern("##")
			.criterion("has_waxed_semi_weathered_copper", conditionsFromItem(Blocks.WAXED_SEMI_WEATHERED_COPPER))
			.offerTo(consumer, "waxed_semi_weathered_cut_copper_from_waxed_block");
		offerStonecutterRecipe(consumer, Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER);
		offerStonecutterRecipe(consumer, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER);
		offerStonecutterRecipe(consumer, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER);
		offerSlabRecipe(consumer, Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER);
		offerSlabRecipe(consumer, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER);
		offerSlabRecipe(consumer, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER);
		ShapelessRecipeJsonFactory.create(Items.CYAN_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.GREEN_DYE)
			.criterion("has_green_dye", conditionsFromItem(Items.GREEN_DYE))
			.criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_PRISMARINE)
			.input('S', Items.PRISMARINE_SHARD)
			.input('I', Items.BLACK_DYE)
			.pattern("SSS")
			.pattern("SIS")
			.pattern("SSS")
			.criterion("has_prismarine_shard", conditionsFromItem(Items.PRISMARINE_SHARD))
			.offerTo(consumer);
		offerStonecutterRecipe(consumer, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE);
		offerStonecutterRecipe(consumer, Blocks.PRISMARINE_BRICK_STAIRS, Blocks.PRISMARINE_BRICKS);
		offerStonecutterRecipe(consumer, Blocks.DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE);
		ShapedRecipeJsonFactory.create(Blocks.DAYLIGHT_DETECTOR)
			.input('Q', Items.QUARTZ)
			.input('G', Blocks.GLASS)
			.input('W', Ingredient.fromTag(ItemTags.WOODEN_SLABS))
			.pattern("GGG")
			.pattern("QQQ")
			.pattern("WWW")
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DETECTOR_RAIL, 6)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.STONE_PRESSURE_PLATE)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", conditionsFromItem(Blocks.RAIL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.DIAMOND, 9)
			.input(Blocks.DIAMOND_BLOCK)
			.criterion("has_diamond_block", conditionsFromItem(Blocks.DIAMOND_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_AXE)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DIAMOND_BLOCK)
			.input('#', Items.DIAMOND)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_BOOTS)
			.input('X', Items.DIAMOND)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_CHESTPLATE)
			.input('X', Items.DIAMOND)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_HELMET)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_HOE)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_LEGGINGS)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_PICKAXE)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_SHOVEL)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_SWORD)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DIORITE, 2)
			.input('Q', Items.QUARTZ)
			.input('C', Blocks.COBBLESTONE)
			.pattern("CQ")
			.pattern("QC")
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DISPENSER)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.input('X', Items.BOW)
			.pattern("###")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_bow", conditionsFromItem(Items.BOW))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DROPPER)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.pattern("# #")
			.pattern("#R#")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.EMERALD, 9)
			.input(Blocks.EMERALD_BLOCK)
			.criterion("has_emerald_block", conditionsFromItem(Blocks.EMERALD_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.EMERALD_BLOCK)
			.input('#', Items.EMERALD)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_emerald", conditionsFromItem(Items.EMERALD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ENCHANTING_TABLE)
			.input('B', Items.BOOK)
			.input('#', Blocks.OBSIDIAN)
			.input('D', Items.DIAMOND)
			.pattern(" B ")
			.pattern("D#D")
			.pattern("###")
			.criterion("has_obsidian", conditionsFromItem(Blocks.OBSIDIAN))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ENDER_CHEST)
			.input('#', Blocks.OBSIDIAN)
			.input('E', Items.ENDER_EYE)
			.pattern("###")
			.pattern("#E#")
			.pattern("###")
			.criterion("has_ender_eye", conditionsFromItem(Items.ENDER_EYE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.ENDER_EYE)
			.input(Items.ENDER_PEARL)
			.input(Items.BLAZE_POWDER)
			.criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.END_STONE_BRICKS, 4)
			.input('#', Blocks.END_STONE)
			.pattern("##")
			.pattern("##")
			.criterion("has_end_stone", conditionsFromItem(Blocks.END_STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.END_CRYSTAL)
			.input('T', Items.GHAST_TEAR)
			.input('E', Items.ENDER_EYE)
			.input('G', Blocks.GLASS)
			.pattern("GGG")
			.pattern("GEG")
			.pattern("GTG")
			.criterion("has_ender_eye", conditionsFromItem(Items.ENDER_EYE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.END_ROD, 4)
			.input('#', Items.POPPED_CHORUS_FRUIT)
			.input('/', Items.BLAZE_ROD)
			.pattern("/")
			.pattern("#")
			.criterion("has_chorus_fruit_popped", conditionsFromItem(Items.POPPED_CHORUS_FRUIT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.FERMENTED_SPIDER_EYE)
			.input(Items.SPIDER_EYE)
			.input(Blocks.BROWN_MUSHROOM)
			.input(Items.SUGAR)
			.criterion("has_spider_eye", conditionsFromItem(Items.SPIDER_EYE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.FIRE_CHARGE, 3)
			.input(Items.GUNPOWDER)
			.input(Items.BLAZE_POWDER)
			.input(Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.FIREWORK_ROCKET, 3)
			.input(Items.GUNPOWDER)
			.input(Items.PAPER)
			.criterion("has_gunpowder", conditionsFromItem(Items.GUNPOWDER))
			.offerTo(consumer, "firework_rocket_simple");
		ShapedRecipeJsonFactory.create(Items.FISHING_ROD)
			.input('#', Items.STICK)
			.input('X', Items.STRING)
			.pattern("  #")
			.pattern(" #X")
			.pattern("# X")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.FLINT_AND_STEEL)
			.input(Items.IRON_INGOT)
			.input(Items.FLINT)
			.criterion("has_flint", conditionsFromItem(Items.FLINT))
			.criterion("has_obsidian", conditionsFromItem(Blocks.OBSIDIAN))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.FLOWER_POT)
			.input('#', Items.BRICK)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brick", conditionsFromItem(Items.BRICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.FURNACE)
			.input('#', ItemTags.STONE_CRAFTING_MATERIALS)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_CRAFTING_MATERIALS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.FURNACE_MINECART)
			.input('A', Blocks.FURNACE)
			.input('B', Items.MINECART)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", conditionsFromItem(Items.MINECART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GLASS_BOTTLE, 3)
			.input('#', Blocks.GLASS)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_glass", conditionsFromItem(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GLASS_PANE, 16)
			.input('#', Blocks.GLASS)
			.pattern("###")
			.pattern("###")
			.criterion("has_glass", conditionsFromItem(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GLOWSTONE)
			.input('#', Items.GLOWSTONE_DUST)
			.pattern("##")
			.pattern("##")
			.criterion("has_glowstone_dust", conditionsFromItem(Items.GLOWSTONE_DUST))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_APPLE)
			.input('#', Items.GOLD_INGOT)
			.input('X', Items.APPLE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_AXE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_BOOTS)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_CARROT)
			.input('#', Items.GOLD_NUGGET)
			.input('X', Items.CARROT)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_nugget", conditionsFromItem(Items.GOLD_NUGGET))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_CHESTPLATE)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_HELMET)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_HOE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_LEGGINGS)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_PICKAXE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POWERED_RAIL, 6)
			.input('R', Items.REDSTONE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", conditionsFromItem(Blocks.RAIL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_SHOVEL)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_SWORD)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GOLD_BLOCK)
			.input('#', Items.GOLD_INGOT)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.GOLD_INGOT, 9)
			.input(Blocks.GOLD_BLOCK)
			.group("gold_ingot")
			.criterion("has_gold_block", conditionsFromItem(Blocks.GOLD_BLOCK))
			.offerTo(consumer, "gold_ingot_from_gold_block");
		ShapedRecipeJsonFactory.create(Items.GOLD_INGOT)
			.input('#', Items.GOLD_NUGGET)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group("gold_ingot")
			.criterion("has_gold_nugget", conditionsFromItem(Items.GOLD_NUGGET))
			.offerTo(consumer, "gold_ingot_from_nuggets");
		ShapelessRecipeJsonFactory.create(Items.GOLD_NUGGET, 9)
			.input(Items.GOLD_INGOT)
			.criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.GRANITE)
			.input(Blocks.DIORITE)
			.input(Items.QUARTZ)
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.GRAY_DYE, 2)
			.input(Items.BLACK_DYE)
			.input(Items.WHITE_DYE)
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.criterion("has_black_dye", conditionsFromItem(Items.BLACK_DYE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.HAY_BLOCK)
			.input('#', Items.WHEAT)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_wheat", conditionsFromItem(Items.WHEAT))
			.offerTo(consumer);
		createPressurePlateRecipe(consumer, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_INGOT);
		ShapelessRecipeJsonFactory.create(Items.HONEY_BOTTLE, 4)
			.input(Items.HONEY_BLOCK)
			.input(Items.GLASS_BOTTLE, 4)
			.criterion("has_honey_block", conditionsFromItem(Blocks.HONEY_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.HONEY_BLOCK, 1)
			.input('S', Items.HONEY_BOTTLE)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_honey_bottle", conditionsFromItem(Items.HONEY_BOTTLE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.HONEYCOMB_BLOCK)
			.input('H', Items.HONEYCOMB)
			.pattern("HH")
			.pattern("HH")
			.criterion("has_honeycomb", conditionsFromItem(Items.HONEYCOMB))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.HOPPER)
			.input('C', Blocks.CHEST)
			.input('I', Items.IRON_INGOT)
			.pattern("I I")
			.pattern("ICI")
			.pattern(" I ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.HOPPER_MINECART)
			.input('A', Blocks.HOPPER)
			.input('B', Items.MINECART)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", conditionsFromItem(Items.MINECART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_AXE)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.IRON_BARS, 16)
			.input('#', Items.IRON_INGOT)
			.pattern("###")
			.pattern("###")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.IRON_BLOCK)
			.input('#', Items.IRON_INGOT)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_BOOTS)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_CHESTPLATE)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.IRON_DOOR, 3)
			.input('#', Items.IRON_INGOT)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_HELMET)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_HOE)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.IRON_INGOT, 9)
			.input(Blocks.IRON_BLOCK)
			.group("iron_ingot")
			.criterion("has_iron_block", conditionsFromItem(Blocks.IRON_BLOCK))
			.offerTo(consumer, "iron_ingot_from_iron_block");
		ShapedRecipeJsonFactory.create(Items.IRON_INGOT)
			.input('#', Items.IRON_NUGGET)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group("iron_ingot")
			.criterion("has_iron_nugget", conditionsFromItem(Items.IRON_NUGGET))
			.offerTo(consumer, "iron_ingot_from_nuggets");
		ShapedRecipeJsonFactory.create(Items.IRON_LEGGINGS)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.IRON_NUGGET, 9)
			.input(Items.IRON_INGOT)
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_PICKAXE)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_SHOVEL)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_SWORD)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.IRON_TRAPDOOR)
			.input('#', Items.IRON_INGOT)
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ITEM_FRAME)
			.input('#', Items.STICK)
			.input('X', Items.LEATHER)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JUKEBOX)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.DIAMOND)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LADDER, 3)
			.input('#', Items.STICK)
			.pattern("# #")
			.pattern("###")
			.pattern("# #")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LAPIS_BLOCK)
			.input('#', Items.LAPIS_LAZULI)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_lapis", conditionsFromItem(Items.LAPIS_LAZULI))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LAPIS_LAZULI, 9)
			.input(Blocks.LAPIS_BLOCK)
			.criterion("has_lapis_block", conditionsFromItem(Blocks.LAPIS_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEAD, 2)
			.input('~', Items.STRING)
			.input('O', Items.SLIME_BALL)
			.pattern("~~ ")
			.pattern("~O ")
			.pattern("  ~")
			.criterion("has_slime_ball", conditionsFromItem(Items.SLIME_BALL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER)
			.input('#', Items.RABBIT_HIDE)
			.pattern("##")
			.pattern("##")
			.criterion("has_rabbit_hide", conditionsFromItem(Items.RABBIT_HIDE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER_BOOTS)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER_CHESTPLATE)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER_HELMET)
			.input('X', Items.LEATHER)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER_LEGGINGS)
			.input('X', Items.LEATHER)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER_HORSE_ARMOR)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LECTERN)
			.input('S', ItemTags.WOODEN_SLABS)
			.input('B', Blocks.BOOKSHELF)
			.pattern("SSS")
			.pattern(" B ")
			.pattern(" S ")
			.criterion("has_book", conditionsFromItem(Items.BOOK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LEVER)
			.input('#', Blocks.COBBLESTONE)
			.input('X', Items.STICK)
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", conditionsFromItem(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIGHT_BLUE_DYE)
			.input(Blocks.BLUE_ORCHID)
			.group("light_blue_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.BLUE_ORCHID))
			.offerTo(consumer, "light_blue_dye_from_blue_orchid");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_BLUE_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.WHITE_DYE)
			.group("light_blue_dye")
			.criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.offerTo(consumer, "light_blue_dye_from_blue_white_dye");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE)
			.input(Blocks.AZURE_BLUET)
			.group("light_gray_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.AZURE_BLUET))
			.offerTo(consumer, "light_gray_dye_from_azure_bluet");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE, 2)
			.input(Items.GRAY_DYE)
			.input(Items.WHITE_DYE)
			.group("light_gray_dye")
			.criterion("has_gray_dye", conditionsFromItem(Items.GRAY_DYE))
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.offerTo(consumer, "light_gray_dye_from_gray_white_dye");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE, 3)
			.input(Items.BLACK_DYE)
			.input(Items.WHITE_DYE, 2)
			.group("light_gray_dye")
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.criterion("has_black_dye", conditionsFromItem(Items.BLACK_DYE))
			.offerTo(consumer, "light_gray_dye_from_black_white_dye");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE)
			.input(Blocks.OXEYE_DAISY)
			.group("light_gray_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.OXEYE_DAISY))
			.offerTo(consumer, "light_gray_dye_from_oxeye_daisy");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE)
			.input(Blocks.WHITE_TULIP)
			.group("light_gray_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.WHITE_TULIP))
			.offerTo(consumer, "light_gray_dye_from_white_tulip");
		createPressurePlateRecipe(consumer, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT);
		ShapedRecipeJsonFactory.create(Blocks.LIGHTNING_ROD)
			.input('#', Items.COPPER_INGOT)
			.pattern("#")
			.pattern("#")
			.pattern("#")
			.criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIME_DYE, 2)
			.input(Items.GREEN_DYE)
			.input(Items.WHITE_DYE)
			.criterion("has_green_dye", conditionsFromItem(Items.GREEN_DYE))
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JACK_O_LANTERN)
			.input('A', Blocks.CARVED_PUMPKIN)
			.input('B', Blocks.TORCH)
			.pattern("A")
			.pattern("B")
			.criterion("has_carved_pumpkin", conditionsFromItem(Blocks.CARVED_PUMPKIN))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE)
			.input(Blocks.ALLIUM)
			.group("magenta_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.ALLIUM))
			.offerTo(consumer, "magenta_dye_from_allium");
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 4)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE, 2)
			.input(Items.WHITE_DYE)
			.group("magenta_dye")
			.criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_rose_red", conditionsFromItem(Items.RED_DYE))
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.offerTo(consumer, "magenta_dye_from_blue_red_white_dye");
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 3)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE)
			.input(Items.PINK_DYE)
			.group("magenta_dye")
			.criterion("has_pink_dye", conditionsFromItem(Items.PINK_DYE))
			.criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_red_dye", conditionsFromItem(Items.RED_DYE))
			.offerTo(consumer, "magenta_dye_from_blue_red_pink");
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 2)
			.input(Blocks.LILAC)
			.group("magenta_dye")
			.criterion("has_double_plant", conditionsFromItem(Blocks.LILAC))
			.offerTo(consumer, "magenta_dye_from_lilac");
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 2)
			.input(Items.PURPLE_DYE)
			.input(Items.PINK_DYE)
			.group("magenta_dye")
			.criterion("has_pink_dye", conditionsFromItem(Items.PINK_DYE))
			.criterion("has_purple_dye", conditionsFromItem(Items.PURPLE_DYE))
			.offerTo(consumer, "magenta_dye_from_purple_and_pink");
		ShapedRecipeJsonFactory.create(Blocks.MAGMA_BLOCK)
			.input('#', Items.MAGMA_CREAM)
			.pattern("##")
			.pattern("##")
			.criterion("has_magma_cream", conditionsFromItem(Items.MAGMA_CREAM))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MAGMA_CREAM)
			.input(Items.BLAZE_POWDER)
			.input(Items.SLIME_BALL)
			.criterion("has_blaze_powder", conditionsFromItem(Items.BLAZE_POWDER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.MAP)
			.input('#', Items.PAPER)
			.input('X', Items.COMPASS)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_compass", conditionsFromItem(Items.COMPASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MELON)
			.input('M', Items.MELON_SLICE)
			.pattern("MMM")
			.pattern("MMM")
			.pattern("MMM")
			.criterion("has_melon", conditionsFromItem(Items.MELON_SLICE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MELON_SEEDS).input(Items.MELON_SLICE).criterion("has_melon", conditionsFromItem(Items.MELON_SLICE)).offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.MINECART)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern("###")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.MOSSY_COBBLESTONE)
			.input(Blocks.COBBLESTONE)
			.input(Blocks.VINE)
			.criterion("has_vine", conditionsFromItem(Blocks.VINE))
			.offerTo(consumer);
		offerWallRecipe(consumer, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE);
		ShapelessRecipeJsonFactory.create(Blocks.MOSSY_STONE_BRICKS)
			.input(Blocks.STONE_BRICKS)
			.input(Blocks.VINE)
			.criterion("has_mossy_cobblestone", conditionsFromItem(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MUSHROOM_STEW)
			.input(Blocks.BROWN_MUSHROOM)
			.input(Blocks.RED_MUSHROOM)
			.input(Items.BOWL)
			.criterion("has_mushroom_stew", conditionsFromItem(Items.MUSHROOM_STEW))
			.criterion("has_bowl", conditionsFromItem(Items.BOWL))
			.criterion("has_brown_mushroom", conditionsFromItem(Blocks.BROWN_MUSHROOM))
			.criterion("has_red_mushroom", conditionsFromItem(Blocks.RED_MUSHROOM))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NETHER_BRICKS)
			.input('N', Items.NETHER_BRICK)
			.pattern("NN")
			.pattern("NN")
			.criterion("has_netherbrick", conditionsFromItem(Items.NETHER_BRICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NETHER_BRICK_FENCE, 6)
			.input('#', Blocks.NETHER_BRICKS)
			.input('-', Items.NETHER_BRICK)
			.pattern("#-#")
			.pattern("#-#")
			.criterion("has_nether_brick", conditionsFromItem(Blocks.NETHER_BRICKS))
			.offerTo(consumer);
		offerSlabRecipe(consumer, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICKS);
		offerStonecutterRecipe(consumer, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICKS);
		ShapedRecipeJsonFactory.create(Blocks.NETHER_WART_BLOCK)
			.input('#', Items.NETHER_WART)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_nether_wart", conditionsFromItem(Items.NETHER_WART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NOTE_BLOCK)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.REDSTONE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.OBSERVER)
			.input('Q', Items.QUARTZ)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.pattern("RRQ")
			.pattern("###")
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.ORANGE_DYE)
			.input(Blocks.ORANGE_TULIP)
			.group("orange_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.ORANGE_TULIP))
			.offerTo(consumer, "orange_dye_from_orange_tulip");
		ShapelessRecipeJsonFactory.create(Items.ORANGE_DYE, 2)
			.input(Items.RED_DYE)
			.input(Items.YELLOW_DYE)
			.group("orange_dye")
			.criterion("has_red_dye", conditionsFromItem(Items.RED_DYE))
			.criterion("has_yellow_dye", conditionsFromItem(Items.YELLOW_DYE))
			.offerTo(consumer, "orange_dye_from_red_yellow");
		ShapedRecipeJsonFactory.create(Items.PAINTING)
			.input('#', Items.STICK)
			.input('X', Ingredient.fromTag(ItemTags.WOOL))
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_wool", conditionsFromTag(ItemTags.WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.PAPER, 3)
			.input('#', Blocks.SUGAR_CANE)
			.pattern("###")
			.criterion("has_reeds", conditionsFromItem(Blocks.SUGAR_CANE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.QUARTZ_PILLAR, 2)
			.input('#', Blocks.QUARTZ_BLOCK)
			.pattern("#")
			.pattern("#")
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.PACKED_ICE).input(Blocks.ICE, 9).criterion("has_ice", conditionsFromItem(Blocks.ICE)).offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PINK_DYE, 2)
			.input(Blocks.PEONY)
			.group("pink_dye")
			.criterion("has_double_plant", conditionsFromItem(Blocks.PEONY))
			.offerTo(consumer, "pink_dye_from_peony");
		ShapelessRecipeJsonFactory.create(Items.PINK_DYE)
			.input(Blocks.PINK_TULIP)
			.group("pink_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.PINK_TULIP))
			.offerTo(consumer, "pink_dye_from_pink_tulip");
		ShapelessRecipeJsonFactory.create(Items.PINK_DYE, 2)
			.input(Items.RED_DYE)
			.input(Items.WHITE_DYE)
			.group("pink_dye")
			.criterion("has_white_dye", conditionsFromItem(Items.WHITE_DYE))
			.criterion("has_red_dye", conditionsFromItem(Items.RED_DYE))
			.offerTo(consumer, "pink_dye_from_red_white_dye");
		ShapedRecipeJsonFactory.create(Blocks.PISTON)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.input('T', ItemTags.PLANKS)
			.input('X', Items.IRON_INGOT)
			.pattern("TTT")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(consumer);
		offerPolishedStoneRecipe(consumer, Blocks.POLISHED_BASALT, Blocks.BASALT);
		offerPolishedStoneRecipe(consumer, Blocks.POLISHED_GRANITE, Blocks.GRANITE);
		offerPolishedStoneRecipe(consumer, Blocks.POLISHED_DIORITE, Blocks.DIORITE);
		offerPolishedStoneRecipe(consumer, Blocks.POLISHED_ANDESITE, Blocks.ANDESITE);
		ShapedRecipeJsonFactory.create(Blocks.PRISMARINE)
			.input('S', Items.PRISMARINE_SHARD)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_prismarine_shard", conditionsFromItem(Items.PRISMARINE_SHARD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PRISMARINE_BRICKS)
			.input('S', Items.PRISMARINE_SHARD)
			.pattern("SSS")
			.pattern("SSS")
			.pattern("SSS")
			.criterion("has_prismarine_shard", conditionsFromItem(Items.PRISMARINE_SHARD))
			.offerTo(consumer);
		offerSlabRecipe(consumer, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE);
		offerSlabRecipe(consumer, Blocks.PRISMARINE_BRICK_SLAB, Blocks.PRISMARINE_BRICKS);
		offerSlabRecipe(consumer, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE);
		ShapelessRecipeJsonFactory.create(Items.PUMPKIN_PIE)
			.input(Blocks.PUMPKIN)
			.input(Items.SUGAR)
			.input(Items.EGG)
			.criterion("has_carved_pumpkin", conditionsFromItem(Blocks.CARVED_PUMPKIN))
			.criterion("has_pumpkin", conditionsFromItem(Blocks.PUMPKIN))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PUMPKIN_SEEDS, 4)
			.input(Blocks.PUMPKIN)
			.criterion("has_pumpkin", conditionsFromItem(Blocks.PUMPKIN))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PURPLE_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE)
			.criterion("has_blue_dye", conditionsFromItem(Items.BLUE_DYE))
			.criterion("has_red_dye", conditionsFromItem(Items.RED_DYE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SHULKER_BOX)
			.input('#', Blocks.CHEST)
			.input('-', Items.SHULKER_SHELL)
			.pattern("-")
			.pattern("#")
			.pattern("-")
			.criterion("has_shulker_shell", conditionsFromItem(Items.SHULKER_SHELL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PURPUR_BLOCK, 4)
			.input('F', Items.POPPED_CHORUS_FRUIT)
			.pattern("FF")
			.pattern("FF")
			.criterion("has_chorus_fruit_popped", conditionsFromItem(Items.POPPED_CHORUS_FRUIT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PURPUR_PILLAR)
			.input('#', Blocks.PURPUR_SLAB)
			.pattern("#")
			.pattern("#")
			.criterion("has_purpur_block", conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(consumer);
		createSlabRecipe(Blocks.PURPUR_SLAB, Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR))
			.criterion("has_purpur_block", conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(consumer);
		createStairsRecipe(Blocks.PURPUR_STAIRS, Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR))
			.criterion("has_purpur_block", conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.QUARTZ_BLOCK)
			.input('#', Items.QUARTZ)
			.pattern("##")
			.pattern("##")
			.criterion("has_quartz", conditionsFromItem(Items.QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.QUARTZ_BRICKS, 4)
			.input('#', Blocks.QUARTZ_BLOCK)
			.pattern("##")
			.pattern("##")
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer);
		createSlabRecipe(Blocks.QUARTZ_SLAB, Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR))
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(consumer);
		createStairsRecipe(Blocks.QUARTZ_STAIRS, Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR))
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.QUARTZ_PILLAR))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.RABBIT_STEW)
			.input(Items.BAKED_POTATO)
			.input(Items.COOKED_RABBIT)
			.input(Items.BOWL)
			.input(Items.CARROT)
			.input(Blocks.BROWN_MUSHROOM)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", conditionsFromItem(Items.COOKED_RABBIT))
			.offerTo(consumer, "rabbit_stew_from_brown_mushroom");
		ShapelessRecipeJsonFactory.create(Items.RABBIT_STEW)
			.input(Items.BAKED_POTATO)
			.input(Items.COOKED_RABBIT)
			.input(Items.BOWL)
			.input(Items.CARROT)
			.input(Blocks.RED_MUSHROOM)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", conditionsFromItem(Items.COOKED_RABBIT))
			.offerTo(consumer, "rabbit_stew_from_red_mushroom");
		ShapedRecipeJsonFactory.create(Blocks.RAIL, 16)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("X X")
			.criterion("has_minecart", conditionsFromItem(Items.MINECART))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.REDSTONE, 9)
			.input(Blocks.REDSTONE_BLOCK)
			.criterion("has_redstone_block", conditionsFromItem(Blocks.REDSTONE_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.REDSTONE_BLOCK)
			.input('#', Items.REDSTONE)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.REDSTONE_LAMP)
			.input('R', Items.REDSTONE)
			.input('G', Blocks.GLOWSTONE)
			.pattern(" R ")
			.pattern("RGR")
			.pattern(" R ")
			.criterion("has_glowstone", conditionsFromItem(Blocks.GLOWSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.REDSTONE_TORCH)
			.input('#', Items.STICK)
			.input('X', Items.REDSTONE)
			.pattern("X")
			.pattern("#")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.RED_DYE)
			.input(Items.BEETROOT)
			.group("red_dye")
			.criterion("has_beetroot", conditionsFromItem(Items.BEETROOT))
			.offerTo(consumer, "red_dye_from_beetroot");
		ShapelessRecipeJsonFactory.create(Items.RED_DYE)
			.input(Blocks.POPPY)
			.group("red_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.POPPY))
			.offerTo(consumer, "red_dye_from_poppy");
		ShapelessRecipeJsonFactory.create(Items.RED_DYE, 2)
			.input(Blocks.ROSE_BUSH)
			.group("red_dye")
			.criterion("has_double_plant", conditionsFromItem(Blocks.ROSE_BUSH))
			.offerTo(consumer, "red_dye_from_rose_bush");
		ShapelessRecipeJsonFactory.create(Items.RED_DYE)
			.input(Blocks.RED_TULIP)
			.group("red_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.RED_TULIP))
			.offerTo(consumer, "red_dye_from_tulip");
		ShapedRecipeJsonFactory.create(Blocks.RED_NETHER_BRICKS)
			.input('W', Items.NETHER_WART)
			.input('N', Items.NETHER_BRICK)
			.pattern("NW")
			.pattern("WN")
			.criterion("has_nether_wart", conditionsFromItem(Items.NETHER_WART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_SANDSTONE)
			.input('#', Blocks.RED_SAND)
			.pattern("##")
			.pattern("##")
			.criterion("has_sand", conditionsFromItem(Blocks.RED_SAND))
			.offerTo(consumer);
		createSlabRecipe(Blocks.RED_SANDSTONE_SLAB, Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE))
			.offerTo(consumer);
		offerSlabRecipe(consumer, Blocks.CUT_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE);
		createStairsRecipe(Blocks.RED_SANDSTONE_STAIRS, Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE))
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", conditionsFromItem(Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_cut_red_sandstone", conditionsFromItem(Blocks.CUT_RED_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.REPEATER)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('X', Items.REDSTONE)
			.input('I', Blocks.STONE)
			.pattern("#X#")
			.pattern("III")
			.criterion("has_redstone_torch", conditionsFromItem(Blocks.REDSTONE_TORCH))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SANDSTONE)
			.input('#', Blocks.SAND)
			.pattern("##")
			.pattern("##")
			.criterion("has_sand", conditionsFromItem(Blocks.SAND))
			.offerTo(consumer);
		createSlabRecipe(Blocks.SANDSTONE_SLAB, Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE))
			.criterion("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.criterion("has_chiseled_sandstone", conditionsFromItem(Blocks.CHISELED_SANDSTONE))
			.offerTo(consumer);
		offerSlabRecipe(consumer, Blocks.CUT_SANDSTONE_SLAB, Blocks.CUT_SANDSTONE);
		createStairsRecipe(Blocks.SANDSTONE_STAIRS, Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE))
			.criterion("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.criterion("has_chiseled_sandstone", conditionsFromItem(Blocks.CHISELED_SANDSTONE))
			.criterion("has_cut_sandstone", conditionsFromItem(Blocks.CUT_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SEA_LANTERN)
			.input('S', Items.PRISMARINE_SHARD)
			.input('C', Items.PRISMARINE_CRYSTALS)
			.pattern("SCS")
			.pattern("CCC")
			.pattern("SCS")
			.criterion("has_prismarine_crystals", conditionsFromItem(Items.PRISMARINE_CRYSTALS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.SHEARS)
			.input('#', Items.IRON_INGOT)
			.pattern(" #")
			.pattern("# ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.SHIELD)
			.input('W', ItemTags.PLANKS)
			.input('o', Items.IRON_INGOT)
			.pattern("WoW")
			.pattern("WWW")
			.pattern(" W ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SLIME_BLOCK)
			.input('#', Items.SLIME_BALL)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_slime_ball", conditionsFromItem(Items.SLIME_BALL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.SLIME_BALL, 9)
			.input(Blocks.SLIME_BLOCK)
			.criterion("has_slime", conditionsFromItem(Blocks.SLIME_BLOCK))
			.offerTo(consumer);
		offerCutCopperRecipe(consumer, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE);
		offerCutCopperRecipe(consumer, Blocks.CUT_SANDSTONE, Blocks.SANDSTONE);
		ShapedRecipeJsonFactory.create(Blocks.SNOW_BLOCK)
			.input('#', Items.SNOWBALL)
			.pattern("##")
			.pattern("##")
			.criterion("has_snowball", conditionsFromItem(Items.SNOWBALL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SNOW, 6)
			.input('#', Blocks.SNOW_BLOCK)
			.pattern("###")
			.criterion("has_snowball", conditionsFromItem(Items.SNOWBALL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SOUL_CAMPFIRE)
			.input('L', ItemTags.LOGS)
			.input('S', Items.STICK)
			.input('#', ItemTags.SOUL_FIRE_BASE_BLOCKS)
			.pattern(" S ")
			.pattern("S#S")
			.pattern("LLL")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.criterion("has_soul_sand", conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GLISTERING_MELON_SLICE)
			.input('#', Items.GOLD_NUGGET)
			.input('X', Items.MELON_SLICE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_melon", conditionsFromItem(Items.MELON_SLICE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.SPECTRAL_ARROW, 2)
			.input('#', Items.GLOWSTONE_DUST)
			.input('X', Items.ARROW)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_glowstone_dust", conditionsFromItem(Items.GLOWSTONE_DUST))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.SPYGLASS)
			.input('#', Items.AMETHYST_SHARD)
			.input('X', Items.COPPER_INGOT)
			.pattern(" # ")
			.pattern(" X ")
			.pattern(" X ")
			.criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STICK, 4)
			.input('#', ItemTags.PLANKS)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_planks", conditionsFromTag(ItemTags.PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STICK, 1)
			.input('#', Blocks.BAMBOO)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_bamboo", conditionsFromItem(Blocks.BAMBOO))
			.offerTo(consumer, "stick_from_bamboo_item");
		ShapedRecipeJsonFactory.create(Blocks.STICKY_PISTON)
			.input('P', Blocks.PISTON)
			.input('S', Items.SLIME_BALL)
			.pattern("S")
			.pattern("P")
			.criterion("has_slime_ball", conditionsFromItem(Items.SLIME_BALL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STONE_BRICKS, 4)
			.input('#', Blocks.STONE)
			.pattern("##")
			.pattern("##")
			.criterion("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STONE_AXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(consumer);
		createSlabRecipe(Blocks.STONE_BRICK_SLAB, Ingredient.ofItems(Blocks.STONE_BRICKS))
			.criterion("has_stone_bricks", conditionsFromTag(ItemTags.STONE_BRICKS))
			.offerTo(consumer);
		createStairsRecipe(Blocks.STONE_BRICK_STAIRS, Ingredient.ofItems(Blocks.STONE_BRICKS))
			.criterion("has_stone_bricks", conditionsFromTag(ItemTags.STONE_BRICKS))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.STONE_BUTTON).input(Blocks.STONE).criterion("has_stone", conditionsFromItem(Blocks.STONE)).offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STONE_HOE)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STONE_PICKAXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(consumer);
		createPressurePlateRecipe(consumer, Blocks.STONE_PRESSURE_PLATE, Items.STONE);
		ShapedRecipeJsonFactory.create(Items.STONE_SHOVEL)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(consumer);
		offerSlabRecipe(consumer, Blocks.STONE_SLAB, Blocks.STONE);
		offerSlabRecipe(consumer, Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE);
		offerStonecutterRecipe(consumer, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE);
		ShapedRecipeJsonFactory.create(Items.STONE_SWORD)
			.input('#', Items.STICK)
			.input('X', ItemTags.STONE_TOOL_MATERIALS)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.WHITE_WOOL)
			.input('#', Items.STRING)
			.pattern("##")
			.pattern("##")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(consumer, "white_wool_from_string");
		ShapelessRecipeJsonFactory.create(Items.SUGAR)
			.input(Blocks.SUGAR_CANE)
			.group("sugar")
			.criterion("has_reeds", conditionsFromItem(Blocks.SUGAR_CANE))
			.offerTo(consumer, "sugar_from_sugar_cane");
		ShapelessRecipeJsonFactory.create(Items.SUGAR, 3)
			.input(Items.HONEY_BOTTLE)
			.group("sugar")
			.criterion("has_honey_bottle", conditionsFromItem(Items.HONEY_BOTTLE))
			.offerTo(consumer, "sugar_from_honey_bottle");
		ShapedRecipeJsonFactory.create(Blocks.TARGET)
			.input('H', Items.HAY_BLOCK)
			.input('R', Items.REDSTONE)
			.pattern(" R ")
			.pattern("RHR")
			.pattern(" R ")
			.criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
			.criterion("has_hay_block", conditionsFromItem(Blocks.HAY_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.TNT)
			.input('#', Ingredient.ofItems(Blocks.SAND, Blocks.RED_SAND))
			.input('X', Items.GUNPOWDER)
			.pattern("X#X")
			.pattern("#X#")
			.pattern("X#X")
			.criterion("has_gunpowder", conditionsFromItem(Items.GUNPOWDER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.TNT_MINECART)
			.input('A', Blocks.TNT)
			.input('B', Items.MINECART)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", conditionsFromItem(Items.MINECART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.TORCH, 4)
			.input('#', Items.STICK)
			.input('X', Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.pattern("X")
			.pattern("#")
			.criterion("has_stone_pickaxe", conditionsFromItem(Items.STONE_PICKAXE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SOUL_TORCH, 4)
			.input('X', Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.input('#', Items.STICK)
			.input('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
			.pattern("X")
			.pattern("#")
			.pattern("S")
			.criterion("has_soul_sand", conditionsFromTag(ItemTags.SOUL_FIRE_BASE_BLOCKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LANTERN)
			.input('#', Items.TORCH)
			.input('X', Items.IRON_NUGGET)
			.pattern("XXX")
			.pattern("X#X")
			.pattern("XXX")
			.criterion("has_iron_nugget", conditionsFromItem(Items.IRON_NUGGET))
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SOUL_LANTERN)
			.input('#', Items.SOUL_TORCH)
			.input('X', Items.IRON_NUGGET)
			.pattern("XXX")
			.pattern("X#X")
			.pattern("XXX")
			.criterion("has_soul_torch", conditionsFromItem(Items.SOUL_TORCH))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.TRAPPED_CHEST)
			.input(Blocks.CHEST)
			.input(Blocks.TRIPWIRE_HOOK)
			.criterion("has_tripwire_hook", conditionsFromItem(Blocks.TRIPWIRE_HOOK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.TRIPWIRE_HOOK, 2)
			.input('#', ItemTags.PLANKS)
			.input('S', Items.STICK)
			.input('I', Items.IRON_INGOT)
			.pattern("I")
			.pattern("S")
			.pattern("#")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.TURTLE_HELMET)
			.input('X', Items.SCUTE)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_scute", conditionsFromItem(Items.SCUTE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WHEAT, 9).input(Blocks.HAY_BLOCK).criterion("has_hay_block", conditionsFromItem(Blocks.HAY_BLOCK)).offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WHITE_DYE)
			.input(Items.BONE_MEAL)
			.group("white_dye")
			.criterion("has_bone_meal", conditionsFromItem(Items.BONE_MEAL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WHITE_DYE)
			.input(Blocks.LILY_OF_THE_VALLEY)
			.group("white_dye")
			.criterion("has_white_flower", conditionsFromItem(Blocks.LILY_OF_THE_VALLEY))
			.offerTo(consumer, "white_dye_from_lily_of_the_valley");
		ShapedRecipeJsonFactory.create(Items.WOODEN_AXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WOODEN_HOE)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WOODEN_PICKAXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WOODEN_SHOVEL)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WOODEN_SWORD)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_stick", conditionsFromItem(Items.STICK))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WRITABLE_BOOK)
			.input(Items.BOOK)
			.input(Items.INK_SAC)
			.input(Items.FEATHER)
			.criterion("has_book", conditionsFromItem(Items.BOOK))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.YELLOW_DYE)
			.input(Blocks.DANDELION)
			.group("yellow_dye")
			.criterion("has_yellow_flower", conditionsFromItem(Blocks.DANDELION))
			.offerTo(consumer, "yellow_dye_from_dandelion");
		ShapelessRecipeJsonFactory.create(Items.YELLOW_DYE, 2)
			.input(Blocks.SUNFLOWER)
			.group("yellow_dye")
			.criterion("has_double_plant", conditionsFromItem(Blocks.SUNFLOWER))
			.offerTo(consumer, "yellow_dye_from_sunflower");
		ShapelessRecipeJsonFactory.create(Items.DRIED_KELP, 9)
			.input(Blocks.DRIED_KELP_BLOCK)
			.criterion("has_dried_kelp_block", conditionsFromItem(Blocks.DRIED_KELP_BLOCK))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.DRIED_KELP_BLOCK)
			.input(Items.DRIED_KELP, 9)
			.criterion("has_dried_kelp", conditionsFromItem(Items.DRIED_KELP))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CONDUIT)
			.input('#', Items.NAUTILUS_SHELL)
			.input('X', Items.HEART_OF_THE_SEA)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_nautilus_core", conditionsFromItem(Items.HEART_OF_THE_SEA))
			.criterion("has_nautilus_shell", conditionsFromItem(Items.NAUTILUS_SHELL))
			.offerTo(consumer);
		offerStonecutterRecipe(consumer, Blocks.POLISHED_GRANITE_STAIRS, Blocks.POLISHED_GRANITE);
		offerStonecutterRecipe(consumer, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE);
		offerStonecutterRecipe(consumer, Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICKS);
		offerStonecutterRecipe(consumer, Blocks.POLISHED_DIORITE_STAIRS, Blocks.POLISHED_DIORITE);
		offerStonecutterRecipe(consumer, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE);
		offerStonecutterRecipe(consumer, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE_BRICKS);
		offerStonecutterRecipe(consumer, Blocks.STONE_STAIRS, Blocks.STONE);
		offerStonecutterRecipe(consumer, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE);
		offerStonecutterRecipe(consumer, Blocks.SMOOTH_QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ);
		offerStonecutterRecipe(consumer, Blocks.GRANITE_STAIRS, Blocks.GRANITE);
		offerStonecutterRecipe(consumer, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE);
		offerStonecutterRecipe(consumer, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICKS);
		offerStonecutterRecipe(consumer, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE);
		offerStonecutterRecipe(consumer, Blocks.DIORITE_STAIRS, Blocks.DIORITE);
		offerSlabRecipe(consumer, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE);
		offerSlabRecipe(consumer, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE);
		offerSlabRecipe(consumer, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICKS);
		offerSlabRecipe(consumer, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE);
		offerSlabRecipe(consumer, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE);
		offerSlabRecipe(consumer, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE_BRICKS);
		offerSlabRecipe(consumer, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE);
		offerSlabRecipe(consumer, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ);
		offerSlabRecipe(consumer, Blocks.GRANITE_SLAB, Blocks.GRANITE);
		offerSlabRecipe(consumer, Blocks.ANDESITE_SLAB, Blocks.ANDESITE);
		offerSlabRecipe(consumer, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICKS);
		offerSlabRecipe(consumer, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE);
		offerSlabRecipe(consumer, Blocks.DIORITE_SLAB, Blocks.DIORITE);
		offerWallRecipe(consumer, Blocks.BRICK_WALL, Blocks.BRICKS);
		offerWallRecipe(consumer, Blocks.PRISMARINE_WALL, Blocks.PRISMARINE);
		offerWallRecipe(consumer, Blocks.RED_SANDSTONE_WALL, Blocks.RED_SANDSTONE);
		offerWallRecipe(consumer, Blocks.MOSSY_STONE_BRICK_WALL, Blocks.MOSSY_STONE_BRICKS);
		offerWallRecipe(consumer, Blocks.GRANITE_WALL, Blocks.GRANITE);
		offerWallRecipe(consumer, Blocks.STONE_BRICK_WALL, Blocks.STONE_BRICKS);
		offerWallRecipe(consumer, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_BRICKS);
		offerWallRecipe(consumer, Blocks.ANDESITE_WALL, Blocks.ANDESITE);
		offerWallRecipe(consumer, Blocks.RED_NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICKS);
		offerWallRecipe(consumer, Blocks.SANDSTONE_WALL, Blocks.SANDSTONE);
		offerWallRecipe(consumer, Blocks.END_STONE_BRICK_WALL, Blocks.END_STONE_BRICKS);
		offerWallRecipe(consumer, Blocks.DIORITE_WALL, Blocks.DIORITE);
		ShapelessRecipeJsonFactory.create(Items.CREEPER_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.CREEPER_HEAD)
			.criterion("has_creeper_head", conditionsFromItem(Items.CREEPER_HEAD))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.SKULL_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.WITHER_SKELETON_SKULL)
			.criterion("has_wither_skeleton_skull", conditionsFromItem(Items.WITHER_SKELETON_SKULL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.FLOWER_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Blocks.OXEYE_DAISY)
			.criterion("has_oxeye_daisy", conditionsFromItem(Blocks.OXEYE_DAISY))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MOJANG_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.ENCHANTED_GOLDEN_APPLE)
			.criterion("has_enchanted_golden_apple", conditionsFromItem(Items.ENCHANTED_GOLDEN_APPLE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SCAFFOLDING, 6)
			.input('~', Items.STRING)
			.input('I', Blocks.BAMBOO)
			.pattern("I~I")
			.pattern("I I")
			.pattern("I I")
			.criterion("has_bamboo", conditionsFromItem(Blocks.BAMBOO))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GRINDSTONE)
			.input('I', Items.STICK)
			.input('-', Blocks.STONE_SLAB)
			.input('#', ItemTags.PLANKS)
			.pattern("I-I")
			.pattern("# #")
			.criterion("has_stone_slab", conditionsFromItem(Blocks.STONE_SLAB))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BLAST_FURNACE)
			.input('#', Blocks.SMOOTH_STONE)
			.input('X', Blocks.FURNACE)
			.input('I', Items.IRON_INGOT)
			.pattern("III")
			.pattern("IXI")
			.pattern("###")
			.criterion("has_smooth_stone", conditionsFromItem(Blocks.SMOOTH_STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMOKER)
			.input('#', ItemTags.LOGS)
			.input('X', Blocks.FURNACE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_furnace", conditionsFromItem(Blocks.FURNACE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CARTOGRAPHY_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.PAPER)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_paper", conditionsFromItem(Items.PAPER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMITHING_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.IRON_INGOT)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.FLETCHING_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.FLINT)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_flint", conditionsFromItem(Items.FLINT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STONECUTTER)
			.input('I', Items.IRON_INGOT)
			.input('#', Blocks.STONE)
			.pattern(" I ")
			.pattern("###")
			.criterion("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LODESTONE)
			.input('S', Items.CHISELED_STONE_BRICKS)
			.input('#', Items.NETHERITE_INGOT)
			.pattern("SSS")
			.pattern("S#S")
			.pattern("SSS")
			.criterion("has_netherite_ingot", conditionsFromItem(Items.NETHERITE_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NETHERITE_BLOCK)
			.input('#', Items.NETHERITE_INGOT)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_netherite_ingot", conditionsFromItem(Items.NETHERITE_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.NETHERITE_INGOT, 9)
			.input(Blocks.NETHERITE_BLOCK)
			.group("netherite_ingot")
			.criterion("has_netherite_block", conditionsFromItem(Blocks.NETHERITE_BLOCK))
			.offerTo(consumer, "netherite_ingot_from_netherite_block");
		ShapelessRecipeJsonFactory.create(Items.NETHERITE_INGOT)
			.input(Items.NETHERITE_SCRAP, 4)
			.input(Items.GOLD_INGOT, 4)
			.group("netherite_ingot")
			.criterion("has_netherite_scrap", conditionsFromItem(Items.NETHERITE_SCRAP))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RESPAWN_ANCHOR)
			.input('O', Blocks.CRYING_OBSIDIAN)
			.input('G', Blocks.GLOWSTONE)
			.pattern("OOO")
			.pattern("GGG")
			.pattern("OOO")
			.criterion("has_obsidian", conditionsFromItem(Blocks.CRYING_OBSIDIAN))
			.offerTo(consumer);
		offerStonecutterRecipe(consumer, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE);
		offerStonecutterRecipe(consumer, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE);
		offerStonecutterRecipe(consumer, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICKS);
		offerSlabRecipe(consumer, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE);
		offerSlabRecipe(consumer, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE);
		offerSlabRecipe(consumer, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICKS);
		offerPolishedStoneRecipe(consumer, Blocks.POLISHED_BLACKSTONE, Blocks.BLACKSTONE);
		offerPolishedStoneRecipe(consumer, Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE);
		createChiseledBlockRecipe(Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_SLAB)
			.criterion("has_polished_blackstone", conditionsFromItem(Blocks.POLISHED_BLACKSTONE))
			.offerTo(consumer);
		offerWallRecipe(consumer, Blocks.BLACKSTONE_WALL, Blocks.BLACKSTONE);
		offerWallRecipe(consumer, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE);
		offerWallRecipe(consumer, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICKS);
		ShapelessRecipeJsonFactory.create(Blocks.POLISHED_BLACKSTONE_BUTTON)
			.input(Blocks.POLISHED_BLACKSTONE)
			.criterion("has_polished_blackstone", conditionsFromItem(Blocks.POLISHED_BLACKSTONE))
			.offerTo(consumer);
		createPressurePlateRecipe(consumer, Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, Blocks.POLISHED_BLACKSTONE);
		ShapedRecipeJsonFactory.create(Blocks.CHAIN)
			.input('I', Items.IRON_INGOT)
			.input('N', Items.IRON_NUGGET)
			.pattern("N")
			.pattern("I")
			.pattern("N")
			.criterion("has_iron_nugget", conditionsFromItem(Items.IRON_NUGGET))
			.criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CANDLE)
			.input('S', Items.STRING)
			.input('H', Items.HONEYCOMB)
			.pattern("S")
			.pattern("H")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.criterion("has_honeycomb", conditionsFromItem(Items.HONEYCOMB))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.TINTED_GLASS, 2)
			.input('G', Blocks.GLASS)
			.input('S', Items.AMETHYST_SHARD)
			.pattern(" S ")
			.pattern("SGS")
			.pattern(" S ")
			.criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.AMETHYST_BLOCK)
			.input('S', Items.AMETHYST_SHARD)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD))
			.offerTo(consumer);
		ComplexRecipeJsonFactory.create(RecipeSerializer.ARMOR_DYE).offerTo(consumer, "armor_dye");
		ComplexRecipeJsonFactory.create(RecipeSerializer.BANNER_DUPLICATE).offerTo(consumer, "banner_duplicate");
		ComplexRecipeJsonFactory.create(RecipeSerializer.BOOK_CLONING).offerTo(consumer, "book_cloning");
		ShapedRecipeJsonFactory.create(Items.BUNDLE)
			.input('#', Items.RABBIT_HIDE)
			.input('-', Items.STRING)
			.pattern("-#-")
			.pattern("# #")
			.pattern("###")
			.criterion("has_string", conditionsFromItem(Items.STRING))
			.offerTo(consumer);
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
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.POTATO), Items.BAKED_POTATO, 0.35F, 200)
			.criterion("has_potato", conditionsFromItem(Items.POTATO))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.CLAY_BALL), Items.BRICK, 0.3F, 200)
			.criterion("has_clay_ball", conditionsFromItem(Items.CLAY_BALL))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.LOGS_THAT_BURN), Items.CHARCOAL, 0.15F, 200)
			.criterion("has_log", conditionsFromTag(ItemTags.LOGS_THAT_BURN))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.CHORUS_FRUIT), Items.POPPED_CHORUS_FRUIT, 0.1F, 200)
			.criterion("has_chorus_fruit", conditionsFromItem(Items.CHORUS_FRUIT))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1F, 200)
			.criterion("has_coal_ore", conditionsFromItem(Blocks.COAL_ORE))
			.offerTo(consumer, "coal_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.BEEF), Items.COOKED_BEEF, 0.35F, 200)
			.criterion("has_beef", conditionsFromItem(Items.BEEF))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, 200)
			.criterion("has_chicken", conditionsFromItem(Items.CHICKEN))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.COD), Items.COOKED_COD, 0.35F, 200)
			.criterion("has_cod", conditionsFromItem(Items.COD))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.KELP), Items.DRIED_KELP, 0.1F, 200)
			.criterion("has_kelp", conditionsFromItem(Blocks.KELP))
			.offerTo(consumer, "dried_kelp_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.SALMON), Items.COOKED_SALMON, 0.35F, 200)
			.criterion("has_salmon", conditionsFromItem(Items.SALMON))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, 200)
			.criterion("has_mutton", conditionsFromItem(Items.MUTTON))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, 200)
			.criterion("has_porkchop", conditionsFromItem(Items.PORKCHOP))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, 200)
			.criterion("has_rabbit", conditionsFromItem(Items.RABBIT))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0F, 200)
			.criterion("has_diamond_ore", conditionsFromItem(Blocks.DIAMOND_ORE))
			.offerTo(consumer, "diamond_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2F, 200)
			.criterion("has_lapis_ore", conditionsFromItem(Blocks.LAPIS_ORE))
			.offerTo(consumer, "lapis_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0F, 200)
			.criterion("has_emerald_ore", conditionsFromItem(Blocks.EMERALD_ORE))
			.offerTo(consumer, "emerald_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.SAND), Blocks.GLASS.asItem(), 0.1F, 200)
			.criterion("has_sand", conditionsFromTag(ItemTags.SAND))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.GOLD_ORES), Items.GOLD_INGOT, 1.0F, 200)
			.criterion("has_gold_ore", conditionsFromTag(ItemTags.GOLD_ORES))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.SEA_PICKLE.asItem()), Items.LIME_DYE, 0.1F, 200)
			.criterion("has_sea_pickle", conditionsFromItem(Blocks.SEA_PICKLE))
			.offerTo(consumer, "lime_dye_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.CACTUS.asItem()), Items.GREEN_DYE, 1.0F, 200)
			.criterion("has_cactus", conditionsFromItem(Blocks.CACTUS))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(
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
			.offerTo(consumer, "gold_nugget_from_smelting");
		CookingRecipeJsonFactory.createSmelting(
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
			.offerTo(consumer, "iron_nugget_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7F, 200)
			.criterion("has_iron_ore", conditionsFromItem(Blocks.IRON_ORE.asItem()))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.COPPER_ORE), Items.COPPER_INGOT, 0.7F, 200)
			.criterion("has_copper_ore", conditionsFromItem(Blocks.COPPER_ORE.asItem()))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.CLAY), Blocks.TERRACOTTA.asItem(), 0.35F, 200)
			.criterion("has_clay_block", conditionsFromItem(Blocks.CLAY))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.NETHERRACK), Items.NETHER_BRICK, 0.1F, 200)
			.criterion("has_netherrack", conditionsFromItem(Blocks.NETHERRACK))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 200)
			.criterion("has_nether_quartz_ore", conditionsFromItem(Blocks.NETHER_QUARTZ_ORE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7F, 200)
			.criterion("has_redstone_ore", conditionsFromItem(Blocks.REDSTONE_ORE))
			.offerTo(consumer, "redstone_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.WET_SPONGE), Blocks.SPONGE.asItem(), 0.15F, 200)
			.criterion("has_wet_sponge", conditionsFromItem(Blocks.WET_SPONGE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.STONE.asItem(), 0.1F, 200)
			.criterion("has_cobblestone", conditionsFromItem(Blocks.COBBLESTONE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.STONE), Blocks.SMOOTH_STONE.asItem(), 0.1F, 200)
			.criterion("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SMOOTH_SANDSTONE.asItem(), 0.1F, 200)
			.criterion("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1F, 200)
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.SMOOTH_QUARTZ.asItem(), 0.1F, 200)
			.criterion("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1F, 200)
			.criterion("has_stone_bricks", conditionsFromItem(Blocks.STONE_BRICKS))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.BLACK_TERRACOTTA), Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_black_terracotta", conditionsFromItem(Blocks.BLACK_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.BLUE_TERRACOTTA), Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_blue_terracotta", conditionsFromItem(Blocks.BLUE_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.BROWN_TERRACOTTA), Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_brown_terracotta", conditionsFromItem(Blocks.BROWN_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.CYAN_TERRACOTTA), Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_cyan_terracotta", conditionsFromItem(Blocks.CYAN_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.GRAY_TERRACOTTA), Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_gray_terracotta", conditionsFromItem(Blocks.GRAY_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.GREEN_TERRACOTTA), Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_green_terracotta", conditionsFromItem(Blocks.GREEN_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LIGHT_BLUE_TERRACOTTA), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_light_blue_terracotta", conditionsFromItem(Blocks.LIGHT_BLUE_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LIGHT_GRAY_TERRACOTTA), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_light_gray_terracotta", conditionsFromItem(Blocks.LIGHT_GRAY_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LIME_TERRACOTTA), Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_lime_terracotta", conditionsFromItem(Blocks.LIME_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.MAGENTA_TERRACOTTA), Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_magenta_terracotta", conditionsFromItem(Blocks.MAGENTA_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.ORANGE_TERRACOTTA), Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_orange_terracotta", conditionsFromItem(Blocks.ORANGE_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.PINK_TERRACOTTA), Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_pink_terracotta", conditionsFromItem(Blocks.PINK_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.PURPLE_TERRACOTTA), Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_purple_terracotta", conditionsFromItem(Blocks.PURPLE_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.RED_TERRACOTTA), Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_red_terracotta", conditionsFromItem(Blocks.RED_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.WHITE_TERRACOTTA), Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_white_terracotta", conditionsFromItem(Blocks.WHITE_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.YELLOW_TERRACOTTA), Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_yellow_terracotta", conditionsFromItem(Blocks.YELLOW_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0F, 200)
			.criterion("has_ancient_debris", conditionsFromItem(Blocks.ANCIENT_DEBRIS))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.asItem(), 0.1F, 200)
			.criterion("has_blackstone_bricks", conditionsFromItem(Blocks.POLISHED_BLACKSTONE_BRICKS))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.CRACKED_NETHER_BRICKS.asItem(), 0.1F, 200)
			.criterion("has_nether_bricks", conditionsFromItem(Blocks.NETHER_BRICKS))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7F, 100)
			.criterion("has_iron_ore", conditionsFromItem(Blocks.IRON_ORE.asItem()))
			.offerTo(consumer, "iron_ingot_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.COPPER_ORE), Items.COPPER_INGOT, 0.7F, 100)
			.criterion("has_copper_ore", conditionsFromItem(Blocks.COPPER_ORE.asItem()))
			.offerTo(consumer, "copper_ingot_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.fromTag(ItemTags.GOLD_ORES), Items.GOLD_INGOT, 1.0F, 100)
			.criterion("has_gold_ore", conditionsFromTag(ItemTags.GOLD_ORES))
			.offerTo(consumer, "gold_ingot_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0F, 100)
			.criterion("has_diamond_ore", conditionsFromItem(Blocks.DIAMOND_ORE))
			.offerTo(consumer, "diamond_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2F, 100)
			.criterion("has_lapis_ore", conditionsFromItem(Blocks.LAPIS_ORE))
			.offerTo(consumer, "lapis_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7F, 100)
			.criterion("has_redstone_ore", conditionsFromItem(Blocks.REDSTONE_ORE))
			.offerTo(consumer, "redstone_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1F, 100)
			.criterion("has_coal_ore", conditionsFromItem(Blocks.COAL_ORE))
			.offerTo(consumer, "coal_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0F, 100)
			.criterion("has_emerald_ore", conditionsFromItem(Blocks.EMERALD_ORE))
			.offerTo(consumer, "emerald_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 100)
			.criterion("has_nether_quartz_ore", conditionsFromItem(Blocks.NETHER_QUARTZ_ORE))
			.offerTo(consumer, "quartz_from_blasting");
		CookingRecipeJsonFactory.createBlasting(
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
			.offerTo(consumer, "gold_nugget_from_blasting");
		CookingRecipeJsonFactory.createBlasting(
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
			.offerTo(consumer, "iron_nugget_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.ANCIENT_DEBRIS), Items.NETHERITE_SCRAP, 2.0F, 100)
			.criterion("has_ancient_debris", conditionsFromItem(Blocks.ANCIENT_DEBRIS))
			.offerTo(consumer, "netherite_scrap_from_blasting");
		generateCookingRecipes(consumer, "smoking", RecipeSerializer.SMOKING, 100);
		generateCookingRecipes(consumer, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, 600);
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_SLAB, 2)
			.create("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(consumer, "stone_slab_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_STAIRS)
			.create("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(consumer, "stone_stairs_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICKS)
			.create("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(consumer, "stone_bricks_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICK_SLAB, 2)
			.create("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(consumer, "stone_brick_slab_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICK_STAIRS)
			.create("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(consumer, "stone_brick_stairs_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.CHISELED_STONE_BRICKS)
			.create("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(consumer, "chiseled_stone_bricks_stone_from_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICK_WALL)
			.create("has_stone", conditionsFromItem(Blocks.STONE))
			.offerTo(consumer, "stone_brick_walls_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE)
			.create("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.offerTo(consumer, "cut_sandstone_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SANDSTONE_SLAB, 2)
			.create("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.offerTo(consumer, "sandstone_slab_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2)
			.create("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.offerTo(consumer, "cut_sandstone_slab_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.CUT_SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2)
			.create("has_cut_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.offerTo(consumer, "cut_sandstone_slab_from_cut_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SANDSTONE_STAIRS)
			.create("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.offerTo(consumer, "sandstone_stairs_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SANDSTONE_WALL)
			.create("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.offerTo(consumer, "sandstone_wall_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.CHISELED_SANDSTONE)
			.create("has_sandstone", conditionsFromItem(Blocks.SANDSTONE))
			.offerTo(consumer, "chiseled_sandstone_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE)
			.create("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "cut_red_sandstone_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_SLAB, 2)
			.create("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "red_sandstone_slab_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2)
			.create("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "cut_red_sandstone_slab_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.CUT_RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2)
			.create("has_cut_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "cut_red_sandstone_slab_from_cut_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_STAIRS)
			.create("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "red_sandstone_stairs_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_WALL)
			.create("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "red_sandstone_wall_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.CHISELED_RED_SANDSTONE)
			.create("has_red_sandstone", conditionsFromItem(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "chiseled_red_sandstone_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_SLAB, 2)
			.create("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer, "quartz_slab_from_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_STAIRS)
			.create("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer, "quartz_stairs_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_PILLAR)
			.create("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer, "quartz_pillar_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.CHISELED_QUARTZ_BLOCK)
			.create("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer, "chiseled_quartz_block_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_BRICKS)
			.create("has_quartz_block", conditionsFromItem(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer, "quartz_bricks_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_STAIRS)
			.create("has_cobblestone", conditionsFromItem(Blocks.COBBLESTONE))
			.offerTo(consumer, "cobblestone_stairs_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_SLAB, 2)
			.create("has_cobblestone", conditionsFromItem(Blocks.COBBLESTONE))
			.offerTo(consumer, "cobblestone_slab_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_WALL)
			.create("has_cobblestone", conditionsFromItem(Blocks.COBBLESTONE))
			.offerTo(consumer, "cobblestone_wall_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_SLAB, 2)
			.create("has_stone_bricks", conditionsFromItem(Blocks.STONE_BRICKS))
			.offerTo(consumer, "stone_brick_slab_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_STAIRS)
			.create("has_stone_bricks", conditionsFromItem(Blocks.STONE_BRICKS))
			.offerTo(consumer, "stone_brick_stairs_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_WALL)
			.create("has_stone_bricks", conditionsFromItem(Blocks.STONE_BRICKS))
			.offerTo(consumer, "stone_brick_wall_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.CHISELED_STONE_BRICKS)
			.create("has_stone_bricks", conditionsFromItem(Blocks.STONE_BRICKS))
			.offerTo(consumer, "chiseled_stone_bricks_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BRICKS), Blocks.BRICK_SLAB, 2)
			.create("has_bricks", conditionsFromItem(Blocks.BRICKS))
			.offerTo(consumer, "brick_slab_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BRICKS), Blocks.BRICK_STAIRS)
			.create("has_bricks", conditionsFromItem(Blocks.BRICKS))
			.offerTo(consumer, "brick_stairs_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BRICKS), Blocks.BRICK_WALL)
			.create("has_bricks", conditionsFromItem(Blocks.BRICKS))
			.offerTo(consumer, "brick_wall_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_SLAB, 2)
			.create("has_nether_bricks", conditionsFromItem(Blocks.NETHER_BRICKS))
			.offerTo(consumer, "nether_brick_slab_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_STAIRS)
			.create("has_nether_bricks", conditionsFromItem(Blocks.NETHER_BRICKS))
			.offerTo(consumer, "nether_brick_stairs_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_WALL)
			.create("has_nether_bricks", conditionsFromItem(Blocks.NETHER_BRICKS))
			.offerTo(consumer, "nether_brick_wall_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.CHISELED_NETHER_BRICKS)
			.create("has_nether_bricks", conditionsFromItem(Blocks.NETHER_BRICKS))
			.offerTo(consumer, "chiseled_nether_bricks_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_SLAB, 2)
			.create("has_nether_bricks", conditionsFromItem(Blocks.RED_NETHER_BRICKS))
			.offerTo(consumer, "red_nether_brick_slab_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_STAIRS)
			.create("has_nether_bricks", conditionsFromItem(Blocks.RED_NETHER_BRICKS))
			.offerTo(consumer, "red_nether_brick_stairs_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_WALL)
			.create("has_nether_bricks", conditionsFromItem(Blocks.RED_NETHER_BRICKS))
			.offerTo(consumer, "red_nether_brick_wall_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_SLAB, 2)
			.create("has_purpur_block", conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(consumer, "purpur_slab_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_STAIRS)
			.create("has_purpur_block", conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(consumer, "purpur_stairs_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_PILLAR)
			.create("has_purpur_block", conditionsFromItem(Blocks.PURPUR_BLOCK))
			.offerTo(consumer, "purpur_pillar_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE), Blocks.PRISMARINE_SLAB, 2)
			.create("has_prismarine", conditionsFromItem(Blocks.PRISMARINE))
			.offerTo(consumer, "prismarine_slab_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE), Blocks.PRISMARINE_STAIRS)
			.create("has_prismarine", conditionsFromItem(Blocks.PRISMARINE))
			.offerTo(consumer, "prismarine_stairs_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE), Blocks.PRISMARINE_WALL)
			.create("has_prismarine", conditionsFromItem(Blocks.PRISMARINE))
			.offerTo(consumer, "prismarine_wall_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_SLAB, 2)
			.create("has_prismarine_brick", conditionsFromItem(Blocks.PRISMARINE_BRICKS))
			.offerTo(consumer, "prismarine_brick_slab_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_STAIRS)
			.create("has_prismarine_brick", conditionsFromItem(Blocks.PRISMARINE_BRICKS))
			.offerTo(consumer, "prismarine_brick_stairs_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_SLAB, 2)
			.create("has_dark_prismarine", conditionsFromItem(Blocks.DARK_PRISMARINE))
			.offerTo(consumer, "dark_prismarine_slab_from_dark_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_STAIRS)
			.create("has_dark_prismarine", conditionsFromItem(Blocks.DARK_PRISMARINE))
			.offerTo(consumer, "dark_prismarine_stairs_from_dark_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.ANDESITE_SLAB, 2)
			.create("has_andesite", conditionsFromItem(Blocks.ANDESITE))
			.offerTo(consumer, "andesite_slab_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.ANDESITE_STAIRS)
			.create("has_andesite", conditionsFromItem(Blocks.ANDESITE))
			.offerTo(consumer, "andesite_stairs_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.ANDESITE_WALL)
			.create("has_andesite", conditionsFromItem(Blocks.ANDESITE))
			.offerTo(consumer, "andesite_wall_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE)
			.create("has_andesite", conditionsFromItem(Blocks.ANDESITE))
			.offerTo(consumer, "polished_andesite_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2)
			.create("has_andesite", conditionsFromItem(Blocks.ANDESITE))
			.offerTo(consumer, "polished_andesite_slab_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS)
			.create("has_andesite", conditionsFromItem(Blocks.ANDESITE))
			.offerTo(consumer, "polished_andesite_stairs_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2)
			.create("has_polished_andesite", conditionsFromItem(Blocks.POLISHED_ANDESITE))
			.offerTo(consumer, "polished_andesite_slab_from_polished_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS)
			.create("has_polished_andesite", conditionsFromItem(Blocks.POLISHED_ANDESITE))
			.offerTo(consumer, "polished_andesite_stairs_from_polished_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BASALT), Blocks.POLISHED_BASALT)
			.create("has_basalt", conditionsFromItem(Blocks.BASALT))
			.offerTo(consumer, "polished_basalt_from_basalt_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.GRANITE_SLAB, 2)
			.create("has_granite", conditionsFromItem(Blocks.GRANITE))
			.offerTo(consumer, "granite_slab_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.GRANITE_STAIRS)
			.create("has_granite", conditionsFromItem(Blocks.GRANITE))
			.offerTo(consumer, "granite_stairs_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.GRANITE_WALL)
			.create("has_granite", conditionsFromItem(Blocks.GRANITE))
			.offerTo(consumer, "granite_wall_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE)
			.create("has_granite", conditionsFromItem(Blocks.GRANITE))
			.offerTo(consumer, "polished_granite_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2)
			.create("has_granite", conditionsFromItem(Blocks.GRANITE))
			.offerTo(consumer, "polished_granite_slab_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE_STAIRS)
			.create("has_granite", conditionsFromItem(Blocks.GRANITE))
			.offerTo(consumer, "polished_granite_stairs_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2)
			.create("has_polished_granite", conditionsFromItem(Blocks.POLISHED_GRANITE))
			.offerTo(consumer, "polished_granite_slab_from_polished_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_STAIRS)
			.create("has_polished_granite", conditionsFromItem(Blocks.POLISHED_GRANITE))
			.offerTo(consumer, "polished_granite_stairs_from_polished_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.DIORITE_SLAB, 2)
			.create("has_diorite", conditionsFromItem(Blocks.DIORITE))
			.offerTo(consumer, "diorite_slab_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.DIORITE_STAIRS)
			.create("has_diorite", conditionsFromItem(Blocks.DIORITE))
			.offerTo(consumer, "diorite_stairs_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.DIORITE_WALL)
			.create("has_diorite", conditionsFromItem(Blocks.DIORITE))
			.offerTo(consumer, "diorite_wall_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE)
			.create("has_diorite", conditionsFromItem(Blocks.DIORITE))
			.offerTo(consumer, "polished_diorite_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2)
			.create("has_diorite", conditionsFromItem(Blocks.POLISHED_DIORITE))
			.offerTo(consumer, "polished_diorite_slab_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE_STAIRS)
			.create("has_diorite", conditionsFromItem(Blocks.POLISHED_DIORITE))
			.offerTo(consumer, "polished_diorite_stairs_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2)
			.create("has_polished_diorite", conditionsFromItem(Blocks.POLISHED_DIORITE))
			.offerTo(consumer, "polished_diorite_slab_from_polished_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_STAIRS)
			.create("has_polished_diorite", conditionsFromItem(Blocks.POLISHED_DIORITE))
			.offerTo(consumer, "polished_diorite_stairs_from_polished_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_SLAB, 2)
			.create("has_mossy_stone_bricks", conditionsFromItem(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(consumer, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_STAIRS)
			.create("has_mossy_stone_bricks", conditionsFromItem(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(consumer, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_WALL)
			.create("has_mossy_stone_bricks", conditionsFromItem(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(consumer, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_SLAB, 2)
			.create("has_mossy_cobblestone", conditionsFromItem(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer, "mossy_cobblestone_slab_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_STAIRS)
			.create("has_mossy_cobblestone", conditionsFromItem(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer, "mossy_cobblestone_stairs_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_WALL)
			.create("has_mossy_cobblestone", conditionsFromItem(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer, "mossy_cobblestone_wall_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_SLAB, 2)
			.create("has_smooth_sandstone", conditionsFromItem(Blocks.SMOOTH_SANDSTONE))
			.offerTo(consumer, "smooth_sandstone_slab_from_smooth_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_STAIRS)
			.create("has_mossy_cobblestone", conditionsFromItem(Blocks.SMOOTH_SANDSTONE))
			.offerTo(consumer, "smooth_sandstone_stairs_from_smooth_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_SLAB, 2)
			.create("has_smooth_red_sandstone", conditionsFromItem(Blocks.SMOOTH_RED_SANDSTONE))
			.offerTo(consumer, "smooth_red_sandstone_slab_from_smooth_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_STAIRS)
			.create("has_smooth_red_sandstone", conditionsFromItem(Blocks.SMOOTH_RED_SANDSTONE))
			.offerTo(consumer, "smooth_red_sandstone_stairs_from_smooth_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_SLAB, 2)
			.create("has_smooth_quartz", conditionsFromItem(Blocks.SMOOTH_QUARTZ))
			.offerTo(consumer, "smooth_quartz_slab_from_smooth_quartz_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_STAIRS)
			.create("has_smooth_quartz", conditionsFromItem(Blocks.SMOOTH_QUARTZ))
			.offerTo(consumer, "smooth_quartz_stairs_from_smooth_quartz_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_SLAB, 2)
			.create("has_end_stone_brick", conditionsFromItem(Blocks.END_STONE_BRICKS))
			.offerTo(consumer, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_STAIRS)
			.create("has_end_stone_brick", conditionsFromItem(Blocks.END_STONE_BRICKS))
			.offerTo(consumer, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_WALL)
			.create("has_end_stone_brick", conditionsFromItem(Blocks.END_STONE_BRICKS))
			.offerTo(consumer, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICKS)
			.create("has_end_stone", conditionsFromItem(Blocks.END_STONE))
			.offerTo(consumer, "end_stone_bricks_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_SLAB, 2)
			.create("has_end_stone", conditionsFromItem(Blocks.END_STONE))
			.offerTo(consumer, "end_stone_brick_slab_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_STAIRS)
			.create("has_end_stone", conditionsFromItem(Blocks.END_STONE))
			.offerTo(consumer, "end_stone_brick_stairs_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_WALL)
			.create("has_end_stone", conditionsFromItem(Blocks.END_STONE))
			.offerTo(consumer, "end_stone_brick_wall_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_STONE), Blocks.SMOOTH_STONE_SLAB, 2)
			.create("has_smooth_stone", conditionsFromItem(Blocks.SMOOTH_STONE))
			.offerTo(consumer, "smooth_stone_slab_from_smooth_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.BLACKSTONE_SLAB, 2)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "blackstone_slab_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.BLACKSTONE_STAIRS)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "blackstone_stairs_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.BLACKSTONE_WALL)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "blackstone_wall_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_WALL)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_wall_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_SLAB, 2)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_slab_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_STAIRS)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_stairs_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.CHISELED_POLISHED_BLACKSTONE)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "chiseled_polished_blackstone_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICKS)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_bricks_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_brick_slab_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_brick_stairs_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_WALL)
			.create("has_blackstone", conditionsFromItem(Blocks.BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_brick_wall_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_SLAB, 2)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.POLISHED_BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_slab_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_STAIRS)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.POLISHED_BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_stairs_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICKS)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.POLISHED_BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_bricks_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_WALL)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.POLISHED_BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_wall_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.POLISHED_BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_brick_slab_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.POLISHED_BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_brick_stairs_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.POLISHED_BLACKSTONE_BRICK_WALL)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.POLISHED_BLACKSTONE))
			.offerTo(consumer, "polished_blackstone_brick_wall_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE), Blocks.CHISELED_POLISHED_BLACKSTONE)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.POLISHED_BLACKSTONE))
			.offerTo(consumer, "chiseled_polished_blackstone_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 2)
			.create("has_polished_blackstone_bricks", conditionsFromItem(Blocks.POLISHED_BLACKSTONE_BRICKS))
			.offerTo(consumer, "polished_blackstone_brick_slab_from_polished_blackstone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS)
			.create("has_polished_blackstone_bricks", conditionsFromItem(Blocks.POLISHED_BLACKSTONE_BRICKS))
			.offerTo(consumer, "polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE_BRICKS), Blocks.POLISHED_BLACKSTONE_BRICK_WALL)
			.create("has_polished_blackstone_bricks", conditionsFromItem(Blocks.POLISHED_BLACKSTONE_BRICKS))
			.offerTo(consumer, "polished_blackstone_brick_wall_from_polished_blackstone_bricks_stonecutting");
		offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
		offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
		offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
		offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
		offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
		offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
		offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
		offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_HOE, Items.NETHERITE_HOE);
		offerNetheriteUpgradeRecipe(consumer, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
	}

	private static void offerNetheriteUpgradeRecipe(Consumer<RecipeJsonProvider> consumer, Item outputItem, Item inputItem) {
		SmithingRecipeJsonFactory.create(Ingredient.ofItems(outputItem), Ingredient.ofItems(Items.NETHERITE_INGOT), inputItem)
			.criterion("has_netherite_ingot", conditionsFromItem(Items.NETHERITE_INGOT))
			.offerTo(consumer, Registry.ITEM.getId(inputItem.asItem()).getPath() + "_smithing");
	}

	private static void offerPlanksRecipe2(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, Tag<Item> inputItem) {
		ShapelessRecipeJsonFactory.create(outputItem, 4).input(inputItem).group("planks").criterion("has_log", conditionsFromTag(inputItem)).offerTo(consumer);
	}

	private static void offerPlanksRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, Tag<Item> inputItem) {
		ShapelessRecipeJsonFactory.create(outputItem, 4).input(inputItem).group("planks").criterion("has_logs", conditionsFromTag(inputItem)).offerTo(consumer);
	}

	private static void offerBarkBlockRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 3)
			.input('#', inputItem)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	private static void offerBoatRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem)
			.input('#', inputItem)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", requireEnteringFluid(Blocks.WATER))
			.offerTo(consumer);
	}

	private static void offerButtonRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapelessRecipeJsonFactory.create(outputItem)
			.input(inputItem)
			.group("wooden_button")
			.criterion("has_planks", conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	private static void offerDoorRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 3)
			.input('#', inputItem)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	private static void offerFenceRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 3)
			.input('#', Items.STICK)
			.input('W', inputItem)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	private static void offerFenceGateRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem)
			.input('#', Items.STICK)
			.input('W', inputItem)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	private static void offerWoodenPressurePlateRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		createPressurePlateRecipe(outputItem, inputItem).group("wooden_pressure_plate").criterion("has_planks", conditionsFromItem(inputItem)).offerTo(consumer);
	}

	private static void createPressurePlateRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		createPressurePlateRecipe(outputItem, inputItem).criterion(hasItem(inputItem), conditionsFromItem(inputItem)).offerTo(consumer);
	}

	private static ShapedRecipeJsonFactory createPressurePlateRecipe(ItemConvertible outputItem, ItemConvertible... inputItems) {
		return ShapedRecipeJsonFactory.create(outputItem).input('#', Ingredient.ofItems(inputItems)).pattern("##");
	}

	private static void offerWoodenSlabRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		createSlabRecipe(outputItem, Ingredient.ofItems(inputItem)).group("wooden_slab").criterion("has_planks", conditionsFromItem(inputItem)).offerTo(consumer);
	}

	private static void offerSlabRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		createSlabRecipe(outputItem, Ingredient.ofItems(inputItem)).criterion(hasItem(inputItem), conditionsFromItem(inputItem)).offerTo(consumer);
	}

	private static ShapedRecipeJsonFactory createSlabRecipe(ItemConvertible outputItem, Ingredient ingredient) {
		return ShapedRecipeJsonFactory.create(outputItem, 6).input('#', ingredient).pattern("###");
	}

	private static void offerWoodenStairsRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		createStairsRecipe(outputItem, Ingredient.ofItems(inputItem)).group("wooden_stairs").criterion("has_planks", conditionsFromItem(inputItem)).offerTo(consumer);
	}

	private static void offerStonecutterRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		createStairsRecipe(outputItem, Ingredient.ofItems(inputItem)).criterion(hasItem(inputItem), conditionsFromItem(inputItem)).offerTo(consumer);
	}

	private static ShapedRecipeJsonFactory createStairsRecipe(ItemConvertible outputItem, Ingredient inputItem) {
		return ShapedRecipeJsonFactory.create(outputItem, 4).input('#', inputItem).pattern("#  ").pattern("## ").pattern("###");
	}

	private static void offerTrapdoorRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 2)
			.input('#', inputItem)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	private static void offerSignRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 3)
			.group("sign")
			.input('#', inputItem)
			.input('X', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion(hasItem(inputItem), conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	private static void offerWoolDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapelessRecipeJsonFactory.create(outputItem)
			.input(inputItem)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", conditionsFromItem(Blocks.WHITE_WOOL))
			.offerTo(consumer);
	}

	private static void offerCarpetRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 3)
			.input('#', inputItem)
			.pattern("##")
			.group("carpet")
			.criterion(hasItem(inputItem), conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	private static void offerCarpetDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible itemConvertible) {
		String string = Registry.ITEM.getId(outputItem.asItem()).getPath();
		ShapedRecipeJsonFactory.create(outputItem, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', itemConvertible)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", conditionsFromItem(Blocks.WHITE_CARPET))
			.criterion(hasItem(itemConvertible), conditionsFromItem(itemConvertible))
			.offerTo(consumer, string + "_from_white_carpet");
	}

	private static void offerBedRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible itemConvertible) {
		ShapedRecipeJsonFactory.create(outputItem)
			.input('#', itemConvertible)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion(hasItem(itemConvertible), conditionsFromItem(itemConvertible))
			.offerTo(consumer);
	}

	private static void offerBedDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		String string = Registry.ITEM.getId(outputItem.asItem()).getPath();
		ShapelessRecipeJsonFactory.create(outputItem)
			.input(Items.WHITE_BED)
			.input(inputItem)
			.group("dyed_bed")
			.criterion("has_bed", conditionsFromItem(Items.WHITE_BED))
			.offerTo(consumer, string + "_from_white_bed");
	}

	private static void offerBannerRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem)
			.input('#', inputItem)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion(hasItem(inputItem), conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	private static void offerStainedGlassDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 8)
			.input('#', Blocks.GLASS)
			.input('X', inputItem)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", conditionsFromItem(Blocks.GLASS))
			.offerTo(consumer);
	}

	private static void offerStainedGlassPaneRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 16)
			.input('#', inputItem)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	private static void offerStainedGlassPaneDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		String string = Registry.ITEM.getId(outputItem.asItem()).getPath();
		ShapedRecipeJsonFactory.create(outputItem, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', inputItem)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", conditionsFromItem(Blocks.GLASS_PANE))
			.criterion(hasItem(inputItem), conditionsFromItem(inputItem))
			.offerTo(consumer, string + "_from_glass_pane");
	}

	private static void offerTerracottaDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', inputItem)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", conditionsFromItem(Blocks.TERRACOTTA))
			.offerTo(consumer);
	}

	private static void offerConcretePowderDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapelessRecipeJsonFactory.create(outputItem, 8)
			.input(inputItem)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", conditionsFromItem(Blocks.SAND))
			.criterion("has_gravel", conditionsFromItem(Blocks.GRAVEL))
			.offerTo(consumer);
	}

	public static void offerCandleDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapelessRecipeJsonFactory.create(outputItem)
			.input(Blocks.CANDLE)
			.input(inputItem)
			.criterion(hasItem(inputItem), conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	public static void offerWallRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 6)
			.input('#', inputItem)
			.pattern("###")
			.pattern("###")
			.criterion(hasItem(inputItem), conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	public static void offerPolishedStoneRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 4)
			.input('S', inputItem)
			.pattern("SS")
			.pattern("SS")
			.criterion(hasItem(inputItem), conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	public static void offerCutCopperRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		ShapedRecipeJsonFactory.create(outputItem, 4)
			.input('#', inputItem)
			.pattern("##")
			.pattern("##")
			.criterion(hasItem(inputItem), conditionsFromItem(inputItem))
			.offerTo(consumer);
	}

	public static void offerChiseledBlockRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible outputItem, ItemConvertible inputItem) {
		createChiseledBlockRecipe(outputItem, inputItem).criterion(hasItem(inputItem), conditionsFromItem(inputItem)).offerTo(consumer);
	}

	public static ShapedRecipeJsonFactory createChiseledBlockRecipe(ItemConvertible outputItem, ItemConvertible inputItem) {
		return ShapedRecipeJsonFactory.create(outputItem).input('#', inputItem).pattern("#").pattern("#");
	}

	private static void generateCookingRecipes(Consumer<RecipeJsonProvider> consumer, String cooker, CookingRecipeSerializer<?> serializer, int cookingTime) {
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.BEEF), Items.COOKED_BEEF, 0.35F, cookingTime, serializer)
			.criterion("has_beef", conditionsFromItem(Items.BEEF))
			.offerTo(consumer, "cooked_beef_from_" + cooker);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, cookingTime, serializer)
			.criterion("has_chicken", conditionsFromItem(Items.CHICKEN))
			.offerTo(consumer, "cooked_chicken_from_" + cooker);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.COD), Items.COOKED_COD, 0.35F, cookingTime, serializer)
			.criterion("has_cod", conditionsFromItem(Items.COD))
			.offerTo(consumer, "cooked_cod_from_" + cooker);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Blocks.KELP), Items.DRIED_KELP, 0.1F, cookingTime, serializer)
			.criterion("has_kelp", conditionsFromItem(Blocks.KELP))
			.offerTo(consumer, "dried_kelp_from_" + cooker);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.SALMON), Items.COOKED_SALMON, 0.35F, cookingTime, serializer)
			.criterion("has_salmon", conditionsFromItem(Items.SALMON))
			.offerTo(consumer, "cooked_salmon_from_" + cooker);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, cookingTime, serializer)
			.criterion("has_mutton", conditionsFromItem(Items.MUTTON))
			.offerTo(consumer, "cooked_mutton_from_" + cooker);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, cookingTime, serializer)
			.criterion("has_porkchop", conditionsFromItem(Items.PORKCHOP))
			.offerTo(consumer, "cooked_porkchop_from_" + cooker);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.POTATO), Items.BAKED_POTATO, 0.35F, cookingTime, serializer)
			.criterion("has_potato", conditionsFromItem(Items.POTATO))
			.offerTo(consumer, "baked_potato_from_" + cooker);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, cookingTime, serializer)
			.criterion("has_rabbit", conditionsFromItem(Items.RABBIT))
			.offerTo(consumer, "cooked_rabbit_from_" + cooker);
	}

	private static EnterBlockCriterion.Conditions requireEnteringFluid(Block block) {
		return new EnterBlockCriterion.Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
	}

	private static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
		return conditionsFromItemPredicates(ItemPredicate.Builder.create().item(item).build());
	}

	private static InventoryChangedCriterion.Conditions conditionsFromTag(Tag<Item> tag) {
		return conditionsFromItemPredicates(ItemPredicate.Builder.create().tag(tag).build());
	}

	private static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate... itemPredicates) {
		return new InventoryChangedCriterion.Conditions(
			EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, itemPredicates
		);
	}

	private static String hasItem(ItemConvertible item) {
		return "has_" + Registry.ITEM.getId(item.asItem()).getPath();
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
