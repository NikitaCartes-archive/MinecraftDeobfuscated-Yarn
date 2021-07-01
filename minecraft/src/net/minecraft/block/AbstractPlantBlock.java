package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class AbstractPlantBlock extends AbstractPlantPartBlock implements Fertilizable {
	protected AbstractPlantBlock(AbstractBlock.Settings settings, Direction direction, VoxelShape voxelShape, boolean bl) {
		super(settings, direction, voxelShape, bl);
	}

	protected BlockState copyState(BlockState from, BlockState to) {
		return to;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (direction == this.growthDirection.getOpposite() && !state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		AbstractPlantStemBlock abstractPlantStemBlock = this.getStem();
		if (direction == this.growthDirection && !neighborState.isOf(this) && !neighborState.isOf(abstractPlantStemBlock)) {
			return this.copyState(state, abstractPlantStemBlock.getRandomGrowthState(world));
		} else {
			if (this.tickWater) {
				world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}

			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(this.getStem());
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		Optional<BlockPos> optional = this.getStemHeadPos(world, pos, state.getBlock());
		return optional.isPresent() && this.getStem().chooseStemState(world.getBlockState(((BlockPos)optional.get()).offset(this.growthDirection)));
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		Optional<BlockPos> optional = this.getStemHeadPos(world, pos, state.getBlock());
		if (optional.isPresent()) {
			BlockState blockState = world.getBlockState((BlockPos)optional.get());
			((AbstractPlantStemBlock)blockState.getBlock()).grow(world, random, (BlockPos)optional.get(), blockState);
		}
	}

	private Optional<BlockPos> getStemHeadPos(BlockView world, BlockPos pos, Block block) {
		return BlockLocating.findColumnEnd(world, pos, block, this.growthDirection, this.getStem());
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		boolean bl = super.canReplace(state, context);
		return bl && context.getStack().isOf(this.getStem().asItem()) ? false : bl;
	}

	@Override
	protected Block getPlant() {
		return this;
	}
}
