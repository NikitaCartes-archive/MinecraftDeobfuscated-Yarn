/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature.util;

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
    public static <T> List<IndexedFeatures> collectIndexedFeatures(List<T> biomes, Function<T, List<RegistryEntryList<PlacedFeature>>> biomesToPlacedFeaturesList, boolean listInvolvedBiomesOnFailure) {
        record IndexedFeature(int featureIndex, int step, PlacedFeature feature) {
        }
        ArrayList<IndexedFeature> list;
        Object2IntOpenHashMap<PlacedFeature> object2IntMap = new Object2IntOpenHashMap<PlacedFeature>();
        MutableInt mutableInt = new MutableInt(0);
        Comparator<IndexedFeature> comparator = Comparator.comparingInt(IndexedFeature::step).thenComparingInt(IndexedFeature::featureIndex);
        TreeMap<IndexedFeature, Set> map = new TreeMap<IndexedFeature, Set>(comparator);
        int i = 0;
        for (T object : biomes) {
            int j;
            list = Lists.newArrayList();
            List<RegistryEntryList<PlacedFeature>> list2 = biomesToPlacedFeaturesList.apply(object);
            i = Math.max(i, list2.size());
            for (j = 0; j < list2.size(); ++j) {
                for (RegistryEntry registryEntry : (RegistryEntryList)list2.get(j)) {
                    PlacedFeature placedFeature = (PlacedFeature)registryEntry.value();
                    list.add(new IndexedFeature(object2IntMap.computeIfAbsent(placedFeature, feature -> mutableInt.getAndIncrement()), j, placedFeature));
                }
            }
            for (j = 0; j < list.size(); ++j) {
                Set set = map.computeIfAbsent((IndexedFeature)list.get(j), feature -> new TreeSet(comparator));
                if (j >= list.size() - 1) continue;
                set.add((IndexedFeature)list.get(j + 1));
            }
        }
        TreeSet<IndexedFeature> set2 = new TreeSet<IndexedFeature>(comparator);
        TreeSet<IndexedFeature> set3 = new TreeSet<IndexedFeature>(comparator);
        list = Lists.newArrayList();
        for (IndexedFeature indexedFeature : map.keySet()) {
            if (!set3.isEmpty()) {
                throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
            }
            if (set2.contains(indexedFeature) || !TopologicalSorts.sort(map, set2, set3, list::add, indexedFeature)) continue;
            if (listInvolvedBiomesOnFailure) {
                int k;
                ArrayList<T> list3 = new ArrayList<T>(biomes);
                do {
                    k = list3.size();
                    ListIterator listIterator = list3.listIterator();
                    while (listIterator.hasNext()) {
                        Object object2 = listIterator.next();
                        listIterator.remove();
                        try {
                            PlacedFeatureIndexer.collectIndexedFeatures(list3, biomesToPlacedFeaturesList, false);
                        } catch (IllegalStateException illegalStateException) {
                            continue;
                        }
                        listIterator.add(object2);
                    }
                } while (k != list3.size());
                throw new IllegalStateException("Feature order cycle found, involved sources: " + list3);
            }
            throw new IllegalStateException("Feature order cycle found");
        }
        Collections.reverse(list);
        ImmutableList.Builder builder = ImmutableList.builder();
        int j = 0;
        while (j < i) {
            int l = j++;
            List<PlacedFeature> list4 = list.stream().filter(feature -> feature.step() == l).map(IndexedFeature::feature).collect(Collectors.toList());
            builder.add(new IndexedFeatures(list4));
        }
        return builder.build();
    }

    public record IndexedFeatures(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
        IndexedFeatures(List<PlacedFeature> features) {
            this(features, Util.lastIndexGetter(features, size -> new Object2IntOpenCustomHashMap(size, Util.identityHashStrategy())));
        }
    }
}

