package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class LoftyPeaksSurfaceBuilder extends AbstractMountainSurfaceBuilder {
	private final AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig STEEP_SLOPE_BLOCK_CONFIG = new AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig(
		Blocks.STONE.getDefaultState(), true, false, false, true
	);

	public LoftyPeaksSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Nullable
	@Override
	protected AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig getSteepSlopeBlockConfig() {
		return this.STEEP_SLOPE_BLOCK_CONFIG;
	}

	@Override
	protected BlockState getTopMaterial(TernarySurfaceConfig config, int x, int z) {
		return config.getTopMaterial();
	}

	@Override
	protected BlockState getUnderMaterial(TernarySurfaceConfig config, int x, int z) {
		return config.getUnderMaterial();
	}
}
