package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.enums.Orientation;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class HeavyCoreBlock extends Block {
	public static final MapCodec<HeavyCoreBlock> CODEC = createCodec(HeavyCoreBlock::new);
	private static final VoxelShape OUTLINE_SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
	public static final EnumProperty<Orientation> ORIENTATION = Properties.ORIENTATION;

	public HeavyCoreBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(ORIENTATION, Orientation.NORTH_UP));
	}

	@Override
	public MapCodec<HeavyCoreBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ORIENTATION);
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ORIENTATION, rotation.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(ORIENTATION, mirror.getDirectionTransformation().mapJigsawOrientation(state.get(ORIENTATION)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide();
		Direction direction2;
		if (direction.getAxis() == Direction.Axis.Y) {
			direction2 = ctx.getHorizontalPlayerFacing().getOpposite();
		} else {
			direction2 = Direction.UP;
		}

		return this.getDefaultState().with(ORIENTATION, Orientation.byDirections(direction, direction2));
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPE;
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}
}
