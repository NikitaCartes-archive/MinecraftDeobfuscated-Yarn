package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FrostedIceBlock extends IceBlock {
	public static final IntegerProperty AGE = Properties.AGE_3;

	public FrostedIceBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((random.nextInt(3) == 0 || this.canMelt(world, blockPos, 4))
			&& world.getLightLevel(blockPos) > 11 - (Integer)blockState.get(AGE) - blockState.getLightSubtracted(world, blockPos)
			&& this.increaseAge(blockState, world, blockPos)) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (Direction direction : Direction.values()) {
					pooledMutable.method_10114(blockPos).method_10118(direction);
					BlockState blockState2 = world.getBlockState(pooledMutable);
					if (blockState2.getBlock() == this && !this.increaseAge(blockState2, world, pooledMutable)) {
						world.getBlockTickScheduler().schedule(pooledMutable, this, MathHelper.nextInt(random, 20, 40));
					}
				}
			}
		} else {
			world.getBlockTickScheduler().schedule(blockPos, this, MathHelper.nextInt(random, 20, 40));
		}
	}

	private boolean increaseAge(BlockState blockState, World world, BlockPos blockPos) {
		int i = (Integer)blockState.get(AGE);
		if (i < 3) {
			world.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(i + 1)), 2);
			return false;
		} else {
			this.melt(blockState, world, blockPos);
			return true;
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (block == this && this.canMelt(world, blockPos, 2)) {
			this.melt(blockState, world, blockPos);
		}

		super.neighborUpdate(blockState, world, blockPos, block, blockPos2, bl);
	}

	private boolean canMelt(BlockView blockView, BlockPos blockPos, int i) {
		int j = 0;

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.values()) {
				pooledMutable.method_10114(blockPos).method_10118(direction);
				if (blockView.getBlockState(pooledMutable).getBlock() == this) {
					if (++j >= i) {
						return false;
					}
				}
			}

			return true;
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(AGE);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}
}
