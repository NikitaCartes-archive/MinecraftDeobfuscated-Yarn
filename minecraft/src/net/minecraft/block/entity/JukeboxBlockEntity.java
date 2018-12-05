package net.minecraft.block.entity;

import net.minecraft.class_3829;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class JukeboxBlockEntity extends BlockEntity implements class_3829 {
	private ItemStack record = ItemStack.EMPTY;

	public JukeboxBlockEntity() {
		super(BlockEntityType.JUKEBOX);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		if (compoundTag.containsKey("RecordItem", 10)) {
			this.setRecord(ItemStack.fromTag(compoundTag.getCompound("RecordItem")));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (!this.getRecord().isEmpty()) {
			compoundTag.put("RecordItem", this.getRecord().toTag(new CompoundTag()));
		}

		return compoundTag;
	}

	public ItemStack getRecord() {
		return this.record;
	}

	public void setRecord(ItemStack itemStack) {
		this.record = itemStack;
		this.markDirty();
	}

	@Override
	public void clearInv() {
		this.setRecord(ItemStack.EMPTY);
	}
}
