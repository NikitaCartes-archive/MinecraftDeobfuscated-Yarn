package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SpongeBlock extends Block {
	protected SpongeBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.method_10620(world, blockPos);
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		this.method_10620(world, blockPos);
		super.method_9612(blockState, world, blockPos, block, blockPos2);
	}

	protected void method_10620(World world, BlockPos blockPos) {
		if (this.method_10619(world, blockPos)) {
			world.method_8652(blockPos, Blocks.field_10562.method_9564(), 2);
			world.method_8535(2001, blockPos, Block.method_9507(Blocks.field_10382.method_9564()));
		}
	}

	private boolean method_10619(World world, BlockPos blockPos) {
		Queue<Pair<BlockPos, Integer>> queue = Lists.<Pair<BlockPos, Integer>>newLinkedList();
		queue.add(new Pair<>(blockPos, 0));
		int i = 0;

		while (!queue.isEmpty()) {
			Pair<BlockPos, Integer> pair = (Pair<BlockPos, Integer>)queue.poll();
			BlockPos blockPos2 = pair.getLeft();
			int j = pair.getRight();

			for (Direction direction : Direction.values()) {
				BlockPos blockPos3 = blockPos2.method_10093(direction);
				BlockState blockState = world.method_8320(blockPos3);
				FluidState fluidState = world.method_8316(blockPos3);
				Material material = blockState.method_11620();
				if (fluidState.method_15767(FluidTags.field_15517)) {
					if (blockState.getBlock() instanceof FluidDrainable && ((FluidDrainable)blockState.getBlock()).method_9700(world, blockPos3, blockState) != Fluids.EMPTY) {
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos3, j + 1));
						}
					} else if (blockState.getBlock() instanceof FluidBlock) {
						world.method_8652(blockPos3, Blocks.field_10124.method_9564(), 3);
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos3, j + 1));
						}
					} else if (material == Material.UNDERWATER_PLANT || material == Material.SEAGRASS) {
						BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? world.method_8321(blockPos3) : null;
						method_9610(blockState, world, blockPos3, blockEntity);
						world.method_8652(blockPos3, Blocks.field_10124.method_9564(), 3);
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos3, j + 1));
						}
					}
				}
			}

			if (i > 64) {
				break;
			}
		}

		return i > 0;
	}
}
