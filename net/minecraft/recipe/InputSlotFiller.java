/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class InputSlotFiller<C extends Inventory>
implements RecipeGridAligner<Integer> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final RecipeFinder recipeFinder = new RecipeFinder();
    protected PlayerInventory inventory;
    protected AbstractRecipeScreenHandler<C> craftingScreenHandler;

    public InputSlotFiller(AbstractRecipeScreenHandler<C> craftingScreenHandler) {
        this.craftingScreenHandler = craftingScreenHandler;
    }

    public void fillInputSlots(ServerPlayerEntity entity, @Nullable Recipe<C> recipe, boolean craftAll) {
        if (recipe == null || !entity.getRecipeBook().contains(recipe)) {
            return;
        }
        this.inventory = entity.getInventory();
        if (!this.canReturnInputs() && !entity.isCreative()) {
            return;
        }
        this.recipeFinder.clear();
        entity.getInventory().populateRecipeFinder(this.recipeFinder);
        this.craftingScreenHandler.populateRecipeFinder(this.recipeFinder);
        if (this.recipeFinder.findRecipe(recipe, null)) {
            this.fillInputSlots(recipe, craftAll);
        } else {
            this.returnInputs(true);
            entity.networkHandler.sendPacket(new CraftFailedResponseS2CPacket(entity.currentScreenHandler.syncId, recipe));
        }
        entity.getInventory().markDirty();
    }

    protected void returnInputs(boolean bl) {
        for (int i = 0; i < this.craftingScreenHandler.getCraftingSlotCount(); ++i) {
            if (!this.craftingScreenHandler.method_32339(i)) continue;
            ItemStack itemStack = this.craftingScreenHandler.getSlot(i).getStack().copy();
            this.inventory.method_32338(itemStack, false);
            this.craftingScreenHandler.getSlot(i).setStack(itemStack);
        }
        this.craftingScreenHandler.clearCraftingSlots();
    }

    protected void fillInputSlots(Recipe<C> recipe, boolean craftAll) {
        IntArrayList intList;
        int j;
        boolean bl = this.craftingScreenHandler.matches(recipe);
        int i = this.recipeFinder.countRecipeCrafts(recipe, null);
        if (bl) {
            for (j = 0; j < this.craftingScreenHandler.getCraftingHeight() * this.craftingScreenHandler.getCraftingWidth() + 1; ++j) {
                ItemStack itemStack;
                if (j == this.craftingScreenHandler.getCraftingResultSlotIndex() || (itemStack = this.craftingScreenHandler.getSlot(j).getStack()).isEmpty() || Math.min(i, itemStack.getMaxCount()) >= itemStack.getCount() + 1) continue;
                return;
            }
        }
        if (this.recipeFinder.findRecipe(recipe, intList = new IntArrayList(), j = this.getAmountToFill(craftAll, i, bl))) {
            int k = j;
            IntListIterator intListIterator = intList.iterator();
            while (intListIterator.hasNext()) {
                int l = (Integer)intListIterator.next();
                int m = RecipeFinder.getStackFromId(l).getMaxCount();
                if (m >= k) continue;
                k = m;
            }
            j = k;
            if (this.recipeFinder.findRecipe(recipe, intList, j)) {
                this.returnInputs(false);
                this.alignRecipeToGrid(this.craftingScreenHandler.getCraftingWidth(), this.craftingScreenHandler.getCraftingHeight(), this.craftingScreenHandler.getCraftingResultSlotIndex(), recipe, intList.iterator(), j);
            }
        }
    }

    @Override
    public void acceptAlignedInput(Iterator<Integer> inputs, int slot, int amount, int gridX, int gridY) {
        Slot slot2 = this.craftingScreenHandler.getSlot(slot);
        ItemStack itemStack = RecipeFinder.getStackFromId(inputs.next());
        if (!itemStack.isEmpty()) {
            for (int i = 0; i < amount; ++i) {
                this.fillInputSlot(slot2, itemStack);
            }
        }
    }

    protected int getAmountToFill(boolean craftAll, int limit, boolean recipeInCraftingSlots) {
        int i = 1;
        if (craftAll) {
            i = limit;
        } else if (recipeInCraftingSlots) {
            i = 64;
            for (int j = 0; j < this.craftingScreenHandler.getCraftingWidth() * this.craftingScreenHandler.getCraftingHeight() + 1; ++j) {
                ItemStack itemStack;
                if (j == this.craftingScreenHandler.getCraftingResultSlotIndex() || (itemStack = this.craftingScreenHandler.getSlot(j).getStack()).isEmpty() || i <= itemStack.getCount()) continue;
                i = itemStack.getCount();
            }
            if (i < 64) {
                ++i;
            }
        }
        return i;
    }

    protected void fillInputSlot(Slot slot, ItemStack stack) {
        int i = this.inventory.indexOf(stack);
        if (i == -1) {
            return;
        }
        ItemStack itemStack = this.inventory.getStack(i).copy();
        if (itemStack.isEmpty()) {
            return;
        }
        if (itemStack.getCount() > 1) {
            this.inventory.removeStack(i, 1);
        } else {
            this.inventory.removeStack(i);
        }
        itemStack.setCount(1);
        if (slot.getStack().isEmpty()) {
            slot.setStack(itemStack);
        } else {
            slot.getStack().increment(1);
        }
    }

    private boolean canReturnInputs() {
        ArrayList<ItemStack> list = Lists.newArrayList();
        int i = this.getFreeInventorySlots();
        for (int j = 0; j < this.craftingScreenHandler.getCraftingWidth() * this.craftingScreenHandler.getCraftingHeight() + 1; ++j) {
            ItemStack itemStack;
            if (j == this.craftingScreenHandler.getCraftingResultSlotIndex() || (itemStack = this.craftingScreenHandler.getSlot(j).getStack().copy()).isEmpty()) continue;
            int k = this.inventory.getOccupiedSlotWithRoomForStack(itemStack);
            if (k == -1 && list.size() <= i) {
                for (ItemStack itemStack2 : list) {
                    if (!itemStack2.isItemEqualIgnoreDamage(itemStack) || itemStack2.getCount() == itemStack2.getMaxCount() || itemStack2.getCount() + itemStack.getCount() > itemStack2.getMaxCount()) continue;
                    itemStack2.increment(itemStack.getCount());
                    itemStack.setCount(0);
                    break;
                }
                if (itemStack.isEmpty()) continue;
                if (list.size() < i) {
                    list.add(itemStack);
                    continue;
                }
                return false;
            }
            if (k != -1) continue;
            return false;
        }
        return true;
    }

    private int getFreeInventorySlots() {
        int i = 0;
        for (ItemStack itemStack : this.inventory.main) {
            if (!itemStack.isEmpty()) continue;
            ++i;
        }
        return i;
    }
}

