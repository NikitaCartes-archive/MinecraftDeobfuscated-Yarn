package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class Generic3x3ContainerScreenHandler extends ScreenHandler {
	private static final int CONTAINER_SIZE = 9;
	private static final int INVENTORY_START = 9;
	private static final int INVENTORY_END = 36;
	private static final int HOTBAR_START = 36;
	private static final int HOTBAR_END = 45;
	private final Inventory inventory;

	public Generic3x3ContainerScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(9));
	}

	public Generic3x3ContainerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
		super(ScreenHandlerType.GENERIC_3X3, syncId);
		checkSize(inventory, 9);
		this.inventory = inventory;
		inventory.onOpen(playerInventory.player);
		this.add3x3Slots(inventory, 62, 17);
		this.addPlayerSlots(playerInventory, 8, 84);
	}

	protected void add3x3Slots(Inventory inventory, int x, int y) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int k = j + i * 3;
				this.addSlot(new Slot(inventory, k, x + j * 18, y + i * 18));
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
			if (slot < 9) {
				if (!this.insertItem(itemStack2, 9, 45, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 0, 9, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot2.setStack(ItemStack.EMPTY);
			} else {
				slot2.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot2.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	@Override
	public void onClosed(PlayerEntity player) {
		super.onClosed(player);
		this.inventory.onClose(player);
	}
}
