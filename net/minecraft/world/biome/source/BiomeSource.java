/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
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
import net.minecraft.SharedConstants;
import net.minecraft.util.TopologicalSorts;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public abstract class BiomeSource
implements BiomeSupplier {
    public static final Codec<BiomeSource> CODEC;
    private final Set<RegistryEntry<Biome>> biomes;
    private final Supplier<List<class_6827>> field_34469;

    protected BiomeSource(Stream<RegistryEntry<Biome>> stream) {
        this(stream.distinct().toList());
    }

    protected BiomeSource(List<RegistryEntry<Biome>> biomes) {
        this.biomes = new ObjectLinkedOpenHashSet<RegistryEntry<Biome>>(biomes);
        this.field_34469 = Suppliers.memoize(() -> this.method_39525(biomes.stream().map(RegistryEntry::value).toList(), true));
    }

    private List<class_6827> method_39525(List<Biome> biomes, boolean bl) {
        record class_6543(int featureIndex, int step, PlacedFeature feature) {
        }
        ArrayList<class_6543> list;
        Object2IntOpenHashMap<PlacedFeature> object2IntMap = new Object2IntOpenHashMap<PlacedFeature>();
        MutableInt mutableInt = new MutableInt(0);
        Comparator<class_6543> comparator = Comparator.comparingInt(class_6543::step).thenComparingInt(class_6543::featureIndex);
        TreeMap<class_6543, Set> map = new TreeMap<class_6543, Set>(comparator);
        int i = 0;
        for (Biome biome : biomes) {
            int j;
            list = Lists.newArrayList();
            List<RegistryEntryList<PlacedFeature>> list2 = biome.getGenerationSettings().getFeatures();
            i = Math.max(i, list2.size());
            for (j = 0; j < list2.size(); ++j) {
                for (RegistryEntry registryEntry : (RegistryEntryList)list2.get(j)) {
                    PlacedFeature placedFeature = (PlacedFeature)registryEntry.value();
                    list.add(new class_6543(object2IntMap.computeIfAbsent(placedFeature, object -> mutableInt.getAndIncrement()), j, placedFeature));
                }
            }
            for (j = 0; j < list.size(); ++j) {
                Set set = map.computeIfAbsent((class_6543)list.get(j), arg -> new TreeSet(comparator));
                if (j >= list.size() - 1) continue;
                set.add((class_6543)list.get(j + 1));
            }
        }
        TreeSet<class_6543> set2 = new TreeSet<class_6543>(comparator);
        TreeSet<class_6543> set3 = new TreeSet<class_6543>(comparator);
        list = Lists.newArrayList();
        for (class_6543 lv : map.keySet()) {
            if (!set3.isEmpty()) {
                throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
            }
            if (set2.contains(lv) || !TopologicalSorts.sort(map, set2, set3, list::add, lv)) continue;
            if (bl) {
                int k;
                ArrayList<Biome> list3 = new ArrayList<Biome>(biomes);
                do {
                    k = list3.size();
                    ListIterator<Biome> listIterator = list3.listIterator();
                    while (listIterator.hasNext()) {
                        Biome biome2 = (Biome)listIterator.next();
                        listIterator.remove();
                        try {
                            this.method_39525(list3, false);
                        } catch (IllegalStateException illegalStateException) {
                            continue;
                        }
                        listIterator.add(biome2);
                    }
                } while (k != list3.size());
                throw new IllegalStateException("Feature order cycle found, involved biomes: " + list3);
            }
            throw new IllegalStateException("Feature order cycle found");
        }
        Collections.reverse(list);
        ImmutableList.Builder builder = ImmutableList.builder();
        for (int j = 0; j < i; ++j) {
            int l = j;
            List<PlacedFeature> list4 = list.stream().filter(arg -> arg.step() == l).map(class_6543::feature).collect(Collectors.toList());
            int m = list4.size();
            Object2IntOpenCustomHashMap<PlacedFeature> object2IntMap2 = new Object2IntOpenCustomHashMap<PlacedFeature>(m, Util.identityHashStrategy());
            for (int n = 0; n < m; ++n) {
                object2IntMap2.put((PlacedFeature)list4.get(n), n);
            }
            builder.add(new class_6827(list4, object2IntMap2));
        }
        return builder.build();
    }

    protected abstract Codec<? extends BiomeSource> getCodec();

    public abstract BiomeSource withSeed(long var1);

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
        HashSet<RegistryEntry<Biome>> set = Sets.newHashSet();
        for (int r = 0; r < q; ++r) {
            for (int s = 0; s < o; ++s) {
                for (int t = 0; t < p; ++t) {
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
    public BlockPos locateBiome(int x, int y, int z, int radius, Predicate<RegistryEntry<Biome>> predicate, Random random, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
        return this.locateBiome(x, y, z, radius, 1, predicate, random, false, noiseSampler);
    }

    @Nullable
    public BlockPos locateBiome(int x, int y, int z, int radius, int blockCheckInterval, Predicate<RegistryEntry<Biome>> predicate, Random random, boolean bl, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
        int n;
        int i = BiomeCoords.fromBlock(x);
        int j = BiomeCoords.fromBlock(z);
        int k = BiomeCoords.fromBlock(radius);
        int l = BiomeCoords.fromBlock(y);
        BlockPos blockPos = null;
        int m = 0;
        for (int o = n = bl ? 0 : k; o <= k; o += blockCheckInterval) {
            int p;
            int n2 = p = SharedConstants.DEBUG_BIOME_SOURCE ? 0 : -o;
            while (p <= o) {
                boolean bl2 = Math.abs(p) == o;
                for (int q = -o; q <= o; q += blockCheckInterval) {
                    int s;
                    int r;
                    if (bl) {
                        boolean bl3;
                        boolean bl4 = bl3 = Math.abs(q) == o;
                        if (!bl3 && !bl2) continue;
                    }
                    if (!predicate.test(this.getBiome(r = i + q, l, s = j + p, noiseSampler))) continue;
                    if (blockPos == null || random.nextInt(m + 1) == 0) {
                        blockPos = new BlockPos(BiomeCoords.toBlock(r), y, BiomeCoords.toBlock(s));
                        if (bl) {
                            return blockPos;
                        }
                    }
                    ++m;
                }
                p += blockCheckInterval;
            }
        }
        return blockPos;
    }

    @Override
    public abstract RegistryEntry<Biome> getBiome(int var1, int var2, int var3, MultiNoiseUtil.MultiNoiseSampler var4);

    public void addDebugInfo(List<String> info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
    }

    public List<class_6827> method_38115() {
        return this.field_34469.get();
    }

    static {
        Registry.register(Registry.BIOME_SOURCE, "fixed", FixedBiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "multi_noise", MultiNoiseBiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "checkerboard", CheckerboardBiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "the_end", TheEndBiomeSource.CODEC);
        CODEC = Registry.BIOME_SOURCE.getCodec().dispatchStable(BiomeSource::getCodec, Function.identity());
    }

    public record class_6827(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
    }
}

