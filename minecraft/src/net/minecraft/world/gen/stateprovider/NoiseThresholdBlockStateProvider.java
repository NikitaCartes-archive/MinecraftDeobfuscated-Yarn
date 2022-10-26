package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.Random;

public class NoiseThresholdBlockStateProvider extends AbstractNoiseBlockStateProvider {
	public static final Codec<NoiseThresholdBlockStateProvider> CODEC = RecordCodecBuilder.create(
		instance -> fillCodecFields(instance)
				.<float, float, BlockState, List<BlockState>, List<BlockState>>and(
					instance.group(
						Codec.floatRange(-1.0F, 1.0F).fieldOf("threshold").forGetter(noiseThresholdBlockStateProvider -> noiseThresholdBlockStateProvider.threshold),
						Codec.floatRange(0.0F, 1.0F).fieldOf("high_chance").forGetter(noiseThresholdBlockStateProvider -> noiseThresholdBlockStateProvider.highChance),
						BlockState.CODEC.fieldOf("default_state").forGetter(noiseThresholdBlockStateProvider -> noiseThresholdBlockStateProvider.defaultState),
						Codec.list(BlockState.CODEC).fieldOf("low_states").forGetter(noiseThresholdBlockStateProvider -> noiseThresholdBlockStateProvider.lowStates),
						Codec.list(BlockState.CODEC).fieldOf("high_states").forGetter(noiseThresholdBlockStateProvider -> noiseThresholdBlockStateProvider.highStates)
					)
				)
				.apply(instance, NoiseThresholdBlockStateProvider::new)
	);
	private final float threshold;
	private final float highChance;
	private final BlockState defaultState;
	private final List<BlockState> lowStates;
	private final List<BlockState> highStates;

	public NoiseThresholdBlockStateProvider(
		long seed,
		DoublePerlinNoiseSampler.NoiseParameters noiseParameters,
		float scale,
		float threshold,
		float highChance,
		BlockState defaultState,
		List<BlockState> lowStates,
		List<BlockState> highStates
	) {
		super(seed, noiseParameters, scale);
		this.threshold = threshold;
		this.highChance = highChance;
		this.defaultState = defaultState;
		this.lowStates = lowStates;
		this.highStates = highStates;
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.NOISE_THRESHOLD_PROVIDER;
	}

	@Override
	public BlockState get(Random random, BlockPos pos) {
		double d = this.getNoiseValue(pos, (double)this.scale);
		if (d < (double)this.threshold) {
			return Util.getRandom(this.lowStates, random);
		} else {
			return random.nextFloat() < this.highChance ? Util.getRandom(this.highStates, random) : this.defaultState;
		}
	}
}
