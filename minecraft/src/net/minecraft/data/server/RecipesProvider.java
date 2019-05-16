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
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
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
		ShapedRecipeJsonFactory.create(Blocks.field_9999, 3)
			.input('#', Blocks.field_10533)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.field_10533))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8094)
			.input('#', Blocks.field_10218)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.field_10382))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10278)
			.input(Blocks.field_10218)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.field_10218))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10232, 3)
			.input('#', Blocks.field_10218)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.field_10218))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10144, 3)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_10218)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.field_10218))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10457)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_10218)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.field_10218))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10218, 4)
			.input(ItemTags.field_15525)
			.group("planks")
			.criterion("has_logs", this.method_10420(ItemTags.field_15525))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10397)
			.input('#', Blocks.field_10218)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.field_10218))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10031, 6)
			.input('#', Blocks.field_10218)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.field_10218))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10256, 4)
			.input('#', Blocks.field_10218)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.field_10218))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10608, 2)
			.input('#', Blocks.field_10218)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.field_10218))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10546, 6)
			.input('#', Blocks.field_10523)
			.input('S', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("XSX")
			.pattern("X#X")
			.pattern("XSX")
			.criterion("has_rail", this.method_10426(Blocks.field_10167))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10115, 2)
			.input(Blocks.field_10508)
			.input(Blocks.field_10445)
			.criterion("has_stone", this.method_10426(Blocks.field_10508))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10535)
			.input('I', Blocks.field_10085)
			.input('i', Items.field_8620)
			.pattern("III")
			.pattern(" i ")
			.pattern("iii")
			.criterion("has_iron_block", this.method_10426(Blocks.field_10085))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8694)
			.input('/', Items.field_8600)
			.input('_', Blocks.field_10136)
			.pattern("///")
			.pattern(" / ")
			.pattern("/_/")
			.criterion("has_stone_slab", this.method_10426(Blocks.field_10136))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8107, 4)
			.input('#', Items.field_8600)
			.input('X', Items.field_8145)
			.input('Y', Items.field_8153)
			.pattern("X")
			.pattern("#")
			.pattern("Y")
			.criterion("has_feather", this.method_10426(Items.field_8153))
			.criterion("has_flint", this.method_10426(Items.field_8145))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16328, 1)
			.input('P', ItemTags.field_15537)
			.input('S', ItemTags.field_15534)
			.pattern("PSP")
			.pattern("P P")
			.pattern("PSP")
			.criterion("has_planks", this.method_10420(ItemTags.field_15537))
			.criterion("has_wood_slab", this.method_10420(ItemTags.field_15534))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10327)
			.input('S', Items.field_8137)
			.input('G', Blocks.field_10033)
			.input('O', Blocks.field_10540)
			.pattern("GGG")
			.pattern("GSG")
			.pattern("OOO")
			.criterion("has_nether_star", this.method_10426(Items.field_8137))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8515)
			.input(Items.field_8428)
			.input(Items.field_8186, 6)
			.criterion("has_beetroot", this.method_10426(Items.field_8186))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10307, 3)
			.input('#', Blocks.field_10511)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.field_10511))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8442)
			.input('#', Blocks.field_10148)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.field_10382))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10417)
			.input(Blocks.field_10148)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.field_10148))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10352, 3)
			.input('#', Blocks.field_10148)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.field_10148))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10299, 3)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_10148)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.field_10148))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10513)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_10148)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.field_10148))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10148, 4)
			.input(ItemTags.field_15554)
			.group("planks")
			.criterion("has_log", this.method_10420(ItemTags.field_15554))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10592)
			.input('#', Blocks.field_10148)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.field_10148))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10257, 6)
			.input('#', Blocks.field_10148)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.field_10148))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10408, 4)
			.input('#', Blocks.field_10148)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.field_10148))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10486, 2)
			.input('#', Blocks.field_10148)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.field_10148))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8572)
			.input('#', Blocks.field_10146)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_black_wool", this.method_10426(Blocks.field_10146))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BLACK_BED)
			.input('#', Blocks.field_10146)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_black_wool", this.method_10426(Blocks.field_10146))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLACK_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8226)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "black_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10106, 3)
			.input('#', Blocks.field_10146)
			.pattern("##")
			.group("carpet")
			.criterion("has_black_wool", this.method_10426(Blocks.field_10146))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10106, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8226)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_black_dye", this.method_10426(Items.field_8226))
			.offerTo(consumer, "black_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10506, 8)
			.input(Items.field_8226)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8226)
			.input(Items.field_8794)
			.group("black_dye")
			.criterion("has_ink_sac", this.method_10426(Items.field_8794))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8226)
			.input(Blocks.field_10606)
			.group("black_dye")
			.criterion("has_black_flower", this.method_10426(Blocks.field_10606))
			.offerTo(consumer, "black_dye_from_wither_rose");
		ShapedRecipeJsonFactory.create(Blocks.field_9997, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8226)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10070, 16)
			.input('#', Blocks.field_9997)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10070, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8226)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_black_dye", this.method_10426(Items.field_8226))
			.offerTo(consumer, "black_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10626, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8226)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10146)
			.input(Items.field_8226)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8183, 2)
			.input(Items.field_8894)
			.criterion("has_blaze_rod", this.method_10426(Items.field_8894))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8128)
			.input('#', Blocks.field_10514)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_blue_wool", this.method_10426(Blocks.field_10514))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BLUE_BED)
			.input('#', Blocks.field_10514)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_blue_wool", this.method_10426(Blocks.field_10514))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BLUE_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8345)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "blue_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10043, 3)
			.input('#', Blocks.field_10514)
			.pattern("##")
			.group("carpet")
			.criterion("has_blue_wool", this.method_10426(Blocks.field_10514))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10043, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8345)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_blue_dye", this.method_10426(Items.field_8345))
			.offerTo(consumer, "blue_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10456, 8)
			.input(Items.field_8345)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8345)
			.input(Items.field_8759)
			.group("blue_dye")
			.criterion("has_lapis_lazuli", this.method_10426(Items.field_8759))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8345)
			.input(Blocks.field_9995)
			.group("blue_dye")
			.criterion("has_blue_flower", this.method_10426(Blocks.field_9995))
			.offerTo(consumer, "blue_dye_from_cornflower");
		ShapedRecipeJsonFactory.create(Blocks.field_10384)
			.input('#', Blocks.field_10225)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_packed_ice", this.method_10424(NumberRange.IntRange.atLeast(9), Blocks.field_10225))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10060, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8345)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9982, 16)
			.input('#', Blocks.field_10060)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9982, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8345)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_blue_dye", this.method_10426(Items.field_8345))
			.offerTo(consumer, "blue_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10409, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8345)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10514)
			.input(Items.field_8345)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8533)
			.input('#', Blocks.field_10161)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.field_10382))
			.offerTo(consumer);
		Item item = Items.field_8324;
		ShapedRecipeJsonFactory.create(Blocks.field_10166)
			.input('X', Items.field_8324)
			.pattern("XXX")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_at_least_9_bonemeal", this.method_10424(NumberRange.IntRange.atLeast(9), item))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8324, 3)
			.input(Items.field_8606)
			.group("bonemeal")
			.criterion("has_bone", this.method_10426(Items.field_8606))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8324, 9)
			.input(Blocks.field_10166)
			.group("bonemeal")
			.criterion("has_at_least_9_bonemeal", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8324))
			.criterion("has_bone_block", this.method_10426(Blocks.field_10166))
			.offerTo(consumer, "bone_meal_from_bone_block");
		ShapelessRecipeJsonFactory.create(Items.field_8529)
			.input(Items.field_8407, 3)
			.input(Items.field_8745)
			.criterion("has_paper", this.method_10426(Items.field_8407))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10504)
			.input('#', ItemTags.field_15537)
			.input('X', Items.field_8529)
			.pattern("###")
			.pattern("XXX")
			.pattern("###")
			.criterion("has_book", this.method_10426(Items.field_8529))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8102)
			.input('#', Items.field_8600)
			.input('X', Items.field_8276)
			.pattern(" #X")
			.pattern("# X")
			.pattern(" #X")
			.criterion("has_string", this.method_10426(Items.field_8276))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8428, 4)
			.input('#', ItemTags.field_15537)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brown_mushroom", this.method_10426(Blocks.field_10251))
			.criterion("has_red_mushroom", this.method_10426(Blocks.field_10559))
			.criterion("has_mushroom_stew", this.method_10426(Items.field_8208))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8229)
			.input('#', Items.field_8861)
			.pattern("###")
			.criterion("has_wheat", this.method_10426(Items.field_8861))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10333)
			.input('B', Items.field_8894)
			.input('#', Blocks.field_10445)
			.pattern(" B ")
			.pattern("###")
			.criterion("has_blaze_rod", this.method_10426(Items.field_8894))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10104)
			.input('#', Items.field_8621)
			.pattern("##")
			.pattern("##")
			.criterion("has_brick", this.method_10426(Items.field_8621))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10191, 6)
			.input('#', Blocks.field_10104)
			.pattern("###")
			.criterion("has_brick_block", this.method_10426(Blocks.field_10104))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10089, 4)
			.input('#', Blocks.field_10104)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_brick_block", this.method_10426(Blocks.field_10104))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8124)
			.input('#', Blocks.field_10113)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_brown_wool", this.method_10426(Blocks.field_10113))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.BROWN_BED)
			.input('#', Blocks.field_10113)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_brown_wool", this.method_10426(Blocks.field_10113))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.BROWN_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8099)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "brown_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10473, 3)
			.input('#', Blocks.field_10113)
			.pattern("##")
			.group("carpet")
			.criterion("has_brown_wool", this.method_10426(Blocks.field_10113))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10473, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8099)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_brown_dye", this.method_10426(Items.field_8099))
			.offerTo(consumer, "brown_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10023, 8)
			.input(Items.field_8099)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8099)
			.input(Items.field_8116)
			.group("brown_dye")
			.criterion("has_cocoa_beans", this.method_10426(Items.field_8116))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10073, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8099)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10163, 16)
			.input('#', Blocks.field_10073)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10163, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8099)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_brown_dye", this.method_10426(Items.field_8099))
			.offerTo(consumer, "brown_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10123, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8099)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10113)
			.input(Items.field_8099)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8550)
			.input('#', Items.field_8620)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10183)
			.input('A', Items.field_8103)
			.input('B', Items.field_8479)
			.input('C', Items.field_8861)
			.input('E', Items.field_8803)
			.pattern("AAA")
			.pattern("BEB")
			.pattern("CCC")
			.criterion("has_egg", this.method_10426(Items.field_8803))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_17350)
			.input('L', ItemTags.field_15539)
			.input('S', Items.field_8600)
			.input('C', ItemTags.field_17487)
			.pattern(" S ")
			.pattern("SCS")
			.pattern("LLL")
			.criterion("has_stick", this.method_10426(Items.field_8600))
			.criterion("has_coal", this.method_10420(ItemTags.field_17487))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8184)
			.input('#', Items.field_8378)
			.input('X', Items.field_8179)
			.pattern("# ")
			.pattern(" X")
			.criterion("has_carrot", this.method_10426(Items.field_8179))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10593)
			.input('#', Items.field_8620)
			.pattern("# #")
			.pattern("# #")
			.pattern("###")
			.criterion("has_water_bucket", this.method_10426(Items.field_8705))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_17563)
			.input('F', ItemTags.field_17620)
			.input('#', ItemTags.field_15537)
			.pattern("F F")
			.pattern("F F")
			.pattern("###")
			.criterion("has_wooden_fences", this.method_10420(ItemTags.field_17620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10034)
			.input('#', ItemTags.field_15537)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion(
				"has_lots_of_items",
				new InventoryChangedCriterion.Conditions(NumberRange.IntRange.atLeast(10), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new ItemPredicate[0])
			)
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8388)
			.input('A', Blocks.field_10034)
			.input('B', Items.field_8045)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", this.method_10426(Items.field_8045))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10044)
			.input('#', Blocks.field_10237)
			.pattern("#")
			.pattern("#")
			.criterion("has_chiseled_quartz_block", this.method_10426(Blocks.field_10044))
			.criterion("has_quartz_block", this.method_10426(Blocks.field_10153))
			.criterion("has_quartz_pillar", this.method_10426(Blocks.field_10437))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10552)
			.input('#', Blocks.field_10131)
			.pattern("#")
			.pattern("#")
			.criterion("has_stone_bricks", this.method_10420(ItemTags.field_15531))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10460)
			.input('#', Items.field_8696)
			.pattern("##")
			.pattern("##")
			.criterion("has_clay_ball", this.method_10426(Items.field_8696))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8557)
			.input('#', Items.field_8695)
			.input('X', Items.field_8725)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", this.method_10426(Items.field_8725))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8713, 9)
			.input(Blocks.field_10381)
			.criterion("has_at_least_9_coal", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8713))
			.criterion("has_coal_block", this.method_10426(Blocks.field_10381))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10381)
			.input('#', Items.field_8713)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_coal", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8713))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10253, 4)
			.input('D', Blocks.field_10566)
			.input('G', Blocks.field_10255)
			.pattern("DG")
			.pattern("GD")
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10351, 6)
			.input('#', Blocks.field_10445)
			.pattern("###")
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10625, 6)
			.input('#', Blocks.field_10445)
			.pattern("###")
			.pattern("###")
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10377)
			.input('#', Blocks.field_10523)
			.input('X', Items.field_8155)
			.input('I', Blocks.field_10340)
			.pattern(" # ")
			.pattern("#X#")
			.pattern("III")
			.criterion("has_quartz", this.method_10426(Items.field_8155))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8251)
			.input('#', Items.field_8620)
			.input('X', Items.field_8725)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", this.method_10426(Items.field_8725))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8423, 8)
			.input('#', Items.field_8861)
			.input('X', Items.field_8116)
			.pattern("#X#")
			.criterion("has_cocoa", this.method_10426(Items.field_8116))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9980)
			.input('#', ItemTags.field_15537)
			.pattern("##")
			.pattern("##")
			.criterion("has_planks", this.method_10420(ItemTags.field_15537))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8399)
			.input('~', Items.field_8276)
			.input('#', Items.field_8600)
			.input('&', Items.field_8620)
			.input('$', Blocks.field_10348)
			.pattern("#&#")
			.pattern("~$~")
			.pattern(" # ")
			.criterion("has_string", this.method_10426(Items.field_8276))
			.criterion("has_stick", this.method_10426(Items.field_8600))
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.criterion("has_tripwire_hook", this.method_10426(Blocks.field_10348))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10083)
			.input('#', ItemTags.field_15537)
			.input('@', Items.field_8276)
			.pattern("@@")
			.pattern("##")
			.criterion("has_string", this.method_10426(Items.field_8276))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10117)
			.input('#', Blocks.field_10624)
			.pattern("#")
			.pattern("#")
			.criterion("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.criterion("has_chiseled_red_sandstone", this.method_10426(Blocks.field_10117))
			.criterion("has_cut_red_sandstone", this.method_10426(Blocks.field_10518))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10292)
			.input('#', Blocks.field_10007)
			.pattern("#")
			.pattern("#")
			.criterion("has_stone_slab", this.method_10426(Blocks.field_10007))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8629)
			.input('#', Blocks.field_10619)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_cyan_wool", this.method_10426(Blocks.field_10619))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.CYAN_BED)
			.input('#', Blocks.field_10619)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_cyan_wool", this.method_10426(Blocks.field_10619))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.CYAN_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8632)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "cyan_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10433, 3)
			.input('#', Blocks.field_10619)
			.pattern("##")
			.group("carpet")
			.criterion("has_cyan_wool", this.method_10426(Blocks.field_10619))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10433, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8632)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_cyan_dye", this.method_10426(Items.field_8632))
			.offerTo(consumer, "cyan_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10233, 8)
			.input(Items.field_8632)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8632, 2)
			.input(Items.field_8345)
			.input(Items.field_8408)
			.criterion("has_green_dye", this.method_10426(Items.field_8408))
			.criterion("has_blue_dye", this.method_10426(Items.field_8345))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10248, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8632)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10355, 16)
			.input('#', Blocks.field_10248)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10355, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8632)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_cyan_dye", this.method_10426(Items.field_8632))
			.offerTo(consumer, "cyan_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10235, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8632)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10619)
			.input(Items.field_8632)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10178, 3)
			.input('#', Blocks.field_10010)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.field_10010))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8138)
			.input('#', Blocks.field_10075)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.field_10382))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10493)
			.input(Blocks.field_10075)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.field_10075))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10403, 3)
			.input('#', Blocks.field_10075)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.field_10075))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10132, 3)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_10075)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.field_10075))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10196)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_10075)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.field_10075))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10075, 4)
			.input(ItemTags.field_15546)
			.group("planks")
			.criterion("has_logs", this.method_10420(ItemTags.field_15546))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10470)
			.input('#', Blocks.field_10075)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.field_10075))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10500, 6)
			.input('#', Blocks.field_10075)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.field_10075))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10616, 4)
			.input('#', Blocks.field_10075)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.field_10075))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10246, 2)
			.input('#', Blocks.field_10075)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.field_10075))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10297)
			.input('S', Items.field_8662)
			.input('I', Items.field_8794)
			.pattern("SSS")
			.pattern("SIS")
			.pattern("SSS")
			.criterion("has_prismarine_shard", this.method_10426(Items.field_8662))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10350, 4)
			.input('#', Blocks.field_10135)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_prismarine", this.method_10426(Blocks.field_10135))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10190, 4)
			.input('#', Blocks.field_10006)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_prismarine_bricks", this.method_10426(Blocks.field_10006))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10130, 4)
			.input('#', Blocks.field_10297)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_dark_prismarine", this.method_10426(Blocks.field_10297))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10429)
			.input('Q', Items.field_8155)
			.input('G', Blocks.field_10033)
			.input('W', Ingredient.fromTag(ItemTags.field_15534))
			.pattern("GGG")
			.pattern("QQQ")
			.pattern("WWW")
			.criterion("has_quartz", this.method_10426(Items.field_8155))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10025, 6)
			.input('R', Items.field_8725)
			.input('#', Blocks.field_10158)
			.input('X', Items.field_8620)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", this.method_10426(Blocks.field_10167))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8477, 9)
			.input(Blocks.field_10201)
			.criterion("has_at_least_9_diamond", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8477))
			.criterion("has_diamond_block", this.method_10426(Blocks.field_10201))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8556)
			.input('#', Items.field_8600)
			.input('X', Items.field_8477)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_diamond", this.method_10426(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10201)
			.input('#', Items.field_8477)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_diamond", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8285)
			.input('X', Items.field_8477)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", this.method_10426(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8058)
			.input('X', Items.field_8477)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_diamond", this.method_10426(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8805)
			.input('X', Items.field_8477)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_diamond", this.method_10426(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8527)
			.input('#', Items.field_8600)
			.input('X', Items.field_8477)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_diamond", this.method_10426(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8348)
			.input('X', Items.field_8477)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", this.method_10426(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8377)
			.input('#', Items.field_8600)
			.input('X', Items.field_8477)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_diamond", this.method_10426(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8250)
			.input('#', Items.field_8600)
			.input('X', Items.field_8477)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_diamond", this.method_10426(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8802)
			.input('#', Items.field_8600)
			.input('X', Items.field_8477)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_diamond", this.method_10426(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10508, 2)
			.input('Q', Items.field_8155)
			.input('C', Blocks.field_10445)
			.pattern("CQ")
			.pattern("QC")
			.criterion("has_quartz", this.method_10426(Items.field_8155))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10200)
			.input('R', Items.field_8725)
			.input('#', Blocks.field_10445)
			.input('X', Items.field_8102)
			.pattern("###")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_bow", this.method_10426(Items.field_8102))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10228)
			.input('R', Items.field_8725)
			.input('#', Blocks.field_10445)
			.pattern("###")
			.pattern("# #")
			.pattern("#R#")
			.criterion("has_redstone", this.method_10426(Items.field_8725))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8687, 9)
			.input(Blocks.field_10234)
			.criterion("has_at_least_9_emerald", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8687))
			.criterion("has_emerald_block", this.method_10426(Blocks.field_10234))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10234)
			.input('#', Items.field_8687)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_emerald", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8687))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10485)
			.input('B', Items.field_8529)
			.input('#', Blocks.field_10540)
			.input('D', Items.field_8477)
			.pattern(" B ")
			.pattern("D#D")
			.pattern("###")
			.criterion("has_obsidian", this.method_10426(Blocks.field_10540))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10443)
			.input('#', Blocks.field_10540)
			.input('E', Items.field_8449)
			.pattern("###")
			.pattern("#E#")
			.pattern("###")
			.criterion("has_ender_eye", this.method_10426(Items.field_8449))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8449)
			.input(Items.field_8634)
			.input(Items.field_8183)
			.criterion("has_blaze_powder", this.method_10426(Items.field_8183))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10462, 4)
			.input('#', Blocks.field_10471)
			.pattern("##")
			.pattern("##")
			.criterion("has_end_stone", this.method_10426(Blocks.field_10471))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8301)
			.input('T', Items.field_8070)
			.input('E', Items.field_8449)
			.input('G', Blocks.field_10033)
			.pattern("GGG")
			.pattern("GEG")
			.pattern("GTG")
			.criterion("has_ender_eye", this.method_10426(Items.field_8449))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10455, 4)
			.input('#', Items.field_8882)
			.input('/', Items.field_8894)
			.pattern("/")
			.pattern("#")
			.criterion("has_chorus_fruit_popped", this.method_10426(Items.field_8882))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10620, 3)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_10161)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.field_10161))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10188)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_10161)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.field_10161))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8711)
			.input(Items.field_8680)
			.input(Blocks.field_10251)
			.input(Items.field_8479)
			.criterion("has_spider_eye", this.method_10426(Items.field_8680))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8814, 3)
			.input(Items.field_8054)
			.input(Items.field_8183)
			.input(Ingredient.ofItems(Items.field_8713, Items.field_8665))
			.criterion("has_blaze_powder", this.method_10426(Items.field_8183))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8378)
			.input('#', Items.field_8600)
			.input('X', Items.field_8276)
			.pattern("  #")
			.pattern(" #X")
			.pattern("# X")
			.criterion("has_string", this.method_10426(Items.field_8276))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8884)
			.input(Items.field_8620)
			.input(Items.field_8145)
			.criterion("has_flint", this.method_10426(Items.field_8145))
			.criterion("has_obsidian", this.method_10426(Blocks.field_10540))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10495)
			.input('#', Items.field_8621)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brick", this.method_10426(Items.field_8621))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10181)
			.input('#', Blocks.field_10445)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8063)
			.input('A', Blocks.field_10181)
			.input('B', Items.field_8045)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", this.method_10426(Items.field_8045))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8469, 3)
			.input('#', Blocks.field_10033)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10285, 16)
			.input('#', Blocks.field_10033)
			.pattern("###")
			.pattern("###")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10171)
			.input('#', Items.field_8601)
			.pattern("##")
			.pattern("##")
			.criterion("has_glowstone_dust", this.method_10426(Items.field_8601))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8463)
			.input('#', Items.field_8695)
			.input('X', Items.field_8279)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8825)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8753)
			.input('X', Items.field_8695)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8071)
			.input('#', Items.field_8397)
			.input('X', Items.field_8179)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_nugget", this.method_10426(Items.field_8397))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8678)
			.input('X', Items.field_8695)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8862)
			.input('X', Items.field_8695)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8303)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8416)
			.input('X', Items.field_8695)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8335)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10425, 6)
			.input('R', Items.field_8725)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", this.method_10426(Blocks.field_10167))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8322)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8845)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10205)
			.input('#', Items.field_8695)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_gold_ingot", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8695))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8695, 9)
			.input(Blocks.field_10205)
			.group("gold_ingot")
			.criterion("has_at_least_9_gold_ingot", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8695))
			.criterion("has_gold_block", this.method_10426(Blocks.field_10205))
			.offerTo(consumer, "gold_ingot_from_gold_block");
		ShapedRecipeJsonFactory.create(Items.field_8695)
			.input('#', Items.field_8397)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group("gold_ingot")
			.criterion("has_at_least_9_gold_nugget", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8397))
			.offerTo(consumer, "gold_ingot_from_nuggets");
		ShapelessRecipeJsonFactory.create(Items.field_8397, 9)
			.input(Items.field_8695)
			.criterion("has_at_least_9_gold_nugget", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8397))
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10474)
			.input(Blocks.field_10508)
			.input(Items.field_8155)
			.criterion("has_quartz", this.method_10426(Items.field_8155))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8617)
			.input('#', Blocks.field_10423)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_gray_wool", this.method_10426(Blocks.field_10423))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GRAY_BED)
			.input('#', Blocks.field_10423)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_gray_wool", this.method_10426(Blocks.field_10423))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.GRAY_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8298)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "gray_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10591, 3)
			.input('#', Blocks.field_10423)
			.pattern("##")
			.group("carpet")
			.criterion("has_gray_wool", this.method_10426(Blocks.field_10423))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10591, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8298)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_gray_dye", this.method_10426(Items.field_8298))
			.offerTo(consumer, "gray_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10353, 8)
			.input(Items.field_8298)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8298, 2)
			.input(Items.field_8226)
			.input(Items.field_8446)
			.criterion("has_white_dye", this.method_10426(Items.field_8446))
			.criterion("has_black_dye", this.method_10426(Items.field_8226))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10555, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8298)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10077, 16)
			.input('#', Blocks.field_10555)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10077, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8298)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_gray_dye", this.method_10426(Items.field_8298))
			.offerTo(consumer, "gray_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10349, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8298)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10423)
			.input(Items.field_8298)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8295)
			.input('#', Blocks.field_10170)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_green_wool", this.method_10426(Blocks.field_10170))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.GREEN_BED)
			.input('#', Blocks.field_10170)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_green_wool", this.method_10426(Blocks.field_10170))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.GREEN_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8408)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "green_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10338, 3)
			.input('#', Blocks.field_10170)
			.pattern("##")
			.group("carpet")
			.criterion("has_green_wool", this.method_10426(Blocks.field_10170))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10338, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8408)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_green_dye", this.method_10426(Items.field_8408))
			.offerTo(consumer, "green_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10529, 8)
			.input(Items.field_8408)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10357, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8408)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10419, 16)
			.input('#', Blocks.field_10357)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10419, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8408)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_green_dye", this.method_10426(Items.field_8408))
			.offerTo(consumer, "green_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10526, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8408)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10170)
			.input(Items.field_8408)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10359)
			.input('#', Items.field_8861)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_wheat", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8861))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10582)
			.input('#', Items.field_8620)
			.pattern("##")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10312)
			.input('C', Blocks.field_10034)
			.input('I', Items.field_8620)
			.pattern("I I")
			.pattern("ICI")
			.pattern(" I ")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8836)
			.input('A', Blocks.field_10312)
			.input('B', Items.field_8045)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", this.method_10426(Items.field_8045))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8475)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10576, 16)
			.input('#', Items.field_8620)
			.pattern("###")
			.pattern("###")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10085)
			.input('#', Items.field_8620)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_iron_ingot", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8660)
			.input('X', Items.field_8620)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8523)
			.input('X', Items.field_8620)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9973, 3)
			.input('#', Items.field_8620)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8743)
			.input('X', Items.field_8620)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8609)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8620, 9)
			.input(Blocks.field_10085)
			.group("iron_ingot")
			.criterion("has_at_least_9_iron_ingot", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8620))
			.criterion("has_iron_block", this.method_10426(Blocks.field_10085))
			.offerTo(consumer, "iron_ingot_from_iron_block");
		ShapedRecipeJsonFactory.create(Items.field_8620)
			.input('#', Items.field_8675)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group("iron_ingot")
			.criterion("has_at_least_9_iron_nugget", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8675))
			.offerTo(consumer, "iron_ingot_from_nuggets");
		ShapedRecipeJsonFactory.create(Items.field_8396)
			.input('X', Items.field_8620)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8675, 9)
			.input(Items.field_8620)
			.criterion("has_at_least_9_iron_nugget", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8675))
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8403)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8699)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8371)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10453)
			.input('#', Items.field_8620)
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8143)
			.input('#', Items.field_8600)
			.input('X', Items.field_8745)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_leather", this.method_10426(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10223)
			.input('#', ItemTags.field_15537)
			.input('X', Items.field_8477)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_diamond", this.method_10426(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10303, 3)
			.input('#', Blocks.field_10306)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.field_10306))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8730)
			.input('#', Blocks.field_10334)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.field_10382))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10553)
			.input(Blocks.field_10334)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.field_10334))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10627, 3)
			.input('#', Blocks.field_10334)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.field_10334))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10319, 3)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_10334)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.field_10334))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10041)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_10334)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.field_10334))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10334, 4)
			.input(ItemTags.field_15538)
			.group("planks")
			.criterion("has_log", this.method_10420(ItemTags.field_15538))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10026)
			.input('#', Blocks.field_10334)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.field_10334))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10617, 6)
			.input('#', Blocks.field_10334)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.field_10334))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10122, 4)
			.input('#', Blocks.field_10334)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.field_10334))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10017, 2)
			.input('#', Blocks.field_10334)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.field_10334))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9983, 3)
			.input('#', Items.field_8600)
			.pattern("# #")
			.pattern("###")
			.pattern("# #")
			.criterion("has_stick", this.method_10426(Items.field_8600))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10441)
			.input('#', Items.field_8759)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_lapis", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8759))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8759, 9)
			.input(Blocks.field_10441)
			.criterion("has_at_least_9_lapis", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8759))
			.criterion("has_lapis_block", this.method_10426(Blocks.field_10441))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8719, 2)
			.input('~', Items.field_8276)
			.input('O', Items.field_8777)
			.pattern("~~ ")
			.pattern("~O ")
			.pattern("  ~")
			.criterion("has_slime_ball", this.method_10426(Items.field_8777))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8745)
			.input('#', Items.field_8245)
			.pattern("##")
			.pattern("##")
			.criterion("has_rabbit_hide", this.method_10426(Items.field_8245))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8370)
			.input('X', Items.field_8745)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", this.method_10426(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8577)
			.input('X', Items.field_8745)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_leather", this.method_10426(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8267)
			.input('X', Items.field_8745)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", this.method_10426(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8570)
			.input('X', Items.field_8745)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", this.method_10426(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_18138)
			.input('X', Items.field_8745)
			.pattern("X X")
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", this.method_10426(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16330)
			.input('S', ItemTags.field_15534)
			.input('B', Blocks.field_10504)
			.pattern("SSS")
			.pattern(" B ")
			.pattern(" S ")
			.criterion("has_book", this.method_10426(Items.field_8529))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10363)
			.input('#', Blocks.field_10445)
			.input('X', Items.field_8600)
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8379)
			.input('#', Blocks.field_10294)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_light_blue_wool", this.method_10426(Blocks.field_10294))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LIGHT_BLUE_BED)
			.input('#', Blocks.field_10294)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_light_blue_wool", this.method_10426(Blocks.field_10294))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIGHT_BLUE_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8273)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "light_blue_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10290, 3)
			.input('#', Blocks.field_10294)
			.pattern("##")
			.group("carpet")
			.criterion("has_light_blue_wool", this.method_10426(Blocks.field_10294))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10290, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8273)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_light_blue_dye", this.method_10426(Items.field_8273))
			.offerTo(consumer, "light_blue_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10321, 8)
			.input(Items.field_8273)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8273)
			.input(Blocks.field_10086)
			.group("light_blue_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.field_10086))
			.offerTo(consumer, "light_blue_dye_from_blue_orchid");
		ShapelessRecipeJsonFactory.create(Items.field_8273, 2)
			.input(Items.field_8345)
			.input(Items.field_8446)
			.group("light_blue_dye")
			.criterion("has_blue_dye", this.method_10426(Items.field_8345))
			.criterion("has_white_dye", this.method_10426(Items.field_8446))
			.offerTo(consumer, "light_blue_dye_from_blue_white_dye");
		ShapedRecipeJsonFactory.create(Blocks.field_10271, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8273)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10193, 16)
			.input('#', Blocks.field_10271)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10193, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8273)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_light_blue_dye", this.method_10426(Items.field_8273))
			.offerTo(consumer, "light_blue_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10325, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8273)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10294)
			.input(Items.field_8273)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8855)
			.input('#', Blocks.field_10222)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_light_gray_wool", this.method_10426(Blocks.field_10222))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LIGHT_GRAY_BED)
			.input('#', Blocks.field_10222)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_light_gray_wool", this.method_10426(Blocks.field_10222))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIGHT_GRAY_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8851)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "light_gray_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10209, 3)
			.input('#', Blocks.field_10222)
			.pattern("##")
			.group("carpet")
			.criterion("has_light_gray_wool", this.method_10426(Blocks.field_10222))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10209, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8851)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_light_gray_dye", this.method_10426(Items.field_8851))
			.offerTo(consumer, "light_gray_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10628, 8)
			.input(Items.field_8851)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8851)
			.input(Blocks.field_10573)
			.group("light_gray_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.field_10573))
			.offerTo(consumer, "light_gray_dye_from_azure_bluet");
		ShapelessRecipeJsonFactory.create(Items.field_8851, 2)
			.input(Items.field_8298)
			.input(Items.field_8446)
			.group("light_gray_dye")
			.criterion("has_gray_dye", this.method_10426(Items.field_8298))
			.criterion("has_white_dye", this.method_10426(Items.field_8446))
			.offerTo(consumer, "light_gray_dye_from_gray_white_dye");
		ShapelessRecipeJsonFactory.create(Items.field_8851, 3)
			.input(Items.field_8226)
			.input(Items.field_8446, 2)
			.group("light_gray_dye")
			.criterion("has_white_dye", this.method_10426(Items.field_8446))
			.criterion("has_black_dye", this.method_10426(Items.field_8226))
			.offerTo(consumer, "light_gray_dye_from_black_white_dye");
		ShapelessRecipeJsonFactory.create(Items.field_8851)
			.input(Blocks.field_10554)
			.group("light_gray_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.field_10554))
			.offerTo(consumer, "light_gray_dye_from_oxeye_daisy");
		ShapelessRecipeJsonFactory.create(Items.field_8851)
			.input(Blocks.field_10156)
			.group("light_gray_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.field_10156))
			.offerTo(consumer, "light_gray_dye_from_white_tulip");
		ShapedRecipeJsonFactory.create(Blocks.field_9996, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8851)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10129, 16)
			.input('#', Blocks.field_9996)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10129, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8851)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_light_gray_dye", this.method_10426(Items.field_8851))
			.offerTo(consumer, "light_gray_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10590, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8851)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10222)
			.input(Items.field_8851)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10224)
			.input('#', Items.field_8695)
			.pattern("##")
			.criterion("has_gold_ingot", this.method_10426(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8778)
			.input('#', Blocks.field_10028)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_lime_wool", this.method_10426(Blocks.field_10028))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.LIME_BED)
			.input('#', Blocks.field_10028)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_lime_wool", this.method_10426(Blocks.field_10028))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.LIME_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8131)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "lime_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10040, 3)
			.input('#', Blocks.field_10028)
			.pattern("##")
			.group("carpet")
			.criterion("has_lime_wool", this.method_10426(Blocks.field_10028))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10040, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8131)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_lime_dye", this.method_10426(Items.field_8131))
			.offerTo(consumer, "lime_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10133, 8)
			.input(Items.field_8131)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8131, 2)
			.input(Items.field_8408)
			.input(Items.field_8446)
			.criterion("has_green_dye", this.method_10426(Items.field_8408))
			.criterion("has_white_dye", this.method_10426(Items.field_8446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10157, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8131)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10305, 16)
			.input('#', Blocks.field_10157)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10305, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8131)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_lime_dye", this.method_10426(Items.field_8131))
			.offerTo(consumer, "lime_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10014, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8131)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10028)
			.input(Items.field_8131)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10009)
			.input('A', Blocks.field_10147)
			.input('B', Blocks.field_10336)
			.pattern("A")
			.pattern("B")
			.criterion("has_carved_pumpkin", this.method_10426(Blocks.field_10147))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8671)
			.input('#', Blocks.field_10215)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_magenta_wool", this.method_10426(Blocks.field_10215))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.MAGENTA_BED)
			.input('#', Blocks.field_10215)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_magenta_wool", this.method_10426(Blocks.field_10215))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.MAGENTA_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8669)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "magenta_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10482, 3)
			.input('#', Blocks.field_10215)
			.pattern("##")
			.group("carpet")
			.criterion("has_magenta_wool", this.method_10426(Blocks.field_10215))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10482, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8669)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_magenta_dye", this.method_10426(Items.field_8669))
			.offerTo(consumer, "magenta_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10300, 8)
			.input(Items.field_8669)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8669)
			.input(Blocks.field_10226)
			.group("magenta_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.field_10226))
			.offerTo(consumer, "magenta_dye_from_allium");
		ShapelessRecipeJsonFactory.create(Items.field_8669, 4)
			.input(Items.field_8345)
			.input(Items.field_8264, 2)
			.input(Items.field_8446)
			.group("magenta_dye")
			.criterion("has_blue_dye", this.method_10426(Items.field_8345))
			.criterion("has_rose_red", this.method_10426(Items.field_8264))
			.criterion("has_white_dye", this.method_10426(Items.field_8446))
			.offerTo(consumer, "magenta_dye_from_blue_red_white_dye");
		ShapelessRecipeJsonFactory.create(Items.field_8669, 3)
			.input(Items.field_8345)
			.input(Items.field_8264)
			.input(Items.field_8330)
			.group("magenta_dye")
			.criterion("has_pink_dye", this.method_10426(Items.field_8330))
			.criterion("has_blue_dye", this.method_10426(Items.field_8345))
			.criterion("has_red_dye", this.method_10426(Items.field_8264))
			.offerTo(consumer, "magenta_dye_from_blue_red_pink");
		ShapelessRecipeJsonFactory.create(Items.field_8669, 2)
			.input(Blocks.field_10378)
			.group("magenta_dye")
			.criterion("has_double_plant", this.method_10426(Blocks.field_10378))
			.offerTo(consumer, "magenta_dye_from_lilac");
		ShapelessRecipeJsonFactory.create(Items.field_8669, 2)
			.input(Items.field_8296)
			.input(Items.field_8330)
			.group("magenta_dye")
			.criterion("has_pink_dye", this.method_10426(Items.field_8330))
			.criterion("has_purple_dye", this.method_10426(Items.field_8296))
			.offerTo(consumer, "magenta_dye_from_purple_and_pink");
		ShapedRecipeJsonFactory.create(Blocks.field_10574, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8669)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10469, 16)
			.input('#', Blocks.field_10574)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10469, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8669)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_magenta_dye", this.method_10426(Items.field_8669))
			.offerTo(consumer, "magenta_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10015, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8669)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10215)
			.input(Items.field_8669)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10092)
			.input('#', Items.field_8135)
			.pattern("##")
			.pattern("##")
			.criterion("has_magma_cream", this.method_10426(Items.field_8135))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8135)
			.input(Items.field_8183)
			.input(Items.field_8777)
			.criterion("has_blaze_powder", this.method_10426(Items.field_8183))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8895)
			.input('#', Items.field_8407)
			.input('X', Items.field_8251)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_compass", this.method_10426(Items.field_8251))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10545)
			.input('M', Items.field_8497)
			.pattern("MMM")
			.pattern("MMM")
			.pattern("MMM")
			.criterion("has_melon", this.method_10426(Items.field_8497))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8188).input(Items.field_8497).criterion("has_melon", this.method_10426(Items.field_8497)).offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8045)
			.input('#', Items.field_8620)
			.pattern("# #")
			.pattern("###")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_9989)
			.input(Blocks.field_10445)
			.input(Blocks.field_10597)
			.criterion("has_vine", this.method_10426(Blocks.field_10597))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9990, 6)
			.input('#', Blocks.field_9989)
			.pattern("###")
			.pattern("###")
			.criterion("has_mossy_cobblestone", this.method_10426(Blocks.field_9989))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10065)
			.input(Blocks.field_10056)
			.input(Blocks.field_10597)
			.criterion("has_mossy_cobblestone", this.method_10426(Blocks.field_9989))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8208)
			.input(Blocks.field_10251)
			.input(Blocks.field_10559)
			.input(Items.field_8428)
			.criterion("has_mushroom_stew", this.method_10426(Items.field_8208))
			.criterion("has_bowl", this.method_10426(Items.field_8428))
			.criterion("has_brown_mushroom", this.method_10426(Blocks.field_10251))
			.criterion("has_red_mushroom", this.method_10426(Blocks.field_10559))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10266)
			.input('N', Items.field_8729)
			.pattern("NN")
			.pattern("NN")
			.criterion("has_netherbrick", this.method_10426(Items.field_8729))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10364, 6)
			.input('#', Blocks.field_10266)
			.input('-', Items.field_8729)
			.pattern("#-#")
			.pattern("#-#")
			.criterion("has_nether_brick", this.method_10426(Blocks.field_10266))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10390, 6)
			.input('#', Blocks.field_10266)
			.pattern("###")
			.criterion("has_nether_brick", this.method_10426(Blocks.field_10266))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10159, 4)
			.input('#', Blocks.field_10266)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_nether_brick", this.method_10426(Blocks.field_10266))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10541)
			.input('#', Items.field_8790)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_nether_wart", this.method_10426(Items.field_8790))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10179)
			.input('#', ItemTags.field_15537)
			.input('X', Items.field_8725)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_redstone", this.method_10426(Items.field_8725))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10126, 3)
			.input('#', Blocks.field_10431)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.field_10431))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10057)
			.input(Blocks.field_10161)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.field_10161))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10161, 4)
			.input(ItemTags.field_15545)
			.group("planks")
			.criterion("has_log", this.method_10420(ItemTags.field_15545))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10484)
			.input('#', Blocks.field_10161)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.field_10161))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10119, 6)
			.input('#', Blocks.field_10161)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.field_10161))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10563, 4)
			.input('#', Blocks.field_10161)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.field_10161))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10137, 2)
			.input('#', Blocks.field_10161)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.field_10161))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10282)
			.input('Q', Items.field_8155)
			.input('R', Items.field_8725)
			.input('#', Blocks.field_10445)
			.pattern("###")
			.pattern("RRQ")
			.pattern("###")
			.criterion("has_quartz", this.method_10426(Items.field_8155))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8824)
			.input('#', Blocks.field_10095)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_orange_wool", this.method_10426(Blocks.field_10095))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.ORANGE_BED)
			.input('#', Blocks.field_10095)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_orange_wool", this.method_10426(Blocks.field_10095))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.ORANGE_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8492)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "orange_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_9977, 3)
			.input('#', Blocks.field_10095)
			.pattern("##")
			.group("carpet")
			.criterion("has_orange_wool", this.method_10426(Blocks.field_10095))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9977, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8492)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_oramge_dye", this.method_10426(Items.field_8492))
			.offerTo(consumer, "orange_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10022, 8)
			.input(Items.field_8492)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8492)
			.input(Blocks.field_10048)
			.group("orange_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.field_10048))
			.offerTo(consumer, "orange_dye_from_orange_tulip");
		ShapelessRecipeJsonFactory.create(Items.field_8492, 2)
			.input(Items.field_8264)
			.input(Items.field_8192)
			.group("orange_dye")
			.criterion("has_red_dye", this.method_10426(Items.field_8264))
			.criterion("has_yellow_dye", this.method_10426(Items.field_8192))
			.offerTo(consumer, "orange_dye_from_red_yellow");
		ShapedRecipeJsonFactory.create(Blocks.field_10227, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8492)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10496, 16)
			.input('#', Blocks.field_10227)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10496, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8492)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_orange_dye", this.method_10426(Items.field_8492))
			.offerTo(consumer, "orange_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10184, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8492)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10095)
			.input(Items.field_8492)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8892)
			.input('#', Items.field_8600)
			.input('X', Ingredient.fromTag(ItemTags.field_15544))
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_wool", this.method_10420(ItemTags.field_15544))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8407, 3)
			.input('#', Blocks.field_10424)
			.pattern("###")
			.criterion("has_reeds", this.method_10426(Blocks.field_10424))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10437, 2)
			.input('#', Blocks.field_10153)
			.pattern("#")
			.pattern("#")
			.criterion("has_chiseled_quartz_block", this.method_10426(Blocks.field_10044))
			.criterion("has_quartz_block", this.method_10426(Blocks.field_10153))
			.criterion("has_quartz_pillar", this.method_10426(Blocks.field_10437))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10225)
			.input(Blocks.field_10295, 9)
			.criterion("has_at_least_9_ice", this.method_10424(NumberRange.IntRange.atLeast(9), Blocks.field_10295))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8329)
			.input('#', Blocks.field_10459)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_pink_wool", this.method_10426(Blocks.field_10459))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.PINK_BED)
			.input('#', Blocks.field_10459)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_pink_wool", this.method_10426(Blocks.field_10459))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PINK_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8330)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "pink_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10393, 3)
			.input('#', Blocks.field_10459)
			.pattern("##")
			.group("carpet")
			.criterion("has_pink_wool", this.method_10426(Blocks.field_10459))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10393, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8330)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_pink_dye", this.method_10426(Items.field_8330))
			.offerTo(consumer, "pink_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10522, 8)
			.input(Items.field_8330)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8330, 2)
			.input(Blocks.field_10003)
			.group("pink_dye")
			.criterion("has_double_plant", this.method_10426(Blocks.field_10003))
			.offerTo(consumer, "pink_dye_from_peony");
		ShapelessRecipeJsonFactory.create(Items.field_8330)
			.input(Blocks.field_10315)
			.group("pink_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.field_10315))
			.offerTo(consumer, "pink_dye_from_pink_tulip");
		ShapelessRecipeJsonFactory.create(Items.field_8330, 2)
			.input(Items.field_8264)
			.input(Items.field_8446)
			.group("pink_dye")
			.criterion("has_white_dye", this.method_10426(Items.field_8446))
			.criterion("has_red_dye", this.method_10426(Items.field_8264))
			.offerTo(consumer, "pink_dye_from_red_white_dye");
		ShapedRecipeJsonFactory.create(Blocks.field_10317, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8330)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10565, 16)
			.input('#', Blocks.field_10317)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10565, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8330)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_pink_dye", this.method_10426(Items.field_8330))
			.offerTo(consumer, "pink_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10444, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8330)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10459)
			.input(Items.field_8330)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10560)
			.input('R', Items.field_8725)
			.input('#', Blocks.field_10445)
			.input('T', ItemTags.field_15537)
			.input('X', Items.field_8620)
			.pattern("TTT")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_redstone", this.method_10426(Items.field_8725))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10289, 4)
			.input('S', Blocks.field_10474)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_stone", this.method_10426(Blocks.field_10474))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10346, 4)
			.input('S', Blocks.field_10508)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_stone", this.method_10426(Blocks.field_10508))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10093, 4)
			.input('S', Blocks.field_10115)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_stone", this.method_10426(Blocks.field_10115))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10135)
			.input('S', Items.field_8662)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_prismarine_shard", this.method_10426(Items.field_8662))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10006)
			.input('S', Items.field_8662)
			.pattern("SSS")
			.pattern("SSS")
			.pattern("SSS")
			.criterion("has_prismarine_shard", this.method_10426(Items.field_8662))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10389, 6)
			.input('#', Blocks.field_10135)
			.pattern("###")
			.criterion("has_prismarine", this.method_10426(Blocks.field_10135))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10236, 6)
			.input('#', Blocks.field_10006)
			.pattern("###")
			.criterion("has_prismarine_bricks", this.method_10426(Blocks.field_10006))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10623, 6)
			.input('#', Blocks.field_10297)
			.pattern("###")
			.criterion("has_dark_prismarine", this.method_10426(Blocks.field_10297))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8741)
			.input(Blocks.field_10261)
			.input(Items.field_8479)
			.input(Items.field_8803)
			.criterion("has_carved_pumpkin", this.method_10426(Blocks.field_10147))
			.criterion("has_pumpkin", this.method_10426(Blocks.field_10261))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8706, 4)
			.input(Blocks.field_10261)
			.criterion("has_pumpkin", this.method_10426(Blocks.field_10261))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8405)
			.input('#', Blocks.field_10259)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_purple_wool", this.method_10426(Blocks.field_10259))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.PURPLE_BED)
			.input('#', Blocks.field_10259)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_purple_wool", this.method_10426(Blocks.field_10259))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.PURPLE_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8296)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "purple_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10510, 3)
			.input('#', Blocks.field_10259)
			.pattern("##")
			.group("carpet")
			.criterion("has_purple_wool", this.method_10426(Blocks.field_10259))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10510, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8296)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_purple_dye", this.method_10426(Items.field_8296))
			.offerTo(consumer, "purple_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10404, 8)
			.input(Items.field_8296)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8296, 2)
			.input(Items.field_8345)
			.input(Items.field_8264)
			.criterion("has_blue_dye", this.method_10426(Items.field_8345))
			.criterion("has_red_dye", this.method_10426(Items.field_8264))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10603)
			.input('#', Blocks.field_10034)
			.input('-', Items.field_8815)
			.pattern("-")
			.pattern("#")
			.pattern("-")
			.criterion("has_shulker_shell", this.method_10426(Items.field_8815))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10399, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8296)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10152, 16)
			.input('#', Blocks.field_10399)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10152, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8296)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_purple_dye", this.method_10426(Items.field_8296))
			.offerTo(consumer, "purple_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10570, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8296)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10259)
			.input(Items.field_8296)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10286, 4)
			.input('F', Items.field_8882)
			.pattern("FF")
			.pattern("FF")
			.criterion("has_chorus_fruit_popped", this.method_10426(Items.field_8882))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10505)
			.input('#', Blocks.field_10175)
			.pattern("#")
			.pattern("#")
			.criterion("has_purpur_block", this.method_10426(Blocks.field_10286))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10175, 6)
			.input('#', Ingredient.ofItems(Blocks.field_10286, Blocks.field_10505))
			.pattern("###")
			.criterion("has_purpur_block", this.method_10426(Blocks.field_10286))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9992, 4)
			.input('#', Ingredient.ofItems(Blocks.field_10286, Blocks.field_10505))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_purpur_block", this.method_10426(Blocks.field_10286))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10153)
			.input('#', Items.field_8155)
			.pattern("##")
			.pattern("##")
			.criterion("has_quartz", this.method_10426(Items.field_8155))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10237, 6)
			.input('#', Ingredient.ofItems(Blocks.field_10044, Blocks.field_10153, Blocks.field_10437))
			.pattern("###")
			.criterion("has_chiseled_quartz_block", this.method_10426(Blocks.field_10044))
			.criterion("has_quartz_block", this.method_10426(Blocks.field_10153))
			.criterion("has_quartz_pillar", this.method_10426(Blocks.field_10437))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10451, 4)
			.input('#', Ingredient.ofItems(Blocks.field_10044, Blocks.field_10153, Blocks.field_10437))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_chiseled_quartz_block", this.method_10426(Blocks.field_10044))
			.criterion("has_quartz_block", this.method_10426(Blocks.field_10153))
			.criterion("has_quartz_pillar", this.method_10426(Blocks.field_10437))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8308)
			.input(Items.field_8512)
			.input(Items.field_8752)
			.input(Items.field_8428)
			.input(Items.field_8179)
			.input(Blocks.field_10251)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", this.method_10426(Items.field_8752))
			.offerTo(consumer, "rabbit_stew_from_brown_mushroom");
		ShapelessRecipeJsonFactory.create(Items.field_8308)
			.input(Items.field_8512)
			.input(Items.field_8752)
			.input(Items.field_8428)
			.input(Items.field_8179)
			.input(Blocks.field_10559)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", this.method_10426(Items.field_8752))
			.offerTo(consumer, "rabbit_stew_from_red_mushroom");
		ShapedRecipeJsonFactory.create(Blocks.field_10167, 16)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("X X")
			.pattern("X#X")
			.pattern("X X")
			.criterion("has_minecart", this.method_10426(Items.field_8045))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8725, 9)
			.input(Blocks.field_10002)
			.criterion("has_redstone_block", this.method_10426(Blocks.field_10002))
			.criterion("has_at_least_9_redstone", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8725))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10002)
			.input('#', Items.field_8725)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_redstone", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8725))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10524)
			.input('R', Items.field_8725)
			.input('G', Blocks.field_10171)
			.pattern(" R ")
			.pattern("RGR")
			.pattern(" R ")
			.criterion("has_glowstone", this.method_10426(Blocks.field_10171))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10523)
			.input('#', Items.field_8600)
			.input('X', Items.field_8725)
			.pattern("X")
			.pattern("#")
			.criterion("has_redstone", this.method_10426(Items.field_8725))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8586)
			.input('#', Blocks.field_10314)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_red_wool", this.method_10426(Blocks.field_10314))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.RED_BED)
			.input('#', Blocks.field_10314)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_red_wool", this.method_10426(Blocks.field_10314))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.RED_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8264)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "red_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10536, 3)
			.input('#', Blocks.field_10314)
			.pattern("##")
			.group("carpet")
			.criterion("has_red_wool", this.method_10426(Blocks.field_10314))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10536, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8264)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_red_dye", this.method_10426(Items.field_8264))
			.offerTo(consumer, "red_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10287, 8)
			.input(Items.field_8264)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8264)
			.input(Items.field_8186)
			.group("red_dye")
			.criterion("has_beetroot", this.method_10426(Items.field_8186))
			.offerTo(consumer, "red_dye_from_beetroot");
		ShapelessRecipeJsonFactory.create(Items.field_8264)
			.input(Blocks.field_10449)
			.group("red_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.field_10449))
			.offerTo(consumer, "red_dye_from_poppy");
		ShapelessRecipeJsonFactory.create(Items.field_8264, 2)
			.input(Blocks.field_10430)
			.group("red_dye")
			.criterion("has_double_plant", this.method_10426(Blocks.field_10430))
			.offerTo(consumer, "red_dye_from_rose_bush");
		ShapelessRecipeJsonFactory.create(Items.field_8264)
			.input(Blocks.field_10270)
			.group("red_dye")
			.criterion("has_red_flower", this.method_10426(Blocks.field_10270))
			.offerTo(consumer, "red_dye_from_tulip");
		ShapedRecipeJsonFactory.create(Blocks.field_9986)
			.input('W', Items.field_8790)
			.input('N', Items.field_8729)
			.pattern("NW")
			.pattern("WN")
			.criterion("has_nether_wart", this.method_10426(Items.field_8790))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10344)
			.input('#', Blocks.field_10534)
			.pattern("##")
			.pattern("##")
			.criterion("has_sand", this.method_10426(Blocks.field_10534))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10624, 6)
			.input('#', Ingredient.ofItems(Blocks.field_10344, Blocks.field_10117))
			.pattern("###")
			.criterion("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.criterion("has_chiseled_red_sandstone", this.method_10426(Blocks.field_10117))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_18891, 6)
			.input('#', Blocks.field_10518)
			.pattern("###")
			.criterion("has_cut_red_sandstone", this.method_10426(Blocks.field_10518))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10420, 4)
			.input('#', Ingredient.ofItems(Blocks.field_10344, Blocks.field_10117, Blocks.field_10518))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.criterion("has_chiseled_red_sandstone", this.method_10426(Blocks.field_10117))
			.criterion("has_cut_red_sandstone", this.method_10426(Blocks.field_10518))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10272, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8264)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10118, 16)
			.input('#', Blocks.field_10272)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10118, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8264)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_red_dye", this.method_10426(Items.field_8264))
			.offerTo(consumer, "red_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10328, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8264)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10314)
			.input(Items.field_8264)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10450)
			.input('#', Blocks.field_10523)
			.input('X', Items.field_8725)
			.input('I', Blocks.field_10340)
			.pattern("#X#")
			.pattern("III")
			.criterion("has_redstone_torch", this.method_10426(Blocks.field_10523))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9979)
			.input('#', Blocks.field_10102)
			.pattern("##")
			.pattern("##")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10007, 6)
			.input('#', Ingredient.ofItems(Blocks.field_9979, Blocks.field_10292))
			.pattern("###")
			.criterion("has_sandstone", this.method_10426(Blocks.field_9979))
			.criterion("has_chiseled_sandstone", this.method_10426(Blocks.field_10292))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_18890, 6)
			.input('#', Blocks.field_10361)
			.pattern("###")
			.criterion("has_cut_sandstone", this.method_10426(Blocks.field_10361))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10142, 4)
			.input('#', Ingredient.ofItems(Blocks.field_9979, Blocks.field_10292, Blocks.field_10361))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_sandstone", this.method_10426(Blocks.field_9979))
			.criterion("has_chiseled_sandstone", this.method_10426(Blocks.field_10292))
			.criterion("has_cut_sandstone", this.method_10426(Blocks.field_10361))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10174)
			.input('S', Items.field_8662)
			.input('C', Items.field_8434)
			.pattern("SCS")
			.pattern("CCC")
			.pattern("SCS")
			.criterion("has_prismarine_crystals", this.method_10426(Items.field_8434))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8868)
			.input('#', Items.field_8620)
			.pattern(" #")
			.pattern("# ")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8255)
			.input('W', ItemTags.field_15537)
			.input('o', Items.field_8620)
			.pattern("WoW")
			.pattern("WWW")
			.pattern(" W ")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8788, 3)
			.input('#', Items.OAK_PLANKS)
			.input('X', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_oak_planks", this.method_10426(Items.OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8111, 3)
			.input('#', Items.SPRUCE_PLANKS)
			.input('X', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_spruce_planks", this.method_10426(Items.SPRUCE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8422, 3)
			.input('#', Items.BIRCH_PLANKS)
			.input('X', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_birch_planks", this.method_10426(Items.BIRCH_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8203, 3)
			.input('#', Items.ACACIA_PLANKS)
			.input('X', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_acacia_planks", this.method_10426(Items.ACACIA_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8867, 3)
			.input('#', Items.JUNGLE_PLANKS)
			.input('X', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_jungle_planks", this.method_10426(Items.JUNGLE_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8496, 3)
			.input('#', Items.DARK_OAK_PLANKS)
			.input('X', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_dark_oak_planks", this.method_10426(Items.DARK_OAK_PLANKS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10030)
			.input('#', Items.field_8777)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_at_least_9_slime_ball", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8777))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8777, 9)
			.input(Blocks.field_10030)
			.criterion("has_at_least_9_slime_ball", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8777))
			.criterion("has_slime", this.method_10426(Blocks.field_10030))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10518, 4)
			.input('#', Blocks.field_10344)
			.pattern("##")
			.pattern("##")
			.criterion("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10361, 4)
			.input('#', Blocks.field_9979)
			.pattern("##")
			.pattern("##")
			.criterion("has_sandstone", this.method_10426(Blocks.field_9979))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10491)
			.input('#', Items.field_8543)
			.pattern("##")
			.pattern("##")
			.criterion("has_snowball", this.method_10426(Items.field_8543))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10477, 6)
			.input('#', Blocks.field_10491)
			.pattern("###")
			.criterion("has_snowball", this.method_10426(Items.field_8543))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8597)
			.input('#', Items.field_8397)
			.input('X', Items.field_8497)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_melon", this.method_10426(Items.field_8497))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8236, 2)
			.input('#', Items.field_8601)
			.input('X', Items.field_8107)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_glowstone_dust", this.method_10426(Items.field_8601))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10155, 3)
			.input('#', Blocks.field_10037)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", this.method_10426(Blocks.field_10037))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8486)
			.input('#', Blocks.field_9975)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", this.method_10422(Blocks.field_10382))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10066)
			.input(Blocks.field_9975)
			.group("wooden_button")
			.criterion("has_planks", this.method_10426(Blocks.field_9975))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10521, 3)
			.input('#', Blocks.field_9975)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.field_9975))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10020, 3)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_9975)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", this.method_10426(Blocks.field_9975))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10291)
			.input('#', Items.field_8600)
			.input('W', Blocks.field_9975)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", this.method_10426(Blocks.field_9975))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_9975, 4)
			.input(ItemTags.field_15549)
			.group("planks")
			.criterion("has_log", this.method_10420(ItemTags.field_15549))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10332)
			.input('#', Blocks.field_9975)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", this.method_10426(Blocks.field_9975))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10071, 6)
			.input('#', Blocks.field_9975)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", this.method_10426(Blocks.field_9975))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10569, 4)
			.input('#', Blocks.field_9975)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", this.method_10426(Blocks.field_9975))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10323, 2)
			.input('#', Blocks.field_9975)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", this.method_10426(Blocks.field_9975))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8600, 4)
			.input('#', ItemTags.field_15537)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_planks", this.method_10420(ItemTags.field_15537))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8600, 1)
			.input('#', Blocks.field_10211)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_bamboo", this.method_10426(Blocks.field_10211))
			.offerTo(consumer, "stick_from_bamboo_item");
		ShapedRecipeJsonFactory.create(Blocks.field_10615)
			.input('P', Blocks.field_10560)
			.input('S', Items.field_8777)
			.pattern("S")
			.pattern("P")
			.criterion("has_slime_ball", this.method_10426(Items.field_8777))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10056, 4)
			.input('#', Blocks.field_10340)
			.pattern("##")
			.pattern("##")
			.criterion("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8062)
			.input('#', Items.field_8600)
			.input('X', Blocks.field_10445)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10131, 6)
			.input('#', Blocks.field_10056)
			.pattern("###")
			.criterion("has_stone_bricks", this.method_10420(ItemTags.field_15531))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10392, 4)
			.input('#', Blocks.field_10056)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_stone_bricks", this.method_10420(ItemTags.field_15531))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10494)
			.input(Blocks.field_10340)
			.criterion("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8431)
			.input('#', Items.field_8600)
			.input('X', Blocks.field_10445)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8387)
			.input('#', Items.field_8600)
			.input('X', Blocks.field_10445)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10158)
			.input('#', Blocks.field_10340)
			.pattern("##")
			.criterion("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8776)
			.input('#', Items.field_8600)
			.input('X', Blocks.field_10445)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10454, 6)
			.input('#', Blocks.field_10340)
			.pattern("###")
			.criterion("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10136, 6)
			.input('#', Blocks.field_10360)
			.pattern("###")
			.criterion("has_smooth_stone", this.method_10426(Blocks.field_10360))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10596, 4)
			.input('#', Blocks.field_10445)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8528)
			.input('#', Items.field_8600)
			.input('X', Blocks.field_10445)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10446)
			.input('#', Items.field_8276)
			.pattern("##")
			.pattern("##")
			.criterion("has_string", this.method_10426(Items.field_8276))
			.offerTo(consumer, "white_wool_from_string");
		ShapelessRecipeJsonFactory.create(Items.field_8479).input(Blocks.field_10424).criterion("has_reeds", this.method_10426(Blocks.field_10424)).offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10375)
			.input('#', Ingredient.ofItems(Blocks.field_10102, Blocks.field_10534))
			.input('X', Items.field_8054)
			.pattern("X#X")
			.pattern("#X#")
			.pattern("X#X")
			.criterion("has_gunpowder", this.method_10426(Items.field_8054))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8069)
			.input('A', Blocks.field_10375)
			.input('B', Items.field_8045)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", this.method_10426(Items.field_8045))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10336, 4)
			.input('#', Items.field_8600)
			.input('X', Ingredient.ofItems(Items.field_8713, Items.field_8665))
			.pattern("X")
			.pattern("#")
			.criterion("has_stone_pickaxe", this.method_10426(Items.field_8387))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16541)
			.input('#', Items.TORCH)
			.input('X', Items.field_8675)
			.pattern("XXX")
			.pattern("X#X")
			.pattern("XXX")
			.criterion("has_iron_nugget", this.method_10426(Items.field_8675))
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10380)
			.input(Blocks.field_10034)
			.input(Blocks.field_10348)
			.criterion("has_tripwire_hook", this.method_10426(Blocks.field_10348))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10348, 2)
			.input('#', ItemTags.field_15537)
			.input('S', Items.field_8600)
			.input('I', Items.field_8620)
			.pattern("I")
			.pattern("S")
			.pattern("#")
			.criterion("has_string", this.method_10426(Items.field_8276))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8090)
			.input('X', Items.field_8161)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_scute", this.method_10426(Items.field_8161))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8861, 9)
			.input(Blocks.field_10359)
			.criterion("has_at_least_9_wheat", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8861))
			.criterion("has_hay_block", this.method_10426(Blocks.field_10359))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8539)
			.input('#', Blocks.field_10446)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.WHITE_BED)
			.input('#', Blocks.field_10446)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10466, 3)
			.input('#', Blocks.field_10446)
			.pattern("##")
			.group("carpet")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10197, 8)
			.input(Items.field_8446)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8446)
			.input(Items.field_8324)
			.group("white_dye")
			.criterion("has_bone_meal", this.method_10426(Items.field_8324))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8446)
			.input(Blocks.field_10548)
			.group("white_dye")
			.criterion("has_white_flower", this.method_10426(Blocks.field_10548))
			.offerTo(consumer, "white_dye_from_lily_of_the_valley");
		ShapedRecipeJsonFactory.create(Blocks.field_10087, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8446)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9991, 16)
			.input('#', Blocks.field_10087)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9991, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8446)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_white_dye", this.method_10426(Items.field_8446))
			.offerTo(consumer, "white_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10611, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8446)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8406)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_15537)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_stick", this.method_10426(Items.field_8600))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10149, 3)
			.input('#', Blocks.field_10161)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", this.method_10426(Blocks.field_10161))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8167)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_15537)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_stick", this.method_10426(Items.field_8600))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8647)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_15537)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_stick", this.method_10426(Items.field_8600))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8876)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_15537)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_stick", this.method_10426(Items.field_8600))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8091)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_15537)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_stick", this.method_10426(Items.field_8600))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8674)
			.input(Items.field_8529)
			.input(Items.field_8794)
			.input(Items.field_8153)
			.criterion("has_book", this.method_10426(Items.field_8529))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8049)
			.input('#', Blocks.field_10490)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_yellow_wool", this.method_10426(Blocks.field_10490))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.YELLOW_BED)
			.input('#', Blocks.field_10490)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_yellow_wool", this.method_10426(Blocks.field_10490))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.YELLOW_BED)
			.input(Items.WHITE_BED)
			.input(Items.field_8192)
			.group("dyed_bed")
			.criterion("has_bed", this.method_10426(Items.WHITE_BED))
			.offerTo(consumer, "yellow_bed_from_white_bed");
		ShapedRecipeJsonFactory.create(Blocks.field_10512, 3)
			.input('#', Blocks.field_10490)
			.pattern("##")
			.group("carpet")
			.criterion("has_yellow_wool", this.method_10426(Blocks.field_10490))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10512, 8)
			.input('#', Blocks.field_10466)
			.input('$', Items.field_8192)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", this.method_10426(Blocks.field_10466))
			.criterion("has_yellow_dye", this.method_10426(Items.field_8192))
			.offerTo(consumer, "yellow_carpet_from_white_carpet");
		ShapelessRecipeJsonFactory.create(Blocks.field_10145, 8)
			.input(Items.field_8192)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", this.method_10426(Blocks.field_10102))
			.criterion("has_gravel", this.method_10426(Blocks.field_10255))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8192)
			.input(Blocks.field_10182)
			.group("yellow_dye")
			.criterion("has_yellow_flower", this.method_10426(Blocks.field_10182))
			.offerTo(consumer, "yellow_dye_from_dandelion");
		ShapelessRecipeJsonFactory.create(Items.field_8192, 2)
			.input(Blocks.field_10583)
			.group("yellow_dye")
			.criterion("has_double_plant", this.method_10426(Blocks.field_10583))
			.offerTo(consumer, "yellow_dye_from_sunflower");
		ShapedRecipeJsonFactory.create(Blocks.field_10049, 8)
			.input('#', Blocks.field_10033)
			.input('X', Items.field_8192)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10578, 16)
			.input('#', Blocks.field_10049)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", this.method_10426(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10578, 8)
			.input('#', Blocks.field_10285)
			.input('$', Items.field_8192)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", this.method_10426(Blocks.field_10285))
			.criterion("has_yellow_dye", this.method_10426(Items.field_8192))
			.offerTo(consumer, "yellow_stained_glass_pane_from_glass_pane");
		ShapedRecipeJsonFactory.create(Blocks.field_10143, 8)
			.input('#', Blocks.field_10415)
			.input('X', Items.field_8192)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", this.method_10426(Blocks.field_10415))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10490)
			.input(Items.field_8192)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", this.method_10426(Blocks.field_10446))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8551, 9)
			.input(Blocks.field_10342)
			.criterion("has_at_least_9_dried_kelp", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8551))
			.criterion("has_dried_kelp_block", this.method_10426(Blocks.field_10342))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10342)
			.input(Items.field_8551, 9)
			.criterion("has_at_least_9_dried_kelp", this.method_10424(NumberRange.IntRange.atLeast(9), Items.field_8551))
			.criterion("has_dried_kelp_block", this.method_10426(Blocks.field_10342))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10502)
			.input('#', Items.field_8864)
			.input('X', Items.field_8207)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_nautilus_core", this.method_10426(Items.field_8207))
			.criterion("has_nautilus_shell", this.method_10426(Items.field_8864))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10435, 4)
			.input('#', Blocks.field_10289)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_granite", this.method_10426(Blocks.field_10289))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10039, 4)
			.input('#', Blocks.field_10483)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_smooth_red_sandstone", this.method_10426(Blocks.field_10483))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10173, 4)
			.input('#', Blocks.field_10065)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_mossy_stone_bricks", this.method_10426(Blocks.field_10065))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10310, 4)
			.input('#', Blocks.field_10346)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_diorite", this.method_10426(Blocks.field_10346))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10207, 4)
			.input('#', Blocks.field_9989)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_mossy_cobblestone", this.method_10426(Blocks.field_9989))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10012, 4)
			.input('#', Blocks.field_10462)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_end_stone_bricks", this.method_10426(Blocks.field_10462))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10440, 4)
			.input('#', Blocks.field_10340)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10549, 4)
			.input('#', Blocks.field_10467)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_smooth_sandstone", this.method_10426(Blocks.field_10467))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10245, 4)
			.input('#', Blocks.field_9978)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_smooth_quartz", this.method_10426(Blocks.field_9978))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10607, 4)
			.input('#', Blocks.field_10474)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_granite", this.method_10426(Blocks.field_10474))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10386, 4)
			.input('#', Blocks.field_10115)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_andesite", this.method_10426(Blocks.field_10115))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10497, 4)
			.input('#', Blocks.field_9986)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_red_nether_bricks", this.method_10426(Blocks.field_9986))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9994, 4)
			.input('#', Blocks.field_10093)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_andesite", this.method_10426(Blocks.field_10093))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10216, 4)
			.input('#', Blocks.field_10508)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_diorite", this.method_10426(Blocks.field_10508))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10329, 6)
			.input('#', Blocks.field_10289)
			.pattern("###")
			.criterion("has_polished_granite", this.method_10426(Blocks.field_10289))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10283, 6)
			.input('#', Blocks.field_10483)
			.pattern("###")
			.criterion("has_smooth_red_sandstone", this.method_10426(Blocks.field_10483))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10024, 6)
			.input('#', Blocks.field_10065)
			.pattern("###")
			.criterion("has_mossy_stone_bricks", this.method_10426(Blocks.field_10065))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10412, 6)
			.input('#', Blocks.field_10346)
			.pattern("###")
			.criterion("has_polished_diorite", this.method_10426(Blocks.field_10346))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10405, 6)
			.input('#', Blocks.field_9989)
			.pattern("###")
			.criterion("has_mossy_cobblestone", this.method_10426(Blocks.field_9989))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10064, 6)
			.input('#', Blocks.field_10462)
			.pattern("###")
			.criterion("has_end_stone_bricks", this.method_10426(Blocks.field_10462))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10262, 6)
			.input('#', Blocks.field_10467)
			.pattern("###")
			.criterion("has_smooth_sandstone", this.method_10426(Blocks.field_10467))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10601, 6)
			.input('#', Blocks.field_9978)
			.pattern("###")
			.criterion("has_smooth_quartz", this.method_10426(Blocks.field_9978))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10189, 6)
			.input('#', Blocks.field_10474)
			.pattern("###")
			.criterion("has_granite", this.method_10426(Blocks.field_10474))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10016, 6)
			.input('#', Blocks.field_10115)
			.pattern("###")
			.criterion("has_andesite", this.method_10426(Blocks.field_10115))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10478, 6)
			.input('#', Blocks.field_9986)
			.pattern("###")
			.criterion("has_red_nether_bricks", this.method_10426(Blocks.field_9986))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10322, 6)
			.input('#', Blocks.field_10093)
			.pattern("###")
			.criterion("has_polished_andesite", this.method_10426(Blocks.field_10093))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10507, 6)
			.input('#', Blocks.field_10508)
			.pattern("###")
			.criterion("has_diorite", this.method_10426(Blocks.field_10508))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10269, 6)
			.input('#', Blocks.field_10104)
			.pattern("###")
			.pattern("###")
			.criterion("has_bricks", this.method_10426(Blocks.field_10104))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10530, 6)
			.input('#', Blocks.field_10135)
			.pattern("###")
			.pattern("###")
			.criterion("has_prismarine", this.method_10426(Blocks.field_10135))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10413, 6)
			.input('#', Blocks.field_10344)
			.pattern("###")
			.pattern("###")
			.criterion("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10059, 6)
			.input('#', Blocks.field_10065)
			.pattern("###")
			.pattern("###")
			.criterion("has_mossy_stone_bricks", this.method_10426(Blocks.field_10065))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10072, 6)
			.input('#', Blocks.field_10474)
			.pattern("###")
			.pattern("###")
			.criterion("has_granite", this.method_10426(Blocks.field_10474))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10252, 6)
			.input('#', Blocks.field_10056)
			.pattern("###")
			.pattern("###")
			.criterion("has_stone_bricks", this.method_10426(Blocks.field_10056))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10127, 6)
			.input('#', Blocks.field_10266)
			.pattern("###")
			.pattern("###")
			.criterion("has_nether_bricks", this.method_10426(Blocks.field_10266))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10489, 6)
			.input('#', Blocks.field_10115)
			.pattern("###")
			.pattern("###")
			.criterion("has_andesite", this.method_10426(Blocks.field_10115))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10311, 6)
			.input('#', Blocks.field_9986)
			.pattern("###")
			.pattern("###")
			.criterion("has_red_nether_bricks", this.method_10426(Blocks.field_9986))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10630, 6)
			.input('#', Blocks.field_9979)
			.pattern("###")
			.pattern("###")
			.criterion("has_sandstone", this.method_10426(Blocks.field_9979))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10001, 6)
			.input('#', Blocks.field_10462)
			.pattern("###")
			.pattern("###")
			.criterion("has_end_stone_bricks", this.method_10426(Blocks.field_10462))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10517, 6)
			.input('#', Blocks.field_10508)
			.pattern("###")
			.pattern("###")
			.criterion("has_diorite", this.method_10426(Blocks.field_10508))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8573)
			.input(Items.field_8407)
			.input(Items.CREEPER_HEAD)
			.criterion("has_creeper_head", this.method_10426(Items.CREEPER_HEAD))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8891)
			.input(Items.field_8407)
			.input(Items.WITHER_SKELETON_SKULL)
			.criterion("has_wither_skeleton_skull", this.method_10426(Items.WITHER_SKELETON_SKULL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8498)
			.input(Items.field_8407)
			.input(Blocks.field_10554)
			.criterion("has_oxeye_daisy", this.method_10426(Blocks.field_10554))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8159)
			.input(Items.field_8407)
			.input(Items.field_8367)
			.criterion("has_enchanted_golden_apple", this.method_10426(Items.field_8367))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16492, 6)
			.input('~', Items.field_8276)
			.input('I', Blocks.field_10211)
			.pattern("I~I")
			.pattern("I I")
			.pattern("I I")
			.criterion("has_bamboo", this.method_10426(Blocks.field_10211))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16337)
			.input('I', Items.field_8600)
			.input('-', Blocks.field_10454)
			.input('#', ItemTags.field_15537)
			.pattern("I-I")
			.pattern("# #")
			.criterion("has_stone_slab", this.method_10426(Blocks.field_10454))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16333)
			.input('#', Blocks.field_10360)
			.input('X', Blocks.field_10181)
			.input('I', Items.field_8620)
			.pattern("III")
			.pattern("IXI")
			.pattern("###")
			.criterion("has_smooth_stone", this.method_10426(Blocks.field_10360))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16334)
			.input('#', ItemTags.field_15539)
			.input('X', Blocks.field_10181)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_furnace", this.method_10426(Blocks.field_10181))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16336)
			.input('#', ItemTags.field_15537)
			.input('@', Items.field_8407)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_string", this.method_10426(Items.field_8276))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16329)
			.input('#', ItemTags.field_15537)
			.input('@', Items.field_8620)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", this.method_10426(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16331)
			.input('#', ItemTags.field_15537)
			.input('@', Items.field_8145)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_flint", this.method_10426(Items.field_8145))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16335)
			.input('I', Items.field_8620)
			.input('#', Blocks.field_10340)
			.pattern(" I ")
			.pattern("###")
			.criterion("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer);
		ComplexRecipeJsonFactory.create(RecipeSerializer.ARMOR_DYE).offerTo(consumer, "armor_dye");
		ComplexRecipeJsonFactory.create(RecipeSerializer.BANNER_DUPLICATE).offerTo(consumer, "banner_duplicate");
		ComplexRecipeJsonFactory.create(RecipeSerializer.BOOK_CLONING).offerTo(consumer, "book_cloning");
		ComplexRecipeJsonFactory.create(RecipeSerializer.FIREWORK_ROCKET).offerTo(consumer, "firework_rocket");
		ComplexRecipeJsonFactory.create(RecipeSerializer.FIREWORK_STAR).offerTo(consumer, "firework_star");
		ComplexRecipeJsonFactory.create(RecipeSerializer.FIREWORK_STAR_FADE).offerTo(consumer, "firework_star_fade");
		ComplexRecipeJsonFactory.create(RecipeSerializer.MAP_CLONING).offerTo(consumer, "map_cloning");
		ComplexRecipeJsonFactory.create(RecipeSerializer.MAP_EXTENDING).offerTo(consumer, "map_extending");
		ComplexRecipeJsonFactory.create(RecipeSerializer.SHIELD_DECORATION).offerTo(consumer, "shield_decoration");
		ComplexRecipeJsonFactory.create(RecipeSerializer.SHULKER_BOX).offerTo(consumer, "shulker_box_coloring");
		ComplexRecipeJsonFactory.create(RecipeSerializer.TIPPED_ARROW).offerTo(consumer, "tipped_arrow");
		ComplexRecipeJsonFactory.create(RecipeSerializer.SUSPICIOUS_STEW).offerTo(consumer, "suspicious_stew");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8567), Items.field_8512, 0.35F, 200)
			.criterion("has_potato", this.method_10426(Items.field_8567))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8696), Items.field_8621, 0.3F, 200)
			.criterion("has_clay_ball", this.method_10426(Items.field_8696))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.field_15539), Items.field_8665, 0.15F, 200)
			.criterion("has_log", this.method_10420(ItemTags.field_15539))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8233), Items.field_8882, 0.1F, 200)
			.criterion("has_chorus_fruit", this.method_10426(Items.field_8233))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10418.asItem()), Items.field_8713, 0.1F, 200)
			.criterion("has_coal_ore", this.method_10426(Blocks.field_10418))
			.offerTo(consumer, "coal_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8046), Items.field_8176, 0.35F, 200)
			.criterion("has_beef", this.method_10426(Items.field_8046))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8726), Items.field_8544, 0.35F, 200)
			.criterion("has_chicken", this.method_10426(Items.field_8726))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8429), Items.field_8373, 0.35F, 200)
			.criterion("has_cod", this.method_10426(Items.field_8429))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_9993), Items.field_8551, 0.1F, 200)
			.criterion("has_kelp", this.method_10426(Blocks.field_9993))
			.offerTo(consumer, "dried_kelp_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8209), Items.field_8509, 0.35F, 200)
			.criterion("has_salmon", this.method_10426(Items.field_8209))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8748), Items.field_8347, 0.35F, 200)
			.criterion("has_mutton", this.method_10426(Items.field_8748))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8389), Items.field_8261, 0.35F, 200)
			.criterion("has_porkchop", this.method_10426(Items.field_8389))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8504), Items.field_8752, 0.35F, 200)
			.criterion("has_rabbit", this.method_10426(Items.field_8504))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10442.asItem()), Items.field_8477, 1.0F, 200)
			.criterion("has_diamond_ore", this.method_10426(Blocks.field_10442))
			.offerTo(consumer, "diamond_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10090.asItem()), Items.field_8759, 0.2F, 200)
			.criterion("has_lapis_ore", this.method_10426(Blocks.field_10090))
			.offerTo(consumer, "lapis_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10013.asItem()), Items.field_8687, 1.0F, 200)
			.criterion("has_emerald_ore", this.method_10426(Blocks.field_10013))
			.offerTo(consumer, "emerald_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.field_15532), Blocks.field_10033.asItem(), 0.1F, 200)
			.criterion("has_sand", this.method_10420(ItemTags.field_15532))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10571.asItem()), Items.field_8695, 1.0F, 200)
			.criterion("has_gold_ore", this.method_10426(Blocks.field_10571))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10476.asItem()), Items.field_8131, 0.1F, 200)
			.criterion("has_sea_pickle", this.method_10426(Blocks.field_10476))
			.offerTo(consumer, "lime_dye_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10029.asItem()), Items.field_8408, 1.0F, 200)
			.criterion("has_cactus", this.method_10426(Blocks.field_10029))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(
				Ingredient.ofItems(
					Items.field_8335,
					Items.field_8322,
					Items.field_8825,
					Items.field_8303,
					Items.field_8845,
					Items.field_8862,
					Items.field_8678,
					Items.field_8416,
					Items.field_8753,
					Items.field_8560
				),
				Items.field_8397,
				0.1F,
				200
			)
			.criterion("has_golden_pickaxe", this.method_10426(Items.field_8335))
			.criterion("has_golden_shovel", this.method_10426(Items.field_8322))
			.criterion("has_golden_axe", this.method_10426(Items.field_8825))
			.criterion("has_golden_hoe", this.method_10426(Items.field_8303))
			.criterion("has_golden_sword", this.method_10426(Items.field_8845))
			.criterion("has_golden_helmet", this.method_10426(Items.field_8862))
			.criterion("has_golden_chestplate", this.method_10426(Items.field_8678))
			.criterion("has_golden_leggings", this.method_10426(Items.field_8416))
			.criterion("has_golden_boots", this.method_10426(Items.field_8753))
			.criterion("has_golden_horse_armor", this.method_10426(Items.field_8560))
			.offerTo(consumer, "gold_nugget_from_smelting");
		CookingRecipeJsonFactory.createSmelting(
				Ingredient.ofItems(
					Items.field_8403,
					Items.field_8699,
					Items.field_8475,
					Items.field_8609,
					Items.field_8371,
					Items.field_8743,
					Items.field_8523,
					Items.field_8396,
					Items.field_8660,
					Items.field_8578,
					Items.field_8283,
					Items.field_8873,
					Items.field_8218,
					Items.field_8313
				),
				Items.field_8675,
				0.1F,
				200
			)
			.criterion("has_iron_pickaxe", this.method_10426(Items.field_8403))
			.criterion("has_iron_shovel", this.method_10426(Items.field_8699))
			.criterion("has_iron_axe", this.method_10426(Items.field_8475))
			.criterion("has_iron_hoe", this.method_10426(Items.field_8609))
			.criterion("has_iron_sword", this.method_10426(Items.field_8371))
			.criterion("has_iron_helmet", this.method_10426(Items.field_8743))
			.criterion("has_iron_chestplate", this.method_10426(Items.field_8523))
			.criterion("has_iron_leggings", this.method_10426(Items.field_8396))
			.criterion("has_iron_boots", this.method_10426(Items.field_8660))
			.criterion("has_iron_horse_armor", this.method_10426(Items.field_8578))
			.criterion("has_chainmail_helmet", this.method_10426(Items.field_8283))
			.criterion("has_chainmail_chestplate", this.method_10426(Items.field_8873))
			.criterion("has_chainmail_leggings", this.method_10426(Items.field_8218))
			.criterion("has_chainmail_boots", this.method_10426(Items.field_8313))
			.offerTo(consumer, "iron_nugget_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10212.asItem()), Items.field_8620, 0.7F, 200)
			.criterion("has_iron_ore", this.method_10426(Blocks.field_10212.asItem()))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10460), Blocks.field_10415.asItem(), 0.35F, 200)
			.criterion("has_clay_block", this.method_10426(Blocks.field_10460))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10515), Items.field_8729, 0.1F, 200)
			.criterion("has_netherrack", this.method_10426(Blocks.field_10515))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10213), Items.field_8155, 0.2F, 200)
			.criterion("has_nether_quartz_ore", this.method_10426(Blocks.field_10213))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10080), Items.field_8725, 0.7F, 200)
			.criterion("has_redstone_ore", this.method_10426(Blocks.field_10080))
			.offerTo(consumer, "redstone_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10562), Blocks.field_10258.asItem(), 0.15F, 200)
			.criterion("has_wet_sponge", this.method_10426(Blocks.field_10562))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10445), Blocks.field_10340.asItem(), 0.1F, 200)
			.criterion("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10360.asItem(), 0.1F, 200)
			.criterion("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10467.asItem(), 0.1F, 200)
			.criterion("has_sandstone", this.method_10426(Blocks.field_9979))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10483.asItem(), 0.1F, 200)
			.criterion("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10153), Blocks.field_9978.asItem(), 0.1F, 200)
			.criterion("has_quartz_block", this.method_10426(Blocks.field_10153))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10056), Blocks.field_10416.asItem(), 0.1F, 200)
			.criterion("has_stone_bricks", this.method_10426(Blocks.field_10056))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10626), Blocks.field_10501.asItem(), 0.1F, 200)
			.criterion("has_black_terracotta", this.method_10426(Blocks.field_10626))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10409), Blocks.field_10550.asItem(), 0.1F, 200)
			.criterion("has_blue_terracotta", this.method_10426(Blocks.field_10409))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10123), Blocks.field_10004.asItem(), 0.1F, 200)
			.criterion("has_brown_terracotta", this.method_10426(Blocks.field_10123))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10235), Blocks.field_10078.asItem(), 0.1F, 200)
			.criterion("has_cyan_terracotta", this.method_10426(Blocks.field_10235))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10349), Blocks.field_10220.asItem(), 0.1F, 200)
			.criterion("has_gray_terracotta", this.method_10426(Blocks.field_10349))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10526), Blocks.field_10475.asItem(), 0.1F, 200)
			.criterion("has_green_terracotta", this.method_10426(Blocks.field_10526))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10325), Blocks.field_10345.asItem(), 0.1F, 200)
			.criterion("has_light_blue_terracotta", this.method_10426(Blocks.field_10325))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10590), Blocks.field_10052.asItem(), 0.1F, 200)
			.criterion("has_light_gray_terracotta", this.method_10426(Blocks.field_10590))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10014), Blocks.field_10046.asItem(), 0.1F, 200)
			.criterion("has_lime_terracotta", this.method_10426(Blocks.field_10014))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10015), Blocks.field_10538.asItem(), 0.1F, 200)
			.criterion("has_magenta_terracotta", this.method_10426(Blocks.field_10015))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10184), Blocks.field_10280.asItem(), 0.1F, 200)
			.criterion("has_orange_terracotta", this.method_10426(Blocks.field_10184))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10444), Blocks.field_10567.asItem(), 0.1F, 200)
			.criterion("has_pink_terracotta", this.method_10426(Blocks.field_10444))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10570), Blocks.field_10426.asItem(), 0.1F, 200)
			.criterion("has_purple_terracotta", this.method_10426(Blocks.field_10570))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10328), Blocks.field_10383.asItem(), 0.1F, 200)
			.criterion("has_red_terracotta", this.method_10426(Blocks.field_10328))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10611), Blocks.field_10595.asItem(), 0.1F, 200)
			.criterion("has_white_terracotta", this.method_10426(Blocks.field_10611))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10143), Blocks.field_10096.asItem(), 0.1F, 200)
			.criterion("has_yellow_terracotta", this.method_10426(Blocks.field_10143))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10212.asItem()), Items.field_8620, 0.7F, 100)
			.criterion("has_iron_ore", this.method_10426(Blocks.field_10212.asItem()))
			.offerTo(consumer, "iron_ingot_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10571.asItem()), Items.field_8695, 1.0F, 100)
			.criterion("has_gold_ore", this.method_10426(Blocks.field_10571))
			.offerTo(consumer, "gold_ingot_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10442.asItem()), Items.field_8477, 1.0F, 100)
			.criterion("has_diamond_ore", this.method_10426(Blocks.field_10442))
			.offerTo(consumer, "diamond_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10090.asItem()), Items.field_8759, 0.2F, 100)
			.criterion("has_lapis_ore", this.method_10426(Blocks.field_10090))
			.offerTo(consumer, "lapis_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10080), Items.field_8725, 0.7F, 100)
			.criterion("has_redstone_ore", this.method_10426(Blocks.field_10080))
			.offerTo(consumer, "redstone_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10418.asItem()), Items.field_8713, 0.1F, 100)
			.criterion("has_coal_ore", this.method_10426(Blocks.field_10418))
			.offerTo(consumer, "coal_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10013.asItem()), Items.field_8687, 1.0F, 100)
			.criterion("has_emerald_ore", this.method_10426(Blocks.field_10013))
			.offerTo(consumer, "emerald_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10213), Items.field_8155, 0.2F, 100)
			.criterion("has_nether_quartz_ore", this.method_10426(Blocks.field_10213))
			.offerTo(consumer, "quartz_from_blasting");
		CookingRecipeJsonFactory.createBlasting(
				Ingredient.ofItems(
					Items.field_8335,
					Items.field_8322,
					Items.field_8825,
					Items.field_8303,
					Items.field_8845,
					Items.field_8862,
					Items.field_8678,
					Items.field_8416,
					Items.field_8753,
					Items.field_8560
				),
				Items.field_8397,
				0.1F,
				100
			)
			.criterion("has_golden_pickaxe", this.method_10426(Items.field_8335))
			.criterion("has_golden_shovel", this.method_10426(Items.field_8322))
			.criterion("has_golden_axe", this.method_10426(Items.field_8825))
			.criterion("has_golden_hoe", this.method_10426(Items.field_8303))
			.criterion("has_golden_sword", this.method_10426(Items.field_8845))
			.criterion("has_golden_helmet", this.method_10426(Items.field_8862))
			.criterion("has_golden_chestplate", this.method_10426(Items.field_8678))
			.criterion("has_golden_leggings", this.method_10426(Items.field_8416))
			.criterion("has_golden_boots", this.method_10426(Items.field_8753))
			.criterion("has_golden_horse_armor", this.method_10426(Items.field_8560))
			.offerTo(consumer, "gold_nugget_from_blasting");
		CookingRecipeJsonFactory.createBlasting(
				Ingredient.ofItems(
					Items.field_8403,
					Items.field_8699,
					Items.field_8475,
					Items.field_8609,
					Items.field_8371,
					Items.field_8743,
					Items.field_8523,
					Items.field_8396,
					Items.field_8660,
					Items.field_8578,
					Items.field_8283,
					Items.field_8873,
					Items.field_8218,
					Items.field_8313
				),
				Items.field_8675,
				0.1F,
				100
			)
			.criterion("has_iron_pickaxe", this.method_10426(Items.field_8403))
			.criterion("has_iron_shovel", this.method_10426(Items.field_8699))
			.criterion("has_iron_axe", this.method_10426(Items.field_8475))
			.criterion("has_iron_hoe", this.method_10426(Items.field_8609))
			.criterion("has_iron_sword", this.method_10426(Items.field_8371))
			.criterion("has_iron_helmet", this.method_10426(Items.field_8743))
			.criterion("has_iron_chestplate", this.method_10426(Items.field_8523))
			.criterion("has_iron_leggings", this.method_10426(Items.field_8396))
			.criterion("has_iron_boots", this.method_10426(Items.field_8660))
			.criterion("has_iron_horse_armor", this.method_10426(Items.field_8578))
			.criterion("has_chainmail_helmet", this.method_10426(Items.field_8283))
			.criterion("has_chainmail_chestplate", this.method_10426(Items.field_8873))
			.criterion("has_chainmail_leggings", this.method_10426(Items.field_8218))
			.criterion("has_chainmail_boots", this.method_10426(Items.field_8313))
			.offerTo(consumer, "iron_nugget_from_blasting");
		this.method_17585(consumer, "smoking", RecipeSerializer.SMOKING, 100);
		this.method_17585(consumer, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, 600);
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10454, 2)
			.create("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer, "stone_slab_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10440)
			.create("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer, "stone_stairs_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10056)
			.create("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer, "stone_bricks_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10131, 2)
			.create("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer, "stone_brick_slab_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10392)
			.create("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer, "stone_brick_stairs_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10552)
			.create("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer, "chiseled_stone_bricks_stone_from_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10252)
			.create("has_stone", this.method_10426(Blocks.field_10340))
			.offerTo(consumer, "stone_brick_walls_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10361)
			.create("has_sandstone", this.method_10426(Blocks.field_9979))
			.offerTo(consumer, "cut_sandstone_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10007, 2)
			.create("has_sandstone", this.method_10426(Blocks.field_9979))
			.offerTo(consumer, "sandstone_slab_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_18890, 2)
			.create("has_sandstone", this.method_10426(Blocks.field_9979))
			.offerTo(consumer, "cut_sandstone_slab_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10361), Blocks.field_18890, 2)
			.create("has_cut_sandstone", this.method_10426(Blocks.field_9979))
			.offerTo(consumer, "cut_sandstone_slab_from_cut_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10142)
			.create("has_sandstone", this.method_10426(Blocks.field_9979))
			.offerTo(consumer, "sandstone_stairs_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10630)
			.create("has_sandstone", this.method_10426(Blocks.field_9979))
			.offerTo(consumer, "sandstone_wall_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10292)
			.create("has_sandstone", this.method_10426(Blocks.field_9979))
			.offerTo(consumer, "chiseled_sandstone_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10518)
			.create("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.offerTo(consumer, "cut_red_sandstone_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10624, 2)
			.create("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.offerTo(consumer, "red_sandstone_slab_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_18891, 2)
			.create("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.offerTo(consumer, "cut_red_sandstone_slab_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10518), Blocks.field_18891, 2)
			.create("has_cut_red_sandstone", this.method_10426(Blocks.field_10344))
			.offerTo(consumer, "cut_red_sandstone_slab_from_cut_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10420)
			.create("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.offerTo(consumer, "red_sandstone_stairs_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10413)
			.create("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.offerTo(consumer, "red_sandstone_wall_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10117)
			.create("has_red_sandstone", this.method_10426(Blocks.field_10344))
			.offerTo(consumer, "chiseled_red_sandstone_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10153), Blocks.field_10237, 2)
			.create("has_quartz_block", this.method_10426(Blocks.field_10153))
			.offerTo(consumer, "quartz_slab_from_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10153), Blocks.field_10451)
			.create("has_quartz_block", this.method_10426(Blocks.field_10153))
			.offerTo(consumer, "quartz_stairs_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10153), Blocks.field_10437)
			.create("has_quartz_block", this.method_10426(Blocks.field_10153))
			.offerTo(consumer, "quartz_pillar_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10153), Blocks.field_10044)
			.create("has_quartz_block", this.method_10426(Blocks.field_10153))
			.offerTo(consumer, "chiseled_quartz_block_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10445), Blocks.field_10596)
			.create("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer, "cobblestone_stairs_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10445), Blocks.field_10351, 2)
			.create("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer, "cobblestone_slab_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10445), Blocks.field_10625)
			.create("has_cobblestone", this.method_10426(Blocks.field_10445))
			.offerTo(consumer, "cobblestone_wall_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10056), Blocks.field_10131, 2)
			.create("has_stone_bricks", this.method_10426(Blocks.field_10056))
			.offerTo(consumer, "stone_brick_slab_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10056), Blocks.field_10392)
			.create("has_stone_bricks", this.method_10426(Blocks.field_10056))
			.offerTo(consumer, "stone_brick_stairs_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10056), Blocks.field_10252)
			.create("has_stone_bricks", this.method_10426(Blocks.field_10056))
			.offerTo(consumer, "stone_brick_wall_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10056), Blocks.field_10552)
			.create("has_stone_bricks", this.method_10426(Blocks.field_10056))
			.offerTo(consumer, "chiseled_stone_bricks_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10104), Blocks.field_10191, 2)
			.create("has_bricks", this.method_10426(Blocks.field_10104))
			.offerTo(consumer, "brick_slab_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10104), Blocks.field_10089)
			.create("has_bricks", this.method_10426(Blocks.field_10104))
			.offerTo(consumer, "brick_stairs_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10104), Blocks.field_10269)
			.create("has_bricks", this.method_10426(Blocks.field_10104))
			.offerTo(consumer, "brick_wall_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10266), Blocks.field_10390, 2)
			.create("has_nether_bricks", this.method_10426(Blocks.field_10266))
			.offerTo(consumer, "nether_brick_slab_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10266), Blocks.field_10159)
			.create("has_nether_bricks", this.method_10426(Blocks.field_10266))
			.offerTo(consumer, "nether_brick_stairs_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10266), Blocks.field_10127)
			.create("has_nether_bricks", this.method_10426(Blocks.field_10266))
			.offerTo(consumer, "nether_brick_wall_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9986), Blocks.field_10478, 2)
			.create("has_nether_bricks", this.method_10426(Blocks.field_9986))
			.offerTo(consumer, "red_nether_brick_slab_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9986), Blocks.field_10497)
			.create("has_nether_bricks", this.method_10426(Blocks.field_9986))
			.offerTo(consumer, "red_nether_brick_stairs_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9986), Blocks.field_10311)
			.create("has_nether_bricks", this.method_10426(Blocks.field_9986))
			.offerTo(consumer, "red_nether_brick_wall_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10286), Blocks.field_10175, 2)
			.create("has_purpur_block", this.method_10426(Blocks.field_10286))
			.offerTo(consumer, "purpur_slab_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10286), Blocks.field_9992)
			.create("has_purpur_block", this.method_10426(Blocks.field_10286))
			.offerTo(consumer, "purpur_stairs_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10286), Blocks.field_10505)
			.create("has_purpur_block", this.method_10426(Blocks.field_10286))
			.offerTo(consumer, "purpur_pillar_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10135), Blocks.field_10389, 2)
			.create("has_prismarine", this.method_10426(Blocks.field_10135))
			.offerTo(consumer, "prismarine_slab_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10135), Blocks.field_10350)
			.create("has_prismarine", this.method_10426(Blocks.field_10135))
			.offerTo(consumer, "prismarine_stairs_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10135), Blocks.field_10530)
			.create("has_prismarine", this.method_10426(Blocks.field_10135))
			.offerTo(consumer, "prismarine_wall_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10006), Blocks.field_10236, 2)
			.create("has_prismarine_brick", this.method_10426(Blocks.field_10006))
			.offerTo(consumer, "prismarine_brick_slab_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10006), Blocks.field_10190)
			.create("has_prismarine_brick", this.method_10426(Blocks.field_10006))
			.offerTo(consumer, "prismarine_brick_stairs_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10297), Blocks.field_10623, 2)
			.create("has_dark_prismarine", this.method_10426(Blocks.field_10297))
			.offerTo(consumer, "dark_prismarine_slab_from_dark_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10297), Blocks.field_10130)
			.create("has_dark_prismarine", this.method_10426(Blocks.field_10297))
			.offerTo(consumer, "dark_prismarine_stairs_from_dark_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_10016, 2)
			.create("has_andesite", this.method_10426(Blocks.field_10115))
			.offerTo(consumer, "andesite_slab_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_10386)
			.create("has_andesite", this.method_10426(Blocks.field_10115))
			.offerTo(consumer, "andesite_stairs_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_10489)
			.create("has_andesite", this.method_10426(Blocks.field_10115))
			.offerTo(consumer, "andesite_wall_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_10093)
			.create("has_andesite", this.method_10426(Blocks.field_10115))
			.offerTo(consumer, "polished_andesite_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_10322, 2)
			.create("has_andesite", this.method_10426(Blocks.field_10115))
			.offerTo(consumer, "polished_andesite_slab_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_9994)
			.create("has_andesite", this.method_10426(Blocks.field_10115))
			.offerTo(consumer, "polished_andesite_stairs_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10093), Blocks.field_10322, 2)
			.create("has_polished_andesite", this.method_10426(Blocks.field_10093))
			.offerTo(consumer, "polished_andesite_slab_from_polished_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10093), Blocks.field_9994)
			.create("has_polished_andesite", this.method_10426(Blocks.field_10093))
			.offerTo(consumer, "polished_andesite_stairs_from_polished_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10189, 2)
			.create("has_granite", this.method_10426(Blocks.field_10474))
			.offerTo(consumer, "granite_slab_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10607)
			.create("has_granite", this.method_10426(Blocks.field_10474))
			.offerTo(consumer, "granite_stairs_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10072)
			.create("has_granite", this.method_10426(Blocks.field_10474))
			.offerTo(consumer, "granite_wall_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10289)
			.create("has_granite", this.method_10426(Blocks.field_10474))
			.offerTo(consumer, "polished_granite_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10329, 2)
			.create("has_granite", this.method_10426(Blocks.field_10474))
			.offerTo(consumer, "polished_granite_slab_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10435)
			.create("has_granite", this.method_10426(Blocks.field_10474))
			.offerTo(consumer, "polished_granite_stairs_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10289), Blocks.field_10329, 2)
			.create("has_polished_granite", this.method_10426(Blocks.field_10289))
			.offerTo(consumer, "polished_granite_slab_from_polished_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10289), Blocks.field_10435)
			.create("has_polished_granite", this.method_10426(Blocks.field_10289))
			.offerTo(consumer, "polished_granite_stairs_from_polished_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10507, 2)
			.create("has_diorite", this.method_10426(Blocks.field_10508))
			.offerTo(consumer, "diorite_slab_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10216)
			.create("has_diorite", this.method_10426(Blocks.field_10508))
			.offerTo(consumer, "diorite_stairs_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10517)
			.create("has_diorite", this.method_10426(Blocks.field_10508))
			.offerTo(consumer, "diorite_wall_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10346)
			.create("has_diorite", this.method_10426(Blocks.field_10508))
			.offerTo(consumer, "polished_diorite_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10412, 2)
			.create("has_diorite", this.method_10426(Blocks.field_10346))
			.offerTo(consumer, "polished_diorite_slab_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10310)
			.create("has_diorite", this.method_10426(Blocks.field_10346))
			.offerTo(consumer, "polished_diorite_stairs_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10346), Blocks.field_10412, 2)
			.create("has_polished_diorite", this.method_10426(Blocks.field_10346))
			.offerTo(consumer, "polished_diorite_slab_from_polished_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10346), Blocks.field_10310)
			.create("has_polished_diorite", this.method_10426(Blocks.field_10346))
			.offerTo(consumer, "polished_diorite_stairs_from_polished_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10065), Blocks.field_10024, 2)
			.create("has_mossy_stone_bricks", this.method_10426(Blocks.field_10065))
			.offerTo(consumer, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10065), Blocks.field_10173)
			.create("has_mossy_stone_bricks", this.method_10426(Blocks.field_10065))
			.offerTo(consumer, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10065), Blocks.field_10059)
			.create("has_mossy_stone_bricks", this.method_10426(Blocks.field_10065))
			.offerTo(consumer, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9989), Blocks.field_10405, 2)
			.create("has_mossy_cobblestone", this.method_10426(Blocks.field_9989))
			.offerTo(consumer, "mossy_cobblestone_slab_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9989), Blocks.field_10207)
			.create("has_mossy_cobblestone", this.method_10426(Blocks.field_9989))
			.offerTo(consumer, "mossy_cobblestone_stairs_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9989), Blocks.field_9990)
			.create("has_mossy_cobblestone", this.method_10426(Blocks.field_9989))
			.offerTo(consumer, "mossy_cobblestone_wall_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10467), Blocks.field_10262, 2)
			.create("has_smooth_sandstone", this.method_10426(Blocks.field_10467))
			.offerTo(consumer, "smooth_sandstone_slab_from_smooth_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10467), Blocks.field_10549)
			.create("has_mossy_cobblestone", this.method_10426(Blocks.field_10467))
			.offerTo(consumer, "smooth_sandstone_stairs_from_smooth_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10483), Blocks.field_10283, 2)
			.create("has_smooth_red_sandstone", this.method_10426(Blocks.field_10483))
			.offerTo(consumer, "smooth_red_sandstone_slab_from_smooth_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10483), Blocks.field_10039)
			.create("has_smooth_red_sandstone", this.method_10426(Blocks.field_10483))
			.offerTo(consumer, "smooth_red_sandstone_stairs_from_smooth_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9978), Blocks.field_10601, 2)
			.create("has_smooth_quartz", this.method_10426(Blocks.field_9978))
			.offerTo(consumer, "smooth_quartz_slab_from_smooth_quartz_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9978), Blocks.field_10245)
			.create("has_smooth_quartz", this.method_10426(Blocks.field_9978))
			.offerTo(consumer, "smooth_quartz_stairs_from_smooth_quartz_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10462), Blocks.field_10064, 2)
			.create("has_end_stone_brick", this.method_10426(Blocks.field_10462))
			.offerTo(consumer, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10462), Blocks.field_10012)
			.create("has_end_stone_brick", this.method_10426(Blocks.field_10462))
			.offerTo(consumer, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10462), Blocks.field_10001)
			.create("has_end_stone_brick", this.method_10426(Blocks.field_10462))
			.offerTo(consumer, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10471), Blocks.field_10462)
			.create("has_end_stone", this.method_10426(Blocks.field_10471))
			.offerTo(consumer, "end_stone_bricks_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10471), Blocks.field_10064, 2)
			.create("has_end_stone", this.method_10426(Blocks.field_10471))
			.offerTo(consumer, "end_stone_brick_slab_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10471), Blocks.field_10012)
			.create("has_end_stone", this.method_10426(Blocks.field_10471))
			.offerTo(consumer, "end_stone_brick_stairs_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10471), Blocks.field_10001)
			.create("has_end_stone", this.method_10426(Blocks.field_10471))
			.offerTo(consumer, "end_stone_brick_wall_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10360), Blocks.field_10136, 2)
			.create("has_smooth_stone", this.method_10426(Blocks.field_10360))
			.offerTo(consumer, "smooth_stone_slab_from_smooth_stone_stonecutting");
	}

	private void method_17585(Consumer<RecipeJsonProvider> consumer, String string, CookingRecipeSerializer<?> cookingRecipeSerializer, int i) {
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8046), Items.field_8176, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_beef", this.method_10426(Items.field_8046))
			.offerTo(consumer, "cooked_beef_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8726), Items.field_8544, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_chicken", this.method_10426(Items.field_8726))
			.offerTo(consumer, "cooked_chicken_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8429), Items.field_8373, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_cod", this.method_10426(Items.field_8429))
			.offerTo(consumer, "cooked_cod_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9993), Items.field_8551, 0.1F, i, cookingRecipeSerializer)
			.criterion("has_kelp", this.method_10426(Blocks.field_9993))
			.offerTo(consumer, "dried_kelp_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8209), Items.field_8509, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_salmon", this.method_10426(Items.field_8209))
			.offerTo(consumer, "cooked_salmon_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8748), Items.field_8347, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_mutton", this.method_10426(Items.field_8748))
			.offerTo(consumer, "cooked_mutton_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8389), Items.field_8261, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_porkchop", this.method_10426(Items.field_8389))
			.offerTo(consumer, "cooked_porkchop_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8567), Items.field_8512, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_potato", this.method_10426(Items.field_8567))
			.offerTo(consumer, "baked_potato_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8504), Items.field_8752, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_rabbit", this.method_10426(Items.field_8504))
			.offerTo(consumer, "cooked_rabbit_from_" + string);
	}

	private EnterBlockCriterion.Conditions method_10422(Block block) {
		return new EnterBlockCriterion.Conditions(block, null);
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
