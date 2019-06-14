package net.minecraft.inventory;

import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class EnderChestInventory extends BasicInventory {
	private EnderChestBlockEntity field_7864;

	public EnderChestInventory() {
		super(27);
	}

	public void method_7661(EnderChestBlockEntity enderChestBlockEntity) {
		this.field_7864 = enderChestBlockEntity;
	}

	public void readTags(ListTag listTag) {
		for (int i = 0; i < this.getInvSize(); i++) {
			this.setInvStack(i, ItemStack.EMPTY);
		}

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			int j = compoundTag.getByte("Slot") & 255;
			if (j >= 0 && j < this.getInvSize()) {
				this.setInvStack(j, ItemStack.fromTag(compoundTag));
			}
		}
	}

	public ListTag getTags() {
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.getInvSize(); i++) {
			ItemStack itemStack = this.getInvStack(i);
			if (!itemStack.isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)i);
				itemStack.toTag(compoundTag);
				listTag.add(compoundTag);
			}
		}

		return listTag;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.field_7864 != null && !this.field_7864.canPlayerUse(playerEntity) ? false : super.canPlayerUseInv(playerEntity);
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
		if (this.field_7864 != null) {
			this.field_7864.onOpen();
		}

		super.onInvOpen(playerEntity);
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
		if (this.field_7864 != null) {
			this.field_7864.onClose();
		}

		super.onInvClose(playerEntity);
		this.field_7864 = null;
	}
}
