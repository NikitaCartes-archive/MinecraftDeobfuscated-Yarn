package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ConcretePowderBlock extends FallingBlock {
	private final BlockState field_10810;

	public ConcretePowderBlock(Block block, Block.Settings settings) {
		super(settings);
		this.field_10810 = block.getDefaultState();
	}

	@Override
	public void method_10127(World world, BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		if (method_9799(blockState2)) {
			world.setBlockState(blockPos, this.field_10810, 3);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		return !method_9799(blockView.getBlockState(blockPos)) && !method_9798(blockView, blockPos)
			? super.getPlacementState(itemPlacementContext)
			: this.field_10810;
	}

	private static boolean method_9798(BlockView blockView, BlockPos blockPos) {
		boolean bl = false;
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

		for (Direction direction : Direction.values()) {
			BlockState blockState = blockView.getBlockState(mutable);
			if (direction != Direction.DOWN || method_9799(blockState)) {
				mutable.set(blockPos).method_10098(direction);
				blockState = blockView.getBlockState(mutable);
				if (method_9799(blockState) && !Block.method_9501(blockState.method_11628(blockView, blockPos), direction.getOpposite())) {
					bl = true;
					break;
				}
			}
		}

		return bl;
	}

	private static boolean method_9799(BlockState blockState) {
		return blockState.getFluidState().matches(FluidTags.field_15517);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return method_9798(iWorld, blockPos) ? this.field_10810 : super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}
}
