package net.minecraft.block;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

public abstract class HorizontalFacingBlock extends Block {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	protected HorizontalFacingBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}
}
