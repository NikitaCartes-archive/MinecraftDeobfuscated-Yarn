package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class GravelBlock extends FallingBlock {
	public GravelBlock(Block.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_10130(BlockState blockState) {
		return -8356741;
	}
}
