package net.minecraft.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager extends JsonDataLoader {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger LOGGER = LogManager.getLogger();
	private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipeMap = ImmutableMap.of();
	private boolean hadErrors;

	public RecipeManager() {
		super(GSON, "recipes");
	}

	protected void method_20705(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
		this.hadErrors = false;
		Map<RecipeType<?>, Builder<Identifier, Recipe<?>>> map2 = Maps.<RecipeType<?>, Builder<Identifier, Recipe<?>>>newHashMap();

		for (Entry<Identifier, JsonObject> entry : map.entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();

			try {
				Recipe<?> recipe = deserialize(identifier, (JsonObject)entry.getValue());
				((Builder)map2.computeIfAbsent(recipe.getType(), recipeType -> ImmutableMap.builder())).put(identifier, recipe);
			} catch (IllegalArgumentException | JsonParseException var9) {
				LOGGER.error("Parsing error loading recipe {}", identifier, var9);
			}
		}

		this.recipeMap = (Map<RecipeType<?>, Map<Identifier, Recipe<?>>>)map2.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Entry::getKey, entryx -> ((Builder)entryx.getValue()).build()));
		LOGGER.info("Loaded {} recipes", map2.size());
	}

	public <C extends Inventory, T extends Recipe<C>> Optional<T> getFirstMatch(RecipeType<T> recipeType, C inventory, World world) {
		return this.getAllForType(recipeType).values().stream().flatMap(recipe -> SystemUtil.stream(recipeType.get(recipe, world, inventory))).findFirst();
	}

	public <C extends Inventory, T extends Recipe<C>> List<T> getAllMatches(RecipeType<T> recipeType, C inventory, World world) {
		return (List<T>)this.getAllForType(recipeType)
			.values()
			.stream()
			.flatMap(recipe -> SystemUtil.stream(recipeType.get(recipe, world, inventory)))
			.sorted(Comparator.comparing(recipe -> recipe.getOutput().getTranslationKey()))
			.collect(Collectors.toList());
	}

	private <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllForType(RecipeType<T> recipeType) {
		return (Map<Identifier, Recipe<C>>)this.recipeMap.getOrDefault(recipeType, Collections.emptyMap());
	}

	public <C extends Inventory, T extends Recipe<C>> DefaultedList<ItemStack> getRemainingStacks(RecipeType<T> recipeType, C inventory, World world) {
		Optional<T> optional = this.getFirstMatch(recipeType, inventory, world);
		if (optional.isPresent()) {
			return ((Recipe)optional.get()).getRemainingStacks(inventory);
		} else {
			DefaultedList<ItemStack> defaultedList = DefaultedList.create(inventory.getInvSize(), ItemStack.EMPTY);

			for (int i = 0; i < defaultedList.size(); i++) {
				defaultedList.set(i, inventory.getInvStack(i));
			}

			return defaultedList;
		}
	}

	public Optional<? extends Recipe<?>> get(Identifier identifier) {
		return this.recipeMap.values().stream().map(map -> (Recipe)map.get(identifier)).filter(Objects::nonNull).findFirst();
	}

	public Collection<Recipe<?>> values() {
		return (Collection<Recipe<?>>)this.recipeMap.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
	}

	public Stream<Identifier> keys() {
		return this.recipeMap.values().stream().flatMap(map -> map.keySet().stream());
	}

	public static Recipe<?> deserialize(Identifier identifier, JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "type");
		return ((RecipeSerializer)Registry.RECIPE_SERIALIZER
				.getOrEmpty(new Identifier(string))
				.orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'")))
			.read(identifier, jsonObject);
	}

	@Environment(EnvType.CLIENT)
	public void method_20702(Iterable<Recipe<?>> iterable) {
		this.hadErrors = false;
		Map<RecipeType<?>, Map<Identifier, Recipe<?>>> map = Maps.<RecipeType<?>, Map<Identifier, Recipe<?>>>newHashMap();
		iterable.forEach(recipe -> {
			Map<Identifier, Recipe<?>> map2 = (Map<Identifier, Recipe<?>>)map.computeIfAbsent(recipe.getType(), recipeType -> Maps.newHashMap());
			Recipe<?> recipe2 = (Recipe<?>)map2.put(recipe.getId(), recipe);
			if (recipe2 != null) {
				throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
			}
		});
		this.recipeMap = ImmutableMap.copyOf(map);
	}
}
