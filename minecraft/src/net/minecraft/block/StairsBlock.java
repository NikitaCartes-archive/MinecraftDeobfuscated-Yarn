package net.minecraft.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4538;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class StairsBlock extends Block implements Waterloggable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
	public static final EnumProperty<StairShape> SHAPE = Properties.STAIR_SHAPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape TOP_SHAPE = SlabBlock.TOP_SHAPE;
	protected static final VoxelShape BOTTOM_SHAPE = SlabBlock.BOTTOM_SHAPE;
	protected static final VoxelShape BOTTOM_NORTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
	protected static final VoxelShape BOTTOM_SOUTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
	protected static final VoxelShape TOP_NORTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
	protected static final VoxelShape TOP_SOUTH_WEST_CORNER_SHAPE = Block.createCuboidShape(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
	protected static final VoxelShape BOTTOM_NORTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
	protected static final VoxelShape BOTTOM_SOUTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape TOP_NORTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
	protected static final VoxelShape TOP_SOUTH_EAST_CORNER_SHAPE = Block.createCuboidShape(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape[] TOP_SHAPES = composeShapes(
		TOP_SHAPE, BOTTOM_NORTH_WEST_CORNER_SHAPE, BOTTOM_NORTH_EAST_CORNER_SHAPE, BOTTOM_SOUTH_WEST_CORNER_SHAPE, BOTTOM_SOUTH_EAST_CORNER_SHAPE
	);
	protected static final VoxelShape[] BOTTOM_SHAPES = composeShapes(
		BOTTOM_SHAPE, TOP_NORTH_WEST_CORNER_SHAPE, TOP_NORTH_EAST_CORNER_SHAPE, TOP_SOUTH_WEST_CORNER_SHAPE, TOP_SOUTH_EAST_CORNER_SHAPE
	);
	private static final int[] SHAPE_INDICES = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
	private final Block baseBlock;
	private final BlockState baseBlockState;

	private static VoxelShape[] composeShapes(
		VoxelShape voxelShape, VoxelShape voxelShape2, VoxelShape voxelShape3, VoxelShape voxelShape4, VoxelShape voxelShape5
	) {
		return (VoxelShape[])IntStream.range(0, 16)
			.mapToObj(i -> composeShape(i, voxelShape, voxelShape2, voxelShape3, voxelShape4, voxelShape5))
			.toArray(VoxelShape[]::new);
	}

	private static VoxelShape composeShape(
		int i, VoxelShape voxelShape, VoxelShape voxelShape2, VoxelShape voxelShape3, VoxelShape voxelShape4, VoxelShape voxelShape5
	) {
		VoxelShape voxelShape6 = voxelShape;
		if ((i & 1) != 0) {
			voxelShape6 = VoxelShapes.union(voxelShape, voxelShape2);
		}

		if ((i & 2) != 0) {
			voxelShape6 = VoxelShapes.union(voxelShape6, voxelShape3);
		}

		if ((i & 4) != 0) {
			voxelShape6 = VoxelShapes.union(voxelShape6, voxelShape4);
		}

		if ((i & 8) != 0) {
			voxelShape6 = VoxelShapes.union(voxelShape6, voxelShape5);
		}

		return voxelShape6;
	}

	protected StairsBlock(BlockState blockState, Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(HALF, BlockHalf.BOTTOM)
				.with(SHAPE, StairShape.STRAIGHT)
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
		this.baseBlock = blockState.getBlock();
		this.baseBlockState = blockState;
	}

	@Override
	public boolean hasSidedTransparency(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return (blockState.get(HALF) == BlockHalf.TOP ? TOP_SHAPES : BOTTOM_SHAPES)[SHAPE_INDICES[this.getShapeIndexIndex(blockState)]];
	}

	private int getShapeIndexIndex(BlockState blockState) {
		return ((StairShape)blockState.get(SHAPE)).ordinal() * 4 + ((Direction)blockState.get(FACING)).getHorizontal();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.baseBlock.randomDisplayTick(blockState, world, blockPos, random);
	}

	@Override
	public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.baseBlockState.onBlockBreakStart(world, blockPos, playerEntity);
	}

	@Override
	public void onBroken(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		this.baseBlock.onBroken(iWorld, blockPos, blockState);
	}

	@Override
	public float getBlastResistance() {
		return this.baseBlock.getBlastResistance();
	}

	@Override
	public int getTickRate(class_4538 arg) {
		return this.baseBlock.getTickRate(arg);
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState.getBlock()) {
			this.baseBlockState.neighborUpdate(world, blockPos, Blocks.AIR, blockPos, false);
			this.baseBlock.onBlockAdded(this.baseBlockState, world, blockPos, blockState2, false);
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			this.baseBlockState.onBlockRemoved(world, blockPos, blockState2, bl);
		}
	}

	@Override
	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
		this.baseBlock.onSteppedOn(world, blockPos, entity);
	}

	@Override
	public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		this.baseBlock.onScheduledTick(blockState, serverWorld, blockPos, random);
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return this.baseBlockState.activate(world, playerEntity, hand, blockHitResult);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
		this.baseBlock.onDestroyedByExplosion(world, blockPos, explosion);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.getSide();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(blockPos);
		BlockState blockState = this.getDefaultState()
			.with(FACING, itemPlacementContext.getPlayerFacing())
			.with(
				HALF,
				direction != Direction.DOWN && (direction == Direction.UP || !(itemPlacementContext.getHitPos().y - (double)blockPos.getY() > 0.5))
					? BlockHalf.BOTTOM
					: BlockHalf.TOP
			)
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
		return blockState.with(SHAPE, method_10675(blockState, itemPlacementContext.getWorld(), blockPos));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction.getAxis().isHorizontal()
			? blockState.with(SHAPE, method_10675(blockState, iWorld, blockPos))
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	private static StairShape method_10675(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Direction direction = blockState.get(FACING);
		BlockState blockState2 = blockView.getBlockState(blockPos.offset(direction));
		if (isStairs(blockState2) && blockState.get(HALF) == blockState2.get(HALF)) {
			Direction direction2 = blockState2.get(FACING);
			if (direction2.getAxis() != ((Direction)blockState.get(FACING)).getAxis() && method_10678(blockState, blockView, blockPos, direction2.getOpposite())) {
				if (direction2 == direction.rotateYCounterclockwise()) {
					return StairShape.OUTER_LEFT;
				}

				return StairShape.OUTER_RIGHT;
			}
		}

		BlockState blockState3 = blockView.getBlockState(blockPos.offset(direction.getOpposite()));
		if (isStairs(blockState3) && blockState.get(HALF) == blockState3.get(HALF)) {
			Direction direction3 = blockState3.get(FACING);
			if (direction3.getAxis() != ((Direction)blockState.get(FACING)).getAxis() && method_10678(blockState, blockView, blockPos, direction3)) {
				if (direction3 == direction.rotateYCounterclockwise()) {
					return StairShape.INNER_LEFT;
				}

				return StairShape.INNER_RIGHT;
			}
		}

		return StairShape.STRAIGHT;
	}

	private static boolean method_10678(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState2 = blockView.getBlockState(blockPos.offset(direction));
		return !isStairs(blockState2) || blockState2.get(FACING) != blockState.get(FACING) || blockState2.get(HALF) != blockState.get(HALF);
	}

	public static boolean isStairs(BlockState blockState) {
		return blockState.getBlock() instanceof StairsBlock;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		Direction direction = blockState.get(FACING);
		StairShape stairShape = blockState.get(SHAPE);
		switch (blockMirror) {
			case LEFT_RIGHT:
				if (direction.getAxis() == Direction.Axis.Z) {
					switch (stairShape) {
						case INNER_LEFT:
							return blockState.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
						case INNER_RIGHT:
							return blockState.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
						case OUTER_LEFT:
							return blockState.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return blockState.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
						default:
							return blockState.rotate(BlockRotation.CLOCKWISE_180);
					}
				}
				break;
			case FRONT_BACK:
				if (direction.getAxis() == Direction.Axis.X) {
					switch (stairShape) {
						case INNER_LEFT:
							return blockState.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_LEFT);
						case INNER_RIGHT:
							return blockState.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.INNER_RIGHT);
						case OUTER_LEFT:
							return blockState.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return blockState.rotate(BlockRotation.CLOCKWISE_180).with(SHAPE, StairShape.OUTER_LEFT);
						case STRAIGHT:
							return blockState.rotate(BlockRotation.CLOCKWISE_180);
					}
				}
		}

		return super.mirror(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, HALF, SHAPE, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
