package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SandBlock extends FallingBlock {
	private final int color;

	public SandBlock(int color, Block.Settings settings) {
		super(settings);
		this.color = color;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getColor(BlockState state) {
		return this.color;
	}
}
