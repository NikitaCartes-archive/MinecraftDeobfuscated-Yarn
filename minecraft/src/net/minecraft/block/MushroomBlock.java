package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class MushroomBlock extends Block {
	public static final MapCodec<MushroomBlock> CODEC = createCodec(MushroomBlock::new);
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final BooleanProperty UP = ConnectingBlock.UP;
	public static final BooleanProperty DOWN = ConnectingBlock.DOWN;
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;

	@Override
	public MapCodec<MushroomBlock> getCodec() {
		return CODEC;
	}

	public MushroomBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(NORTH, Boolean.valueOf(true))
				.with(EAST, Boolean.valueOf(true))
				.with(SOUTH, Boolean.valueOf(true))
				.with(WEST, Boolean.valueOf(true))
				.with(UP, Boolean.valueOf(true))
				.with(DOWN, Boolean.valueOf(true))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		return this.getDefaultState()
			.with(DOWN, Boolean.valueOf(!blockView.getBlockState(blockPos.down()).isOf(this)))
			.with(UP, Boolean.valueOf(!blockView.getBlockState(blockPos.up()).isOf(this)))
			.with(NORTH, Boolean.valueOf(!blockView.getBlockState(blockPos.north()).isOf(this)))
			.with(EAST, Boolean.valueOf(!blockView.getBlockState(blockPos.east()).isOf(this)))
			.with(SOUTH, Boolean.valueOf(!blockView.getBlockState(blockPos.south()).isOf(this)))
			.with(WEST, Boolean.valueOf(!blockView.getBlockState(blockPos.west()).isOf(this)));
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return neighborState.isOf(this)
			? state.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(false))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.NORTH)), (Boolean)state.get(NORTH))
			.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.SOUTH)), (Boolean)state.get(SOUTH))
			.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.EAST)), (Boolean)state.get(EAST))
			.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.WEST)), (Boolean)state.get(WEST))
			.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.UP)), (Boolean)state.get(UP))
			.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.DOWN)), (Boolean)state.get(DOWN));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.NORTH)), (Boolean)state.get(NORTH))
			.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.SOUTH)), (Boolean)state.get(SOUTH))
			.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.EAST)), (Boolean)state.get(EAST))
			.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.WEST)), (Boolean)state.get(WEST))
			.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.UP)), (Boolean)state.get(UP))
			.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.DOWN)), (Boolean)state.get(DOWN));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
	}
}
