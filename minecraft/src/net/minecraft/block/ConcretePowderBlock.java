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

	public ConcretePowderBlock(Block hardened, Block.Settings settings) {
		super(settings);
		this.hardenedState = hardened.getDefaultState();
	}

	@Override
	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos) {
		if (hardensIn(currentStateInPos)) {
			world.setBlockState(pos, this.hardenedState, 3);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		return !hardensIn(blockView.getBlockState(blockPos)) && !hardensOnAnySide(blockView, blockPos) ? super.getPlacementState(ctx) : this.hardenedState;
	}

	private static boolean hardensOnAnySide(BlockView view, BlockPos pos) {
		boolean bl = false;
		BlockPos.Mutable mutable = new BlockPos.Mutable(pos);

		for (Direction direction : Direction.values()) {
			BlockState blockState = view.getBlockState(mutable);
			if (direction != Direction.DOWN || hardensIn(blockState)) {
				mutable.set(pos).setOffset(direction);
				blockState = view.getBlockState(mutable);
				if (hardensIn(blockState) && !blockState.isSideSolidFullSquare(view, pos, direction.getOpposite())) {
					bl = true;
					break;
				}
			}
		}

		return bl;
	}

	private static boolean hardensIn(BlockState state) {
		return state.getFluidState().matches(FluidTags.WATER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return hardensOnAnySide(world, pos) ? this.hardenedState : super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}
}
