package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class FrostedIceBlock extends IceBlock {
	public static final MapCodec<FrostedIceBlock> CODEC = createCodec(FrostedIceBlock::new);
	public static final int MAX_AGE = 3;
	public static final IntProperty AGE = Properties.AGE_3;
	private static final int NEIGHBORS_CHECKED_ON_SCHEDULED_TICK = 4;
	private static final int NEIGHBORS_CHECKED_ON_NEIGHBOR_UPDATE = 2;

	@Override
	public MapCodec<FrostedIceBlock> getCodec() {
		return CODEC;
	}

	public FrostedIceBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		world.scheduleBlockTick(pos, this, MathHelper.nextInt(world.getRandom(), 60, 120));
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((random.nextInt(3) == 0 || this.canMelt(world, pos, 4))
			&& world.getLightLevel(pos) > 11 - (Integer)state.get(AGE) - state.getOpacity(world, pos)
			&& this.increaseAge(state, world, pos)) {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (Direction direction : Direction.values()) {
				mutable.set(pos, direction);
				BlockState blockState = world.getBlockState(mutable);
				if (blockState.isOf(this) && !this.increaseAge(blockState, world, mutable)) {
					world.scheduleBlockTick(mutable, this, MathHelper.nextInt(random, 20, 40));
				}
			}
		} else {
			world.scheduleBlockTick(pos, this, MathHelper.nextInt(random, 20, 40));
		}
	}

	private boolean increaseAge(BlockState state, World world, BlockPos pos) {
		int i = (Integer)state.get(AGE);
		if (i < 3) {
			world.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), Block.NOTIFY_LISTENERS);
			return false;
		} else {
			this.melt(state, world, pos);
			return true;
		}
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (sourceBlock.getDefaultState().isOf(this) && this.canMelt(world, pos, 2)) {
			this.melt(state, world, pos);
		}

		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
	}

	private boolean canMelt(BlockView world, BlockPos pos, int maxNeighbors) {
		int i = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : Direction.values()) {
			mutable.set(pos, direction);
			if (world.getBlockState(mutable).isOf(this)) {
				if (++i >= maxNeighbors) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
}
