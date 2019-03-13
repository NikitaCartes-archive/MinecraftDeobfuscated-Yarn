package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class HopperContainer extends Container {
	private final Inventory inventory;

	public HopperContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, new BasicInventory(5));
	}

	public HopperContainer(int i, PlayerInventory playerInventory, Inventory inventory) {
		super(ContainerType.HOPPER, i);
		this.inventory = inventory;
		checkContainerSize(inventory, 5);
		inventory.method_5435(playerInventory.field_7546);
		int j = 51;

		for (int k = 0; k < 5; k++) {
			this.method_7621(new Slot(inventory, k, 44 + k * 18, 20));
		}

		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 9; l++) {
				this.method_7621(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, k * 18 + 51));
			}
		}

		for (int k = 0; k < 9; k++) {
			this.method_7621(new Slot(playerInventory, k, 8 + k * 18, 109));
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.inventory.method_5443(playerEntity);
	}

	@Override
	public ItemStack method_7601(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.method_7677();
			itemStack = itemStack2.copy();
			if (i < this.inventory.getInvSize()) {
				if (!this.method_7616(itemStack2, this.inventory.getInvSize(), this.slotList.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.method_7616(itemStack2, 0, this.inventory.getInvSize(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.method_7673(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.inventory.method_5432(playerEntity);
	}
}
