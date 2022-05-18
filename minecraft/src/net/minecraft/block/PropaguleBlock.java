package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.sapling.MangroveSaplingGenerator;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class PropaguleBlock extends SaplingBlock implements Waterloggable {
	public static final IntProperty AGE = Properties.AGE_4;
	public static final int field_37589 = 4;
	private static final VoxelShape[] SHAPES = new VoxelShape[]{
		Block.createCuboidShape(7.0, 13.0, 7.0, 9.0, 16.0, 9.0),
		Block.createCuboidShape(7.0, 10.0, 7.0, 9.0, 16.0, 9.0),
		Block.createCuboidShape(7.0, 7.0, 7.0, 9.0, 16.0, 9.0),
		Block.createCuboidShape(7.0, 3.0, 7.0, 9.0, 16.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
	};
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final BooleanProperty HANGING = Properties.HANGING;
	private static final float field_38749 = 0.85F;

	public PropaguleBlock(AbstractBlock.Settings settings) {
		super(new MangroveSaplingGenerator(0.85F), settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(STAGE, Integer.valueOf(0))
				.with(AGE, Integer.valueOf(0))
				.with(WATERLOGGED, Boolean.valueOf(false))
				.with(HANGING, Boolean.valueOf(false))
		);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(STAGE).add(AGE).add(WATERLOGGED).add(HANGING);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return super.canPlantOnTop(floor, world, pos) || floor.isOf(Blocks.CLAY);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		boolean bl = fluidState.getFluid() == Fluids.WATER;
		return super.getPlacementState(ctx).with(WATERLOGGED, Boolean.valueOf(bl)).with(AGE, Integer.valueOf(4));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Vec3d vec3d = state.getModelOffset(world, pos);
		VoxelShape voxelShape;
		if (!(Boolean)state.get(HANGING)) {
			voxelShape = SHAPES[4];
		} else {
			voxelShape = SHAPES[state.get(AGE)];
		}

		return voxelShape.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return isHanging(state) ? world.getBlockState(pos.up()).isOf(Blocks.MANGROVE_LEAVES) : super.canPlaceAt(state, world, pos);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return direction == Direction.UP && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!isHanging(state)) {
			if (random.nextInt(7) == 0) {
				this.generate(world, pos, state, random);
			}
		} else {
			if (!isFullyGrown(state)) {
				world.setBlockState(pos, state.cycle(AGE), Block.NOTIFY_LISTENERS);
			}
		}
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return !isHanging(state) || !isFullyGrown(state);
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return isHanging(state) ? !isFullyGrown(state) : super.canGrow(world, random, pos, state);
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		if (isHanging(state) && !isFullyGrown(state)) {
			world.setBlockState(pos, state.cycle(AGE), Block.NOTIFY_LISTENERS);
		} else {
			super.grow(world, random, pos, state);
		}
	}

	private static boolean isHanging(BlockState state) {
		return (Boolean)state.get(HANGING);
	}

	private static boolean isFullyGrown(BlockState state) {
		return (Integer)state.get(AGE) == 4;
	}

	public static BlockState getDefaultHangingState() {
		return getHangingState(0);
	}

	public static BlockState getHangingState(int age) {
		return Blocks.MANGROVE_PROPAGULE.getDefaultState().with(HANGING, Boolean.valueOf(true)).with(AGE, Integer.valueOf(age));
	}
}
