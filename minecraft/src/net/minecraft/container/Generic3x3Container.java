package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class Generic3x3Container extends Container {
	private final Inventory inventory;

	public Generic3x3Container(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, new BasicInventory(9));
	}

	public Generic3x3Container(int i, PlayerInventory playerInventory, Inventory inventory) {
		super(ContainerType.GENERIC_3X3, i);
		checkContainerSize(inventory, 9);
		this.inventory = inventory;
		inventory.method_5435(playerInventory.field_7546);

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 3; k++) {
				this.method_7621(new Slot(inventory, k + j * 3, 62 + k * 18, 17 + j * 18));
			}
		}

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new Slot(playerInventory, j, 8 + j * 18, 142));
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
			if (i < 9) {
				if (!this.method_7616(itemStack2, 9, 45, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.method_7616(itemStack2, 0, 9, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.method_7673(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.method_7667(playerEntity, itemStack2);
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.inventory.method_5432(playerEntity);
	}
}
