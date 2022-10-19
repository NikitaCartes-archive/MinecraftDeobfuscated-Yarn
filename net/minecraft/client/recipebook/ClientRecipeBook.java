/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.recipebook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientRecipeBook
extends RecipeBook {
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup = ImmutableMap.of();
    private List<RecipeResultCollection> orderedResults = ImmutableList.of();

    public void reload(Iterable<Recipe<?>> recipes2) {
        Map<RecipeBookGroup, List<List<Recipe<?>>>> map = ClientRecipeBook.toGroupedMap(recipes2);
        HashMap map2 = Maps.newHashMap();
        ImmutableList.Builder builder = ImmutableList.builder();
        map.forEach((group, recipes) -> map2.put(group, (List)recipes.stream().map(RecipeResultCollection::new).peek(builder::add).collect(ImmutableList.toImmutableList())));
        RecipeBookGroup.SEARCH_MAP.forEach((group, searchGroups) -> map2.put(group, (List)searchGroups.stream().flatMap(searchGroup -> ((List)map2.getOrDefault(searchGroup, ImmutableList.of())).stream()).collect(ImmutableList.toImmutableList())));
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
        if (recipe instanceof CraftingRecipe) {
            CraftingRecipe craftingRecipe = (CraftingRecipe)recipe;
            return switch (craftingRecipe.getCategory()) {
                default -> throw new IncompatibleClassChangeError();
                case CraftingRecipeCategory.BUILDING -> RecipeBookGroup.CRAFTING_BUILDING_BLOCKS;
                case CraftingRecipeCategory.EQUIPMENT -> RecipeBookGroup.CRAFTING_EQUIPMENT;
                case CraftingRecipeCategory.REDSTONE -> RecipeBookGroup.CRAFTING_REDSTONE;
                case CraftingRecipeCategory.MISC -> RecipeBookGroup.CRAFTING_MISC;
            };
        }
        RecipeType<?> recipeType = recipe.getType();
        if (recipe instanceof AbstractCookingRecipe) {
            AbstractCookingRecipe abstractCookingRecipe = (AbstractCookingRecipe)recipe;
            CookingRecipeCategory cookingRecipeCategory = abstractCookingRecipe.getCategory();
            if (recipeType == RecipeType.SMELTING) {
                return switch (cookingRecipeCategory) {
                    default -> throw new IncompatibleClassChangeError();
                    case CookingRecipeCategory.BLOCKS -> RecipeBookGroup.FURNACE_BLOCKS;
                    case CookingRecipeCategory.FOOD -> RecipeBookGroup.FURNACE_FOOD;
                    case CookingRecipeCategory.MISC -> RecipeBookGroup.FURNACE_MISC;
                };
            }
            if (recipeType == RecipeType.BLASTING) {
                return cookingRecipeCategory == CookingRecipeCategory.BLOCKS ? RecipeBookGroup.BLAST_FURNACE_BLOCKS : RecipeBookGroup.BLAST_FURNACE_MISC;
            }
            if (recipeType == RecipeType.SMOKING) {
                return RecipeBookGroup.SMOKER_FOOD;
            }
            if (recipeType == RecipeType.CAMPFIRE_COOKING) {
                return RecipeBookGroup.CAMPFIRE;
            }
        }
        if (recipeType == RecipeType.STONECUTTING) {
            return RecipeBookGroup.STONECUTTER;
        }
        if (recipeType == RecipeType.SMITHING) {
            return RecipeBookGroup.SMITHING;
        }
        LOGGER.warn("Unknown recipe category: {}/{}", LogUtils.defer(() -> Registry.RECIPE_TYPE.getId(recipe.getType())), LogUtils.defer(recipe::getId));
        return RecipeBookGroup.UNKNOWN;
    }

    public List<RecipeResultCollection> getOrderedResults() {
        return this.orderedResults;
    }

    public List<RecipeResultCollection> getResultsForGroup(RecipeBookGroup category) {
        return this.resultsByGroup.getOrDefault((Object)category, Collections.emptyList());
    }
}

