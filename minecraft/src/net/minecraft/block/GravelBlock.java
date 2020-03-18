package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GravelBlock extends FallingBlock {
	public GravelBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getColor(BlockState state, BlockView blockView, BlockPos blockPos) {
		return -8356741;
	}
}
