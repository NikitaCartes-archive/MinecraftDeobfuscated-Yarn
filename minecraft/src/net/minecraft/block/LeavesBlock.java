package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
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
	public static final IntProperty DISTANCE = Properties.DISTANCE_1_7;
	public static final BooleanProperty PERSISTENT = Properties.PERSISTENT;

	public LeavesBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(DISTANCE, Integer.valueOf(7)).with(PERSISTENT, Boolean.valueOf(false)));
	}

	@Override
	public boolean hasRandomTicks(BlockState blockState) {
		return (Integer)blockState.get(DISTANCE) == 7 && !(Boolean)blockState.get(PERSISTENT);
	}

	@Override
	public void randomTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (!(Boolean)blockState.get(PERSISTENT) && (Integer)blockState.get(DISTANCE) == 7) {
			dropStacks(blockState, serverWorld, blockPos);
			serverWorld.removeBlock(blockPos, false);
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		serverWorld.setBlockState(blockPos, updateDistanceFromLogs(blockState, serverWorld, blockPos), 3);
	}

	@Override
	public int getOpacity(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return 1;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		int i = getDistanceFromLog(blockState2) + 1;
		if (i != 1 || (Integer)blockState.get(DISTANCE) != i) {
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

		return blockState.with(DISTANCE, Integer.valueOf(i));
	}

	private static int getDistanceFromLog(BlockState blockState) {
		if (BlockTags.LOGS.contains(blockState.getBlock())) {
			return 0;
		} else {
			return blockState.getBlock() instanceof LeavesBlock ? (Integer)blockState.get(DISTANCE) : 7;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.hasRain(blockPos.up())) {
			if (random.nextInt(15) == 1) {
				BlockPos blockPos2 = blockPos.method_10074();
				BlockState blockState2 = world.getBlockState(blockPos2);
				if (!blockState2.isOpaque() || !blockState2.isSideSolidFullSquare(world, blockPos2, Direction.UP)) {
					double d = (double)((float)blockPos.getX() + random.nextFloat());
					double e = (double)blockPos.getY() - 0.05;
					double f = (double)((float)blockPos.getZ() + random.nextFloat());
					world.addParticle(ParticleTypes.DRIPPING_WATER, d, e, f, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	@Override
	public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return entityType == EntityType.OCELOT || entityType == EntityType.PARROT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(DISTANCE, PERSISTENT);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return updateDistanceFromLogs(
			this.getDefaultState().with(PERSISTENT, Boolean.valueOf(true)), itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos()
		);
	}
}
