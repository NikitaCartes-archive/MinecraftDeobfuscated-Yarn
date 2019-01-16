package net.minecraft.inventory;

import net.minecraft.class_3829;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface Inventory extends class_3829 {
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
