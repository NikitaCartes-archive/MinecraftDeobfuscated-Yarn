package net.minecraft.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
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
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class StairsBlock extends Block implements Waterloggable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
	public static final EnumProperty<StairShape> SHAPE = Properties.STAIR_SHAPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape TOP_SHAPE = SlabBlock.TOP_SHAPE;
	protected static final VoxelShape BOTTOM_SHAPE = SlabBlock.BOTTOM_SHAPE;
	protected static final VoxelShape field_11561 = Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
	protected static final VoxelShape field_11578 = Block.createCuboidShape(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
	protected static final VoxelShape field_11568 = Block.createCuboidShape(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
	protected static final VoxelShape field_11563 = Block.createCuboidShape(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
	protected static final VoxelShape field_11575 = Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
	protected static final VoxelShape field_11569 = Block.createCuboidShape(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape field_11577 = Block.createCuboidShape(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
	protected static final VoxelShape field_11567 = Block.createCuboidShape(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape[] field_11566 = method_10672(TOP_SHAPE, field_11561, field_11575, field_11578, field_11569);
	protected static final VoxelShape[] field_11564 = method_10672(BOTTOM_SHAPE, field_11568, field_11577, field_11563, field_11567);
	private static final int[] field_11570 = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
	private final Block baseBlock;
	private final BlockState baseBlockState;

	private static VoxelShape[] method_10672(VoxelShape voxelShape, VoxelShape voxelShape2, VoxelShape voxelShape3, VoxelShape voxelShape4, VoxelShape voxelShape5) {
		return (VoxelShape[])IntStream.range(0, 16)
			.mapToObj(i -> method_10671(i, voxelShape, voxelShape2, voxelShape3, voxelShape4, voxelShape5))
			.toArray(VoxelShape[]::new);
	}

	private static VoxelShape method_10671(
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
				.with(SHAPE, StairShape.field_12710)
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
		this.baseBlock = blockState.getBlock();
		this.baseBlockState = blockState;
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return (blockState.get(HALF) == BlockHalf.TOP ? field_11566 : field_11564)[field_11570[this.method_10673(blockState)]];
	}

	private int method_10673(BlockState blockState) {
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
	public BlockRenderLayer getRenderLayer() {
		return this.baseBlock.getRenderLayer();
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return this.baseBlock.getTickRate(viewableWorld);
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
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.baseBlock.onScheduledTick(blockState, world, blockPos, random);
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
		Direction direction = itemPlacementContext.getFacing();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(blockPos);
		BlockState blockState = this.getDefaultState()
			.with(FACING, itemPlacementContext.getPlayerHorizontalFacing())
			.with(
				HALF,
				direction != Direction.DOWN && (direction == Direction.UP || !(itemPlacementContext.getPos().y - (double)blockPos.getY() > 0.5))
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
					return StairShape.field_12708;
				}

				return StairShape.field_12709;
			}
		}

		BlockState blockState3 = blockView.getBlockState(blockPos.offset(direction.getOpposite()));
		if (isStairs(blockState3) && blockState.get(HALF) == blockState3.get(HALF)) {
			Direction direction3 = blockState3.get(FACING);
			if (direction3.getAxis() != ((Direction)blockState.get(FACING)).getAxis() && method_10678(blockState, blockView, blockPos, direction3)) {
				if (direction3 == direction.rotateYCounterclockwise()) {
					return StairShape.field_12712;
				}

				return StairShape.field_12713;
			}
		}

		return StairShape.field_12710;
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
						case field_12712:
							return blockState.rotate(BlockRotation.ROT_180).with(SHAPE, StairShape.field_12713);
						case field_12713:
							return blockState.rotate(BlockRotation.ROT_180).with(SHAPE, StairShape.field_12712);
						case field_12708:
							return blockState.rotate(BlockRotation.ROT_180).with(SHAPE, StairShape.field_12709);
						case field_12709:
							return blockState.rotate(BlockRotation.ROT_180).with(SHAPE, StairShape.field_12708);
						default:
							return blockState.rotate(BlockRotation.ROT_180);
					}
				}
				break;
			case FRONT_BACK:
				if (direction.getAxis() == Direction.Axis.X) {
					switch (stairShape) {
						case field_12712:
							return blockState.rotate(BlockRotation.ROT_180).with(SHAPE, StairShape.field_12712);
						case field_12713:
							return blockState.rotate(BlockRotation.ROT_180).with(SHAPE, StairShape.field_12713);
						case field_12708:
							return blockState.rotate(BlockRotation.ROT_180).with(SHAPE, StairShape.field_12709);
						case field_12709:
							return blockState.rotate(BlockRotation.ROT_180).with(SHAPE, StairShape.field_12708);
						case field_12710:
							return blockState.rotate(BlockRotation.ROT_180);
					}
				}
		}

		return super.mirror(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, HALF, SHAPE, WATERLOGGED);
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
