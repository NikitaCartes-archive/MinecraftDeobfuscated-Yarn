package net.minecraft.block;

import net.minecraft.util.math.Vec3d;

public class DropperBlock extends DispenserBlock {
	public DropperBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected Vec3d method_42874(BlockState blockState) {
		return Vec3d.ZERO;
	}
}
