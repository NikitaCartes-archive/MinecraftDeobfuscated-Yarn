package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DoubleInventory implements Inventory {
	private final Inventory field_5769;
	private final Inventory field_5771;

	public DoubleInventory(Inventory inventory, Inventory inventory2) {
		if (inventory == null) {
			inventory = inventory2;
		}

		if (inventory2 == null) {
			inventory2 = inventory;
		}

		this.field_5769 = inventory;
		this.field_5771 = inventory2;
	}

	@Override
	public int getInvSize() {
		return this.field_5769.getInvSize() + this.field_5771.getInvSize();
	}

	@Override
	public boolean isInvEmpty() {
		return this.field_5769.isInvEmpty() && this.field_5771.isInvEmpty();
	}

	public boolean method_5405(Inventory inventory) {
		return this.field_5769 == inventory || this.field_5771 == inventory;
	}

	@Override
	public ItemStack method_5438(int i) {
		return i >= this.field_5769.getInvSize() ? this.field_5771.method_5438(i - this.field_5769.getInvSize()) : this.field_5769.method_5438(i);
	}

	@Override
	public ItemStack method_5434(int i, int j) {
		return i >= this.field_5769.getInvSize() ? this.field_5771.method_5434(i - this.field_5769.getInvSize(), j) : this.field_5769.method_5434(i, j);
	}

	@Override
	public ItemStack method_5441(int i) {
		return i >= this.field_5769.getInvSize() ? this.field_5771.method_5441(i - this.field_5769.getInvSize()) : this.field_5769.method_5441(i);
	}

	@Override
	public void method_5447(int i, ItemStack itemStack) {
		if (i >= this.field_5769.getInvSize()) {
			this.field_5771.method_5447(i - this.field_5769.getInvSize(), itemStack);
		} else {
			this.field_5769.method_5447(i, itemStack);
		}
	}

	@Override
	public int getInvMaxStackAmount() {
		return this.field_5769.getInvMaxStackAmount();
	}

	@Override
	public void markDirty() {
		this.field_5769.markDirty();
		this.field_5771.markDirty();
	}

	@Override
	public boolean method_5443(PlayerEntity playerEntity) {
		return this.field_5769.method_5443(playerEntity) && this.field_5771.method_5443(playerEntity);
	}

	@Override
	public void method_5435(PlayerEntity playerEntity) {
		this.field_5769.method_5435(playerEntity);
		this.field_5771.method_5435(playerEntity);
	}

	@Override
	public void method_5432(PlayerEntity playerEntity) {
		this.field_5769.method_5432(playerEntity);
		this.field_5771.method_5432(playerEntity);
	}

	@Override
	public boolean method_5437(int i, ItemStack itemStack) {
		return i >= this.field_5769.getInvSize()
			? this.field_5771.method_5437(i - this.field_5769.getInvSize(), itemStack)
			: this.field_5769.method_5437(i, itemStack);
	}

	@Override
	public void clear() {
		this.field_5769.clear();
		this.field_5771.clear();
	}
}
