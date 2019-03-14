package net.minecraft.recipe;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager implements SynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final int PREFIX_LENGTH = "recipes/".length();
	public static final int SUFFIX_LENGTH = ".json".length();
	private final Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipeMap = SystemUtil.consume(
		Maps.<RecipeType<?>, Map<Identifier, Recipe<?>>>newHashMap(), RecipeManager::method_17719
	);
	private boolean hadErrors;

	@Override
	public void apply(ResourceManager resourceManager) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		this.hadErrors = false;
		method_17719(this.recipeMap);

		for (Identifier identifier : resourceManager.findResources("recipes", stringx -> stringx.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(PREFIX_LENGTH, string.length() - SUFFIX_LENGTH));

			try {
				Resource resource = resourceManager.getResource(identifier);
				Throwable var8 = null;

				try {
					JsonObject jsonObject = JsonHelper.deserialize(gson, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
					if (jsonObject == null) {
						LOGGER.error("Couldn't load recipe {} as it's null or empty", identifier2);
					} else {
						this.add(deserialize(identifier2, jsonObject));
					}
				} catch (Throwable var19) {
					var8 = var19;
					throw var19;
				} finally {
					if (resource != null) {
						if (var8 != null) {
							try {
								resource.close();
							} catch (Throwable var18) {
								var8.addSuppressed(var18);
							}
						} else {
							resource.close();
						}
					}
				}
			} catch (IllegalArgumentException | JsonParseException var21) {
				LOGGER.error("Parsing error loading recipe {}", identifier2, var21);
				this.hadErrors = true;
			} catch (IOException var22) {
				LOGGER.error("Couldn't read custom advancement {} from {}", identifier2, identifier, var22);
				this.hadErrors = true;
			}
		}

		LOGGER.info("Loaded {} recipes", this.recipeMap.size());
	}

	public void add(Recipe<?> recipe) {
		Map<Identifier, Recipe<?>> map = (Map<Identifier, Recipe<?>>)this.recipeMap.get(recipe.getType());
		if (map.containsKey(recipe.getId())) {
			throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
		} else {
			map.put(recipe.getId(), recipe);
		}
	}

	public <C extends Inventory, T extends Recipe<C>> Optional<T> get(RecipeType<T> recipeType, C inventory, World world) {
		return this.method_17717(recipeType).values().stream().flatMap(recipe -> SystemUtil.method_17815(recipeType.get(recipe, world, inventory))).findFirst();
	}

	public <C extends Inventory, T extends Recipe<C>> List<T> method_17877(RecipeType<T> recipeType, C inventory, World world) {
		return (List<T>)this.method_17717(recipeType)
			.values()
			.stream()
			.flatMap(recipe -> SystemUtil.method_17815(recipeType.get(recipe, world, inventory)))
			.sorted(Comparator.comparing(recipe -> recipe.getOutput().getTranslationKey()))
			.collect(Collectors.toList());
	}

	private <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> method_17717(RecipeType<T> recipeType) {
		return (Map<Identifier, Recipe<C>>)this.recipeMap.getOrDefault(recipeType, Maps.newHashMap());
	}

	public <C extends Inventory, T extends Recipe<C>> DefaultedList<ItemStack> method_8128(RecipeType<T> recipeType, C inventory, World world) {
		Optional<T> optional = this.get(recipeType, inventory, world);
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

	@Environment(EnvType.CLIENT)
	public void clear() {
		method_17719(this.recipeMap);
	}

	public static Recipe<?> deserialize(Identifier identifier, JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "type");
		return ((RecipeSerializer)Registry.RECIPE_SERIALIZER
				.getOrEmpty(new Identifier(string))
				.orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'")))
			.read(identifier, jsonObject);
	}

	private static void method_17719(Map<RecipeType<?>, Map<Identifier, Recipe<?>>> map) {
		map.clear();

		for (RecipeType<?> recipeType : Registry.RECIPE_TYPE) {
			map.put(recipeType, Maps.newHashMap());
		}
	}
}
