package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ConcretePowderBlock extends FallingBlock {
	private final BlockState hardenedState;

	public ConcretePowderBlock(Block block, Block.Settings settings) {
		super(settings);
		this.hardenedState = block.getDefaultState();
	}

	@Override
	public void onLanding(World world, BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		if (hardensIn(blockState2)) {
			world.setBlockState(blockPos, this.hardenedState, 3);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		return !hardensIn(blockView.getBlockState(blockPos)) && !hardensOnAnySide(blockView, blockPos)
			? super.getPlacementState(itemPlacementContext)
			: this.hardenedState;
	}

	private static boolean hardensOnAnySide(BlockView blockView, BlockPos blockPos) {
		boolean bl = false;
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

		for (Direction direction : Direction.values()) {
			BlockState blockState = blockView.getBlockState(mutable);
			if (direction != Direction.field_11033 || hardensIn(blockState)) {
				mutable.set(blockPos).setOffset(direction);
				blockState = blockView.getBlockState(mutable);
				if (hardensIn(blockState) && !blockState.isSideSolidFullSquare(blockView, blockPos, direction.getOpposite())) {
					bl = true;
					break;
				}
			}
		}

		return bl;
	}

	private static boolean hardensIn(BlockState blockState) {
		return blockState.getFluidState().matches(FluidTags.field_15517);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return hardensOnAnySide(iWorld, blockPos)
			? this.hardenedState
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}
}
