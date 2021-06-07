/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.recipebook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

@Environment(value=EnvType.CLIENT)
public class ClientRecipeBook
extends RecipeBook {
    private static final Logger LOGGER = LogManager.getLogger();
    private Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup = ImmutableMap.of();
    private List<RecipeResultCollection> orderedResults = ImmutableList.of();

    public void reload(Iterable<Recipe<?>> recipes) {
        Map<RecipeBookGroup, List<List<Recipe<?>>>> map = ClientRecipeBook.toGroupedMap(recipes);
        HashMap map2 = Maps.newHashMap();
        ImmutableList.Builder builder = ImmutableList.builder();
        map.forEach((recipeBookGroup, list) -> map2.put(recipeBookGroup, (List)list.stream().map(RecipeResultCollection::new).peek(builder::add).collect(ImmutableList.toImmutableList())));
        RecipeBookGroup.SEARCH_MAP.forEach((recipeBookGroup2, list) -> map2.put(recipeBookGroup2, (List)list.stream().flatMap(recipeBookGroup -> ((List)map2.getOrDefault(recipeBookGroup, ImmutableList.of())).stream()).collect(ImmutableList.toImmutableList())));
        this.resultsByGroup = ImmutableMap.copyOf(map2);
        this.orderedResults = builder.build();
    }

    private static Map<RecipeBookGroup, List<List<Recipe<?>>>> toGroupedMap(Iterable<Recipe<?>> recipes) {
        HashMap<RecipeBookGroup, List<List<Recipe<?>>>> map = Maps.newHashMap();
        HashBasedTable table = HashBasedTable.create();
        for (Recipe<?> recipe : recipes) {
            if (recipe.isIgnoredInRecipeBook() || recipe.isEmpty()) continue;
            RecipeBookGroup recipeBookGroup = ClientRecipeBook.getGroupForRecipe(recipe);
            String string = recipe.getGroup();
            if (string.isEmpty()) {
                map.computeIfAbsent(recipeBookGroup, group -> Lists.newArrayList()).add(ImmutableList.of(recipe));
                continue;
            }
            ArrayList<Recipe<?>> list = (ArrayList<Recipe<?>>)table.get((Object)recipeBookGroup, string);
            if (list == null) {
                list = Lists.newArrayList();
                table.put(recipeBookGroup, string, list);
                map.computeIfAbsent(recipeBookGroup, group -> Lists.newArrayList()).add(list);
            }
            list.add(recipe);
        }
        return map;
    }

    private static RecipeBookGroup getGroupForRecipe(Recipe<?> recipe) {
        RecipeType<?> recipeType = recipe.getType();
        if (recipeType == RecipeType.CRAFTING) {
            ItemStack itemStack = recipe.getOutput();
            ItemGroup itemGroup = itemStack.getItem().getGroup();
            if (itemGroup == ItemGroup.BUILDING_BLOCKS) {
                return RecipeBookGroup.CRAFTING_BUILDING_BLOCKS;
            }
            if (itemGroup == ItemGroup.TOOLS || itemGroup == ItemGroup.COMBAT) {
                return RecipeBookGroup.CRAFTING_EQUIPMENT;
            }
            if (itemGroup == ItemGroup.REDSTONE) {
                return RecipeBookGroup.CRAFTING_REDSTONE;
            }
            return RecipeBookGroup.CRAFTING_MISC;
        }
        if (recipeType == RecipeType.SMELTING) {
            if (recipe.getOutput().getItem().isFood()) {
                return RecipeBookGroup.FURNACE_FOOD;
            }
            if (recipe.getOutput().getItem() instanceof BlockItem) {
                return RecipeBookGroup.FURNACE_BLOCKS;
            }
            return RecipeBookGroup.FURNACE_MISC;
        }
        if (recipeType == RecipeType.BLASTING) {
            if (recipe.getOutput().getItem() instanceof BlockItem) {
                return RecipeBookGroup.BLAST_FURNACE_BLOCKS;
            }
            return RecipeBookGroup.BLAST_FURNACE_MISC;
        }
        if (recipeType == RecipeType.SMOKING) {
            return RecipeBookGroup.SMOKER_FOOD;
        }
        if (recipeType == RecipeType.STONECUTTING) {
            return RecipeBookGroup.STONECUTTER;
        }
        if (recipeType == RecipeType.CAMPFIRE_COOKING) {
            return RecipeBookGroup.CAMPFIRE;
        }
        if (recipeType == RecipeType.SMITHING) {
            return RecipeBookGroup.SMITHING;
        }
        Supplier[] supplierArray = new Supplier[2];
        supplierArray[0] = () -> Registry.RECIPE_TYPE.getId(recipe.getType());
        supplierArray[1] = recipe::getId;
        LOGGER.warn("Unknown recipe category: {}/{}", supplierArray);
        return RecipeBookGroup.UNKNOWN;
    }

    public List<RecipeResultCollection> getOrderedResults() {
        return this.orderedResults;
    }

    public List<RecipeResultCollection> getResultsForGroup(RecipeBookGroup category) {
        return this.resultsByGroup.getOrDefault((Object)category, Collections.emptyList());
    }
}

