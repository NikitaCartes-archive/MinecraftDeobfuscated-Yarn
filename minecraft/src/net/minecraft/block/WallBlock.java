package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
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
import net.minecraft.world.ViewableWorld;

public class WallBlock extends HorizontalConnectedBlock {
	public static final BooleanProperty field_11717 = Properties.UP_BOOL;
	private final VoxelShape[] field_11718;
	private final VoxelShape[] field_11716;

	public WallBlock(Block.Settings settings) {
		super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11717, Boolean.valueOf(true))
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
		this.field_11718 = this.createShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
		this.field_11716 = this.createShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return blockState.get(field_11717)
			? this.field_11718[this.getShapeIndex(blockState)]
			: super.getOutlineShape(blockState, blockView, blockPos, verticalEntityPosition);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return blockState.get(field_11717)
			? this.field_11716[this.getShapeIndex(blockState)]
			: super.getCollisionShape(blockState, blockView, blockPos, verticalEntityPosition);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	private boolean method_16704(BlockState blockState, boolean bl, Direction direction) {
		Block block = blockState.getBlock();
		boolean bl2 = block.matches(BlockTags.field_15504) || block instanceof FenceGateBlock && FenceGateBlock.method_16703(blockState, direction);
		return !method_10837(block) && bl || bl2;
	}

	public static boolean method_10837(Block block) {
		return Block.method_9581(block)
			|| block == Blocks.field_10499
			|| block == Blocks.field_10545
			|| block == Blocks.field_10261
			|| block == Blocks.field_10147
			|| block == Blocks.field_10009
			|| block == Blocks.field_10110
			|| block == Blocks.field_10375;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockState blockState = viewableWorld.getBlockState(blockPos2);
		BlockState blockState2 = viewableWorld.getBlockState(blockPos3);
		BlockState blockState3 = viewableWorld.getBlockState(blockPos4);
		BlockState blockState4 = viewableWorld.getBlockState(blockPos5);
		boolean bl = this.method_16704(blockState, Block.isFaceFullCube(blockState.getCollisionShape(viewableWorld, blockPos2), Direction.SOUTH), Direction.SOUTH);
		boolean bl2 = this.method_16704(blockState2, Block.isFaceFullCube(blockState2.getCollisionShape(viewableWorld, blockPos3), Direction.WEST), Direction.WEST);
		boolean bl3 = this.method_16704(blockState3, Block.isFaceFullCube(blockState3.getCollisionShape(viewableWorld, blockPos4), Direction.NORTH), Direction.NORTH);
		boolean bl4 = this.method_16704(blockState4, Block.isFaceFullCube(blockState4.getCollisionShape(viewableWorld, blockPos5), Direction.EAST), Direction.EAST);
		boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
		return this.getDefaultState()
			.with(field_11717, Boolean.valueOf(bl5 || !viewableWorld.isAir(blockPos.up())))
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
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
		}

		if (direction == Direction.DOWN) {
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			Direction direction2 = direction.getOpposite();
			boolean bl = direction == Direction.NORTH
				? this.method_16704(blockState2, Block.isFaceFullCube(blockState2.getCollisionShape(iWorld, blockPos2), direction2), direction2)
				: (Boolean)blockState.get(NORTH);
			boolean bl2 = direction == Direction.EAST
				? this.method_16704(blockState2, Block.isFaceFullCube(blockState2.getCollisionShape(iWorld, blockPos2), direction2), direction2)
				: (Boolean)blockState.get(EAST);
			boolean bl3 = direction == Direction.SOUTH
				? this.method_16704(blockState2, Block.isFaceFullCube(blockState2.getCollisionShape(iWorld, blockPos2), direction2), direction2)
				: (Boolean)blockState.get(SOUTH);
			boolean bl4 = direction == Direction.WEST
				? this.method_16704(blockState2, Block.isFaceFullCube(blockState2.getCollisionShape(iWorld, blockPos2), direction2), direction2)
				: (Boolean)blockState.get(WEST);
			boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
			return blockState.with(field_11717, Boolean.valueOf(bl5 || !iWorld.isAir(blockPos.up())))
				.with(NORTH, Boolean.valueOf(bl))
				.with(EAST, Boolean.valueOf(bl2))
				.with(SOUTH, Boolean.valueOf(bl3))
				.with(WEST, Boolean.valueOf(bl4));
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11717, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}
}
