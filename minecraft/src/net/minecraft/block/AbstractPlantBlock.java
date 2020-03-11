package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractPlantBlock extends AbstractPlantPartBlock implements Fertilizable {
	protected AbstractPlantBlock(Block.Settings settings, Direction direction, VoxelShape voxelShape, boolean bl) {
		super(settings, direction, voxelShape, bl);
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
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		Optional<BlockPos> optional = this.method_25960(world, pos, state);
		return optional.isPresent() && this.getStem().chooseStemState(world.getBlockState(((BlockPos)optional.get()).offset(this.growthDirection)));
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		Optional<BlockPos> optional = this.method_25960(world, pos, state);
		if (optional.isPresent()) {
			BlockState blockState = world.getBlockState((BlockPos)optional.get());
			((AbstractPlantStemBlock)blockState.getBlock()).grow(world, random, (BlockPos)optional.get(), blockState);
		}
	}

	private Optional<BlockPos> method_25960(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos;

		Block block;
		do {
			blockPos2 = blockPos2.offset(this.growthDirection);
			block = blockView.getBlockState(blockPos2).getBlock();
		} while (block == blockState.getBlock());

		return block == this.getStem() ? Optional.of(blockPos2) : Optional.empty();
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
		boolean bl = super.canReplace(state, ctx);
		return bl && ctx.getStack().getItem() == this.getStem().asItem() ? false : bl;
	}

	@Override
	protected Block getPlant() {
		return this;
	}
}
