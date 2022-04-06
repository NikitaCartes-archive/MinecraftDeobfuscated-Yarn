package net.minecraft.block;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
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
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LeavesBlock extends Block implements Waterloggable {
	public static final int MAX_DISTANCE = 7;
	public static final IntProperty DISTANCE = Properties.DISTANCE_1_7;
	public static final BooleanProperty PERSISTENT = Properties.PERSISTENT;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private static final int field_31112 = 1;

	public LeavesBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(DISTANCE, Integer.valueOf(7)).with(PERSISTENT, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false))
		);
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
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, AbstractRandom random) {
		if (this.shouldDecay(state)) {
			dropStacks(state, world, pos);
			world.removeBlock(pos, false);
		}
	}

	protected boolean shouldDecay(BlockState state) {
		return !(Boolean)state.get(PERSISTENT) && (Integer)state.get(DISTANCE) == 7;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, AbstractRandom random) {
		world.setBlockState(pos, updateDistanceFromLogs(state, world, pos), Block.NOTIFY_ALL);
	}

	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		return 1;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		int i = getDistanceFromLog(neighborState) + 1;
		if (i != 1 || (Integer)state.get(DISTANCE) != i) {
			world.createAndScheduleBlockTick(pos, this, 1);
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
		if (state.isIn(BlockTags.LOGS)) {
			return 0;
		} else {
			return state.getBlock() instanceof LeavesBlock ? (Integer)state.get(DISTANCE) : 7;
		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, AbstractRandom random) {
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
		builder.add(DISTANCE, PERSISTENT, WATERLOGGED);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		BlockState blockState = this.getDefaultState()
			.with(PERSISTENT, Boolean.valueOf(true))
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
		return updateDistanceFromLogs(blockState, ctx.getWorld(), ctx.getBlockPos());
	}
}
