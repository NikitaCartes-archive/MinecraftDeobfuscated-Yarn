package net.minecraft.block;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public abstract class HorizontalFacingBlock extends Block {
	public static final DirectionProperty FACING = Properties.FACING_HORIZONTAL;

	protected HorizontalFacingBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.with(FACING, rotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.get(FACING)));
	}
}
