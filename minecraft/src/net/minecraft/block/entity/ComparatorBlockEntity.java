package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;

public class ComparatorBlockEntity extends BlockEntity {
	private int outputSignal;

	public ComparatorBlockEntity() {
		super(BlockEntityType.COMPARATOR);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putInt("OutputSignal", this.outputSignal);
		return tag;
	}

	@Override
	public void fromTag(BlockState blockState, CompoundTag compoundTag) {
		super.fromTag(blockState, compoundTag);
		this.outputSignal = compoundTag.getInt("OutputSignal");
	}

	public int getOutputSignal() {
		return this.outputSignal;
	}

	public void setOutputSignal(int outputSignal) {
		this.outputSignal = outputSignal;
	}
}
