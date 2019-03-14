package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;

public interface Inventory extends Clearable {
	int getInvSize();

	boolean isInvEmpty();

	ItemStack getInvStack(int i);

	ItemStack takeInvStack(int i, int j);

	ItemStack removeInvStack(int i);

	void setInvStack(int i, ItemStack itemStack);

	default int getInvMaxStackAmount() {
		return 64;
	}

	void markDirty();

	boolean canPlayerUseInv(PlayerEntity playerEntity);

	default void onInvOpen(PlayerEntity playerEntity) {
	}

	default void onInvClose(PlayerEntity playerEntity) {
	}

	default boolean isValidInvStack(int i, ItemStack itemStack) {
		return true;
	}

	default int getInvAmountOf(Item item) {
		int i = 0;

		for (int j = 0; j < this.getInvSize(); j++) {
			ItemStack itemStack = this.getInvStack(j);
			if (itemStack.getItem().equals(item)) {
				i += itemStack.getAmount();
			}
		}

		return i;
	}

	default boolean method_18862(Set<Item> set) {
		for (int i = 0; i < this.getInvSize(); i++) {
			ItemStack itemStack = this.getInvStack(i);
			if (set.contains(itemStack.getItem()) && itemStack.getAmount() > 0) {
				return true;
			}
		}

		return false;
	}
}
