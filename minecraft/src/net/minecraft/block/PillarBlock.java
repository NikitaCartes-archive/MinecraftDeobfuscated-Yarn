package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.Direction;

public class PillarBlock extends Block {
	public static final EnumProperty<Direction.Axis> field_11459 = Properties.field_12496;

	public PillarBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.method_9564().method_11657(field_11459, Direction.Axis.Y));
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		switch (rotation) {
			case ROT_270:
			case ROT_90:
				switch ((Direction.Axis)blockState.method_11654(field_11459)) {
					case X:
						return blockState.method_11657(field_11459, Direction.Axis.Z);
					case Z:
						return blockState.method_11657(field_11459, Direction.Axis.X);
					default:
						return blockState;
				}
			default:
				return blockState;
		}
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11459);
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_11459, itemPlacementContext.method_8038().getAxis());
	}
}
