package net.minecraft.container;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3914;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BeaconContainer extends Container {
	private final Inventory field_17287 = new BasicInventory(1) {
		@Override
		public boolean isValidInvStack(int i, ItemStack itemStack) {
			return itemStack.getItem() == Items.field_8687
				|| itemStack.getItem() == Items.field_8477
				|| itemStack.getItem() == Items.field_8695
				|| itemStack.getItem() == Items.field_8620;
		}

		@Override
		public int getInvMaxStackAmount() {
			return 1;
		}
	};
	private final BeaconContainer.SlotPayment field_17288;
	private final class_3914 field_17289;
	private final PropertyDelegate propertyDelegate;

	public BeaconContainer(int i, Inventory inventory) {
		this(i, inventory, new ArrayPropertyDelegate(3), class_3914.field_17304);
	}

	public BeaconContainer(int i, Inventory inventory, PropertyDelegate propertyDelegate, class_3914 arg) {
		super(ContainerType.BEACON, i);
		checkContainerDataCount(propertyDelegate, 3);
		this.propertyDelegate = propertyDelegate;
		this.field_17289 = arg;
		this.field_17288 = new BeaconContainer.SlotPayment(this.field_17287, 0, 136, 110);
		this.addSlot(this.field_17288);
		this.readData(propertyDelegate);
		int j = 36;
		int k = 137;

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.addSlot(new Slot(inventory, m + l * 9 + 9, 36 + m * 18, 137 + l * 18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.addSlot(new Slot(inventory, l, 36 + l * 18, 195));
		}
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		if (!playerEntity.world.isClient) {
			ItemStack itemStack = this.field_17288.takeStack(this.field_17288.getMaxStackAmount());
			if (!itemStack.isEmpty()) {
				playerEntity.dropItem(itemStack, false);
			}
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return canUse(this.field_17289, playerEntity, Blocks.field_10327);
	}

	@Override
	public void setProperty(int i, int j) {
		super.setProperty(i, j);
		this.sendContentUpdates();
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
			} else if (!this.field_17288.hasStack() && this.field_17288.canInsert(itemStack2) && itemStack2.getAmount() == 1) {
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

	@Environment(EnvType.CLIENT)
	public int method_17373() {
		return this.propertyDelegate.get(0);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public StatusEffect method_17374() {
		return StatusEffect.byRawId(this.propertyDelegate.get(1));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public StatusEffect method_17375() {
		return StatusEffect.byRawId(this.propertyDelegate.get(2));
	}

	public void method_17372(int i, int j) {
		if (this.field_17288.hasStack()) {
			this.propertyDelegate.set(1, i);
			this.propertyDelegate.set(2, j);
			this.field_17288.takeStack(1);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_17376() {
		return !this.field_17287.getInvStack(0).isEmpty();
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
