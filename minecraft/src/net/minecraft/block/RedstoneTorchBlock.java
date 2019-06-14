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
	public static final BooleanProperty field_11446 = Properties.field_12548;
	private static final Map<BlockView, List<RedstoneTorchBlock.BurnoutEntry>> BURNOUT_MAP = new WeakHashMap();

	protected RedstoneTorchBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11446, Boolean.valueOf(true)));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 2;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		for (Direction direction : Direction.values()) {
			world.method_8452(blockPos.offset(direction), this);
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl) {
			for (Direction direction : Direction.values()) {
				world.method_8452(blockPos.offset(direction), this);
			}
		}
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_11446) && Direction.field_11036 != direction ? 15 : 0;
	}

	protected boolean method_10488(World world, BlockPos blockPos, BlockState blockState) {
		return world.isEmittingRedstonePower(blockPos.down(), Direction.field_11033);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		method_10490(blockState, world, blockPos, random, this.method_10488(world, blockPos, blockState));
	}

	public static void method_10490(BlockState blockState, World world, BlockPos blockPos, Random random, boolean bl) {
		List<RedstoneTorchBlock.BurnoutEntry> list = (List<RedstoneTorchBlock.BurnoutEntry>)BURNOUT_MAP.get(world);

		while (list != null && !list.isEmpty() && world.getTime() - ((RedstoneTorchBlock.BurnoutEntry)list.get(0)).time > 60L) {
			list.remove(0);
		}

		if ((Boolean)blockState.method_11654(field_11446)) {
			if (bl) {
				world.method_8652(blockPos, blockState.method_11657(field_11446, Boolean.valueOf(false)), 3);
				if (isBurnedOut(world, blockPos, true)) {
					world.playLevelEvent(1502, blockPos, 0);
					world.method_8397().schedule(blockPos, world.method_8320(blockPos).getBlock(), 160);
				}
			}
		} else if (!bl && !isBurnedOut(world, blockPos, false)) {
			world.method_8652(blockPos, blockState.method_11657(field_11446, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if ((Boolean)blockState.method_11654(field_11446) == this.method_10488(world, blockPos, blockState) && !world.method_8397().isTicking(blockPos, this)) {
			world.method_8397().schedule(blockPos, this, this.getTickRate(world));
		}
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.field_11033 ? blockState.getWeakRedstonePower(blockView, blockPos, direction) : 0;
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.method_11654(field_11446)) {
			double d = (double)blockPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			double e = (double)blockPos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
			double f = (double)blockPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
			world.addParticle(DustParticleEffect.RED, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public int method_9593(BlockState blockState) {
		return blockState.method_11654(field_11446) ? super.method_9593(blockState) : 0;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11446);
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
