/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.class_6496;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.CheckerboardBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public abstract class BiomeSource {
    public static final Codec<BiomeSource> CODEC;
    private final ImmutableSet<BlockState> topMaterials;
    private final List<Biome> biomes;
    private final ImmutableList<ImmutableList<ConfiguredFeature<?, ?>>> field_34469;

    protected BiomeSource(Stream<Supplier<Biome>> stream) {
        this(stream.map(Supplier::get).collect(ImmutableList.toImmutableList()));
    }

    protected BiomeSource(List<Biome> list) {
        record class_6543(int step, ConfiguredFeature<?, ?> feature) {
        }
        ArrayList<class_6543> list2;
        this.biomes = list;
        this.topMaterials = list.stream().map(biome -> biome.getGenerationSettings().getSurfaceConfig().getTopMaterial()).collect(ImmutableSet.toImmutableSet());
        HashMap<class_6543, Set> map = Maps.newHashMap();
        int i = 0;
        for (Biome biome2 : list) {
            int j;
            list2 = Lists.newArrayList();
            List<List<Supplier<ConfiguredFeature<?, ?>>>> list3 = biome2.getGenerationSettings().getFeatures();
            i = Math.max(i, list3.size());
            for (j = 0; j < list3.size(); ++j) {
                for (Supplier supplier : (List)list3.get(j)) {
                    list2.add(new class_6543(j, (ConfiguredFeature)supplier.get()));
                }
            }
            for (j = 0; j < list2.size(); ++j) {
                Set set = map.computeIfAbsent((class_6543)list2.get(j), arg -> Sets.newHashSet());
                if (j >= list2.size() - 1) continue;
                set.add((class_6543)list2.get(j + 1));
            }
        }
        HashSet set2 = Sets.newHashSet();
        HashSet set3 = Sets.newHashSet();
        list2 = Lists.newArrayList();
        for (class_6543 lv : map.keySet()) {
            if (!set3.isEmpty()) {
                throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
            }
            if (set2.contains(lv) || !class_6496.method_37951(map, set2, set3, list2::add, lv)) continue;
            Collections.reverse(list2);
            throw new IllegalStateException("Feature order cycle found: " + list2.stream().filter(set3::contains).map(Object::toString).collect(Collectors.joining(", ")));
        }
        Collections.reverse(list2);
        ImmutableList.Builder builder = ImmutableList.builder();
        int j = 0;
        while (j < i) {
            int k = j++;
            builder.add(list2.stream().filter(arg -> arg.step() == k).map(class_6543::feature).collect(ImmutableList.toImmutableList()));
        }
        this.field_34469 = builder.build();
    }

    protected abstract Codec<? extends BiomeSource> getCodec();

    public abstract BiomeSource withSeed(long var1);

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
        HashSet<Biome> set = Sets.newHashSet();
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
    public BlockPos locateBiome(int x, int y, int z, int radius, Predicate<Biome> predicate, Random random, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
        return this.locateBiome(x, y, z, radius, 1, predicate, random, false, noiseSampler);
    }

    @Nullable
    public BlockPos locateBiome(int x, int y, int z, int radius, int i, Predicate<Biome> predicate, Random random, boolean bl, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
        int o;
        int j = BiomeCoords.fromBlock(x);
        int k = BiomeCoords.fromBlock(z);
        int l = BiomeCoords.fromBlock(radius);
        int m = BiomeCoords.fromBlock(y);
        BlockPos blockPos = null;
        int n = 0;
        for (int p = o = bl ? 0 : l; p <= l; p += i) {
            int q;
            int n2 = q = SharedConstants.DEBUG_BIOME_SOURCE ? 0 : -p;
            while (q <= p) {
                boolean bl2 = Math.abs(q) == p;
                for (int r = -p; r <= p; r += i) {
                    int t;
                    int s;
                    if (bl) {
                        boolean bl3;
                        boolean bl4 = bl3 = Math.abs(r) == p;
                        if (!bl3 && !bl2) continue;
                    }
                    if (!predicate.test(this.getBiome(s = j + r, m, t = k + q, noiseSampler))) continue;
                    if (blockPos == null || random.nextInt(n + 1) == 0) {
                        blockPos = new BlockPos(BiomeCoords.toBlock(s), y, BiomeCoords.toBlock(t));
                        if (bl) {
                            return blockPos;
                        }
                    }
                    ++n;
                }
                q += i;
            }
        }
        return blockPos;
    }

    public abstract Biome getBiome(int var1, int var2, int var3, MultiNoiseUtil.MultiNoiseSampler var4);

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
        CODEC = Registry.BIOME_SOURCE.dispatchStable(BiomeSource::getCodec, Function.identity());
    }
}

