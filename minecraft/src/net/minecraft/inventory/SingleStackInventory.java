package net.minecraft.inventory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * An inventory that holds exactly one {@link ItemStack}, at slot {@code 0}.
 */
public interface SingleStackInventory extends Inventory {
	ItemStack getStack();

	default ItemStack decreaseStack(int count) {
		return this.getStack().split(count);
	}

	void setStack(ItemStack stack);

	default ItemStack emptyStack() {
		return this.decreaseStack(this.getMaxCountPerStack());
	}

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
		this.emptyStack();
	}

	@Override
	default ItemStack removeStack(int slot) {
		return this.removeStack(slot, this.getMaxCountPerStack());
	}

	@Override
	default ItemStack getStack(int slot) {
		return slot == 0 ? this.getStack() : ItemStack.EMPTY;
	}

	@Override
	default ItemStack removeStack(int slot, int amount) {
		return slot != 0 ? ItemStack.EMPTY : this.decreaseStack(amount);
	}

	@Override
	default void setStack(int slot, ItemStack stack) {
		if (slot == 0) {
			this.setStack(stack);
		}
	}

	public interface SingleStackBlockEntityInventory extends SingleStackInventory {
		BlockEntity asBlockEntity();

		@Override
		default boolean canPlayerUse(PlayerEntity player) {
			return Inventory.canPlayerUse(this.asBlockEntity(), player);
		}
	}
}
