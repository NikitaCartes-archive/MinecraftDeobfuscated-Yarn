package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;

public class JukeboxBlockEntity extends BlockEntity implements Clearable {
	private ItemStack record = ItemStack.EMPTY;

	public JukeboxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.JUKEBOX, pos, state);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		if (tag.contains("RecordItem", 10)) {
			this.setRecord(ItemStack.fromTag(tag.getCompound("RecordItem")));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		if (!this.getRecord().isEmpty()) {
			tag.put("RecordItem", this.getRecord().toTag(new CompoundTag()));
		}

		return tag;
	}

	public ItemStack getRecord() {
		return this.record;
	}

	public void setRecord(ItemStack stack) {
		this.record = stack;
		this.markDirty();
	}

	@Override
	public void clear() {
		this.setRecord(ItemStack.EMPTY);
	}
}
