package net.minecraft.world.gen.feature.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.TopologicalSorts;
import net.minecraft.util.Util;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * A class for indexing placed features with a feature order cycle detection.
 * 
 * <p>Placed features have the {@link net.minecraft.world.gen.GenerationStep.Feature
 * generation steps}, and they generate in that order. For example, if biome X registers
 * the placed feature A in the {@link
 * net.minecraft.world.gen.GenerationStep.Feature#UNDERGROUND_ORES} step and the
 * placed feature B and C in the {@link
 * net.minecraft.world.gen.GenerationStep.Feature#TOP_LAYER_MODIFICATION} step,
 * then B and C generate after A. If, then, biome Y registers B in the {@link
 * net.minecraft.world.gen.GenerationStep.Feature#LOCAL_MODIFICATIONS}, this will
 * cause a <strong>"feature order cycle"</strong>, because B should generate after A
 * according to the biome X, but A should generate after B according to biome Y. This
 * is wrong and causes a crash.
 * 
 * <p>In other words, "feature order cycle" occurs when <strong>placed features are
 * registered in multiple generation steps</strong>, due to e.g. reusing vanilla features.
 * To prevent this error, make sure to generate the feature in the same generation step
 * as vanilla, and if that is not possible, create a new feature.
 */
public class PlacedFeatureIndexer {
	/**
	 * {@return the indexed placed features collected after validating feature orders}
	 * 
	 * @throws IllegalStateException when a feature order cycle is detected
	 * 
	 * @apiNote Check the class documentation for what feature order cycle means.
	 * 
	 * @param listInvolvedBiomesOnFailure whether to include involved biomes in the thrown exception
	 * @param biomesToPlacedFeaturesList a function that, given a biome, returns a list of placed features grouped
	 * by their generation steps
	 */
	public static <T> List<PlacedFeatureIndexer.IndexedFeatures> collectIndexedFeatures(
		List<T> biomes, Function<T, List<RegistryEntryList<PlacedFeature>>> biomesToPlacedFeaturesList, boolean listInvolvedBiomesOnFailure
	) {
		Object2IntMap<PlacedFeature> object2IntMap = new Object2IntOpenHashMap<>();
		MutableInt mutableInt = new MutableInt(0);

		record IndexedFeature(int featureIndex, int step, PlacedFeature feature) {
		}

		Comparator<IndexedFeature> comparator = Comparator.comparingInt(IndexedFeature::step).thenComparingInt(IndexedFeature::featureIndex);
		Map<IndexedFeature, Set<IndexedFeature>> map = new TreeMap(comparator);
		int i = 0;

		for (T object : biomes) {
			List<IndexedFeature> list = Lists.<IndexedFeature>newArrayList();
			List<RegistryEntryList<PlacedFeature>> list2 = (List<RegistryEntryList<PlacedFeature>>)biomesToPlacedFeaturesList.apply(object);
			i = Math.max(i, list2.size());

			for (int j = 0; j < list2.size(); j++) {
				for (RegistryEntry<PlacedFeature> registryEntry : (RegistryEntryList)list2.get(j)) {
					PlacedFeature placedFeature = registryEntry.value();
					list.add(
						new IndexedFeature(
							object2IntMap.computeIfAbsent(placedFeature, (Object2IntFunction<? super PlacedFeature>)(feature -> mutableInt.getAndIncrement())), j, placedFeature
						)
					);
				}
			}

			for (int j = 0; j < list.size(); j++) {
				Set<IndexedFeature> set = (Set<IndexedFeature>)map.computeIfAbsent((IndexedFeature)list.get(j), feature -> new TreeSet(comparator));
				if (j < list.size() - 1) {
					set.add((IndexedFeature)list.get(j + 1));
				}
			}
		}

		Set<IndexedFeature> set2 = new TreeSet(comparator);
		Set<IndexedFeature> set3 = new TreeSet(comparator);
		List<IndexedFeature> list = Lists.<IndexedFeature>newArrayList();

		for (IndexedFeature indexedFeature : map.keySet()) {
			if (!set3.isEmpty()) {
				throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
			}

			if (!set2.contains(indexedFeature) && TopologicalSorts.sort(map, set2, set3, list::add, indexedFeature)) {
				if (!listInvolvedBiomesOnFailure) {
					throw new IllegalStateException("Feature order cycle found");
				}

				List<T> list3 = new ArrayList(biomes);

				int k;
				do {
					k = list3.size();
					ListIterator<T> listIterator = list3.listIterator();

					while (listIterator.hasNext()) {
						T object2 = (T)listIterator.next();
						listIterator.remove();

						try {
							collectIndexedFeatures(list3, biomesToPlacedFeaturesList, false);
						} catch (IllegalStateException var18) {
							continue;
						}

						listIterator.add(object2);
					}
				} while (k != list3.size());

				throw new IllegalStateException("Feature order cycle found, involved sources: " + list3);
			}
		}

		Collections.reverse(list);
		Builder<PlacedFeatureIndexer.IndexedFeatures> builder = ImmutableList.builder();

		for (int jx = 0; jx < i; jx++) {
			int l = jx;
			List<PlacedFeature> list4 = (List<PlacedFeature>)list.stream()
				.filter(feature -> feature.step() == l)
				.map(IndexedFeature::feature)
				.collect(Collectors.toList());
			builder.add(new PlacedFeatureIndexer.IndexedFeatures(list4));
		}

		return builder.build();
	}

	public static record IndexedFeatures(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
		IndexedFeatures(List<PlacedFeature> features) {
			this(features, Util.lastIdentityIndexGetter(features));
		}
	}
}
