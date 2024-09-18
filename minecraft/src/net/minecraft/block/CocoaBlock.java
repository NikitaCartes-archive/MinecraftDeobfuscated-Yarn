package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class CocoaBlock extends HorizontalFacingBlock implements Fertilizable {
	public static final MapCodec<CocoaBlock> CODEC = createCodec(CocoaBlock::new);
	public static final int MAX_AGE = 2;
	public static final IntProperty AGE = Properties.AGE_2;
	protected static final int field_31062 = 4;
	protected static final int field_31063 = 5;
	protected static final int field_31064 = 2;
	protected static final int field_31065 = 6;
	protected static final int field_31066 = 7;
	protected static final int field_31067 = 3;
	protected static final int field_31068 = 8;
	protected static final int field_31069 = 9;
	protected static final int field_31070 = 4;
	protected static final VoxelShape[] AGE_TO_EAST_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(11.0, 7.0, 6.0, 15.0, 12.0, 10.0),
		Block.createCuboidShape(9.0, 5.0, 5.0, 15.0, 12.0, 11.0),
		Block.createCuboidShape(7.0, 3.0, 4.0, 15.0, 12.0, 12.0)
	};
	protected static final VoxelShape[] AGE_TO_WEST_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(1.0, 7.0, 6.0, 5.0, 12.0, 10.0),
		Block.createCuboidShape(1.0, 5.0, 5.0, 7.0, 12.0, 11.0),
		Block.createCuboidShape(1.0, 3.0, 4.0, 9.0, 12.0, 12.0)
	};
	protected static final VoxelShape[] AGE_TO_NORTH_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(6.0, 7.0, 1.0, 10.0, 12.0, 5.0),
		Block.createCuboidShape(5.0, 5.0, 1.0, 11.0, 12.0, 7.0),
		Block.createCuboidShape(4.0, 3.0, 1.0, 12.0, 12.0, 9.0)
	};
	protected static final VoxelShape[] AGE_TO_SOUTH_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(6.0, 7.0, 11.0, 10.0, 12.0, 15.0),
		Block.createCuboidShape(5.0, 5.0, 9.0, 11.0, 12.0, 15.0),
		Block.createCuboidShape(4.0, 3.0, 7.0, 12.0, 12.0, 15.0)
	};

	@Override
	public MapCodec<CocoaBlock> getCodec() {
		return CODEC;
	}

	public CocoaBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(AGE, Integer.valueOf(0)));
	}

	@Override
	protected boolean hasRandomTicks(BlockState state) {
		return (Integer)state.get(AGE) < 2;
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.random.nextInt(5) == 0) {
			int i = (Integer)state.get(AGE);
			if (i < 2) {
				world.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), Block.NOTIFY_LISTENERS);
			}
		}
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.offset(state.get(FACING)));
		return blockState.isIn(BlockTags.JUNGLE_LOGS);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		int i = (Integer)state.get(AGE);
		switch ((Direction)state.get(FACING)) {
			case SOUTH:
				return AGE_TO_SOUTH_SHAPE[i];
			case NORTH:
			default:
				return AGE_TO_NORTH_SHAPE[i];
			case WEST:
				return AGE_TO_WEST_SHAPE[i];
			case EAST:
				return AGE_TO_EAST_SHAPE[i];
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = this.getDefaultState();
		WorldView worldView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();

		for (Direction direction : ctx.getPlacementDirections()) {
			if (direction.getAxis().isHorizontal()) {
				blockState = blockState.with(FACING, direction);
				if (blockState.canPlaceAt(worldView, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		return direction == state.get(FACING) && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return (Integer)state.get(AGE) < 2;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(AGE, Integer.valueOf((Integer)state.get(AGE) + 1)), Block.NOTIFY_LISTENERS);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, AGE);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}
}
