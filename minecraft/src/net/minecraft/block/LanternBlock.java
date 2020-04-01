package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class LanternBlock extends Block {
	public static final BooleanProperty HANGING = Properties.HANGING;
	protected static final VoxelShape STANDING_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), Block.createCuboidShape(6.0, 7.0, 6.0, 10.0, 9.0, 10.0)
	);
	protected static final VoxelShape HANGING_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(5.0, 1.0, 5.0, 11.0, 8.0, 11.0), Block.createCuboidShape(6.0, 8.0, 6.0, 10.0, 10.0, 10.0)
	);

	public LanternBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(HANGING, Boolean.valueOf(false)));
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		for (Direction direction : ctx.getPlacementDirections()) {
			if (direction.getAxis() == Direction.Axis.Y) {
				BlockState blockState = this.getDefaultState().with(HANGING, Boolean.valueOf(direction == Direction.UP));
				if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HANGING);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Direction direction = attachedDirection(state).getOpposite();
		return Block.sideCoversSmallSquare(world, pos.offset(direction), direction.getOpposite());
	}

	@Override
	public boolean method_26477() {
		return true;
	}

	protected static Direction attachedDirection(BlockState state) {
		return state.get(HANGING) ? Direction.DOWN : Direction.UP;
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		return attachedDirection(state).getOpposite() == direction && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
