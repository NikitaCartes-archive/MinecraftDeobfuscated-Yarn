/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

/**
 * Matching class that matches a recipe to its required resources.
 * This specifically does not check patterns (See {@link ShapedRecipe} for that).
 */
public class RecipeMatcher {
    private static final int field_30653 = 0;
    public final Int2IntMap inputs = new Int2IntOpenHashMap();

    /**
     * Adds a full item stack to the pool of available resources.
     * 
     * <p>This is equivalent to calling {@code addInput(stack, Item.DEFAULT_MAX_COUNT)}.</p>
     */
    public void addUnenchantedInput(ItemStack stack) {
        if (!(stack.isDamaged() || stack.hasEnchantments() || stack.hasCustomName())) {
            this.addInput(stack);
        }
    }

    /**
     * Adds a full item stack to the pool of available resources.
     * 
     * <p>This is equivalent to calling {@code addInput(stack, Item.DEFAULT_MAX_COUNT)}.</p>
     */
    public void addInput(ItemStack stack) {
        this.addInput(stack, 64);
    }

    /**
     * Adds an item stack to the pool of available resources.
     */
    public void addInput(ItemStack stack, int maxCount) {
        if (!stack.isEmpty()) {
            int i = RecipeMatcher.getItemId(stack);
            int j = Math.min(maxCount, stack.getCount());
            this.addInput(i, j);
        }
    }

    public static int getItemId(ItemStack stack) {
        return Registries.ITEM.getRawId(stack.getItem());
    }

    /**
     * Determines whether a raw item id is present in the pool of crafting resources.
     */
    boolean contains(int itemId) {
        return this.inputs.get(itemId) > 0;
    }

    /**
     * Consumes a resource from the pool of available items.
     * 
     * @param itemId the raw id of the item being consumed
     * @param count the number of times that item must be consumed
     */
    int consume(int itemId, int count) {
        int i = this.inputs.get(itemId);
        if (i >= count) {
            this.inputs.put(itemId, i - count);
            return itemId;
        }
        return 0;
    }

    /**
     * Adds an input to be used for recipe matching.
     * 
     * @param itemId the raw ID of the item to match
     * @param count the item's count
     */
    void addInput(int itemId, int count) {
        this.inputs.put(itemId, this.inputs.get(itemId) + count);
    }

    /**
     * Attempts to match the recipe against the collected inputs.
     * Assumes only one output is required.
     * 
     * @param recipe the recipe to match against
     * @param output optional output list of item ids that were matched whilst evaluating the recipe conditions
     */
    public boolean match(Recipe<?> recipe, @Nullable IntList output) {
        return this.match(recipe, output, 1);
    }

    /**
     * Attempts to match the recipe against the collected inputs. Will only succeed if there has been enough
     * resources gathered to produce the requested number of outputs.
     * 
     * @param multiplier the number of expected outputs
     * @param output optional output list of item ids that were matched whilst evaluating the recipe conditions
     * @param recipe the recipe to match against
     */
    public boolean match(Recipe<?> recipe, @Nullable IntList output, int multiplier) {
        return new Matcher(recipe).match(multiplier, output);
    }

    /**
     * Determines the number of crafts that can be produced for a recipe using the
     * collected resources available to this crafter.
     * 
     * @param recipe the recipe to match against
     * @param output optional output list of item ids that were matched whilst evaluating the recipe conditions
     */
    public int countCrafts(Recipe<?> recipe, @Nullable IntList output) {
        return this.countCrafts(recipe, Integer.MAX_VALUE, output);
    }

    /**
     * Determines the number of crafts that can be produced for a recipe using the
     * collected resources available to this crafter.
     * 
     * @param recipe the recipe to match against
     * @param output optional output list of item ids that were matched whilst evaluating the recipe conditions
     */
    public int countCrafts(Recipe<?> recipe, int limit, @Nullable IntList output) {
        return new Matcher(recipe).countCrafts(limit, output);
    }

    public static ItemStack getStackFromId(int itemId) {
        if (itemId == 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(Item.byRawId(itemId));
    }

    public void clear() {
        this.inputs.clear();
    }

    class Matcher {
        private final Recipe<?> recipe;
        private final List<Ingredient> ingredients = Lists.newArrayList();
        private final int totalIngredients;
        private final int[] requiredItems;
        private final int totalRequiredItems;
        private final BitSet requirementsMatrix;
        private final IntList ingredientItemLookup = new IntArrayList();

        public Matcher(Recipe<?> recipe) {
            this.recipe = recipe;
            this.ingredients.addAll(recipe.getIngredients());
            this.ingredients.removeIf(Ingredient::isEmpty);
            this.totalIngredients = this.ingredients.size();
            this.requiredItems = this.createItemRequirementList();
            this.totalRequiredItems = this.requiredItems.length;
            this.requirementsMatrix = new BitSet(this.totalIngredients + this.totalRequiredItems + this.totalIngredients + this.totalIngredients * this.totalRequiredItems);
            for (int i = 0; i < this.ingredients.size(); ++i) {
                IntList intList = this.ingredients.get(i).getMatchingItemIds();
                for (int j = 0; j < this.totalRequiredItems; ++j) {
                    if (!intList.contains(this.requiredItems[j])) continue;
                    this.requirementsMatrix.set(this.getRequirementIndex(true, j, i));
                }
            }
        }

        public boolean match(int multiplier, @Nullable IntList output) {
            boolean bl2;
            if (multiplier <= 0) {
                return true;
            }
            int i = 0;
            while (this.checkRequirements(multiplier)) {
                RecipeMatcher.this.consume(this.requiredItems[this.ingredientItemLookup.getInt(0)], multiplier);
                int j = this.ingredientItemLookup.size() - 1;
                this.unfulfillRequirement(this.ingredientItemLookup.getInt(j));
                for (int k = 0; k < j; ++k) {
                    this.flipRequirement((k & 1) == 0, this.ingredientItemLookup.get(k), this.ingredientItemLookup.get(k + 1));
                }
                this.ingredientItemLookup.clear();
                this.requirementsMatrix.clear(0, this.totalIngredients + this.totalRequiredItems);
                ++i;
            }
            boolean bl = i == this.totalIngredients;
            boolean bl3 = bl2 = bl && output != null;
            if (bl2) {
                output.clear();
            }
            this.requirementsMatrix.clear(0, this.totalIngredients + this.totalRequiredItems + this.totalIngredients);
            int l = 0;
            DefaultedList<Ingredient> list = this.recipe.getIngredients();
            for (int m = 0; m < list.size(); ++m) {
                if (bl2 && ((Ingredient)list.get(m)).isEmpty()) {
                    output.add(0);
                    continue;
                }
                for (int n = 0; n < this.totalRequiredItems; ++n) {
                    if (!this.checkRequirement(false, l, n)) continue;
                    this.flipRequirement(true, n, l);
                    RecipeMatcher.this.addInput(this.requiredItems[n], multiplier);
                    if (!bl2) continue;
                    output.add(this.requiredItems[n]);
                }
                ++l;
            }
            return bl;
        }

        private int[] createItemRequirementList() {
            IntAVLTreeSet intCollection = new IntAVLTreeSet();
            for (Ingredient ingredient : this.ingredients) {
                intCollection.addAll(ingredient.getMatchingItemIds());
            }
            IntIterator intIterator = intCollection.iterator();
            while (intIterator.hasNext()) {
                if (RecipeMatcher.this.contains(intIterator.nextInt())) continue;
                intIterator.remove();
            }
            return intCollection.toIntArray();
        }

        private boolean checkRequirements(int multiplier) {
            int i = this.totalRequiredItems;
            for (int j = 0; j < i; ++j) {
                if (RecipeMatcher.this.inputs.get(this.requiredItems[j]) < multiplier) continue;
                this.addRequirement(false, j);
                while (!this.ingredientItemLookup.isEmpty()) {
                    int n;
                    int k = this.ingredientItemLookup.size();
                    boolean bl = (k & 1) == 1;
                    int l = this.ingredientItemLookup.getInt(k - 1);
                    if (!bl && !this.getRequirement(l)) break;
                    int m = bl ? this.totalIngredients : i;
                    for (n = 0; n < m; ++n) {
                        if (this.isRequirementUnfulfilled(bl, n) || !this.needsRequirement(bl, l, n) || !this.checkRequirement(bl, l, n)) continue;
                        this.addRequirement(bl, n);
                        break;
                    }
                    if ((n = this.ingredientItemLookup.size()) != k) continue;
                    this.ingredientItemLookup.removeInt(n - 1);
                }
                if (this.ingredientItemLookup.isEmpty()) continue;
                return true;
            }
            return false;
        }

        private boolean getRequirement(int itemId) {
            return this.requirementsMatrix.get(this.getRequirementIndex(itemId));
        }

        private void unfulfillRequirement(int itemId) {
            this.requirementsMatrix.set(this.getRequirementIndex(itemId));
        }

        private int getRequirementIndex(int itemId) {
            return this.totalIngredients + this.totalRequiredItems + itemId;
        }

        private boolean needsRequirement(boolean reversed, int itemIndex, int ingredientIndex) {
            return this.requirementsMatrix.get(this.getRequirementIndex(reversed, itemIndex, ingredientIndex));
        }

        private boolean checkRequirement(boolean reversed, int itemIndex, int ingredientIndex) {
            return reversed != this.requirementsMatrix.get(1 + this.getRequirementIndex(reversed, itemIndex, ingredientIndex));
        }

        private void flipRequirement(boolean reversed, int itemIndex, int ingredientIndex) {
            this.requirementsMatrix.flip(1 + this.getRequirementIndex(reversed, itemIndex, ingredientIndex));
        }

        private int getRequirementIndex(boolean reversed, int itemIndex, int ingredientIndex) {
            int i = reversed ? itemIndex * this.totalIngredients + ingredientIndex : ingredientIndex * this.totalIngredients + itemIndex;
            return this.totalIngredients + this.totalRequiredItems + this.totalIngredients + 2 * i;
        }

        private void addRequirement(boolean reversed, int itemId) {
            this.requirementsMatrix.set(this.getRequirementIndex(reversed, itemId));
            this.ingredientItemLookup.add(itemId);
        }

        private boolean isRequirementUnfulfilled(boolean reversed, int itemId) {
            return this.requirementsMatrix.get(this.getRequirementIndex(reversed, itemId));
        }

        private int getRequirementIndex(boolean reversed, int itemId) {
            return (reversed ? 0 : this.totalIngredients) + itemId;
        }

        public int countCrafts(int minimum, @Nullable IntList output) {
            int k;
            int i = 0;
            int j = Math.min(minimum, this.getMaximumCrafts()) + 1;
            while (true) {
                if (this.match(k = (i + j) / 2, null)) {
                    if (j - i <= 1) break;
                    i = k;
                    continue;
                }
                j = k;
            }
            if (k > 0) {
                this.match(k, output);
            }
            return k;
        }

        private int getMaximumCrafts() {
            int i = Integer.MAX_VALUE;
            for (Ingredient ingredient : this.ingredients) {
                int j = 0;
                IntListIterator intListIterator = ingredient.getMatchingItemIds().iterator();
                while (intListIterator.hasNext()) {
                    int k = (Integer)intListIterator.next();
                    j = Math.max(j, RecipeMatcher.this.inputs.get(k));
                }
                if (i <= 0) continue;
                i = Math.min(i, j);
            }
            return i;
        }
    }
}

