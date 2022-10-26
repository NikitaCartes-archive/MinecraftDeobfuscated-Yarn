package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;

public class DualNoiseBlockStateProvider extends NoiseBlockStateProvider {
	public static final Codec<DualNoiseBlockStateProvider> DUAL_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Range.createRangedCodec(Codec.INT, 1, 64).fieldOf("variety").forGetter(dualNoiseBlockStateProvider -> dualNoiseBlockStateProvider.variety),
					DoublePerlinNoiseSampler.NoiseParameters.CODEC
						.fieldOf("slow_noise")
						.forGetter(dualNoiseBlockStateProvider -> dualNoiseBlockStateProvider.slowNoiseParameters),
					Codecs.POSITIVE_FLOAT.fieldOf("slow_scale").forGetter(dualNoiseBlockStateProvider -> dualNoiseBlockStateProvider.slowScale)
				)
				.<long, DoublePerlinNoiseSampler.NoiseParameters, float, List<BlockState>>and(fillNoiseCodecFields(instance))
				.apply(instance, DualNoiseBlockStateProvider::new)
	);
	private final Range<Integer> variety;
	private final DoublePerlinNoiseSampler.NoiseParameters slowNoiseParameters;
	private final float slowScale;
	private final DoublePerlinNoiseSampler slowNoiseSampler;

	public DualNoiseBlockStateProvider(
		Range<Integer> variety,
		DoublePerlinNoiseSampler.NoiseParameters slowNoiseParameters,
		float slowScale,
		long seed,
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters,
		float scale,
		List<BlockState> states
	) {
		super(seed, noiseParameters, scale, states);
		this.variety = variety;
		this.slowNoiseParameters = slowNoiseParameters;
		this.slowScale = slowScale;
		this.slowNoiseSampler = DoublePerlinNoiseSampler.create(new ChunkRandom(new CheckedRandom(seed)), slowNoiseParameters);
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.DUAL_NOISE_PROVIDER;
	}

	@Override
	public BlockState get(Random random, BlockPos pos) {
		double d = this.getSlowNoiseValue(pos);
		int i = (int)MathHelper.clampedMap(
			d, -1.0, 1.0, (double)((Integer)this.variety.minInclusive()).intValue(), (double)((Integer)this.variety.maxInclusive() + 1)
		);
		List<BlockState> list = Lists.<BlockState>newArrayListWithCapacity(i);

		for (int j = 0; j < i; j++) {
			list.add(this.getStateAtValue(this.states, this.getSlowNoiseValue(pos.add(j * 54545, 0, j * 34234))));
		}

		return this.getStateFromList(list, pos, (double)this.scale);
	}

	protected double getSlowNoiseValue(BlockPos pos) {
		return this.slowNoiseSampler
			.sample((double)((float)pos.getX() * this.slowScale), (double)((float)pos.getY() * this.slowScale), (double)((float)pos.getZ() * this.slowScale));
	}
}
