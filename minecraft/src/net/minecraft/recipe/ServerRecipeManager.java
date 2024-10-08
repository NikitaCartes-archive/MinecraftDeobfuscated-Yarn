package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.slf4j.Logger;

/**
 * A recipe manager allows easier use of recipes, such as finding matches and
 * remainders. It is also integrated with a recipe loader, which loads recipes
 * from data packs' JSON files.
 */
public class ServerRecipeManager extends SinglePreparationResourceReloader<PreparedRecipes> implements RecipeManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Map<RegistryKey<RecipePropertySet>, ServerRecipeManager.SoleIngredientGetter> SOLE_INGREDIENT_GETTERS = Map.of(
		RecipePropertySet.SMITHING_ADDITION,
		(ServerRecipeManager.SoleIngredientGetter)recipe -> recipe instanceof SmithingRecipe smithingRecipe ? smithingRecipe.addition() : Optional.empty(),
		RecipePropertySet.SMITHING_BASE,
		(ServerRecipeManager.SoleIngredientGetter)recipe -> recipe instanceof SmithingRecipe smithingRecipe ? smithingRecipe.base() : Optional.empty(),
		RecipePropertySet.SMITHING_TEMPLATE,
		(ServerRecipeManager.SoleIngredientGetter)recipe -> recipe instanceof SmithingRecipe smithingRecipe ? smithingRecipe.template() : Optional.empty(),
		RecipePropertySet.FURNACE_INPUT,
		cookingIngredientGetter(RecipeType.SMELTING),
		RecipePropertySet.BLAST_FURNACE_INPUT,
		cookingIngredientGetter(RecipeType.BLASTING),
		RecipePropertySet.SMOKER_INPUT,
		cookingIngredientGetter(RecipeType.SMOKING),
		RecipePropertySet.CAMPFIRE_INPUT,
		cookingIngredientGetter(RecipeType.CAMPFIRE_COOKING)
	);
	private final RegistryWrapper.WrapperLookup registries;
	private PreparedRecipes preparedRecipes = PreparedRecipes.EMPTY;
	private Map<RegistryKey<RecipePropertySet>, RecipePropertySet> propertySets = Map.of();
	private CuttingRecipeDisplay.Grouping<StonecuttingRecipe> stonecutterRecipes = CuttingRecipeDisplay.Grouping.empty();
	private List<ServerRecipeManager.ServerRecipe> recipes = List.of();
	private Map<RegistryKey<Recipe<?>>, List<ServerRecipeManager.ServerRecipe>> recipesByKey = Map.of();

	public ServerRecipeManager(RegistryWrapper.WrapperLookup registries) {
		this.registries = registries;
	}

	protected PreparedRecipes prepare(ResourceManager resourceManager, Profiler profiler) {
		SortedMap<Identifier, Recipe<?>> sortedMap = new TreeMap();
		JsonDataLoader.load(resourceManager, RegistryKeys.getPath(RegistryKeys.RECIPE), this.registries.getOps(JsonOps.INSTANCE), Recipe.CODEC, sortedMap);
		List<RecipeEntry<?>> list = new ArrayList(sortedMap.size());
		sortedMap.forEach((id, recipe) -> {
			RegistryKey<Recipe<?>> registryKey = RegistryKey.of(RegistryKeys.RECIPE, id);
			RecipeEntry<?> recipeEntry = new RecipeEntry(registryKey, recipe);
			list.add(recipeEntry);
		});
		return PreparedRecipes.of(list);
	}

	protected void apply(PreparedRecipes preparedRecipes, ResourceManager resourceManager, Profiler profiler) {
		this.preparedRecipes = preparedRecipes;
		LOGGER.info("Loaded {} recipes", preparedRecipes.recipes().size());
	}

	public void initialize(FeatureSet features) {
		List<CuttingRecipeDisplay.GroupEntry<StonecuttingRecipe>> list = new ArrayList();
		List<ServerRecipeManager.PropertySetBuilder> list2 = SOLE_INGREDIENT_GETTERS.entrySet()
			.stream()
			.map(
				entry -> new ServerRecipeManager.PropertySetBuilder(
						(RegistryKey<RecipePropertySet>)entry.getKey(), (ServerRecipeManager.SoleIngredientGetter)entry.getValue()
					)
			)
			.toList();
		this.preparedRecipes
			.recipes()
			.forEach(
				recipe -> {
					Recipe<?> recipe2 = recipe.value();
					if (!recipe2.isIgnoredInRecipeBook() && recipe2.getIngredientPlacement().hasNoPlacement()) {
						LOGGER.warn("Recipe {} can't be placed due to empty ingredients and will be ignored", recipe.id().getValue());
					} else {
						list2.forEach(builder -> builder.accept(recipe2));
						if (recipe2 instanceof StonecuttingRecipe stonecuttingRecipe
							&& isEnabled(features, stonecuttingRecipe.ingredient())
							&& stonecuttingRecipe.createResultDisplay().isEnabled(features)) {
							list.add(
								new CuttingRecipeDisplay.GroupEntry(
									stonecuttingRecipe.ingredient(), new CuttingRecipeDisplay(stonecuttingRecipe.createResultDisplay(), Optional.of(recipe))
								)
							);
						}
					}
				}
			);
		this.propertySets = (Map<RegistryKey<RecipePropertySet>, RecipePropertySet>)list2.stream()
			.collect(Collectors.toUnmodifiableMap(builder -> builder.propertySetKey, builder -> builder.build(features)));
		this.stonecutterRecipes = new CuttingRecipeDisplay.Grouping<>(list);
		this.recipes = collectServerRecipes(this.preparedRecipes.recipes(), features);
		this.recipesByKey = (Map<RegistryKey<Recipe<?>>, List<ServerRecipeManager.ServerRecipe>>)this.recipes
			.stream()
			.collect(Collectors.groupingBy(recipe -> recipe.parent.id(), IdentityHashMap::new, Collectors.toList()));
	}

	static List<Ingredient> filterIngredients(FeatureSet features, List<Ingredient> ingredients) {
		ingredients.removeIf(ingredient -> !isEnabled(features, ingredient));
		return ingredients;
	}

	private static boolean isEnabled(FeatureSet features, Ingredient ingredient) {
		return ingredient.getMatchingStacks().stream().allMatch(entry -> ((Item)entry.value()).isEnabled(features));
	}

	public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeEntry<T>> getFirstMatch(
		RecipeType<T> type, I input, World world, @Nullable RegistryKey<Recipe<?>> recipe
	) {
		RecipeEntry<T> recipeEntry = recipe != null ? this.get(type, recipe) : null;
		return this.getFirstMatch(type, input, world, recipeEntry);
	}

	public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeEntry<T>> getFirstMatch(
		RecipeType<T> type, I input, World world, @Nullable RecipeEntry<T> recipe
	) {
		return recipe != null && recipe.value().matches(input, world) ? Optional.of(recipe) : this.getFirstMatch(type, input, world);
	}

	/**
	 * {@return a recipe of the given {@code type} that match the given
	 * {@code inventory} and {@code world}}
	 * 
	 * <p>If there are multiple matching recipes, the result is arbitrary,
	 * but this method will return the same result unless the recipes in this
	 * manager are updated.
	 * 
	 * @param type the desired recipe type
	 * @param world the input world
	 */
	public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeEntry<T>> getFirstMatch(RecipeType<T> type, I input, World world) {
		return this.preparedRecipes.find(type, input, world).findFirst();
	}

	/**
	 * {@return a recipe with the given {@code id}, or empty if there is no such recipe}
	 */
	public Optional<RecipeEntry<?>> get(RegistryKey<Recipe<?>> key) {
		return Optional.ofNullable(this.preparedRecipes.get(key));
	}

	/**
	 * {@return a recipe with the given {@code id} and {@code type}, or empty if there is no such recipe}
	 * 
	 * @param type the type of the desired recipe
	 */
	@Nullable
	private <T extends Recipe<?>> RecipeEntry<T> get(RecipeType<T> type, RegistryKey<Recipe<?>> key) {
		RecipeEntry<?> recipeEntry = this.preparedRecipes.get(key);
		return (RecipeEntry<T>)(recipeEntry != null && recipeEntry.value().getType().equals(type) ? recipeEntry : null);
	}

	public Map<RegistryKey<RecipePropertySet>, RecipePropertySet> getPropertySets() {
		return this.propertySets;
	}

	public CuttingRecipeDisplay.Grouping<StonecuttingRecipe> getStonecutterRecipeForSync() {
		return this.stonecutterRecipes;
	}

	@Override
	public RecipePropertySet getPropertySet(RegistryKey<RecipePropertySet> key) {
		return (RecipePropertySet)this.propertySets.getOrDefault(key, RecipePropertySet.EMPTY);
	}

	@Override
	public CuttingRecipeDisplay.Grouping<StonecuttingRecipe> getStonecutterRecipes() {
		return this.stonecutterRecipes;
	}

	/**
	 * {@return all recipes in this manager}
	 * 
	 * <p>The returned set does not update with the manager. Modifications to the
	 * returned set does not affect this manager.
	 */
	public Collection<RecipeEntry<?>> values() {
		return this.preparedRecipes.recipes();
	}

	@Nullable
	public ServerRecipeManager.ServerRecipe get(NetworkRecipeId id) {
		return (ServerRecipeManager.ServerRecipe)this.recipes.get(id.index());
	}

	public void forEachRecipeDisplay(RegistryKey<Recipe<?>> key, Consumer<RecipeDisplayEntry> action) {
		List<ServerRecipeManager.ServerRecipe> list = (List<ServerRecipeManager.ServerRecipe>)this.recipesByKey.get(key);
		if (list != null) {
			list.forEach(recipe -> action.accept(recipe.display));
		}
	}

	/**
	 * Reads a recipe from a JSON object.
	 * 
	 * @implNote Even though a recipe's {@linkplain Recipe#getSerializer() serializer}
	 * is stored in a {@code type} field in the JSON format and referred so in this
	 * method, its registry has key {@code minecraft:root/minecraft:recipe_serializer}
	 * and is thus named.
	 * 
	 * @throws com.google.gson.JsonParseException if the recipe JSON is invalid
	 * @return the read recipe
	 */
	@VisibleForTesting
	protected static RecipeEntry<?> deserialize(RegistryKey<Recipe<?>> key, JsonObject json, RegistryWrapper.WrapperLookup registries) {
		Recipe<?> recipe = Recipe.CODEC.parse(registries.getOps(JsonOps.INSTANCE), json).getOrThrow(JsonParseException::new);
		return new RecipeEntry<>(key, recipe);
	}

	/**
	 * Creates a cached match getter. This is optimized for getting matches of the same
	 * recipe repeatedly, such as furnaces.
	 */
	public static <I extends RecipeInput, T extends Recipe<I>> ServerRecipeManager.MatchGetter<I, T> createCachedMatchGetter(RecipeType<T> type) {
		return new ServerRecipeManager.MatchGetter<I, T>() {
			@Nullable
			private RegistryKey<Recipe<?>> id;

			@Override
			public Optional<RecipeEntry<T>> getFirstMatch(I input, ServerWorld world) {
				ServerRecipeManager serverRecipeManager = world.getRecipeManager();
				Optional<RecipeEntry<T>> optional = serverRecipeManager.getFirstMatch(type, input, world, this.id);
				if (optional.isPresent()) {
					RecipeEntry<T> recipeEntry = (RecipeEntry<T>)optional.get();
					this.id = recipeEntry.id();
					return Optional.of(recipeEntry);
				} else {
					return Optional.empty();
				}
			}
		};
	}

	/**
	 * Filters recipes by {@code enabledFeatures} and assigns an integer
	 * ID to each recipe and recipe group.
	 */
	private static List<ServerRecipeManager.ServerRecipe> collectServerRecipes(Iterable<RecipeEntry<?>> recipes, FeatureSet enabledFeatures) {
		List<ServerRecipeManager.ServerRecipe> list = new ArrayList();
		Object2IntMap<String> object2IntMap = new Object2IntOpenHashMap<>();

		for (RecipeEntry<?> recipeEntry : recipes) {
			Recipe<?> recipe = recipeEntry.value();
			OptionalInt optionalInt;
			if (recipe.getGroup().isEmpty()) {
				optionalInt = OptionalInt.empty();
			} else {
				optionalInt = OptionalInt.of(object2IntMap.computeIfAbsent(recipe.getGroup(), group -> object2IntMap.size()));
			}

			Optional<List<Ingredient>> optional;
			if (recipe.isIgnoredInRecipeBook()) {
				optional = Optional.empty();
			} else {
				optional = Optional.of(recipe.getIngredientPlacement().getIngredients());
			}

			for (RecipeDisplay recipeDisplay : recipe.getDisplays()) {
				if (recipeDisplay.isEnabled(enabledFeatures)) {
					int i = list.size();
					NetworkRecipeId networkRecipeId = new NetworkRecipeId(i);
					RecipeDisplayEntry recipeDisplayEntry = new RecipeDisplayEntry(networkRecipeId, recipeDisplay, optionalInt, recipe.getRecipeBookTab(), optional);
					list.add(new ServerRecipeManager.ServerRecipe(recipeDisplayEntry, recipeEntry));
				}
			}
		}

		return list;
	}

	private static ServerRecipeManager.SoleIngredientGetter cookingIngredientGetter(RecipeType<? extends SingleStackRecipe> expectedType) {
		return recipe -> recipe.getType() == expectedType && recipe instanceof SingleStackRecipe singleStackRecipe
				? Optional.of(singleStackRecipe.ingredient())
				: Optional.empty();
	}

	public interface MatchGetter<I extends RecipeInput, T extends Recipe<I>> {
		Optional<RecipeEntry<T>> getFirstMatch(I input, ServerWorld world);
	}

	public static class PropertySetBuilder implements Consumer<Recipe<?>> {
		final RegistryKey<RecipePropertySet> propertySetKey;
		private final ServerRecipeManager.SoleIngredientGetter ingredientGetter;
		private final List<Ingredient> ingredients = new ArrayList();

		protected PropertySetBuilder(RegistryKey<RecipePropertySet> propertySetKey, ServerRecipeManager.SoleIngredientGetter ingredientGetter) {
			this.propertySetKey = propertySetKey;
			this.ingredientGetter = ingredientGetter;
		}

		public void accept(Recipe<?> recipe) {
			this.ingredientGetter.apply(recipe).ifPresent(this.ingredients::add);
		}

		public RecipePropertySet build(FeatureSet enabledFeatures) {
			return RecipePropertySet.of(ServerRecipeManager.filterIngredients(enabledFeatures, this.ingredients));
		}
	}

	public static record ServerRecipe(RecipeDisplayEntry display, RecipeEntry<?> parent) {
	}

	@FunctionalInterface
	public interface SoleIngredientGetter {
		Optional<Ingredient> apply(Recipe<?> recipe);
	}
}
