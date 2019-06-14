package net.minecraft.block;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

public abstract class HorizontalFacingBlock extends Block {
	public static final DirectionProperty field_11177 = Properties.field_12481;

	protected HorizontalFacingBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_11177, blockRotation.rotate(blockState.method_11654(field_11177)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_11177)));
	}
}
