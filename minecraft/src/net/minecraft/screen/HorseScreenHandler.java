package net.minecraft.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class HorseScreenHandler extends ScreenHandler {
	private final Inventory inventory;
	private final HorseBaseEntity entity;

	public HorseScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, HorseBaseEntity entity) {
		super(null, syncId);
		this.inventory = inventory;
		this.entity = entity;
		int i = 3;
		inventory.onOpen(playerInventory.player);
		int j = -18;
		this.addSlot(new Slot(inventory, 0, 8, 18) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() == Items.SADDLE && !this.hasStack() && entity.canBeSaddled();
			}

			@Environment(EnvType.CLIENT)
			@Override
			public boolean doDrawHoveringEffect() {
				return entity.canBeSaddled();
			}
		});
		this.addSlot(new Slot(inventory, 1, 8, 36) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return entity.isHorseArmor(stack);
			}

			@Environment(EnvType.CLIENT)
			@Override
			public boolean doDrawHoveringEffect() {
				return entity.hasArmorSlot();
			}

			@Override
			public int getMaxStackAmount() {
				return 1;
			}
		});
		if (entity instanceof AbstractDonkeyEntity && ((AbstractDonkeyEntity)entity).hasChest()) {
			for (int k = 0; k < 3; k++) {
				for (int l = 0; l < ((AbstractDonkeyEntity)entity).getInventoryColumns(); l++) {
					this.addSlot(new Slot(inventory, 2 + l + k * ((AbstractDonkeyEntity)entity).getInventoryColumns(), 80 + l * 18, 18 + k * 18));
				}
			}
		}

		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 9; l++) {
				this.addSlot(new Slot(playerInventory, l + k * 9 + 9, 8 + l * 18, 102 + k * 18 + -18));
			}
		}

		for (int k = 0; k < 9; k++) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
		}
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player) && this.entity.isAlive() && this.entity.distanceTo(player) < 8.0F;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			int i = this.inventory.size();
			if (index < i) {
				if (!this.insertItem(itemStack2, i, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(1).canInsert(itemStack2) && !this.getSlot(1).hasStack()) {
				if (!this.insertItem(itemStack2, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(0).canInsert(itemStack2)) {
				if (!this.insertItem(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i <= 2 || !this.insertItem(itemStack2, 2, i, false)) {
				int k = i + 27;
				int m = k + 9;
				if (index >= k && index < m) {
					if (!this.insertItem(itemStack2, i, k, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= i && index < k) {
					if (!this.insertItem(itemStack2, k, m, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(itemStack2, k, k, false)) {
					return ItemStack.EMPTY;
				}

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
	public void close(PlayerEntity player) {
		super.close(player);
		this.inventory.onClose(player);
	}
}
