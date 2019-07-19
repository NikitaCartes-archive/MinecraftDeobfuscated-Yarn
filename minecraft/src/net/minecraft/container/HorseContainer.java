package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HorseContainer extends Container {
	private final Inventory playerInv;
	private final HorseBaseEntity entity;

	public HorseContainer(int syncId, PlayerInventory playerInventory, Inventory inventory, HorseBaseEntity horseBaseEntity) {
		super(null, syncId);
		this.playerInv = inventory;
		this.entity = horseBaseEntity;
		int i = 3;
		inventory.onInvOpen(playerInventory.player);
		int j = -18;
		this.addSlot(new Slot(inventory, 0, 8, 18) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return stack.getItem() == Items.SADDLE && !this.hasStack() && horseBaseEntity.canBeSaddled();
			}

			@Environment(EnvType.CLIENT)
			@Override
			public boolean doDrawHoveringEffect() {
				return horseBaseEntity.canBeSaddled();
			}
		});
		this.addSlot(new Slot(inventory, 1, 8, 36) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return horseBaseEntity.canEquip(stack);
			}

			@Environment(EnvType.CLIENT)
			@Override
			public boolean doDrawHoveringEffect() {
				return horseBaseEntity.canEquip();
			}

			@Override
			public int getMaxStackAmount() {
				return 1;
			}
		});
		if (horseBaseEntity instanceof AbstractDonkeyEntity && ((AbstractDonkeyEntity)horseBaseEntity).hasChest()) {
			for (int k = 0; k < 3; k++) {
				for (int l = 0; l < ((AbstractDonkeyEntity)horseBaseEntity).method_6702(); l++) {
					this.addSlot(new Slot(inventory, 2 + l + k * ((AbstractDonkeyEntity)horseBaseEntity).method_6702(), 80 + l * 18, 18 + k * 18));
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
		return this.playerInv.canPlayerUseInv(player) && this.entity.isAlive() && this.entity.distanceTo(player) < 8.0F;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot < this.playerInv.getInvSize()) {
				if (!this.insertItem(itemStack2, this.playerInv.getInvSize(), this.slots.size(), true)) {
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
			} else if (this.playerInv.getInvSize() <= 2 || !this.insertItem(itemStack2, 2, this.playerInv.getInvSize(), false)) {
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
		this.playerInv.onInvClose(player);
	}
}
