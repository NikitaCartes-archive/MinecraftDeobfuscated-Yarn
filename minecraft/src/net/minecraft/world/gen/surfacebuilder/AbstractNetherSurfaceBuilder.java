package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.random.ChunkRandom;

public abstract class AbstractNetherSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private long seed;
	private ImmutableMap<BlockState, OctavePerlinNoiseSampler> surfaceNoises = ImmutableMap.of();
	private ImmutableMap<BlockState, OctavePerlinNoiseSampler> underLavaNoises = ImmutableMap.of();
	private OctavePerlinNoiseSampler shoreNoise;

	public AbstractNetherSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	public void generate(
		Random random,
		BlockColumn blockColumn,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		int m,
		long n,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		int o = l + 1;
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		double e = 0.03125;
		boolean bl = this.shoreNoise.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) * 75.0 + random.nextDouble() > 0.0;
		BlockState blockState3 = (BlockState)((Entry)this.underLavaNoises
				.entrySet()
				.stream()
				.max(Comparator.comparing(entry -> ((OctavePerlinNoiseSampler)entry.getValue()).sample((double)i, (double)l, (double)j)))
				.get())
			.getKey();
		BlockState blockState4 = (BlockState)((Entry)this.surfaceNoises
				.entrySet()
				.stream()
				.max(Comparator.comparing(entry -> ((OctavePerlinNoiseSampler)entry.getValue()).sample((double)i, (double)l, (double)j)))
				.get())
			.getKey();
		BlockState blockState5 = blockColumn.getState(128);

		for (int r = 127; r >= m; r--) {
			BlockState blockState6 = blockColumn.getState(r);
			if (blockState5.isOf(blockState.getBlock()) && (blockState6.isAir() || blockState6 == blockState2)) {
				for (int s = 0; s < p && blockColumn.getState(r + s).isOf(blockState.getBlock()); s++) {
					blockColumn.setState(r + s, blockState3);
				}
			}

			if ((blockState5.isAir() || blockState5 == blockState2) && blockState6.isOf(blockState.getBlock())) {
				for (int s = 0; s < q && blockColumn.getState(r - s).isOf(blockState.getBlock()); s++) {
					if (bl && r >= o - 4 && r <= o + 1) {
						blockColumn.setState(r - s, this.getLavaShoreState());
					} else {
						blockColumn.setState(r - s, blockState4);
					}
				}
			}

			blockState5 = blockState6;
		}
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed || this.shoreNoise == null || this.surfaceNoises.isEmpty() || this.underLavaNoises.isEmpty()) {
			this.surfaceNoises = createNoisesForStates(this.getSurfaceStates(), seed);
			this.underLavaNoises = createNoisesForStates(this.getUnderLavaStates(), seed + (long)this.surfaceNoises.size());
			this.shoreNoise = new OctavePerlinNoiseSampler(
				new ChunkRandom(seed + (long)this.surfaceNoises.size() + (long)this.underLavaNoises.size()), ImmutableList.of(0)
			);
		}

		this.seed = seed;
	}

	private static ImmutableMap<BlockState, OctavePerlinNoiseSampler> createNoisesForStates(ImmutableList<BlockState> states, long seed) {
		Builder<BlockState, OctavePerlinNoiseSampler> builder = new Builder<>();

		for (BlockState blockState : states) {
			builder.put(blockState, new OctavePerlinNoiseSampler(new ChunkRandom(seed), ImmutableList.of(-4)));
			seed++;
		}

		return builder.build();
	}

	protected abstract ImmutableList<BlockState> getSurfaceStates();

	protected abstract ImmutableList<BlockState> getUnderLavaStates();

	/**
	 * Returns the state that will make up the boundary between the land and the lava ocean.
	 */
	protected abstract BlockState getLavaShoreState();
}
