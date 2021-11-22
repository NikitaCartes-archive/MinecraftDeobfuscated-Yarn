package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class BiomeSource implements BiomeSupplier {
	public static final Codec<BiomeSource> CODEC = Registry.BIOME_SOURCE.getCodec().dispatchStable(BiomeSource::getCodec, Function.identity());
	private final Set<Biome> biomes;
	private final List<BiomeSource.class_6827> field_34469;

	protected BiomeSource(Stream<Supplier<Biome>> stream) {
		this((List<Biome>)stream.map(Supplier::get).distinct().collect(ImmutableList.toImmutableList()));
	}

	protected BiomeSource(List<Biome> biomes) {
		this.biomes = new ObjectLinkedOpenHashSet<>(biomes);
		this.field_34469 = this.method_39525(biomes, true);
	}

	private List<BiomeSource.class_6827> method_39525(List<Biome> biomes, boolean bl) {
		Object2IntMap<PlacedFeature> object2IntMap = new Object2IntOpenHashMap<>();
		MutableInt mutableInt = new MutableInt(0);

		record class_6543(int featureIndex, int step, PlacedFeature feature) {
		}

		Comparator<class_6543> comparator = Comparator.comparingInt(class_6543::step).thenComparingInt(class_6543::featureIndex);
		Map<class_6543, Set<class_6543>> map = new TreeMap(comparator);
		int i = 0;

		for (Biome biome : biomes) {
			List<class_6543> list = Lists.<class_6543>newArrayList();
			List<List<Supplier<PlacedFeature>>> list2 = biome.getGenerationSettings().getFeatures();
			i = Math.max(i, list2.size());

			for (int j = 0; j < list2.size(); j++) {
				for (Supplier<PlacedFeature> supplier : (List)list2.get(j)) {
					PlacedFeature placedFeature = (PlacedFeature)supplier.get();
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
		Builder<BiomeSource.class_6827> builder = ImmutableList.builder();

		for (int jx = 0; jx < i; jx++) {
			int l = jx;
			List<PlacedFeature> list4 = (List<PlacedFeature>)list.stream().filter(arg -> arg.step() == l).map(class_6543::feature).collect(Collectors.toList());
			int m = list4.size();
			Object2IntMap<PlacedFeature> object2IntMap2 = new Object2IntOpenCustomHashMap<>(m, Util.identityHashStrategy());

			for (int n = 0; n < m; n++) {
				object2IntMap2.put((PlacedFeature)list4.get(n), n);
			}

			builder.add(new BiomeSource.class_6827(list4, object2IntMap2));
		}

		return builder.build();
	}

	protected abstract Codec<? extends BiomeSource> getCodec();

	public abstract BiomeSource withSeed(long seed);

	public Set<Biome> getBiomes() {
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

	@Override
	public abstract Biome getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise);

	public void addDebugInfo(List<String> info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
	}

	public List<BiomeSource.class_6827> method_38115() {
		return this.field_34469;
	}

	static {
		Registry.register(Registry.BIOME_SOURCE, "fixed", FixedBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "multi_noise", MultiNoiseBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "checkerboard", CheckerboardBiomeSource.CODEC);
		Registry.register(Registry.BIOME_SOURCE, "the_end", TheEndBiomeSource.CODEC);
	}

	public static record class_6827(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
	}
}
