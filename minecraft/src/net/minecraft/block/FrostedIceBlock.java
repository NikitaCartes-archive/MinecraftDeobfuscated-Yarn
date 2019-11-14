package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FrostedIceBlock extends IceBlock {
	public static final IntProperty AGE = Properties.AGE_3;

	public FrostedIceBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((random.nextInt(3) == 0 || this.canMelt(world, pos, 4))
			&& world.getLightLevel(pos) > 11 - (Integer)state.get(AGE) - state.getOpacity(world, pos)
			&& this.increaseAge(state, world, pos)) {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (Direction direction : Direction.values()) {
					pooledMutable.method_10114(pos).method_10118(direction);
					BlockState blockState = world.getBlockState(pooledMutable);
					if (blockState.getBlock() == this && !this.increaseAge(blockState, world, pooledMutable)) {
						world.method_14196().schedule(pooledMutable, this, MathHelper.nextInt(random, 20, 40));
					}
				}
			}
		} else {
			world.method_14196().schedule(pos, this, MathHelper.nextInt(random, 20, 40));
		}
	}

	private boolean increaseAge(BlockState state, World world, BlockPos pos) {
		int i = (Integer)state.get(AGE);
		if (i < 3) {
			world.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), 2);
			return false;
		} else {
			this.melt(state, world, pos);
			return true;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (block == this && this.canMelt(world, pos, 2)) {
			this.melt(state, world, pos);
		}

		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
	}

	private boolean canMelt(BlockView world, BlockPos pos, int maxNeighbors) {
		int i = 0;

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.values()) {
				pooledMutable.method_10114(pos).method_10118(direction);
				if (world.getBlockState(pooledMutable).getBlock() == this) {
					if (++i >= maxNeighbors) {
						return false;
					}
				}
			}

			return true;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
}
