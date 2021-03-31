package net.minecraft.block;

import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ConcretePowderBlock extends FallingBlock {
	private final BlockState hardenedState;

	public ConcretePowderBlock(Block hardened, AbstractBlock.Settings settings) {
		super(settings);
		this.hardenedState = hardened.getDefaultState();
	}

	@Override
	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
		if (shouldHarden(world, pos, currentStateInPos)) {
			world.setBlockState(pos, this.hardenedState, Block.NOTIFY_ALL);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		BlockState blockState = blockView.getBlockState(blockPos);
		return shouldHarden(blockView, blockPos, blockState) ? this.hardenedState : super.getPlacementState(ctx);
	}

	private static boolean shouldHarden(BlockView world, BlockPos pos, BlockState state) {
		return hardensIn(state) || hardensOnAnySide(world, pos);
	}

	private static boolean hardensOnAnySide(BlockView world, BlockPos pos) {
		boolean bl = false;
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (Direction direction : Direction.values()) {
			BlockState blockState = world.getBlockState(mutable);
			if (direction != Direction.DOWN || hardensIn(blockState)) {
				mutable.set(pos, direction);
				blockState = world.getBlockState(mutable);
				if (hardensIn(blockState) && !blockState.isSideSolidFullSquare(world, pos, direction.getOpposite())) {
					bl = true;
					break;
				}
			}
		}

		return bl;
	}

	private static boolean hardensIn(BlockState state) {
		return state.getFluidState().isIn(FluidTags.WATER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return hardensOnAnySide(world, pos) ? this.hardenedState : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public int getColor(BlockState state, BlockView world, BlockPos pos) {
		return state.getMapColor(world, pos).color;
	}
}
