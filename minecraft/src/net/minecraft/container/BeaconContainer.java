package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BeaconContainer extends Container {
	private final Inventory inventory;
	private final BeaconContainer.SlotPayment slotPayment;

	public BeaconContainer(Inventory inventory, Inventory inventory2) {
		this.inventory = inventory2;
		this.slotPayment = new BeaconContainer.SlotPayment(inventory2, 0, 136, 110);
		this.addSlot(this.slotPayment);
		int i = 36;
		int j = 137;

		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 9; l++) {
				this.addSlot(new Slot(inventory, l + k * 9 + 9, 36 + l * 18, 137 + k * 18));
			}
		}

		for (int k = 0; k < 9; k++) {
			this.addSlot(new Slot(inventory, k, 36 + k * 18, 195));
		}
	}

	@Override
	public void addListener(ContainerListener containerListener) {
		super.addListener(containerListener);
		containerListener.onContainerInvRegistered(this, this.inventory);
	}

	@Override
	public void setProperty(int i, int j) {
		this.inventory.setInvProperty(i, j);
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		if (!playerEntity.world.isClient) {
			ItemStack itemStack = this.slotPayment.takeStack(this.slotPayment.getMaxStackAmount());
			if (!itemStack.isEmpty()) {
				playerEntity.dropItem(itemStack, false);
			}
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
			if (i == 0) {
				if (!this.insertItem(itemStack2, 1, 37, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (!this.slotPayment.hasStack() && this.slotPayment.canInsert(itemStack2) && itemStack2.getAmount() == 1) {
				if (!this.insertItem(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 1 && i < 28) {
				if (!this.insertItem(itemStack2, 28, 37, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 28 && i < 37) {
				if (!this.insertItem(itemStack2, 1, 28, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 1, 37, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(playerEntity, itemStack2);
		}

		return itemStack;
	}

	class SlotPayment extends Slot {
		public SlotPayment(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean canInsert(ItemStack itemStack) {
			Item item = itemStack.getItem();
			return item == Items.field_8687 || item == Items.field_8477 || item == Items.field_8695 || item == Items.field_8620;
		}

		@Override
		public int getMaxStackAmount() {
			return 1;
		}
	}
}
