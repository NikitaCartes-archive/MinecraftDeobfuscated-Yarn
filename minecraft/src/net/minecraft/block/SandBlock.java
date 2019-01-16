package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SandBlock extends FallingBlock {
	private final int field_11462;

	public SandBlock(int i, Block.Settings settings) {
		super(settings);
		this.field_11462 = i;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getColor(BlockState blockState) {
		return this.field_11462;
	}
}
