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
	public static final IntegerProperty field_11097 = Properties.AGE_3;

	public FrostedIceBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11097, Integer.valueOf(0)));
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((random.nextInt(3) == 0 || this.method_10202(world, blockPos, 4))
			&& world.method_8602(blockPos) > 11 - (Integer)blockState.get(field_11097) - blockState.getLightSubtracted(world, blockPos)
			&& this.method_10201(blockState, world, blockPos)) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (Direction direction : Direction.values()) {
					pooledMutable.set(blockPos).setOffset(direction);
					BlockState blockState2 = world.getBlockState(pooledMutable);
					if (blockState2.getBlock() == this && !this.method_10201(blockState2, world, pooledMutable)) {
						world.getBlockTickScheduler().schedule(pooledMutable, this, MathHelper.nextInt(random, 20, 40));
					}
				}
			}
		} else {
			world.getBlockTickScheduler().schedule(blockPos, this, MathHelper.nextInt(random, 20, 40));
		}
	}

	private boolean method_10201(BlockState blockState, World world, BlockPos blockPos) {
		int i = (Integer)blockState.get(field_11097);
		if (i < 3) {
			world.setBlockState(blockPos, blockState.with(field_11097, Integer.valueOf(i + 1)), 2);
			return false;
		} else {
			this.method_10275(blockState, world, blockPos);
			return true;
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (block == this && this.method_10202(world, blockPos, 2)) {
			this.method_10275(blockState, world, blockPos);
		}

		super.neighborUpdate(blockState, world, blockPos, block, blockPos2);
	}

	private boolean method_10202(BlockView blockView, BlockPos blockPos, int i) {
		int j = 0;

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.values()) {
				pooledMutable.set(blockPos).setOffset(direction);
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
		builder.with(field_11097);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}
}
