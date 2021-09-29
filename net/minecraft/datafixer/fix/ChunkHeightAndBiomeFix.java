/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ChunkHeightAndBiomeFix
extends DataFix {
    public static final String field_35015 = "__dimension";
    private static final String field_35017 = "ChunkHeightAndBiomeFix";
    private static final int field_35018 = 16;
    private static final int field_35019 = 24;
    private static final int field_35020 = -4;
    private static final int field_35021 = 64;
    private static final int field_35022 = 9;
    private static final long field_35023 = 511L;
    private static final int field_35024 = 64;
    private static final String[] field_35025 = new String[]{"WORLD_SURFACE_WG", "WORLD_SURFACE", "WORLD_SURFACE_IGNORE_SNOW", "OCEAN_FLOOR_WG", "OCEAN_FLOOR", "MOTION_BLOCKING", "MOTION_BLOCKING_NO_LEAVES"};
    private static final int field_35026 = 16;
    private static final int field_35027 = 64;
    private static final int field_35028 = 1008;
    public static final String PLAINS_ID = "minecraft:plains";
    private static final Int2ObjectMap<String> RAW_BIOME_IDS = new Int2ObjectOpenHashMap<String>();

    public ChunkHeightAndBiomeFix(Schema schema) {
        super(schema, true);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
        OpticFinder<?> opticFinder = type.findField("Level");
        OpticFinder<?> opticFinder2 = opticFinder.type().findField("Sections");
        Schema schema = this.getOutputSchema();
        Type<?> type2 = schema.getType(TypeReferences.CHUNK);
        Type<?> type3 = type2.findField("Level").type();
        Type<?> type4 = type3.findField("Sections").type();
        return this.fixTypeEverywhereTyped(field_35017, type, type2, (Typed<?> typed) -> typed.updateTyped(opticFinder, type3, typed2 -> {
            Dynamic dynamic2 = typed2.get(DSL.remainderFinder());
            String string = dynamic2.get(field_35015).asString().result().orElse("");
            boolean bl = "minecraft:overworld".equals(string);
            MutableBoolean mutableBoolean = new MutableBoolean();
            int i = bl ? -4 : 0;
            Dynamic[] dynamics = ChunkHeightAndBiomeFix.method_38806(dynamic2, bl, i, mutableBoolean);
            Dynamic<?> dynamic22 = ChunkHeightAndBiomeFix.method_38816(dynamic2.createList(Stream.of(dynamic2.createMap(ImmutableMap.of(dynamic2.createString("Name"), dynamic2.createString("minecraft:air"))))));
            typed2 = typed2.updateTyped(opticFinder2, type4, typed -> {
                IntOpenHashSet intSet = new IntOpenHashSet();
                Dynamic<?> dynamic3 = typed.write().result().orElseThrow(() -> new IllegalStateException("Malformed Chunk.Level.Sections"));
                List list = dynamic3.asStream().map(dynamic2 -> {
                    int j = dynamic2.get("Y").asInt(0);
                    Dynamic dynamic3 = DataFixUtils.orElse(dynamic2.get("Palette").result().flatMap(dynamic22 -> dynamic2.get("BlockStates").result().map(dynamic2 -> ChunkHeightAndBiomeFix.method_38813(dynamic22, dynamic2))), dynamic22);
                    Dynamic dynamic4 = dynamic2;
                    int k = j - i;
                    if (k >= 0 && k < dynamics.length) {
                        dynamic4 = dynamic4.set("biomes", dynamics[k]);
                    }
                    intSet.add(j);
                    return dynamic4.set("block_states", dynamic3).remove("Palette").remove("BlockStates");
                }).collect(Collectors.toCollection(ArrayList::new));
                for (int j = 0; j < dynamics.length; ++j) {
                    int k = j + i;
                    if (!intSet.add(k)) continue;
                    Dynamic dynamic4 = dynamic2.createMap(Map.of(dynamic2.createString("Y"), dynamic2.createInt(k)));
                    dynamic4 = dynamic4.set("block_states", dynamic22);
                    dynamic4 = dynamic4.set("biomes", dynamics[j]);
                    list.add(dynamic4);
                }
                return type4.readTyped(dynamic2.createList(list.stream())).result().orElseThrow(() -> new IllegalStateException("ChunkHeightAndBiomeFix failed.")).getFirst();
            });
            return typed2.update(DSL.remainderFinder(), dynamic -> ChunkHeightAndBiomeFix.method_38807(dynamic, bl, mutableBoolean.booleanValue()));
        }));
    }

    private static Dynamic<?>[] method_38806(Dynamic<?> dynamic, boolean bl, int i2, MutableBoolean mutableBoolean) {
        Object[] dynamics = new Dynamic[bl ? 24 : 16];
        Optional<IntStream> optional = dynamic.get("Biomes").asIntStreamOpt().result();
        if (optional.isPresent()) {
            int[] is = optional.get().toArray();
            mutableBoolean.setValue(is.length == 1536);
            if (mutableBoolean.booleanValue()) {
                for (int j2 = 0; j2 < 24; ++j2) {
                    int k = j2;
                    dynamics[j2] = ChunkHeightAndBiomeFix.method_38803(dynamic, j -> is[k * 64 + j]);
                }
            } else {
                int l;
                int j3 = 0;
                while (j3 < 16) {
                    int k = j3 - i2;
                    l = j3++;
                    dynamics[k] = ChunkHeightAndBiomeFix.method_38803(dynamic, j -> is[l * 64 + j]);
                }
                if (bl) {
                    Dynamic<?> dynamic2 = ChunkHeightAndBiomeFix.method_38803(dynamic, i -> is[i % 16]);
                    Dynamic<?> dynamic3 = ChunkHeightAndBiomeFix.method_38803(dynamic, i -> is[i % 16 + 1008]);
                    for (l = 0; l < 4; ++l) {
                        dynamics[l] = dynamic2;
                    }
                    for (l = 20; l < 24; ++l) {
                        dynamics[l] = dynamic3;
                    }
                }
            }
        } else {
            Arrays.fill(dynamics, ChunkHeightAndBiomeFix.method_38816(dynamic.createList(Stream.of(dynamic.createString(PLAINS_ID)))));
        }
        return dynamics;
    }

    private static Dynamic<?> method_38807(Dynamic<?> dynamic, boolean bl, boolean bl2) {
        dynamic = dynamic.remove("Biomes");
        if (!bl) {
            return ChunkHeightAndBiomeFix.method_38799(dynamic, 16, 0);
        }
        if (bl2) {
            return ChunkHeightAndBiomeFix.method_38799(dynamic, 24, 0);
        }
        dynamic = ChunkHeightAndBiomeFix.method_38798(dynamic);
        dynamic = ChunkHeightAndBiomeFix.method_38805(dynamic, "Lights");
        dynamic = ChunkHeightAndBiomeFix.method_38805(dynamic, "LiquidsToBeTicked");
        dynamic = ChunkHeightAndBiomeFix.method_38805(dynamic, "PostProcessing");
        dynamic = ChunkHeightAndBiomeFix.method_38805(dynamic, "ToBeTicked");
        dynamic = ChunkHeightAndBiomeFix.method_38799(dynamic, 24, 4);
        return dynamic;
    }

    private static Dynamic<?> method_38799(Dynamic<?> dynamic, int i, int j) {
        Dynamic<?> dynamic2 = dynamic.get("CarvingMasks").orElseEmptyMap();
        dynamic2 = dynamic2.updateMapValues(pair -> {
            long[] ls = BitSet.valueOf(((Dynamic)pair.getSecond()).asByteBuffer().array()).toLongArray();
            long[] ms = new long[64 * i];
            System.arraycopy(ls, 0, ms, 64 * j, ls.length);
            return Pair.of((Dynamic)pair.getFirst(), dynamic.createLongList(LongStream.of(ms)));
        });
        return dynamic.set("CarvingMasks", dynamic2);
    }

    private static Dynamic<?> method_38805(Dynamic<?> dynamic, String string) {
        List list = dynamic.get(string).orElseEmptyList().asStream().collect(Collectors.toCollection(ArrayList::new));
        if (list.size() == 24) {
            return dynamic;
        }
        Dynamic dynamic2 = dynamic.emptyList();
        for (int i = 0; i < 4; ++i) {
            list.add(0, dynamic2);
            list.add(dynamic2);
        }
        return dynamic.set(string, dynamic.createList(list.stream()));
    }

    private static Dynamic<?> method_38798(Dynamic<?> dynamic2) {
        return dynamic2.update("Heightmaps", dynamic -> {
            for (String string : field_35025) {
                dynamic = dynamic.update(string, ChunkHeightAndBiomeFix::method_38812);
            }
            return dynamic;
        });
    }

    private static Dynamic<?> method_38812(Dynamic<?> dynamic) {
        return dynamic.createLongList(dynamic.asLongStream().map(l -> {
            long m = 0L;
            int i = 0;
            while (i + 9 <= 64) {
                long n = l >> i & 0x1FFL;
                long o = n == 0L ? 0L : Math.min(n + 64L, 511L);
                m |= o << i;
                i += 9;
            }
            return m;
        }));
    }

    private static Dynamic<?> method_38803(Dynamic<?> dynamic, Int2IntFunction int2IntFunction) {
        int j;
        Int2IntLinkedOpenHashMap int2IntMap = new Int2IntLinkedOpenHashMap();
        for (int i = 0; i < 64; ++i) {
            j = int2IntFunction.applyAsInt(i);
            if (int2IntMap.containsKey(j)) continue;
            int2IntMap.put(j, int2IntMap.size());
        }
        Dynamic dynamic2 = dynamic.createList(int2IntMap.keySet().stream().map(integer -> dynamic.createString(RAW_BIOME_IDS.getOrDefault((int)integer, PLAINS_ID))));
        j = ChunkHeightAndBiomeFix.method_38793(int2IntMap.size());
        if (j == 0) {
            return ChunkHeightAndBiomeFix.method_38816(dynamic2);
        }
        int k = 64 / j;
        int l = (64 + k - 1) / k;
        long[] ls = new long[l];
        int m = 0;
        int n = 0;
        for (int o = 0; o < 64; ++o) {
            int p = int2IntFunction.applyAsInt(o);
            int n2 = m++;
            ls[n2] = ls[n2] | (long)int2IntMap.get(p) << n;
            if ((n += j) + j <= 64) continue;
            n = 0;
        }
        Dynamic<?> dynamic3 = dynamic.createLongList(Arrays.stream(ls));
        return ChunkHeightAndBiomeFix.method_38802(dynamic2, dynamic3);
    }

    private static Dynamic<?> method_38816(Dynamic<?> dynamic) {
        return dynamic.createMap(ImmutableMap.of(dynamic.createString("palette"), dynamic));
    }

    private static Dynamic<?> method_38802(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
        return dynamic.createMap(ImmutableMap.of(dynamic.createString("palette"), dynamic, dynamic.createString("data"), dynamic2));
    }

    private static Dynamic<?> method_38813(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
        if (dynamic.asStream().count() == 1L) {
            return ChunkHeightAndBiomeFix.method_38816(dynamic);
        }
        return ChunkHeightAndBiomeFix.method_38802(dynamic, dynamic2);
    }

    public static int method_38793(int i) {
        if (i == 0) {
            return 0;
        }
        return (int)Math.ceil(Math.log(i) / Math.log(2.0));
    }

    static {
        RAW_BIOME_IDS.put(0, "minecraft:ocean");
        RAW_BIOME_IDS.put(1, PLAINS_ID);
        RAW_BIOME_IDS.put(2, "minecraft:desert");
        RAW_BIOME_IDS.put(3, "minecraft:mountains");
        RAW_BIOME_IDS.put(4, "minecraft:forest");
        RAW_BIOME_IDS.put(5, "minecraft:taiga");
        RAW_BIOME_IDS.put(6, "minecraft:swamp");
        RAW_BIOME_IDS.put(7, "minecraft:river");
        RAW_BIOME_IDS.put(8, "minecraft:nether_wastes");
        RAW_BIOME_IDS.put(9, "minecraft:the_end");
        RAW_BIOME_IDS.put(10, "minecraft:frozen_ocean");
        RAW_BIOME_IDS.put(11, "minecraft:frozen_river");
        RAW_BIOME_IDS.put(12, "minecraft:snowy_tundra");
        RAW_BIOME_IDS.put(13, "minecraft:snowy_mountains");
        RAW_BIOME_IDS.put(14, "minecraft:mushroom_fields");
        RAW_BIOME_IDS.put(15, "minecraft:mushroom_field_shore");
        RAW_BIOME_IDS.put(16, "minecraft:beach");
        RAW_BIOME_IDS.put(17, "minecraft:desert_hills");
        RAW_BIOME_IDS.put(18, "minecraft:wooded_hills");
        RAW_BIOME_IDS.put(19, "minecraft:taiga_hills");
        RAW_BIOME_IDS.put(20, "minecraft:mountain_edge");
        RAW_BIOME_IDS.put(21, "minecraft:jungle");
        RAW_BIOME_IDS.put(22, "minecraft:jungle_hills");
        RAW_BIOME_IDS.put(23, "minecraft:jungle_edge");
        RAW_BIOME_IDS.put(24, "minecraft:deep_ocean");
        RAW_BIOME_IDS.put(25, "minecraft:stone_shore");
        RAW_BIOME_IDS.put(26, "minecraft:snowy_beach");
        RAW_BIOME_IDS.put(27, "minecraft:birch_forest");
        RAW_BIOME_IDS.put(28, "minecraft:birch_forest_hills");
        RAW_BIOME_IDS.put(29, "minecraft:dark_forest");
        RAW_BIOME_IDS.put(30, "minecraft:snowy_taiga");
        RAW_BIOME_IDS.put(31, "minecraft:snowy_taiga_hills");
        RAW_BIOME_IDS.put(32, "minecraft:giant_tree_taiga");
        RAW_BIOME_IDS.put(33, "minecraft:giant_tree_taiga_hills");
        RAW_BIOME_IDS.put(34, "minecraft:wooded_mountains");
        RAW_BIOME_IDS.put(35, "minecraft:savanna");
        RAW_BIOME_IDS.put(36, "minecraft:savanna_plateau");
        RAW_BIOME_IDS.put(37, "minecraft:badlands");
        RAW_BIOME_IDS.put(38, "minecraft:wooded_badlands_plateau");
        RAW_BIOME_IDS.put(39, "minecraft:badlands_plateau");
        RAW_BIOME_IDS.put(40, "minecraft:small_end_islands");
        RAW_BIOME_IDS.put(41, "minecraft:end_midlands");
        RAW_BIOME_IDS.put(42, "minecraft:end_highlands");
        RAW_BIOME_IDS.put(43, "minecraft:end_barrens");
        RAW_BIOME_IDS.put(44, "minecraft:warm_ocean");
        RAW_BIOME_IDS.put(45, "minecraft:lukewarm_ocean");
        RAW_BIOME_IDS.put(46, "minecraft:cold_ocean");
        RAW_BIOME_IDS.put(47, "minecraft:deep_warm_ocean");
        RAW_BIOME_IDS.put(48, "minecraft:deep_lukewarm_ocean");
        RAW_BIOME_IDS.put(49, "minecraft:deep_cold_ocean");
        RAW_BIOME_IDS.put(50, "minecraft:deep_frozen_ocean");
        RAW_BIOME_IDS.put(127, "minecraft:the_void");
        RAW_BIOME_IDS.put(129, "minecraft:sunflower_plains");
        RAW_BIOME_IDS.put(130, "minecraft:desert_lakes");
        RAW_BIOME_IDS.put(131, "minecraft:gravelly_mountains");
        RAW_BIOME_IDS.put(132, "minecraft:flower_forest");
        RAW_BIOME_IDS.put(133, "minecraft:taiga_mountains");
        RAW_BIOME_IDS.put(134, "minecraft:swamp_hills");
        RAW_BIOME_IDS.put(140, "minecraft:ice_spikes");
        RAW_BIOME_IDS.put(149, "minecraft:modified_jungle");
        RAW_BIOME_IDS.put(151, "minecraft:modified_jungle_edge");
        RAW_BIOME_IDS.put(155, "minecraft:tall_birch_forest");
        RAW_BIOME_IDS.put(156, "minecraft:tall_birch_hills");
        RAW_BIOME_IDS.put(157, "minecraft:dark_forest_hills");
        RAW_BIOME_IDS.put(158, "minecraft:snowy_taiga_mountains");
        RAW_BIOME_IDS.put(160, "minecraft:giant_spruce_taiga");
        RAW_BIOME_IDS.put(161, "minecraft:giant_spruce_taiga_hills");
        RAW_BIOME_IDS.put(162, "minecraft:modified_gravelly_mountains");
        RAW_BIOME_IDS.put(163, "minecraft:shattered_savanna");
        RAW_BIOME_IDS.put(164, "minecraft:shattered_savanna_plateau");
        RAW_BIOME_IDS.put(165, "minecraft:eroded_badlands");
        RAW_BIOME_IDS.put(166, "minecraft:modified_wooded_badlands_plateau");
        RAW_BIOME_IDS.put(167, "minecraft:modified_badlands_plateau");
        RAW_BIOME_IDS.put(168, "minecraft:bamboo_jungle");
        RAW_BIOME_IDS.put(169, "minecraft:bamboo_jungle_hills");
        RAW_BIOME_IDS.put(170, "minecraft:soul_sand_valley");
        RAW_BIOME_IDS.put(171, "minecraft:crimson_forest");
        RAW_BIOME_IDS.put(172, "minecraft:warped_forest");
        RAW_BIOME_IDS.put(173, "minecraft:basalt_deltas");
        RAW_BIOME_IDS.put(174, "minecraft:dripstone_caves");
        RAW_BIOME_IDS.put(175, "minecraft:lush_caves");
        RAW_BIOME_IDS.put(177, "minecraft:meadow");
        RAW_BIOME_IDS.put(178, "minecraft:grove");
        RAW_BIOME_IDS.put(179, "minecraft:snowy_slopes");
        RAW_BIOME_IDS.put(180, "minecraft:snowcapped_peaks");
        RAW_BIOME_IDS.put(181, "minecraft:lofty_peaks");
        RAW_BIOME_IDS.put(182, "minecraft:stony_peaks");
    }
}

