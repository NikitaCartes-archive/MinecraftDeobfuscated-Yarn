package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class SpongeBlock extends Block {
	public static final int field_31250 = 6;
	public static final int field_31251 = 64;

	protected SpongeBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock())) {
			this.update(world, pos);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		this.update(world, pos);
		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
	}

	protected void update(World world, BlockPos pos) {
		if (this.absorbWater(world, pos)) {
			world.setBlockState(pos, Blocks.WET_SPONGE.getDefaultState(), Block.NOTIFY_LISTENERS);
			world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(Blocks.WATER.getDefaultState()));
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
				if (fluidState.isIn(FluidTags.WATER)) {
					if (blockState.getBlock() instanceof FluidDrainable && !((FluidDrainable)blockState.getBlock()).tryDrainFluid(world, blockPos2, blockState).isEmpty()) {
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos2, j + 1));
						}
					} else if (blockState.getBlock() instanceof FluidBlock) {
						world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
						i++;
						if (j < 6) {
							queue.add(new Pair<>(blockPos2, j + 1));
						}
					} else if (material == Material.UNDERWATER_PLANT || material == Material.REPLACEABLE_UNDERWATER_PLANT) {
						BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos2) : null;
						dropStacks(blockState, world, blockPos2, blockEntity);
						world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
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
