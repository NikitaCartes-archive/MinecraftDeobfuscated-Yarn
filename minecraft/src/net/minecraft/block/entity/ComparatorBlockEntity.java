package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class ComparatorBlockEntity extends BlockEntity {
	private int outputSignal;

	public ComparatorBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.COMPARATOR, pos, state);
	}

	@Override
	public CompoundTag writeNbt(CompoundTag tag) {
		super.writeNbt(tag);
		tag.putInt("OutputSignal", this.outputSignal);
		return tag;
	}

	@Override
	public void readNbt(CompoundTag tag) {
		super.readNbt(tag);
		this.outputSignal = tag.getInt("OutputSignal");
	}

	public int getOutputSignal() {
		return this.outputSignal;
	}

	public void setOutputSignal(int outputSignal) {
		this.outputSignal = outputSignal;
	}
}
