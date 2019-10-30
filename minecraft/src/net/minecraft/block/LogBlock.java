package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class LogBlock extends PillarBlock {
	private final MaterialColor endMaterialColor;

	public LogBlock(MaterialColor endMaterialColor, Block.Settings settings) {
		super(settings);
		this.endMaterialColor = endMaterialColor;
	}

	@Override
	public MaterialColor getMapColor(BlockState state, BlockView view, BlockPos pos) {
		return state.get(AXIS) == Direction.Axis.Y ? this.endMaterialColor : this.materialColor;
	}
}
