package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
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
}
