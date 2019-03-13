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
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!this.method_9808(world, blockPos)) {
			world.method_8652(blockPos, this.deadCoralBlock.method_9564(), 2);
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (!this.method_9808(iWorld, blockPos)) {
			iWorld.method_8397().method_8676(blockPos, this, 60 + iWorld.getRandom().nextInt(40));
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	protected boolean method_9808(BlockView blockView, BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			FluidState fluidState = blockView.method_8316(blockPos.method_10093(direction));
			if (fluidState.method_15767(FluidTags.field_15517)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		if (!this.method_9808(itemPlacementContext.method_8045(), itemPlacementContext.method_8037())) {
			itemPlacementContext.method_8045()
				.method_8397()
				.method_8676(itemPlacementContext.method_8037(), this, 60 + itemPlacementContext.method_8045().getRandom().nextInt(40));
		}

		return this.method_9564();
	}
}
