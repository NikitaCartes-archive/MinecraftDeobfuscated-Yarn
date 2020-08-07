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

	public RecipesProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void run(DataCache cache) throws IOException {
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

	private static void saveRecipe(DataCache dataCache, JsonObject jsonObject, Path path) {
		try {
			String string = GSON.toJson((JsonElement)jsonObject);
			String string2 = SHA1.hashUnencodedChars(string).toString();
			if (!Objects.equals(dataCache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
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

			dataCache.updateSha1(path, string2);
		} catch (IOException var18) {
			LOGGER.error("Couldn't save recipe {}", path, var18);
		}
	}

	private static void saveRecipeAdvancement(DataCache dataCache, JsonObject jsonObject, Path path) {
		try {
			String string = GSON.toJson((JsonElement)jsonObject);
			String string2 = SHA1.hashUnencodedChars(string).toString();
			if (!Objects.equals(dataCache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
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

			dataCache.updateSha1(path, string2);
		} catch (IOException var18) {
			LOGGER.error("Couldn't save recipe advancement {}", path, var18);
		}
	}

	private static void generate(Consumer<RecipeJsonProvider> consumer) {
		method_24475(consumer, Blocks.field_10218, ItemTags.field_15525);
		method_24477(consumer, Blocks.field_10148, ItemTags.field_15554);
		method_24477(consumer, Blocks.field_22126, ItemTags.field_21957);
		method_24475(consumer, Blocks.field_10075, ItemTags.field_15546);
		method_24477(consumer, Blocks.field_10334, ItemTags.field_15538);
		method_24477(consumer, Blocks.field_10161, ItemTags.field_15545);
		method_24477(consumer, Blocks.field_9975, ItemTags.field_15549);
		method_24477(consumer, Blocks.field_22127, ItemTags.field_21958);
		method_24476(consumer, Blocks.field_9999, Blocks.field_10533);
		method_24476(consumer, Blocks.field_10307, Blocks.field_10511);
		method_24476(consumer, Blocks.field_10178, Blocks.field_10010);
		method_24476(consumer, Blocks.field_10303, Blocks.field_10306);
		method_24476(consumer, Blocks.field_10126, Blocks.field_10431);
		method_24476(consumer, Blocks.field_10155, Blocks.field_10037);
		method_24476(consumer, Blocks.field_22505, Blocks.field_22118);
		method_24476(consumer, Blocks.field_22503, Blocks.field_22111);
		method_24476(consumer, Blocks.field_10103, Blocks.field_10622);
		method_24476(consumer, Blocks.field_10204, Blocks.field_10366);
		method_24476(consumer, Blocks.field_10374, Blocks.field_10244);
		method_24476(consumer, Blocks.field_10084, Blocks.field_10254);
		method_24476(consumer, Blocks.field_10250, Blocks.field_10519);
		method_24476(consumer, Blocks.field_10558, Blocks.field_10436);
		method_24476(consumer, Blocks.field_22506, Blocks.field_22119);
		method_24476(consumer, Blocks.field_22504, Blocks.field_22112);
		method_24478(consumer, Items.field_8094, Blocks.field_10218);
		method_24478(consumer, Items.field_8442, Blocks.field_10148);
		method_24478(consumer, Items.field_8138, Blocks.field_10075);
		method_24478(consumer, Items.field_8730, Blocks.field_10334);
		method_24478(consumer, Items.field_8533, Blocks.field_10161);
		method_24478(consumer, Items.field_8486, Blocks.field_9975);
		method_24479(consumer, Blocks.field_10278, Blocks.field_10218);
		method_24480(consumer, Blocks.field_10232, Blocks.field_10218);
		method_24481(consumer, Blocks.field_10144, Blocks.field_10218);
		method_24482(consumer, Blocks.field_10457, Blocks.field_10218);
		method_24483(consumer, Blocks.field_10397, Blocks.field_10218);
		method_24484(consumer, Blocks.field_10031, Blocks.field_10218);
		method_24485(consumer, Blocks.field_10256, Blocks.field_10218);
		method_24486(consumer, Blocks.field_10608, Blocks.field_10218);
		method_24883(consumer, Blocks.field_10284, Blocks.field_10218);
		method_24479(consumer, Blocks.field_10417, Blocks.field_10148);
		method_24480(consumer, Blocks.field_10352, Blocks.field_10148);
		method_24481(consumer, Blocks.field_10299, Blocks.field_10148);
		method_24482(consumer, Blocks.field_10513, Blocks.field_10148);
		method_24483(consumer, Blocks.field_10592, Blocks.field_10148);
		method_24484(consumer, Blocks.field_10257, Blocks.field_10148);
		method_24485(consumer, Blocks.field_10408, Blocks.field_10148);
		method_24486(consumer, Blocks.field_10486, Blocks.field_10148);
		method_24883(consumer, Blocks.field_10231, Blocks.field_10148);
		method_24479(consumer, Blocks.field_22100, Blocks.field_22126);
		method_24480(consumer, Blocks.field_22102, Blocks.field_22126);
		method_24481(consumer, Blocks.field_22132, Blocks.field_22126);
		method_24482(consumer, Blocks.field_22096, Blocks.field_22126);
		method_24483(consumer, Blocks.field_22130, Blocks.field_22126);
		method_24484(consumer, Blocks.field_22128, Blocks.field_22126);
		method_24485(consumer, Blocks.field_22098, Blocks.field_22126);
		method_24486(consumer, Blocks.field_22094, Blocks.field_22126);
		method_24883(consumer, Blocks.field_22104, Blocks.field_22126);
		method_24479(consumer, Blocks.field_10493, Blocks.field_10075);
		method_24480(consumer, Blocks.field_10403, Blocks.field_10075);
		method_24481(consumer, Blocks.field_10132, Blocks.field_10075);
		method_24482(consumer, Blocks.field_10196, Blocks.field_10075);
		method_24483(consumer, Blocks.field_10470, Blocks.field_10075);
		method_24484(consumer, Blocks.field_10500, Blocks.field_10075);
		method_24485(consumer, Blocks.field_10616, Blocks.field_10075);
		method_24486(consumer, Blocks.field_10246, Blocks.field_10075);
		method_24883(consumer, Blocks.field_10330, Blocks.field_10075);
		method_24479(consumer, Blocks.field_10553, Blocks.field_10334);
		method_24480(consumer, Blocks.field_10627, Blocks.field_10334);
		method_24481(consumer, Blocks.field_10319, Blocks.field_10334);
		method_24482(consumer, Blocks.field_10041, Blocks.field_10334);
		method_24483(consumer, Blocks.field_10026, Blocks.field_10334);
		method_24484(consumer, Blocks.field_10617, Blocks.field_10334);
		method_24485(consumer, Blocks.field_10122, Blocks.field_10334);
		method_24486(consumer, Blocks.field_10017, Blocks.field_10334);
		method_24883(consumer, Blocks.field_10544, Blocks.field_10334);
		method_24479(consumer, Blocks.field_10057, Blocks.field_10161);
		method_24480(consumer, Blocks.field_10149, Blocks.field_10161);
		method_24481(consumer, Blocks.field_10620, Blocks.field_10161);
		method_24482(consumer, Blocks.field_10188, Blocks.field_10161);
		method_24483(consumer, Blocks.field_10484, Blocks.field_10161);
		method_24484(consumer, Blocks.field_10119, Blocks.field_10161);
		method_24485(consumer, Blocks.field_10563, Blocks.field_10161);
		method_24486(consumer, Blocks.field_10137, Blocks.field_10161);
		method_24883(consumer, Blocks.field_10121, Blocks.field_10161);
		method_24479(consumer, Blocks.field_10066, Blocks.field_9975);
		method_24480(consumer, Blocks.field_10521, Blocks.field_9975);
		method_24481(consumer, Blocks.field_10020, Blocks.field_9975);
		method_24482(consumer, Blocks.field_10291, Blocks.field_9975);
		method_24483(consumer, Blocks.field_10332, Blocks.field_9975);
		method_24484(consumer, Blocks.field_10071, Blocks.field_9975);
		method_24485(consumer, Blocks.field_10569, Blocks.field_9975);
		method_24486(consumer, Blocks.field_10323, Blocks.field_9975);
		method_24883(consumer, Blocks.field_10411, Blocks.field_9975);
		method_24479(consumer, Blocks.field_22101, Blocks.field_22127);
		method_24480(consumer, Blocks.field_22103, Blocks.field_22127);
		method_24481(consumer, Blocks.field_22133, Blocks.field_22127);
		method_24482(consumer, Blocks.field_22097, Blocks.field_22127);
		method_24483(consumer, Blocks.field_22131, Blocks.field_22127);
		method_24484(consumer, Blocks.field_22129, Blocks.field_22127);
		method_24485(consumer, Blocks.field_22099, Blocks.field_22127);
		method_24486(consumer, Blocks.field_22095, Blocks.field_22127);
		method_24883(consumer, Blocks.field_22105, Blocks.field_22127);
		method_24884(consumer, Blocks.field_10146, Items.field_8226);
		method_24885(consumer, Blocks.field_10106, Blocks.field_10146);
		method_24886(consumer, Blocks.field_10106, Items.field_8226);
		method_24887(consumer, Items.BLACK_BED, Blocks.field_10146);
		method_24888(consumer, Items.BLACK_BED, Items.field_8226);
		method_24889(consumer, Items.field_8572, Blocks.field_10146);
		method_24884(consumer, Blocks.field_10514, Items.field_8345);
		method_24885(consumer, Blocks.field_10043, Blocks.field_10514);
		method_24886(consumer, Blocks.field_10043, Items.field_8345);
		method_24887(consumer, Items.BLUE_BED, Blocks.field_10514);
		method_24888(consumer, Items.BLUE_BED, Items.field_8345);
		method_24889(consumer, Items.field_8128, Blocks.field_10514);
		method_24884(consumer, Blocks.field_10113, Items.field_8099);
		method_24885(consumer, Blocks.field_10473, Blocks.field_10113);
		method_24886(consumer, Blocks.field_10473, Items.field_8099);
		method_24887(consumer, Items.BROWN_BED, Blocks.field_10113);
		method_24888(consumer, Items.BROWN_BED, Items.field_8099);
		method_24889(consumer, Items.field_8124, Blocks.field_10113);
		method_24884(consumer, Blocks.field_10619, Items.field_8632);
		method_24885(consumer, Blocks.field_10433, Blocks.field_10619);
		method_24886(consumer, Blocks.field_10433, Items.field_8632);
		method_24887(consumer, Items.CYAN_BED, Blocks.field_10619);
		method_24888(consumer, Items.CYAN_BED, Items.field_8632);
		method_24889(consumer, Items.field_8629, Blocks.field_10619);
		method_24884(consumer, Blocks.field_10423, Items.field_8298);
		method_24885(consumer, Blocks.field_10591, Blocks.field_10423);
		method_24886(consumer, Blocks.field_10591, Items.field_8298);
		method_24887(consumer, Items.GRAY_BED, Blocks.field_10423);
		method_24888(consumer, Items.GRAY_BED, Items.field_8298);
		method_24889(consumer, Items.field_8617, Blocks.field_10423);
		method_24884(consumer, Blocks.field_10170, Items.field_8408);
		method_24885(consumer, Blocks.field_10338, Blocks.field_10170);
		method_24886(consumer, Blocks.field_10338, Items.field_8408);
		method_24887(consumer, Items.GREEN_BED, Blocks.field_10170);
		method_24888(consumer, Items.GREEN_BED, Items.field_8408);
		method_24889(consumer, Items.field_8295, Blocks.field_10170);
		method_24884(consumer, Blocks.field_10294, Items.field_8273);
		method_24885(consumer, Blocks.field_10290, Blocks.field_10294);
		method_24886(consumer, Blocks.field_10290, Items.field_8273);
		method_24887(consumer, Items.LIGHT_BLUE_BED, Blocks.field_10294);
		method_24888(consumer, Items.LIGHT_BLUE_BED, Items.field_8273);
		method_24889(consumer, Items.field_8379, Blocks.field_10294);
		method_24884(consumer, Blocks.field_10222, Items.field_8851);
		method_24885(consumer, Blocks.field_10209, Blocks.field_10222);
		method_24886(consumer, Blocks.field_10209, Items.field_8851);
		method_24887(consumer, Items.LIGHT_GRAY_BED, Blocks.field_10222);
		method_24888(consumer, Items.LIGHT_GRAY_BED, Items.field_8851);
		method_24889(consumer, Items.field_8855, Blocks.field_10222);
		method_24884(consumer, Blocks.field_10028, Items.field_8131);
		method_24885(consumer, Blocks.field_10040, Blocks.field_10028);
		method_24886(consumer, Blocks.field_10040, Items.field_8131);
		method_24887(consumer, Items.LIME_BED, Blocks.field_10028);
		method_24888(consumer, Items.LIME_BED, Items.field_8131);
		method_24889(consumer, Items.field_8778, Blocks.field_10028);
		method_24884(consumer, Blocks.field_10215, Items.field_8669);
		method_24885(consumer, Blocks.field_10482, Blocks.field_10215);
		method_24886(consumer, Blocks.field_10482, Items.field_8669);
		method_24887(consumer, Items.MAGENTA_BED, Blocks.field_10215);
		method_24888(consumer, Items.MAGENTA_BED, Items.field_8669);
		method_24889(consumer, Items.field_8671, Blocks.field_10215);
		method_24884(consumer, Blocks.field_10095, Items.field_8492);
		method_24885(consumer, Blocks.field_9977, Blocks.field_10095);
		method_24886(consumer, Blocks.field_9977, Items.field_8492);
		method_24887(consumer, Items.ORANGE_BED, Blocks.field_10095);
		method_24888(consumer, Items.ORANGE_BED, Items.field_8492);
		method_24889(consumer, Items.field_8824, Blocks.field_10095);
		method_24884(consumer, Blocks.field_10459, Items.field_8330);
		method_24885(consumer, Blocks.field_10393, Blocks.field_10459);
		method_24886(consumer, Blocks.field_10393, Items.field_8330);
		method_24887(consumer, Items.PINK_BED, Blocks.field_10459);
		method_24888(consumer, Items.PINK_BED, Items.field_8330);
		method_24889(consumer, Items.field_8329, Blocks.field_10459);
		method_24884(consumer, Blocks.field_10259, Items.field_8296);
		method_24885(consumer, Blocks.field_10510, Blocks.field_10259);
		method_24886(consumer, Blocks.field_10510, Items.field_8296);
		method_24887(consumer, Items.PURPLE_BED, Blocks.field_10259);
		method_24888(consumer, Items.PURPLE_BED, Items.field_8296);
		method_24889(consumer, Items.field_8405, Blocks.field_10259);
		method_24884(consumer, Blocks.field_10314, Items.field_8264);
		method_24885(consumer, Blocks.field_10536, Blocks.field_10314);
		method_24886(consumer, Blocks.field_10536, Items.field_8264);
		method_24887(consumer, Items.RED_BED, Blocks.field_10314);
		method_24888(consumer, Items.RED_BED, Items.field_8264);
		method_24889(consumer, Items.field_8586, Blocks.field_10314);
		method_24885(consumer, Blocks.field_10466, Blocks.field_10446);
		method_24887(consumer, Items.WHITE_BED, Blocks.field_10446);
		method_24889(consumer, Items.field_8539, Blocks.field_10446);
		method_24884(consumer, Blocks.field_10490, Items.field_8192);
		method_24885(consumer, Blocks.field_10512, Blocks.field_10490);
		method_24886(consumer, Blocks.field_10512, Items.field_8192);
		method_24887(consumer, Items.YELLOW_BED, Blocks.field_10490);
		method_24888(consumer, Items.YELLOW_BED, Items.field_8192);
		method_24889(consumer, Items.field_8049, Blocks.field_10490);
		method_24890(consumer, Blocks.field_9997, Items.field_8226);
		method_24891(consumer, Blocks.field_10070, Blocks.field_9997);
		method_24892(consumer, Blocks.field_10070, Items.field_8226);
		method_24890(consumer, Blocks.field_10060, Items.field_8345);
		method_24891(consumer, Blocks.field_9982, Blocks.field_10060);
		method_24892(consumer, Blocks.field_9982, Items.field_8345);
		method_24890(consumer, Blocks.field_10073, Items.field_8099);
		method_24891(consumer, Blocks.field_10163, Blocks.field_10073);
		method_24892(consumer, Blocks.field_10163, Items.field_8099);
		method_24890(consumer, Blocks.field_10248, Items.field_8632);
		method_24891(consumer, Blocks.field_10355, Blocks.field_10248);
		method_24892(consumer, Blocks.field_10355, Items.field_8632);
		method_24890(consumer, Blocks.field_10555, Items.field_8298);
		method_24891(consumer, Blocks.field_10077, Blocks.field_10555);
		method_24892(consumer, Blocks.field_10077, Items.field_8298);
		method_24890(consumer, Blocks.field_10357, Items.field_8408);
		method_24891(consumer, Blocks.field_10419, Blocks.field_10357);
		method_24892(consumer, Blocks.field_10419, Items.field_8408);
		method_24890(consumer, Blocks.field_10271, Items.field_8273);
		method_24891(consumer, Blocks.field_10193, Blocks.field_10271);
		method_24892(consumer, Blocks.field_10193, Items.field_8273);
		method_24890(consumer, Blocks.field_9996, Items.field_8851);
		method_24891(consumer, Blocks.field_10129, Blocks.field_9996);
		method_24892(consumer, Blocks.field_10129, Items.field_8851);
		method_24890(consumer, Blocks.field_10157, Items.field_8131);
		method_24891(consumer, Blocks.field_10305, Blocks.field_10157);
		method_24892(consumer, Blocks.field_10305, Items.field_8131);
		method_24890(consumer, Blocks.field_10574, Items.field_8669);
		method_24891(consumer, Blocks.field_10469, Blocks.field_10574);
		method_24892(consumer, Blocks.field_10469, Items.field_8669);
		method_24890(consumer, Blocks.field_10227, Items.field_8492);
		method_24891(consumer, Blocks.field_10496, Blocks.field_10227);
		method_24892(consumer, Blocks.field_10496, Items.field_8492);
		method_24890(consumer, Blocks.field_10317, Items.field_8330);
		method_24891(consumer, Blocks.field_10565, Blocks.field_10317);
		method_24892(consumer, Blocks.field_10565, Items.field_8330);
		method_24890(consumer, Blocks.field_10399, Items.field_8296);
		method_24891(consumer, Blocks.field_10152, Blocks.field_10399);
		method_24892(consumer, Blocks.field_10152, Items.field_8296);
		method_24890(consumer, Blocks.field_10272, Items.field_8264);
		method_24891(consumer, Blocks.field_10118, Blocks.field_10272);
		method_24892(consumer, Blocks.field_10118, Items.field_8264);
		method_24890(consumer, Blocks.field_10087, Items.field_8446);
		method_24891(consumer, Blocks.field_9991, Blocks.field_10087);
		method_24892(consumer, Blocks.field_9991, Items.field_8446);
		method_24890(consumer, Blocks.field_10049, Items.field_8192);
		method_24891(consumer, Blocks.field_10578, Blocks.field_10049);
		method_24892(consumer, Blocks.field_10578, Items.field_8192);
		method_24893(consumer, Blocks.field_10626, Items.field_8226);
		method_24893(consumer, Blocks.field_10409, Items.field_8345);
		method_24893(consumer, Blocks.field_10123, Items.field_8099);
		method_24893(consumer, Blocks.field_10235, Items.field_8632);
		method_24893(consumer, Blocks.field_10349, Items.field_8298);
		method_24893(consumer, Blocks.field_10526, Items.field_8408);
		method_24893(consumer, Blocks.field_10325, Items.field_8273);
		method_24893(consumer, Blocks.field_10590, Items.field_8851);
		method_24893(consumer, Blocks.field_10014, Items.field_8131);
		method_24893(consumer, Blocks.field_10015, Items.field_8669);
		method_24893(consumer, Blocks.field_10184, Items.field_8492);
		method_24893(consumer, Blocks.field_10444, Items.field_8330);
		method_24893(consumer, Blocks.field_10570, Items.field_8296);
		method_24893(consumer, Blocks.field_10328, Items.field_8264);
		method_24893(consumer, Blocks.field_10611, Items.field_8446);
		method_24893(consumer, Blocks.field_10143, Items.field_8192);
		method_24894(consumer, Blocks.field_10506, Items.field_8226);
		method_24894(consumer, Blocks.field_10456, Items.field_8345);
		method_24894(consumer, Blocks.field_10023, Items.field_8099);
		method_24894(consumer, Blocks.field_10233, Items.field_8632);
		method_24894(consumer, Blocks.field_10353, Items.field_8298);
		method_24894(consumer, Blocks.field_10529, Items.field_8408);
		method_24894(consumer, Blocks.field_10321, Items.field_8273);
		method_24894(consumer, Blocks.field_10628, Items.field_8851);
		method_24894(consumer, Blocks.field_10133, Items.field_8131);
		method_24894(consumer, Blocks.field_10300, Items.field_8669);
		method_24894(consumer, Blocks.field_10022, Items.field_8492);
		method_24894(consumer, Blocks.field_10522, Items.field_8330);
		method_24894(consumer, Blocks.field_10404, Items.field_8296);
		method_24894(consumer, Blocks.field_10287, Items.field_8264);
		method_24894(consumer, Blocks.field_10197, Items.field_8446);
		method_24894(consumer, Blocks.field_10145, Items.field_8192);
		ShapedRecipeJsonFactory.create(Blocks.field_10546, 6)
			.input('#', Blocks.field_10523)
			.input('S', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("XSX")
			.pattern("X#X")
			.pattern("XSX")
			.criterion("has_rail", conditionsFromItem(Blocks.field_10167))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10115, 2)
			.input(Blocks.field_10508)
			.input(Blocks.field_10445)
			.criterion("has_stone", conditionsFromItem(Blocks.field_10508))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10535)
			.input('I', Blocks.field_10085)
			.input('i', Items.field_8620)
			.pattern("III")
			.pattern(" i ")
			.pattern("iii")
			.criterion("has_iron_block", conditionsFromItem(Blocks.field_10085))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8694)
			.input('/', Items.field_8600)
			.input('_', Blocks.field_10136)
			.pattern("///")
			.pattern(" / ")
			.pattern("/_/")
			.criterion("has_stone_slab", conditionsFromItem(Blocks.field_10136))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8107, 4)
			.input('#', Items.field_8600)
			.input('X', Items.field_8145)
			.input('Y', Items.field_8153)
			.pattern("X")
			.pattern("#")
			.pattern("Y")
			.criterion("has_feather", conditionsFromItem(Items.field_8153))
			.criterion("has_flint", conditionsFromItem(Items.field_8145))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16328, 1)
			.input('P', ItemTags.field_15537)
			.input('S', ItemTags.field_15534)
			.pattern("PSP")
			.pattern("P P")
			.pattern("PSP")
			.criterion("has_planks", conditionsFromTag(ItemTags.field_15537))
			.criterion("has_wood_slab", conditionsFromTag(ItemTags.field_15534))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10327)
			.input('S', Items.field_8137)
			.input('G', Blocks.field_10033)
			.input('O', Blocks.field_10540)
			.pattern("GGG")
			.pattern("GSG")
			.pattern("OOO")
			.criterion("has_nether_star", conditionsFromItem(Items.field_8137))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_20422)
			.input('P', ItemTags.field_15537)
			.input('H', Items.field_20414)
			.pattern("PPP")
			.pattern("HHH")
			.pattern("PPP")
			.criterion("has_honeycomb", conditionsFromItem(Items.field_20414))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8515)
			.input(Items.field_8428)
			.input(Items.field_8186, 6)
			.criterion("has_beetroot", conditionsFromItem(Items.field_8186))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8226)
			.input(Items.field_8794)
			.group("black_dye")
			.criterion("has_ink_sac", conditionsFromItem(Items.field_8794))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8226)
			.input(Blocks.field_10606)
			.group("black_dye")
			.criterion("has_black_flower", conditionsFromItem(Blocks.field_10606))
			.offerTo(consumer, "black_dye_from_wither_rose");
		ShapelessRecipeJsonFactory.create(Items.field_8183, 2)
			.input(Items.field_8894)
			.criterion("has_blaze_rod", conditionsFromItem(Items.field_8894))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8345)
			.input(Items.field_8759)
			.group("blue_dye")
			.criterion("has_lapis_lazuli", conditionsFromItem(Items.field_8759))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8345)
			.input(Blocks.field_9995)
			.group("blue_dye")
			.criterion("has_blue_flower", conditionsFromItem(Blocks.field_9995))
			.offerTo(consumer, "blue_dye_from_cornflower");
		ShapedRecipeJsonFactory.create(Blocks.field_10384)
			.input('#', Blocks.field_10225)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_packed_ice", conditionsFromItem(Blocks.field_10225))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10166)
			.input('X', Items.field_8324)
			.pattern("XXX")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_bonemeal", conditionsFromItem(Items.field_8324))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8324, 3)
			.input(Items.field_8606)
			.group("bonemeal")
			.criterion("has_bone", conditionsFromItem(Items.field_8606))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8324, 9)
			.input(Blocks.field_10166)
			.group("bonemeal")
			.criterion("has_bone_block", conditionsFromItem(Blocks.field_10166))
			.offerTo(consumer, "bone_meal_from_bone_block");
		ShapelessRecipeJsonFactory.create(Items.field_8529)
			.input(Items.field_8407, 3)
			.input(Items.field_8745)
			.criterion("has_paper", conditionsFromItem(Items.field_8407))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10504)
			.input('#', ItemTags.field_15537)
			.input('X', Items.field_8529)
			.pattern("###")
			.pattern("XXX")
			.pattern("###")
			.criterion("has_book", conditionsFromItem(Items.field_8529))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8102)
			.input('#', Items.field_8600)
			.input('X', Items.field_8276)
			.pattern(" #X")
			.pattern("# X")
			.pattern(" #X")
			.criterion("has_string", conditionsFromItem(Items.field_8276))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8428, 4)
			.input('#', ItemTags.field_15537)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brown_mushroom", conditionsFromItem(Blocks.field_10251))
			.criterion("has_red_mushroom", conditionsFromItem(Blocks.field_10559))
			.criterion("has_mushroom_stew", conditionsFromItem(Items.field_8208))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8229)
			.input('#', Items.field_8861)
			.pattern("###")
			.criterion("has_wheat", conditionsFromItem(Items.field_8861))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10333)
			.input('B', Items.field_8894)
			.input('#', ItemTags.field_25808)
			.pattern(" B ")
			.pattern("###")
			.criterion("has_blaze_rod", conditionsFromItem(Items.field_8894))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10104)
			.input('#', Items.field_8621)
			.pattern("##")
			.pattern("##")
			.criterion("has_brick", conditionsFromItem(Items.field_8621))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10191, 6)
			.input('#', Blocks.field_10104)
			.pattern("###")
			.criterion("has_brick_block", conditionsFromItem(Blocks.field_10104))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10089, 4)
			.input('#', Blocks.field_10104)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_brick_block", conditionsFromItem(Blocks.field_10104))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8099)
			.input(Items.field_8116)
			.group("brown_dye")
			.criterion("has_cocoa_beans", conditionsFromItem(Items.field_8116))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8550)
			.input('#', Items.field_8620)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10183)
			.input('A', Items.field_8103)
			.input('B', Items.field_8479)
			.input('C', Items.field_8861)
			.input('E', Items.field_8803)
			.pattern("AAA")
			.pattern("BEB")
			.pattern("CCC")
			.criterion("has_egg", conditionsFromItem(Items.field_8803))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_17350)
			.input('L', ItemTags.field_15539)
			.input('S', Items.field_8600)
			.input('C', ItemTags.field_17487)
			.pattern(" S ")
			.pattern("SCS")
			.pattern("LLL")
			.criterion("has_stick", conditionsFromItem(Items.field_8600))
			.criterion("has_coal", conditionsFromTag(ItemTags.field_17487))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8184)
			.input('#', Items.field_8378)
			.input('X', Items.field_8179)
			.pattern("# ")
			.pattern(" X")
			.criterion("has_carrot", conditionsFromItem(Items.field_8179))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_23254)
			.input('#', Items.field_8378)
			.input('X', Items.WARPED_FUNGUS)
			.pattern("# ")
			.pattern(" X")
			.criterion("has_warped_fungus", conditionsFromItem(Items.WARPED_FUNGUS))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10593)
			.input('#', Items.field_8620)
			.pattern("# #")
			.pattern("# #")
			.pattern("###")
			.criterion("has_water_bucket", conditionsFromItem(Items.field_8705))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_17563)
			.input('#', ItemTags.field_15534)
			.pattern("# #")
			.pattern("# #")
			.pattern("###")
			.criterion("has_wood_slab", conditionsFromTag(ItemTags.field_15534))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10034)
			.input('#', ItemTags.field_15537)
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
		ShapedRecipeJsonFactory.create(Items.field_8388)
			.input('A', Blocks.field_10034)
			.input('B', Items.field_8045)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", conditionsFromItem(Items.field_8045))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23866)
			.input('#', Blocks.field_10390)
			.pattern("#")
			.pattern("#")
			.criterion("has_nether_bricks", conditionsFromItem(Blocks.field_10266))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10044)
			.input('#', Blocks.field_10237)
			.pattern("#")
			.pattern("#")
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.field_10044))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.field_10437))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10552)
			.input('#', Blocks.field_10131)
			.pattern("#")
			.pattern("#")
			.criterion("has_stone_bricks", conditionsFromTag(ItemTags.field_15531))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10460)
			.input('#', Items.field_8696)
			.pattern("##")
			.pattern("##")
			.criterion("has_clay_ball", conditionsFromItem(Items.field_8696))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8557)
			.input('#', Items.field_8695)
			.input('X', Items.field_8725)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", conditionsFromItem(Items.field_8725))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8713, 9)
			.input(Blocks.field_10381)
			.criterion("has_coal_block", conditionsFromItem(Blocks.field_10381))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10381)
			.input('#', Items.field_8713)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_coal", conditionsFromItem(Items.field_8713))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10253, 4)
			.input('D', Blocks.field_10566)
			.input('G', Blocks.field_10255)
			.pattern("DG")
			.pattern("GD")
			.criterion("has_gravel", conditionsFromItem(Blocks.field_10255))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10351, 6)
			.input('#', Blocks.field_10445)
			.pattern("###")
			.criterion("has_cobblestone", conditionsFromItem(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10625, 6)
			.input('#', Blocks.field_10445)
			.pattern("###")
			.pattern("###")
			.criterion("has_cobblestone", conditionsFromItem(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10377)
			.input('#', Blocks.field_10523)
			.input('X', Items.field_8155)
			.input('I', Blocks.field_10340)
			.pattern(" # ")
			.pattern("#X#")
			.pattern("III")
			.criterion("has_quartz", conditionsFromItem(Items.field_8155))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8251)
			.input('#', Items.field_8620)
			.input('X', Items.field_8725)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_redstone", conditionsFromItem(Items.field_8725))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8423, 8)
			.input('#', Items.field_8861)
			.input('X', Items.field_8116)
			.pattern("#X#")
			.criterion("has_cocoa", conditionsFromItem(Items.field_8116))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9980)
			.input('#', ItemTags.field_15537)
			.pattern("##")
			.pattern("##")
			.criterion("has_planks", conditionsFromTag(ItemTags.field_15537))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8399)
			.input('~', Items.field_8276)
			.input('#', Items.field_8600)
			.input('&', Items.field_8620)
			.input('$', Blocks.field_10348)
			.pattern("#&#")
			.pattern("~$~")
			.pattern(" # ")
			.criterion("has_string", conditionsFromItem(Items.field_8276))
			.criterion("has_stick", conditionsFromItem(Items.field_8600))
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.criterion("has_tripwire_hook", conditionsFromItem(Blocks.field_10348))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10083)
			.input('#', ItemTags.field_15537)
			.input('@', Items.field_8276)
			.pattern("@@")
			.pattern("##")
			.criterion("has_string", conditionsFromItem(Items.field_8276))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10117)
			.input('#', Blocks.field_10624)
			.pattern("#")
			.pattern("#")
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.criterion("has_chiseled_red_sandstone", conditionsFromItem(Blocks.field_10117))
			.criterion("has_cut_red_sandstone", conditionsFromItem(Blocks.field_10518))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10292)
			.input('#', Blocks.field_10007)
			.pattern("#")
			.pattern("#")
			.criterion("has_stone_slab", conditionsFromItem(Blocks.field_10007))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8632, 2)
			.input(Items.field_8345)
			.input(Items.field_8408)
			.criterion("has_green_dye", conditionsFromItem(Items.field_8408))
			.criterion("has_blue_dye", conditionsFromItem(Items.field_8345))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10297)
			.input('S', Items.field_8662)
			.input('I', Items.field_8226)
			.pattern("SSS")
			.pattern("SIS")
			.pattern("SSS")
			.criterion("has_prismarine_shard", conditionsFromItem(Items.field_8662))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10350, 4)
			.input('#', Blocks.field_10135)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_prismarine", conditionsFromItem(Blocks.field_10135))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10190, 4)
			.input('#', Blocks.field_10006)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_prismarine_bricks", conditionsFromItem(Blocks.field_10006))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10130, 4)
			.input('#', Blocks.field_10297)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_dark_prismarine", conditionsFromItem(Blocks.field_10297))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10429)
			.input('Q', Items.field_8155)
			.input('G', Blocks.field_10033)
			.input('W', Ingredient.fromTag(ItemTags.field_15534))
			.pattern("GGG")
			.pattern("QQQ")
			.pattern("WWW")
			.criterion("has_quartz", conditionsFromItem(Items.field_8155))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10025, 6)
			.input('R', Items.field_8725)
			.input('#', Blocks.field_10158)
			.input('X', Items.field_8620)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", conditionsFromItem(Blocks.field_10167))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8477, 9)
			.input(Blocks.field_10201)
			.criterion("has_diamond_block", conditionsFromItem(Blocks.field_10201))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8556)
			.input('#', Items.field_8600)
			.input('X', Items.field_8477)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10201)
			.input('#', Items.field_8477)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8285)
			.input('X', Items.field_8477)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8058)
			.input('X', Items.field_8477)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8805)
			.input('X', Items.field_8477)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8527)
			.input('#', Items.field_8600)
			.input('X', Items.field_8477)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8348)
			.input('X', Items.field_8477)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8377)
			.input('#', Items.field_8600)
			.input('X', Items.field_8477)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8250)
			.input('#', Items.field_8600)
			.input('X', Items.field_8477)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8802)
			.input('#', Items.field_8600)
			.input('X', Items.field_8477)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10508, 2)
			.input('Q', Items.field_8155)
			.input('C', Blocks.field_10445)
			.pattern("CQ")
			.pattern("QC")
			.criterion("has_quartz", conditionsFromItem(Items.field_8155))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10200)
			.input('R', Items.field_8725)
			.input('#', Blocks.field_10445)
			.input('X', Items.field_8102)
			.pattern("###")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_bow", conditionsFromItem(Items.field_8102))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10228)
			.input('R', Items.field_8725)
			.input('#', Blocks.field_10445)
			.pattern("###")
			.pattern("# #")
			.pattern("#R#")
			.criterion("has_redstone", conditionsFromItem(Items.field_8725))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8687, 9)
			.input(Blocks.field_10234)
			.criterion("has_emerald_block", conditionsFromItem(Blocks.field_10234))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10234)
			.input('#', Items.field_8687)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_emerald", conditionsFromItem(Items.field_8687))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10485)
			.input('B', Items.field_8529)
			.input('#', Blocks.field_10540)
			.input('D', Items.field_8477)
			.pattern(" B ")
			.pattern("D#D")
			.pattern("###")
			.criterion("has_obsidian", conditionsFromItem(Blocks.field_10540))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10443)
			.input('#', Blocks.field_10540)
			.input('E', Items.field_8449)
			.pattern("###")
			.pattern("#E#")
			.pattern("###")
			.criterion("has_ender_eye", conditionsFromItem(Items.field_8449))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8449)
			.input(Items.field_8634)
			.input(Items.field_8183)
			.criterion("has_blaze_powder", conditionsFromItem(Items.field_8183))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10462, 4)
			.input('#', Blocks.field_10471)
			.pattern("##")
			.pattern("##")
			.criterion("has_end_stone", conditionsFromItem(Blocks.field_10471))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8301)
			.input('T', Items.field_8070)
			.input('E', Items.field_8449)
			.input('G', Blocks.field_10033)
			.pattern("GGG")
			.pattern("GEG")
			.pattern("GTG")
			.criterion("has_ender_eye", conditionsFromItem(Items.field_8449))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10455, 4)
			.input('#', Items.field_8882)
			.input('/', Items.field_8894)
			.pattern("/")
			.pattern("#")
			.criterion("has_chorus_fruit_popped", conditionsFromItem(Items.field_8882))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8711)
			.input(Items.field_8680)
			.input(Blocks.field_10251)
			.input(Items.field_8479)
			.criterion("has_spider_eye", conditionsFromItem(Items.field_8680))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8814, 3)
			.input(Items.field_8054)
			.input(Items.field_8183)
			.input(Ingredient.ofItems(Items.field_8713, Items.field_8665))
			.criterion("has_blaze_powder", conditionsFromItem(Items.field_8183))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8378)
			.input('#', Items.field_8600)
			.input('X', Items.field_8276)
			.pattern("  #")
			.pattern(" #X")
			.pattern("# X")
			.criterion("has_string", conditionsFromItem(Items.field_8276))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8884)
			.input(Items.field_8620)
			.input(Items.field_8145)
			.criterion("has_flint", conditionsFromItem(Items.field_8145))
			.criterion("has_obsidian", conditionsFromItem(Blocks.field_10540))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10495)
			.input('#', Items.field_8621)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_brick", conditionsFromItem(Items.field_8621))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10181)
			.input('#', ItemTags.field_25808)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.field_25808))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8063)
			.input('A', Blocks.field_10181)
			.input('B', Items.field_8045)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", conditionsFromItem(Items.field_8045))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8469, 3)
			.input('#', Blocks.field_10033)
			.pattern("# #")
			.pattern(" # ")
			.criterion("has_glass", conditionsFromItem(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10285, 16)
			.input('#', Blocks.field_10033)
			.pattern("###")
			.pattern("###")
			.criterion("has_glass", conditionsFromItem(Blocks.field_10033))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10171)
			.input('#', Items.field_8601)
			.pattern("##")
			.pattern("##")
			.criterion("has_glowstone_dust", conditionsFromItem(Items.field_8601))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8463)
			.input('#', Items.field_8695)
			.input('X', Items.field_8279)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8825)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8753)
			.input('X', Items.field_8695)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8071)
			.input('#', Items.field_8397)
			.input('X', Items.field_8179)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_gold_nugget", conditionsFromItem(Items.field_8397))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8678)
			.input('X', Items.field_8695)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8862)
			.input('X', Items.field_8695)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8303)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8416)
			.input('X', Items.field_8695)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8335)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10425, 6)
			.input('R', Items.field_8725)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("X X")
			.pattern("X#X")
			.pattern("XRX")
			.criterion("has_rail", conditionsFromItem(Blocks.field_10167))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8322)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8845)
			.input('#', Items.field_8600)
			.input('X', Items.field_8695)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10205)
			.input('#', Items.field_8695)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8695, 9)
			.input(Blocks.field_10205)
			.group("gold_ingot")
			.criterion("has_gold_block", conditionsFromItem(Blocks.field_10205))
			.offerTo(consumer, "gold_ingot_from_gold_block");
		ShapedRecipeJsonFactory.create(Items.field_8695)
			.input('#', Items.field_8397)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group("gold_ingot")
			.criterion("has_gold_nugget", conditionsFromItem(Items.field_8397))
			.offerTo(consumer, "gold_ingot_from_nuggets");
		ShapelessRecipeJsonFactory.create(Items.field_8397, 9)
			.input(Items.field_8695)
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10474)
			.input(Blocks.field_10508)
			.input(Items.field_8155)
			.criterion("has_quartz", conditionsFromItem(Items.field_8155))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8298, 2)
			.input(Items.field_8226)
			.input(Items.field_8446)
			.criterion("has_white_dye", conditionsFromItem(Items.field_8446))
			.criterion("has_black_dye", conditionsFromItem(Items.field_8226))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10359)
			.input('#', Items.field_8861)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_wheat", conditionsFromItem(Items.field_8861))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10582)
			.input('#', Items.field_8620)
			.pattern("##")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_20417, 4)
			.input(Items.HONEY_BLOCK)
			.input(Items.field_8469, 4)
			.criterion("has_honey_block", conditionsFromItem(Blocks.field_21211))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_21211, 1)
			.input('S', Items.field_20417)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_honey_bottle", conditionsFromItem(Items.field_20417))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_21212)
			.input('H', Items.field_20414)
			.pattern("HH")
			.pattern("HH")
			.criterion("has_honeycomb", conditionsFromItem(Items.field_20414))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10312)
			.input('C', Blocks.field_10034)
			.input('I', Items.field_8620)
			.pattern("I I")
			.pattern("ICI")
			.pattern(" I ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8836)
			.input('A', Blocks.field_10312)
			.input('B', Items.field_8045)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", conditionsFromItem(Items.field_8045))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8475)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10576, 16)
			.input('#', Items.field_8620)
			.pattern("###")
			.pattern("###")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10085)
			.input('#', Items.field_8620)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8660)
			.input('X', Items.field_8620)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8523)
			.input('X', Items.field_8620)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9973, 3)
			.input('#', Items.field_8620)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8743)
			.input('X', Items.field_8620)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8609)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8620, 9)
			.input(Blocks.field_10085)
			.group("iron_ingot")
			.criterion("has_iron_block", conditionsFromItem(Blocks.field_10085))
			.offerTo(consumer, "iron_ingot_from_iron_block");
		ShapedRecipeJsonFactory.create(Items.field_8620)
			.input('#', Items.field_8675)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.group("iron_ingot")
			.criterion("has_iron_nugget", conditionsFromItem(Items.field_8675))
			.offerTo(consumer, "iron_ingot_from_nuggets");
		ShapedRecipeJsonFactory.create(Items.field_8396)
			.input('X', Items.field_8620)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8675, 9)
			.input(Items.field_8620)
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8403)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8699)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8371)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10453)
			.input('#', Items.field_8620)
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8143)
			.input('#', Items.field_8600)
			.input('X', Items.field_8745)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_leather", conditionsFromItem(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10223)
			.input('#', ItemTags.field_15537)
			.input('X', Items.field_8477)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_diamond", conditionsFromItem(Items.field_8477))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9983, 3)
			.input('#', Items.field_8600)
			.pattern("# #")
			.pattern("###")
			.pattern("# #")
			.criterion("has_stick", conditionsFromItem(Items.field_8600))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10441)
			.input('#', Items.field_8759)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_lapis", conditionsFromItem(Items.field_8759))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8759, 9)
			.input(Blocks.field_10441)
			.criterion("has_lapis_block", conditionsFromItem(Blocks.field_10441))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8719, 2)
			.input('~', Items.field_8276)
			.input('O', Items.field_8777)
			.pattern("~~ ")
			.pattern("~O ")
			.pattern("  ~")
			.criterion("has_slime_ball", conditionsFromItem(Items.field_8777))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8745)
			.input('#', Items.field_8245)
			.pattern("##")
			.pattern("##")
			.criterion("has_rabbit_hide", conditionsFromItem(Items.field_8245))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8370)
			.input('X', Items.field_8745)
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8577)
			.input('X', Items.field_8745)
			.pattern("X X")
			.pattern("XXX")
			.pattern("XXX")
			.criterion("has_leather", conditionsFromItem(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8267)
			.input('X', Items.field_8745)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8570)
			.input('X', Items.field_8745)
			.pattern("XXX")
			.pattern("X X")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_18138)
			.input('X', Items.field_8745)
			.pattern("X X")
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_leather", conditionsFromItem(Items.field_8745))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16330)
			.input('S', ItemTags.field_15534)
			.input('B', Blocks.field_10504)
			.pattern("SSS")
			.pattern(" B ")
			.pattern(" S ")
			.criterion("has_book", conditionsFromItem(Items.field_8529))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10363)
			.input('#', Blocks.field_10445)
			.input('X', Items.field_8600)
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", conditionsFromItem(Blocks.field_10445))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8273)
			.input(Blocks.field_10086)
			.group("light_blue_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.field_10086))
			.offerTo(consumer, "light_blue_dye_from_blue_orchid");
		ShapelessRecipeJsonFactory.create(Items.field_8273, 2)
			.input(Items.field_8345)
			.input(Items.field_8446)
			.group("light_blue_dye")
			.criterion("has_blue_dye", conditionsFromItem(Items.field_8345))
			.criterion("has_white_dye", conditionsFromItem(Items.field_8446))
			.offerTo(consumer, "light_blue_dye_from_blue_white_dye");
		ShapelessRecipeJsonFactory.create(Items.field_8851)
			.input(Blocks.field_10573)
			.group("light_gray_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.field_10573))
			.offerTo(consumer, "light_gray_dye_from_azure_bluet");
		ShapelessRecipeJsonFactory.create(Items.field_8851, 2)
			.input(Items.field_8298)
			.input(Items.field_8446)
			.group("light_gray_dye")
			.criterion("has_gray_dye", conditionsFromItem(Items.field_8298))
			.criterion("has_white_dye", conditionsFromItem(Items.field_8446))
			.offerTo(consumer, "light_gray_dye_from_gray_white_dye");
		ShapelessRecipeJsonFactory.create(Items.field_8851, 3)
			.input(Items.field_8226)
			.input(Items.field_8446, 2)
			.group("light_gray_dye")
			.criterion("has_white_dye", conditionsFromItem(Items.field_8446))
			.criterion("has_black_dye", conditionsFromItem(Items.field_8226))
			.offerTo(consumer, "light_gray_dye_from_black_white_dye");
		ShapelessRecipeJsonFactory.create(Items.field_8851)
			.input(Blocks.field_10554)
			.group("light_gray_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.field_10554))
			.offerTo(consumer, "light_gray_dye_from_oxeye_daisy");
		ShapelessRecipeJsonFactory.create(Items.field_8851)
			.input(Blocks.field_10156)
			.group("light_gray_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.field_10156))
			.offerTo(consumer, "light_gray_dye_from_white_tulip");
		ShapedRecipeJsonFactory.create(Blocks.field_10224)
			.input('#', Items.field_8695)
			.pattern("##")
			.criterion("has_gold_ingot", conditionsFromItem(Items.field_8695))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8131, 2)
			.input(Items.field_8408)
			.input(Items.field_8446)
			.criterion("has_green_dye", conditionsFromItem(Items.field_8408))
			.criterion("has_white_dye", conditionsFromItem(Items.field_8446))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10009)
			.input('A', Blocks.field_10147)
			.input('B', Blocks.field_10336)
			.pattern("A")
			.pattern("B")
			.criterion("has_carved_pumpkin", conditionsFromItem(Blocks.field_10147))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8669)
			.input(Blocks.field_10226)
			.group("magenta_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.field_10226))
			.offerTo(consumer, "magenta_dye_from_allium");
		ShapelessRecipeJsonFactory.create(Items.field_8669, 4)
			.input(Items.field_8345)
			.input(Items.field_8264, 2)
			.input(Items.field_8446)
			.group("magenta_dye")
			.criterion("has_blue_dye", conditionsFromItem(Items.field_8345))
			.criterion("has_rose_red", conditionsFromItem(Items.field_8264))
			.criterion("has_white_dye", conditionsFromItem(Items.field_8446))
			.offerTo(consumer, "magenta_dye_from_blue_red_white_dye");
		ShapelessRecipeJsonFactory.create(Items.field_8669, 3)
			.input(Items.field_8345)
			.input(Items.field_8264)
			.input(Items.field_8330)
			.group("magenta_dye")
			.criterion("has_pink_dye", conditionsFromItem(Items.field_8330))
			.criterion("has_blue_dye", conditionsFromItem(Items.field_8345))
			.criterion("has_red_dye", conditionsFromItem(Items.field_8264))
			.offerTo(consumer, "magenta_dye_from_blue_red_pink");
		ShapelessRecipeJsonFactory.create(Items.field_8669, 2)
			.input(Blocks.field_10378)
			.group("magenta_dye")
			.criterion("has_double_plant", conditionsFromItem(Blocks.field_10378))
			.offerTo(consumer, "magenta_dye_from_lilac");
		ShapelessRecipeJsonFactory.create(Items.field_8669, 2)
			.input(Items.field_8296)
			.input(Items.field_8330)
			.group("magenta_dye")
			.criterion("has_pink_dye", conditionsFromItem(Items.field_8330))
			.criterion("has_purple_dye", conditionsFromItem(Items.field_8296))
			.offerTo(consumer, "magenta_dye_from_purple_and_pink");
		ShapedRecipeJsonFactory.create(Blocks.field_10092)
			.input('#', Items.field_8135)
			.pattern("##")
			.pattern("##")
			.criterion("has_magma_cream", conditionsFromItem(Items.field_8135))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8135)
			.input(Items.field_8183)
			.input(Items.field_8777)
			.criterion("has_blaze_powder", conditionsFromItem(Items.field_8183))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8895)
			.input('#', Items.field_8407)
			.input('X', Items.field_8251)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_compass", conditionsFromItem(Items.field_8251))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10545)
			.input('M', Items.field_8497)
			.pattern("MMM")
			.pattern("MMM")
			.pattern("MMM")
			.criterion("has_melon", conditionsFromItem(Items.field_8497))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8188).input(Items.field_8497).criterion("has_melon", conditionsFromItem(Items.field_8497)).offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8045)
			.input('#', Items.field_8620)
			.pattern("# #")
			.pattern("###")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_9989)
			.input(Blocks.field_10445)
			.input(Blocks.field_10597)
			.criterion("has_vine", conditionsFromItem(Blocks.field_10597))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9990, 6)
			.input('#', Blocks.field_9989)
			.pattern("###")
			.pattern("###")
			.criterion("has_mossy_cobblestone", conditionsFromItem(Blocks.field_9989))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10065)
			.input(Blocks.field_10056)
			.input(Blocks.field_10597)
			.criterion("has_mossy_cobblestone", conditionsFromItem(Blocks.field_9989))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8208)
			.input(Blocks.field_10251)
			.input(Blocks.field_10559)
			.input(Items.field_8428)
			.criterion("has_mushroom_stew", conditionsFromItem(Items.field_8208))
			.criterion("has_bowl", conditionsFromItem(Items.field_8428))
			.criterion("has_brown_mushroom", conditionsFromItem(Blocks.field_10251))
			.criterion("has_red_mushroom", conditionsFromItem(Blocks.field_10559))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10266)
			.input('N', Items.field_8729)
			.pattern("NN")
			.pattern("NN")
			.criterion("has_netherbrick", conditionsFromItem(Items.field_8729))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10364, 6)
			.input('#', Blocks.field_10266)
			.input('-', Items.field_8729)
			.pattern("#-#")
			.pattern("#-#")
			.criterion("has_nether_brick", conditionsFromItem(Blocks.field_10266))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10390, 6)
			.input('#', Blocks.field_10266)
			.pattern("###")
			.criterion("has_nether_brick", conditionsFromItem(Blocks.field_10266))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10159, 4)
			.input('#', Blocks.field_10266)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_nether_brick", conditionsFromItem(Blocks.field_10266))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10541)
			.input('#', Items.field_8790)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_nether_wart", conditionsFromItem(Items.field_8790))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10179)
			.input('#', ItemTags.field_15537)
			.input('X', Items.field_8725)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_redstone", conditionsFromItem(Items.field_8725))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10282)
			.input('Q', Items.field_8155)
			.input('R', Items.field_8725)
			.input('#', Blocks.field_10445)
			.pattern("###")
			.pattern("RRQ")
			.pattern("###")
			.criterion("has_quartz", conditionsFromItem(Items.field_8155))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8492)
			.input(Blocks.field_10048)
			.group("orange_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.field_10048))
			.offerTo(consumer, "orange_dye_from_orange_tulip");
		ShapelessRecipeJsonFactory.create(Items.field_8492, 2)
			.input(Items.field_8264)
			.input(Items.field_8192)
			.group("orange_dye")
			.criterion("has_red_dye", conditionsFromItem(Items.field_8264))
			.criterion("has_yellow_dye", conditionsFromItem(Items.field_8192))
			.offerTo(consumer, "orange_dye_from_red_yellow");
		ShapedRecipeJsonFactory.create(Items.field_8892)
			.input('#', Items.field_8600)
			.input('X', Ingredient.fromTag(ItemTags.field_15544))
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_wool", conditionsFromTag(ItemTags.field_15544))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8407, 3)
			.input('#', Blocks.field_10424)
			.pattern("###")
			.criterion("has_reeds", conditionsFromItem(Blocks.field_10424))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10437, 2)
			.input('#', Blocks.field_10153)
			.pattern("#")
			.pattern("#")
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.field_10044))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.field_10437))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10225)
			.input(Blocks.field_10295, 9)
			.criterion("has_ice", conditionsFromItem(Blocks.field_10295))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8330, 2)
			.input(Blocks.field_10003)
			.group("pink_dye")
			.criterion("has_double_plant", conditionsFromItem(Blocks.field_10003))
			.offerTo(consumer, "pink_dye_from_peony");
		ShapelessRecipeJsonFactory.create(Items.field_8330)
			.input(Blocks.field_10315)
			.group("pink_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.field_10315))
			.offerTo(consumer, "pink_dye_from_pink_tulip");
		ShapelessRecipeJsonFactory.create(Items.field_8330, 2)
			.input(Items.field_8264)
			.input(Items.field_8446)
			.group("pink_dye")
			.criterion("has_white_dye", conditionsFromItem(Items.field_8446))
			.criterion("has_red_dye", conditionsFromItem(Items.field_8264))
			.offerTo(consumer, "pink_dye_from_red_white_dye");
		ShapedRecipeJsonFactory.create(Blocks.field_10560)
			.input('R', Items.field_8725)
			.input('#', Blocks.field_10445)
			.input('T', ItemTags.field_15537)
			.input('X', Items.field_8620)
			.pattern("TTT")
			.pattern("#X#")
			.pattern("#R#")
			.criterion("has_redstone", conditionsFromItem(Items.field_8725))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23151, 4)
			.input('S', Blocks.field_22091)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_basalt", conditionsFromItem(Blocks.field_22091))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10289, 4)
			.input('S', Blocks.field_10474)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_stone", conditionsFromItem(Blocks.field_10474))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10346, 4)
			.input('S', Blocks.field_10508)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_stone", conditionsFromItem(Blocks.field_10508))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10093, 4)
			.input('S', Blocks.field_10115)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_stone", conditionsFromItem(Blocks.field_10115))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10135)
			.input('S', Items.field_8662)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_prismarine_shard", conditionsFromItem(Items.field_8662))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10006)
			.input('S', Items.field_8662)
			.pattern("SSS")
			.pattern("SSS")
			.pattern("SSS")
			.criterion("has_prismarine_shard", conditionsFromItem(Items.field_8662))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10389, 6)
			.input('#', Blocks.field_10135)
			.pattern("###")
			.criterion("has_prismarine", conditionsFromItem(Blocks.field_10135))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10236, 6)
			.input('#', Blocks.field_10006)
			.pattern("###")
			.criterion("has_prismarine_bricks", conditionsFromItem(Blocks.field_10006))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10623, 6)
			.input('#', Blocks.field_10297)
			.pattern("###")
			.criterion("has_dark_prismarine", conditionsFromItem(Blocks.field_10297))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8741)
			.input(Blocks.field_10261)
			.input(Items.field_8479)
			.input(Items.field_8803)
			.criterion("has_carved_pumpkin", conditionsFromItem(Blocks.field_10147))
			.criterion("has_pumpkin", conditionsFromItem(Blocks.field_10261))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8706, 4)
			.input(Blocks.field_10261)
			.criterion("has_pumpkin", conditionsFromItem(Blocks.field_10261))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8296, 2)
			.input(Items.field_8345)
			.input(Items.field_8264)
			.criterion("has_blue_dye", conditionsFromItem(Items.field_8345))
			.criterion("has_red_dye", conditionsFromItem(Items.field_8264))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10603)
			.input('#', Blocks.field_10034)
			.input('-', Items.field_8815)
			.pattern("-")
			.pattern("#")
			.pattern("-")
			.criterion("has_shulker_shell", conditionsFromItem(Items.field_8815))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10286, 4)
			.input('F', Items.field_8882)
			.pattern("FF")
			.pattern("FF")
			.criterion("has_chorus_fruit_popped", conditionsFromItem(Items.field_8882))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10505)
			.input('#', Blocks.field_10175)
			.pattern("#")
			.pattern("#")
			.criterion("has_purpur_block", conditionsFromItem(Blocks.field_10286))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10175, 6)
			.input('#', Ingredient.ofItems(Blocks.field_10286, Blocks.field_10505))
			.pattern("###")
			.criterion("has_purpur_block", conditionsFromItem(Blocks.field_10286))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9992, 4)
			.input('#', Ingredient.ofItems(Blocks.field_10286, Blocks.field_10505))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_purpur_block", conditionsFromItem(Blocks.field_10286))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10153)
			.input('#', Items.field_8155)
			.pattern("##")
			.pattern("##")
			.criterion("has_quartz", conditionsFromItem(Items.field_8155))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23868, 4)
			.input('#', Blocks.field_10153)
			.pattern("##")
			.pattern("##")
			.criterion("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10237, 6)
			.input('#', Ingredient.ofItems(Blocks.field_10044, Blocks.field_10153, Blocks.field_10437))
			.pattern("###")
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.field_10044))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.field_10437))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10451, 4)
			.input('#', Ingredient.ofItems(Blocks.field_10044, Blocks.field_10153, Blocks.field_10437))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_chiseled_quartz_block", conditionsFromItem(Blocks.field_10044))
			.criterion("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.criterion("has_quartz_pillar", conditionsFromItem(Blocks.field_10437))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8308)
			.input(Items.field_8512)
			.input(Items.field_8752)
			.input(Items.field_8428)
			.input(Items.field_8179)
			.input(Blocks.field_10251)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", conditionsFromItem(Items.field_8752))
			.offerTo(consumer, "rabbit_stew_from_brown_mushroom");
		ShapelessRecipeJsonFactory.create(Items.field_8308)
			.input(Items.field_8512)
			.input(Items.field_8752)
			.input(Items.field_8428)
			.input(Items.field_8179)
			.input(Blocks.field_10559)
			.group("rabbit_stew")
			.criterion("has_cooked_rabbit", conditionsFromItem(Items.field_8752))
			.offerTo(consumer, "rabbit_stew_from_red_mushroom");
		ShapedRecipeJsonFactory.create(Blocks.field_10167, 16)
			.input('#', Items.field_8600)
			.input('X', Items.field_8620)
			.pattern("X X")
			.pattern("X#X")
			.pattern("X X")
			.criterion("has_minecart", conditionsFromItem(Items.field_8045))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8725, 9)
			.input(Blocks.field_10002)
			.criterion("has_redstone_block", conditionsFromItem(Blocks.field_10002))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10002)
			.input('#', Items.field_8725)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_redstone", conditionsFromItem(Items.field_8725))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10524)
			.input('R', Items.field_8725)
			.input('G', Blocks.field_10171)
			.pattern(" R ")
			.pattern("RGR")
			.pattern(" R ")
			.criterion("has_glowstone", conditionsFromItem(Blocks.field_10171))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10523)
			.input('#', Items.field_8600)
			.input('X', Items.field_8725)
			.pattern("X")
			.pattern("#")
			.criterion("has_redstone", conditionsFromItem(Items.field_8725))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8264)
			.input(Items.field_8186)
			.group("red_dye")
			.criterion("has_beetroot", conditionsFromItem(Items.field_8186))
			.offerTo(consumer, "red_dye_from_beetroot");
		ShapelessRecipeJsonFactory.create(Items.field_8264)
			.input(Blocks.field_10449)
			.group("red_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.field_10449))
			.offerTo(consumer, "red_dye_from_poppy");
		ShapelessRecipeJsonFactory.create(Items.field_8264, 2)
			.input(Blocks.field_10430)
			.group("red_dye")
			.criterion("has_double_plant", conditionsFromItem(Blocks.field_10430))
			.offerTo(consumer, "red_dye_from_rose_bush");
		ShapelessRecipeJsonFactory.create(Items.field_8264)
			.input(Blocks.field_10270)
			.group("red_dye")
			.criterion("has_red_flower", conditionsFromItem(Blocks.field_10270))
			.offerTo(consumer, "red_dye_from_tulip");
		ShapedRecipeJsonFactory.create(Blocks.field_9986)
			.input('W', Items.field_8790)
			.input('N', Items.field_8729)
			.pattern("NW")
			.pattern("WN")
			.criterion("has_nether_wart", conditionsFromItem(Items.field_8790))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10344)
			.input('#', Blocks.field_10534)
			.pattern("##")
			.pattern("##")
			.criterion("has_sand", conditionsFromItem(Blocks.field_10534))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10624, 6)
			.input('#', Ingredient.ofItems(Blocks.field_10344, Blocks.field_10117))
			.pattern("###")
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.criterion("has_chiseled_red_sandstone", conditionsFromItem(Blocks.field_10117))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_18891, 6)
			.input('#', Blocks.field_10518)
			.pattern("###")
			.criterion("has_cut_red_sandstone", conditionsFromItem(Blocks.field_10518))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10420, 4)
			.input('#', Ingredient.ofItems(Blocks.field_10344, Blocks.field_10117, Blocks.field_10518))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.criterion("has_chiseled_red_sandstone", conditionsFromItem(Blocks.field_10117))
			.criterion("has_cut_red_sandstone", conditionsFromItem(Blocks.field_10518))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10450)
			.input('#', Blocks.field_10523)
			.input('X', Items.field_8725)
			.input('I', Blocks.field_10340)
			.pattern("#X#")
			.pattern("III")
			.criterion("has_redstone_torch", conditionsFromItem(Blocks.field_10523))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9979)
			.input('#', Blocks.field_10102)
			.pattern("##")
			.pattern("##")
			.criterion("has_sand", conditionsFromItem(Blocks.field_10102))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10007, 6)
			.input('#', Ingredient.ofItems(Blocks.field_9979, Blocks.field_10292))
			.pattern("###")
			.criterion("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.criterion("has_chiseled_sandstone", conditionsFromItem(Blocks.field_10292))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_18890, 6)
			.input('#', Blocks.field_10361)
			.pattern("###")
			.criterion("has_cut_sandstone", conditionsFromItem(Blocks.field_10361))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10142, 4)
			.input('#', Ingredient.ofItems(Blocks.field_9979, Blocks.field_10292, Blocks.field_10361))
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.criterion("has_chiseled_sandstone", conditionsFromItem(Blocks.field_10292))
			.criterion("has_cut_sandstone", conditionsFromItem(Blocks.field_10361))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10174)
			.input('S', Items.field_8662)
			.input('C', Items.field_8434)
			.pattern("SCS")
			.pattern("CCC")
			.pattern("SCS")
			.criterion("has_prismarine_crystals", conditionsFromItem(Items.field_8434))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8868)
			.input('#', Items.field_8620)
			.pattern(" #")
			.pattern("# ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8255)
			.input('W', ItemTags.field_15537)
			.input('o', Items.field_8620)
			.pattern("WoW")
			.pattern("WWW")
			.pattern(" W ")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10030)
			.input('#', Items.field_8777)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_slime_ball", conditionsFromItem(Items.field_8777))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8777, 9)
			.input(Blocks.field_10030)
			.criterion("has_slime", conditionsFromItem(Blocks.field_10030))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10518, 4)
			.input('#', Blocks.field_10344)
			.pattern("##")
			.pattern("##")
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10361, 4)
			.input('#', Blocks.field_9979)
			.pattern("##")
			.pattern("##")
			.criterion("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10491)
			.input('#', Items.field_8543)
			.pattern("##")
			.pattern("##")
			.criterion("has_snowball", conditionsFromItem(Items.field_8543))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10477, 6)
			.input('#', Blocks.field_10491)
			.pattern("###")
			.criterion("has_snowball", conditionsFromItem(Items.field_8543))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23860)
			.input('L', ItemTags.field_15539)
			.input('S', Items.field_8600)
			.input('#', ItemTags.field_23801)
			.pattern(" S ")
			.pattern("S#S")
			.pattern("LLL")
			.criterion("has_stick", conditionsFromItem(Items.field_8600))
			.criterion("has_soul_sand", conditionsFromTag(ItemTags.field_23801))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8597)
			.input('#', Items.field_8397)
			.input('X', Items.field_8497)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_melon", conditionsFromItem(Items.field_8497))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8236, 2)
			.input('#', Items.field_8601)
			.input('X', Items.field_8107)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_glowstone_dust", conditionsFromItem(Items.field_8601))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8600, 4)
			.input('#', ItemTags.field_15537)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_planks", conditionsFromTag(ItemTags.field_15537))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8600, 1)
			.input('#', Blocks.field_10211)
			.pattern("#")
			.pattern("#")
			.group("sticks")
			.criterion("has_bamboo", conditionsFromItem(Blocks.field_10211))
			.offerTo(consumer, "stick_from_bamboo_item");
		ShapedRecipeJsonFactory.create(Blocks.field_10615)
			.input('P', Blocks.field_10560)
			.input('S', Items.field_8777)
			.pattern("S")
			.pattern("P")
			.criterion("has_slime_ball", conditionsFromItem(Items.field_8777))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10056, 4)
			.input('#', Blocks.field_10340)
			.pattern("##")
			.pattern("##")
			.criterion("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8062)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_23802)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.field_23802))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10131, 6)
			.input('#', Blocks.field_10056)
			.pattern("###")
			.criterion("has_stone_bricks", conditionsFromTag(ItemTags.field_15531))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10392, 4)
			.input('#', Blocks.field_10056)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_stone_bricks", conditionsFromTag(ItemTags.field_15531))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10494)
			.input(Blocks.field_10340)
			.criterion("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8431)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_23802)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.field_23802))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8387)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_23802)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.field_23802))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10158)
			.input('#', Blocks.field_10340)
			.pattern("##")
			.criterion("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8776)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_23802)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.field_23802))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10454, 6)
			.input('#', Blocks.field_10340)
			.pattern("###")
			.criterion("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10136, 6)
			.input('#', Blocks.field_10360)
			.pattern("###")
			.criterion("has_smooth_stone", conditionsFromItem(Blocks.field_10360))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10596, 4)
			.input('#', Blocks.field_10445)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_cobblestone", conditionsFromItem(Blocks.field_10445))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8528)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_23802)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_cobblestone", conditionsFromTag(ItemTags.field_23802))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10446)
			.input('#', Items.field_8276)
			.pattern("##")
			.pattern("##")
			.criterion("has_string", conditionsFromItem(Items.field_8276))
			.offerTo(consumer, "white_wool_from_string");
		ShapelessRecipeJsonFactory.create(Items.field_8479)
			.input(Blocks.field_10424)
			.group("sugar")
			.criterion("has_reeds", conditionsFromItem(Blocks.field_10424))
			.offerTo(consumer, "sugar_from_sugar_cane");
		ShapelessRecipeJsonFactory.create(Items.field_8479, 3)
			.input(Items.field_20417)
			.group("sugar")
			.criterion("has_honey_bottle", conditionsFromItem(Items.field_20417))
			.offerTo(consumer, "sugar_from_honey_bottle");
		ShapedRecipeJsonFactory.create(Blocks.field_22422)
			.input('H', Items.HAY_BLOCK)
			.input('R', Items.field_8725)
			.pattern(" R ")
			.pattern("RHR")
			.pattern(" R ")
			.criterion("has_redstone", conditionsFromItem(Items.field_8725))
			.criterion("has_hay_block", conditionsFromItem(Blocks.field_10359))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10375)
			.input('#', Ingredient.ofItems(Blocks.field_10102, Blocks.field_10534))
			.input('X', Items.field_8054)
			.pattern("X#X")
			.pattern("#X#")
			.pattern("X#X")
			.criterion("has_gunpowder", conditionsFromItem(Items.field_8054))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8069)
			.input('A', Blocks.field_10375)
			.input('B', Items.field_8045)
			.pattern("A")
			.pattern("B")
			.criterion("has_minecart", conditionsFromItem(Items.field_8045))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10336, 4)
			.input('#', Items.field_8600)
			.input('X', Ingredient.ofItems(Items.field_8713, Items.field_8665))
			.pattern("X")
			.pattern("#")
			.criterion("has_stone_pickaxe", conditionsFromItem(Items.field_8387))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_22092, 4)
			.input('X', Ingredient.ofItems(Items.field_8713, Items.field_8665))
			.input('#', Items.field_8600)
			.input('S', ItemTags.field_23801)
			.pattern("X")
			.pattern("#")
			.pattern("S")
			.criterion("has_soul_sand", conditionsFromTag(ItemTags.field_23801))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16541)
			.input('#', Items.TORCH)
			.input('X', Items.field_8675)
			.pattern("XXX")
			.pattern("X#X")
			.pattern("XXX")
			.criterion("has_iron_nugget", conditionsFromItem(Items.field_8675))
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_22110)
			.input('#', Items.SOUL_TORCH)
			.input('X', Items.field_8675)
			.pattern("XXX")
			.pattern("X#X")
			.pattern("XXX")
			.criterion("has_soul_torch", conditionsFromItem(Items.SOUL_TORCH))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10380)
			.input(Blocks.field_10034)
			.input(Blocks.field_10348)
			.criterion("has_tripwire_hook", conditionsFromItem(Blocks.field_10348))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10348, 2)
			.input('#', ItemTags.field_15537)
			.input('S', Items.field_8600)
			.input('I', Items.field_8620)
			.pattern("I")
			.pattern("S")
			.pattern("#")
			.criterion("has_string", conditionsFromItem(Items.field_8276))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8090)
			.input('X', Items.field_8161)
			.pattern("XXX")
			.pattern("X X")
			.criterion("has_scute", conditionsFromItem(Items.field_8161))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8861, 9)
			.input(Blocks.field_10359)
			.criterion("has_hay_block", conditionsFromItem(Blocks.field_10359))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8446)
			.input(Items.field_8324)
			.group("white_dye")
			.criterion("has_bone_meal", conditionsFromItem(Items.field_8324))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8446)
			.input(Blocks.field_10548)
			.group("white_dye")
			.criterion("has_white_flower", conditionsFromItem(Blocks.field_10548))
			.offerTo(consumer, "white_dye_from_lily_of_the_valley");
		ShapedRecipeJsonFactory.create(Items.field_8406)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_15537)
			.pattern("XX")
			.pattern("X#")
			.pattern(" #")
			.criterion("has_stick", conditionsFromItem(Items.field_8600))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8167)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_15537)
			.pattern("XX")
			.pattern(" #")
			.pattern(" #")
			.criterion("has_stick", conditionsFromItem(Items.field_8600))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8647)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_15537)
			.pattern("XXX")
			.pattern(" # ")
			.pattern(" # ")
			.criterion("has_stick", conditionsFromItem(Items.field_8600))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8876)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_15537)
			.pattern("X")
			.pattern("#")
			.pattern("#")
			.criterion("has_stick", conditionsFromItem(Items.field_8600))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Items.field_8091)
			.input('#', Items.field_8600)
			.input('X', ItemTags.field_15537)
			.pattern("X")
			.pattern("X")
			.pattern("#")
			.criterion("has_stick", conditionsFromItem(Items.field_8600))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8674)
			.input(Items.field_8529)
			.input(Items.field_8794)
			.input(Items.field_8153)
			.criterion("has_book", conditionsFromItem(Items.field_8529))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8192)
			.input(Blocks.field_10182)
			.group("yellow_dye")
			.criterion("has_yellow_flower", conditionsFromItem(Blocks.field_10182))
			.offerTo(consumer, "yellow_dye_from_dandelion");
		ShapelessRecipeJsonFactory.create(Items.field_8192, 2)
			.input(Blocks.field_10583)
			.group("yellow_dye")
			.criterion("has_double_plant", conditionsFromItem(Blocks.field_10583))
			.offerTo(consumer, "yellow_dye_from_sunflower");
		ShapelessRecipeJsonFactory.create(Items.field_8551, 9)
			.input(Blocks.field_10342)
			.criterion("has_dried_kelp_block", conditionsFromItem(Blocks.field_10342))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_10342)
			.input(Items.field_8551, 9)
			.criterion("has_dried_kelp", conditionsFromItem(Items.field_8551))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10502)
			.input('#', Items.field_8864)
			.input('X', Items.field_8207)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.criterion("has_nautilus_core", conditionsFromItem(Items.field_8207))
			.criterion("has_nautilus_shell", conditionsFromItem(Items.field_8864))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10435, 4)
			.input('#', Blocks.field_10289)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_granite", conditionsFromItem(Blocks.field_10289))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10039, 4)
			.input('#', Blocks.field_10483)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_smooth_red_sandstone", conditionsFromItem(Blocks.field_10483))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10173, 4)
			.input('#', Blocks.field_10065)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_mossy_stone_bricks", conditionsFromItem(Blocks.field_10065))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10310, 4)
			.input('#', Blocks.field_10346)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_diorite", conditionsFromItem(Blocks.field_10346))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10207, 4)
			.input('#', Blocks.field_9989)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_mossy_cobblestone", conditionsFromItem(Blocks.field_9989))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10012, 4)
			.input('#', Blocks.field_10462)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_end_stone_bricks", conditionsFromItem(Blocks.field_10462))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10440, 4)
			.input('#', Blocks.field_10340)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10549, 4)
			.input('#', Blocks.field_10467)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_smooth_sandstone", conditionsFromItem(Blocks.field_10467))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10245, 4)
			.input('#', Blocks.field_9978)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_smooth_quartz", conditionsFromItem(Blocks.field_9978))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10607, 4)
			.input('#', Blocks.field_10474)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_granite", conditionsFromItem(Blocks.field_10474))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10386, 4)
			.input('#', Blocks.field_10115)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_andesite", conditionsFromItem(Blocks.field_10115))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10497, 4)
			.input('#', Blocks.field_9986)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_red_nether_bricks", conditionsFromItem(Blocks.field_9986))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_9994, 4)
			.input('#', Blocks.field_10093)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_andesite", conditionsFromItem(Blocks.field_10093))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10216, 4)
			.input('#', Blocks.field_10508)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_diorite", conditionsFromItem(Blocks.field_10508))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10329, 6)
			.input('#', Blocks.field_10289)
			.pattern("###")
			.criterion("has_polished_granite", conditionsFromItem(Blocks.field_10289))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10283, 6)
			.input('#', Blocks.field_10483)
			.pattern("###")
			.criterion("has_smooth_red_sandstone", conditionsFromItem(Blocks.field_10483))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10024, 6)
			.input('#', Blocks.field_10065)
			.pattern("###")
			.criterion("has_mossy_stone_bricks", conditionsFromItem(Blocks.field_10065))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10412, 6)
			.input('#', Blocks.field_10346)
			.pattern("###")
			.criterion("has_polished_diorite", conditionsFromItem(Blocks.field_10346))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10405, 6)
			.input('#', Blocks.field_9989)
			.pattern("###")
			.criterion("has_mossy_cobblestone", conditionsFromItem(Blocks.field_9989))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10064, 6)
			.input('#', Blocks.field_10462)
			.pattern("###")
			.criterion("has_end_stone_bricks", conditionsFromItem(Blocks.field_10462))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10262, 6)
			.input('#', Blocks.field_10467)
			.pattern("###")
			.criterion("has_smooth_sandstone", conditionsFromItem(Blocks.field_10467))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10601, 6)
			.input('#', Blocks.field_9978)
			.pattern("###")
			.criterion("has_smooth_quartz", conditionsFromItem(Blocks.field_9978))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10189, 6)
			.input('#', Blocks.field_10474)
			.pattern("###")
			.criterion("has_granite", conditionsFromItem(Blocks.field_10474))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10016, 6)
			.input('#', Blocks.field_10115)
			.pattern("###")
			.criterion("has_andesite", conditionsFromItem(Blocks.field_10115))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10478, 6)
			.input('#', Blocks.field_9986)
			.pattern("###")
			.criterion("has_red_nether_bricks", conditionsFromItem(Blocks.field_9986))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10322, 6)
			.input('#', Blocks.field_10093)
			.pattern("###")
			.criterion("has_polished_andesite", conditionsFromItem(Blocks.field_10093))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10507, 6)
			.input('#', Blocks.field_10508)
			.pattern("###")
			.criterion("has_diorite", conditionsFromItem(Blocks.field_10508))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10269, 6)
			.input('#', Blocks.field_10104)
			.pattern("###")
			.pattern("###")
			.criterion("has_bricks", conditionsFromItem(Blocks.field_10104))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10530, 6)
			.input('#', Blocks.field_10135)
			.pattern("###")
			.pattern("###")
			.criterion("has_prismarine", conditionsFromItem(Blocks.field_10135))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10413, 6)
			.input('#', Blocks.field_10344)
			.pattern("###")
			.pattern("###")
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10059, 6)
			.input('#', Blocks.field_10065)
			.pattern("###")
			.pattern("###")
			.criterion("has_mossy_stone_bricks", conditionsFromItem(Blocks.field_10065))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10072, 6)
			.input('#', Blocks.field_10474)
			.pattern("###")
			.pattern("###")
			.criterion("has_granite", conditionsFromItem(Blocks.field_10474))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10252, 6)
			.input('#', Blocks.field_10056)
			.pattern("###")
			.pattern("###")
			.criterion("has_stone_bricks", conditionsFromItem(Blocks.field_10056))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10127, 6)
			.input('#', Blocks.field_10266)
			.pattern("###")
			.pattern("###")
			.criterion("has_nether_bricks", conditionsFromItem(Blocks.field_10266))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10489, 6)
			.input('#', Blocks.field_10115)
			.pattern("###")
			.pattern("###")
			.criterion("has_andesite", conditionsFromItem(Blocks.field_10115))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10311, 6)
			.input('#', Blocks.field_9986)
			.pattern("###")
			.pattern("###")
			.criterion("has_red_nether_bricks", conditionsFromItem(Blocks.field_9986))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10630, 6)
			.input('#', Blocks.field_9979)
			.pattern("###")
			.pattern("###")
			.criterion("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10001, 6)
			.input('#', Blocks.field_10462)
			.pattern("###")
			.pattern("###")
			.criterion("has_end_stone_bricks", conditionsFromItem(Blocks.field_10462))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_10517, 6)
			.input('#', Blocks.field_10508)
			.pattern("###")
			.pattern("###")
			.criterion("has_diorite", conditionsFromItem(Blocks.field_10508))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8573)
			.input(Items.field_8407)
			.input(Items.CREEPER_HEAD)
			.criterion("has_creeper_head", conditionsFromItem(Items.CREEPER_HEAD))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8891)
			.input(Items.field_8407)
			.input(Items.WITHER_SKELETON_SKULL)
			.criterion("has_wither_skeleton_skull", conditionsFromItem(Items.WITHER_SKELETON_SKULL))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8498)
			.input(Items.field_8407)
			.input(Blocks.field_10554)
			.criterion("has_oxeye_daisy", conditionsFromItem(Blocks.field_10554))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_8159)
			.input(Items.field_8407)
			.input(Items.field_8367)
			.criterion("has_enchanted_golden_apple", conditionsFromItem(Items.field_8367))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16492, 6)
			.input('~', Items.field_8276)
			.input('I', Blocks.field_10211)
			.pattern("I~I")
			.pattern("I I")
			.pattern("I I")
			.criterion("has_bamboo", conditionsFromItem(Blocks.field_10211))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16337)
			.input('I', Items.field_8600)
			.input('-', Blocks.field_10454)
			.input('#', ItemTags.field_15537)
			.pattern("I-I")
			.pattern("# #")
			.criterion("has_stone_slab", conditionsFromItem(Blocks.field_10454))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16333)
			.input('#', Blocks.field_10360)
			.input('X', Blocks.field_10181)
			.input('I', Items.field_8620)
			.pattern("III")
			.pattern("IXI")
			.pattern("###")
			.criterion("has_smooth_stone", conditionsFromItem(Blocks.field_10360))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16334)
			.input('#', ItemTags.field_15539)
			.input('X', Blocks.field_10181)
			.pattern(" # ")
			.pattern("#X#")
			.pattern(" # ")
			.criterion("has_furnace", conditionsFromItem(Blocks.field_10181))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16336)
			.input('#', ItemTags.field_15537)
			.input('@', Items.field_8407)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_paper", conditionsFromItem(Items.field_8407))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16329)
			.input('#', ItemTags.field_15537)
			.input('@', Items.field_8620)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16331)
			.input('#', ItemTags.field_15537)
			.input('@', Items.field_8145)
			.pattern("@@")
			.pattern("##")
			.pattern("##")
			.criterion("has_flint", conditionsFromItem(Items.field_8145))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_16335)
			.input('I', Items.field_8620)
			.input('#', Blocks.field_10340)
			.pattern(" I ")
			.pattern("###")
			.criterion("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23261)
			.input('S', Items.CHISELED_STONE_BRICKS)
			.input('#', Items.field_22020)
			.pattern("SSS")
			.pattern("S#S")
			.pattern("SSS")
			.criterion("has_netherite_ingot", conditionsFromItem(Items.field_22020))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_22108)
			.input('#', Items.field_22020)
			.pattern("###")
			.pattern("###")
			.pattern("###")
			.criterion("has_netherite_ingot", conditionsFromItem(Items.field_22020))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Items.field_22020, 9)
			.input(Blocks.field_22108)
			.group("netherite_ingot")
			.criterion("has_netherite_block", conditionsFromItem(Blocks.field_22108))
			.offerTo(consumer, "netherite_ingot_from_netherite_block");
		ShapelessRecipeJsonFactory.create(Items.field_22020)
			.input(Items.field_22021, 4)
			.input(Items.field_8695, 4)
			.group("netherite_ingot")
			.criterion("has_netherite_scrap", conditionsFromItem(Items.field_22021))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23152)
			.input('O', Blocks.field_22423)
			.input('G', Blocks.field_10171)
			.pattern("OOO")
			.pattern("GGG")
			.pattern("OOO")
			.criterion("has_obsidian", conditionsFromItem(Blocks.field_22423))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23870, 4)
			.input('#', Blocks.field_23869)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23861, 4)
			.input('#', Blocks.field_23873)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23878, 4)
			.input('#', Blocks.field_23874)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.criterion("has_polished_blackstone_bricks", conditionsFromItem(Blocks.field_23874))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23872, 6)
			.input('#', Blocks.field_23869)
			.pattern("###")
			.criterion("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23862, 6)
			.input('#', Blocks.field_23873)
			.pattern("###")
			.criterion("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23877, 6)
			.input('#', Blocks.field_23874)
			.pattern("###")
			.criterion("has_polished_blackstone_bricks", conditionsFromItem(Blocks.field_23874))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23873, 4)
			.input('S', Blocks.field_23869)
			.pattern("SS")
			.pattern("SS")
			.criterion("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23874, 4)
			.input('#', Blocks.field_23873)
			.pattern("##")
			.pattern("##")
			.criterion("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23876)
			.input('#', Blocks.field_23862)
			.pattern("#")
			.pattern("#")
			.criterion("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23871, 6)
			.input('#', Blocks.field_23869)
			.pattern("###")
			.pattern("###")
			.criterion("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23865, 6)
			.input('#', Blocks.field_23873)
			.pattern("###")
			.pattern("###")
			.criterion("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23879, 6)
			.input('#', Blocks.field_23874)
			.pattern("###")
			.pattern("###")
			.criterion("has_polished_blackstone_bricks", conditionsFromItem(Blocks.field_23874))
			.offerTo(consumer);
		ShapelessRecipeJsonFactory.create(Blocks.field_23864)
			.input(Blocks.field_23873)
			.criterion("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23863)
			.input('#', Blocks.field_23873)
			.pattern("##")
			.criterion("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer);
		ShapedRecipeJsonFactory.create(Blocks.field_23985)
			.input('I', Items.field_8620)
			.input('N', Items.field_8675)
			.pattern("N")
			.pattern("I")
			.pattern("N")
			.criterion("has_iron_nugget", conditionsFromItem(Items.field_8675))
			.criterion("has_iron_ingot", conditionsFromItem(Items.field_8620))
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
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8567), Items.field_8512, 0.35F, 200)
			.criterion("has_potato", conditionsFromItem(Items.field_8567))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8696), Items.field_8621, 0.3F, 200)
			.criterion("has_clay_ball", conditionsFromItem(Items.field_8696))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.field_23212), Items.field_8665, 0.15F, 200)
			.criterion("has_log", conditionsFromTag(ItemTags.field_23212))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8233), Items.field_8882, 0.1F, 200)
			.criterion("has_chorus_fruit", conditionsFromItem(Items.field_8233))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10418.asItem()), Items.field_8713, 0.1F, 200)
			.criterion("has_coal_ore", conditionsFromItem(Blocks.field_10418))
			.offerTo(consumer, "coal_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8046), Items.field_8176, 0.35F, 200)
			.criterion("has_beef", conditionsFromItem(Items.field_8046))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8726), Items.field_8544, 0.35F, 200)
			.criterion("has_chicken", conditionsFromItem(Items.field_8726))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8429), Items.field_8373, 0.35F, 200)
			.criterion("has_cod", conditionsFromItem(Items.field_8429))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_9993), Items.field_8551, 0.1F, 200)
			.criterion("has_kelp", conditionsFromItem(Blocks.field_9993))
			.offerTo(consumer, "dried_kelp_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8209), Items.field_8509, 0.35F, 200)
			.criterion("has_salmon", conditionsFromItem(Items.field_8209))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8748), Items.field_8347, 0.35F, 200)
			.criterion("has_mutton", conditionsFromItem(Items.field_8748))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8389), Items.field_8261, 0.35F, 200)
			.criterion("has_porkchop", conditionsFromItem(Items.field_8389))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Items.field_8504), Items.field_8752, 0.35F, 200)
			.criterion("has_rabbit", conditionsFromItem(Items.field_8504))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10442.asItem()), Items.field_8477, 1.0F, 200)
			.criterion("has_diamond_ore", conditionsFromItem(Blocks.field_10442))
			.offerTo(consumer, "diamond_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10090.asItem()), Items.field_8759, 0.2F, 200)
			.criterion("has_lapis_ore", conditionsFromItem(Blocks.field_10090))
			.offerTo(consumer, "lapis_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10013.asItem()), Items.field_8687, 1.0F, 200)
			.criterion("has_emerald_ore", conditionsFromItem(Blocks.field_10013))
			.offerTo(consumer, "emerald_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.field_15532), Blocks.field_10033.asItem(), 0.1F, 200)
			.criterion("has_sand", conditionsFromTag(ItemTags.field_15532))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.fromTag(ItemTags.field_23065), Items.field_8695, 1.0F, 200)
			.criterion("has_gold_ore", conditionsFromTag(ItemTags.field_23065))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10476.asItem()), Items.field_8131, 0.1F, 200)
			.criterion("has_sea_pickle", conditionsFromItem(Blocks.field_10476))
			.offerTo(consumer, "lime_dye_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10029.asItem()), Items.field_8408, 1.0F, 200)
			.criterion("has_cactus", conditionsFromItem(Blocks.field_10029))
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
			.criterion("has_golden_pickaxe", conditionsFromItem(Items.field_8335))
			.criterion("has_golden_shovel", conditionsFromItem(Items.field_8322))
			.criterion("has_golden_axe", conditionsFromItem(Items.field_8825))
			.criterion("has_golden_hoe", conditionsFromItem(Items.field_8303))
			.criterion("has_golden_sword", conditionsFromItem(Items.field_8845))
			.criterion("has_golden_helmet", conditionsFromItem(Items.field_8862))
			.criterion("has_golden_chestplate", conditionsFromItem(Items.field_8678))
			.criterion("has_golden_leggings", conditionsFromItem(Items.field_8416))
			.criterion("has_golden_boots", conditionsFromItem(Items.field_8753))
			.criterion("has_golden_horse_armor", conditionsFromItem(Items.field_8560))
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
			.criterion("has_iron_pickaxe", conditionsFromItem(Items.field_8403))
			.criterion("has_iron_shovel", conditionsFromItem(Items.field_8699))
			.criterion("has_iron_axe", conditionsFromItem(Items.field_8475))
			.criterion("has_iron_hoe", conditionsFromItem(Items.field_8609))
			.criterion("has_iron_sword", conditionsFromItem(Items.field_8371))
			.criterion("has_iron_helmet", conditionsFromItem(Items.field_8743))
			.criterion("has_iron_chestplate", conditionsFromItem(Items.field_8523))
			.criterion("has_iron_leggings", conditionsFromItem(Items.field_8396))
			.criterion("has_iron_boots", conditionsFromItem(Items.field_8660))
			.criterion("has_iron_horse_armor", conditionsFromItem(Items.field_8578))
			.criterion("has_chainmail_helmet", conditionsFromItem(Items.field_8283))
			.criterion("has_chainmail_chestplate", conditionsFromItem(Items.field_8873))
			.criterion("has_chainmail_leggings", conditionsFromItem(Items.field_8218))
			.criterion("has_chainmail_boots", conditionsFromItem(Items.field_8313))
			.offerTo(consumer, "iron_nugget_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10212.asItem()), Items.field_8620, 0.7F, 200)
			.criterion("has_iron_ore", conditionsFromItem(Blocks.field_10212.asItem()))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10460), Blocks.field_10415.asItem(), 0.35F, 200)
			.criterion("has_clay_block", conditionsFromItem(Blocks.field_10460))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10515), Items.field_8729, 0.1F, 200)
			.criterion("has_netherrack", conditionsFromItem(Blocks.field_10515))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10213), Items.field_8155, 0.2F, 200)
			.criterion("has_nether_quartz_ore", conditionsFromItem(Blocks.field_10213))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10080), Items.field_8725, 0.7F, 200)
			.criterion("has_redstone_ore", conditionsFromItem(Blocks.field_10080))
			.offerTo(consumer, "redstone_from_smelting");
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10562), Blocks.field_10258.asItem(), 0.15F, 200)
			.criterion("has_wet_sponge", conditionsFromItem(Blocks.field_10562))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10445), Blocks.field_10340.asItem(), 0.1F, 200)
			.criterion("has_cobblestone", conditionsFromItem(Blocks.field_10445))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10360.asItem(), 0.1F, 200)
			.criterion("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10467.asItem(), 0.1F, 200)
			.criterion("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10483.asItem(), 0.1F, 200)
			.criterion("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10153), Blocks.field_9978.asItem(), 0.1F, 200)
			.criterion("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10056), Blocks.field_10416.asItem(), 0.1F, 200)
			.criterion("has_stone_bricks", conditionsFromItem(Blocks.field_10056))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10626), Blocks.field_10501.asItem(), 0.1F, 200)
			.criterion("has_black_terracotta", conditionsFromItem(Blocks.field_10626))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10409), Blocks.field_10550.asItem(), 0.1F, 200)
			.criterion("has_blue_terracotta", conditionsFromItem(Blocks.field_10409))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10123), Blocks.field_10004.asItem(), 0.1F, 200)
			.criterion("has_brown_terracotta", conditionsFromItem(Blocks.field_10123))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10235), Blocks.field_10078.asItem(), 0.1F, 200)
			.criterion("has_cyan_terracotta", conditionsFromItem(Blocks.field_10235))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10349), Blocks.field_10220.asItem(), 0.1F, 200)
			.criterion("has_gray_terracotta", conditionsFromItem(Blocks.field_10349))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10526), Blocks.field_10475.asItem(), 0.1F, 200)
			.criterion("has_green_terracotta", conditionsFromItem(Blocks.field_10526))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10325), Blocks.field_10345.asItem(), 0.1F, 200)
			.criterion("has_light_blue_terracotta", conditionsFromItem(Blocks.field_10325))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10590), Blocks.field_10052.asItem(), 0.1F, 200)
			.criterion("has_light_gray_terracotta", conditionsFromItem(Blocks.field_10590))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10014), Blocks.field_10046.asItem(), 0.1F, 200)
			.criterion("has_lime_terracotta", conditionsFromItem(Blocks.field_10014))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10015), Blocks.field_10538.asItem(), 0.1F, 200)
			.criterion("has_magenta_terracotta", conditionsFromItem(Blocks.field_10015))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10184), Blocks.field_10280.asItem(), 0.1F, 200)
			.criterion("has_orange_terracotta", conditionsFromItem(Blocks.field_10184))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10444), Blocks.field_10567.asItem(), 0.1F, 200)
			.criterion("has_pink_terracotta", conditionsFromItem(Blocks.field_10444))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10570), Blocks.field_10426.asItem(), 0.1F, 200)
			.criterion("has_purple_terracotta", conditionsFromItem(Blocks.field_10570))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10328), Blocks.field_10383.asItem(), 0.1F, 200)
			.criterion("has_red_terracotta", conditionsFromItem(Blocks.field_10328))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10611), Blocks.field_10595.asItem(), 0.1F, 200)
			.criterion("has_white_terracotta", conditionsFromItem(Blocks.field_10611))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10143), Blocks.field_10096.asItem(), 0.1F, 200)
			.criterion("has_yellow_terracotta", conditionsFromItem(Blocks.field_10143))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_22109), Items.field_22021, 2.0F, 200)
			.criterion("has_ancient_debris", conditionsFromItem(Blocks.field_22109))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_23874), Blocks.field_23875.asItem(), 0.1F, 200)
			.criterion("has_blackstone_bricks", conditionsFromItem(Blocks.field_23874))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(Blocks.field_10266), Blocks.field_23867.asItem(), 0.1F, 200)
			.criterion("has_nether_bricks", conditionsFromItem(Blocks.field_10266))
			.offerTo(consumer);
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10212.asItem()), Items.field_8620, 0.7F, 100)
			.criterion("has_iron_ore", conditionsFromItem(Blocks.field_10212.asItem()))
			.offerTo(consumer, "iron_ingot_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.fromTag(ItemTags.field_23065), Items.field_8695, 1.0F, 100)
			.criterion("has_gold_ore", conditionsFromTag(ItemTags.field_23065))
			.offerTo(consumer, "gold_ingot_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10442.asItem()), Items.field_8477, 1.0F, 100)
			.criterion("has_diamond_ore", conditionsFromItem(Blocks.field_10442))
			.offerTo(consumer, "diamond_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10090.asItem()), Items.field_8759, 0.2F, 100)
			.criterion("has_lapis_ore", conditionsFromItem(Blocks.field_10090))
			.offerTo(consumer, "lapis_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10080), Items.field_8725, 0.7F, 100)
			.criterion("has_redstone_ore", conditionsFromItem(Blocks.field_10080))
			.offerTo(consumer, "redstone_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10418.asItem()), Items.field_8713, 0.1F, 100)
			.criterion("has_coal_ore", conditionsFromItem(Blocks.field_10418))
			.offerTo(consumer, "coal_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10013.asItem()), Items.field_8687, 1.0F, 100)
			.criterion("has_emerald_ore", conditionsFromItem(Blocks.field_10013))
			.offerTo(consumer, "emerald_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_10213), Items.field_8155, 0.2F, 100)
			.criterion("has_nether_quartz_ore", conditionsFromItem(Blocks.field_10213))
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
			.criterion("has_golden_pickaxe", conditionsFromItem(Items.field_8335))
			.criterion("has_golden_shovel", conditionsFromItem(Items.field_8322))
			.criterion("has_golden_axe", conditionsFromItem(Items.field_8825))
			.criterion("has_golden_hoe", conditionsFromItem(Items.field_8303))
			.criterion("has_golden_sword", conditionsFromItem(Items.field_8845))
			.criterion("has_golden_helmet", conditionsFromItem(Items.field_8862))
			.criterion("has_golden_chestplate", conditionsFromItem(Items.field_8678))
			.criterion("has_golden_leggings", conditionsFromItem(Items.field_8416))
			.criterion("has_golden_boots", conditionsFromItem(Items.field_8753))
			.criterion("has_golden_horse_armor", conditionsFromItem(Items.field_8560))
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
			.criterion("has_iron_pickaxe", conditionsFromItem(Items.field_8403))
			.criterion("has_iron_shovel", conditionsFromItem(Items.field_8699))
			.criterion("has_iron_axe", conditionsFromItem(Items.field_8475))
			.criterion("has_iron_hoe", conditionsFromItem(Items.field_8609))
			.criterion("has_iron_sword", conditionsFromItem(Items.field_8371))
			.criterion("has_iron_helmet", conditionsFromItem(Items.field_8743))
			.criterion("has_iron_chestplate", conditionsFromItem(Items.field_8523))
			.criterion("has_iron_leggings", conditionsFromItem(Items.field_8396))
			.criterion("has_iron_boots", conditionsFromItem(Items.field_8660))
			.criterion("has_iron_horse_armor", conditionsFromItem(Items.field_8578))
			.criterion("has_chainmail_helmet", conditionsFromItem(Items.field_8283))
			.criterion("has_chainmail_chestplate", conditionsFromItem(Items.field_8873))
			.criterion("has_chainmail_leggings", conditionsFromItem(Items.field_8218))
			.criterion("has_chainmail_boots", conditionsFromItem(Items.field_8313))
			.offerTo(consumer, "iron_nugget_from_blasting");
		CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(Blocks.field_22109), Items.field_22021, 2.0F, 100)
			.criterion("has_ancient_debris", conditionsFromItem(Blocks.field_22109))
			.offerTo(consumer, "netherite_scrap_from_blasting");
		generateCookingRecipes(consumer, "smoking", RecipeSerializer.SMOKING, 100);
		generateCookingRecipes(consumer, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, 600);
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10454, 2)
			.create("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer, "stone_slab_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10440)
			.create("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer, "stone_stairs_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10056)
			.create("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer, "stone_bricks_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10131, 2)
			.create("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer, "stone_brick_slab_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10392)
			.create("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer, "stone_brick_stairs_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10552)
			.create("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer, "chiseled_stone_bricks_stone_from_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10340), Blocks.field_10252)
			.create("has_stone", conditionsFromItem(Blocks.field_10340))
			.offerTo(consumer, "stone_brick_walls_from_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10361)
			.create("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.offerTo(consumer, "cut_sandstone_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10007, 2)
			.create("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.offerTo(consumer, "sandstone_slab_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_18890, 2)
			.create("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.offerTo(consumer, "cut_sandstone_slab_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10361), Blocks.field_18890, 2)
			.create("has_cut_sandstone", conditionsFromItem(Blocks.field_9979))
			.offerTo(consumer, "cut_sandstone_slab_from_cut_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10142)
			.create("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.offerTo(consumer, "sandstone_stairs_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10630)
			.create("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.offerTo(consumer, "sandstone_wall_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9979), Blocks.field_10292)
			.create("has_sandstone", conditionsFromItem(Blocks.field_9979))
			.offerTo(consumer, "chiseled_sandstone_from_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10518)
			.create("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.offerTo(consumer, "cut_red_sandstone_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10624, 2)
			.create("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.offerTo(consumer, "red_sandstone_slab_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_18891, 2)
			.create("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.offerTo(consumer, "cut_red_sandstone_slab_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10518), Blocks.field_18891, 2)
			.create("has_cut_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.offerTo(consumer, "cut_red_sandstone_slab_from_cut_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10420)
			.create("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.offerTo(consumer, "red_sandstone_stairs_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10413)
			.create("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.offerTo(consumer, "red_sandstone_wall_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10344), Blocks.field_10117)
			.create("has_red_sandstone", conditionsFromItem(Blocks.field_10344))
			.offerTo(consumer, "chiseled_red_sandstone_from_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10153), Blocks.field_10237, 2)
			.create("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.offerTo(consumer, "quartz_slab_from_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10153), Blocks.field_10451)
			.create("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.offerTo(consumer, "quartz_stairs_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10153), Blocks.field_10437)
			.create("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.offerTo(consumer, "quartz_pillar_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10153), Blocks.field_10044)
			.create("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.offerTo(consumer, "chiseled_quartz_block_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10153), Blocks.field_23868)
			.create("has_quartz_block", conditionsFromItem(Blocks.field_10153))
			.offerTo(consumer, "quartz_bricks_from_quartz_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10445), Blocks.field_10596)
			.create("has_cobblestone", conditionsFromItem(Blocks.field_10445))
			.offerTo(consumer, "cobblestone_stairs_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10445), Blocks.field_10351, 2)
			.create("has_cobblestone", conditionsFromItem(Blocks.field_10445))
			.offerTo(consumer, "cobblestone_slab_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10445), Blocks.field_10625)
			.create("has_cobblestone", conditionsFromItem(Blocks.field_10445))
			.offerTo(consumer, "cobblestone_wall_from_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10056), Blocks.field_10131, 2)
			.create("has_stone_bricks", conditionsFromItem(Blocks.field_10056))
			.offerTo(consumer, "stone_brick_slab_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10056), Blocks.field_10392)
			.create("has_stone_bricks", conditionsFromItem(Blocks.field_10056))
			.offerTo(consumer, "stone_brick_stairs_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10056), Blocks.field_10252)
			.create("has_stone_bricks", conditionsFromItem(Blocks.field_10056))
			.offerTo(consumer, "stone_brick_wall_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10056), Blocks.field_10552)
			.create("has_stone_bricks", conditionsFromItem(Blocks.field_10056))
			.offerTo(consumer, "chiseled_stone_bricks_from_stone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10104), Blocks.field_10191, 2)
			.create("has_bricks", conditionsFromItem(Blocks.field_10104))
			.offerTo(consumer, "brick_slab_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10104), Blocks.field_10089)
			.create("has_bricks", conditionsFromItem(Blocks.field_10104))
			.offerTo(consumer, "brick_stairs_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10104), Blocks.field_10269)
			.create("has_bricks", conditionsFromItem(Blocks.field_10104))
			.offerTo(consumer, "brick_wall_from_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10266), Blocks.field_10390, 2)
			.create("has_nether_bricks", conditionsFromItem(Blocks.field_10266))
			.offerTo(consumer, "nether_brick_slab_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10266), Blocks.field_10159)
			.create("has_nether_bricks", conditionsFromItem(Blocks.field_10266))
			.offerTo(consumer, "nether_brick_stairs_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10266), Blocks.field_10127)
			.create("has_nether_bricks", conditionsFromItem(Blocks.field_10266))
			.offerTo(consumer, "nether_brick_wall_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10266), Blocks.field_23866)
			.create("has_nether_bricks", conditionsFromItem(Blocks.field_10266))
			.offerTo(consumer, "chiseled_nether_bricks_from_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9986), Blocks.field_10478, 2)
			.create("has_nether_bricks", conditionsFromItem(Blocks.field_9986))
			.offerTo(consumer, "red_nether_brick_slab_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9986), Blocks.field_10497)
			.create("has_nether_bricks", conditionsFromItem(Blocks.field_9986))
			.offerTo(consumer, "red_nether_brick_stairs_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9986), Blocks.field_10311)
			.create("has_nether_bricks", conditionsFromItem(Blocks.field_9986))
			.offerTo(consumer, "red_nether_brick_wall_from_red_nether_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10286), Blocks.field_10175, 2)
			.create("has_purpur_block", conditionsFromItem(Blocks.field_10286))
			.offerTo(consumer, "purpur_slab_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10286), Blocks.field_9992)
			.create("has_purpur_block", conditionsFromItem(Blocks.field_10286))
			.offerTo(consumer, "purpur_stairs_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10286), Blocks.field_10505)
			.create("has_purpur_block", conditionsFromItem(Blocks.field_10286))
			.offerTo(consumer, "purpur_pillar_from_purpur_block_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10135), Blocks.field_10389, 2)
			.create("has_prismarine", conditionsFromItem(Blocks.field_10135))
			.offerTo(consumer, "prismarine_slab_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10135), Blocks.field_10350)
			.create("has_prismarine", conditionsFromItem(Blocks.field_10135))
			.offerTo(consumer, "prismarine_stairs_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10135), Blocks.field_10530)
			.create("has_prismarine", conditionsFromItem(Blocks.field_10135))
			.offerTo(consumer, "prismarine_wall_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10006), Blocks.field_10236, 2)
			.create("has_prismarine_brick", conditionsFromItem(Blocks.field_10006))
			.offerTo(consumer, "prismarine_brick_slab_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10006), Blocks.field_10190)
			.create("has_prismarine_brick", conditionsFromItem(Blocks.field_10006))
			.offerTo(consumer, "prismarine_brick_stairs_from_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10297), Blocks.field_10623, 2)
			.create("has_dark_prismarine", conditionsFromItem(Blocks.field_10297))
			.offerTo(consumer, "dark_prismarine_slab_from_dark_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10297), Blocks.field_10130)
			.create("has_dark_prismarine", conditionsFromItem(Blocks.field_10297))
			.offerTo(consumer, "dark_prismarine_stairs_from_dark_prismarine_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_10016, 2)
			.create("has_andesite", conditionsFromItem(Blocks.field_10115))
			.offerTo(consumer, "andesite_slab_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_10386)
			.create("has_andesite", conditionsFromItem(Blocks.field_10115))
			.offerTo(consumer, "andesite_stairs_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_10489)
			.create("has_andesite", conditionsFromItem(Blocks.field_10115))
			.offerTo(consumer, "andesite_wall_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_10093)
			.create("has_andesite", conditionsFromItem(Blocks.field_10115))
			.offerTo(consumer, "polished_andesite_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_10322, 2)
			.create("has_andesite", conditionsFromItem(Blocks.field_10115))
			.offerTo(consumer, "polished_andesite_slab_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10115), Blocks.field_9994)
			.create("has_andesite", conditionsFromItem(Blocks.field_10115))
			.offerTo(consumer, "polished_andesite_stairs_from_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10093), Blocks.field_10322, 2)
			.create("has_polished_andesite", conditionsFromItem(Blocks.field_10093))
			.offerTo(consumer, "polished_andesite_slab_from_polished_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10093), Blocks.field_9994)
			.create("has_polished_andesite", conditionsFromItem(Blocks.field_10093))
			.offerTo(consumer, "polished_andesite_stairs_from_polished_andesite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_22091), Blocks.field_23151)
			.create("has_basalt", conditionsFromItem(Blocks.field_22091))
			.offerTo(consumer, "polished_basalt_from_basalt_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10189, 2)
			.create("has_granite", conditionsFromItem(Blocks.field_10474))
			.offerTo(consumer, "granite_slab_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10607)
			.create("has_granite", conditionsFromItem(Blocks.field_10474))
			.offerTo(consumer, "granite_stairs_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10072)
			.create("has_granite", conditionsFromItem(Blocks.field_10474))
			.offerTo(consumer, "granite_wall_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10289)
			.create("has_granite", conditionsFromItem(Blocks.field_10474))
			.offerTo(consumer, "polished_granite_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10329, 2)
			.create("has_granite", conditionsFromItem(Blocks.field_10474))
			.offerTo(consumer, "polished_granite_slab_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10474), Blocks.field_10435)
			.create("has_granite", conditionsFromItem(Blocks.field_10474))
			.offerTo(consumer, "polished_granite_stairs_from_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10289), Blocks.field_10329, 2)
			.create("has_polished_granite", conditionsFromItem(Blocks.field_10289))
			.offerTo(consumer, "polished_granite_slab_from_polished_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10289), Blocks.field_10435)
			.create("has_polished_granite", conditionsFromItem(Blocks.field_10289))
			.offerTo(consumer, "polished_granite_stairs_from_polished_granite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10507, 2)
			.create("has_diorite", conditionsFromItem(Blocks.field_10508))
			.offerTo(consumer, "diorite_slab_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10216)
			.create("has_diorite", conditionsFromItem(Blocks.field_10508))
			.offerTo(consumer, "diorite_stairs_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10517)
			.create("has_diorite", conditionsFromItem(Blocks.field_10508))
			.offerTo(consumer, "diorite_wall_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10346)
			.create("has_diorite", conditionsFromItem(Blocks.field_10508))
			.offerTo(consumer, "polished_diorite_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10412, 2)
			.create("has_diorite", conditionsFromItem(Blocks.field_10346))
			.offerTo(consumer, "polished_diorite_slab_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10508), Blocks.field_10310)
			.create("has_diorite", conditionsFromItem(Blocks.field_10346))
			.offerTo(consumer, "polished_diorite_stairs_from_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10346), Blocks.field_10412, 2)
			.create("has_polished_diorite", conditionsFromItem(Blocks.field_10346))
			.offerTo(consumer, "polished_diorite_slab_from_polished_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10346), Blocks.field_10310)
			.create("has_polished_diorite", conditionsFromItem(Blocks.field_10346))
			.offerTo(consumer, "polished_diorite_stairs_from_polished_diorite_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10065), Blocks.field_10024, 2)
			.create("has_mossy_stone_bricks", conditionsFromItem(Blocks.field_10065))
			.offerTo(consumer, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10065), Blocks.field_10173)
			.create("has_mossy_stone_bricks", conditionsFromItem(Blocks.field_10065))
			.offerTo(consumer, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10065), Blocks.field_10059)
			.create("has_mossy_stone_bricks", conditionsFromItem(Blocks.field_10065))
			.offerTo(consumer, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9989), Blocks.field_10405, 2)
			.create("has_mossy_cobblestone", conditionsFromItem(Blocks.field_9989))
			.offerTo(consumer, "mossy_cobblestone_slab_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9989), Blocks.field_10207)
			.create("has_mossy_cobblestone", conditionsFromItem(Blocks.field_9989))
			.offerTo(consumer, "mossy_cobblestone_stairs_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9989), Blocks.field_9990)
			.create("has_mossy_cobblestone", conditionsFromItem(Blocks.field_9989))
			.offerTo(consumer, "mossy_cobblestone_wall_from_mossy_cobblestone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10467), Blocks.field_10262, 2)
			.create("has_smooth_sandstone", conditionsFromItem(Blocks.field_10467))
			.offerTo(consumer, "smooth_sandstone_slab_from_smooth_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10467), Blocks.field_10549)
			.create("has_mossy_cobblestone", conditionsFromItem(Blocks.field_10467))
			.offerTo(consumer, "smooth_sandstone_stairs_from_smooth_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10483), Blocks.field_10283, 2)
			.create("has_smooth_red_sandstone", conditionsFromItem(Blocks.field_10483))
			.offerTo(consumer, "smooth_red_sandstone_slab_from_smooth_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10483), Blocks.field_10039)
			.create("has_smooth_red_sandstone", conditionsFromItem(Blocks.field_10483))
			.offerTo(consumer, "smooth_red_sandstone_stairs_from_smooth_red_sandstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9978), Blocks.field_10601, 2)
			.create("has_smooth_quartz", conditionsFromItem(Blocks.field_9978))
			.offerTo(consumer, "smooth_quartz_slab_from_smooth_quartz_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9978), Blocks.field_10245)
			.create("has_smooth_quartz", conditionsFromItem(Blocks.field_9978))
			.offerTo(consumer, "smooth_quartz_stairs_from_smooth_quartz_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10462), Blocks.field_10064, 2)
			.create("has_end_stone_brick", conditionsFromItem(Blocks.field_10462))
			.offerTo(consumer, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10462), Blocks.field_10012)
			.create("has_end_stone_brick", conditionsFromItem(Blocks.field_10462))
			.offerTo(consumer, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10462), Blocks.field_10001)
			.create("has_end_stone_brick", conditionsFromItem(Blocks.field_10462))
			.offerTo(consumer, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10471), Blocks.field_10462)
			.create("has_end_stone", conditionsFromItem(Blocks.field_10471))
			.offerTo(consumer, "end_stone_bricks_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10471), Blocks.field_10064, 2)
			.create("has_end_stone", conditionsFromItem(Blocks.field_10471))
			.offerTo(consumer, "end_stone_brick_slab_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10471), Blocks.field_10012)
			.create("has_end_stone", conditionsFromItem(Blocks.field_10471))
			.offerTo(consumer, "end_stone_brick_stairs_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10471), Blocks.field_10001)
			.create("has_end_stone", conditionsFromItem(Blocks.field_10471))
			.offerTo(consumer, "end_stone_brick_wall_from_end_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_10360), Blocks.field_10136, 2)
			.create("has_smooth_stone", conditionsFromItem(Blocks.field_10360))
			.offerTo(consumer, "smooth_stone_slab_from_smooth_stone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23872, 2)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "blackstone_slab_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23870)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "blackstone_stairs_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23871)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "blackstone_wall_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23873)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "polished_blackstone_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23865)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "polished_blackstone_wall_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23862, 2)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "polished_blackstone_slab_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23861)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "polished_blackstone_stairs_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23876)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "chiseled_polished_blackstone_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23874)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "polished_blackstone_bricks_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23877, 2)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "polished_blackstone_brick_slab_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23878)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "polished_blackstone_brick_stairs_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23869), Blocks.field_23879)
			.create("has_blackstone", conditionsFromItem(Blocks.field_23869))
			.offerTo(consumer, "polished_blackstone_brick_wall_from_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23873), Blocks.field_23862, 2)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer, "polished_blackstone_slab_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23873), Blocks.field_23861)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer, "polished_blackstone_stairs_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23873), Blocks.field_23874)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer, "polished_blackstone_bricks_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23873), Blocks.field_23865)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer, "polished_blackstone_wall_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23873), Blocks.field_23877, 2)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer, "polished_blackstone_brick_slab_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23873), Blocks.field_23878)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer, "polished_blackstone_brick_stairs_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23873), Blocks.field_23879)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer, "polished_blackstone_brick_wall_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23873), Blocks.field_23876)
			.create("has_polished_blackstone", conditionsFromItem(Blocks.field_23873))
			.offerTo(consumer, "chiseled_polished_blackstone_from_polished_blackstone_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23874), Blocks.field_23877, 2)
			.create("has_polished_blackstone_bricks", conditionsFromItem(Blocks.field_23874))
			.offerTo(consumer, "polished_blackstone_brick_slab_from_polished_blackstone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23874), Blocks.field_23878)
			.create("has_polished_blackstone_bricks", conditionsFromItem(Blocks.field_23874))
			.offerTo(consumer, "polished_blackstone_brick_stairs_from_polished_blackstone_bricks_stonecutting");
		SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_23874), Blocks.field_23879)
			.create("has_polished_blackstone_bricks", conditionsFromItem(Blocks.field_23874))
			.offerTo(consumer, "polished_blackstone_brick_wall_from_polished_blackstone_bricks_stonecutting");
		method_29728(consumer, Items.field_8058, Items.field_22028);
		method_29728(consumer, Items.field_8348, Items.field_22029);
		method_29728(consumer, Items.field_8805, Items.field_22027);
		method_29728(consumer, Items.field_8285, Items.field_22030);
		method_29728(consumer, Items.field_8802, Items.field_22022);
		method_29728(consumer, Items.field_8556, Items.field_22025);
		method_29728(consumer, Items.field_8377, Items.field_22024);
		method_29728(consumer, Items.field_8527, Items.field_22026);
		method_29728(consumer, Items.field_8250, Items.field_22023);
	}

	private static void method_29728(Consumer<RecipeJsonProvider> consumer, Item item, Item item2) {
		SmithingRecipeJsonFactory.create(Ingredient.ofItems(item), Ingredient.ofItems(Items.field_22020), item2)
			.criterion("has_netherite_ingot", conditionsFromItem(Items.field_22020))
			.offerTo(consumer, Registry.ITEM.getId(item2.asItem()).getPath() + "_smithing");
	}

	private static void method_24475(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, Tag<Item> tag) {
		ShapelessRecipeJsonFactory.create(itemConvertible, 4).input(tag).group("planks").criterion("has_log", conditionsFromTag(tag)).offerTo(consumer);
	}

	private static void method_24477(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, Tag<Item> tag) {
		ShapelessRecipeJsonFactory.create(itemConvertible, 4).input(tag).group("planks").criterion("has_logs", conditionsFromTag(tag)).offerTo(consumer);
	}

	private static void method_24476(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible, 3)
			.input('#', itemConvertible2)
			.pattern("##")
			.pattern("##")
			.group("bark")
			.criterion("has_log", conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24478(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible)
			.input('#', itemConvertible2)
			.pattern("# #")
			.pattern("###")
			.group("boat")
			.criterion("in_water", requireEnteringFluid(Blocks.field_10382))
			.offerTo(consumer);
	}

	private static void method_24479(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapelessRecipeJsonFactory.create(itemConvertible)
			.input(itemConvertible2)
			.group("wooden_button")
			.criterion("has_planks", conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24480(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible, 3)
			.input('#', itemConvertible2)
			.pattern("##")
			.pattern("##")
			.pattern("##")
			.group("wooden_door")
			.criterion("has_planks", conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24481(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible, 3)
			.input('#', Items.field_8600)
			.input('W', itemConvertible2)
			.pattern("W#W")
			.pattern("W#W")
			.group("wooden_fence")
			.criterion("has_planks", conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24482(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible)
			.input('#', Items.field_8600)
			.input('W', itemConvertible2)
			.pattern("#W#")
			.pattern("#W#")
			.group("wooden_fence_gate")
			.criterion("has_planks", conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24483(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible)
			.input('#', itemConvertible2)
			.pattern("##")
			.group("wooden_pressure_plate")
			.criterion("has_planks", conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24484(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible, 6)
			.input('#', itemConvertible2)
			.pattern("###")
			.group("wooden_slab")
			.criterion("has_planks", conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24485(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible, 4)
			.input('#', itemConvertible2)
			.pattern("#  ")
			.pattern("## ")
			.pattern("###")
			.group("wooden_stairs")
			.criterion("has_planks", conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24486(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible, 2)
			.input('#', itemConvertible2)
			.pattern("###")
			.pattern("###")
			.group("wooden_trapdoor")
			.criterion("has_planks", conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24883(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		String string = Registry.ITEM.getId(itemConvertible2.asItem()).getPath();
		ShapedRecipeJsonFactory.create(itemConvertible, 3)
			.group("sign")
			.input('#', itemConvertible2)
			.input('X', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" X ")
			.criterion("has_" + string, conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24884(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapelessRecipeJsonFactory.create(itemConvertible)
			.input(itemConvertible2)
			.input(Blocks.field_10446)
			.group("wool")
			.criterion("has_white_wool", conditionsFromItem(Blocks.field_10446))
			.offerTo(consumer);
	}

	private static void method_24885(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		String string = Registry.ITEM.getId(itemConvertible2.asItem()).getPath();
		ShapedRecipeJsonFactory.create(itemConvertible, 3)
			.input('#', itemConvertible2)
			.pattern("##")
			.group("carpet")
			.criterion("has_" + string, conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24886(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		String string = Registry.ITEM.getId(itemConvertible.asItem()).getPath();
		String string2 = Registry.ITEM.getId(itemConvertible2.asItem()).getPath();
		ShapedRecipeJsonFactory.create(itemConvertible, 8)
			.input('#', Blocks.field_10466)
			.input('$', itemConvertible2)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("carpet")
			.criterion("has_white_carpet", conditionsFromItem(Blocks.field_10466))
			.criterion("has_" + string2, conditionsFromItem(itemConvertible2))
			.offerTo(consumer, string + "_from_white_carpet");
	}

	private static void method_24887(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		String string = Registry.ITEM.getId(itemConvertible2.asItem()).getPath();
		ShapedRecipeJsonFactory.create(itemConvertible)
			.input('#', itemConvertible2)
			.input('X', ItemTags.field_15537)
			.pattern("###")
			.pattern("XXX")
			.group("bed")
			.criterion("has_" + string, conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24888(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		String string = Registry.ITEM.getId(itemConvertible.asItem()).getPath();
		ShapelessRecipeJsonFactory.create(itemConvertible)
			.input(Items.WHITE_BED)
			.input(itemConvertible2)
			.group("dyed_bed")
			.criterion("has_bed", conditionsFromItem(Items.WHITE_BED))
			.offerTo(consumer, string + "_from_white_bed");
	}

	private static void method_24889(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		String string = Registry.ITEM.getId(itemConvertible2.asItem()).getPath();
		ShapedRecipeJsonFactory.create(itemConvertible)
			.input('#', itemConvertible2)
			.input('|', Items.field_8600)
			.pattern("###")
			.pattern("###")
			.pattern(" | ")
			.group("banner")
			.criterion("has_" + string, conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24890(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible, 8)
			.input('#', Blocks.field_10033)
			.input('X', itemConvertible2)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_glass")
			.criterion("has_glass", conditionsFromItem(Blocks.field_10033))
			.offerTo(consumer);
	}

	private static void method_24891(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible, 16)
			.input('#', itemConvertible2)
			.pattern("###")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass", conditionsFromItem(itemConvertible2))
			.offerTo(consumer);
	}

	private static void method_24892(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		String string = Registry.ITEM.getId(itemConvertible.asItem()).getPath();
		String string2 = Registry.ITEM.getId(itemConvertible2.asItem()).getPath();
		ShapedRecipeJsonFactory.create(itemConvertible, 8)
			.input('#', Blocks.field_10285)
			.input('$', itemConvertible2)
			.pattern("###")
			.pattern("#$#")
			.pattern("###")
			.group("stained_glass_pane")
			.criterion("has_glass_pane", conditionsFromItem(Blocks.field_10285))
			.criterion("has_" + string2, conditionsFromItem(itemConvertible2))
			.offerTo(consumer, string + "_from_glass_pane");
	}

	private static void method_24893(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapedRecipeJsonFactory.create(itemConvertible, 8)
			.input('#', Blocks.field_10415)
			.input('X', itemConvertible2)
			.pattern("###")
			.pattern("#X#")
			.pattern("###")
			.group("stained_terracotta")
			.criterion("has_terracotta", conditionsFromItem(Blocks.field_10415))
			.offerTo(consumer);
	}

	private static void method_24894(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
		ShapelessRecipeJsonFactory.create(itemConvertible, 8)
			.input(itemConvertible2)
			.input(Blocks.field_10102, 4)
			.input(Blocks.field_10255, 4)
			.group("concrete_powder")
			.criterion("has_sand", conditionsFromItem(Blocks.field_10102))
			.criterion("has_gravel", conditionsFromItem(Blocks.field_10255))
			.offerTo(consumer);
	}

	private static void generateCookingRecipes(Consumer<RecipeJsonProvider> consumer, String string, CookingRecipeSerializer<?> cookingRecipeSerializer, int i) {
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8046), Items.field_8176, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_beef", conditionsFromItem(Items.field_8046))
			.offerTo(consumer, "cooked_beef_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8726), Items.field_8544, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_chicken", conditionsFromItem(Items.field_8726))
			.offerTo(consumer, "cooked_chicken_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8429), Items.field_8373, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_cod", conditionsFromItem(Items.field_8429))
			.offerTo(consumer, "cooked_cod_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Blocks.field_9993), Items.field_8551, 0.1F, i, cookingRecipeSerializer)
			.criterion("has_kelp", conditionsFromItem(Blocks.field_9993))
			.offerTo(consumer, "dried_kelp_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8209), Items.field_8509, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_salmon", conditionsFromItem(Items.field_8209))
			.offerTo(consumer, "cooked_salmon_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8748), Items.field_8347, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_mutton", conditionsFromItem(Items.field_8748))
			.offerTo(consumer, "cooked_mutton_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8389), Items.field_8261, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_porkchop", conditionsFromItem(Items.field_8389))
			.offerTo(consumer, "cooked_porkchop_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8567), Items.field_8512, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_potato", conditionsFromItem(Items.field_8567))
			.offerTo(consumer, "baked_potato_from_" + string);
		CookingRecipeJsonFactory.create(Ingredient.ofItems(Items.field_8504), Items.field_8752, 0.35F, i, cookingRecipeSerializer)
			.criterion("has_rabbit", conditionsFromItem(Items.field_8504))
			.offerTo(consumer, "cooked_rabbit_from_" + string);
	}

	private static EnterBlockCriterion.Conditions requireEnteringFluid(Block block) {
		return new EnterBlockCriterion.Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
	}

	private static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible itemConvertible) {
		return conditionsFromItemPredicates(ItemPredicate.Builder.create().item(itemConvertible).build());
	}

	private static InventoryChangedCriterion.Conditions conditionsFromTag(Tag<Item> tag) {
		return conditionsFromItemPredicates(ItemPredicate.Builder.create().tag(tag).build());
	}

	private static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate... itemPredicates) {
		return new InventoryChangedCriterion.Conditions(
			EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, itemPredicates
		);
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
