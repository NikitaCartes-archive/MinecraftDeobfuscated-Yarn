package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SnowcappedPeaksSurfaceBuilder extends AbstractMountainSurfaceBuilder {
	private final AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig STEEP_SLOPE_BLOCK_CONFIG = new AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig(
		Blocks.PACKED_ICE.getDefaultState(), true, false, false, true
	);

	public SnowcappedPeaksSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Nullable
	@Override
	protected AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig getSteepSlopeBlockConfig() {
		return this.STEEP_SLOPE_BLOCK_CONFIG;
	}

	@Override
	protected BlockState getTopMaterial(TernarySurfaceConfig config, int x, int z) {
		BlockState blockState = this.getBlockFromNoise(0.5, x, z, Blocks.SNOW_BLOCK.getDefaultState(), Blocks.ICE.getDefaultState(), 0.0, 0.025);
		return this.getBlockFromNoise(0.0625, x, z, blockState, Blocks.PACKED_ICE.getDefaultState(), 0.0, 0.2);
	}

	@Override
	protected BlockState getUnderMaterial(TernarySurfaceConfig config, int x, int z) {
		BlockState blockState = this.getBlockFromNoise(0.5, x, z, Blocks.SNOW_BLOCK.getDefaultState(), Blocks.ICE.getDefaultState(), -0.0625, 0.025);
		return this.getBlockFromNoise(0.0625, x, z, blockState, Blocks.PACKED_ICE.getDefaultState(), -0.5, 0.2);
	}
}
