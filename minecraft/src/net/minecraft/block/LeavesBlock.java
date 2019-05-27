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
	public static final BooleanProperty PERSISTENT = Properties.PERSISTENT;
	protected static boolean fancy;

	public LeavesBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11199, Integer.valueOf(7)).with(PERSISTENT, Boolean.valueOf(false)));
	}

	@Override
	public boolean hasRandomTicks(BlockState blockState) {
		return (Integer)blockState.get(field_11199) == 7 && !(Boolean)blockState.get(PERSISTENT);
	}

	@Override
	public void onRandomTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!(Boolean)blockState.get(PERSISTENT) && (Integer)blockState.get(field_11199) == 7) {
			dropStacks(blockState, world, blockPos);
			world.clearBlockState(blockPos, false);
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		world.setBlockState(blockPos, updateDistanceFromLogs(blockState, world, blockPos), 3);
	}

	@Override
	public int getLightSubtracted(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return 1;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		int i = getDistanceFromLog(blockState2) + 1;
		if (i != 1 || (Integer)blockState.get(field_11199) != i) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		return blockState;
	}

	private static BlockState updateDistanceFromLogs(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		int i = 7;

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.values()) {
				pooledMutable.method_10114(blockPos).method_10118(direction);
				i = Math.min(i, getDistanceFromLog(iWorld.getBlockState(pooledMutable)) + 1);
				if (i == 1) {
					break;
				}
			}
		}

		return blockState.with(field_11199, Integer.valueOf(i));
	}

	private static int getDistanceFromLog(BlockState blockState) {
		if (BlockTags.field_15475.contains(blockState.getBlock())) {
			return 0;
		} else {
			return blockState.getBlock() instanceof LeavesBlock ? (Integer)blockState.get(field_11199) : 7;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.hasRain(blockPos.up())) {
			if (random.nextInt(15) == 1) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState2 = world.getBlockState(blockPos2);
				if (!blockState2.isOpaque() || !Block.isSolidFullSquare(blockState2, world, blockPos2, Direction.field_11036)) {
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
	public boolean isOpaque(BlockState blockState) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return fancy ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.field_9178;
	}

	@Override
	public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return entityType == EntityType.field_6081 || entityType == EntityType.field_6104;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(field_11199, PERSISTENT);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return updateDistanceFromLogs(
			this.getDefaultState().with(PERSISTENT, Boolean.valueOf(true)), itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos()
		);
	}
}
