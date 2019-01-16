package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public abstract class GenericContainer extends Container {
	private final Inventory field_17495;
	private final int field_17496;

	public GenericContainer(ContainerType<?> containerType, int i, PlayerInventory playerInventory, int j) {
		this(containerType, i, playerInventory, new BasicInventory(9 * j), j);
	}

	public GenericContainer(ContainerType<?> containerType, int i, PlayerInventory playerInventory, Inventory inventory, int j) {
		super(containerType, i);
		checkContainerSize(inventory, j * 9);
		this.field_17495 = inventory;
		this.field_17496 = j;
		inventory.onInvOpen(playerInventory.player);
		int k = (this.field_17496 - 4) * 18;

		for (int l = 0; l < this.field_17496; l++) {
			for (int m = 0; m < 9; m++) {
				this.addSlot(new Slot(inventory, m + l * 9, 8 + m * 18, 18 + l * 18));
			}
		}

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.addSlot(new Slot(playerInventory, m + l * 9 + 9, 8 + m * 18, 103 + l * 18 + k));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 161 + k));
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.field_17495.canPlayerUseInv(playerEntity);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i < this.field_17496 * 9) {
				if (!this.insertItem(itemStack2, this.field_17496 * 9, this.slotList.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 0, this.field_17496 * 9, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.field_17495.onInvClose(playerEntity);
	}

	public Inventory getInventory() {
		return this.field_17495;
	}

	@Environment(EnvType.CLIENT)
	public int method_17388() {
		return this.field_17496;
	}

	public static class Generic9x3 extends GenericContainer {
		public Generic9x3(int i, PlayerInventory playerInventory) {
			super(ContainerType.GENERIC_9X3, i, playerInventory, 3);
		}

		public Generic9x3(int i, PlayerInventory playerInventory, Inventory inventory) {
			super(ContainerType.GENERIC_9X3, i, playerInventory, inventory, 3);
		}
	}

	public static class Generic9x6 extends GenericContainer {
		public Generic9x6(int i, PlayerInventory playerInventory) {
			super(ContainerType.GENERIC_9X6, i, playerInventory, 6);
		}

		public Generic9x6(int i, PlayerInventory playerInventory, Inventory inventory) {
			super(ContainerType.GENERIC_9X6, i, playerInventory, inventory, 6);
		}
	}
}
