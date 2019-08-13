package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class LogBlock extends PillarBlock {
	private final MaterialColor endMaterialColor;

	public LogBlock(MaterialColor materialColor, Block.Settings settings) {
		super(settings);
		this.endMaterialColor = materialColor;
	}

	@Override
	public MaterialColor getMapColor(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(AXIS) == Direction.Axis.field_11052 ? this.endMaterialColor : this.materialColor;
	}
}
