package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class AttachedStemBlock extends PlantBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	protected static final float field_30995 = 2.0F;
	private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.SOUTH,
			Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 16.0),
			Direction.WEST,
			Block.createCuboidShape(0.0, 0.0, 6.0, 10.0, 10.0, 10.0),
			Direction.NORTH,
			Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 10.0, 10.0),
			Direction.EAST,
			Block.createCuboidShape(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)
		)
	);
	private final GourdBlock gourdBlock;
	private final Supplier<Item> pickBlockItem;

	protected AttachedStemBlock(GourdBlock gourdBlock, Supplier<Item> pickBlockItem, AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		this.gourdBlock = gourdBlock;
		this.pickBlockItem = pickBlockItem;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)FACING_TO_SHAPE.get(state.get(FACING));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return !neighborState.isOf(this.gourdBlock) && direction == state.get(FACING)
			? this.gourdBlock.getStem().getDefaultState().with(StemBlock.AGE, Integer.valueOf(7))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.FARMLAND);
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack((ItemConvertible)this.pickBlockItem.get());
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
