package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LeavesBlock extends Block {
	public static final IntProperty DISTANCE = Properties.DISTANCE_1_7;
	public static final BooleanProperty PERSISTENT = Properties.PERSISTENT;

	public LeavesBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(DISTANCE, Integer.valueOf(7)).with(PERSISTENT, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return (Integer)state.get(DISTANCE) == 7 && !(Boolean)state.get(PERSISTENT);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!(Boolean)state.get(PERSISTENT) && (Integer)state.get(DISTANCE) == 7) {
			dropStacks(state, world, pos);
			world.removeBlock(pos, false);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.setBlockState(pos, updateDistanceFromLogs(state, world, pos), 3);
	}

	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		return 1;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		int i = getDistanceFromLog(neighborState) + 1;
		if (i != 1 || (Integer)state.get(DISTANCE) != i) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		return state;
	}

	private static BlockState updateDistanceFromLogs(BlockState state, WorldAccess world, BlockPos pos) {
		int i = 7;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : Direction.values()) {
			mutable.set(pos, direction);
			i = Math.min(i, getDistanceFromLog(world.getBlockState(mutable)) + 1);
			if (i == 1) {
				break;
			}
		}

		return state.with(DISTANCE, Integer.valueOf(i));
	}

	private static int getDistanceFromLog(BlockState state) {
		if (BlockTags.LOGS.contains(state.getBlock())) {
			return 0;
		} else {
			return state.getBlock() instanceof LeavesBlock ? (Integer)state.get(DISTANCE) : 7;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.hasRain(pos.up())) {
			if (random.nextInt(15) == 1) {
				BlockPos blockPos = pos.down();
				BlockState blockState = world.getBlockState(blockPos);
				if (!blockState.isOpaque() || !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
					double d = (double)pos.getX() + random.nextDouble();
					double e = (double)pos.getY() - 0.05;
					double f = (double)pos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.DRIPPING_WATER, d, e, f, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(DISTANCE, PERSISTENT);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return updateDistanceFromLogs(this.getDefaultState().with(PERSISTENT, Boolean.valueOf(true)), ctx.getWorld(), ctx.getBlockPos());
	}
}
