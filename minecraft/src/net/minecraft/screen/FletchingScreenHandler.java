package net.minecraft.screen;

import net.minecraft.block.entity.FletchingTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;

public class FletchingScreenHandler extends ScreenHandler {
	public static final int field_50530 = 59;
	public static final int field_50531 = 79;
	public static final int field_50532 = 38;
	private static final int field_50534 = 6;
	private static final int field_50535 = 3;
	private static final int field_50536 = 30;
	private static final int field_50537 = 30;
	private static final int field_50538 = 39;
	public static final int field_50533 = 160;
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;
	private final Slot inputSlot;

	public FletchingScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(6));
	}

	public FletchingScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
		super(ScreenHandlerType.FLETCHING, syncId);
		checkSize(inventory, 3);
		checkDataCount(propertyDelegate, 6);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		this.inputSlot = this.addSlot(new FletchingScreenHandler.FletchingInputSlot(inventory, 0, 239, 17));
		this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 1, 239, 59));
		this.addSlot(new Slot(inventory, 2, 180, 38) {
			@Override
			public int getMaxItemCount() {
				return 1;
			}
		});
		this.addProperties(propertyDelegate);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 168 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 168 + i * 18, 142));
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
			if (slot != 0 && slot != 1 && slot != 2) {
				if (this.inputSlot.canInsert(itemStack2)) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (itemStack2.isOf(Items.FEATHER)) {
					if (!this.insertItem(itemStack2, 2, 3, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slot >= 3 && slot < 30) {
					if (!this.insertItem(itemStack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slot >= 30 && slot < 39) {
					if (!this.insertItem(itemStack2, 3, 30, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, 3, 39, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot2.onQuickTransfer(itemStack2, itemStack);
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

	public int getProgress() {
		return this.propertyDelegate.get(0);
	}

	public char getQuality() {
		return (char)this.propertyDelegate.get(1);
	}

	public char getImpurities() {
		return (char)this.propertyDelegate.get(2);
	}

	public char getNextLevelImpurities() {
		return (char)this.propertyDelegate.get(3);
	}

	public int getProcessTime() {
		return this.propertyDelegate.get(4);
	}

	public boolean isExplored() {
		return this.propertyDelegate.get(5) > 0;
	}

	class FletchingInputSlot extends Slot {
		public FletchingInputSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean canInsert(ItemStack stack) {
			return FletchingTableBlockEntity.stackResinMatches(stack, FletchingScreenHandler.this.getQuality(), FletchingScreenHandler.this.getImpurities());
		}

		@Override
		public int getMaxItemCount() {
			return 64;
		}
	}
}
