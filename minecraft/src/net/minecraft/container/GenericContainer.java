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

	public static GenericContainer method_19244(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.field_18664, i, playerInventory, 1);
	}

	public static GenericContainer method_19246(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.field_18665, i, playerInventory, 2);
	}

	public static GenericContainer method_19248(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.GENERIC_9X3, i, playerInventory, 3);
	}

	public static GenericContainer method_19249(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.field_18666, i, playerInventory, 4);
	}

	public static GenericContainer method_19250(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.field_18667, i, playerInventory, 5);
	}

	public static GenericContainer method_19251(int i, PlayerInventory playerInventory) {
		return new GenericContainer(ContainerType.GENERIC_9X6, i, playerInventory, 6);
	}

	public static GenericContainer method_19245(int i, PlayerInventory playerInventory, Inventory inventory) {
		return new GenericContainer(ContainerType.GENERIC_9X3, i, playerInventory, inventory, 3);
	}

	public static GenericContainer method_19247(int i, PlayerInventory playerInventory, Inventory inventory) {
		return new GenericContainer(ContainerType.GENERIC_9X6, i, playerInventory, inventory, 6);
	}

	public GenericContainer(ContainerType<?> containerType, int i, PlayerInventory playerInventory, Inventory inventory, int j) {
		super(containerType, i);
		checkContainerSize(inventory, j * 9);
		this.inventory = inventory;
		this.rows = j;
		inventory.method_5435(playerInventory.field_7546);
		int k = (this.rows - 4) * 18;

		for (int l = 0; l < this.rows; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new Slot(inventory, m + l * 9, 8 + m * 18, 18 + l * 18));
			}
		}

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new Slot(playerInventory, m + l * 9 + 9, 8 + m * 18, 103 + l * 18 + k));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.method_7621(new Slot(playerInventory, l, 8 + l * 18, 161 + k));
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
			if (i < this.rows * 9) {
				if (!this.method_7616(itemStack2, this.rows * 9, this.slotList.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.method_7616(itemStack2, 0, this.rows * 9, false)) {
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

	public Inventory getInventory() {
		return this.inventory;
	}

	@Environment(EnvType.CLIENT)
	public int getRows() {
		return this.rows;
	}
}
