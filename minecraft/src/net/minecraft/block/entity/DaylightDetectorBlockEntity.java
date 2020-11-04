package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class DaylightDetectorBlockEntity extends BlockEntity {
	public DaylightDetectorBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.DAYLIGHT_DETECTOR, blockPos, blockState);
	}
}
