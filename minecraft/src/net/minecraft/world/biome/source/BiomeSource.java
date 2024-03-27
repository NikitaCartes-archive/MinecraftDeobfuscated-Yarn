package net.minecraft.world.biome.source;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public abstract class BiomeSource implements BiomeSupplier {
	public static final Codec<BiomeSource> CODEC = Registries.BIOME_SOURCE.getCodec().dispatchStable(BiomeSource::getCodec, Function.identity());
	private final Supplier<Set<RegistryEntry<Biome>>> biomes = Suppliers.memoize(
		() -> (Set<RegistryEntry<Biome>>)this.biomeStream().distinct().collect(ImmutableSet.toImmutableSet())
	);

	protected BiomeSource() {
	}

	protected abstract MapCodec<? extends BiomeSource> getCodec();

	protected abstract Stream<RegistryEntry<Biome>> biomeStream();

	public Set<RegistryEntry<Biome>> getBiomes() {
		return (Set<RegistryEntry<Biome>>)this.biomes.get();
	}

	public Set<RegistryEntry<Biome>> getBiomesInArea(int x, int y, int z, int radius, MultiNoiseUtil.MultiNoiseSampler sampler) {
		int i = BiomeCoords.fromBlock(x - radius);
		int j = BiomeCoords.fromBlock(y - radius);
		int k = BiomeCoords.fromBlock(z - radius);
		int l = BiomeCoords.fromBlock(x + radius);
		int m = BiomeCoords.fromBlock(y + radius);
		int n = BiomeCoords.fromBlock(z + radius);
		int o = l - i + 1;
		int p = m - j + 1;
		int q = n - k + 1;
		Set<RegistryEntry<Biome>> set = Sets.<RegistryEntry<Biome>>newHashSet();

		for (int r = 0; r < q; r++) {
			for (int s = 0; s < o; s++) {
				for (int t = 0; t < p; t++) {
					int u = i + s;
					int v = j + t;
					int w = k + r;
					set.add(this.getBiome(u, v, w, sampler));
				}
			}
		}

		return set;
	}

	@Nullable
	public Pair<BlockPos, RegistryEntry<Biome>> locateBiome(
		int x, int y, int z, int radius, Predicate<RegistryEntry<Biome>> predicate, Random random, MultiNoiseUtil.MultiNoiseSampler noiseSampler
	) {
		return this.locateBiome(x, y, z, radius, 1, predicate, random, false, noiseSampler);
	}

	@Nullable
	public Pair<BlockPos, RegistryEntry<Biome>> locateBiome(
		BlockPos origin,
		int radius,
		int horizontalBlockCheckInterval,
		int verticalBlockCheckInterval,
		Predicate<RegistryEntry<Biome>> predicate,
		MultiNoiseUtil.MultiNoiseSampler noiseSampler,
		WorldView world
	) {
		Set<RegistryEntry<Biome>> set = (Set<RegistryEntry<Biome>>)this.getBiomes().stream().filter(predicate).collect(Collectors.toUnmodifiableSet());
		if (set.isEmpty()) {
			return null;
		} else {
			int i = Math.floorDiv(radius, horizontalBlockCheckInterval);
			int[] is = MathHelper.stream(origin.getY(), world.getBottomY() + 1, world.getTopY(), verticalBlockCheckInterval).toArray();

			for (BlockPos.Mutable mutable : BlockPos.iterateInSquare(BlockPos.ORIGIN, i, Direction.EAST, Direction.SOUTH)) {
				int j = origin.getX() + mutable.getX() * horizontalBlockCheckInterval;
				int k = origin.getZ() + mutable.getZ() * horizontalBlockCheckInterval;
				int l = BiomeCoords.fromBlock(j);
				int m = BiomeCoords.fromBlock(k);

				for (int n : is) {
					int o = BiomeCoords.fromBlock(n);
					RegistryEntry<Biome> registryEntry = this.getBiome(l, o, m, noiseSampler);
					if (set.contains(registryEntry)) {
						return Pair.of(new BlockPos(j, n, k), registryEntry);
					}
				}
			}

			return null;
		}
	}

	@Nullable
	public Pair<BlockPos, RegistryEntry<Biome>> locateBiome(
		int x,
		int y,
		int z,
		int radius,
		int blockCheckInterval,
		Predicate<RegistryEntry<Biome>> predicate,
		Random random,
		boolean bl,
		MultiNoiseUtil.MultiNoiseSampler noiseSampler
	) {
		int i = BiomeCoords.fromBlock(x);
		int j = BiomeCoords.fromBlock(z);
		int k = BiomeCoords.fromBlock(radius);
		int l = BiomeCoords.fromBlock(y);
		Pair<BlockPos, RegistryEntry<Biome>> pair = null;
		int m = 0;
		int n = bl ? 0 : k;
		int o = n;

		while (o <= k) {
			for (int p = SharedConstants.DEBUG_BIOME_SOURCE ? 0 : -o; p <= o; p += blockCheckInterval) {
				boolean bl2 = Math.abs(p) == o;

				for (int q = -o; q <= o; q += blockCheckInterval) {
					if (bl) {
						boolean bl3 = Math.abs(q) == o;
						if (!bl3 && !bl2) {
							continue;
						}
					}

					int r = i + q;
					int s = j + p;
					RegistryEntry<Biome> registryEntry = this.getBiome(r, l, s, noiseSampler);
					if (predicate.test(registryEntry)) {
						if (pair == null || random.nextInt(m + 1) == 0) {
							BlockPos blockPos = new BlockPos(BiomeCoords.toBlock(r), y, BiomeCoords.toBlock(s));
							if (bl) {
								return Pair.of(blockPos, registryEntry);
							}

							pair = Pair.of(blockPos, registryEntry);
						}

						m++;
					}
				}
			}

			o += blockCheckInterval;
		}

		return pair;
	}

	@Override
	public abstract RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise);

	public void addDebugInfo(List<String> info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
	}
}
