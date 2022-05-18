/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
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
    public static <T> List<IndexedFeatures> method_44210(List<T> list, Function<T, List<RegistryEntryList<PlacedFeature>>> function, boolean bl) {
        record class_6543(int featureIndex, int step, PlacedFeature feature) {
        }
        ArrayList<class_6543> list2;
        Object2IntOpenHashMap<PlacedFeature> object2IntMap = new Object2IntOpenHashMap<PlacedFeature>();
        MutableInt mutableInt = new MutableInt(0);
        Comparator<class_6543> comparator = Comparator.comparingInt(class_6543::step).thenComparingInt(class_6543::featureIndex);
        TreeMap<class_6543, Set> map = new TreeMap<class_6543, Set>(comparator);
        int i = 0;
        for (T object2 : list) {
            int j;
            list2 = Lists.newArrayList();
            List<RegistryEntryList<PlacedFeature>> list3 = function.apply(object2);
            i = Math.max(i, list3.size());
            for (j = 0; j < list3.size(); ++j) {
                for (RegistryEntry registryEntry : (RegistryEntryList)list3.get(j)) {
                    PlacedFeature placedFeature = (PlacedFeature)registryEntry.value();
                    list2.add(new class_6543(object2IntMap.computeIfAbsent(placedFeature, object -> mutableInt.getAndIncrement()), j, placedFeature));
                }
            }
            for (j = 0; j < list2.size(); ++j) {
                Set set = map.computeIfAbsent((class_6543)list2.get(j), arg -> new TreeSet(comparator));
                if (j >= list2.size() - 1) continue;
                set.add((class_6543)list2.get(j + 1));
            }
        }
        TreeSet<class_6543> set2 = new TreeSet<class_6543>(comparator);
        TreeSet<class_6543> set3 = new TreeSet<class_6543>(comparator);
        list2 = Lists.newArrayList();
        for (class_6543 lv : map.keySet()) {
            if (!set3.isEmpty()) {
                throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
            }
            if (set2.contains(lv) || !TopologicalSorts.sort(map, set2, set3, list2::add, lv)) continue;
            if (bl) {
                int k;
                ArrayList<T> list4 = new ArrayList<T>(list);
                do {
                    k = list4.size();
                    ListIterator listIterator = list4.listIterator();
                    while (listIterator.hasNext()) {
                        Object object2 = listIterator.next();
                        listIterator.remove();
                        try {
                            class_7510.method_44210(list4, function, false);
                        } catch (IllegalStateException illegalStateException) {
                            continue;
                        }
                        listIterator.add(object2);
                    }
                } while (k != list4.size());
                throw new IllegalStateException("Feature order cycle found, involved sources: " + list4);
            }
            throw new IllegalStateException("Feature order cycle found");
        }
        Collections.reverse(list2);
        ImmutableList.Builder builder = ImmutableList.builder();
        int j = 0;
        while (j < i) {
            int l = j++;
            List<PlacedFeature> list5 = list2.stream().filter(arg -> arg.step() == l).map(class_6543::feature).collect(Collectors.toList());
            builder.add(new IndexedFeatures(list5));
        }
        return builder.build();
    }

    public record IndexedFeatures(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
        IndexedFeatures(List<PlacedFeature> list) {
            this(list, Util.method_44146(list, i -> new Object2IntOpenCustomHashMap(i, Util.identityHashStrategy())));
        }
    }
}

