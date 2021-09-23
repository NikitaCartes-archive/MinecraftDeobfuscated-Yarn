package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.class_6496;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public abstract class BiomeSource {
	public static final Codec<BiomeSource> CODEC = Registry.BIOME_SOURCE.dispatchStable(BiomeSource::getCodec, Function.identity());
	private final ImmutableSet<BlockState> topMaterials;
	private final List<Biome> biomes;
	private final ImmutableList<ImmutableList<ConfiguredFeature<?, ?>>> field_34469;

	protected BiomeSource(Stream<Supplier<Biome>> stream) {
		this((List<Biome>)stream.map(Supplier::get).collect(ImmutableList.toImmutableList()));
	}

	protected BiomeSource(List<Biome> list) {
		this.biomes = list;

		record class_6543() {
			private final int step;
			private final ConfiguredFeature<?, ?> feature;

			class_6543(int i, ConfiguredFeature<?, ?> configuredFeature) {
				this.step = i;
				this.feature = configuredFeature;
			}
		}

		this.topMaterials = (ImmutableSet<BlockState>)list.stream()
			.map(biome -> biome.getGenerationSettings().getSurfaceConfig().getTopMaterial())
			.collect(ImmutableSet.toImmutableSet());
		Map<class_6543, Set<class_6543>> map = Maps.<class_6543, Set<class_6543>>newHashMap();
		int i = 0;

		for (Biome biome : list) {
			List<class_6543> list2 = Lists.<class_6543>newArrayList();
			List<List<Supplier<ConfiguredFeature<?, ?>>>> list3 = biome.getGenerationSettings().getFeatures();
			i = Math.max(i, list3.size());

			for (int j = 0; j < list3.size(); j++) {
				for (Supplier<ConfiguredFeature<?, ?>> supplier : (List)list3.get(j)) {
					list2.add(new class_6543(j, (ConfiguredFeature<?, ?>)supplier.get()));
				}
			}

			for (int j = 0; j < list2.size(); j++) {
				Set<class_6543> set = (Set<class_6543>)map.computeIfAbsent((class_6543)list2.get(j), arg -> Sets.newHashSet());
				if (j < list2.size() - 1) {
					set.add((class_6543)list2.get(j + 1));
				}
			}
		}

		Set<class_6543> set2 = Sets.<class_6543>newHashSet();
		Set<class_6543> set3 = Sets.<class_6543>newHashSet();
		List<class_6543> list2 = Lists.<class_6543>newArrayList();

		for (class_6543 lv : map.keySet()) {
			if (!set3.isEmpty()) {
				throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
			}

			if (!set2.contains(lv) && class_6496.method_37951(map, set2, set3, list2::add, lv)) {
				Collections.reverse(list2);
				throw new IllegalStateException(
					"Feature order cycle found: " + (String)list2.stream().filter(set3::contains).map(Object::toString).collect(Collectors.joining(", "))
				);
			}
		}

		Collections.reverse(list2);
		Builder<ImmutableList<ConfiguredFeature<?, ?>>> builder = ImmutableList.builder();

		for (int jx = 0; jx < i; jx++) {
			int k = jx;
			builder.add(
				(ImmutableList<ConfiguredFeature<?, ?>>)list2.stream().filter(arg -> arg.step() == k).map(class_6543::feature).collect(ImmutableList.toImmutableList())
			);
		}

		this.field_34469 = builder.build();
	}

	protected abstract Codec<? extends BiomeSource> getCodec();

	public abstract BiomeSource withSeed(long seed);

	public List<Biome> getBiomes() {
		return this.biomes;
	}

	public Set<Biome> getBiomesInArea(int x, int y, int z, int radius, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler) {
		int i = BiomeCoords.fromBlock(x - radius);
		int j = BiomeCoords.fromBlock(y - radius);
		int k = BiomeCoords.fromBlock(z - radius);
		int l = BiomeCoords.fromBlock(x + radius);
		int m = BiomeCoords.fromBlock(y + radius);
		int n = BiomeCoords.fromBlock(z + radius);
		int o = l - i + 1;
		int p = m - j + 1;
		int q = n - k + 1;
		Set<Biome> set = Sets.<Biome>newHashSet();

		for (int r = 0; r < q; r++) {
			for (int s = 0; s < o; s++) {
				for (int t = 0; t < p; t++) {
					int u = i + s;
					int v = j + t;
					int w = k + r;
					set.add(this.getBiome(u, v, w, multiNoiseSampler));
				}
			}
		}

		return set;
	}

	@Nullable
	public BlockPos locateBiome(int x, int y, int z, int radius, Predicate<Biome> predicate, Random random, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
		return this.locateBiome(x, y, z, radius, 1, predicate, random, false, noiseSampler);
	}

	@Nullable
	public BlockPos locateBiome(
		int x, int y, int z, int radius, int i, Predicate<Biome> predicate, Random random, boolean bl, MultiNoiseUtil.MultiNoiseSampler noiseSampler
	) {
		int j = BiomeCoords.fromBlock(x);
		int k = BiomeCoords.fromBlock(z);
		int l = BiomeCoords.fromBlock(radius);
		int m = BiomeCoords.fromBlock(y);
		BlockPos blockPos = null;
		int n = 0;
		int o = bl ? 0 : l;
		int p = o;

		while (p <= l) {
			for (int q = SharedConstants.DEBUG_BIOME_SOURCE ? 0 : -p; q <= p; q += i) {
				boolean bl2 = Math.abs(q) == p;

				for (int r = -p; r <= p; r += i) {
					if (bl) {
						boolean bl3 = Math.abs(r) == p;
						if (!bl3 && !bl2) {
							continue;
						}
					}

					int s = j + r;
					int t = k + q;
					if (predicate.test(this.getBiome(s, m, t, noiseSampler))) {
						if (blockPos == null || random.nextInt(n + 1) == 0) {
							blockPos = new BlockPos(BiomeCoords.toBlock(s), y, BiomeCoords.toBlock(t));
							if (bl) {
								return blockPos;
							}
						}

						n++;
					}
				}
			}

			p += i;
		}

		return blockPos;
	}

	public abstract Biome getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noiseSampler);

	public boolean method_38113(BlockState blockState) {
		return this.topMaterials.contains(blockState);
	}

	public void addDebugInfo(List<String> info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
	}

	public ImmutableList<ImmutableList<ConfiguredFeature<?, ?>>> method_38115() {
		return this.field_34469;
	}

	static {
		Registry.register(Registry.BIOME_SOURCE, "fixed", FixedBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "multi_noise", MultiNoiseBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "checkerboard", CheckerboardBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "the_end", TheEndBiomeSource.CODEC);
	}
}
