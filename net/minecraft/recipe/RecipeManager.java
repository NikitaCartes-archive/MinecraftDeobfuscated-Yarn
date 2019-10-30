/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager
extends JsonDataLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes = ImmutableMap.of();
    private boolean errored;

    public RecipeManager() {
        super(GSON, "recipes");
    }

    protected void method_20705(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
        this.errored = false;
        HashMap<RecipeType, ImmutableMap.Builder> map2 = Maps.newHashMap();
        for (Map.Entry<Identifier, JsonObject> entry2 : map.entrySet()) {
            Identifier identifier = entry2.getKey();
            try {
                Recipe<?> recipe = RecipeManager.deserialize(identifier, entry2.getValue());
                map2.computeIfAbsent(recipe.getType(), recipeType -> ImmutableMap.builder()).put(identifier, recipe);
            } catch (JsonParseException | IllegalArgumentException runtimeException) {
                LOGGER.error("Parsing error loading recipe {}", (Object)identifier, (Object)runtimeException);
            }
        }
        this.recipes = map2.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((ImmutableMap.Builder)entry.getValue()).build()));
        LOGGER.info("Loaded {} recipes", (Object)map2.size());
    }

    public <C extends Inventory, T extends Recipe<C>> Optional<T> getFirstMatch(RecipeType<T> recipeType, C inventory, World world) {
        return this.getAllOfType(recipeType).values().stream().flatMap(recipe -> Util.stream(recipeType.get(recipe, world, inventory))).findFirst();
    }

    public <C extends Inventory, T extends Recipe<C>> List<T> getAllMatches(RecipeType<T> recipeType, C inventory, World world) {
        return this.getAllOfType(recipeType).values().stream().flatMap(recipe -> Util.stream(recipeType.get(recipe, world, inventory))).sorted(Comparator.comparing(recipe -> recipe.getOutput().getTranslationKey())).collect(Collectors.toList());
    }

    private <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllOfType(RecipeType<T> recipeType) {
        return this.recipes.getOrDefault(recipeType, Collections.emptyMap());
    }

    public <C extends Inventory, T extends Recipe<C>> DefaultedList<ItemStack> getRemainingStacks(RecipeType<T> recipeType, C inventory, World world) {
        Optional<T> optional = this.getFirstMatch(recipeType, inventory, world);
        if (optional.isPresent()) {
            return ((Recipe)optional.get()).getRemainingStacks(inventory);
        }
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.getInvSize(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            defaultedList.set(i, inventory.getInvStack(i));
        }
        return defaultedList;
    }

    public Optional<? extends Recipe<?>> get(Identifier identifier) {
        return this.recipes.values().stream().map(map -> (Recipe)map.get(identifier)).filter(Objects::nonNull).findFirst();
    }

    public Collection<Recipe<?>> values() {
        return this.recipes.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
    }

    public Stream<Identifier> keys() {
        return this.recipes.values().stream().flatMap(map -> map.keySet().stream());
    }

    public static Recipe<?> deserialize(Identifier identifier, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "type");
        return Registry.RECIPE_SERIALIZER.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'")).read(identifier, jsonObject);
    }

    @Environment(value=EnvType.CLIENT)
    public void setRecipes(Iterable<Recipe<?>> iterable) {
        this.errored = false;
        HashMap map = Maps.newHashMap();
        iterable.forEach(recipe -> {
            Map map2 = map.computeIfAbsent(recipe.getType(), recipeType -> Maps.newHashMap());
            Recipe recipe2 = map2.put(recipe.getId(), recipe);
            if (recipe2 != null) {
                throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
            }
        });
        this.recipes = ImmutableMap.copyOf(map);
    }
}

