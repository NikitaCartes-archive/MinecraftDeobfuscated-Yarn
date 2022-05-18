package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
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
import net.minecraft.util.TopologicalSorts;
import net.minecraft.util.Util;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public class class_7510 {
	public static <T> List<class_7510.IndexedFeatures> method_44210(List<T> list, Function<T, List<RegistryEntryList<PlacedFeature>>> function, boolean bl) {
		Object2IntMap<PlacedFeature> object2IntMap = new Object2IntOpenHashMap<>();
		MutableInt mutableInt = new MutableInt(0);

		record class_6543(int featureIndex, int step, PlacedFeature feature) {
		}

		Comparator<class_6543> comparator = Comparator.comparingInt(class_6543::step).thenComparingInt(class_6543::featureIndex);
		Map<class_6543, Set<class_6543>> map = new TreeMap(comparator);
		int i = 0;

		for (T object : list) {
			List<class_6543> list2 = Lists.<class_6543>newArrayList();
			List<RegistryEntryList<PlacedFeature>> list3 = (List<RegistryEntryList<PlacedFeature>>)function.apply(object);
			i = Math.max(i, list3.size());

			for (int j = 0; j < list3.size(); j++) {
				for (RegistryEntry<PlacedFeature> registryEntry : (RegistryEntryList)list3.get(j)) {
					PlacedFeature placedFeature = registryEntry.value();
					list2.add(
						new class_6543(
							object2IntMap.computeIfAbsent(placedFeature, (Object2IntFunction<? super PlacedFeature>)(objectx -> mutableInt.getAndIncrement())), j, placedFeature
						)
					);
				}
			}

			for (int j = 0; j < list2.size(); j++) {
				Set<class_6543> set = (Set<class_6543>)map.computeIfAbsent((class_6543)list2.get(j), arg -> new TreeSet(comparator));
				if (j < list2.size() - 1) {
					set.add((class_6543)list2.get(j + 1));
				}
			}
		}

		Set<class_6543> set2 = new TreeSet(comparator);
		Set<class_6543> set3 = new TreeSet(comparator);
		List<class_6543> list2 = Lists.<class_6543>newArrayList();

		for (class_6543 lv : map.keySet()) {
			if (!set3.isEmpty()) {
				throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
			}

			if (!set2.contains(lv) && TopologicalSorts.sort(map, set2, set3, list2::add, lv)) {
				if (!bl) {
					throw new IllegalStateException("Feature order cycle found");
				}

				List<T> list4 = new ArrayList(list);

				int k;
				do {
					k = list4.size();
					ListIterator<T> listIterator = list4.listIterator();

					while (listIterator.hasNext()) {
						T object2 = (T)listIterator.next();
						listIterator.remove();

						try {
							method_44210(list4, function, false);
						} catch (IllegalStateException var18) {
							continue;
						}

						listIterator.add(object2);
					}
				} while (k != list4.size());

				throw new IllegalStateException("Feature order cycle found, involved sources: " + list4);
			}
		}

		Collections.reverse(list2);
		Builder<class_7510.IndexedFeatures> builder = ImmutableList.builder();

		for (int jx = 0; jx < i; jx++) {
			int l = jx;
			List<PlacedFeature> list5 = (List<PlacedFeature>)list2.stream().filter(arg -> arg.step() == l).map(class_6543::feature).collect(Collectors.toList());
			builder.add(new class_7510.IndexedFeatures(list5));
		}

		return builder.build();
	}

	public static record IndexedFeatures(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
		IndexedFeatures(List<PlacedFeature> list) {
			this(list, Util.method_44146(list, i -> new Object2IntOpenCustomHashMap(i, Util.identityHashStrategy())));
		}
	}
}
