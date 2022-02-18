package net.minecraft.world.biome.source;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.util.TopologicalSorts;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class BiomeSource implements BiomeSupplier {
	public static final Codec<BiomeSource> CODEC = Registry.BIOME_SOURCE.getCodec().dispatchStable(BiomeSource::getCodec, Function.identity());
	private final Set<RegistryEntry<Biome>> biomes;
	private final Supplier<List<BiomeSource.IndexedFeatures>> indexedFeaturesSupplier;

	protected BiomeSource(Stream<RegistryEntry<Biome>> biomeStream) {
		this(biomeStream.distinct().toList());
	}

	protected BiomeSource(List<RegistryEntry<Biome>> biomes) {
		this.biomes = new ObjectLinkedOpenHashSet<>(biomes);
		this.indexedFeaturesSupplier = Suppliers.memoize(() -> this.method_39525(biomes.stream().map(RegistryEntry::value).toList(), true));
	}

	private List<BiomeSource.IndexedFeatures> method_39525(List<Biome> biomes, boolean bl) {
		Object2IntMap<PlacedFeature> object2IntMap = new Object2IntOpenHashMap<>();
		MutableInt mutableInt = new MutableInt(0);

		record class_6543(int featureIndex, int step, PlacedFeature feature) {
		}

		Comparator<class_6543> comparator = Comparator.comparingInt(class_6543::step).thenComparingInt(class_6543::featureIndex);
		Map<class_6543, Set<class_6543>> map = new TreeMap(comparator);
		int i = 0;

		for (Biome biome : biomes) {
			List<class_6543> list = Lists.<class_6543>newArrayList();
			List<RegistryEntryList<PlacedFeature>> list2 = biome.getGenerationSettings().getFeatures();
			i = Math.max(i, list2.size());

			for (int j = 0; j < list2.size(); j++) {
				for (RegistryEntry<PlacedFeature> registryEntry : (RegistryEntryList)list2.get(j)) {
					PlacedFeature placedFeature = registryEntry.value();
					list.add(
						new class_6543(
							object2IntMap.computeIfAbsent(placedFeature, (Object2IntFunction<? super PlacedFeature>)(object -> mutableInt.getAndIncrement())), j, placedFeature
						)
					);
				}
			}

			for (int j = 0; j < list.size(); j++) {
				Set<class_6543> set = (Set<class_6543>)map.computeIfAbsent((class_6543)list.get(j), arg -> new TreeSet(comparator));
				if (j < list.size() - 1) {
					set.add((class_6543)list.get(j + 1));
				}
			}
		}

		Set<class_6543> set2 = new TreeSet(comparator);
		Set<class_6543> set3 = new TreeSet(comparator);
		List<class_6543> list = Lists.<class_6543>newArrayList();

		for (class_6543 lv : map.keySet()) {
			if (!set3.isEmpty()) {
				throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
			}

			if (!set2.contains(lv) && TopologicalSorts.sort(map, set2, set3, list::add, lv)) {
				if (!bl) {
					throw new IllegalStateException("Feature order cycle found");
				}

				List<Biome> list3 = new ArrayList(biomes);

				int k;
				do {
					k = list3.size();
					ListIterator<Biome> listIterator = list3.listIterator();

					while (listIterator.hasNext()) {
						Biome biome2 = (Biome)listIterator.next();
						listIterator.remove();

						try {
							this.method_39525(list3, false);
						} catch (IllegalStateException var18) {
							continue;
						}

						listIterator.add(biome2);
					}
				} while (k != list3.size());

				throw new IllegalStateException("Feature order cycle found, involved biomes: " + list3);
			}
		}

		Collections.reverse(list);
		Builder<BiomeSource.IndexedFeatures> builder = ImmutableList.builder();

		for (int jx = 0; jx < i; jx++) {
			int l = jx;
			List<PlacedFeature> list4 = (List<PlacedFeature>)list.stream().filter(arg -> arg.step() == l).map(class_6543::feature).collect(Collectors.toList());
			int m = list4.size();
			Object2IntMap<PlacedFeature> object2IntMap2 = new Object2IntOpenCustomHashMap<>(m, Util.identityHashStrategy());

			for (int n = 0; n < m; n++) {
				object2IntMap2.put((PlacedFeature)list4.get(n), n);
			}

			builder.add(new BiomeSource.IndexedFeatures(list4, object2IntMap2));
		}

		return builder.build();
	}

	protected abstract Codec<? extends BiomeSource> getCodec();

	public abstract BiomeSource withSeed(long seed);

	public Set<RegistryEntry<Biome>> getBiomes() {
		return this.biomes;
	}

	public Set<RegistryEntry<Biome>> getBiomesInArea(int x, int y, int z, int radius, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler) {
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
					set.add(this.getBiome(u, v, w, multiNoiseSampler));
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

	public List<BiomeSource.IndexedFeatures> getIndexedFeatures() {
		return (List<BiomeSource.IndexedFeatures>)this.indexedFeaturesSupplier.get();
	}

	static {
		Registry.register(Registry.BIOME_SOURCE, "fixed", FixedBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "multi_noise", MultiNoiseBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "checkerboard", CheckerboardBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "the_end", TheEndBiomeSource.CODEC);
	}

	public static record IndexedFeatures(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
	}
}
