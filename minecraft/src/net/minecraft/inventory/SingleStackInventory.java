package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

public interface SingleStackInventory extends Inventory {
	@Override
	default int size() {
		return 1;
	}

	@Override
	default boolean isEmpty() {
		return this.getStack().isEmpty();
	}

	@Override
	default void clear() {
		this.removeStack();
	}

	default ItemStack getStack() {
		return this.getStack(0);
	}

	default ItemStack removeStack() {
		return this.removeStack(0);
	}

	default void setStack(ItemStack stack) {
		this.setStack(0, stack);
	}

	@Override
	default ItemStack removeStack(int slot) {
		return this.removeStack(slot, this.getMaxCountPerStack());
	}
}
