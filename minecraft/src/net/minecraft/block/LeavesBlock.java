package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LeavesBlock extends Block {
	public static final IntProperty field_11199 = Properties.field_12541;
	public static final BooleanProperty field_11200 = Properties.field_12514;
	protected static boolean fancy;

	public LeavesBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11199, Integer.valueOf(7)).method_11657(field_11200, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9542(BlockState blockState) {
		return (Integer)blockState.method_11654(field_11199) == 7 && !(Boolean)blockState.method_11654(field_11200);
	}

	@Override
	public void method_9514(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!(Boolean)blockState.method_11654(field_11200) && (Integer)blockState.method_11654(field_11199) == 7) {
			method_9497(blockState, world, blockPos);
			world.clearBlockState(blockPos, false);
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		world.method_8652(blockPos, method_10300(blockState, world, blockPos), 3);
	}

	@Override
	public int method_9505(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return 1;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		int i = method_10302(blockState2) + 1;
		if (i != 1 || (Integer)blockState.method_11654(field_11199) != i) {
			iWorld.method_8397().schedule(blockPos, this, 1);
		}

		return blockState;
	}

	private static BlockState method_10300(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		int i = 7;

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.values()) {
				pooledMutable.method_10114(blockPos).method_10118(direction);
				i = Math.min(i, method_10302(iWorld.method_8320(pooledMutable)) + 1);
				if (i == 1) {
					break;
				}
			}
		}

		return blockState.method_11657(field_11199, Integer.valueOf(i));
	}

	private static int method_10302(BlockState blockState) {
		if (BlockTags.field_15475.contains(blockState.getBlock())) {
			return 0;
		} else {
			return blockState.getBlock() instanceof LeavesBlock ? (Integer)blockState.method_11654(field_11199) : 7;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.hasRain(blockPos.up())) {
			if (random.nextInt(15) == 1) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState2 = world.method_8320(blockPos2);
				if (!blockState2.isOpaque() || !Block.method_20045(blockState2, world, blockPos2, Direction.field_11036)) {
					double d = (double)((float)blockPos.getX() + random.nextFloat());
					double e = (double)blockPos.getY() - 0.05;
					double f = (double)((float)blockPos.getZ() + random.nextFloat());
					world.addParticle(ParticleTypes.field_11232, d, e, f, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static void setRenderingMode(boolean bl) {
		fancy = bl;
	}

	@Override
	public boolean method_9601(BlockState blockState) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return fancy ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.field_9178;
	}

	@Override
	public boolean method_16362(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean method_9523(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return entityType == EntityType.field_6081 || entityType == EntityType.field_6104;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11199, field_11200);
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return method_10300(
			this.method_9564().method_11657(field_11200, Boolean.valueOf(true)), itemPlacementContext.method_8045(), itemPlacementContext.getBlockPos()
		);
	}
}
