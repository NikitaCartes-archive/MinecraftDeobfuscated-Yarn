/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
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

public class RecipeManager
implements SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int PREFIX_LENGTH = "recipes/".length();
    public static final int SUFFIX_LENGTH = ".json".length();
    private final Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipeMap = SystemUtil.consume(Maps.newHashMap(), RecipeManager::clear);
    private boolean hadErrors;

    @Override
    public void apply(ResourceManager resourceManager) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.hadErrors = false;
        RecipeManager.clear(this.recipeMap);
        for (Identifier identifier : resourceManager.findResources("recipes", string -> string.endsWith(".json"))) {
            String string2 = identifier.getPath();
            Identifier identifier2 = new Identifier(identifier.getNamespace(), string2.substring(PREFIX_LENGTH, string2.length() - SUFFIX_LENGTH));
            try {
                Resource resource = resourceManager.getResource(identifier);
                Throwable throwable = null;
                try {
                    JsonObject jsonObject = JsonHelper.deserialize(gson, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                    if (jsonObject == null) {
                        LOGGER.error("Couldn't load recipe {} as it's null or empty", (Object)identifier2);
                        continue;
                    }
                    this.add(RecipeManager.deserialize(identifier2, jsonObject));
                } catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                } finally {
                    if (resource == null) continue;
                    if (throwable != null) {
                        try {
                            resource.close();
                        } catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    resource.close();
                }
            } catch (JsonParseException | IllegalArgumentException runtimeException) {
                LOGGER.error("Parsing error loading recipe {}", (Object)identifier2, (Object)runtimeException);
                this.hadErrors = true;
            } catch (IOException iOException) {
                LOGGER.error("Couldn't read custom advancement {} from {}", (Object)identifier2, (Object)identifier, (Object)iOException);
                this.hadErrors = true;
            }
        }
        LOGGER.info("Loaded {} recipes", (Object)this.recipeMap.size());
    }

    public void add(Recipe<?> recipe) {
        Map<Identifier, Recipe<?>> map = this.recipeMap.get(recipe.getType());
        if (map.containsKey(recipe.getId())) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
        }
        map.put(recipe.getId(), recipe);
    }

    public <C extends Inventory, T extends Recipe<C>> Optional<T> getFirstMatch(RecipeType<T> recipeType, C inventory, World world) {
        return this.getAllForType(recipeType).values().stream().flatMap(recipe -> SystemUtil.stream(recipeType.get(recipe, world, inventory))).findFirst();
    }

    public <C extends Inventory, T extends Recipe<C>> List<T> getAllMatches(RecipeType<T> recipeType, C inventory, World world) {
        return this.getAllForType(recipeType).values().stream().flatMap(recipe -> SystemUtil.stream(recipeType.get(recipe, world, inventory))).sorted(Comparator.comparing(recipe -> recipe.getOutput().getTranslationKey())).collect(Collectors.toList());
    }

    private <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllForType(RecipeType<T> recipeType) {
        return this.recipeMap.getOrDefault(recipeType, Maps.newHashMap());
    }

    public <C extends Inventory, T extends Recipe<C>> DefaultedList<ItemStack> getRemainingStacks(RecipeType<T> recipeType, C inventory, World world) {
        Optional<T> optional = this.getFirstMatch(recipeType, inventory, world);
        if (optional.isPresent()) {
            return ((Recipe)optional.get()).getRemainingStacks(inventory);
        }
        DefaultedList<ItemStack> defaultedList = DefaultedList.create(inventory.getInvSize(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            defaultedList.set(i, inventory.getInvStack(i));
        }
        return defaultedList;
    }

    public Optional<? extends Recipe<?>> get(Identifier identifier) {
        return this.recipeMap.values().stream().map(map -> (Recipe)map.get(identifier)).filter(Objects::nonNull).findFirst();
    }

    public Collection<Recipe<?>> values() {
        return this.recipeMap.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
    }

    public Stream<Identifier> keys() {
        return this.recipeMap.values().stream().flatMap(map -> map.keySet().stream());
    }

    @Environment(value=EnvType.CLIENT)
    public void clear() {
        RecipeManager.clear(this.recipeMap);
    }

    public static Recipe<?> deserialize(Identifier identifier, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "type");
        return Registry.RECIPE_SERIALIZER.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'")).read(identifier, jsonObject);
    }

    private static void clear(Map<RecipeType<?>, Map<Identifier, Recipe<?>>> map) {
        map.clear();
        for (RecipeType recipeType : Registry.RECIPE_TYPE) {
            map.put(recipeType, Maps.newHashMap());
        }
    }
}

