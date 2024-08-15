package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;

public enum EmptyBlockRenderView implements BlockRenderView {
	INSTANCE;

	@Override
	public float getBrightness(Direction direction, boolean shaded) {
		return 1.0F;
	}

	@Override
	public LightingProvider getLightingProvider() {
		return LightingProvider.DEFAULT;
	}

	@Override
	public int getColor(BlockPos pos, ColorResolver colorResolver) {
		return -1;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return Fluids.EMPTY.getDefaultState();
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getBottomY() {
		return 0;
	}
}
