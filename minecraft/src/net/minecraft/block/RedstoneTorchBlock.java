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
	public static final BooleanProperty field_11446 = Properties.field_12548;
	private static final Map<BlockView, List<RedstoneTorchBlock.class_2460>> field_11445 = Maps.<BlockView, List<RedstoneTorchBlock.class_2460>>newHashMap();

	protected RedstoneTorchBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11446, Boolean.valueOf(true)));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 2;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		for (Direction direction : Direction.values()) {
			world.method_8452(blockPos.method_10093(direction), this);
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl) {
			for (Direction direction : Direction.values()) {
				world.method_8452(blockPos.method_10093(direction), this);
			}
		}
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_11446) && Direction.UP != direction ? 15 : 0;
	}

	protected boolean method_10488(World world, BlockPos blockPos, BlockState blockState) {
		return world.method_8459(blockPos.down(), Direction.DOWN);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		method_10490(blockState, world, blockPos, random, this.method_10488(world, blockPos, blockState));
	}

	public static void method_10490(BlockState blockState, World world, BlockPos blockPos, Random random, boolean bl) {
		List<RedstoneTorchBlock.class_2460> list = (List<RedstoneTorchBlock.class_2460>)field_11445.get(world);

		while (list != null && !list.isEmpty() && world.getTime() - ((RedstoneTorchBlock.class_2460)list.get(0)).field_11447 > 60L) {
			list.remove(0);
		}

		if ((Boolean)blockState.method_11654(field_11446)) {
			if (bl) {
				world.method_8652(blockPos, blockState.method_11657(field_11446, Boolean.valueOf(false)), 3);
				if (method_10489(world, blockPos, true)) {
					world.method_8396(
						null, blockPos, SoundEvents.field_14909, SoundCategory.field_15245, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
					);

					for (int i = 0; i < 5; i++) {
						double d = (double)blockPos.getX() + random.nextDouble() * 0.6 + 0.2;
						double e = (double)blockPos.getY() + random.nextDouble() * 0.6 + 0.2;
						double f = (double)blockPos.getZ() + random.nextDouble() * 0.6 + 0.2;
						world.method_8406(ParticleTypes.field_11251, d, e, f, 0.0, 0.0, 0.0);
					}

					world.method_8397().method_8676(blockPos, world.method_8320(blockPos).getBlock(), 160);
				}
			}
		} else if (!bl && !method_10489(world, blockPos, false)) {
			world.method_8652(blockPos, blockState.method_11657(field_11446, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_11446) == this.method_10488(world, blockPos, blockState) && !world.method_8397().method_8677(blockPos, this)) {
			world.method_8397().method_8676(blockPos, this, this.getTickRate(world));
		}
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.DOWN ? blockState.method_11597(blockView, blockPos, direction) : 0;
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
			world.method_8406(DustParticleParameters.RED, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public int method_9593(BlockState blockState) {
		return blockState.method_11654(field_11446) ? super.method_9593(blockState) : 0;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11446);
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
