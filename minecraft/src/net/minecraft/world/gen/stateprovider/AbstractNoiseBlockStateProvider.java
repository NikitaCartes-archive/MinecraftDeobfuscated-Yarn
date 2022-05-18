package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;

public abstract class AbstractNoiseBlockStateProvider extends BlockStateProvider {
	protected final long seed;
	protected final DoublePerlinNoiseSampler.NoiseParameters noiseParameters;
	protected final float scale;
	protected final DoublePerlinNoiseSampler noiseSampler;

	protected static <P extends AbstractNoiseBlockStateProvider> P3<Mu<P>, Long, DoublePerlinNoiseSampler.NoiseParameters, Float> fillCodecFields(
		Instance<P> instance
	) {
		return instance.group(
			Codec.LONG.fieldOf("seed").forGetter(abstractNoiseBlockStateProvider -> abstractNoiseBlockStateProvider.seed),
			DoublePerlinNoiseSampler.NoiseParameters.CODEC
				.fieldOf("noise")
				.forGetter(abstractNoiseBlockStateProvider -> abstractNoiseBlockStateProvider.noiseParameters),
			Codecs.POSITIVE_FLOAT.fieldOf("scale").forGetter(abstractNoiseBlockStateProvider -> abstractNoiseBlockStateProvider.scale)
		);
	}

	protected AbstractNoiseBlockStateProvider(long seed, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float scale) {
		this.seed = seed;
		this.noiseParameters = noiseParameters;
		this.scale = scale;
		this.noiseSampler = DoublePerlinNoiseSampler.create(new ChunkRandom(new CheckedRandom(seed)), noiseParameters);
	}

	protected double getNoiseValue(BlockPos pos, double scale) {
		return this.noiseSampler.sample((double)pos.getX() * scale, (double)pos.getY() * scale, (double)pos.getZ() * scale);
	}
}
