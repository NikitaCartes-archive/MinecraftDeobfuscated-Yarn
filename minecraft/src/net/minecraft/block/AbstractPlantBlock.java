package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public abstract class AbstractPlantBlock extends AbstractPlantPartBlock {
	protected AbstractPlantBlock(Block.Settings settings, Direction direction, boolean bl) {
		super(settings, direction, bl);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, true);
		}

		super.scheduledTick(state, world, pos, random);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (facing == this.growthDirection.getOpposite() && !state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		AbstractPlantStemBlock abstractPlantStemBlock = this.getStem();
		if (facing == this.growthDirection) {
			Block block = neighborState.getBlock();
			if (block != this && block != abstractPlantStemBlock) {
				return abstractPlantStemBlock.getRandomGrowthState(world);
			}
		}

		if (this.tickWater) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(this.getStem());
	}

	@Override
	protected Block getPlant() {
		return this;
	}
}
