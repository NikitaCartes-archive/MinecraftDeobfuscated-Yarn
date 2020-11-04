package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class ComparatorBlockEntity extends BlockEntity {
	private int outputSignal;

	public ComparatorBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.COMPARATOR, blockPos, blockState);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putInt("OutputSignal", this.outputSignal);
		return tag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.outputSignal = compoundTag.getInt("OutputSignal");
	}

	public int getOutputSignal() {
		return this.outputSignal;
	}

	public void setOutputSignal(int outputSignal) {
		this.outputSignal = outputSignal;
	}
}
