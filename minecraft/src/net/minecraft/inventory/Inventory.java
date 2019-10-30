package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;

public interface Inventory extends Clearable {
	int getInvSize();

	boolean isInvEmpty();

	ItemStack getInvStack(int slot);

	ItemStack takeInvStack(int slot, int amount);

	ItemStack removeInvStack(int slot);

	void setInvStack(int slot, ItemStack stack);

	default int getInvMaxStackAmount() {
		return 64;
	}

	void markDirty();

	boolean canPlayerUseInv(PlayerEntity player);

	default void onInvOpen(PlayerEntity player) {
	}

	default void onInvClose(PlayerEntity player) {
	}

	default boolean isValidInvStack(int slot, ItemStack stack) {
		return true;
	}

	default int countInInv(Item item) {
		int i = 0;

		for (int j = 0; j < this.getInvSize(); j++) {
			ItemStack itemStack = this.getInvStack(j);
			if (itemStack.getItem().equals(item)) {
				i += itemStack.getCount();
			}
		}

		return i;
	}

	default boolean containsAnyInInv(Set<Item> items) {
		for (int i = 0; i < this.getInvSize(); i++) {
			ItemStack itemStack = this.getInvStack(i);
			if (items.contains(itemStack.getItem()) && itemStack.getCount() > 0) {
				return true;
			}
		}

		return false;
	}
}
