package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralBlockBlock extends Block {
	private final Block deadCoralBlock;

	public CoralBlockBlock(Block block, Block.Settings settings) {
		super(settings);
		this.deadCoralBlock = block;
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!this.isInWater(world, blockPos)) {
			world.setBlockState(blockPos, this.deadCoralBlock.getDefaultState(), 2);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (!this.isInWater(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 60 + iWorld.getRandom().nextInt(40));
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	protected boolean isInWater(BlockView blockView, BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			FluidState fluidState = blockView.getFluidState(blockPos.offset(direction));
			if (fluidState.matches(FluidTags.field_15517)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		if (!this.isInWater(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())) {
			itemPlacementContext.getWorld()
				.getBlockTickScheduler()
				.schedule(itemPlacementContext.getBlockPos(), this, 60 + itemPlacementContext.getWorld().getRandom().nextInt(40));
		}

		return this.getDefaultState();
	}
}
