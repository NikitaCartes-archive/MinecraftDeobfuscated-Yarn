package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet.Builder;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class BiomeSource implements BiomeAccess.Storage {
	public static final Codec<BiomeSource> CODEC = Registry.BIOME_SOURCE.dispatchStable(BiomeSource::getCodec, Function.identity());
	private final ImmutableSet<StructureFeature<?>> structureFeatures;
	private final ImmutableSet<BlockState> topMaterials;
	private final List<Biome> biomes;
	private final ImmutableList<List<ConfiguredFeature<?, ?>>> field_34162;

	protected BiomeSource(Stream<Supplier<Biome>> stream) {
		this((List<Biome>)stream.map(Supplier::get).collect(ImmutableList.toImmutableList()));
	}

	protected BiomeSource(List<Biome> list) {
		this.biomes = list;
		Builder<StructureFeature<?>> builder = ImmutableSet.builder();

		for (StructureFeature<?> structureFeature : Registry.STRUCTURE_FEATURE) {
			if (list.stream().anyMatch(biome -> biome.getGenerationSettings().hasStructureFeature(structureFeature))) {
				builder.add(structureFeature);
			}
		}

		this.structureFeatures = builder.build();
		Builder<BlockState> builder2 = ImmutableSet.builder();

		for (Biome biome : list) {
			builder2.add(biome.getGenerationSettings().getSurfaceConfig().getTopMaterial());
		}

		this.topMaterials = builder2.build();
		Map<Pair<Integer, ConfiguredFeature<?, ?>>, Set<Pair<Integer, ConfiguredFeature<?, ?>>>> map = Maps.<Pair<Integer, ConfiguredFeature<?, ?>>, Set<Pair<Integer, ConfiguredFeature<?, ?>>>>newHashMap();
		int i = 0;

		for (Biome biome2 : list) {
			List<Pair<Integer, ConfiguredFeature<?, ?>>> list2 = Lists.<Pair<Integer, ConfiguredFeature<?, ?>>>newArrayList();
			List<List<Supplier<ConfiguredFeature<?, ?>>>> list3 = biome2.getGenerationSettings().getFeatures();
			i = Math.max(i, list3.size());

			for (int j = 0; j < list3.size(); j++) {
				for (Supplier<ConfiguredFeature<?, ?>> supplier : (List)list3.get(j)) {
					ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature<?, ?>)supplier.get();
					list2.add(Pair.of(j, configuredFeature));
				}
			}

			for (int j = 0; j < list2.size() - 1; j++) {
				((Set)map.computeIfAbsent((Pair)list2.get(j), pair -> Sets.newHashSet())).add((Pair)list2.get(j + 1));
			}
		}

		Set<Pair<Integer, ConfiguredFeature<?, ?>>> set = Sets.<Pair<Integer, ConfiguredFeature<?, ?>>>newHashSet();
		Set<Pair<Integer, ConfiguredFeature<?, ?>>> set2 = Sets.<Pair<Integer, ConfiguredFeature<?, ?>>>newHashSet();
		List<Pair<Integer, ConfiguredFeature<?, ?>>> list2 = Lists.<Pair<Integer, ConfiguredFeature<?, ?>>>newArrayList();

		for (Pair<Integer, ConfiguredFeature<?, ?>> pair : map.keySet()) {
			if (!set2.isEmpty()) {
				throw new IllegalStateException("DFS bork");
			}

			if (!set.contains(pair) && method_37618(map, set, set2, list2, pair)) {
				throw new IllegalStateException("Feature order cycle found: " + (String)set2.stream().map(Object::toString).collect(Collectors.joining(", ")));
			}
		}

		Collections.reverse(list2);
		com.google.common.collect.ImmutableList.Builder<List<ConfiguredFeature<?, ?>>> builder3 = ImmutableList.builder();

		for (int j = 0; j < i; j++) {
			int k = j;
			builder3.add((List<ConfiguredFeature<?, ?>>)list2.stream().filter(pair -> (Integer)pair.getFirst() == k).map(Pair::getSecond).collect(Collectors.toList()));
		}

		this.field_34162 = builder3.build();
	}

	private static <T> boolean method_37618(Map<T, Set<T>> map, Set<T> set, Set<T> set2, List<T> list, T object) {
		if (set.contains(object)) {
			return false;
		} else if (set2.contains(object)) {
			return true;
		} else {
			set2.add(object);

			for (T object2 : (Set)map.getOrDefault(object, ImmutableSet.of())) {
				if (method_37618(map, set, set2, list, object2)) {
					return true;
				}
			}

			set2.remove(object);
			set.add(object);
			list.add(object);
			return false;
		}
	}

	protected abstract Codec<? extends BiomeSource> getCodec();

	public abstract BiomeSource withSeed(long seed);

	public List<Biome> getBiomes() {
		return this.biomes;
	}

	public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
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
					set.add(this.getBiomeForNoiseGen(u, v, w));
				}
			}
		}

		return set;
	}

	@Nullable
	public BlockPos locateBiome(int x, int y, int z, int radius, Predicate<Biome> predicate, Random random) {
		return this.locateBiome(x, y, z, radius, 1, predicate, random, false);
	}

	@Nullable
	public BlockPos locateBiome(int x, int y, int z, int radius, int i, Predicate<Biome> predicate, Random random, boolean bl) {
		int j = BiomeCoords.fromBlock(x);
		int k = BiomeCoords.fromBlock(z);
		int l = BiomeCoords.fromBlock(radius);
		int m = BiomeCoords.fromBlock(y);
		BlockPos blockPos = null;
		int n = 0;
		int o = bl ? 0 : l;
		int p = o;

		while (p <= l) {
			for (int q = SharedConstants.field_34061 ? 0 : -p; q <= p; q += i) {
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
					if (predicate.test(this.getBiomeForNoiseGen(s, m, t))) {
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

	public boolean hasStructureFeature(StructureFeature<?> feature) {
		return this.structureFeatures.contains(feature);
	}

	public boolean method_37614(BlockState blockState) {
		return this.topMaterials.contains(blockState);
	}

	public double[] method_37612(int i, int j) {
		double d = 0.03;
		double e = 342.8571468713332;
		return new double[]{0.03, 342.8571468713332};
	}

	public List<List<ConfiguredFeature<?, ?>>> method_37619() {
		return this.field_34162;
	}

	public void method_37617(List<String> list, BlockPos blockPos) {
	}

	static {
		Registry.register(Registry.BIOME_SOURCE, "fixed", FixedBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "multi_noise", MultiNoiseBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "checkerboard", CheckerboardBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "the_end", TheEndBiomeSource.CODEC);
	}
}
