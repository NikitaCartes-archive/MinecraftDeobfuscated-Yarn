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
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

@Environment(value=EnvType.CLIENT)
public class ClientRecipeBook
extends RecipeBook {
    private static final Logger field_25622 = LogManager.getLogger();
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
        } else if (group == RecipeBookGroup.CRAFTING_BUILDING_BLOCKS || group == RecipeBookGroup.CRAFTING_REDSTONE || group == RecipeBookGroup.CRAFTING_EQUIPMENT || group == RecipeBookGroup.CRAFTING_MISC) {
            this.addGroupResults(RecipeBookGroup.SEARCH, recipeResultCollection);
        }
        return recipeResultCollection;
    }

    private void addGroupResults(RecipeBookGroup group, RecipeResultCollection results) {
        this.resultsByGroup.computeIfAbsent(group, recipeBookGroup -> Lists.newArrayList()).add(results);
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
        field_25622.warn("Unknown recipe category: {}/{}", supplierArray);
        return RecipeBookGroup.UNKNOWN;
    }

    public static List<RecipeBookGroup> getGroups(AbstractRecipeScreenHandler<?> handler) {
        if (handler instanceof CraftingScreenHandler || handler instanceof PlayerScreenHandler) {
            return Lists.newArrayList(RecipeBookGroup.SEARCH, RecipeBookGroup.CRAFTING_EQUIPMENT, RecipeBookGroup.CRAFTING_BUILDING_BLOCKS, RecipeBookGroup.CRAFTING_MISC, RecipeBookGroup.CRAFTING_REDSTONE);
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

