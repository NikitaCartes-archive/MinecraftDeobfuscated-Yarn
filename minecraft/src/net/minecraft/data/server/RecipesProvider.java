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
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipesProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator root;

	public RecipesProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void run(DataCache dataCache) throws IOException {
		Path path = this.root.getOutput();
		Set<Identifier> set = Sets.<Identifier>newHashSet();
		this.generate(
			recipeJsonProvider -> {
				if (!set.add(recipeJsonProvider.getRecipeId())) {
					throw new IllegalStateException("Duplicate recipe " + recipeJsonProvider.getRecipeId());
				} else {
					this.method_10425(
						dataCache,
						recipeJsonProvider.toJson(),
						path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/recipes/" + recipeJsonProvider.getRecipeId().getPath() + ".json")
					);
					JsonObject jsonObject = recipeJsonProvider.toAdvancementJson();
					if (jsonObject != null) {
						this.method_10427(
							dataCache,
							jsonObject,
							path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/advancements/" + recipeJsonProvider.getAdvancementId().getPath() + ".json")
						);
					}
				}
			}
		);
		this.method_10427(
			dataCache,
			Advancement.Task.create().criterion("impossible", new ImpossibleCriterion.Conditions()).toJson(),
			path.resolve("data/minecraft/advancements/recipes/root.json")
		);
	}

	private void method_10425(DataCache dataCache, JsonObject jsonObject, Path path) {
		try {
			String string = GSON.toJson((JsonElement)jsonObject);
			String string2 = SHA1.hashUnencodedChars(string).toString();
			if (!Objects.equals(dataCache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
				Files.createDirectories(path.getParent());
				BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
				Throwable var7 = null;

				try {
					bufferedWriter.write(string);
				} catch (Throwable var17) {
					var7 = var17;
					throw var17;
				} finally {
					if (bufferedWriter != null) {
						if (var7 != null) {
							try {
								bufferedWriter.close();
							} catch (Throwable var16) {
								var7.addSuppressed(var16);
							}
						} else {
							bufferedWriter.close();
						}
					}
				}
			}

			dataCache.updateSha1(path, string2);
		} catch (IOException var19) {
			LOGGER.error("Couldn't save recipe {}", path, var19);
		}
	}

	private void method_10427(DataCache dataCache, JsonObject jsonObject, Path path) {
		try {
			String string = GSON.toJson((JsonElement)jsonObject);
			String string2 = SHA1.hashUnencodedChars(string).toString();
			if (!Objects.equals(dataCache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
				Files.createDirectories(path.getParent());
				BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
				Throwable var7 = null;

				try {
					bufferedWriter.write(string);
				} catch (Throwable var17) {
					var7 = var17;
					throw var17;
				} finally {
					if (bufferedWriter != null) {
						if (var7 != null) {
							try {
								bufferedWriter.close();
							} catch (Throwable var16) {
								var7.addSuppressed(var16);
							}
						} else {
							bufferedWriter.close();
						}
					}
				}
			}

			dataCache.updateSha1(path, string2);
		} catch (IOException var19) {
			LOGGER.error("Couldn't save recipe advancement {}", path, var19);
		}
	}

	private void generate(Consumer<RecipeJsonProvider> consumer) {
		ShapedRecipeJsonFactory.create(Blocks.ACACIA_WOOD, 3)
			.input('#', Blocks.ACACIA_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.ACACIA_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STRIPPED_ACACIA_WOOD, 3)
			.input('#', Blocks.STRIPPED_ACACIA_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.STRIPPED_ACACIA_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ACACIA_BOAT)
			.input('#', Blocks.ACACIA_PLANKS)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.WATER))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.ACACIA_BUTTON)
			.input(Blocks.ACACIA_PLANKS)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.ACACIA_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ACACIA_DOOR, 3)
			.input('#', Blocks.ACACIA_PLANKS)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.ACACIA_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ACACIA_FENCE, 3)
			.input('#', Items.STICK)
			.input('W', Blocks.ACACIA_PLANKS)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.ACACIA_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ACACIA_FENCE_GATE)
			.input('#', Items.STICK)
			.input('W', Blocks.ACACIA_PLANKS)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.ACACIA_PLANKS))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.ACACIA_PLANKS, 4)
			.input(ItemTags.ACACIA_LOGS)
			.group("planks")
			.criterion("has_logs", this.method_10420(ItemTags.ACACIA_LOGS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ACACIA_PRESSURE_PLATE)
			.input('#', Blocks.ACACIA_PLANKS)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.ACACIA_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ACACIA_SLAB, 6)
			.input('#', Blocks.ACACIA_PLANKS)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.ACACIA_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ACACIA_STAIRS, 4)
			.input('#', Blocks.ACACIA_PLANKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.ACACIA_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ACACIA_TRAPDOOR, 2)
			.input('#', Blocks.ACACIA_PLANKS)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.ACACIA_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ACTIVATOR_RAIL, 6)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('S', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XSX")
			.pattern("X#X")
			.pattern("XSX")
			.criterion("has_rail", this.method_10426(Blocks.RAIL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.ANDESITE, 2)
			.input(Blocks.DIORITE)
			.input(Blocks.COBBLESTONE)
			.criterion("has_stone", this.method_10426(Blocks.DIORITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ANVIL)
			.input('I', Blocks.IRON_BLOCK)
			.input('i', Items.IRON_INGOT)
			.pattern("III")
			.pattern(" i ")
			.pattern("iii")
			.criterion("has_iron_block", this.method_10426(Blocks.IRON_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ARMOR_STAND)
			.input('/', Items.STICK)
			.input('_', Blocks.SMOOTH_STONE_SLAB)
			.pattern("///")
			.pattern(" / ")
			.pattern("/_/")
			.criterion("has_stone_slab", this.method_10426(Blocks.SMOOTH_STONE_SLAB))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ARROW, 4)
			.input('#', Items.STICK)
			.input('X', Items.FLINT)
			.input('Y', Items.FEATHER)
			.pattern("X")
			.pattern("#")
			.pattern("Y")
			.criterion("has_feather", this.method_10426(Items.FEATHER))
			.criterion("has_flint", this.method_10426(Items.FLINT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BARREL, 1)
			.input('P', ItemTags.PLANKS)
			.input('S', ItemTags.WOODEN_SLABS)
			.pattern("PSP")
			.pattern("P P")
			.pattern("PSP")
			.criterion("has_planks", this.method_10420(ItemTags.PLANKS))
			.criterion("has_wood_slab", this.method_10420(ItemTags.WOODEN_SLABS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BEACON)
			.input('S', Items.NETHER_STAR)
			.input('G', Blocks.GLASS)
			.input('O', Blocks.OBSIDIAN)
			.pattern("GGG")
			.pattern("GSG")
			.pattern("OOO")
			.criterion("has_nether_star", this.method_10426(Items.NETHER_STAR))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BEEHIVE)
			.input('P', ItemTags.PLANKS)
			.input('H', Items.HONEYCOMB)
			.pattern("PPP")
			.pattern("HHH")
			.pattern("PPP")
			.criterion("has_honeycomb", this.method_10426(Items.HONEYCOMB))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BEETROOT_SOUP)
			.input(Items.BOWL)
			.input(Items.BEETROOT, 6)
			.criterion("has_beetroot", this.method_10426(Items.BEETROOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BIRCH_WOOD, 3)
			.input('#', Blocks.BIRCH_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.BIRCH_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STRIPPED_BIRCH_WOOD, 3)
			.input('#', Blocks.STRIPPED_BIRCH_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.STRIPPED_BIRCH_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BIRCH_BOAT)
			.input('#', Blocks.BIRCH_PLANKS)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.WATER))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.BIRCH_BUTTON)
			.input(Blocks.BIRCH_PLANKS)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.BIRCH_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BIRCH_DOOR, 3)
			.input('#', Blocks.BIRCH_PLANKS)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.BIRCH_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BIRCH_FENCE, 3)
			.input('#', Items.STICK)
			.input('W', Blocks.BIRCH_PLANKS)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.BIRCH_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BIRCH_FENCE_GATE)
			.input('#', Items.STICK)
			.input('W', Blocks.BIRCH_PLANKS)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.BIRCH_PLANKS))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.BIRCH_PLANKS, 4)
			.input(ItemTags.BIRCH_LOGS)
			.group("planks")
			.criterion("has_log", this.method_10420(ItemTags.BIRCH_LOGS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BIRCH_PRESSURE_PLATE)
			.input('#', Blocks.BIRCH_PLANKS)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.BIRCH_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BIRCH_SLAB, 6)
			.input('#', Blocks.BIRCH_PLANKS)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.BIRCH_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BIRCH_STAIRS, 4)
			.input('#', Blocks.BIRCH_PLANKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.BIRCH_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BIRCH_TRAPDOOR, 2)
			.input('#', Blocks.BIRCH_PLANKS)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.BIRCH_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BLACK_BANNER)
			.input('#', Blocks.BLACK_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_black_wool", this.method_10426(Blocks.BLACK_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BLACK_BED)
			.input('#', Blocks.BLACK_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_black_wool", this.method_10426(Blocks.BLACK_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLACK_BED)
			.input(Items.WHITE_BED)
			.input(Items.BLACK_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "black_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.BLACK_CARPET, 3)
			.input('#', Blocks.BLACK_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_black_wool", this.method_10426(Blocks.BLACK_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BLACK_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.BLACK_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_black_dye", this.method_10426(Items.BLACK_DYE))
			.offerTo(consumer, "black_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.BLACK_CONCRETE_POWDER, 8)
			.input(Items.BLACK_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLACK_DYE)
			.input(Items.INK_SAC)
			.group("black_dye")
			.criterion("has_ink_sac", this.method_10426(Items.INK_SAC))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLACK_DYE)
			.input(Blocks.WITHER_ROSE)
			.group("black_dye")
			.criterion("has_black_flower", this.method_10426(Blocks.WITHER_ROSE))
			.offerTo(consumer, "black_dye_from_wither_rose");
		ShapedRecipeJsonFactory.create(Blocks.BLACK_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.BLACK_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BLACK_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.BLACK_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BLACK_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.BLACK_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_black_dye", this.method_10426(Items.BLACK_DYE))
			.offerTo(consumer, "black_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.BLACK_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.BLACK_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.BLACK_WOOL)
			.input(Items.BLACK_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLAZE_POWDER, 2)
			.input(Items.BLAZE_ROD)
			.criterion("has_blaze_rod", this.method_10426(Items.BLAZE_ROD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BLUE_BANNER)
			.input('#', Blocks.BLUE_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_blue_wool", this.method_10426(Blocks.BLUE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BLUE_BED)
			.input('#', Blocks.BLUE_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_blue_wool", this.method_10426(Blocks.BLUE_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLUE_BED)
			.input(Items.WHITE_BED)
			.input(Items.BLUE_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "blue_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.BLUE_CARPET, 3)
			.input('#', Blocks.BLUE_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_blue_wool", this.method_10426(Blocks.BLUE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BLUE_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.BLUE_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_blue_dye", this.method_10426(Items.BLUE_DYE))
			.offerTo(consumer, "blue_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.BLUE_CONCRETE_POWDER, 8)
			.input(Items.BLUE_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLUE_DYE)
			.input(Items.LAPIS_LAZULI)
			.group("blue_dye")
			.criterion("has_lapis_lazuli", this.method_10426(Items.LAPIS_LAZULI))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLUE_DYE)
			.input(Blocks.CORNFLOWER)
			.group("blue_dye")
			.criterion("has_blue_flower", this.method_10426(Blocks.CORNFLOWER))
			.offerTo(consumer, "blue_dye_from_cornflower");
		ShapedRecipeJsonFactory.create(Blocks.BLUE_ICE)
			.input('#', Blocks.PACKED_ICE)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_packed_ice", this.method_10424(NumberRange.IntRange.atLeast(9), Blocks.PACKED_ICE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BLUE_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.BLUE_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BLUE_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.BLUE_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BLUE_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.BLUE_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_blue_dye", this.method_10426(Items.BLUE_DYE))
			.offerTo(consumer, "blue_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.BLUE_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.BLUE_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.BLUE_WOOL)
			.input(Items.BLUE_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.OAK_BOAT)
			.input('#', Blocks.OAK_PLANKS)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.WATER))
			.offerTo(consumer);
		Item item = Items.BONE_MEAL;
		ShapedRecipeJsonFactory.create(Blocks.BONE_BLOCK)
			.input('X', Items.BONE_MEAL)
			.pattern("XXX")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_at_least_9_bonemeal", this.method_10424(NumberRange.IntRange.atLeast(9), item))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BONE_MEAL, 3)
			.input(Items.BONE)
			.group("bonemeal")
			.criterion("has_bone", this.method_10426(Items.BONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BONE_MEAL, 9)
			.input(Blocks.BONE_BLOCK)
			.group("bonemeal")
			.criterion("has_at_least_9_bonemeal", this.method_10424(NumberRange.IntRange.atLeast(9), Items.BONE_MEAL))
			.criterion("has_bone_block", this.method_10426(Blocks.BONE_BLOCK))
			.offerTo(consumer, "bone_meal_from_bone_block");
		ShapelessRecipeJsonFactory.create(Items.BOOK)
			.input(Items.PAPER, 3)
			.input(Items.LEATHER)
			.criterion("has_paper", this.method_10426(Items.PAPER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BOOKSHELF)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.BOOK)
			.pattern("###")
			.pattern("XXX")
			.pattern("###")
			.criterion("has_book", this.method_10426(Items.BOOK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BOW)
			.input('#', Items.STICK)
			.input('X', Items.STRING)
			.pattern(" #X")
			.pattern("# X")
			.pattern(" #X")
			.criterion("has_string", this.method_10426(Items.STRING))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BOWL, 4)
			.input('#', ItemTags.PLANKS)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brown_mushroom", this.method_10426(Blocks.BROWN_MUSHROOM))
			.criterion("has_red_mushroom", this.method_10426(Blocks.RED_MUSHROOM))
			.criterion("has_mushroom_stew", this.method_10426(Items.MUSHROOM_STEW))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BREAD).input('#', Items.WHEAT).pattern("###").criterion("has_wheat", this.method_10426(Items.WHEAT)).offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BREWING_STAND)
			.input('B', Items.BLAZE_ROD)
			.input('#', Blocks.COBBLESTONE)
			.pattern(" B ")
			.pattern("###")
			.criterion("has_blaze_rod", this.method_10426(Items.BLAZE_ROD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BRICKS)
			.input('#', Items.BRICK)
			.pattern("##")
			.pattern("##")
			.criterion("has_brick", this.method_10426(Items.BRICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BRICK_SLAB, 6)
			.input('#', Blocks.BRICKS)
			.pattern("###")
			.criterion("has_brick_block", this.method_10426(Blocks.BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BRICK_STAIRS, 4)
			.input('#', Blocks.BRICKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_brick_block", this.method_10426(Blocks.BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BROWN_BANNER)
			.input('#', Blocks.BROWN_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_brown_wool", this.method_10426(Blocks.BROWN_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BROWN_BED)
			.input('#', Blocks.BROWN_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_brown_wool", this.method_10426(Blocks.BROWN_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BROWN_BED)
			.input(Items.WHITE_BED)
			.input(Items.BROWN_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "brown_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.BROWN_CARPET, 3)
			.input('#', Blocks.BROWN_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_brown_wool", this.method_10426(Blocks.BROWN_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BROWN_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.BROWN_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_brown_dye", this.method_10426(Items.BROWN_DYE))
			.offerTo(consumer, "brown_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.BROWN_CONCRETE_POWDER, 8)
			.input(Items.BROWN_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BROWN_DYE)
			.input(Items.COCOA_BEANS)
			.group("brown_dye")
			.criterion("has_cocoa_beans", this.method_10426(Items.COCOA_BEANS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BROWN_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.BROWN_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BROWN_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.BROWN_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BROWN_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.BROWN_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_brown_dye", this.method_10426(Items.BROWN_DYE))
			.offerTo(consumer, "brown_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.BROWN_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.BROWN_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.BROWN_WOOL)
			.input(Items.BROWN_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BUCKET)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CAKE)
			.input('A', Items.MILK_BUCKET)
			.input('B', Items.SUGAR)
			.input('C', Items.WHEAT)
			.input('E', Items.EGG)
			.pattern("AAA")
			.pattern("BEB")
			.pattern("CCC")
			.criterion("has_egg", this.method_10426(Items.EGG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CAMPFIRE)
			.input('L', ItemTags.LOGS)
			.input('S', Items.STICK)
			.input('C', ItemTags.COALS)
			.pattern(" S ")
			.pattern("SCS")
			.pattern("LLL")
			.criterion("has_stick", this.method_10426(Items.STICK))
			.criterion("has_coal", this.method_10420(ItemTags.COALS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CARROT_ON_A_STICK)
			.input('#', Items.FISHING_ROD)
			.input('X', Items.CARROT)
			.pattern("# ")
			.pattern(" X")
			.criterion("has_carrot", this.method_10426(Items.CARROT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CAULDRON)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern("# #")
			.pattern("###")
			.criterion("has_water_bucket", this.method_10426(Items.WATER_BUCKET))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.COMPOSTER)
			.input('F', ItemTags.WOODEN_FENCES)
			.input('#', ItemTags.PLANKS)
			.pattern("F F")
			.pattern("F F")
			.pattern("###")
			.criterion("has_wooden_fences", this.method_10420(ItemTags.WOODEN_FENCES))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CHEST)
			.input('#', ItemTags.PLANKS)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion(
				"has_lots_of_items",
				new InventoryChangedCriterion.Conditions(NumberRange.IntRange.atLeast(10), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new ItemPredicate[0])
			)
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CHEST_MINECART)
			.input('A', Blocks.CHEST)
			.input('B', Items.MINECART)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", this.method_10426(Items.MINECART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CHISELED_QUARTZ_BLOCK)
			.input('#', Blocks.QUARTZ_SLAB)
			.pattern("#")
			.pattern("#")
			.criterion("has_chiseled_quartz_block", this.method_10426(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", this.method_10426(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", this.method_10426(Blocks.QUARTZ_PILLAR))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CHISELED_STONE_BRICKS)
			.input('#', Blocks.STONE_BRICK_SLAB)
			.pattern("#")
			.pattern("#")
			.criterion("has_stone_bricks", this.method_10420(ItemTags.STONE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CLAY)
			.input('#', Items.CLAY_BALL)
			.pattern("##")
			.pattern("##")
			.criterion("has_clay_ball", this.method_10426(Items.CLAY_BALL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CLOCK)
			.input('#', Items.GOLD_INGOT)
			.input('X', Items.REDSTONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", this.method_10426(Items.REDSTONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.COAL, 9)
			.input(Blocks.COAL_BLOCK)
			.criterion("has_at_least_9_coal", this.method_10424(NumberRange.IntRange.atLeast(9), Items.COAL))
			.criterion("has_coal_block", this.method_10426(Blocks.COAL_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.COAL_BLOCK)
			.input('#', Items.COAL)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_coal", this.method_10424(NumberRange.IntRange.atLeast(9), Items.COAL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.COARSE_DIRT, 4)
			.input('D', Blocks.DIRT)
			.input('G', Blocks.GRAVEL)
			.pattern("DG")
			.pattern("GD")
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.COBBLESTONE_SLAB, 6)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.COBBLESTONE_WALL, 6)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.pattern("###")
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.COMPARATOR)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('X', Items.QUARTZ)
			.input('I', Blocks.STONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern("III")
			.criterion("has_quartz", this.method_10426(Items.QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.COMPASS)
			.input('#', Items.IRON_INGOT)
			.input('X', Items.REDSTONE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", this.method_10426(Items.REDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.COOKIE, 8)
			.input('#', Items.WHEAT)
			.input('X', Items.COCOA_BEANS)
			.pattern("#X#")
			.criterion("has_cocoa", this.method_10426(Items.COCOA_BEANS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CRAFTING_TABLE)
			.input('#', ItemTags.PLANKS)
			.pattern("##")
			.pattern("##")
			.criterion("has_planks", this.method_10420(ItemTags.PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CROSSBOW)
			.input('~', Items.STRING)
			.input('#', Items.STICK)
			.input('&', Items.IRON_INGOT)
			.input('$', Blocks.TRIPWIRE_HOOK)
			.pattern("#&#")
			.pattern("~$~")
			.pattern(" # ")
			.criterion("has_string", this.method_10426(Items.STRING))
			.criterion("has_stick", this.method_10426(Items.STICK))
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.criterion("has_tripwire_hook", this.method_10426(Blocks.TRIPWIRE_HOOK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LOOM)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.STRING)
			.pattern("@@")
			.pattern("##")
			.criterion("has_string", this.method_10426(Items.STRING))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CHISELED_RED_SANDSTONE)
			.input('#', Blocks.RED_SANDSTONE_SLAB)
			.pattern("#")
			.pattern("#")
			.criterion("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", this.method_10426(Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_cut_red_sandstone", this.method_10426(Blocks.CUT_RED_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CHISELED_SANDSTONE)
			.input('#', Blocks.SANDSTONE_SLAB)
			.pattern("#")
			.pattern("#")
			.criterion("has_stone_slab", this.method_10426(Blocks.SANDSTONE_SLAB))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CYAN_BANNER)
			.input('#', Blocks.CYAN_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_cyan_wool", this.method_10426(Blocks.CYAN_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CYAN_BED)
			.input('#', Blocks.CYAN_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_cyan_wool", this.method_10426(Blocks.CYAN_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.CYAN_BED)
			.input(Items.WHITE_BED)
			.input(Items.CYAN_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "cyan_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.CYAN_CARPET, 3)
			.input('#', Blocks.CYAN_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_cyan_wool", this.method_10426(Blocks.CYAN_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CYAN_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.CYAN_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_cyan_dye", this.method_10426(Items.CYAN_DYE))
			.offerTo(consumer, "cyan_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.CYAN_CONCRETE_POWDER, 8)
			.input(Items.CYAN_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.CYAN_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.GREEN_DYE)
			.criterion("has_green_dye", this.method_10426(Items.GREEN_DYE))
			.criterion("has_blue_dye", this.method_10426(Items.BLUE_DYE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CYAN_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.CYAN_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CYAN_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.CYAN_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CYAN_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.CYAN_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_cyan_dye", this.method_10426(Items.CYAN_DYE))
			.offerTo(consumer, "cyan_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.CYAN_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.CYAN_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.CYAN_WOOL)
			.input(Items.CYAN_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_OAK_WOOD, 3)
			.input('#', Blocks.DARK_OAK_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.DARK_OAK_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STRIPPED_DARK_OAK_WOOD, 3)
			.input('#', Blocks.STRIPPED_DARK_OAK_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.STRIPPED_DARK_OAK_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DARK_OAK_BOAT)
			.input('#', Blocks.DARK_OAK_PLANKS)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.WATER))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.DARK_OAK_BUTTON)
			.input(Blocks.DARK_OAK_PLANKS)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.DARK_OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_OAK_DOOR, 3)
			.input('#', Blocks.DARK_OAK_PLANKS)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.DARK_OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_OAK_FENCE, 3)
			.input('#', Items.STICK)
			.input('W', Blocks.DARK_OAK_PLANKS)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.DARK_OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_OAK_FENCE_GATE)
			.input('#', Items.STICK)
			.input('W', Blocks.DARK_OAK_PLANKS)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.DARK_OAK_PLANKS))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.DARK_OAK_PLANKS, 4)
			.input(ItemTags.DARK_OAK_LOGS)
			.group("planks")
			.criterion("has_logs", this.method_10420(ItemTags.DARK_OAK_LOGS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_OAK_PRESSURE_PLATE)
			.input('#', Blocks.DARK_OAK_PLANKS)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.DARK_OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_OAK_SLAB, 6)
			.input('#', Blocks.DARK_OAK_PLANKS)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.DARK_OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_OAK_STAIRS, 4)
			.input('#', Blocks.DARK_OAK_PLANKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.DARK_OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_OAK_TRAPDOOR, 2)
			.input('#', Blocks.DARK_OAK_PLANKS)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.DARK_OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_PRISMARINE)
			.input('S', Items.PRISMARINE_SHARD)
			.input('I', Items.INK_SAC)
			.pattern("SSS")
			.pattern("SIS")
			.pattern("SSS")
			.criterion("has_prismarine_shard", this.method_10426(Items.PRISMARINE_SHARD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PRISMARINE_STAIRS, 4)
			.input('#', Blocks.PRISMARINE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_prismarine", this.method_10426(Blocks.PRISMARINE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PRISMARINE_BRICK_STAIRS, 4)
			.input('#', Blocks.PRISMARINE_BRICKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_prismarine_bricks", this.method_10426(Blocks.PRISMARINE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_PRISMARINE_STAIRS, 4)
			.input('#', Blocks.DARK_PRISMARINE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_dark_prismarine", this.method_10426(Blocks.DARK_PRISMARINE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DAYLIGHT_DETECTOR)
			.input('Q', Items.QUARTZ)
			.input('G', Blocks.GLASS)
			.input('W', Ingredient.fromTag(ItemTags.WOODEN_SLABS))
			.pattern("GGG")
			.pattern("QQQ")
			.pattern("WWW")
			.criterion("has_quartz", this.method_10426(Items.QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DETECTOR_RAIL, 6)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.STONE_PRESSURE_PLATE)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", this.method_10426(Blocks.RAIL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.DIAMOND, 9)
			.input(Blocks.DIAMOND_BLOCK)
			.criterion("has_at_least_9_diamond", this.method_10424(NumberRange.IntRange.atLeast(9), Items.DIAMOND))
			.criterion("has_diamond_block", this.method_10426(Blocks.DIAMOND_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_AXE)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_diamond", this.method_10426(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DIAMOND_BLOCK)
			.input('#', Items.DIAMOND)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_diamond", this.method_10424(NumberRange.IntRange.atLeast(9), Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_BOOTS)
			.input('X', Items.DIAMOND)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", this.method_10426(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_CHESTPLATE)
			.input('X', Items.DIAMOND)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_diamond", this.method_10426(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_HELMET)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_diamond", this.method_10426(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_HOE)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_diamond", this.method_10426(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_LEGGINGS)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", this.method_10426(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_PICKAXE)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_diamond", this.method_10426(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_SHOVEL)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_diamond", this.method_10426(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DIAMOND_SWORD)
			.input('#', Items.STICK)
			.input('X', Items.DIAMOND)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_diamond", this.method_10426(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DIORITE, 2)
			.input('Q', Items.QUARTZ)
			.input('C', Blocks.COBBLESTONE)
			.pattern("CQ")
			.pattern("QC")
			.criterion("has_quartz", this.method_10426(Items.QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DISPENSER)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.input('X', Items.BOW)
			.pattern("###")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_bow", this.method_10426(Items.BOW))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DROPPER)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.pattern("# #")
			.pattern("#R#")
			.criterion("has_redstone", this.method_10426(Items.REDSTONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.EMERALD, 9)
			.input(Blocks.EMERALD_BLOCK)
			.criterion("has_at_least_9_emerald", this.method_10424(NumberRange.IntRange.atLeast(9), Items.EMERALD))
			.criterion("has_emerald_block", this.method_10426(Blocks.EMERALD_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.EMERALD_BLOCK)
			.input('#', Items.EMERALD)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_emerald", this.method_10424(NumberRange.IntRange.atLeast(9), Items.EMERALD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ENCHANTING_TABLE)
			.input('B', Items.BOOK)
			.input('#', Blocks.OBSIDIAN)
			.input('D', Items.DIAMOND)
			.pattern(" B ")
			.pattern("D#D")
			.pattern("###")
			.criterion("has_obsidian", this.method_10426(Blocks.OBSIDIAN))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ENDER_CHEST)
			.input('#', Blocks.OBSIDIAN)
			.input('E', Items.ENDER_EYE)
			.pattern("###")
			.pattern("#E#")
			.pattern("###")
			.criterion("has_ender_eye", this.method_10426(Items.ENDER_EYE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.ENDER_EYE)
			.input(Items.ENDER_PEARL)
			.input(Items.BLAZE_POWDER)
			.criterion("has_blaze_powder", this.method_10426(Items.BLAZE_POWDER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.END_STONE_BRICKS, 4)
			.input('#', Blocks.END_STONE)
			.pattern("##")
			.pattern("##")
			.criterion("has_end_stone", this.method_10426(Blocks.END_STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.END_CRYSTAL)
			.input('T', Items.GHAST_TEAR)
			.input('E', Items.ENDER_EYE)
			.input('G', Blocks.GLASS)
			.pattern("GGG")
			.pattern("GEG")
			.pattern("GTG")
			.criterion("has_ender_eye", this.method_10426(Items.ENDER_EYE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.END_ROD, 4)
			.input('#', Items.POPPED_CHORUS_FRUIT)
			.input('/', Items.BLAZE_ROD)
			.pattern("/")
			.pattern("#")
			.criterion("has_chorus_fruit_popped", this.method_10426(Items.POPPED_CHORUS_FRUIT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.OAK_FENCE, 3)
			.input('#', Items.STICK)
			.input('W', Blocks.OAK_PLANKS)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.OAK_FENCE_GATE)
			.input('#', Items.STICK)
			.input('W', Blocks.OAK_PLANKS)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.OAK_PLANKS))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.FERMENTED_SPIDER_EYE)
			.input(Items.SPIDER_EYE)
			.input(Blocks.BROWN_MUSHROOM)
			.input(Items.SUGAR)
			.criterion("has_spider_eye", this.method_10426(Items.SPIDER_EYE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.FIRE_CHARGE, 3)
			.input(Items.GUNPOWDER)
			.input(Items.BLAZE_POWDER)
			.input(Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.criterion("has_blaze_powder", this.method_10426(Items.BLAZE_POWDER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.FISHING_ROD)
			.input('#', Items.STICK)
			.input('X', Items.STRING)
			.pattern("  #")
			.pattern(" #X")
			.pattern("# X")
			.criterion("has_string", this.method_10426(Items.STRING))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.FLINT_AND_STEEL)
			.input(Items.IRON_INGOT)
			.input(Items.FLINT)
			.criterion("has_flint", this.method_10426(Items.FLINT))
			.criterion("has_obsidian", this.method_10426(Blocks.OBSIDIAN))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.FLOWER_POT)
			.input('#', Items.BRICK)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brick", this.method_10426(Items.BRICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.FURNACE)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.FURNACE_MINECART)
			.input('A', Blocks.FURNACE)
			.input('B', Items.MINECART)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", this.method_10426(Items.MINECART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GLASS_BOTTLE, 3)
			.input('#', Blocks.GLASS)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GLASS_PANE, 16)
			.input('#', Blocks.GLASS)
			.pattern("###")
			.pattern("###")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GLOWSTONE)
			.input('#', Items.GLOWSTONE_DUST)
			.pattern("##")
			.pattern("##")
			.criterion("has_glowstone_dust", this.method_10426(Items.GLOWSTONE_DUST))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_APPLE)
			.input('#', Items.GOLD_INGOT)
			.input('X', Items.APPLE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_AXE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_BOOTS)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_CARROT)
			.input('#', Items.GOLD_NUGGET)
			.input('X', Items.CARROT)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_nugget", this.method_10426(Items.GOLD_NUGGET))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_CHESTPLATE)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_HELMET)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_HOE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_LEGGINGS)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_PICKAXE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POWERED_RAIL, 6)
			.input('R', Items.REDSTONE)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", this.method_10426(Blocks.RAIL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_SHOVEL)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GOLDEN_SWORD)
			.input('#', Items.STICK)
			.input('X', Items.GOLD_INGOT)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GOLD_BLOCK)
			.input('#', Items.GOLD_INGOT)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_gold_ingot", this.method_10424(NumberRange.IntRange.atLeast(9), Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.GOLD_INGOT, 9)
			.input(Blocks.GOLD_BLOCK)
			.group("gold_ingot")
			.criterion("has_at_least_9_gold_ingot", this.method_10424(NumberRange.IntRange.atLeast(9), Items.GOLD_INGOT))
			.criterion("has_gold_block", this.method_10426(Blocks.GOLD_BLOCK))
			.offerTo(consumer, "gold_ingot_from_gold_block");
		ShapedRecipeJsonFactory.create(Items.GOLD_INGOT)
			.input('#', Items.GOLD_NUGGET)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group("gold_ingot")
			.criterion("has_at_least_9_gold_nugget", this.method_10424(NumberRange.IntRange.atLeast(9), Items.GOLD_NUGGET))
			.offerTo(consumer, "gold_ingot_from_nuggets");
		ShapelessRecipeJsonFactory.create(Items.GOLD_NUGGET, 9)
			.input(Items.GOLD_INGOT)
			.criterion("has_at_least_9_gold_nugget", this.method_10424(NumberRange.IntRange.atLeast(9), Items.GOLD_NUGGET))
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.GRANITE)
			.input(Blocks.DIORITE)
			.input(Items.QUARTZ)
			.criterion("has_quartz", this.method_10426(Items.QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GRAY_BANNER)
			.input('#', Blocks.GRAY_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_gray_wool", this.method_10426(Blocks.GRAY_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GRAY_BED)
			.input('#', Blocks.GRAY_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_gray_wool", this.method_10426(Blocks.GRAY_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.GRAY_BED)
			.input(Items.WHITE_BED)
			.input(Items.GRAY_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "gray_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.GRAY_CARPET, 3)
			.input('#', Blocks.GRAY_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_gray_wool", this.method_10426(Blocks.GRAY_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GRAY_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.GRAY_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_gray_dye", this.method_10426(Items.GRAY_DYE))
			.offerTo(consumer, "gray_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.GRAY_CONCRETE_POWDER, 8)
			.input(Items.GRAY_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.GRAY_DYE, 2)
			.input(Items.BLACK_DYE)
			.input(Items.WHITE_DYE)
			.criterion("has_white_dye", this.method_10426(Items.WHITE_DYE))
			.criterion("has_black_dye", this.method_10426(Items.BLACK_DYE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GRAY_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.GRAY_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GRAY_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.GRAY_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GRAY_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.GRAY_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_gray_dye", this.method_10426(Items.GRAY_DYE))
			.offerTo(consumer, "gray_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.GRAY_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.GRAY_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.GRAY_WOOL)
			.input(Items.GRAY_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GREEN_BANNER)
			.input('#', Blocks.GREEN_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_green_wool", this.method_10426(Blocks.GREEN_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GREEN_BED)
			.input('#', Blocks.GREEN_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_green_wool", this.method_10426(Blocks.GREEN_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.GREEN_BED)
			.input(Items.WHITE_BED)
			.input(Items.GREEN_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "green_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.GREEN_CARPET, 3)
			.input('#', Blocks.GREEN_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_green_wool", this.method_10426(Blocks.GREEN_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GREEN_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.GREEN_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_green_dye", this.method_10426(Items.GREEN_DYE))
			.offerTo(consumer, "green_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.GREEN_CONCRETE_POWDER, 8)
			.input(Items.GREEN_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GREEN_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.GREEN_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GREEN_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.GREEN_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GREEN_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.GREEN_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_green_dye", this.method_10426(Items.GREEN_DYE))
			.offerTo(consumer, "green_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.GREEN_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.GREEN_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.GREEN_WOOL)
			.input(Items.GREEN_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.HAY_BLOCK)
			.input('#', Items.WHEAT)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_wheat", this.method_10424(NumberRange.IntRange.atLeast(9), Items.WHEAT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
			.input('#', Items.IRON_INGOT)
			.pattern("##")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.HONEY_BLOCK, 1)
			.input('S', Items.HONEY_BOTTLE)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_honey_block", this.method_10426(Blocks.HONEY_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.HONEYCOMB_BLOCK)
			.input('H', Items.HONEYCOMB)
			.pattern("HH")
			.pattern("HH")
			.criterion("has_honeycomb", this.method_10426(Items.HONEYCOMB))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.HOPPER)
			.input('C', Blocks.CHEST)
			.input('I', Items.IRON_INGOT)
			.pattern("I I")
			.pattern("ICI")
			.pattern(" I ")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.HOPPER_MINECART)
			.input('A', Blocks.HOPPER)
			.input('B', Items.MINECART)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", this.method_10426(Items.MINECART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_AXE)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.IRON_BARS, 16)
			.input('#', Items.IRON_INGOT)
			.pattern("###")
			.pattern("###")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.IRON_BLOCK)
			.input('#', Items.IRON_INGOT)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_iron_ingot", this.method_10424(NumberRange.IntRange.atLeast(9), Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_BOOTS)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_CHESTPLATE)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.IRON_DOOR, 3)
			.input('#', Items.IRON_INGOT)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_HELMET)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_HOE)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.IRON_INGOT, 9)
			.input(Blocks.IRON_BLOCK)
			.group("iron_ingot")
			.criterion("has_at_least_9_iron_ingot", this.method_10424(NumberRange.IntRange.atLeast(9), Items.IRON_INGOT))
			.criterion("has_iron_block", this.method_10426(Blocks.IRON_BLOCK))
			.offerTo(consumer, "iron_ingot_from_iron_block");
		ShapedRecipeJsonFactory.create(Items.IRON_INGOT)
			.input('#', Items.IRON_NUGGET)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group("iron_ingot")
			.criterion("has_at_least_9_iron_nugget", this.method_10424(NumberRange.IntRange.atLeast(9), Items.IRON_NUGGET))
			.offerTo(consumer, "iron_ingot_from_nuggets");
		ShapedRecipeJsonFactory.create(Items.IRON_LEGGINGS)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.IRON_NUGGET, 9)
			.input(Items.IRON_INGOT)
			.criterion("has_at_least_9_iron_nugget", this.method_10424(NumberRange.IntRange.atLeast(9), Items.IRON_NUGGET))
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_PICKAXE)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_SHOVEL)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.IRON_SWORD)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.IRON_TRAPDOOR)
			.input('#', Items.IRON_INGOT)
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ITEM_FRAME)
			.input('#', Items.STICK)
			.input('X', Items.LEATHER)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_leather", this.method_10426(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JUKEBOX)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.DIAMOND)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_diamond", this.method_10426(Items.DIAMOND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JUNGLE_WOOD, 3)
			.input('#', Blocks.JUNGLE_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.JUNGLE_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STRIPPED_JUNGLE_WOOD, 3)
			.input('#', Blocks.STRIPPED_JUNGLE_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.STRIPPED_JUNGLE_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.JUNGLE_BOAT)
			.input('#', Blocks.JUNGLE_PLANKS)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.WATER))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.JUNGLE_BUTTON)
			.input(Blocks.JUNGLE_PLANKS)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.JUNGLE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JUNGLE_DOOR, 3)
			.input('#', Blocks.JUNGLE_PLANKS)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.JUNGLE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JUNGLE_FENCE, 3)
			.input('#', Items.STICK)
			.input('W', Blocks.JUNGLE_PLANKS)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.JUNGLE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JUNGLE_FENCE_GATE)
			.input('#', Items.STICK)
			.input('W', Blocks.JUNGLE_PLANKS)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.JUNGLE_PLANKS))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.JUNGLE_PLANKS, 4)
			.input(ItemTags.JUNGLE_LOGS)
			.group("planks")
			.criterion("has_log", this.method_10420(ItemTags.JUNGLE_LOGS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JUNGLE_PRESSURE_PLATE)
			.input('#', Blocks.JUNGLE_PLANKS)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.JUNGLE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JUNGLE_SLAB, 6)
			.input('#', Blocks.JUNGLE_PLANKS)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.JUNGLE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JUNGLE_STAIRS, 4)
			.input('#', Blocks.JUNGLE_PLANKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.JUNGLE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JUNGLE_TRAPDOOR, 2)
			.input('#', Blocks.JUNGLE_PLANKS)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.JUNGLE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LADDER, 3)
			.input('#', Items.STICK)
			.pattern("# #")
			.pattern("###")
			.pattern("# #")
			.criterion("has_stick", this.method_10426(Items.STICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LAPIS_BLOCK)
			.input('#', Items.LAPIS_LAZULI)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_lapis", this.method_10424(NumberRange.IntRange.atLeast(9), Items.LAPIS_LAZULI))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LAPIS_LAZULI, 9)
			.input(Blocks.LAPIS_BLOCK)
			.criterion("has_at_least_9_lapis", this.method_10424(NumberRange.IntRange.atLeast(9), Items.LAPIS_LAZULI))
			.criterion("has_lapis_block", this.method_10426(Blocks.LAPIS_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEAD, 2)
			.input('~', Items.STRING)
			.input('O', Items.SLIME_BALL)
			.pattern("~~ ")
			.pattern("~O ")
			.pattern("  ~")
			.criterion("has_slime_ball", this.method_10426(Items.SLIME_BALL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER)
			.input('#', Items.RABBIT_HIDE)
			.pattern("##")
			.pattern("##")
			.criterion("has_rabbit_hide", this.method_10426(Items.RABBIT_HIDE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER_BOOTS)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", this.method_10426(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER_CHESTPLATE)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_leather", this.method_10426(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER_HELMET)
			.input('X', Items.LEATHER)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", this.method_10426(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER_LEGGINGS)
			.input('X', Items.LEATHER)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", this.method_10426(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LEATHER_HORSE_ARMOR)
			.input('X', Items.LEATHER)
			.pattern("X X")
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", this.method_10426(Items.LEATHER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LECTERN)
			.input('S', ItemTags.WOODEN_SLABS)
			.input('B', Blocks.BOOKSHELF)
			.pattern("SSS")
			.pattern(" B ")
			.pattern(" S ")
			.criterion("has_book", this.method_10426(Items.BOOK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LEVER)
			.input('#', Blocks.COBBLESTONE)
			.input('X', Items.STICK)
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LIGHT_BLUE_BANNER)
			.input('#', Blocks.LIGHT_BLUE_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_light_blue_wool", this.method_10426(Blocks.LIGHT_BLUE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LIGHT_BLUE_BED)
			.input('#', Blocks.LIGHT_BLUE_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_light_blue_wool", this.method_10426(Blocks.LIGHT_BLUE_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIGHT_BLUE_BED)
			.input(Items.WHITE_BED)
			.input(Items.LIGHT_BLUE_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "light_blue_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_BLUE_CARPET, 3)
			.input('#', Blocks.LIGHT_BLUE_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_light_blue_wool", this.method_10426(Blocks.LIGHT_BLUE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_BLUE_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.LIGHT_BLUE_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_light_blue_dye", this.method_10426(Items.LIGHT_BLUE_DYE))
			.offerTo(consumer, "light_blue_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.LIGHT_BLUE_CONCRETE_POWDER, 8)
			.input(Items.LIGHT_BLUE_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIGHT_BLUE_DYE)
			.input(Blocks.BLUE_ORCHID)
			.group("light_blue_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.BLUE_ORCHID))
			.offerTo(consumer, "light_blue_dye_from_blue_orchid");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_BLUE_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.WHITE_DYE)
			.group("light_blue_dye")
			.criterion("has_blue_dye", this.method_10426(Items.BLUE_DYE))
			.criterion("has_white_dye", this.method_10426(Items.WHITE_DYE))
			.offerTo(consumer, "light_blue_dye_from_blue_white_dye");
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_BLUE_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.LIGHT_BLUE_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.LIGHT_BLUE_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.LIGHT_BLUE_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_light_blue_dye", this.method_10426(Items.LIGHT_BLUE_DYE))
			.offerTo(consumer, "light_blue_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_BLUE_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.LIGHT_BLUE_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.LIGHT_BLUE_WOOL)
			.input(Items.LIGHT_BLUE_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LIGHT_GRAY_BANNER)
			.input('#', Blocks.LIGHT_GRAY_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_light_gray_wool", this.method_10426(Blocks.LIGHT_GRAY_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LIGHT_GRAY_BED)
			.input('#', Blocks.LIGHT_GRAY_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_light_gray_wool", this.method_10426(Blocks.LIGHT_GRAY_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_BED)
			.input(Items.WHITE_BED)
			.input(Items.LIGHT_GRAY_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "light_gray_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_GRAY_CARPET, 3)
			.input('#', Blocks.LIGHT_GRAY_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_light_gray_wool", this.method_10426(Blocks.LIGHT_GRAY_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_GRAY_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.LIGHT_GRAY_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_light_gray_dye", this.method_10426(Items.LIGHT_GRAY_DYE))
			.offerTo(consumer, "light_gray_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.LIGHT_GRAY_CONCRETE_POWDER, 8)
			.input(Items.LIGHT_GRAY_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE)
			.input(Blocks.AZURE_BLUET)
			.group("light_gray_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.AZURE_BLUET))
			.offerTo(consumer, "light_gray_dye_from_azure_bluet");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE, 2)
			.input(Items.GRAY_DYE)
			.input(Items.WHITE_DYE)
			.group("light_gray_dye")
			.criterion("has_gray_dye", this.method_10426(Items.GRAY_DYE))
			.criterion("has_white_dye", this.method_10426(Items.WHITE_DYE))
			.offerTo(consumer, "light_gray_dye_from_gray_white_dye");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE, 3)
			.input(Items.BLACK_DYE)
			.input(Items.WHITE_DYE, 2)
			.group("light_gray_dye")
			.criterion("has_white_dye", this.method_10426(Items.WHITE_DYE))
			.criterion("has_black_dye", this.method_10426(Items.BLACK_DYE))
			.offerTo(consumer, "light_gray_dye_from_black_white_dye");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE)
			.input(Blocks.OXEYE_DAISY)
			.group("light_gray_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.OXEYE_DAISY))
			.offerTo(consumer, "light_gray_dye_from_oxeye_daisy");
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_DYE)
			.input(Blocks.WHITE_TULIP)
			.group("light_gray_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.WHITE_TULIP))
			.offerTo(consumer, "light_gray_dye_from_white_tulip");
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_GRAY_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.LIGHT_GRAY_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.LIGHT_GRAY_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.LIGHT_GRAY_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_light_gray_dye", this.method_10426(Items.LIGHT_GRAY_DYE))
			.offerTo(consumer, "light_gray_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_GRAY_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.LIGHT_GRAY_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.LIGHT_GRAY_WOOL)
			.input(Items.LIGHT_GRAY_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE)
			.input('#', Items.GOLD_INGOT)
			.pattern("##")
			.criterion("has_gold_ingot", this.method_10426(Items.GOLD_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LIME_BANNER)
			.input('#', Blocks.LIME_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_lime_wool", this.method_10426(Blocks.LIME_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LIME_BED)
			.input('#', Blocks.LIME_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_lime_wool", this.method_10426(Blocks.LIME_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIME_BED)
			.input(Items.WHITE_BED)
			.input(Items.LIME_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "lime_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.LIME_CARPET, 3)
			.input('#', Blocks.LIME_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_lime_wool", this.method_10426(Blocks.LIME_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIME_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.LIME_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_lime_dye", this.method_10426(Items.LIME_DYE))
			.offerTo(consumer, "lime_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.LIME_CONCRETE_POWDER, 8)
			.input(Items.LIME_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIME_DYE, 2)
			.input(Items.GREEN_DYE)
			.input(Items.WHITE_DYE)
			.criterion("has_green_dye", this.method_10426(Items.GREEN_DYE))
			.criterion("has_white_dye", this.method_10426(Items.WHITE_DYE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIME_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.LIME_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIME_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.LIME_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LIME_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.LIME_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_lime_dye", this.method_10426(Items.LIME_DYE))
			.offerTo(consumer, "lime_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.LIME_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.LIME_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.LIME_WOOL)
			.input(Items.LIME_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.JACK_O_LANTERN)
			.input('A', Blocks.CARVED_PUMPKIN)
			.input('B', Blocks.TORCH)
			.pattern("A")
			.pattern("B")
			.criterion("has_carved_pumpkin", this.method_10426(Blocks.CARVED_PUMPKIN))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.MAGENTA_BANNER)
			.input('#', Blocks.MAGENTA_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_magenta_wool", this.method_10426(Blocks.MAGENTA_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.MAGENTA_BED)
			.input('#', Blocks.MAGENTA_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_magenta_wool", this.method_10426(Blocks.MAGENTA_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_BED)
			.input(Items.WHITE_BED)
			.input(Items.MAGENTA_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "magenta_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.MAGENTA_CARPET, 3)
			.input('#', Blocks.MAGENTA_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_magenta_wool", this.method_10426(Blocks.MAGENTA_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MAGENTA_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.MAGENTA_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_magenta_dye", this.method_10426(Items.MAGENTA_DYE))
			.offerTo(consumer, "magenta_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.MAGENTA_CONCRETE_POWDER, 8)
			.input(Items.MAGENTA_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE)
			.input(Blocks.ALLIUM)
			.group("magenta_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.ALLIUM))
			.offerTo(consumer, "magenta_dye_from_allium");
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 4)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE, 2)
			.input(Items.WHITE_DYE)
			.group("magenta_dye")
			.criterion("has_blue_dye", this.method_10426(Items.BLUE_DYE))
			.criterion("has_rose_red", this.method_10426(Items.RED_DYE))
			.criterion("has_white_dye", this.method_10426(Items.WHITE_DYE))
			.offerTo(consumer, "magenta_dye_from_blue_red_white_dye");
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 3)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE)
			.input(Items.PINK_DYE)
			.group("magenta_dye")
			.criterion("has_pink_dye", this.method_10426(Items.PINK_DYE))
			.criterion("has_blue_dye", this.method_10426(Items.BLUE_DYE))
			.criterion("has_red_dye", this.method_10426(Items.RED_DYE))
			.offerTo(consumer, "magenta_dye_from_blue_red_pink");
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 2)
			.input(Blocks.LILAC)
			.group("magenta_dye")
			.criterion("has_double_plant", this.method_10426(Blocks.LILAC))
			.offerTo(consumer, "magenta_dye_from_lilac");
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_DYE, 2)
			.input(Items.PURPLE_DYE)
			.input(Items.PINK_DYE)
			.group("magenta_dye")
			.criterion("has_pink_dye", this.method_10426(Items.PINK_DYE))
			.criterion("has_purple_dye", this.method_10426(Items.PURPLE_DYE))
			.offerTo(consumer, "magenta_dye_from_purple_and_pink");
		ShapedRecipeJsonFactory.create(Blocks.MAGENTA_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.MAGENTA_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MAGENTA_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.MAGENTA_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MAGENTA_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.MAGENTA_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_magenta_dye", this.method_10426(Items.MAGENTA_DYE))
			.offerTo(consumer, "magenta_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.MAGENTA_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.MAGENTA_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.MAGENTA_WOOL)
			.input(Items.MAGENTA_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MAGMA_BLOCK)
			.input('#', Items.MAGMA_CREAM)
			.pattern("##")
			.pattern("##")
			.criterion("has_magma_cream", this.method_10426(Items.MAGMA_CREAM))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MAGMA_CREAM)
			.input(Items.BLAZE_POWDER)
			.input(Items.SLIME_BALL)
			.criterion("has_blaze_powder", this.method_10426(Items.BLAZE_POWDER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.MAP)
			.input('#', Items.PAPER)
			.input('X', Items.COMPASS)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_compass", this.method_10426(Items.COMPASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MELON)
			.input('M', Items.MELON_SLICE)
			.pattern("MMM")
			.pattern("MMM")
			.pattern("MMM")
			.criterion("has_melon", this.method_10426(Items.MELON_SLICE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MELON_SEEDS).input(Items.MELON_SLICE).criterion("has_melon", this.method_10426(Items.MELON_SLICE)).offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.MINECART)
			.input('#', Items.IRON_INGOT)
			.pattern("# #")
			.pattern("###")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.MOSSY_COBBLESTONE)
			.input(Blocks.COBBLESTONE)
			.input(Blocks.VINE)
			.criterion("has_vine", this.method_10426(Blocks.VINE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MOSSY_COBBLESTONE_WALL, 6)
			.input('#', Blocks.MOSSY_COBBLESTONE)
			.pattern("###")
			.pattern("###")
			.criterion("has_mossy_cobblestone", this.method_10426(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.MOSSY_STONE_BRICKS)
			.input(Blocks.STONE_BRICKS)
			.input(Blocks.VINE)
			.criterion("has_mossy_cobblestone", this.method_10426(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MUSHROOM_STEW)
			.input(Blocks.BROWN_MUSHROOM)
			.input(Blocks.RED_MUSHROOM)
			.input(Items.BOWL)
			.criterion("has_mushroom_stew", this.method_10426(Items.MUSHROOM_STEW))
			.criterion("has_bowl", this.method_10426(Items.BOWL))
			.criterion("has_brown_mushroom", this.method_10426(Blocks.BROWN_MUSHROOM))
			.criterion("has_red_mushroom", this.method_10426(Blocks.RED_MUSHROOM))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NETHER_BRICKS)
			.input('N', Items.NETHER_BRICK)
			.pattern("NN")
			.pattern("NN")
			.criterion("has_netherbrick", this.method_10426(Items.NETHER_BRICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NETHER_BRICK_FENCE, 6)
			.input('#', Blocks.NETHER_BRICKS)
			.input('-', Items.NETHER_BRICK)
			.pattern("#-#")
			.pattern("#-#")
			.criterion("has_nether_brick", this.method_10426(Blocks.NETHER_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NETHER_BRICK_SLAB, 6)
			.input('#', Blocks.NETHER_BRICKS)
			.pattern("###")
			.criterion("has_nether_brick", this.method_10426(Blocks.NETHER_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NETHER_BRICK_STAIRS, 4)
			.input('#', Blocks.NETHER_BRICKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_nether_brick", this.method_10426(Blocks.NETHER_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NETHER_WART_BLOCK)
			.input('#', Items.NETHER_WART)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_nether_wart", this.method_10426(Items.NETHER_WART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NOTE_BLOCK)
			.input('#', ItemTags.PLANKS)
			.input('X', Items.REDSTONE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_redstone", this.method_10426(Items.REDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.OAK_WOOD, 3)
			.input('#', Blocks.OAK_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.OAK_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STRIPPED_OAK_WOOD, 3)
			.input('#', Blocks.STRIPPED_OAK_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.STRIPPED_OAK_LOG))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.OAK_BUTTON)
			.input(Blocks.OAK_PLANKS)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.OAK_PLANKS))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.OAK_PLANKS, 4)
			.input(ItemTags.OAK_LOGS)
			.group("planks")
			.criterion("has_log", this.method_10420(ItemTags.OAK_LOGS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.OAK_PRESSURE_PLATE)
			.input('#', Blocks.OAK_PLANKS)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.OAK_SLAB, 6)
			.input('#', Blocks.OAK_PLANKS)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.OAK_STAIRS, 4)
			.input('#', Blocks.OAK_PLANKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.OAK_TRAPDOOR, 2)
			.input('#', Blocks.OAK_PLANKS)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.OBSERVER)
			.input('Q', Items.QUARTZ)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.pattern("###")
			.pattern("RRQ")
			.pattern("###")
			.criterion("has_quartz", this.method_10426(Items.QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ORANGE_BANNER)
			.input('#', Blocks.ORANGE_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_orange_wool", this.method_10426(Blocks.ORANGE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ORANGE_BED)
			.input('#', Blocks.ORANGE_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_orange_wool", this.method_10426(Blocks.ORANGE_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.ORANGE_BED)
			.input(Items.WHITE_BED)
			.input(Items.ORANGE_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "orange_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.ORANGE_CARPET, 3)
			.input('#', Blocks.ORANGE_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_orange_wool", this.method_10426(Blocks.ORANGE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ORANGE_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.ORANGE_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_oramge_dye", this.method_10426(Items.ORANGE_DYE))
			.offerTo(consumer, "orange_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.ORANGE_CONCRETE_POWDER, 8)
			.input(Items.ORANGE_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.ORANGE_DYE)
			.input(Blocks.ORANGE_TULIP)
			.group("orange_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.ORANGE_TULIP))
			.offerTo(consumer, "orange_dye_from_orange_tulip");
		ShapelessRecipeJsonFactory.create(Items.ORANGE_DYE, 2)
			.input(Items.RED_DYE)
			.input(Items.YELLOW_DYE)
			.group("orange_dye")
			.criterion("has_red_dye", this.method_10426(Items.RED_DYE))
			.criterion("has_yellow_dye", this.method_10426(Items.YELLOW_DYE))
			.offerTo(consumer, "orange_dye_from_red_yellow");
		ShapedRecipeJsonFactory.create(Blocks.ORANGE_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.ORANGE_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ORANGE_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.ORANGE_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ORANGE_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.ORANGE_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_orange_dye", this.method_10426(Items.ORANGE_DYE))
			.offerTo(consumer, "orange_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.ORANGE_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.ORANGE_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.ORANGE_WOOL)
			.input(Items.ORANGE_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.PAINTING)
			.input('#', Items.STICK)
			.input('X', Ingredient.fromTag(ItemTags.WOOL))
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_wool", this.method_10420(ItemTags.WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.PAPER, 3)
			.input('#', Blocks.SUGAR_CANE)
			.pattern("###")
			.criterion("has_reeds", this.method_10426(Blocks.SUGAR_CANE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.QUARTZ_PILLAR, 2)
			.input('#', Blocks.QUARTZ_BLOCK)
			.pattern("#")
			.pattern("#")
			.criterion("has_chiseled_quartz_block", this.method_10426(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", this.method_10426(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", this.method_10426(Blocks.QUARTZ_PILLAR))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.PACKED_ICE)
			.input(Blocks.ICE, 9)
			.criterion("has_at_least_9_ice", this.method_10424(NumberRange.IntRange.atLeast(9), Blocks.ICE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.PINK_BANNER)
			.input('#', Blocks.PINK_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_pink_wool", this.method_10426(Blocks.PINK_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.PINK_BED)
			.input('#', Blocks.PINK_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_pink_wool", this.method_10426(Blocks.PINK_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PINK_BED)
			.input(Items.WHITE_BED)
			.input(Items.PINK_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "pink_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.PINK_CARPET, 3)
			.input('#', Blocks.PINK_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_pink_wool", this.method_10426(Blocks.PINK_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PINK_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.PINK_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_pink_dye", this.method_10426(Items.PINK_DYE))
			.offerTo(consumer, "pink_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.PINK_CONCRETE_POWDER, 8)
			.input(Items.PINK_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PINK_DYE, 2)
			.input(Blocks.PEONY)
			.group("pink_dye")
			.criterion("has_double_plant", this.method_10426(Blocks.PEONY))
			.offerTo(consumer, "pink_dye_from_peony");
		ShapelessRecipeJsonFactory.create(Items.PINK_DYE)
			.input(Blocks.PINK_TULIP)
			.group("pink_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.PINK_TULIP))
			.offerTo(consumer, "pink_dye_from_pink_tulip");
		ShapelessRecipeJsonFactory.create(Items.PINK_DYE, 2)
			.input(Items.RED_DYE)
			.input(Items.WHITE_DYE)
			.group("pink_dye")
			.criterion("has_white_dye", this.method_10426(Items.WHITE_DYE))
			.criterion("has_red_dye", this.method_10426(Items.RED_DYE))
			.offerTo(consumer, "pink_dye_from_red_white_dye");
		ShapedRecipeJsonFactory.create(Blocks.PINK_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.PINK_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PINK_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.PINK_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PINK_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.PINK_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_pink_dye", this.method_10426(Items.PINK_DYE))
			.offerTo(consumer, "pink_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.PINK_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.PINK_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.PINK_WOOL)
			.input(Items.PINK_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PISTON)
			.input('R', Items.REDSTONE)
			.input('#', Blocks.COBBLESTONE)
			.input('T', ItemTags.PLANKS)
			.input('X', Items.IRON_INGOT)
			.pattern("TTT")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_redstone", this.method_10426(Items.REDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POLISHED_GRANITE, 4)
			.input('S', Blocks.GRANITE)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_stone", this.method_10426(Blocks.GRANITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POLISHED_DIORITE, 4)
			.input('S', Blocks.DIORITE)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_stone", this.method_10426(Blocks.DIORITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POLISHED_ANDESITE, 4)
			.input('S', Blocks.ANDESITE)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_stone", this.method_10426(Blocks.ANDESITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PRISMARINE)
			.input('S', Items.PRISMARINE_SHARD)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_prismarine_shard", this.method_10426(Items.PRISMARINE_SHARD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PRISMARINE_BRICKS)
			.input('S', Items.PRISMARINE_SHARD)
			.pattern("SSS")
			.pattern("SSS")
			.pattern("SSS")
			.criterion("has_prismarine_shard", this.method_10426(Items.PRISMARINE_SHARD))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PRISMARINE_SLAB, 6)
			.input('#', Blocks.PRISMARINE)
			.pattern("###")
			.criterion("has_prismarine", this.method_10426(Blocks.PRISMARINE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PRISMARINE_BRICK_SLAB, 6)
			.input('#', Blocks.PRISMARINE_BRICKS)
			.pattern("###")
			.criterion("has_prismarine_bricks", this.method_10426(Blocks.PRISMARINE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DARK_PRISMARINE_SLAB, 6)
			.input('#', Blocks.DARK_PRISMARINE)
			.pattern("###")
			.criterion("has_dark_prismarine", this.method_10426(Blocks.DARK_PRISMARINE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PUMPKIN_PIE)
			.input(Blocks.PUMPKIN)
			.input(Items.SUGAR)
			.input(Items.EGG)
			.criterion("has_carved_pumpkin", this.method_10426(Blocks.CARVED_PUMPKIN))
			.criterion("has_pumpkin", this.method_10426(Blocks.PUMPKIN))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PUMPKIN_SEEDS, 4).input(Blocks.PUMPKIN).criterion("has_pumpkin", this.method_10426(Blocks.PUMPKIN)).offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.PURPLE_BANNER)
			.input('#', Blocks.PURPLE_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_purple_wool", this.method_10426(Blocks.PURPLE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.PURPLE_BED)
			.input('#', Blocks.PURPLE_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_purple_wool", this.method_10426(Blocks.PURPLE_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PURPLE_BED)
			.input(Items.WHITE_BED)
			.input(Items.PURPLE_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "purple_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.PURPLE_CARPET, 3)
			.input('#', Blocks.PURPLE_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_purple_wool", this.method_10426(Blocks.PURPLE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PURPLE_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.PURPLE_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_purple_dye", this.method_10426(Items.PURPLE_DYE))
			.offerTo(consumer, "purple_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.PURPLE_CONCRETE_POWDER, 8)
			.input(Items.PURPLE_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PURPLE_DYE, 2)
			.input(Items.BLUE_DYE)
			.input(Items.RED_DYE)
			.criterion("has_blue_dye", this.method_10426(Items.BLUE_DYE))
			.criterion("has_red_dye", this.method_10426(Items.RED_DYE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SHULKER_BOX)
			.input('#', Blocks.CHEST)
			.input('-', Items.SHULKER_SHELL)
			.pattern("-")
			.pattern("#")
			.pattern("-")
			.criterion("has_shulker_shell", this.method_10426(Items.SHULKER_SHELL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PURPLE_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.PURPLE_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PURPLE_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.PURPLE_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PURPLE_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.PURPLE_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_purple_dye", this.method_10426(Items.PURPLE_DYE))
			.offerTo(consumer, "purple_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.PURPLE_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.PURPLE_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.PURPLE_WOOL)
			.input(Items.PURPLE_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PURPUR_BLOCK, 4)
			.input('F', Items.POPPED_CHORUS_FRUIT)
			.pattern("FF")
			.pattern("FF")
			.criterion("has_chorus_fruit_popped", this.method_10426(Items.POPPED_CHORUS_FRUIT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PURPUR_PILLAR)
			.input('#', Blocks.PURPUR_SLAB)
			.pattern("#")
			.pattern("#")
			.criterion("has_purpur_block", this.method_10426(Blocks.PURPUR_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PURPUR_SLAB, 6)
			.input('#', Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR))
			.pattern("###")
			.criterion("has_purpur_block", this.method_10426(Blocks.PURPUR_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PURPUR_STAIRS, 4)
			.input('#', Ingredient.ofItems(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_purpur_block", this.method_10426(Blocks.PURPUR_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.QUARTZ_BLOCK)
			.input('#', Items.QUARTZ)
			.pattern("##")
			.pattern("##")
			.criterion("has_quartz", this.method_10426(Items.QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.QUARTZ_SLAB, 6)
			.input('#', Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR))
			.pattern("###")
			.criterion("has_chiseled_quartz_block", this.method_10426(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", this.method_10426(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", this.method_10426(Blocks.QUARTZ_PILLAR))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.QUARTZ_STAIRS, 4)
			.input('#', Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_chiseled_quartz_block", this.method_10426(Blocks.CHISELED_QUARTZ_BLOCK))
			.criterion("has_quartz_block", this.method_10426(Blocks.QUARTZ_BLOCK))
			.criterion("has_quartz_pillar", this.method_10426(Blocks.QUARTZ_PILLAR))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.RABBIT_STEW)
			.input(Items.BAKED_POTATO)
			.input(Items.COOKED_RABBIT)
			.input(Items.BOWL)
			.input(Items.CARROT)
			.input(Blocks.BROWN_MUSHROOM)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", this.method_10426(Items.COOKED_RABBIT))
			.offerTo(consumer, "rabbit_stew_from_brown_mushroom");
		ShapelessRecipeJsonFactory.create(Items.RABBIT_STEW)
			.input(Items.BAKED_POTATO)
			.input(Items.COOKED_RABBIT)
			.input(Items.BOWL)
			.input(Items.CARROT)
			.input(Blocks.RED_MUSHROOM)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", this.method_10426(Items.COOKED_RABBIT))
			.offerTo(consumer, "rabbit_stew_from_red_mushroom");
		ShapedRecipeJsonFactory.create(Blocks.RAIL, 16)
			.input('#', Items.STICK)
			.input('X', Items.IRON_INGOT)
			.pattern("X X")
			.pattern("X#X")
			.pattern("X X")
			.criterion("has_minecart", this.method_10426(Items.MINECART))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.REDSTONE, 9)
			.input(Blocks.REDSTONE_BLOCK)
			.criterion("has_redstone_block", this.method_10426(Blocks.REDSTONE_BLOCK))
			.criterion("has_at_least_9_redstone", this.method_10424(NumberRange.IntRange.atLeast(9), Items.REDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.REDSTONE_BLOCK)
			.input('#', Items.REDSTONE)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_redstone", this.method_10424(NumberRange.IntRange.atLeast(9), Items.REDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.REDSTONE_LAMP)
			.input('R', Items.REDSTONE)
			.input('G', Blocks.GLOWSTONE)
			.pattern(" R ")
			.pattern("RGR")
			.pattern(" R ")
			.criterion("has_glowstone", this.method_10426(Blocks.GLOWSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.REDSTONE_TORCH)
			.input('#', Items.STICK)
			.input('X', Items.REDSTONE)
			.pattern("X")
			.pattern("#")
			.criterion("has_redstone", this.method_10426(Items.REDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.RED_BANNER)
			.input('#', Blocks.RED_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_red_wool", this.method_10426(Blocks.RED_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.RED_BED)
			.input('#', Blocks.RED_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_red_wool", this.method_10426(Blocks.RED_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.RED_BED)
			.input(Items.WHITE_BED)
			.input(Items.RED_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "red_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.RED_CARPET, 3)
			.input('#', Blocks.RED_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_red_wool", this.method_10426(Blocks.RED_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.RED_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_red_dye", this.method_10426(Items.RED_DYE))
			.offerTo(consumer, "red_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.RED_CONCRETE_POWDER, 8)
			.input(Items.RED_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.RED_DYE)
			.input(Items.BEETROOT)
			.group("red_dye")
			.criterion("has_beetroot", this.method_10426(Items.BEETROOT))
			.offerTo(consumer, "red_dye_from_beetroot");
		ShapelessRecipeJsonFactory.create(Items.RED_DYE)
			.input(Blocks.POPPY)
			.group("red_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.POPPY))
			.offerTo(consumer, "red_dye_from_poppy");
		ShapelessRecipeJsonFactory.create(Items.RED_DYE, 2)
			.input(Blocks.ROSE_BUSH)
			.group("red_dye")
			.criterion("has_double_plant", this.method_10426(Blocks.ROSE_BUSH))
			.offerTo(consumer, "red_dye_from_rose_bush");
		ShapelessRecipeJsonFactory.create(Items.RED_DYE)
			.input(Blocks.RED_TULIP)
			.group("red_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.RED_TULIP))
			.offerTo(consumer, "red_dye_from_tulip");
		ShapedRecipeJsonFactory.create(Blocks.RED_NETHER_BRICKS)
			.input('W', Items.NETHER_WART)
			.input('N', Items.NETHER_BRICK)
			.pattern("NW")
			.pattern("WN")
			.criterion("has_nether_wart", this.method_10426(Items.NETHER_WART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_SANDSTONE)
			.input('#', Blocks.RED_SAND)
			.pattern("##")
			.pattern("##")
			.criterion("has_sand", this.method_10426(Blocks.RED_SAND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_SANDSTONE_SLAB, 6)
			.input('#', Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE))
			.pattern("###")
			.criterion("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", this.method_10426(Blocks.CHISELED_RED_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CUT_RED_SANDSTONE_SLAB, 6)
			.input('#', Blocks.CUT_RED_SANDSTONE)
			.pattern("###")
			.criterion("has_cut_red_sandstone", this.method_10426(Blocks.CUT_RED_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_SANDSTONE_STAIRS, 4)
			.input('#', Ingredient.ofItems(Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.criterion("has_chiseled_red_sandstone", this.method_10426(Blocks.CHISELED_RED_SANDSTONE))
			.criterion("has_cut_red_sandstone", this.method_10426(Blocks.CUT_RED_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.RED_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.RED_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.RED_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_red_dye", this.method_10426(Items.RED_DYE))
			.offerTo(consumer, "red_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.RED_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.RED_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.RED_WOOL)
			.input(Items.RED_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.REPEATER)
			.input('#', Blocks.REDSTONE_TORCH)
			.input('X', Items.REDSTONE)
			.input('I', Blocks.STONE)
			.pattern("#X#")
			.pattern("III")
			.criterion("has_redstone_torch", this.method_10426(Blocks.REDSTONE_TORCH))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SANDSTONE)
			.input('#', Blocks.SAND)
			.pattern("##")
			.pattern("##")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SANDSTONE_SLAB, 6)
			.input('#', Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE))
			.pattern("###")
			.criterion("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.criterion("has_chiseled_sandstone", this.method_10426(Blocks.CHISELED_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CUT_SANDSTONE_SLAB, 6)
			.input('#', Blocks.CUT_SANDSTONE)
			.pattern("###")
			.criterion("has_cut_sandstone", this.method_10426(Blocks.CUT_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SANDSTONE_STAIRS, 4)
			.input('#', Ingredient.ofItems(Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.criterion("has_chiseled_sandstone", this.method_10426(Blocks.CHISELED_SANDSTONE))
			.criterion("has_cut_sandstone", this.method_10426(Blocks.CUT_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SEA_LANTERN)
			.input('S', Items.PRISMARINE_SHARD)
			.input('C', Items.PRISMARINE_CRYSTALS)
			.pattern("SCS")
			.pattern("CCC")
			.pattern("SCS")
			.criterion("has_prismarine_crystals", this.method_10426(Items.PRISMARINE_CRYSTALS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.SHEARS)
			.input('#', Items.IRON_INGOT)
			.pattern(" #")
			.pattern("# ")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.SHIELD)
			.input('W', ItemTags.PLANKS)
			.input('o', Items.IRON_INGOT)
			.pattern("WoW")
			.pattern("WWW")
			.pattern(" W ")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.OAK_SIGN, 3)
			.input('#', Items.OAK_PLANKS)
			.input('X', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_oak_planks", this.method_10426(Items.OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.SPRUCE_SIGN, 3)
			.input('#', Items.SPRUCE_PLANKS)
			.input('X', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_spruce_planks", this.method_10426(Items.SPRUCE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BIRCH_SIGN, 3)
			.input('#', Items.BIRCH_PLANKS)
			.input('X', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_birch_planks", this.method_10426(Items.BIRCH_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ACACIA_SIGN, 3)
			.input('#', Items.ACACIA_PLANKS)
			.input('X', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_acacia_planks", this.method_10426(Items.ACACIA_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.JUNGLE_SIGN, 3)
			.input('#', Items.JUNGLE_PLANKS)
			.input('X', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_jungle_planks", this.method_10426(Items.JUNGLE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.DARK_OAK_SIGN, 3)
			.input('#', Items.DARK_OAK_PLANKS)
			.input('X', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_dark_oak_planks", this.method_10426(Items.DARK_OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SLIME_BLOCK)
			.input('#', Items.SLIME_BALL)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_slime_ball", this.method_10424(NumberRange.IntRange.atLeast(9), Items.SLIME_BALL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.SLIME_BALL, 9)
			.input(Blocks.SLIME_BLOCK)
			.criterion("has_at_least_9_slime_ball", this.method_10424(NumberRange.IntRange.atLeast(9), Items.SLIME_BALL))
			.criterion("has_slime", this.method_10426(Blocks.SLIME_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CUT_RED_SANDSTONE, 4)
			.input('#', Blocks.RED_SANDSTONE)
			.pattern("##")
			.pattern("##")
			.criterion("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CUT_SANDSTONE, 4)
			.input('#', Blocks.SANDSTONE)
			.pattern("##")
			.pattern("##")
			.criterion("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SNOW_BLOCK)
			.input('#', Items.SNOWBALL)
			.pattern("##")
			.pattern("##")
			.criterion("has_snowball", this.method_10426(Items.SNOWBALL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SNOW, 6)
			.input('#', Blocks.SNOW_BLOCK)
			.pattern("###")
			.criterion("has_snowball", this.method_10426(Items.SNOWBALL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GLISTERING_MELON_SLICE)
			.input('#', Items.GOLD_NUGGET)
			.input('X', Items.MELON_SLICE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_melon", this.method_10426(Items.MELON_SLICE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.SPECTRAL_ARROW, 2)
			.input('#', Items.GLOWSTONE_DUST)
			.input('X', Items.ARROW)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_glowstone_dust", this.method_10426(Items.GLOWSTONE_DUST))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SPRUCE_WOOD, 3)
			.input('#', Blocks.SPRUCE_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.SPRUCE_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STRIPPED_SPRUCE_WOOD, 3)
			.input('#', Blocks.STRIPPED_SPRUCE_LOG)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.STRIPPED_SPRUCE_LOG))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.SPRUCE_BOAT)
			.input('#', Blocks.SPRUCE_PLANKS)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.WATER))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.SPRUCE_BUTTON)
			.input(Blocks.SPRUCE_PLANKS)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.SPRUCE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SPRUCE_DOOR, 3)
			.input('#', Blocks.SPRUCE_PLANKS)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.SPRUCE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SPRUCE_FENCE, 3)
			.input('#', Items.STICK)
			.input('W', Blocks.SPRUCE_PLANKS)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.SPRUCE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SPRUCE_FENCE_GATE)
			.input('#', Items.STICK)
			.input('W', Blocks.SPRUCE_PLANKS)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.SPRUCE_PLANKS))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.SPRUCE_PLANKS, 4)
			.input(ItemTags.SPRUCE_LOGS)
			.group("planks")
			.criterion("has_log", this.method_10420(ItemTags.SPRUCE_LOGS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SPRUCE_PRESSURE_PLATE)
			.input('#', Blocks.SPRUCE_PLANKS)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.SPRUCE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SPRUCE_SLAB, 6)
			.input('#', Blocks.SPRUCE_PLANKS)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.SPRUCE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SPRUCE_STAIRS, 4)
			.input('#', Blocks.SPRUCE_PLANKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.SPRUCE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SPRUCE_TRAPDOOR, 2)
			.input('#', Blocks.SPRUCE_PLANKS)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.SPRUCE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STICK, 4)
			.input('#', ItemTags.PLANKS)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_planks", this.method_10420(ItemTags.PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STICK, 1)
			.input('#', Blocks.BAMBOO)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_bamboo", this.method_10426(Blocks.BAMBOO))
			.offerTo(consumer, "stick_from_bamboo_item");
		ShapedRecipeJsonFactory.create(Blocks.STICKY_PISTON)
			.input('P', Blocks.PISTON)
			.input('S', Items.SLIME_BALL)
			.pattern("S")
			.pattern("P")
			.criterion("has_slime_ball", this.method_10426(Items.SLIME_BALL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STONE_BRICKS, 4)
			.input('#', Blocks.STONE)
			.pattern("##")
			.pattern("##")
			.criterion("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STONE_AXE)
			.input('#', Items.STICK)
			.input('X', Blocks.COBBLESTONE)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STONE_BRICK_SLAB, 6)
			.input('#', Blocks.STONE_BRICKS)
			.pattern("###")
			.criterion("has_stone_bricks", this.method_10420(ItemTags.STONE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STONE_BRICK_STAIRS, 4)
			.input('#', Blocks.STONE_BRICKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_stone_bricks", this.method_10420(ItemTags.STONE_BRICKS))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.STONE_BUTTON).input(Blocks.STONE).criterion("has_stone", this.method_10426(Blocks.STONE)).offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STONE_HOE)
			.input('#', Items.STICK)
			.input('X', Blocks.COBBLESTONE)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STONE_PICKAXE)
			.input('#', Items.STICK)
			.input('X', Blocks.COBBLESTONE)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STONE_PRESSURE_PLATE)
			.input('#', Blocks.STONE)
			.pattern("##")
			.criterion("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STONE_SHOVEL)
			.input('#', Items.STICK)
			.input('X', Blocks.COBBLESTONE)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STONE_SLAB, 6)
			.input('#', Blocks.STONE)
			.pattern("###")
			.criterion("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMOOTH_STONE_SLAB, 6)
			.input('#', Blocks.SMOOTH_STONE)
			.pattern("###")
			.criterion("has_smooth_stone", this.method_10426(Blocks.SMOOTH_STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.COBBLESTONE_STAIRS, 4)
			.input('#', Blocks.COBBLESTONE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.STONE_SWORD)
			.input('#', Items.STICK)
			.input('X', Blocks.COBBLESTONE)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.WHITE_WOOL)
			.input('#', Items.STRING)
			.pattern("##")
			.pattern("##")
			.criterion("has_string", this.method_10426(Items.STRING))
			.offerTo(consumer, "white_wool_from_string");
		ShapelessRecipeJsonFactory.create(Items.SUGAR)
			.input(Blocks.SUGAR_CANE)
			.group("sugar")
			.criterion("has_reeds", this.method_10426(Blocks.SUGAR_CANE))
			.offerTo(consumer, "sugar_from_sugar_cane");
		ShapelessRecipeJsonFactory.create(Items.SUGAR, 3)
			.input(Items.HONEY_BOTTLE)
			.group("sugar")
			.criterion("has_honey_bottle", this.method_10426(Items.HONEY_BOTTLE))
			.offerTo(consumer, "sugar_from_honey_bottle");
		ShapedRecipeJsonFactory.create(Blocks.TNT)
			.input('#', Ingredient.ofItems(Blocks.SAND, Blocks.RED_SAND))
			.input('X', Items.GUNPOWDER)
			.pattern("X#X")
			.pattern("#X#")
			.pattern("X#X")
			.criterion("has_gunpowder", this.method_10426(Items.GUNPOWDER))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.TNT_MINECART)
			.input('A', Blocks.TNT)
			.input('B', Items.MINECART)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", this.method_10426(Items.MINECART))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.TORCH, 4)
			.input('#', Items.STICK)
			.input('X', Ingredient.ofItems(Items.COAL, Items.CHARCOAL))
			.pattern("X")
			.pattern("#")
			.criterion("has_stone_pickaxe", this.method_10426(Items.STONE_PICKAXE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.LANTERN)
			.input('#', Items.TORCH)
			.input('X', Items.IRON_NUGGET)
			.pattern("XXX")
			.pattern("X#X")
			.pattern("XXX")
			.criterion("has_iron_nugget", this.method_10426(Items.IRON_NUGGET))
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.TRAPPED_CHEST)
			.input(Blocks.CHEST)
			.input(Blocks.TRIPWIRE_HOOK)
			.criterion("has_tripwire_hook", this.method_10426(Blocks.TRIPWIRE_HOOK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.TRIPWIRE_HOOK, 2)
			.input('#', ItemTags.PLANKS)
			.input('S', Items.STICK)
			.input('I', Items.IRON_INGOT)
			.pattern("I")
			.pattern("S")
			.pattern("#")
			.criterion("has_string", this.method_10426(Items.STRING))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.TURTLE_HELMET)
			.input('X', Items.SCUTE)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_scute", this.method_10426(Items.SCUTE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WHEAT, 9)
			.input(Blocks.HAY_BLOCK)
			.criterion("has_at_least_9_wheat", this.method_10424(NumberRange.IntRange.atLeast(9), Items.WHEAT))
			.criterion("has_hay_block", this.method_10426(Blocks.HAY_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WHITE_BANNER)
			.input('#', Blocks.WHITE_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WHITE_BED)
			.input('#', Blocks.WHITE_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.WHITE_CARPET, 3)
			.input('#', Blocks.WHITE_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.WHITE_CONCRETE_POWDER, 8)
			.input(Items.WHITE_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WHITE_DYE)
			.input(Items.BONE_MEAL)
			.group("white_dye")
			.criterion("has_bone_meal", this.method_10426(Items.BONE_MEAL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WHITE_DYE)
			.input(Blocks.LILY_OF_THE_VALLEY)
			.group("white_dye")
			.criterion("has_white_flower", this.method_10426(Blocks.LILY_OF_THE_VALLEY))
			.offerTo(consumer, "white_dye_from_lily_of_the_valley");
		ShapedRecipeJsonFactory.create(Blocks.WHITE_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.WHITE_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.WHITE_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.WHITE_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.WHITE_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.WHITE_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_white_dye", this.method_10426(Items.WHITE_DYE))
			.offerTo(consumer, "white_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.WHITE_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.WHITE_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WOODEN_AXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_stick", this.method_10426(Items.STICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.OAK_DOOR, 3)
			.input('#', Blocks.OAK_PLANKS)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WOODEN_HOE)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_stick", this.method_10426(Items.STICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WOODEN_PICKAXE)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_stick", this.method_10426(Items.STICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WOODEN_SHOVEL)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_stick", this.method_10426(Items.STICK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WOODEN_SWORD)
			.input('#', Items.STICK)
			.input('X', ItemTags.PLANKS)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_stick", this.method_10426(Items.STICK))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.WRITABLE_BOOK)
			.input(Items.BOOK)
			.input(Items.INK_SAC)
			.input(Items.FEATHER)
			.criterion("has_book", this.method_10426(Items.BOOK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.YELLOW_BANNER)
			.input('#', Blocks.YELLOW_WOOL)
			.input('|', Items.STICK)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_yellow_wool", this.method_10426(Blocks.YELLOW_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.YELLOW_BED)
			.input('#', Blocks.YELLOW_WOOL)
			.input('X', ItemTags.PLANKS)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_yellow_wool", this.method_10426(Blocks.YELLOW_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.YELLOW_BED)
			.input(Items.WHITE_BED)
			.input(Items.YELLOW_DYE)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "yellow_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.YELLOW_CARPET, 3)
			.input('#', Blocks.YELLOW_WOOL)
			.pattern("##")
			.group("carpet")
			.criterion("has_yellow_wool", this.method_10426(Blocks.YELLOW_WOOL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.YELLOW_CARPET, 8)
			.input('#', Blocks.WHITE_CARPET)
			.input('$', Items.YELLOW_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.WHITE_CARPET))
			.criterion("has_yellow_dye", this.method_10426(Items.YELLOW_DYE))
			.offerTo(consumer, "yellow_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.YELLOW_CONCRETE_POWDER, 8)
			.input(Items.YELLOW_DYE)
			.input(Blocks.SAND, 4)
			.input(Blocks.GRAVEL, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.SAND))
			.criterion("has_gravel", this.method_10426(Blocks.GRAVEL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.YELLOW_DYE)
			.input(Blocks.DANDELION)
			.group("yellow_dye")
			.criterion("has_yellow_flower", this.method_10426(Blocks.DANDELION))
			.offerTo(consumer, "yellow_dye_from_dandelion");
		ShapelessRecipeJsonFactory.create(Items.YELLOW_DYE, 2)
			.input(Blocks.SUNFLOWER)
			.group("yellow_dye")
			.criterion("has_double_plant", this.method_10426(Blocks.SUNFLOWER))
			.offerTo(consumer, "yellow_dye_from_sunflower");
		ShapedRecipeJsonFactory.create(Blocks.YELLOW_STAINED_GLASS, 8)
			.input('#', Blocks.GLASS)
			.input('X', Items.YELLOW_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.YELLOW_STAINED_GLASS_PANE, 16)
			.input('#', Blocks.YELLOW_STAINED_GLASS)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.GLASS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.YELLOW_STAINED_GLASS_PANE, 8)
			.input('#', Blocks.GLASS_PANE)
			.input('$', Items.YELLOW_DYE)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.GLASS_PANE))
			.criterion("has_yellow_dye", this.method_10426(Items.YELLOW_DYE))
			.offerTo(consumer, "yellow_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.YELLOW_TERRACOTTA, 8)
			.input('#', Blocks.TERRACOTTA)
			.input('X', Items.YELLOW_DYE)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.TERRACOTTA))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.YELLOW_WOOL)
			.input(Items.YELLOW_DYE)
			.input(Blocks.WHITE_WOOL)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.WHITE_WOOL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.DRIED_KELP, 9)
			.input(Blocks.DRIED_KELP_BLOCK)
			.criterion("has_at_least_9_dried_kelp", this.method_10424(NumberRange.IntRange.atLeast(9), Items.DRIED_KELP))
			.criterion("has_dried_kelp_block", this.method_10426(Blocks.DRIED_KELP_BLOCK))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.DRIED_KELP_BLOCK)
			.input(Items.DRIED_KELP, 9)
			.criterion("has_at_least_9_dried_kelp", this.method_10424(NumberRange.IntRange.atLeast(9), Items.DRIED_KELP))
			.criterion("has_dried_kelp_block", this.method_10426(Blocks.DRIED_KELP_BLOCK))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CONDUIT)
			.input('#', Items.NAUTILUS_SHELL)
			.input('X', Items.HEART_OF_THE_SEA)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_nautilus_core", this.method_10426(Items.HEART_OF_THE_SEA))
			.criterion("has_nautilus_shell", this.method_10426(Items.NAUTILUS_SHELL))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POLISHED_GRANITE_STAIRS, 4)
			.input('#', Blocks.POLISHED_GRANITE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_granite", this.method_10426(Blocks.POLISHED_GRANITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, 4)
			.input('#', Blocks.SMOOTH_RED_SANDSTONE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_smooth_red_sandstone", this.method_10426(Blocks.SMOOTH_RED_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MOSSY_STONE_BRICK_STAIRS, 4)
			.input('#', Blocks.MOSSY_STONE_BRICKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_mossy_stone_bricks", this.method_10426(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POLISHED_DIORITE_STAIRS, 4)
			.input('#', Blocks.POLISHED_DIORITE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_diorite", this.method_10426(Blocks.POLISHED_DIORITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MOSSY_COBBLESTONE_STAIRS, 4)
			.input('#', Blocks.MOSSY_COBBLESTONE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_mossy_cobblestone", this.method_10426(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.END_STONE_BRICK_STAIRS, 4)
			.input('#', Blocks.END_STONE_BRICKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_end_stone_bricks", this.method_10426(Blocks.END_STONE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STONE_STAIRS, 4)
			.input('#', Blocks.STONE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMOOTH_SANDSTONE_STAIRS, 4)
			.input('#', Blocks.SMOOTH_SANDSTONE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_smooth_sandstone", this.method_10426(Blocks.SMOOTH_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMOOTH_QUARTZ_STAIRS, 4)
			.input('#', Blocks.SMOOTH_QUARTZ)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_smooth_quartz", this.method_10426(Blocks.SMOOTH_QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GRANITE_STAIRS, 4)
			.input('#', Blocks.GRANITE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_granite", this.method_10426(Blocks.GRANITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ANDESITE_STAIRS, 4)
			.input('#', Blocks.ANDESITE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_andesite", this.method_10426(Blocks.ANDESITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_NETHER_BRICK_STAIRS, 4)
			.input('#', Blocks.RED_NETHER_BRICKS)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_red_nether_bricks", this.method_10426(Blocks.RED_NETHER_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POLISHED_ANDESITE_STAIRS, 4)
			.input('#', Blocks.POLISHED_ANDESITE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_andesite", this.method_10426(Blocks.POLISHED_ANDESITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DIORITE_STAIRS, 4)
			.input('#', Blocks.DIORITE)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_diorite", this.method_10426(Blocks.DIORITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POLISHED_GRANITE_SLAB, 6)
			.input('#', Blocks.POLISHED_GRANITE)
			.pattern("###")
			.criterion("has_polished_granite", this.method_10426(Blocks.POLISHED_GRANITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMOOTH_RED_SANDSTONE_SLAB, 6)
			.input('#', Blocks.SMOOTH_RED_SANDSTONE)
			.pattern("###")
			.criterion("has_smooth_red_sandstone", this.method_10426(Blocks.SMOOTH_RED_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MOSSY_STONE_BRICK_SLAB, 6)
			.input('#', Blocks.MOSSY_STONE_BRICKS)
			.pattern("###")
			.criterion("has_mossy_stone_bricks", this.method_10426(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POLISHED_DIORITE_SLAB, 6)
			.input('#', Blocks.POLISHED_DIORITE)
			.pattern("###")
			.criterion("has_polished_diorite", this.method_10426(Blocks.POLISHED_DIORITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MOSSY_COBBLESTONE_SLAB, 6)
			.input('#', Blocks.MOSSY_COBBLESTONE)
			.pattern("###")
			.criterion("has_mossy_cobblestone", this.method_10426(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.END_STONE_BRICK_SLAB, 6)
			.input('#', Blocks.END_STONE_BRICKS)
			.pattern("###")
			.criterion("has_end_stone_bricks", this.method_10426(Blocks.END_STONE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMOOTH_SANDSTONE_SLAB, 6)
			.input('#', Blocks.SMOOTH_SANDSTONE)
			.pattern("###")
			.criterion("has_smooth_sandstone", this.method_10426(Blocks.SMOOTH_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMOOTH_QUARTZ_SLAB, 6)
			.input('#', Blocks.SMOOTH_QUARTZ)
			.pattern("###")
			.criterion("has_smooth_quartz", this.method_10426(Blocks.SMOOTH_QUARTZ))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GRANITE_SLAB, 6)
			.input('#', Blocks.GRANITE)
			.pattern("###")
			.criterion("has_granite", this.method_10426(Blocks.GRANITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ANDESITE_SLAB, 6)
			.input('#', Blocks.ANDESITE)
			.pattern("###")
			.criterion("has_andesite", this.method_10426(Blocks.ANDESITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_NETHER_BRICK_SLAB, 6)
			.input('#', Blocks.RED_NETHER_BRICKS)
			.pattern("###")
			.criterion("has_red_nether_bricks", this.method_10426(Blocks.RED_NETHER_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.POLISHED_ANDESITE_SLAB, 6)
			.input('#', Blocks.POLISHED_ANDESITE)
			.pattern("###")
			.criterion("has_polished_andesite", this.method_10426(Blocks.POLISHED_ANDESITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DIORITE_SLAB, 6)
			.input('#', Blocks.DIORITE)
			.pattern("###")
			.criterion("has_diorite", this.method_10426(Blocks.DIORITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BRICK_WALL, 6)
			.input('#', Blocks.BRICKS)
			.pattern("###")
			.pattern("###")
			.criterion("has_bricks", this.method_10426(Blocks.BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.PRISMARINE_WALL, 6)
			.input('#', Blocks.PRISMARINE)
			.pattern("###")
			.pattern("###")
			.criterion("has_prismarine", this.method_10426(Blocks.PRISMARINE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_SANDSTONE_WALL, 6)
			.input('#', Blocks.RED_SANDSTONE)
			.pattern("###")
			.pattern("###")
			.criterion("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.MOSSY_STONE_BRICK_WALL, 6)
			.input('#', Blocks.MOSSY_STONE_BRICKS)
			.pattern("###")
			.pattern("###")
			.criterion("has_mossy_stone_bricks", this.method_10426(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GRANITE_WALL, 6)
			.input('#', Blocks.GRANITE)
			.pattern("###")
			.pattern("###")
			.criterion("has_granite", this.method_10426(Blocks.GRANITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STONE_BRICK_WALL, 6)
			.input('#', Blocks.STONE_BRICKS)
			.pattern("###")
			.pattern("###")
			.criterion("has_stone_bricks", this.method_10426(Blocks.STONE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.NETHER_BRICK_WALL, 6)
			.input('#', Blocks.NETHER_BRICKS)
			.pattern("###")
			.pattern("###")
			.criterion("has_nether_bricks", this.method_10426(Blocks.NETHER_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.ANDESITE_WALL, 6)
			.input('#', Blocks.ANDESITE)
			.pattern("###")
			.pattern("###")
			.criterion("has_andesite", this.method_10426(Blocks.ANDESITE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.RED_NETHER_BRICK_WALL, 6)
			.input('#', Blocks.RED_NETHER_BRICKS)
			.pattern("###")
			.pattern("###")
			.criterion("has_red_nether_bricks", this.method_10426(Blocks.RED_NETHER_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SANDSTONE_WALL, 6)
			.input('#', Blocks.SANDSTONE)
			.pattern("###")
			.pattern("###")
			.criterion("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.END_STONE_BRICK_WALL, 6)
			.input('#', Blocks.END_STONE_BRICKS)
			.pattern("###")
			.pattern("###")
			.criterion("has_end_stone_bricks", this.method_10426(Blocks.END_STONE_BRICKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.DIORITE_WALL, 6)
			.input('#', Blocks.DIORITE)
			.pattern("###")
			.pattern("###")
			.criterion("has_diorite", this.method_10426(Blocks.DIORITE))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.CREEPER_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.CREEPER_HEAD)
			.criterion("has_creeper_head", this.method_10426(Items.CREEPER_HEAD))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.SKULL_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.WITHER_SKELETON_SKULL)
			.criterion("has_wither_skeleton_skull", this.method_10426(Items.WITHER_SKELETON_SKULL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.FLOWER_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Blocks.OXEYE_DAISY)
			.criterion("has_oxeye_daisy", this.method_10426(Blocks.OXEYE_DAISY))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MOJANG_BANNER_PATTERN)
			.input(Items.PAPER)
			.input(Items.ENCHANTED_GOLDEN_APPLE)
			.criterion("has_enchanted_golden_apple", this.method_10426(Items.ENCHANTED_GOLDEN_APPLE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SCAFFOLDING, 6)
			.input('~', Items.STRING)
			.input('I', Blocks.BAMBOO)
			.pattern("I~I")
			.pattern("I I")
			.pattern("I I")
			.criterion("has_bamboo", this.method_10426(Blocks.BAMBOO))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.GRINDSTONE)
			.input('I', Items.STICK)
			.input('-', Blocks.STONE_SLAB)
			.input('#', ItemTags.PLANKS)
			.pattern("I-I")
			.pattern("# #")
			.criterion("has_stone_slab", this.method_10426(Blocks.STONE_SLAB))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.BLAST_FURNACE)
			.input('#', Blocks.SMOOTH_STONE)
			.input('X', Blocks.FURNACE)
			.input('I', Items.IRON_INGOT)
			.pattern("III")
			.pattern("IXI")
			.pattern("###")
			.criterion("has_smooth_stone", this.method_10426(Blocks.SMOOTH_STONE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMOKER)
			.input('#', ItemTags.LOGS)
			.input('X', Blocks.FURNACE)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_furnace", this.method_10426(Blocks.FURNACE))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.CARTOGRAPHY_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.PAPER)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_string", this.method_10426(Items.STRING))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.SMITHING_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.IRON_INGOT)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", this.method_10426(Items.IRON_INGOT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.FLETCHING_TABLE)
			.input('#', ItemTags.PLANKS)
			.input('@', Items.FLINT)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_flint", this.method_10426(Items.FLINT))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.STONECUTTER)
			.input('I', Items.IRON_INGOT)
			.input('#', Blocks.STONE)
			.pattern(" I ")
			.pattern("###")
			.criterion("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer);
		ComplexRecipeJsonFactory.create(RecipeSerializer.ARMOR_DYE).offerTo(consumer, "armor_dye");
		ComplexRecipeJsonFactory.create(RecipeSerializer.BANNER_DUPLICATE).offerTo(consumer, "banner_duplicate");
		ComplexRecipeJsonFactory.create(RecipeSerializer.BOOK_CLONING).offerTo(consumer, "book_cloning");
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
			.criterion("has_potato", this.method_10426(Items.POTATO))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.CLAY_BALL), Items.BRICK, 0.3F, 200)
			.criterion("has_clay_ball", this.method_10426(Items.CLAY_BALL))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.LOGS), Items.CHARCOAL, 0.15F, 200)
			.criterion("has_log", this.method_10420(ItemTags.LOGS))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.CHORUS_FRUIT), Items.POPPED_CHORUS_FRUIT, 0.1F, 200)
			.criterion("has_chorus_fruit", this.method_10426(Items.CHORUS_FRUIT))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1F, 200)
			.criterion("has_coal_ore", this.method_10426(Blocks.COAL_ORE))
			.offerTo(consumer, "coal_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.BEEF), Items.COOKED_BEEF, 0.35F, 200)
			.criterion("has_beef", this.method_10426(Items.BEEF))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, 200)
			.criterion("has_chicken", this.method_10426(Items.CHICKEN))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.COD), Items.COOKED_COD, 0.35F, 200)
			.criterion("has_cod", this.method_10426(Items.COD))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.KELP), Items.DRIED_KELP, 0.1F, 200)
			.criterion("has_kelp", this.method_10426(Blocks.KELP))
			.offerTo(consumer, "dried_kelp_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.SALMON), Items.COOKED_SALMON, 0.35F, 200)
			.criterion("has_salmon", this.method_10426(Items.SALMON))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, 200)
			.criterion("has_mutton", this.method_10426(Items.MUTTON))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, 200)
			.criterion("has_porkchop", this.method_10426(Items.PORKCHOP))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, 200)
			.criterion("has_rabbit", this.method_10426(Items.RABBIT))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0F, 200)
			.criterion("has_diamond_ore", this.method_10426(Blocks.DIAMOND_ORE))
			.offerTo(consumer, "diamond_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2F, 200)
			.criterion("has_lapis_ore", this.method_10426(Blocks.LAPIS_ORE))
			.offerTo(consumer, "lapis_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0F, 200)
			.criterion("has_emerald_ore", this.method_10426(Blocks.EMERALD_ORE))
			.offerTo(consumer, "emerald_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.SAND), Blocks.GLASS.asItem(), 0.1F, 200)
			.criterion("has_sand", this.method_10420(ItemTags.SAND))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.GOLD_ORE.asItem()), Items.GOLD_INGOT, 1.0F, 200)
			.criterion("has_gold_ore", this.method_10426(Blocks.GOLD_ORE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.SEA_PICKLE.asItem()), Items.LIME_DYE, 0.1F, 200)
			.criterion("has_sea_pickle", this.method_10426(Blocks.SEA_PICKLE))
			.offerTo(consumer, "lime_dye_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.CACTUS.asItem()), Items.GREEN_DYE, 1.0F, 200)
			.criterion("has_cactus", this.method_10426(Blocks.CACTUS))
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
			.criterion("has_golden_pickaxe", this.method_10426(Items.GOLDEN_PICKAXE))
			.criterion("has_golden_shovel", this.method_10426(Items.GOLDEN_SHOVEL))
			.criterion("has_golden_axe", this.method_10426(Items.GOLDEN_AXE))
			.criterion("has_golden_hoe", this.method_10426(Items.GOLDEN_HOE))
			.criterion("has_golden_sword", this.method_10426(Items.GOLDEN_SWORD))
			.criterion("has_golden_helmet", this.method_10426(Items.GOLDEN_HELMET))
			.criterion("has_golden_chestplate", this.method_10426(Items.GOLDEN_CHESTPLATE))
			.criterion("has_golden_leggings", this.method_10426(Items.GOLDEN_LEGGINGS))
			.criterion("has_golden_boots", this.method_10426(Items.GOLDEN_BOOTS))
			.criterion("has_golden_horse_armor", this.method_10426(Items.GOLDEN_HORSE_ARMOR))
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
			.criterion("has_iron_pickaxe", this.method_10426(Items.IRON_PICKAXE))
			.criterion("has_iron_shovel", this.method_10426(Items.IRON_SHOVEL))
			.criterion("has_iron_axe", this.method_10426(Items.IRON_AXE))
			.criterion("has_iron_hoe", this.method_10426(Items.IRON_HOE))
			.criterion("has_iron_sword", this.method_10426(Items.IRON_SWORD))
			.criterion("has_iron_helmet", this.method_10426(Items.IRON_HELMET))
			.criterion("has_iron_chestplate", this.method_10426(Items.IRON_CHESTPLATE))
			.criterion("has_iron_leggings", this.method_10426(Items.IRON_LEGGINGS))
			.criterion("has_iron_boots", this.method_10426(Items.IRON_BOOTS))
			.criterion("has_iron_horse_armor", this.method_10426(Items.IRON_HORSE_ARMOR))
			.criterion("has_chainmail_helmet", this.method_10426(Items.CHAINMAIL_HELMET))
			.criterion("has_chainmail_chestplate", this.method_10426(Items.CHAINMAIL_CHESTPLATE))
			.criterion("has_chainmail_leggings", this.method_10426(Items.CHAINMAIL_LEGGINGS))
			.criterion("has_chainmail_boots", this.method_10426(Items.CHAINMAIL_BOOTS))
			.offerTo(consumer, "iron_nugget_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7F, 200)
			.criterion("has_iron_ore", this.method_10426(Blocks.IRON_ORE.asItem()))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.CLAY), Blocks.TERRACOTTA.asItem(), 0.35F, 200)
			.criterion("has_clay_block", this.method_10426(Blocks.CLAY))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.NETHERRACK), Items.NETHER_BRICK, 0.1F, 200)
			.criterion("has_netherrack", this.method_10426(Blocks.NETHERRACK))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 200)
			.criterion("has_nether_quartz_ore", this.method_10426(Blocks.NETHER_QUARTZ_ORE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7F, 200)
			.criterion("has_redstone_ore", this.method_10426(Blocks.REDSTONE_ORE))
			.offerTo(consumer, "redstone_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.WET_SPONGE), Blocks.SPONGE.asItem(), 0.15F, 200)
			.criterion("has_wet_sponge", this.method_10426(Blocks.WET_SPONGE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.STONE.asItem(), 0.1F, 200)
			.criterion("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.STONE), Blocks.SMOOTH_STONE.asItem(), 0.1F, 200)
			.criterion("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SMOOTH_SANDSTONE.asItem(), 0.1F, 200)
			.criterion("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE.asItem(), 0.1F, 200)
			.criterion("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.SMOOTH_QUARTZ.asItem(), 0.1F, 200)
			.criterion("has_quartz_block", this.method_10426(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.CRACKED_STONE_BRICKS.asItem(), 0.1F, 200)
			.criterion("has_stone_bricks", this.method_10426(Blocks.STONE_BRICKS))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.BLACK_TERRACOTTA), Blocks.BLACK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_black_terracotta", this.method_10426(Blocks.BLACK_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.BLUE_TERRACOTTA), Blocks.BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_blue_terracotta", this.method_10426(Blocks.BLUE_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.BROWN_TERRACOTTA), Blocks.BROWN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_brown_terracotta", this.method_10426(Blocks.BROWN_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.CYAN_TERRACOTTA), Blocks.CYAN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_cyan_terracotta", this.method_10426(Blocks.CYAN_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.GRAY_TERRACOTTA), Blocks.GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_gray_terracotta", this.method_10426(Blocks.GRAY_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.GREEN_TERRACOTTA), Blocks.GREEN_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_green_terracotta", this.method_10426(Blocks.GREEN_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LIGHT_BLUE_TERRACOTTA), Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_light_blue_terracotta", this.method_10426(Blocks.LIGHT_BLUE_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LIGHT_GRAY_TERRACOTTA), Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_light_gray_terracotta", this.method_10426(Blocks.LIGHT_GRAY_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.LIME_TERRACOTTA), Blocks.LIME_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_lime_terracotta", this.method_10426(Blocks.LIME_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.MAGENTA_TERRACOTTA), Blocks.MAGENTA_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_magenta_terracotta", this.method_10426(Blocks.MAGENTA_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.ORANGE_TERRACOTTA), Blocks.ORANGE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_orange_terracotta", this.method_10426(Blocks.ORANGE_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.PINK_TERRACOTTA), Blocks.PINK_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_pink_terracotta", this.method_10426(Blocks.PINK_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.PURPLE_TERRACOTTA), Blocks.PURPLE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_purple_terracotta", this.method_10426(Blocks.PURPLE_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.RED_TERRACOTTA), Blocks.RED_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_red_terracotta", this.method_10426(Blocks.RED_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.WHITE_TERRACOTTA), Blocks.WHITE_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_white_terracotta", this.method_10426(Blocks.WHITE_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.YELLOW_TERRACOTTA), Blocks.YELLOW_GLAZED_TERRACOTTA.asItem(), 0.1F, 200)
			.criterion("has_yellow_terracotta", this.method_10426(Blocks.YELLOW_TERRACOTTA))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.IRON_ORE.asItem()), Items.IRON_INGOT, 0.7F, 100)
			.criterion("has_iron_ore", this.method_10426(Blocks.IRON_ORE.asItem()))
			.offerTo(consumer, "iron_ingot_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.GOLD_ORE.asItem()), Items.GOLD_INGOT, 1.0F, 100)
			.criterion("has_gold_ore", this.method_10426(Blocks.GOLD_ORE))
			.offerTo(consumer, "gold_ingot_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.DIAMOND_ORE.asItem()), Items.DIAMOND, 1.0F, 100)
			.criterion("has_diamond_ore", this.method_10426(Blocks.DIAMOND_ORE))
			.offerTo(consumer, "diamond_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.LAPIS_ORE.asItem()), Items.LAPIS_LAZULI, 0.2F, 100)
			.criterion("has_lapis_ore", this.method_10426(Blocks.LAPIS_ORE))
			.offerTo(consumer, "lapis_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.REDSTONE_ORE), Items.REDSTONE, 0.7F, 100)
			.criterion("has_redstone_ore", this.method_10426(Blocks.REDSTONE_ORE))
			.offerTo(consumer, "redstone_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.COAL_ORE.asItem()), Items.COAL, 0.1F, 100)
			.criterion("has_coal_ore", this.method_10426(Blocks.COAL_ORE))
			.offerTo(consumer, "coal_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.EMERALD_ORE.asItem()), Items.EMERALD, 1.0F, 100)
			.criterion("has_emerald_ore", this.method_10426(Blocks.EMERALD_ORE))
			.offerTo(consumer, "emerald_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.NETHER_QUARTZ_ORE), Items.QUARTZ, 0.2F, 100)
			.criterion("has_nether_quartz_ore", this.method_10426(Blocks.NETHER_QUARTZ_ORE))
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
			.criterion("has_golden_pickaxe", this.method_10426(Items.GOLDEN_PICKAXE))
			.criterion("has_golden_shovel", this.method_10426(Items.GOLDEN_SHOVEL))
			.criterion("has_golden_axe", this.method_10426(Items.GOLDEN_AXE))
			.criterion("has_golden_hoe", this.method_10426(Items.GOLDEN_HOE))
			.criterion("has_golden_sword", this.method_10426(Items.GOLDEN_SWORD))
			.criterion("has_golden_helmet", this.method_10426(Items.GOLDEN_HELMET))
			.criterion("has_golden_chestplate", this.method_10426(Items.GOLDEN_CHESTPLATE))
			.criterion("has_golden_leggings", this.method_10426(Items.GOLDEN_LEGGINGS))
			.criterion("has_golden_boots", this.method_10426(Items.GOLDEN_BOOTS))
			.criterion("has_golden_horse_armor", this.method_10426(Items.GOLDEN_HORSE_ARMOR))
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
			.criterion("has_iron_pickaxe", this.method_10426(Items.IRON_PICKAXE))
			.criterion("has_iron_shovel", this.method_10426(Items.IRON_SHOVEL))
			.criterion("has_iron_axe", this.method_10426(Items.IRON_AXE))
			.criterion("has_iron_hoe", this.method_10426(Items.IRON_HOE))
			.criterion("has_iron_sword", this.method_10426(Items.IRON_SWORD))
			.criterion("has_iron_helmet", this.method_10426(Items.IRON_HELMET))
			.criterion("has_iron_chestplate", this.method_10426(Items.IRON_CHESTPLATE))
			.criterion("has_iron_leggings", this.method_10426(Items.IRON_LEGGINGS))
			.criterion("has_iron_boots", this.method_10426(Items.IRON_BOOTS))
			.criterion("has_iron_horse_armor", this.method_10426(Items.IRON_HORSE_ARMOR))
			.criterion("has_chainmail_helmet", this.method_10426(Items.CHAINMAIL_HELMET))
			.criterion("has_chainmail_chestplate", this.method_10426(Items.CHAINMAIL_CHESTPLATE))
			.criterion("has_chainmail_leggings", this.method_10426(Items.CHAINMAIL_LEGGINGS))
			.criterion("has_chainmail_boots", this.method_10426(Items.CHAINMAIL_BOOTS))
			.offerTo(consumer, "iron_nugget_from_blasting");
		this.method_17585(consumer, "smoking", RecipeSerializer.SMOKING, 100);
		this.method_17585(consumer, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, 600);
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_SLAB, 2)
			.create("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer, "stone_slab_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_STAIRS)
			.create("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer, "stone_stairs_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICKS)
			.create("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer, "stone_bricks_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICK_SLAB, 2)
			.create("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer, "stone_brick_slab_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICK_STAIRS)
			.create("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer, "stone_brick_stairs_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.CHISELED_STONE_BRICKS)
			.create("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer, "chiseled_stone_bricks_stone_from_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE), Blocks.STONE_BRICK_WALL)
			.create("has_stone", this.method_10426(Blocks.STONE))
			.offerTo(consumer, "stone_brick_walls_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE)
			.create("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.offerTo(consumer, "cut_sandstone_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SANDSTONE_SLAB, 2)
			.create("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.offerTo(consumer, "sandstone_slab_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2)
			.create("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.offerTo(consumer, "cut_sandstone_slab_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.CUT_SANDSTONE), Blocks.CUT_SANDSTONE_SLAB, 2)
			.create("has_cut_sandstone", this.method_10426(Blocks.SANDSTONE))
			.offerTo(consumer, "cut_sandstone_slab_from_cut_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SANDSTONE_STAIRS)
			.create("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.offerTo(consumer, "sandstone_stairs_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.SANDSTONE_WALL)
			.create("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.offerTo(consumer, "sandstone_wall_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SANDSTONE), Blocks.CHISELED_SANDSTONE)
			.create("has_sandstone", this.method_10426(Blocks.SANDSTONE))
			.offerTo(consumer, "chiseled_sandstone_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE)
			.create("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "cut_red_sandstone_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_SLAB, 2)
			.create("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "red_sandstone_slab_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2)
			.create("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "cut_red_sandstone_slab_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.CUT_RED_SANDSTONE), Blocks.CUT_RED_SANDSTONE_SLAB, 2)
			.create("has_cut_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "cut_red_sandstone_slab_from_cut_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_STAIRS)
			.create("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "red_sandstone_stairs_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE_WALL)
			.create("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "red_sandstone_wall_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_SANDSTONE), Blocks.CHISELED_RED_SANDSTONE)
			.create("has_red_sandstone", this.method_10426(Blocks.RED_SANDSTONE))
			.offerTo(consumer, "chiseled_red_sandstone_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_SLAB, 2)
			.create("has_quartz_block", this.method_10426(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer, "quartz_slab_from_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_STAIRS)
			.create("has_quartz_block", this.method_10426(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer, "quartz_stairs_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_PILLAR)
			.create("has_quartz_block", this.method_10426(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer, "quartz_pillar_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK), Blocks.CHISELED_QUARTZ_BLOCK)
			.create("has_quartz_block", this.method_10426(Blocks.QUARTZ_BLOCK))
			.offerTo(consumer, "chiseled_quartz_block_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_STAIRS)
			.create("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer, "cobblestone_stairs_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_SLAB, 2)
			.create("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer, "cobblestone_slab_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.COBBLESTONE), Blocks.COBBLESTONE_WALL)
			.create("has_cobblestone", this.method_10426(Blocks.COBBLESTONE))
			.offerTo(consumer, "cobblestone_wall_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_SLAB, 2)
			.create("has_stone_bricks", this.method_10426(Blocks.STONE_BRICKS))
			.offerTo(consumer, "stone_brick_slab_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_STAIRS)
			.create("has_stone_bricks", this.method_10426(Blocks.STONE_BRICKS))
			.offerTo(consumer, "stone_brick_stairs_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.STONE_BRICK_WALL)
			.create("has_stone_bricks", this.method_10426(Blocks.STONE_BRICKS))
			.offerTo(consumer, "stone_brick_wall_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.STONE_BRICKS), Blocks.CHISELED_STONE_BRICKS)
			.create("has_stone_bricks", this.method_10426(Blocks.STONE_BRICKS))
			.offerTo(consumer, "chiseled_stone_bricks_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BRICKS), Blocks.BRICK_SLAB, 2)
			.create("has_bricks", this.method_10426(Blocks.BRICKS))
			.offerTo(consumer, "brick_slab_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BRICKS), Blocks.BRICK_STAIRS)
			.create("has_bricks", this.method_10426(Blocks.BRICKS))
			.offerTo(consumer, "brick_stairs_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.BRICKS), Blocks.BRICK_WALL)
			.create("has_bricks", this.method_10426(Blocks.BRICKS))
			.offerTo(consumer, "brick_wall_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_SLAB, 2)
			.create("has_nether_bricks", this.method_10426(Blocks.NETHER_BRICKS))
			.offerTo(consumer, "nether_brick_slab_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_STAIRS)
			.create("has_nether_bricks", this.method_10426(Blocks.NETHER_BRICKS))
			.offerTo(consumer, "nether_brick_stairs_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.NETHER_BRICKS), Blocks.NETHER_BRICK_WALL)
			.create("has_nether_bricks", this.method_10426(Blocks.NETHER_BRICKS))
			.offerTo(consumer, "nether_brick_wall_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_SLAB, 2)
			.create("has_nether_bricks", this.method_10426(Blocks.RED_NETHER_BRICKS))
			.offerTo(consumer, "red_nether_brick_slab_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_STAIRS)
			.create("has_nether_bricks", this.method_10426(Blocks.RED_NETHER_BRICKS))
			.offerTo(consumer, "red_nether_brick_stairs_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.RED_NETHER_BRICKS), Blocks.RED_NETHER_BRICK_WALL)
			.create("has_nether_bricks", this.method_10426(Blocks.RED_NETHER_BRICKS))
			.offerTo(consumer, "red_nether_brick_wall_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_SLAB, 2)
			.create("has_purpur_block", this.method_10426(Blocks.PURPUR_BLOCK))
			.offerTo(consumer, "purpur_slab_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_STAIRS)
			.create("has_purpur_block", this.method_10426(Blocks.PURPUR_BLOCK))
			.offerTo(consumer, "purpur_stairs_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PURPUR_BLOCK), Blocks.PURPUR_PILLAR)
			.create("has_purpur_block", this.method_10426(Blocks.PURPUR_BLOCK))
			.offerTo(consumer, "purpur_pillar_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE), Blocks.PRISMARINE_SLAB, 2)
			.create("has_prismarine", this.method_10426(Blocks.PRISMARINE))
			.offerTo(consumer, "prismarine_slab_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE), Blocks.PRISMARINE_STAIRS)
			.create("has_prismarine", this.method_10426(Blocks.PRISMARINE))
			.offerTo(consumer, "prismarine_stairs_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE), Blocks.PRISMARINE_WALL)
			.create("has_prismarine", this.method_10426(Blocks.PRISMARINE))
			.offerTo(consumer, "prismarine_wall_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_SLAB, 2)
			.create("has_prismarine_brick", this.method_10426(Blocks.PRISMARINE_BRICKS))
			.offerTo(consumer, "prismarine_brick_slab_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS), Blocks.PRISMARINE_BRICK_STAIRS)
			.create("has_prismarine_brick", this.method_10426(Blocks.PRISMARINE_BRICKS))
			.offerTo(consumer, "prismarine_brick_stairs_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_SLAB, 2)
			.create("has_dark_prismarine", this.method_10426(Blocks.DARK_PRISMARINE))
			.offerTo(consumer, "dark_prismarine_slab_from_dark_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DARK_PRISMARINE), Blocks.DARK_PRISMARINE_STAIRS)
			.create("has_dark_prismarine", this.method_10426(Blocks.DARK_PRISMARINE))
			.offerTo(consumer, "dark_prismarine_stairs_from_dark_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.ANDESITE_SLAB, 2)
			.create("has_andesite", this.method_10426(Blocks.ANDESITE))
			.offerTo(consumer, "andesite_slab_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.ANDESITE_STAIRS)
			.create("has_andesite", this.method_10426(Blocks.ANDESITE))
			.offerTo(consumer, "andesite_stairs_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.ANDESITE_WALL)
			.create("has_andesite", this.method_10426(Blocks.ANDESITE))
			.offerTo(consumer, "andesite_wall_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE)
			.create("has_andesite", this.method_10426(Blocks.ANDESITE))
			.offerTo(consumer, "polished_andesite_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2)
			.create("has_andesite", this.method_10426(Blocks.ANDESITE))
			.offerTo(consumer, "polished_andesite_slab_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS)
			.create("has_andesite", this.method_10426(Blocks.ANDESITE))
			.offerTo(consumer, "polished_andesite_stairs_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_SLAB, 2)
			.create("has_polished_andesite", this.method_10426(Blocks.POLISHED_ANDESITE))
			.offerTo(consumer, "polished_andesite_slab_from_polished_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_ANDESITE), Blocks.POLISHED_ANDESITE_STAIRS)
			.create("has_polished_andesite", this.method_10426(Blocks.POLISHED_ANDESITE))
			.offerTo(consumer, "polished_andesite_stairs_from_polished_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.GRANITE_SLAB, 2)
			.create("has_granite", this.method_10426(Blocks.GRANITE))
			.offerTo(consumer, "granite_slab_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.GRANITE_STAIRS)
			.create("has_granite", this.method_10426(Blocks.GRANITE))
			.offerTo(consumer, "granite_stairs_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.GRANITE_WALL)
			.create("has_granite", this.method_10426(Blocks.GRANITE))
			.offerTo(consumer, "granite_wall_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE)
			.create("has_granite", this.method_10426(Blocks.GRANITE))
			.offerTo(consumer, "polished_granite_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2)
			.create("has_granite", this.method_10426(Blocks.GRANITE))
			.offerTo(consumer, "polished_granite_slab_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.GRANITE), Blocks.POLISHED_GRANITE_STAIRS)
			.create("has_granite", this.method_10426(Blocks.GRANITE))
			.offerTo(consumer, "polished_granite_stairs_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_SLAB, 2)
			.create("has_polished_granite", this.method_10426(Blocks.POLISHED_GRANITE))
			.offerTo(consumer, "polished_granite_slab_from_polished_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_GRANITE), Blocks.POLISHED_GRANITE_STAIRS)
			.create("has_polished_granite", this.method_10426(Blocks.POLISHED_GRANITE))
			.offerTo(consumer, "polished_granite_stairs_from_polished_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.DIORITE_SLAB, 2)
			.create("has_diorite", this.method_10426(Blocks.DIORITE))
			.offerTo(consumer, "diorite_slab_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.DIORITE_STAIRS)
			.create("has_diorite", this.method_10426(Blocks.DIORITE))
			.offerTo(consumer, "diorite_stairs_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.DIORITE_WALL)
			.create("has_diorite", this.method_10426(Blocks.DIORITE))
			.offerTo(consumer, "diorite_wall_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE)
			.create("has_diorite", this.method_10426(Blocks.DIORITE))
			.offerTo(consumer, "polished_diorite_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2)
			.create("has_diorite", this.method_10426(Blocks.POLISHED_DIORITE))
			.offerTo(consumer, "polished_diorite_slab_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.DIORITE), Blocks.POLISHED_DIORITE_STAIRS)
			.create("has_diorite", this.method_10426(Blocks.POLISHED_DIORITE))
			.offerTo(consumer, "polished_diorite_stairs_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_SLAB, 2)
			.create("has_polished_diorite", this.method_10426(Blocks.POLISHED_DIORITE))
			.offerTo(consumer, "polished_diorite_slab_from_polished_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_DIORITE), Blocks.POLISHED_DIORITE_STAIRS)
			.create("has_polished_diorite", this.method_10426(Blocks.POLISHED_DIORITE))
			.offerTo(consumer, "polished_diorite_stairs_from_polished_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_SLAB, 2)
			.create("has_mossy_stone_bricks", this.method_10426(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(consumer, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_STAIRS)
			.create("has_mossy_stone_bricks", this.method_10426(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(consumer, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS), Blocks.MOSSY_STONE_BRICK_WALL)
			.create("has_mossy_stone_bricks", this.method_10426(Blocks.MOSSY_STONE_BRICKS))
			.offerTo(consumer, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_SLAB, 2)
			.create("has_mossy_cobblestone", this.method_10426(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer, "mossy_cobblestone_slab_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_STAIRS)
			.create("has_mossy_cobblestone", this.method_10426(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer, "mossy_cobblestone_stairs_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE_WALL)
			.create("has_mossy_cobblestone", this.method_10426(Blocks.MOSSY_COBBLESTONE))
			.offerTo(consumer, "mossy_cobblestone_wall_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_SLAB, 2)
			.create("has_smooth_sandstone", this.method_10426(Blocks.SMOOTH_SANDSTONE))
			.offerTo(consumer, "smooth_sandstone_slab_from_smooth_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_SANDSTONE), Blocks.SMOOTH_SANDSTONE_STAIRS)
			.create("has_mossy_cobblestone", this.method_10426(Blocks.SMOOTH_SANDSTONE))
			.offerTo(consumer, "smooth_sandstone_stairs_from_smooth_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_SLAB, 2)
			.create("has_smooth_red_sandstone", this.method_10426(Blocks.SMOOTH_RED_SANDSTONE))
			.offerTo(consumer, "smooth_red_sandstone_slab_from_smooth_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_RED_SANDSTONE), Blocks.SMOOTH_RED_SANDSTONE_STAIRS)
			.create("has_smooth_red_sandstone", this.method_10426(Blocks.SMOOTH_RED_SANDSTONE))
			.offerTo(consumer, "smooth_red_sandstone_stairs_from_smooth_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_SLAB, 2)
			.create("has_smooth_quartz", this.method_10426(Blocks.SMOOTH_QUARTZ))
			.offerTo(consumer, "smooth_quartz_slab_from_smooth_quartz_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_QUARTZ), Blocks.SMOOTH_QUARTZ_STAIRS)
			.create("has_smooth_quartz", this.method_10426(Blocks.SMOOTH_QUARTZ))
			.offerTo(consumer, "smooth_quartz_stairs_from_smooth_quartz_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_SLAB, 2)
			.create("has_end_stone_brick", this.method_10426(Blocks.END_STONE_BRICKS))
			.offerTo(consumer, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_STAIRS)
			.create("has_end_stone_brick", this.method_10426(Blocks.END_STONE_BRICKS))
			.offerTo(consumer, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE_BRICKS), Blocks.END_STONE_BRICK_WALL)
			.create("has_end_stone_brick", this.method_10426(Blocks.END_STONE_BRICKS))
			.offerTo(consumer, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICKS)
			.create("has_end_stone", this.method_10426(Blocks.END_STONE))
			.offerTo(consumer, "end_stone_bricks_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_SLAB, 2)
			.create("has_end_stone", this.method_10426(Blocks.END_STONE))
			.offerTo(consumer, "end_stone_brick_slab_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_STAIRS)
			.create("has_end_stone", this.method_10426(Blocks.END_STONE))
			.offerTo(consumer, "end_stone_brick_stairs_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.END_STONE), Blocks.END_STONE_BRICK_WALL)
			.create("has_end_stone", this.method_10426(Blocks.END_STONE))
			.offerTo(consumer, "end_stone_brick_wall_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_STONE), Blocks.SMOOTH_STONE_SLAB, 2)
			.create("has_smooth_stone", this.method_10426(Blocks.SMOOTH_STONE))
			.offerTo(consumer, "smooth_stone_slab_from_smooth_stone_stonecutting");
	}

	private void method_17585(Consumer<RecipeJsonProvider> consumer, String string, CookingRecipeSerializer<?> cookingRecipeSerializer, int i) {
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.BEEF), Items.COOKED_BEEF, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_beef", this.method_10426(Items.BEEF))
			.offerTo(consumer, "cooked_beef_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.CHICKEN), Items.COOKED_CHICKEN, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_chicken", this.method_10426(Items.CHICKEN))
			.offerTo(consumer, "cooked_chicken_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.COD), Items.COOKED_COD, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_cod", this.method_10426(Items.COD))
			.offerTo(consumer, "cooked_cod_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Blocks.KELP), Items.DRIED_KELP, 0.1F, i, cookingRecipeSerializer)
			.criterion("has_kelp", this.method_10426(Blocks.KELP))
			.offerTo(consumer, "dried_kelp_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.SALMON), Items.COOKED_SALMON, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_salmon", this.method_10426(Items.SALMON))
			.offerTo(consumer, "cooked_salmon_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.MUTTON), Items.COOKED_MUTTON, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_mutton", this.method_10426(Items.MUTTON))
			.offerTo(consumer, "cooked_mutton_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.PORKCHOP), Items.COOKED_PORKCHOP, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_porkchop", this.method_10426(Items.PORKCHOP))
			.offerTo(consumer, "cooked_porkchop_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.POTATO), Items.BAKED_POTATO, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_potato", this.method_10426(Items.POTATO))
			.offerTo(consumer, "baked_potato_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.RABBIT), Items.COOKED_RABBIT, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_rabbit", this.method_10426(Items.RABBIT))
			.offerTo(consumer, "cooked_rabbit_from_" + string);
	}

	private EnterBlockCriterion.Conditions method_10422(Block block) {
		return new EnterBlockCriterion.Conditions(block, StatePredicate.ANY);
	}

	private InventoryChangedCriterion.Conditions method_10424(NumberRange.IntRange intRange, ItemConvertible itemConvertible) {
		return this.method_10423(ItemPredicate.Builder.create().item(itemConvertible).count(intRange).build());
	}

	private InventoryChangedCriterion.Conditions method_10426(ItemConvertible itemConvertible) {
		return this.method_10423(ItemPredicate.Builder.create().item(itemConvertible).build());
	}

	private InventoryChangedCriterion.Conditions method_10420(Tag<Item> tag) {
		return this.method_10423(ItemPredicate.Builder.create().tag(tag).build());
	}

	private InventoryChangedCriterion.Conditions method_10423(ItemPredicate... itemPredicates) {
		return new InventoryChangedCriterion.Conditions(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, itemPredicates);
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
