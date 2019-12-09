/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;

public class FurnaceInputSlotFiller<C extends Inventory>
extends InputSlotFiller<C> {
    private boolean slotMatchesRecipe;

    public FurnaceInputSlotFiller(CraftingContainer<C> craftingContainer) {
        super(craftingContainer);
    }

    @Override
    protected void fillInputSlots(Recipe<C> recipe, boolean craftAll) {
        ItemStack itemStack;
        this.slotMatchesRecipe = this.craftingContainer.matches(recipe);
        int i = this.recipeFinder.countRecipeCrafts(recipe, null);
        if (this.slotMatchesRecipe && ((itemStack = this.craftingContainer.getSlot(0).getStack()).isEmpty() || i <= itemStack.getCount())) {
            return;
        }
        IntArrayList intList = new IntArrayList();
        int j = this.getAmountToFill(craftAll, i, this.slotMatchesRecipe);
        if (!this.recipeFinder.findRecipe(recipe, intList, j)) {
            return;
        }
        if (!this.slotMatchesRecipe) {
            this.returnSlot(this.craftingContainer.getCraftingResultSlotIndex());
            this.returnSlot(0);
        }
        this.fillInputSlot(j, intList);
    }

    @Override
    protected void returnInputs() {
        this.returnSlot(this.craftingContainer.getCraftingResultSlotIndex());
        super.returnInputs();
    }

    protected void fillInputSlot(int limit, IntList inputs) {
        IntListIterator iterator = inputs.iterator();
        Slot slot = this.craftingContainer.getSlot(0);
        ItemStack itemStack = RecipeFinder.getStackFromId((Integer)iterator.next());
        if (itemStack.isEmpty()) {
            return;
        }
        int i = Math.min(itemStack.getMaxCount(), limit);
        if (this.slotMatchesRecipe) {
            i -= slot.getStack().getCount();
        }
        for (int j = 0; j < i; ++j) {
            this.fillInputSlot(slot, itemStack);
        }
    }
}

