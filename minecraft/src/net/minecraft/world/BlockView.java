package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;

public interface BlockView {
	default int getLuminance(BlockPos blockPos) {
		return this.getBlockState(blockPos).getLuminance();
	}

	@Nullable
	BlockEntity getBlockEntity(BlockPos blockPos);

	BlockState getBlockState(BlockPos blockPos);

	FluidState getFluidState(BlockPos blockPos);

	default int getMaxLightLevel() {
		return 15;
	}

	default int getHeight() {
		return 256;
	}
}
