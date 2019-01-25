package net.minecraft.block;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public abstract class HorizontalFacingBlock extends Block {
	public static final DirectionProperty field_11177 = Properties.FACING_HORIZONTAL;

	protected HorizontalFacingBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.with(field_11177, rotation.rotate(blockState.get(field_11177)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.get(field_11177)));
	}
}
