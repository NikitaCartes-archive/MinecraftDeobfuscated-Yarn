package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class LanternBlock extends Block {
	public static final BooleanProperty HANGING = Properties.HANGING;
	protected static final VoxelShape STANDING_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), Block.createCuboidShape(6.0, 7.0, 6.0, 10.0, 9.0, 10.0)
	);
	protected static final VoxelShape HANGING_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(5.0, 1.0, 5.0, 11.0, 8.0, 11.0), Block.createCuboidShape(6.0, 8.0, 6.0, 10.0, 10.0, 10.0)
	);

	public LanternBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(HANGING, Boolean.valueOf(false)));
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		for (Direction direction : itemPlacementContext.getPlacementDirections()) {
			if (direction.getAxis() == Direction.Axis.field_11052) {
				BlockState blockState = this.getDefaultState().with(HANGING, Boolean.valueOf(direction == Direction.field_11036));
				if (blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return blockState.get(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(HANGING);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = attachedDirection(blockState).getOpposite();
		return Block.isSolidSmallSquare(viewableWorld, blockPos.offset(direction), direction.getOpposite());
	}

	protected static Direction attachedDirection(BlockState blockState) {
		return blockState.get(HANGING) ? Direction.field_11033 : Direction.field_11036;
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return attachedDirection(blockState).getOpposite() == direction && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
