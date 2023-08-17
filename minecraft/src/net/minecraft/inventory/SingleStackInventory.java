package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

/**
 * An inventory that holds exactly one {@link ItemStack}, at slot {@code 0}.
 */
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

	/**
	 * {@return the stack held by the inventory}
	 */
	default ItemStack getStack() {
		return this.getStack(0);
	}

	/**
	 * Removes the stack held by the inventory.
	 * 
	 * @return the removed stack
	 */
	default ItemStack removeStack() {
		return this.removeStack(0);
	}

	/**
	 * Sets the stack held by the inventory to {@code stack}.
	 */
	default void setStack(ItemStack stack) {
		this.setStack(0, stack);
	}

	@Override
	default ItemStack removeStack(int slot) {
		return this.removeStack(slot, this.getMaxCountPerStack());
	}
}
