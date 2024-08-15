package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class GenericContainerScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final int rows;

	private GenericContainerScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, int rows) {
		this(type, syncId, playerInventory, new SimpleInventory(9 * rows), rows);
	}

	public static GenericContainerScreenHandler createGeneric9x1(int syncId, PlayerInventory playerInventory) {
		return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, syncId, playerInventory, 1);
	}

	public static GenericContainerScreenHandler createGeneric9x2(int syncId, PlayerInventory playerInventory) {
		return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X2, syncId, playerInventory, 2);
	}

	public static GenericContainerScreenHandler createGeneric9x3(int syncId, PlayerInventory playerInventory) {
		return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, 3);
	}

	public static GenericContainerScreenHandler createGeneric9x4(int syncId, PlayerInventory playerInventory) {
		return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X4, syncId, playerInventory, 4);
	}

	public static GenericContainerScreenHandler createGeneric9x5(int syncId, PlayerInventory playerInventory) {
		return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, syncId, playerInventory, 5);
	}

	public static GenericContainerScreenHandler createGeneric9x6(int syncId, PlayerInventory playerInventory) {
		return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, 6);
	}

	public static GenericContainerScreenHandler createGeneric9x3(int syncId, PlayerInventory playerInventory, Inventory inventory) {
		return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, inventory, 3);
	}

	public static GenericContainerScreenHandler createGeneric9x6(int syncId, PlayerInventory playerInventory, Inventory inventory) {
		return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inventory, 6);
	}

	public GenericContainerScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
		super(type, syncId);
		checkSize(inventory, rows * 9);
		this.inventory = inventory;
		this.rows = rows;
		inventory.onOpen(playerInventory.player);
		int i = 18;
		this.addInventorySlots(inventory, 8, 18);
		int j = 18 + this.rows * 18 + 13;
		this.addPlayerSlots(playerInventory, 8, j);
	}

	private void addInventorySlots(Inventory inventory, int left, int top) {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(inventory, j + i * 9, left + j * 18, top + i * 18));
			}
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot2 = this.slots.get(slot);
		if (slot2 != null && slot2.hasStack()) {
			ItemStack itemStack2 = slot2.getStack();
			itemStack = itemStack2.copy();
			if (slot < this.rows * 9) {
				if (!this.insertItem(itemStack2, this.rows * 9, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 0, this.rows * 9, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStack(ItemStack.EMPTY);
			} else {
				slot2.markDirty();
			}
		}

		return itemStack;
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.inventory.onClose(player);
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public int getRows() {
		return this.rows;
	}
}
