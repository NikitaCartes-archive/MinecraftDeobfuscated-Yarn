package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class FurnaceInputSlotFiller<C extends Inventory> extends InputSlotFiller<C> {
	private boolean slotMatchesRecipe;

	public FurnaceInputSlotFiller(CraftingContainer<C> craftingContainer) {
		super(craftingContainer);
	}

	@Override
	protected void fillInputSlots(Recipe<C> recipe, boolean craftAll) {
		this.slotMatchesRecipe = this.craftingContainer.matches(recipe);
		int i = this.recipeFinder.countRecipeCrafts(recipe, null);
		if (this.slotMatchesRecipe) {
			ItemStack itemStack = this.craftingContainer.getSlot(0).getStack();
			if (itemStack.isEmpty() || i <= itemStack.getCount()) {
				return;
			}
		}

		int j = this.getAmountToFill(craftAll, i, this.slotMatchesRecipe);
		IntList intList = new IntArrayList();
		if (this.recipeFinder.findRecipe(recipe, intList, j)) {
			if (!this.slotMatchesRecipe) {
				this.returnSlot(this.craftingContainer.getCraftingResultSlotIndex());
				this.returnSlot(0);
			}

			this.fillInputSlot(j, intList);
		}
	}

	@Override
	protected void returnInputs() {
		this.returnSlot(this.craftingContainer.getCraftingResultSlotIndex());
		super.returnInputs();
	}

	protected void fillInputSlot(int limit, IntList inputs) {
		Iterator<Integer> iterator = inputs.iterator();
		Slot slot = this.craftingContainer.getSlot(0);
		ItemStack itemStack = RecipeFinder.getStackFromId((Integer)iterator.next());
		if (!itemStack.isEmpty()) {
			int i = Math.min(itemStack.getMaxCount(), limit);
			if (this.slotMatchesRecipe) {
				i -= slot.getStack().getCount();
			}

			for (int j = 0; j < i; j++) {
				this.fillInputSlot(slot, itemStack);
			}
		}
	}
}
