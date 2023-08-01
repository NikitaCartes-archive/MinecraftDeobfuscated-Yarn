package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SpongeBlock extends Block {
	public static final int field_31250 = 6;
	public static final int field_31251 = 64;
	private static final Direction[] field_43257 = Direction.values();

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
			world.playSound(null, pos, SoundEvents.BLOCK_SPONGE_ABSORB, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	private boolean absorbWater(World world, BlockPos pos) {
		return BlockPos.iterateRecursively(pos, 6, 65, (currentPos, queuer) -> {
			for (Direction direction : field_43257) {
				queuer.accept(currentPos.offset(direction));
			}
		}, currentPos -> {
			if (currentPos.equals(pos)) {
				return true;
			} else {
				BlockState blockState = world.getBlockState(currentPos);
				FluidState fluidState = world.getFluidState(currentPos);
				if (!fluidState.isIn(FluidTags.WATER)) {
					return false;
				} else {
					if (blockState.getBlock() instanceof FluidDrainable fluidDrainable && !fluidDrainable.tryDrainFluid(null, world, currentPos, blockState).isEmpty()) {
						return true;
					}

					if (blockState.getBlock() instanceof FluidBlock) {
						world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
					} else {
						if (!blockState.isOf(Blocks.KELP) && !blockState.isOf(Blocks.KELP_PLANT) && !blockState.isOf(Blocks.SEAGRASS) && !blockState.isOf(Blocks.TALL_SEAGRASS)) {
							return false;
						}

						BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(currentPos) : null;
						dropStacks(blockState, world, currentPos, blockEntity);
						world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
					}

					return true;
				}
			}
		}) > 1;
	}
}
