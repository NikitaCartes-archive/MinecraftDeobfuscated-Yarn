package net.minecraft.container;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BeaconContainer extends Container {
	private final Inventory paymentInv = new BasicInventory(1) {
		@Override
		public boolean method_5437(int i, ItemStack itemStack) {
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
	private final BeaconContainer.SlotPayment paymentSlot;
	private final BlockContext field_17289;
	private final PropertyDelegate field_17290;

	public BeaconContainer(int i, Inventory inventory) {
		this(i, inventory, new ArrayPropertyDelegate(3), BlockContext.EMPTY);
	}

	public BeaconContainer(int i, Inventory inventory, PropertyDelegate propertyDelegate, BlockContext blockContext) {
		super(ContainerType.BEACON, i);
		method_17361(propertyDelegate, 3);
		this.field_17290 = propertyDelegate;
		this.field_17289 = blockContext;
		this.paymentSlot = new BeaconContainer.SlotPayment(this.paymentInv, 0, 136, 110);
		this.method_7621(this.paymentSlot);
		this.method_17360(propertyDelegate);
		int j = 36;
		int k = 137;

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.method_7621(new Slot(inventory, m + l * 9 + 9, 36 + m * 18, 137 + l * 18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.method_7621(new Slot(inventory, l, 36 + l * 18, 195));
		}
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		if (!playerEntity.field_6002.isClient) {
			ItemStack itemStack = this.paymentSlot.method_7671(this.paymentSlot.getMaxStackAmount());
			if (!itemStack.isEmpty()) {
				playerEntity.method_7328(itemStack, false);
			}
		}
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return method_17695(this.field_17289, playerEntity, Blocks.field_10327);
	}

	@Override
	public void setProperty(int i, int j) {
		super.setProperty(i, j);
		this.sendContentUpdates();
	}

	@Override
	public ItemStack method_7601(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.method_7677();
			itemStack = itemStack2.copy();
			if (i == 0) {
				if (!this.method_7616(itemStack2, 1, 37, true)) {
					return ItemStack.EMPTY;
				}

				slot.method_7670(itemStack2, itemStack);
			} else if (!this.paymentSlot.hasStack() && this.paymentSlot.method_7680(itemStack2) && itemStack2.getAmount() == 1) {
				if (!this.method_7616(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 1 && i < 28) {
				if (!this.method_7616(itemStack2, 28, 37, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 28 && i < 37) {
				if (!this.method_7616(itemStack2, 1, 28, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.method_7616(itemStack2, 1, 37, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.method_7673(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.method_7667(playerEntity, itemStack2);
		}

		return itemStack;
	}

	@Environment(EnvType.CLIENT)
	public int method_17373() {
		return this.field_17290.get(0);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public StatusEffect getPrimaryEffect() {
		return StatusEffect.byRawId(this.field_17290.get(1));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public StatusEffect getSecondaryEffect() {
		return StatusEffect.byRawId(this.field_17290.get(2));
	}

	public void method_17372(int i, int j) {
		if (this.paymentSlot.hasStack()) {
			this.field_17290.set(1, i);
			this.field_17290.set(2, j);
			this.paymentSlot.method_7671(1);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean hasPayment() {
		return !this.paymentInv.method_5438(0).isEmpty();
	}

	class SlotPayment extends Slot {
		public SlotPayment(Inventory inventory, int i, int j, int k) {
			super(inventory, i, j, k);
		}

		@Override
		public boolean method_7680(ItemStack itemStack) {
			Item item = itemStack.getItem();
			return item == Items.field_8687 || item == Items.field_8477 || item == Items.field_8695 || item == Items.field_8620;
		}

		@Override
		public int getMaxStackAmount() {
			return 1;
		}
	}
}
