package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public abstract class AbstractPlantPartBlock extends Block {
	protected final Direction growthDirection;
	protected final boolean tickWater;

	protected AbstractPlantPartBlock(Block.Settings settings, Direction growthDirection, boolean tickWater) {
		super(settings);
		this.growthDirection = growthDirection;
		this.tickWater = tickWater;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.offset(this.growthDirection.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		return !this.canAttachTo(block)
			? false
			: block == this.getStem() || block == this.getPlant() || blockState.isSideSolidFullSquare(world, blockPos, this.growthDirection);
	}

	protected boolean canAttachTo(Block block) {
		return true;
	}

	protected abstract AbstractPlantStemBlock getStem();

	protected abstract Block getPlant();
}
