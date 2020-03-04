package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
		if (method_24279(world, pos, currentStateInPos)) {
			world.setBlockState(pos, this.hardenedState, 3);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		BlockState blockState = blockView.getBlockState(blockPos);
		return method_24279(blockView, blockPos, blockState) ? this.hardenedState : super.getPlacementState(ctx);
	}

	private static boolean method_24279(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return hardensIn(blockState) || hardensOnAnySide(blockView, blockPos);
	}

	private static boolean hardensOnAnySide(BlockView world, BlockPos pos) {
		boolean bl = false;
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (Direction direction : Direction.values()) {
			BlockState blockState = world.getBlockState(mutable);
			if (direction != Direction.DOWN || hardensIn(blockState)) {
				mutable.move(pos, direction);
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
		return state.getFluidState().matches(FluidTags.WATER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return hardensOnAnySide(world, pos) ? this.hardenedState : super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getColor(BlockState state) {
		return this.materialColor.color;
	}
}
