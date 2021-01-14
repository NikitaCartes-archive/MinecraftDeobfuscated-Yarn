package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;

public class ComparatorBlockEntity extends BlockEntity {
	private int outputSignal;

	public ComparatorBlockEntity() {
		super(BlockEntityType.COMPARATOR);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("OutputSignal", this.outputSignal);
		return nbt;
	}

	@Override
	public void fromTag(BlockState state, NbtCompound tag) {
		super.fromTag(state, tag);
		this.outputSignal = tag.getInt("OutputSignal");
	}

	public int getOutputSignal() {
		return this.outputSignal;
	}

	public void setOutputSignal(int outputSignal) {
		this.outputSignal = outputSignal;
	}
}
