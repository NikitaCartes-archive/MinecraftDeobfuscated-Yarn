package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DoubleInventory implements Inventory {
	private final Inventory first;
	private final Inventory second;

	public DoubleInventory(Inventory inventory, Inventory inventory2) {
		if (inventory == null) {
			inventory = inventory2;
		}

		if (inventory2 == null) {
			inventory2 = inventory;
		}

		this.first = inventory;
		this.second = inventory2;
	}

	@Override
	public int getInvSize() {
		return this.first.getInvSize() + this.second.getInvSize();
	}

	@Override
	public boolean isInvEmpty() {
		return this.first.isInvEmpty() && this.second.isInvEmpty();
	}

	public boolean isPart(Inventory inventory) {
		return this.first == inventory || this.second == inventory;
	}

	@Override
	public ItemStack getInvStack(int i) {
		return i >= this.first.getInvSize() ? this.second.getInvStack(i - this.first.getInvSize()) : this.first.getInvStack(i);
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		return i >= this.first.getInvSize() ? this.second.takeInvStack(i - this.first.getInvSize(), j) : this.first.takeInvStack(i, j);
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return i >= this.first.getInvSize() ? this.second.removeInvStack(i - this.first.getInvSize()) : this.first.removeInvStack(i);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		if (i >= this.first.getInvSize()) {
			this.second.setInvStack(i - this.first.getInvSize(), itemStack);
		} else {
			this.first.setInvStack(i, itemStack);
		}
	}

	@Override
	public int getInvMaxStackAmount() {
		return this.first.getInvMaxStackAmount();
	}

	@Override
	public void markDirty() {
		this.first.markDirty();
		this.second.markDirty();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.first.canPlayerUseInv(playerEntity) && this.second.canPlayerUseInv(playerEntity);
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
		this.first.onInvOpen(playerEntity);
		this.second.onInvOpen(playerEntity);
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
		this.first.onInvClose(playerEntity);
		this.second.onInvClose(playerEntity);
	}

	@Override
	public boolean isValidInvStack(int i, ItemStack itemStack) {
		return i >= this.first.getInvSize() ? this.second.isValidInvStack(i - this.first.getInvSize(), itemStack) : this.first.isValidInvStack(i, itemStack);
	}

	@Override
	public void clear() {
		this.first.clear();
		this.second.clear();
	}
}
