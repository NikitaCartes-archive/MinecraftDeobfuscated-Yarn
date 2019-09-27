package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4538;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class KelpPlantBlock extends Block implements FluidFillable {
	private final KelpBlock kelpBlock;

	protected KelpPlantBlock(KelpBlock kelpBlock, Block.Settings settings) {
		super(settings);
		this.kelpBlock = kelpBlock;
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return Fluids.WATER.getStill(false);
	}

	@Override
	public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(serverWorld, blockPos)) {
			serverWorld.breakBlock(blockPos, true);
		}

		super.onScheduledTick(blockState, serverWorld, blockPos, random);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		if (direction == Direction.UP) {
			Block block = blockState2.getBlock();
			if (block != this && block != this.kelpBlock) {
				return this.kelpBlock.getPlacementState(iWorld);
			}
		}

		iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.method_10074();
		BlockState blockState2 = arg.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		return block != Blocks.MAGMA_BLOCK && (block == this || blockState2.isSideSolidFullSquare(arg, blockPos2, Direction.UP));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Blocks.KELP);
	}

	@Override
	public boolean canFillWithFluid(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return false;
	}

	@Override
	public boolean tryFillWithFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return false;
	}
}
