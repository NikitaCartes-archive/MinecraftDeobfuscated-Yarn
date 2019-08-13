package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
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

	public boolean canConnect(BlockState blockState, boolean bl, Direction direction) {
		Block block = blockState.getBlock();
		boolean bl2 = block.matches(BlockTags.field_16584) && blockState.getMaterial() == this.material;
		boolean bl3 = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(blockState, direction);
		return !canConnect(block) && bl || bl2 || bl3;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!world.isClient) {
			return LeadItem.attachHeldMobsToBlock(playerEntity, world, blockPos);
		} else {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			return itemStack.getItem() == Items.field_8719 || itemStack.isEmpty();
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
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
				NORTH, Boolean.valueOf(this.canConnect(blockState, blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11035), Direction.field_11035))
			)
			.with(
				EAST, Boolean.valueOf(this.canConnect(blockState2, blockState2.isSideSolidFullSquare(blockView, blockPos3, Direction.field_11039), Direction.field_11039))
			)
			.with(
				SOUTH, Boolean.valueOf(this.canConnect(blockState3, blockState3.isSideSolidFullSquare(blockView, blockPos4, Direction.field_11043), Direction.field_11043))
			)
			.with(
				WEST, Boolean.valueOf(this.canConnect(blockState4, blockState4.isSideSolidFullSquare(blockView, blockPos5, Direction.field_11034), Direction.field_11034))
			)
			.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction.getAxis().getType() == Direction.Type.field_11062
			? blockState.with(
				(Property)FACING_PROPERTIES.get(direction),
				Boolean.valueOf(this.canConnect(blockState2, blockState2.isSideSolidFullSquare(iWorld, blockPos2, direction.getOpposite()), direction.getOpposite()))
			)
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}
}
