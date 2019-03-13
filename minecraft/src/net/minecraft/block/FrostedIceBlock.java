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
	public static final IntegerProperty field_11097 = Properties.field_12497;

	public FrostedIceBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11097, Integer.valueOf(0)));
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((random.nextInt(3) == 0 || this.method_10202(world, blockPos, 4))
			&& world.method_8602(blockPos) > 11 - (Integer)blockState.method_11654(field_11097) - blockState.method_11581(world, blockPos)
			&& this.method_10201(blockState, world, blockPos)) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (Direction direction : Direction.values()) {
					pooledMutable.method_10114(blockPos).method_10118(direction);
					BlockState blockState2 = world.method_8320(pooledMutable);
					if (blockState2.getBlock() == this && !this.method_10201(blockState2, world, pooledMutable)) {
						world.method_8397().method_8676(pooledMutable, this, MathHelper.nextInt(random, 20, 40));
					}
				}
			}
		} else {
			world.method_8397().method_8676(blockPos, this, MathHelper.nextInt(random, 20, 40));
		}
	}

	private boolean method_10201(BlockState blockState, World world, BlockPos blockPos) {
		int i = (Integer)blockState.method_11654(field_11097);
		if (i < 3) {
			world.method_8652(blockPos, blockState.method_11657(field_11097, Integer.valueOf(i + 1)), 2);
			return false;
		} else {
			this.method_10275(blockState, world, blockPos);
			return true;
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (block == this && this.method_10202(world, blockPos, 2)) {
			this.method_10275(blockState, world, blockPos);
		}

		super.method_9612(blockState, world, blockPos, block, blockPos2);
	}

	private boolean method_10202(BlockView blockView, BlockPos blockPos, int i) {
		int j = 0;

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.values()) {
				pooledMutable.method_10114(blockPos).method_10118(direction);
				if (blockView.method_8320(pooledMutable).getBlock() == this) {
					if (++j >= i) {
						return false;
					}
				}
			}

			return true;
		}
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11097);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}
}
