package net.minecraft;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

public class class_2958 extends class_2955 {
	private boolean field_13351;

	@Override
	protected void method_12821(Recipe recipe, boolean bl) {
		this.field_13351 = this.field_13348.matches(recipe);
		int i = this.field_13347.method_7407(recipe, null);
		if (this.field_13351) {
			ItemStack itemStack = this.field_13348.getSlot(0).getStack();
			if (itemStack.isEmpty() || i <= itemStack.getAmount()) {
				return;
			}
		}

		int j = this.method_12819(bl, i, this.field_13351);
		IntList intList = new IntArrayList();
		if (this.field_13347.method_7406(recipe, intList, j)) {
			if (!this.field_13351) {
				this.method_12820(this.field_13348.getCraftingResultSlotIndex());
				this.method_12820(0);
			}

			this.method_12827(j, intList);
		}
	}

	@Override
	protected void method_12822() {
		this.method_12820(this.field_13348.getCraftingResultSlotIndex());
		super.method_12822();
	}

	protected void method_12827(int i, IntList intList) {
		Iterator<Integer> iterator = intList.iterator();
		Slot slot = this.field_13348.getSlot(0);
		ItemStack itemStack = class_1662.method_7405((Integer)iterator.next());
		if (!itemStack.isEmpty()) {
			int j = Math.min(itemStack.getMaxAmount(), i);
			if (this.field_13351) {
				j -= slot.getStack().getAmount();
			}

			for (int k = 0; k < j; k++) {
				this.method_12824(slot, itemStack);
			}
		}
	}
}
