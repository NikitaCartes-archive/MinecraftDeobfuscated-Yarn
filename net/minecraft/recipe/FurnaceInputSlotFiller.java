/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;

public class FurnaceInputSlotFiller<C extends Inventory>
extends InputSlotFiller<C> {
    private boolean slotMatchesRecipe;

    public FurnaceInputSlotFiller(AbstractRecipeScreenHandler<C> abstractRecipeScreenHandler) {
        super(abstractRecipeScreenHandler);
    }

    @Override
    protected void fillInputSlots(Recipe<C> recipe, boolean craftAll) {
        ItemStack itemStack;
        this.slotMatchesRecipe = this.handler.matches(recipe);
        int i = this.matcher.countCrafts(recipe, null);
        if (this.slotMatchesRecipe && ((itemStack = this.handler.getSlot(0).getStack()).isEmpty() || i <= itemStack.getCount())) {
            return;
        }
        IntArrayList intList = new IntArrayList();
        int j = this.getAmountToFill(craftAll, i, this.slotMatchesRecipe);
        if (!this.matcher.match(recipe, intList, j)) {
            return;
        }
        if (!this.slotMatchesRecipe) {
            this.returnSlot(this.handler.getCraftingResultSlotIndex());
            this.returnSlot(0);
        }
        this.fillInputSlot(j, intList);
    }

    @Override
    protected void returnInputs() {
        this.returnSlot(this.handler.getCraftingResultSlotIndex());
        super.returnInputs();
    }

    protected void fillInputSlot(int limit, IntList inputs) {
        IntListIterator iterator = inputs.iterator();
        Slot slot = this.handler.getSlot(0);
        ItemStack itemStack = RecipeMatcher.getStackFromId((Integer)iterator.next());
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

