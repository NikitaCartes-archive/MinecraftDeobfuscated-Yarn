package net.minecraft.recipe;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager implements ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final int PREFIX_LENGTH = "recipes/".length();
	public static final int SUFFIX_LENGTH = ".json".length();
	private final Map<Identifier, Recipe> recipeMap = Maps.<Identifier, Recipe>newHashMap();
	private boolean hadErrors;

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		this.hadErrors = false;
		this.recipeMap.clear();

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
						this.add(RecipeSerializers.fromJson(identifier2, jsonObject));
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

	public void add(Recipe recipe) {
		if (this.recipeMap.containsKey(recipe.getId())) {
			throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
		} else {
			this.recipeMap.put(recipe.getId(), recipe);
		}
	}

	public ItemStack craft(Inventory inventory, World world) {
		for (Recipe recipe : this.recipeMap.values()) {
			if (recipe.matches(inventory, world)) {
				return recipe.craft(inventory);
			}
		}

		return ItemStack.EMPTY;
	}

	@Nullable
	public Recipe get(Inventory inventory, World world) {
		for (Recipe recipe : this.recipeMap.values()) {
			if (recipe.matches(inventory, world)) {
				return recipe;
			}
		}

		return null;
	}

	public DefaultedList<ItemStack> method_8128(Inventory inventory, World world) {
		for (Recipe recipe : this.recipeMap.values()) {
			if (recipe.matches(inventory, world)) {
				return recipe.getRemainingStacks(inventory);
			}
		}

		DefaultedList<ItemStack> defaultedList = DefaultedList.create(inventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			defaultedList.set(i, inventory.getInvStack(i));
		}

		return defaultedList;
	}

	@Nullable
	public Recipe get(Identifier identifier) {
		return (Recipe)this.recipeMap.get(identifier);
	}

	public Collection<Recipe> values() {
		return this.recipeMap.values();
	}

	public Collection<Identifier> keys() {
		return this.recipeMap.keySet();
	}

	@Environment(EnvType.CLIENT)
	public void clear() {
		this.recipeMap.clear();
	}
}
