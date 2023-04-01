package net.minecraft.screen;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.vote.MidasCurser;
import net.minecraft.world.World;

public class GenericContainerScreenHandler extends ScreenHandler {
	private static final int field_30780 = 9;
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
		int i = (this.rows - 4) * 18;

		for (int j = 0; j < this.rows; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 161 + i));
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

	@Override
	public void method_50780(World world) {
		if (this.method_50783() && world.random.nextInt(20) == 0) {
			int i = world.random.nextInt(this.inventory.size());
			ItemStack itemStack = this.inventory.getStack(i);
			this.inventory.setStack(i, MidasCurser.curse(itemStack));
		}
	}

	private boolean method_50783() {
		if (this.inventory instanceof ChestBlockEntity chestBlockEntity) {
			return chestBlockEntity.method_50887();
		} else {
			return this.inventory instanceof LivingEntity livingEntity ? livingEntity.isGolden() : false;
		}
	}
}
