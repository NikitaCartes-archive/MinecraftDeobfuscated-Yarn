package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class CoralBlockBlock extends Block {
	private final Block deadCoralBlock;

	public CoralBlockBlock(Block deadCoralBlock, AbstractBlock.Settings settings) {
		super(settings);
		this.deadCoralBlock = deadCoralBlock;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!this.isInWater(world, pos)) {
			world.setBlockState(pos, this.deadCoralBlock.getDefaultState(), 2);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (!this.isInWater(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 60 + world.getRandom().nextInt(40));
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	protected boolean isInWater(BlockView world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			FluidState fluidState = world.getFluidState(pos.offset(direction));
			if (fluidState.isIn(FluidTags.WATER)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		if (!this.isInWater(ctx.getWorld(), ctx.getBlockPos())) {
			ctx.getWorld().getBlockTickScheduler().schedule(ctx.getBlockPos(), this, 60 + ctx.getWorld().getRandom().nextInt(40));
		}

		return this.getDefaultState();
	}
}
