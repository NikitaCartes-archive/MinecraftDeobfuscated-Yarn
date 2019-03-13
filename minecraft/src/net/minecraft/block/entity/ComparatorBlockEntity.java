package net.minecraft.block.entity;

import net.minecraft.nbt.CompoundTag;

public class ComparatorBlockEntity extends BlockEntity {
	private int outputSignal;

	public ComparatorBlockEntity() {
		super(BlockEntityType.COMPARATOR);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		compoundTag.putInt("OutputSignal", this.outputSignal);
		return compoundTag;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.outputSignal = compoundTag.getInt("OutputSignal");
	}

	public int getOutputSignal() {
		return this.outputSignal;
	}

	public void setOutputSignal(int i) {
		this.outputSignal = i;
	}
}
