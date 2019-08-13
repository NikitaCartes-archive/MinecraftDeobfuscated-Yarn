package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class GenericContainer extends Container {
	private final Inventory inventory;
	private final int rows;

	private GenericContainer(ContainerType<?> containerType, int i, PlayerInventory playerInventory, int j) {
		this(containerType, i, playerInventory, new BasicInventory(9 * j), j);
	}

	public static GenericContainer createGeneric9x1(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.field_18664, i, playerInventory, 1);
	}

	public static GenericContainer createGeneric9x2(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.field_18665, i, playerInventory, 2);
	}

	public static GenericContainer createGeneric9x3(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.field_17326, i, playerInventory, 3);
	}

	public static GenericContainer createGeneric9x4(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.field_18666, i, playerInventory, 4);
	}

	public static GenericContainer createGeneric9x5(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.field_18667, i, playerInventory, 5);
	}

	public static GenericContainer createGeneric9x6(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.field_17327, i, playerInventory, 6);
	}

	public static GenericContainer createGeneric9x3(int i, PlayerInventory playerInventory, Inventory inventory) {
		return new GenericContainer(ContainerType.field_17326, i, playerInventory, inventory, 3);
	}

	public static GenericContainer createGeneric9x6(int i, PlayerInventory playerInventory, Inventory inventory) {
		return new GenericContainer(ContainerType.field_17327, i, playerInventory, inventory, 6);
	}

	public GenericContainer(ContainerType<?> containerType, int i, PlayerInventory playerInventory, Inventory inventory, int j) {
		super(containerType, i);
		checkContainerSize(inventory, j * 9);
		this.inventory = inventory;
		this.rows = j;
		inventory.onInvOpen(playerInventory.player);
		int k = (this.rows - 4) * 18;

		for (int l = 0; l < this.rows; l++) {
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
		return this.inventory.canPlayerUseInv(playerEntity);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i < this.rows * 9) {
				if (!this.insertItem(itemStack2, this.rows * 9, this.slotList.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 0, this.rows * 9, false)) {
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
		this.inventory.onInvClose(playerEntity);
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	@Environment(EnvType.CLIENT)
	public int getRows() {
		return this.rows;
	}
}
