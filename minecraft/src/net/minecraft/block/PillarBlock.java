package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class PillarBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

	public PillarBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.field_11052));
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11465:
			case field_11463:
				switch ((Direction.Axis)blockState.get(AXIS)) {
					case field_11048:
						return blockState.with(AXIS, Direction.Axis.field_11051);
					case field_11051:
						return blockState.with(AXIS, Direction.Axis.field_11048);
					default:
						return blockState;
				}
			default:
				return blockState;
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(AXIS, itemPlacementContext.getSide().getAxis());
	}
}
