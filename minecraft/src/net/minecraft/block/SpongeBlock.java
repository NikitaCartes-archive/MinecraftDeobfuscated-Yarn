package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.class_3545;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SpongeBlock extends Block {
	protected SpongeBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.method_10620(world, blockPos);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		this.method_10620(world, blockPos);
		super.neighborUpdate(blockState, world, blockPos, block, blockPos2);
	}

	protected void method_10620(World world, BlockPos blockPos) {
		if (this.method_10619(world, blockPos)) {
			world.setBlockState(blockPos, Blocks.field_10562.getDefaultState(), 2);
			world.fireWorldEvent(2001, blockPos, Block.getRawIdFromState(Blocks.field_10382.getDefaultState()));
		}
	}

	private boolean method_10619(World world, BlockPos blockPos) {
		Queue<class_3545<BlockPos, Integer>> queue = Lists.<class_3545<BlockPos, Integer>>newLinkedList();
		queue.add(new class_3545<>(blockPos, 0));
		int i = 0;

		while (!queue.isEmpty()) {
			class_3545<BlockPos, Integer> lv = (class_3545<BlockPos, Integer>)queue.poll();
			BlockPos blockPos2 = lv.method_15442();
			int j = lv.method_15441();

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
							queue.add(new class_3545<>(blockPos3, j + 1));
						}
					} else if (blockState.getBlock() instanceof FluidBlock) {
						world.setBlockState(blockPos3, Blocks.field_10124.getDefaultState(), 3);
						i++;
						if (j < 6) {
							queue.add(new class_3545<>(blockPos3, j + 1));
						}
					} else if (material == Material.UNDERWATER_PLANT || material == Material.SEAGRASS) {
						BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
						dropStacks(blockState, world, blockPos3, blockEntity);
						world.setBlockState(blockPos3, Blocks.field_10124.getDefaultState(), 3);
						i++;
						if (j < 6) {
							queue.add(new class_3545<>(blockPos3, j + 1));
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
