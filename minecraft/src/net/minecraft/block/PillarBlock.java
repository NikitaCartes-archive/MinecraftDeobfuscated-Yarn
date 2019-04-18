package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class PillarBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS_XYZ;

	public PillarBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.Y));
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case ROT_270:
			case ROT_90:
				switch ((Direction.Axis)blockState.get(AXIS)) {
					case X:
						return blockState.with(AXIS, Direction.Axis.Z);
					case Z:
						return blockState.with(AXIS, Direction.Axis.X);
					default:
						return blockState;
				}
			default:
				return blockState;
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(AXIS);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(AXIS, itemPlacementContext.getFacing().getAxis());
	}
}
