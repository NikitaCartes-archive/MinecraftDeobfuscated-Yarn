package net.minecraft.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.slf4j.Logger;

/**
 * A recipe manager allows easier use of recipes, such as finding matches and
 * remainders. It is also integrated with a recipe loader, which loads recipes
 * from data packs' JSON files.
 */
public class RecipeManager extends JsonDataLoader {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger LOGGER = LogUtils.getLogger();
	private Map<RecipeType<?>, Map<Identifier, RecipeEntry<?>>> recipes = ImmutableMap.of();
	private Map<Identifier, RecipeEntry<?>> recipesById = ImmutableMap.of();
	/**
	 * This isn't quite indicating an errored state; its value is only set to
	 * {@code false} and is never {@code true}, and isn't used anywhere.
	 */
	private boolean errored;

	public RecipeManager() {
		super(GSON, "recipes");
	}

	protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
		this.errored = false;
		Map<RecipeType<?>, Builder<Identifier, RecipeEntry<?>>> map2 = Maps.<RecipeType<?>, Builder<Identifier, RecipeEntry<?>>>newHashMap();
		Builder<Identifier, RecipeEntry<?>> builder = ImmutableMap.builder();

		for (Entry<Identifier, JsonElement> entry : map.entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();

			try {
				RecipeEntry<?> recipeEntry = deserialize(identifier, JsonHelper.asObject((JsonElement)entry.getValue(), "top element"));
				((Builder)map2.computeIfAbsent(recipeEntry.value().getType(), recipeType -> ImmutableMap.builder())).put(identifier, recipeEntry);
				builder.put(identifier, recipeEntry);
			} catch (IllegalArgumentException | JsonParseException var10) {
				LOGGER.error("Parsing error loading recipe {}", identifier, var10);
			}
		}

		this.recipes = (Map<RecipeType<?>, Map<Identifier, RecipeEntry<?>>>)map2.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Entry::getKey, entryx -> ((Builder)entryx.getValue()).build()));
		this.recipesById = builder.build();
		LOGGER.info("Loaded {} recipes", map2.size());
	}

	/**
	 * {@return the {@link #errored} field} This is unused in vanilla and will only
	 * return {@code false} without mods.
	 */
	public boolean isErrored() {
		return this.errored;
	}

	/**
	 * {@return a recipe of the given {@code type} that match the given
	 * {@code inventory} and {@code world}}
	 * 
	 * <p>If there are multiple matching recipes, the result is arbitrary,
	 * but this method will return the same result unless the recipes in this
	 * manager are updated.
	 * 
	 * @param world the input world
	 * @param type the desired recipe type
	 * @param inventory the input inventory
	 */
	public <C extends Inventory, T extends Recipe<C>> Optional<RecipeEntry<T>> getFirstMatch(RecipeType<T> type, C inventory, World world) {
		return this.getAllOfType(type).values().stream().filter(recipe -> recipe.value().matches(inventory, world)).findFirst();
	}

	public <C extends Inventory, T extends Recipe<C>> Optional<Pair<Identifier, RecipeEntry<T>>> getFirstMatch(
		RecipeType<T> type, C inventory, World world, @Nullable Identifier id
	) {
		Map<Identifier, RecipeEntry<T>> map = this.getAllOfType(type);
		if (id != null) {
			RecipeEntry<T> recipeEntry = (RecipeEntry<T>)map.get(id);
			if (recipeEntry != null && recipeEntry.value().matches(inventory, world)) {
				return Optional.of(Pair.of(id, recipeEntry));
			}
		}

		return map.entrySet()
			.stream()
			.filter(entry -> ((RecipeEntry)entry.getValue()).value().matches(inventory, world))
			.findFirst()
			.map(entry -> Pair.of((Identifier)entry.getKey(), (RecipeEntry)entry.getValue()));
	}

	/**
	 * Creates a list of all recipes of the given {@code type}. The list has an
	 * arbitrary order.
	 * 
	 * <p>This list does not update with this manager. Modifications to
	 * the returned list do not affect this manager.
	 * 
	 * @return the created list of recipes of the given {@code type}
	 * 
	 * @param type the desired recipe type
	 */
	public <C extends Inventory, T extends Recipe<C>> List<RecipeEntry<T>> listAllOfType(RecipeType<T> type) {
		return List.copyOf(this.getAllOfType(type).values());
	}

	/**
	 * Creates a list of all recipes of the given {@code type} that match the
	 * given {@code inventory} and {@code world}. The list is ordered by the
	 * translation key of the output item stack of each recipe.
	 * 
	 * <p>This list does not update with this manager. Modifications to
	 * the returned list do not affect this manager.
	 * 
	 * @return the created list of matching recipes
	 * 
	 * @param world the input world
	 * @param inventory the input inventory
	 * @param type the desired recipe type
	 */
	public <C extends Inventory, T extends Recipe<C>> List<RecipeEntry<T>> getAllMatches(RecipeType<T> type, C inventory, World world) {
		return (List<RecipeEntry<T>>)this.getAllOfType(type)
			.values()
			.stream()
			.filter(recipe -> recipe.value().matches(inventory, world))
			.sorted(Comparator.comparing(recipeEntry -> recipeEntry.value().getResult(world.getRegistryManager()).getTranslationKey()))
			.collect(Collectors.toList());
	}

	private <C extends Inventory, T extends Recipe<C>> Map<Identifier, RecipeEntry<T>> getAllOfType(RecipeType<T> type) {
		return (Map<Identifier, RecipeEntry<T>>)this.recipes.getOrDefault(type, Collections.emptyMap());
	}

	/**
	 * {@return the remainder of a recipe of the given {@code type} that match
	 * the given {@code inventory} and {@code world}, or a shallow copy of the
	 * {@code inventory}}
	 * 
	 * <p>This retrieves the {@linkplain Recipe#getRemainder(Inventory)
	 * remainders} of {@link #getFirstMatch(RecipeType, Inventory, World)
	 * getFirstMatch(type, inventory, world)} if the match exists.
	 * 
	 * @see Recipe#getRemainder(Inventory)
	 * 
	 * @param world the input world
	 * @param inventory the input inventory
	 * @param type the desired recipe type
	 */
	public <C extends Inventory, T extends Recipe<C>> DefaultedList<ItemStack> getRemainingStacks(RecipeType<T> type, C inventory, World world) {
		Optional<RecipeEntry<T>> optional = this.getFirstMatch(type, inventory, world);
		if (optional.isPresent()) {
			return ((RecipeEntry)optional.get()).value().getRemainder(inventory);
		} else {
			DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

			for (int i = 0; i < defaultedList.size(); i++) {
				defaultedList.set(i, inventory.getStack(i));
			}

			return defaultedList;
		}
	}

	/**
	 * {@return a recipe with the given {@code id}, or empty if there is no such recipe}
	 * 
	 * @param id the ID of the desired recipe
	 */
	public Optional<RecipeEntry<?>> get(Identifier id) {
		return Optional.ofNullable((RecipeEntry)this.recipesById.get(id));
	}

	/**
	 * {@return all recipes in this manager}
	 * 
	 * <p>The returned set does not update with the manager. Modifications to the
	 * returned set does not affect this manager.
	 */
	public Collection<RecipeEntry<?>> values() {
		return (Collection<RecipeEntry<?>>)this.recipes.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
	}

	/**
	 * {@return a stream of IDs of recipes in this manager}
	 * 
	 * <p>The returned stream does not update after {@link #setRecipes(Iterable)}
	 * call.
	 * 
	 * @apiNote This is used by the command sources to suggest recipe IDs for command
	 * arguments.
	 */
	public Stream<Identifier> keys() {
		return this.recipes.values().stream().flatMap(map -> map.keySet().stream());
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
	 * @see RecipeSerializer#read
	 * 
	 * @param json the recipe JSON
	 * @param id the recipe's ID
	 */
	protected static RecipeEntry<?> deserialize(Identifier id, JsonObject json) {
		String string = JsonHelper.getString(json, "type");
		Codec<? extends Recipe<?>> codec = ((RecipeSerializer)Registries.RECIPE_SERIALIZER
				.getOrEmpty(new Identifier(string))
				.orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'")))
			.codec();
		Recipe<?> recipe = Util.getResult((DataResult<Recipe<?>>)codec.parse(JsonOps.INSTANCE, json), JsonParseException::new);
		return new RecipeEntry<>(id, recipe);
	}

	/**
	 * Sets the recipes for this recipe manager. Used by the client to set the server
	 * side recipes.
	 * 
	 * @param recipes the recipes to set
	 */
	public void setRecipes(Iterable<RecipeEntry<?>> recipes) {
		this.errored = false;
		Map<RecipeType<?>, Map<Identifier, RecipeEntry<?>>> map = Maps.<RecipeType<?>, Map<Identifier, RecipeEntry<?>>>newHashMap();
		Builder<Identifier, RecipeEntry<?>> builder = ImmutableMap.builder();
		recipes.forEach(recipe -> {
			Map<Identifier, RecipeEntry<?>> map2 = (Map<Identifier, RecipeEntry<?>>)map.computeIfAbsent(recipe.value().getType(), t -> Maps.newHashMap());
			Identifier identifier = recipe.id();
			RecipeEntry<?> recipeEntry = (RecipeEntry<?>)map2.put(identifier, recipe);
			builder.put(identifier, recipe);
			if (recipeEntry != null) {
				throw new IllegalStateException("Duplicate recipe ignored with ID " + identifier);
			}
		});
		this.recipes = ImmutableMap.copyOf(map);
		this.recipesById = builder.build();
	}

	/**
	 * Creates a cached match getter. This is optimized for getting matches of the same
	 * recipe repeatedly, such as furnaces.
	 */
	public static <C extends Inventory, T extends Recipe<C>> RecipeManager.MatchGetter<C, T> createCachedMatchGetter(RecipeType<T> type) {
		return new RecipeManager.MatchGetter<C, T>() {
			@Nullable
			private Identifier id;

			@Override
			public Optional<RecipeEntry<T>> getFirstMatch(C inventory, World world) {
				RecipeManager recipeManager = world.getRecipeManager();
				Optional<Pair<Identifier, RecipeEntry<T>>> optional = recipeManager.getFirstMatch(type, inventory, world, this.id);
				if (optional.isPresent()) {
					Pair<Identifier, RecipeEntry<T>> pair = (Pair<Identifier, RecipeEntry<T>>)optional.get();
					this.id = pair.getFirst();
					return Optional.of(pair.getSecond());
				} else {
					return Optional.empty();
				}
			}
		};
	}

	public interface MatchGetter<C extends Inventory, T extends Recipe<C>> {
		Optional<RecipeEntry<T>> getFirstMatch(C inventory, World world);
	}
}
