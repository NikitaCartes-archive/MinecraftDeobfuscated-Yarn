package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DropperBlockEntity extends DispenserBlockEntity {
	public DropperBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.DROPPER, blockPos, blockState);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.dropper");
	}
}
