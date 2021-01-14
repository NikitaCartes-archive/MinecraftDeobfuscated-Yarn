package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Clearable;

public class JukeboxBlockEntity extends BlockEntity implements Clearable {
	private ItemStack record = ItemStack.EMPTY;

	public JukeboxBlockEntity() {
		super(BlockEntityType.JUKEBOX);
	}

	@Override
	public void fromTag(BlockState state, NbtCompound tag) {
		super.fromTag(state, tag);
		if (tag.contains("RecordItem", 10)) {
			this.setRecord(ItemStack.fromNbt(tag.getCompound("RecordItem")));
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (!this.getRecord().isEmpty()) {
			nbt.put("RecordItem", this.getRecord().writeNbt(new NbtCompound()));
		}

		return nbt;
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
