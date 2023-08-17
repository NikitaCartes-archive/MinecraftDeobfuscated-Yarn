package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

/**
 * Represents an inventory used for ender chests.
 * A new instance is created for each player.
 */
public class EnderChestInventory extends SimpleInventory {
	@Nullable
	private EnderChestBlockEntity activeBlockEntity;

	public EnderChestInventory() {
		super(27);
	}

	/**
	 * Sets the block entity the player is using to access the inventory to {@code
	 * blockEntity}. The block entity is used to delegate {@link #canPlayerUse},
	 * {@link #onOpen}, and {@link #onClose}.
	 */
	public void setActiveBlockEntity(EnderChestBlockEntity blockEntity) {
		this.activeBlockEntity = blockEntity;
	}

	/**
	 * {@return whether this inventory is being accessed from {@code blockEntity}}
	 */
	public boolean isActiveBlockEntity(EnderChestBlockEntity blockEntity) {
		return this.activeBlockEntity == blockEntity;
	}

	@Override
	public void readNbtList(NbtList nbtList) {
		for (int i = 0; i < this.size(); i++) {
			this.setStack(i, ItemStack.EMPTY);
		}

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 255;
			if (j >= 0 && j < this.size()) {
				this.setStack(j, ItemStack.fromNbt(nbtCompound));
			}
		}
	}

	@Override
	public NbtList toNbtList() {
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.size(); i++) {
			ItemStack itemStack = this.getStack(i);
			if (!itemStack.isEmpty()) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Slot", (byte)i);
				itemStack.writeNbt(nbtCompound);
				nbtList.add(nbtCompound);
			}
		}

		return nbtList;
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
