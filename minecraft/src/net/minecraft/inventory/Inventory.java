package net.minecraft.inventory;

import net.minecraft.class_3829;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;

public interface Inventory extends class_3829, Nameable {
	int getInvSize();

	boolean isInvEmpty();

	ItemStack getInvStack(int i);

	ItemStack takeInvStack(int i, int j);

	ItemStack removeInvStack(int i);

	void setInvStack(int i, ItemStack itemStack);

	int getInvMaxStackAmount();

	void markDirty();

	boolean canPlayerUseInv(PlayerEntity playerEntity);

	void onInvOpen(PlayerEntity playerEntity);

	void onInvClose(PlayerEntity playerEntity);

	boolean isValidInvStack(int i, ItemStack itemStack);

	int getInvProperty(int i);

	void setInvProperty(int i, int j);

	int getInvPropertyCount();

	default int getInvHeight() {
		return 0;
	}

	default int getInvWidth() {
		return 0;
	}
}
