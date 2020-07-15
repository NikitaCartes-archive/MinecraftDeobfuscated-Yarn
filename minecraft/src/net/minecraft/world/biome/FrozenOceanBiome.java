package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;

public final class FrozenOceanBiome extends Biome {
	protected static final OctaveSimplexNoiseSampler field_26380 = new OctaveSimplexNoiseSampler(new ChunkRandom(3456L), ImmutableList.of(-2, -1, 0));

	public FrozenOceanBiome(Biome.Settings settings) {
		super(settings);
	}

	@Override
	protected float computeTemperature(BlockPos blockPos) {
		float f = this.getTemperature();
		double d = field_26380.sample((double)blockPos.getX() * 0.05, (double)blockPos.getZ() * 0.05, false) * 7.0;
		double e = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.2, (double)blockPos.getZ() * 0.2, false);
		double g = d + e;
		if (g < 0.3) {
			double h = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.09, (double)blockPos.getZ() * 0.09, false);
			if (h < 0.8) {
				f = 0.2F;
			}
		}

		if (blockPos.getY() > 64) {
			float i = (float)(TEMPERATURE_NOISE.sample((double)((float)blockPos.getX() / 8.0F), (double)((float)blockPos.getZ() / 8.0F), false) * 4.0);
			return f - (i + (float)blockPos.getY() - 64.0F) * 0.05F / 30.0F;
		} else {
			return f;
		}
	}
}
