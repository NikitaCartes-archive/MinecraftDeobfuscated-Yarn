package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class SandBlock extends FallingBlock {
	private final int color;

	public SandBlock(int color, AbstractBlock.Settings settings) {
		super(settings);
		this.color = color;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getColor(BlockState state, BlockView blockView, BlockPos blockPos) {
		return this.color;
	}
}
