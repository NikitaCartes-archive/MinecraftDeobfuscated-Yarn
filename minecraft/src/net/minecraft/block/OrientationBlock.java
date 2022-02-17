package net.minecraft.block;

import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class OrientationBlock extends Block {
	public static final EnumProperty<JigsawOrientation> field_36844 = Properties.ORIENTATION;

	public OrientationBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(field_36844, JigsawOrientation.NORTH_UP));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(field_36844);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(field_36844, rotation.getDirectionTransformation().mapJigsawOrientation(state.get(field_36844)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(field_36844, mirror.getDirectionTransformation().mapJigsawOrientation(state.get(field_36844)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide();
		Direction direction2;
		if (direction.getAxis() == Direction.Axis.Y) {
			direction2 = ctx.getPlayerFacing().getOpposite();
		} else {
			direction2 = Direction.UP;
		}

		return this.getDefaultState().with(field_36844, JigsawOrientation.byDirections(direction, direction2));
	}
}
