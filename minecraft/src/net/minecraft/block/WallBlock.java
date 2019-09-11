package net.minecraft.block;

import net.minecraft.class_4538;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class WallBlock extends HorizontalConnectedBlock {
	public static final BooleanProperty UP = Properties.UP;
	private final VoxelShape[] UP_OUTLINE_SHAPES;
	private final VoxelShape[] UP_COLLISION_SHAPES;

	public WallBlock(Block.Settings settings) {
		super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(UP, Boolean.valueOf(true))
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
		this.UP_OUTLINE_SHAPES = this.createShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
		this.UP_COLLISION_SHAPES = this.createShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return blockState.get(UP) ? this.UP_OUTLINE_SHAPES[this.getShapeIndex(blockState)] : super.getOutlineShape(blockState, blockView, blockPos, entityContext);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return blockState.get(UP)
			? this.UP_COLLISION_SHAPES[this.getShapeIndex(blockState)]
			: super.getCollisionShape(blockState, blockView, blockPos, entityContext);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	private boolean shouldConnectTo(BlockState blockState, boolean bl, Direction direction) {
		Block block = blockState.getBlock();
		boolean bl2 = block.matches(BlockTags.WALLS) || block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(blockState, direction);
		return !canConnect(block) && bl || bl2;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		class_4538 lv = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockState blockState = lv.getBlockState(blockPos2);
		BlockState blockState2 = lv.getBlockState(blockPos3);
		BlockState blockState3 = lv.getBlockState(blockPos4);
		BlockState blockState4 = lv.getBlockState(blockPos5);
		boolean bl = this.shouldConnectTo(blockState, blockState.isSideSolidFullSquare(lv, blockPos2, Direction.SOUTH), Direction.SOUTH);
		boolean bl2 = this.shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(lv, blockPos3, Direction.WEST), Direction.WEST);
		boolean bl3 = this.shouldConnectTo(blockState3, blockState3.isSideSolidFullSquare(lv, blockPos4, Direction.NORTH), Direction.NORTH);
		boolean bl4 = this.shouldConnectTo(blockState4, blockState4.isSideSolidFullSquare(lv, blockPos5, Direction.EAST), Direction.EAST);
		boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
		return this.getDefaultState()
			.with(UP, Boolean.valueOf(bl5 || !lv.isAir(blockPos.up())))
			.with(NORTH, Boolean.valueOf(bl))
			.with(EAST, Boolean.valueOf(bl2))
			.with(SOUTH, Boolean.valueOf(bl3))
			.with(WEST, Boolean.valueOf(bl4))
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		if (direction == Direction.DOWN) {
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			Direction direction2 = direction.getOpposite();
			boolean bl = direction == Direction.NORTH
				? this.shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(iWorld, blockPos2, direction2), direction2)
				: (Boolean)blockState.get(NORTH);
			boolean bl2 = direction == Direction.EAST
				? this.shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(iWorld, blockPos2, direction2), direction2)
				: (Boolean)blockState.get(EAST);
			boolean bl3 = direction == Direction.SOUTH
				? this.shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(iWorld, blockPos2, direction2), direction2)
				: (Boolean)blockState.get(SOUTH);
			boolean bl4 = direction == Direction.WEST
				? this.shouldConnectTo(blockState2, blockState2.isSideSolidFullSquare(iWorld, blockPos2, direction2), direction2)
				: (Boolean)blockState.get(WEST);
			boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
			return blockState.with(UP, Boolean.valueOf(bl5 || !iWorld.isAir(blockPos.up())))
				.with(NORTH, Boolean.valueOf(bl))
				.with(EAST, Boolean.valueOf(bl2))
				.with(SOUTH, Boolean.valueOf(bl3))
				.with(WEST, Boolean.valueOf(bl4));
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}
}
