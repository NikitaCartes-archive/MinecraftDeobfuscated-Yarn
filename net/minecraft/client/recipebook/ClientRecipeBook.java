/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.recipebook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
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
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.CraftingTableScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;

@Environment(value=EnvType.CLIENT)
public class ClientRecipeBook
extends RecipeBook {
    private final RecipeManager manager;
    private final Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup = Maps.newHashMap();
    private final List<RecipeResultCollection> orderedResults = Lists.newArrayList();

    public ClientRecipeBook(RecipeManager manager) {
        this.manager = manager;
    }

    public void reload() {
        this.orderedResults.clear();
        this.resultsByGroup.clear();
        HashBasedTable<RecipeBookGroup, String, RecipeResultCollection> table = HashBasedTable.create();
        for (Recipe<?> recipe : this.manager.values()) {
            RecipeResultCollection recipeResultCollection;
            if (recipe.isIgnoredInRecipeBook()) continue;
            RecipeBookGroup recipeBookGroup = ClientRecipeBook.getGroupForRecipe(recipe);
            String string = recipe.getGroup();
            if (string.isEmpty()) {
                recipeResultCollection = this.addGroup(recipeBookGroup);
            } else {
                recipeResultCollection = (RecipeResultCollection)table.get((Object)recipeBookGroup, string);
                if (recipeResultCollection == null) {
                    recipeResultCollection = this.addGroup(recipeBookGroup);
                    table.put(recipeBookGroup, string, recipeResultCollection);
                }
            }
            recipeResultCollection.addRecipe(recipe);
        }
    }

    private RecipeResultCollection addGroup(RecipeBookGroup group) {
        RecipeResultCollection recipeResultCollection = new RecipeResultCollection();
        this.orderedResults.add(recipeResultCollection);
        this.resultsByGroup.computeIfAbsent(group, recipeBookGroup -> Lists.newArrayList()).add(recipeResultCollection);
        if (group == RecipeBookGroup.FURNACE_BLOCKS || group == RecipeBookGroup.FURNACE_FOOD || group == RecipeBookGroup.FURNACE_MISC) {
            this.addGroupResults(RecipeBookGroup.FURNACE_SEARCH, recipeResultCollection);
        } else if (group == RecipeBookGroup.BLAST_FURNACE_BLOCKS || group == RecipeBookGroup.BLAST_FURNACE_MISC) {
            this.addGroupResults(RecipeBookGroup.BLAST_FURNACE_SEARCH, recipeResultCollection);
        } else if (group == RecipeBookGroup.SMOKER_FOOD) {
            this.addGroupResults(RecipeBookGroup.SMOKER_SEARCH, recipeResultCollection);
        } else if (group == RecipeBookGroup.STONECUTTER) {
            this.addGroupResults(RecipeBookGroup.STONECUTTER, recipeResultCollection);
        } else if (group == RecipeBookGroup.CAMPFIRE) {
            this.addGroupResults(RecipeBookGroup.CAMPFIRE, recipeResultCollection);
        } else {
            this.addGroupResults(RecipeBookGroup.SEARCH, recipeResultCollection);
        }
        return recipeResultCollection;
    }

    private void addGroupResults(RecipeBookGroup group, RecipeResultCollection results) {
        this.resultsByGroup.computeIfAbsent(group, recipeBookGroup -> Lists.newArrayList()).add(results);
    }

    private static RecipeBookGroup getGroupForRecipe(Recipe<?> recipe) {
        RecipeType<?> recipeType = recipe.getType();
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
        ItemStack itemStack = recipe.getOutput();
        ItemGroup itemGroup = itemStack.getItem().getGroup();
        if (itemGroup == ItemGroup.BUILDING_BLOCKS) {
            return RecipeBookGroup.BUILDING_BLOCKS;
        }
        if (itemGroup == ItemGroup.TOOLS || itemGroup == ItemGroup.COMBAT) {
            return RecipeBookGroup.EQUIPMENT;
        }
        if (itemGroup == ItemGroup.REDSTONE) {
            return RecipeBookGroup.REDSTONE;
        }
        return RecipeBookGroup.MISC;
    }

    public static List<RecipeBookGroup> getGroups(CraftingScreenHandler<?> handler) {
        if (handler instanceof CraftingTableScreenHandler || handler instanceof PlayerScreenHandler) {
            return Lists.newArrayList(RecipeBookGroup.SEARCH, RecipeBookGroup.EQUIPMENT, RecipeBookGroup.BUILDING_BLOCKS, RecipeBookGroup.MISC, RecipeBookGroup.REDSTONE);
        }
        if (handler instanceof FurnaceScreenHandler) {
            return Lists.newArrayList(RecipeBookGroup.FURNACE_SEARCH, RecipeBookGroup.FURNACE_FOOD, RecipeBookGroup.FURNACE_BLOCKS, RecipeBookGroup.FURNACE_MISC);
        }
        if (handler instanceof BlastFurnaceScreenHandler) {
            return Lists.newArrayList(RecipeBookGroup.BLAST_FURNACE_SEARCH, RecipeBookGroup.BLAST_FURNACE_BLOCKS, RecipeBookGroup.BLAST_FURNACE_MISC);
        }
        if (handler instanceof SmokerScreenHandler) {
            return Lists.newArrayList(RecipeBookGroup.SMOKER_SEARCH, RecipeBookGroup.SMOKER_FOOD);
        }
        return Lists.newArrayList();
    }

    public List<RecipeResultCollection> getOrderedResults() {
        return this.orderedResults;
    }

    public List<RecipeResultCollection> getResultsForGroup(RecipeBookGroup category) {
        return this.resultsByGroup.getOrDefault((Object)category, Collections.emptyList());
    }
}

