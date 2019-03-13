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
		this.field_10810 = block.method_9564();
	}

	@Override
	public void method_10127(World world, BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		if (method_9799(blockState2)) {
			world.method_8652(blockPos, this.field_10810, 3);
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.method_8037();
		return !method_9799(blockView.method_8320(blockPos)) && !method_9798(blockView, blockPos) ? super.method_9605(itemPlacementContext) : this.field_10810;
	}

	private static boolean method_9798(BlockView blockView, BlockPos blockPos) {
		boolean bl = false;
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

		for (Direction direction : Direction.values()) {
			BlockState blockState = blockView.method_8320(mutable);
			if (direction != Direction.DOWN || method_9799(blockState)) {
				mutable.method_10101(blockPos).method_10098(direction);
				blockState = blockView.method_8320(mutable);
				if (method_9799(blockState) && !Block.method_9501(blockState.method_11628(blockView, blockPos), direction.getOpposite())) {
					bl = true;
					break;
				}
			}
		}

		return bl;
	}

	private static boolean method_9799(BlockState blockState) {
		return blockState.method_11618().method_15767(FluidTags.field_15517);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return method_9798(iWorld, blockPos) ? this.field_10810 : super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}
}
