package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class EnderChestInventory extends SimpleInventory {
	@Nullable
	private EnderChestBlockEntity activeBlockEntity;

	public EnderChestInventory() {
		super(27);
	}

	public void setActiveBlockEntity(EnderChestBlockEntity blockEntity) {
		this.activeBlockEntity = blockEntity;
	}

	public boolean method_31556(EnderChestBlockEntity enderChestBlockEntity) {
		return this.activeBlockEntity == enderChestBlockEntity;
	}

	@Override
	public void readTags(ListTag tags) {
		for (int i = 0; i < this.size(); i++) {
			this.setStack(i, ItemStack.EMPTY);
		}

		for (int i = 0; i < tags.size(); i++) {
			CompoundTag compoundTag = tags.getCompound(i);
			int j = compoundTag.getByte("Slot") & 255;
			if (j >= 0 && j < this.size()) {
				this.setStack(j, ItemStack.fromNbt(compoundTag));
			}
		}
	}

	@Override
	public ListTag getTags() {
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.size(); i++) {
			ItemStack itemStack = this.getStack(i);
			if (!itemStack.isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)i);
				itemStack.writeNbt(compoundTag);
				listTag.add(compoundTag);
			}
		}

		return listTag;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.activeBlockEntity != null && !this.activeBlockEntity.canPlayerUse(player) ? false : super.canPlayerUse(player);
	}

	@Override
	public void onOpen(PlayerEntity player) {
		if (this.activeBlockEntity != null) {
			this.activeBlockEntity.onOpen(player);
		}

		super.onOpen(player);
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (this.activeBlockEntity != null) {
			this.activeBlockEntity.onClose(player);
		}

		super.onClose(player);
		this.activeBlockEntity = null;
	}
}
