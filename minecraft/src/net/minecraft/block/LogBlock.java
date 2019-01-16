package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class LogBlock extends PillarBlock {
	private final MaterialColor field_11292;

	public LogBlock(MaterialColor materialColor, Block.Settings settings) {
		super(settings);
		this.field_11292 = materialColor;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(AXIS) == Direction.Axis.Y ? this.field_11292 : this.materialColor;
	}
}
