package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
	private static final Map<BlockView, List<RedstoneTorchBlock.class_2460>> field_11445 = Maps.<BlockView, List<RedstoneTorchBlock.class_2460>>newHashMap();

	protected RedstoneTorchBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(LIT, Boolean.valueOf(true)));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 2;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
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
		return blockState.get(LIT) && Direction.UP != direction ? 15 : 0;
	}

	protected boolean method_10488(World world, BlockPos blockPos, BlockState blockState) {
		return world.isEmittingRedstonePower(blockPos.down(), Direction.DOWN);
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		method_10490(blockState, world, blockPos, random, this.method_10488(world, blockPos, blockState));
	}

	public static void method_10490(BlockState blockState, World world, BlockPos blockPos, Random random, boolean bl) {
		List<RedstoneTorchBlock.class_2460> list = (List<RedstoneTorchBlock.class_2460>)field_11445.get(world);

		while (list != null && !list.isEmpty() && world.getTime() - ((RedstoneTorchBlock.class_2460)list.get(0)).field_11447 > 60L) {
			list.remove(0);
		}

		if ((Boolean)blockState.get(LIT)) {
			if (bl) {
				world.setBlockState(blockPos, blockState.with(LIT, Boolean.valueOf(false)), 3);
				if (method_10489(world, blockPos, true)) {
					world.playSound(
						null, blockPos, SoundEvents.field_14909, SoundCategory.field_15245, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int i = 0; i < 5; i++) {
						double d = (double)blockPos.getX() + random.nextDouble() * 0.6 + 0.2;
						double e = (double)blockPos.getY() + random.nextDouble() * 0.6 + 0.2;
						double f = (double)blockPos.getZ() + random.nextDouble() * 0.6 + 0.2;
						world.addParticle(ParticleTypes.field_11251, d, e, f, 0.0, 0.0, 0.0);
					}

					world.getBlockTickScheduler().schedule(blockPos, world.getBlockState(blockPos).getBlock(), 160);
				}
			}
		} else if (!bl && !method_10489(world, blockPos, false)) {
			world.setBlockState(blockPos, blockState.with(LIT, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if ((Boolean)blockState.get(LIT) == this.method_10488(world, blockPos, blockState) && !world.getBlockTickScheduler().isTicking(blockPos, this)) {
			world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.DOWN ? blockState.getWeakRedstonePower(blockView, blockPos, direction) : 0;
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
			world.addParticle(DustParticleParameters.RED, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public int getLuminance(BlockState blockState) {
		return blockState.get(LIT) ? super.getLuminance(blockState) : 0;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(LIT);
	}

	private static boolean method_10489(World world, BlockPos blockPos, boolean bl) {
		List<RedstoneTorchBlock.class_2460> list = (List<RedstoneTorchBlock.class_2460>)field_11445.get(world);
		if (list == null) {
			list = Lists.<RedstoneTorchBlock.class_2460>newArrayList();
			field_11445.put(world, list);
		}

		if (bl) {
			list.add(new RedstoneTorchBlock.class_2460(blockPos.toImmutable(), world.getTime()));
		}

		int i = 0;

		for (int j = 0; j < list.size(); j++) {
			RedstoneTorchBlock.class_2460 lv = (RedstoneTorchBlock.class_2460)list.get(j);
			if (lv.field_11448.equals(blockPos)) {
				if (++i >= 8) {
					return true;
				}
			}
		}

		return false;
	}

	public static class class_2460 {
		private final BlockPos field_11448;
		private final long field_11447;

		public class_2460(BlockPos blockPos, long l) {
			this.field_11448 = blockPos;
			this.field_11447 = l;
		}
	}
}
