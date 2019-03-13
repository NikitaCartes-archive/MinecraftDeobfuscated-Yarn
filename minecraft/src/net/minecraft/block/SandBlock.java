package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SandBlock extends FallingBlock {
	private final int color;

	public SandBlock(int i, Block.Settings settings) {
		super(settings);
		this.color = i;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int method_10130(BlockState blockState) {
		return this.color;
	}
}
