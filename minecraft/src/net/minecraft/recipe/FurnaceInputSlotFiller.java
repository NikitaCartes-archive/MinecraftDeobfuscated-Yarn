package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;

public class FurnaceInputSlotFiller<C extends Inventory> extends InputSlotFiller<C> {
	private boolean slotMatchesRecipe;

	public FurnaceInputSlotFiller(AbstractRecipeScreenHandler<C> abstractRecipeScreenHandler) {
		super(abstractRecipeScreenHandler);
	}

	@Override
	protected void fillInputSlots(Recipe<C> recipe, boolean craftAll) {
		this.slotMatchesRecipe = this.handler.matches(recipe);
		int i = this.matcher.countCrafts(recipe, null);
		if (this.slotMatchesRecipe) {
			ItemStack itemStack = this.handler.getSlot(0).getStack();
			if (itemStack.isEmpty() || i <= itemStack.getCount()) {
				return;
			}
		}

		int j = this.getAmountToFill(craftAll, i, this.slotMatchesRecipe);
		IntList intList = new IntArrayList();
		if (this.matcher.match(recipe, intList, j)) {
			if (!this.slotMatchesRecipe) {
				this.returnSlot(this.handler.getCraftingResultSlotIndex());
				this.returnSlot(0);
			}

			this.fillInputSlot(j, intList);
		}
	}

	@Override
	protected void returnInputs() {
		this.returnSlot(this.handler.getCraftingResultSlotIndex());
		super.returnInputs();
	}

	protected void fillInputSlot(int limit, IntList inputs) {
		Iterator<Integer> iterator = inputs.iterator();
		Slot slot = this.handler.getSlot(0);
		ItemStack itemStack = RecipeMatcher.getStackFromId((Integer)iterator.next());
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
