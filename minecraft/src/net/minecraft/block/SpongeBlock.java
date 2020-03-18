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
	protected SpongeBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (oldState.getBlock() != state.getBlock()) {
			this.update(world, pos);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		this.update(world, pos);
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
	}

	protected void update(World world, BlockPos pos) {
		if (this.absorbWater(world, pos)) {
			world.setBlockState(pos, Blocks.WET_SPONGE.getDefaultState(), 2);
			world.playLevelEvent(2001, pos, Block.getRawIdFromState(Blocks.WATER.getDefaultState()));
		}
	}

	private boolean absorbWater(World world, BlockPos pos) {
		Queue<Pair<BlockPos, Integer>> queue = Lists.<Pair<BlockPos, Integer>>newLinkedList();
		queue.add(new Pair<>(pos, 0));
		int i = 0;

		while (!queue.isEmpty()) {
			Pair<BlockPos, Integer> pair = (Pair<BlockPos, Integer>)queue.poll();
			BlockPos blockPos = pair.getLeft();
			int j = pair.getRight();

			for (Direction direction : Direction.values()) {
				BlockPos blockPos2 = blockPos.offset(direction);
				BlockState blockState = world.getBlockState(blockPos2);
				FluidState fluidState = world.getFluidState(blockPos2);
				Material material = blockState.getMaterial();
				if (fluidState.matches(FluidTags.WATER)) {
					if (blockState.getBlock() instanceof FluidDrainable && ((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, blockPos2, blockState) != Fluids.EMPTY
						)
					 {
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos2, j + 1));
						}
					} else if (blockState.getBlock() instanceof FluidBlock) {
						world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos2, j + 1));
						}
					} else if (material == Material.UNDERWATER_PLANT || material == Material.SEAGRASS) {
						BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos2) : null;
						dropStacks(blockState, world, blockPos2, blockEntity);
						world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 3);
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos2, j + 1));
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
