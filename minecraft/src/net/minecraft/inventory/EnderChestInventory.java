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

	public void method_7659(ListTag listTag) {
		for (int i = 0; i < this.getInvSize(); i++) {
			this.method_5447(i, ItemStack.EMPTY);
		}

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			int j = compoundTag.getByte("Slot") & 255;
			if (j >= 0 && j < this.getInvSize()) {
				this.method_5447(j, ItemStack.method_7915(compoundTag));
			}
		}
	}

	public ListTag method_7660() {
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.getInvSize(); i++) {
			ItemStack itemStack = this.method_5438(i);
			if (!itemStack.isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)i);
				itemStack.method_7953(compoundTag);
				listTag.add(compoundTag);
			}
		}

		return listTag;
	}

	@Override
	public boolean method_5443(PlayerEntity playerEntity) {
		return this.field_7864 != null && !this.field_7864.canPlayerUse(playerEntity) ? false : super.method_5443(playerEntity);
	}

	@Override
	public void method_5435(PlayerEntity playerEntity) {
		if (this.field_7864 != null) {
			this.field_7864.onOpen();
		}

		super.method_5435(playerEntity);
	}

	@Override
	public void method_5432(PlayerEntity playerEntity) {
		if (this.field_7864 != null) {
			this.field_7864.onClose();
		}

		super.method_5432(playerEntity);
		this.field_7864 = null;
	}
}
