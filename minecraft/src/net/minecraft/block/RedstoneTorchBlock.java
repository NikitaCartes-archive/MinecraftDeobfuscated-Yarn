package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class RedstoneTorchBlock extends TorchBlock {
	public static final BooleanProperty LIT = Properties.LIT;
	private static final Map<BlockView, List<RedstoneTorchBlock.BurnoutEntry>> BURNOUT_MAP = new WeakHashMap();

	protected RedstoneTorchBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(LIT, Boolean.valueOf(true)));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 2;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		for (Direction direction : Direction.values()) {
			world.updateNeighborsAlways(blockPos.offset(direction), this);
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl) {
			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(blockPos.offset(direction), this);
			}
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(LIT) && Direction.field_11036 != direction ? 15 : 0;
	}

	protected boolean shouldUnpower(World world, BlockPos blockPos, BlockState blockState) {
		return world.isEmittingRedstonePower(blockPos.down(), Direction.field_11033);
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		update(blockState, world, blockPos, random, this.shouldUnpower(world, blockPos, blockState));
	}

	public static void update(BlockState blockState, World world, BlockPos blockPos, Random random, boolean bl) {
		List<RedstoneTorchBlock.BurnoutEntry> list = (List<RedstoneTorchBlock.BurnoutEntry>)BURNOUT_MAP.get(world);

		while (list != null && !list.isEmpty() && world.getTime() - ((RedstoneTorchBlock.BurnoutEntry)list.get(0)).time > 60L) {
			list.remove(0);
		}

		if ((Boolean)blockState.get(LIT)) {
			if (bl) {
				world.setBlockState(blockPos, blockState.with(LIT, Boolean.valueOf(false)), 3);
				if (isBurnedOut(world, blockPos, true)) {
					world.playLevelEvent(1502, blockPos, 0);
					world.getBlockTickScheduler().schedule(blockPos, world.getBlockState(blockPos).getBlock(), 160);
				}
			}
		} else if (!bl && !isBurnedOut(world, blockPos, false)) {
			world.setBlockState(blockPos, blockState.with(LIT, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if ((Boolean)blockState.get(LIT) == this.shouldUnpower(world, blockPos, blockState) && !world.getBlockTickScheduler().isTicking(blockPos, this)) {
			world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.field_11033 ? blockState.getWeakRedstonePower(blockView, blockPos, direction) : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(LIT)) {
			double d = (double)blockPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			double e = (double)blockPos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
			double f = (double)blockPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			world.addParticle(DustParticleEffect.RED, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public int getLuminance(BlockState blockState) {
		return blockState.get(LIT) ? super.getLuminance(blockState) : 0;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}

	private static boolean isBurnedOut(World world, BlockPos blockPos, boolean bl) {
		List<RedstoneTorchBlock.BurnoutEntry> list = (List<RedstoneTorchBlock.BurnoutEntry>)BURNOUT_MAP.computeIfAbsent(world, blockView -> Lists.newArrayList());
		if (bl) {
			list.add(new RedstoneTorchBlock.BurnoutEntry(blockPos.toImmutable(), world.getTime()));
		}

		int i = 0;

		for (int j = 0; j < list.size(); j++) {
			RedstoneTorchBlock.BurnoutEntry burnoutEntry = (RedstoneTorchBlock.BurnoutEntry)list.get(j);
			if (burnoutEntry.pos.equals(blockPos)) {
				if (++i >= 8) {
					return true;
				}
			}
		}

		return false;
	}

	public static class BurnoutEntry {
		private final BlockPos pos;
		private final long time;

		public BurnoutEntry(BlockPos blockPos, long l) {
			this.pos = blockPos;
			this.time = l;
		}
	}
}
