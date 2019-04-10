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
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.update(world, blockPos);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		this.update(world, blockPos);
		super.neighborUpdate(blockState, world, blockPos, block, blockPos2, bl);
	}

	protected void update(World world, BlockPos blockPos) {
		if (this.absorbWater(world, blockPos)) {
			world.setBlockState(blockPos, Blocks.field_10562.getDefaultState(), 2);
			world.method_20290(2001, blockPos, Block.getRawIdFromState(Blocks.field_10382.getDefaultState()));
		}
	}

	private boolean absorbWater(World world, BlockPos blockPos) {
		Queue<Pair<BlockPos, Integer>> queue = Lists.<Pair<BlockPos, Integer>>newLinkedList();
		queue.add(new Pair<>(blockPos, 0));
		int i = 0;

		while (!queue.isEmpty()) {
			Pair<BlockPos, Integer> pair = (Pair<BlockPos, Integer>)queue.poll();
			BlockPos blockPos2 = pair.getLeft();
			int j = pair.getRight();

			for (Direction direction : Direction.values()) {
				BlockPos blockPos3 = blockPos2.offset(direction);
				BlockState blockState = world.getBlockState(blockPos3);
				FluidState fluidState = world.getFluidState(blockPos3);
				Material material = blockState.getMaterial();
				if (fluidState.matches(FluidTags.field_15517)) {
					if (blockState.getBlock() instanceof FluidDrainable && ((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, blockPos3, blockState) != Fluids.EMPTY
						)
					 {
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos3, j + 1));
						}
					} else if (blockState.getBlock() instanceof FluidBlock) {
						world.setBlockState(blockPos3, Blocks.field_10124.getDefaultState(), 3);
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos3, j + 1));
						}
					} else if (material == Material.UNDERWATER_PLANT || material == Material.SEAGRASS) {
						BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
						dropStacks(blockState, world, blockPos3, blockEntity);
						world.setBlockState(blockPos3, Blocks.field_10124.getDefaultState(), 3);
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
