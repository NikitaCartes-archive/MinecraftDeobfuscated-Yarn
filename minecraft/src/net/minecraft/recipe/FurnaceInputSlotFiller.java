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
	protected void fillInputSlots(Recipe<C> recipe, boolean bl) {
		this.slotMatchesRecipe = this.craftingContainer.matches(recipe);
		int i = this.recipeFinder.countRecipeCrafts(recipe, null);
		if (this.slotMatchesRecipe) {
			ItemStack itemStack = this.craftingContainer.getSlot(0).getStack();
			if (itemStack.isEmpty() || i <= itemStack.getCount()) {
				return;
			}
		}

		int j = this.getAmountToFill(bl, i, this.slotMatchesRecipe);
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

	protected void fillInputSlot(int i, IntList intList) {
		Iterator<Integer> iterator = intList.iterator();
		Slot slot = this.craftingContainer.getSlot(0);
		ItemStack itemStack = RecipeFinder.getStackFromId((Integer)iterator.next());
		if (!itemStack.isEmpty()) {
			int j = Math.min(itemStack.getMaxCount(), i);
			if (this.slotMatchesRecipe) {
				j -= slot.getStack().getCount();
			}

			for (int k = 0; k < j; k++) {
				this.fillInputSlot(slot, itemStack);
			}
		}
	}
}
