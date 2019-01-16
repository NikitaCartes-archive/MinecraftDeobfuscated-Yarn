package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeashItem;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceBlock extends HorizontalConnectedBlock {
	private final VoxelShape[] SHAPES;

	public FenceBlock(Block.Settings settings) {
		super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
		this.SHAPES = this.createShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
	}

	@Override
	public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.SHAPES[this.getShapeIndex(blockState)];
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	public boolean method_10184(BlockState blockState, boolean bl, Direction direction) {
		Block block = blockState.getBlock();
		boolean bl2 = block.matches(BlockTags.field_16584) && blockState.getMaterial() == this.material;
		boolean bl3 = block instanceof FenceGateBlock && FenceGateBlock.method_16703(blockState, direction);
		return !method_10185(block) && bl || bl2 || bl3;
	}

	public static boolean method_10185(Block block) {
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
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!world.isClient) {
			return LeashItem.method_7994(playerEntity, world, blockPos);
		} else {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			return itemStack.getItem() == Items.field_8719 || itemStack.isEmpty();
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		BlockState blockState3 = blockView.getBlockState(blockPos4);
		BlockState blockState4 = blockView.getBlockState(blockPos5);
		return super.getPlacementState(itemPlacementContext)
			.with(
				NORTH,
				Boolean.valueOf(this.method_10184(blockState, Block.isFaceFullCube(blockState.getCollisionShape(blockView, blockPos2), Direction.SOUTH), Direction.SOUTH))
			)
			.with(
				EAST,
				Boolean.valueOf(this.method_10184(blockState2, Block.isFaceFullCube(blockState2.getCollisionShape(blockView, blockPos3), Direction.WEST), Direction.WEST))
			)
			.with(
				SOUTH,
				Boolean.valueOf(this.method_10184(blockState3, Block.isFaceFullCube(blockState3.getCollisionShape(blockView, blockPos4), Direction.NORTH), Direction.NORTH))
			)
			.with(
				WEST,
				Boolean.valueOf(this.method_10184(blockState4, Block.isFaceFullCube(blockState4.getCollisionShape(blockView, blockPos5), Direction.EAST), Direction.EAST))
			)
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
		}

		return direction.getAxis().method_10180() == Direction.class_2353.HORIZONTAL
			? blockState.with(
				(Property)FACING_PROPERTIES.get(direction),
				Boolean.valueOf(
					this.method_10184(blockState2, Block.isFaceFullCube(blockState2.getCollisionShape(iWorld, blockPos2), direction.getOpposite()), direction.getOpposite())
				)
			)
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}
}
