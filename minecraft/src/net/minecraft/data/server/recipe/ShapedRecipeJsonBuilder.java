package net.minecraft.data.server.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

public class ShapedRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RegistryEntryLookup<Item> registryLookup;
	private final RecipeCategory category;
	private final Item output;
	private final int count;
	private final List<String> pattern = Lists.<String>newArrayList();
	private final Map<Character, Ingredient> inputs = Maps.<Character, Ingredient>newLinkedHashMap();
	private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap();
	@Nullable
	private String group;
	private boolean showNotification = true;

	private ShapedRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output, int count) {
		this.registryLookup = registryLookup;
		this.category = category;
		this.output = output.asItem();
		this.count = count;
	}

	public static ShapedRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output) {
		return create(registryLookup, category, output, 1);
	}

	public static ShapedRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output, int count) {
		return new ShapedRecipeJsonBuilder(registryLookup, category, output, count);
	}

	public ShapedRecipeJsonBuilder input(Character c, TagKey<Item> tag) {
		return this.input(c, Ingredient.fromTag(this.registryLookup.getOrThrow(tag)));
	}

	public ShapedRecipeJsonBuilder input(Character c, ItemConvertible item) {
		return this.input(c, Ingredient.ofItem(item));
	}

	public ShapedRecipeJsonBuilder input(Character c, Ingredient ingredient) {
		if (this.inputs.containsKey(c)) {
			throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
		} else if (c == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.inputs.put(c, ingredient);
			return this;
		}
	}

	public ShapedRecipeJsonBuilder pattern(String patternStr) {
		if (!this.pattern.isEmpty() && patternStr.length() != ((String)this.pattern.get(0)).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.pattern.add(patternStr);
			return this;
		}
	}

	public ShapedRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
		this.criteria.put(string, advancementCriterion);
		return this;
	}

	public ShapedRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	public ShapedRecipeJsonBuilder showNotification(boolean showNotification) {
		this.showNotification = showNotification;
		return this;
	}

	@Override
	public Item getOutputItem() {
		return this.output;
	}

	@Override
	public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
		RawShapedRecipe rawShapedRecipe = this.validate(recipeKey);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
			.rewards(AdvancementRewards.Builder.recipe(recipeKey))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		this.criteria.forEach(builder::criterion);
		ShapedRecipe shapedRecipe = new ShapedRecipe(
			(String)Objects.requireNonNullElse(this.group, ""),
			CraftingRecipeJsonBuilder.toCraftingCategory(this.category),
			rawShapedRecipe,
			new ItemStack(this.output, this.count),
			this.showNotification
		);
		exporter.accept(recipeKey, shapedRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + this.category.getName() + "/")));
	}

	private RawShapedRecipe validate(RegistryKey<Recipe<?>> recipeKey) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeKey.getValue());
		} else {
			return RawShapedRecipe.create(this.inputs, this.pattern);
		}
	}
}
