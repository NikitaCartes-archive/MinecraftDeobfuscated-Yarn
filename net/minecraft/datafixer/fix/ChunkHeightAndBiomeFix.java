/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
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
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ProtoChunkTickListFix;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

public class ChunkHeightAndBiomeFix
extends DataFix {
    public static final String CONTEXT = "__context";
    private static final String NAME = "ChunkHeightAndBiomeFix";
    private static final int CHUNK_SECTIONS_IN_OLD_CHUNK = 16;
    private static final int CHUNK_SECTIONS_IN_NEW_CHUNK = 24;
    private static final int MIN_CHUNK_SECTION_Y = -4;
    public static final int field_36214 = 4096;
    private static final int field_36215 = 64;
    private static final int field_35022 = 9;
    private static final long field_35023 = 511L;
    private static final int field_35024 = 64;
    private static final String[] HEIGHTMAP_KEYS = new String[]{"WORLD_SURFACE_WG", "WORLD_SURFACE", "WORLD_SURFACE_IGNORE_SNOW", "OCEAN_FLOOR_WG", "OCEAN_FLOOR", "MOTION_BLOCKING", "MOTION_BLOCKING_NO_LEAVES"};
    private static final Set<String> STATUSES_TO_SKIP_UPDATE = Set.of("surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
    private static final Set<String> field_35668 = Set.of("noise", "surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
    private static final Set<String> SURFACE_BLOCKS = Set.of("minecraft:air", "minecraft:basalt", "minecraft:bedrock", "minecraft:blackstone", "minecraft:calcite", "minecraft:cave_air", "minecraft:coarse_dirt", "minecraft:crimson_nylium", "minecraft:dirt", "minecraft:end_stone", "minecraft:grass_block", "minecraft:gravel", "minecraft:ice", "minecraft:lava", "minecraft:mycelium", "minecraft:nether_wart_block", "minecraft:netherrack", "minecraft:orange_terracotta", "minecraft:packed_ice", "minecraft:podzol", "minecraft:powder_snow", "minecraft:red_sand", "minecraft:red_sandstone", "minecraft:sand", "minecraft:sandstone", "minecraft:snow_block", "minecraft:soul_sand", "minecraft:soul_soil", "minecraft:stone", "minecraft:terracotta", "minecraft:warped_nylium", "minecraft:warped_wart_block", "minecraft:water", "minecraft:white_terracotta");
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
        return this.fixTypeEverywhereTyped(NAME, type, type2, (Typed<?> chunk) -> chunk.updateTyped(opticFinder, type3, level -> {
            Dynamic dynamic = level.get(DSL.remainderFinder());
            OptionalDynamic<?> optionalDynamic = chunk.get(DSL.remainderFinder()).get(CONTEXT);
            String string = optionalDynamic.get("dimension").asString().result().orElse("");
            String string2 = optionalDynamic.get("generator").asString().result().orElse("");
            boolean bl = "minecraft:overworld".equals(string);
            MutableBoolean mutableBoolean = new MutableBoolean();
            int i = bl ? -4 : 0;
            Dynamic[] dynamics = ChunkHeightAndBiomeFix.fixBiomes(dynamic, bl, i, mutableBoolean);
            Dynamic<?> dynamic2 = ChunkHeightAndBiomeFix.fixPalette(dynamic.createList(Stream.of(dynamic.createMap(ImmutableMap.of(dynamic.createString("Name"), dynamic.createString("minecraft:air"))))));
            HashSet set = Sets.newHashSet();
            MutableObject<Supplier<ProtoChunkTickListFix.class_6741>> mutableObject = new MutableObject<Supplier<ProtoChunkTickListFix.class_6741>>(() -> null);
            level = level.updateTyped(opticFinder2, type4, sections -> {
                IntOpenHashSet intSet = new IntOpenHashSet();
                Dynamic<?> dynamic3 = sections.write().result().orElseThrow(() -> new IllegalStateException("Malformed Chunk.Level.Sections"));
                List list = dynamic3.asStream().map(dynamic2 -> {
                    int j = dynamic2.get("Y").asInt(0);
                    Dynamic dynamic3 = DataFixUtils.orElse(dynamic2.get("Palette").result().flatMap(dynamic22 -> {
                        dynamic22.asStream().map(dynamic -> dynamic.get("Name").asString("minecraft:air")).forEach(set::add);
                        return dynamic2.get("BlockStates").result().map(dynamic2 -> ChunkHeightAndBiomeFix.fixPalette(dynamic22, dynamic2));
                    }), dynamic2);
                    Dynamic dynamic4 = dynamic2;
                    int k = j - i;
                    if (k >= 0 && k < dynamics.length) {
                        dynamic4 = dynamic4.set("biomes", dynamics[k]);
                    }
                    intSet.add(j);
                    if (dynamic2.get("Y").asInt(Integer.MAX_VALUE) == 0) {
                        mutableObject.setValue(() -> {
                            List list = dynamic3.get("palette").asList(Function.identity());
                            long[] ls = dynamic3.get("data").asLongStream().toArray();
                            return new ProtoChunkTickListFix.class_6741(list, ls);
                        });
                    }
                    return dynamic4.set("block_states", dynamic3).remove("Palette").remove("BlockStates");
                }).collect(Collectors.toCollection(ArrayList::new));
                for (int j = 0; j < dynamics.length; ++j) {
                    int k = j + i;
                    if (!intSet.add(k)) continue;
                    Dynamic dynamic4 = dynamic.createMap(Map.of(dynamic.createString("Y"), dynamic.createInt(k)));
                    dynamic4 = dynamic4.set("block_states", dynamic2);
                    dynamic4 = dynamic4.set("biomes", dynamics[j]);
                    list.add(dynamic4);
                }
                return type4.readTyped(dynamic.createList(list.stream())).result().orElseThrow(() -> new IllegalStateException("ChunkHeightAndBiomeFix failed.")).getFirst();
            });
            return level.update(DSL.remainderFinder(), level2 -> {
                if (bl) {
                    level2 = this.fixStatus((Dynamic<?>)level2, set);
                }
                return ChunkHeightAndBiomeFix.fixLevel(level2, bl, mutableBoolean.booleanValue(), "minecraft:noise".equals(string2), (Supplier)mutableObject.getValue());
            });
        }));
    }

    private Dynamic<?> fixStatus(Dynamic<?> level, Set<String> blocks) {
        return level.update("Status", status -> {
            boolean bl2;
            String string = status.asString("empty");
            if (STATUSES_TO_SKIP_UPDATE.contains(string)) {
                return status;
            }
            blocks.remove("minecraft:air");
            boolean bl = !blocks.isEmpty();
            blocks.removeAll(SURFACE_BLOCKS);
            boolean bl3 = bl2 = !blocks.isEmpty();
            if (bl2) {
                return status.createString("liquid_carvers");
            }
            if ("noise".equals(string) || bl) {
                return status.createString("noise");
            }
            if ("biomes".equals(string)) {
                return status.createString("structure_references");
            }
            return status;
        });
    }

    private static Dynamic<?>[] fixBiomes(Dynamic<?> level, boolean overworld, int i, MutableBoolean heightAlreadyUpdated) {
        Object[] dynamics = new Dynamic[overworld ? 24 : 16];
        int[] is = level.get("Biomes").asIntStreamOpt().result().map(IntStream::toArray).orElse(null);
        if (is != null && is.length == 1536) {
            heightAlreadyUpdated.setValue(true);
            for (int j = 0; j < 24; ++j) {
                int k = j;
                dynamics[j] = ChunkHeightAndBiomeFix.fixBiomes(level, sectionY -> ChunkHeightAndBiomeFix.getClamped(is, k * 64 + sectionY));
            }
        } else if (is != null && is.length == 1024) {
            int l;
            int j = 0;
            while (j < 16) {
                int k = j - i;
                l = j++;
                dynamics[k] = ChunkHeightAndBiomeFix.fixBiomes(level, sectionY -> ChunkHeightAndBiomeFix.getClamped(is, l * 64 + sectionY));
            }
            if (overworld) {
                Dynamic<?> dynamic = ChunkHeightAndBiomeFix.fixBiomes(level, sectionY -> ChunkHeightAndBiomeFix.getClamped(is, sectionY % 16));
                Dynamic<?> dynamic2 = ChunkHeightAndBiomeFix.fixBiomes(level, sectionY -> ChunkHeightAndBiomeFix.getClamped(is, sectionY % 16 + 1008));
                for (l = 0; l < 4; ++l) {
                    dynamics[l] = dynamic;
                }
                for (l = 20; l < 24; ++l) {
                    dynamics[l] = dynamic2;
                }
            }
        } else {
            Arrays.fill(dynamics, ChunkHeightAndBiomeFix.fixPalette(level.createList(Stream.of(level.createString(PLAINS_ID)))));
        }
        return dynamics;
    }

    private static int getClamped(int[] is, int index) {
        return is[index] & 0xFF;
    }

    private static Dynamic<?> fixLevel(Dynamic<?> level, boolean overworld, boolean heightAlreadyUpdated, boolean atNoiseStatus, Supplier<ProtoChunkTickListFix.class_6741> supplier) {
        Dynamic<?> dynamic;
        String string;
        level = level.remove("Biomes");
        if (!overworld) {
            return ChunkHeightAndBiomeFix.fixCarvingMasks(level, 16, 0);
        }
        if (heightAlreadyUpdated) {
            return ChunkHeightAndBiomeFix.fixCarvingMasks(level, 24, 0);
        }
        level = ChunkHeightAndBiomeFix.fixHeightmaps(level);
        level = ChunkHeightAndBiomeFix.fixChunkSectionList(level, "Lights");
        level = ChunkHeightAndBiomeFix.fixChunkSectionList(level, "LiquidsToBeTicked");
        level = ChunkHeightAndBiomeFix.fixChunkSectionList(level, "PostProcessing");
        level = ChunkHeightAndBiomeFix.fixChunkSectionList(level, "ToBeTicked");
        level = ChunkHeightAndBiomeFix.fixCarvingMasks(level, 24, 4);
        level = level.update("UpgradeData", ChunkHeightAndBiomeFix::fixUpgradeData);
        if (!atNoiseStatus) {
            return level;
        }
        Optional<Dynamic<?>> optional = level.get("Status").result();
        if (optional.isPresent() && !"empty".equals(string = (dynamic = optional.get()).asString(""))) {
            level = level.set("blending_data", level.createMap(ImmutableMap.of(level.createString("old_noise"), level.createBoolean(field_35668.contains(string)))));
            ProtoChunkTickListFix.class_6741 lv = supplier.get();
            if (lv != null) {
                BitSet bitSet = new BitSet(256);
                boolean bl = string.equals("noise");
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        boolean bl3;
                        Dynamic<?> dynamic2 = lv.method_39265(j, 0, i);
                        boolean bl2 = dynamic2 != null && "minecraft:bedrock".equals(dynamic2.get("Name").asString(""));
                        boolean bl4 = bl3 = dynamic2 != null && "minecraft:air".equals(dynamic2.get("Name").asString(""));
                        if (bl3) {
                            bitSet.set(i * 16 + j);
                        }
                        bl |= bl2;
                    }
                }
                if (bl && bitSet.cardinality() != bitSet.size()) {
                    Dynamic<?> dynamic3 = "full".equals(string) ? level.createString("heightmaps") : dynamic;
                    level = level.set("below_zero_retrogen", level.createMap(ImmutableMap.of(level.createString("target_status"), dynamic3, level.createString("missing_bedrock"), level.createLongList(LongStream.of(bitSet.toLongArray())))));
                    level = level.set("Status", level.createString("empty"));
                }
                level = level.set("isLightOn", level.createBoolean(false));
            }
        }
        return level;
    }

    private static <T> Dynamic<T> fixUpgradeData(Dynamic<T> upgradeData) {
        return upgradeData.update("Indices", indices -> {
            HashMap map = new HashMap();
            indices.getMapValues().result().ifPresent(indicesMap -> indicesMap.forEach((key, value) -> {
                try {
                    key.asString().result().map(Integer::parseInt).ifPresent(index -> {
                        int i = index - -4;
                        map.put(key.createString(Integer.toString(i)), value);
                    });
                } catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }));
            return indices.createMap(map);
        });
    }

    private static Dynamic<?> fixCarvingMasks(Dynamic<?> level, int sectionsPerChunk, int oldBottomSectionY) {
        Dynamic<?> dynamic = level.get("CarvingMasks").orElseEmptyMap();
        dynamic = dynamic.updateMapValues(mask -> {
            long[] ls = BitSet.valueOf(((Dynamic)mask.getSecond()).asByteBuffer().array()).toLongArray();
            long[] ms = new long[64 * sectionsPerChunk];
            System.arraycopy(ls, 0, ms, 64 * oldBottomSectionY, ls.length);
            return Pair.of((Dynamic)mask.getFirst(), level.createLongList(LongStream.of(ms)));
        });
        return level.set("CarvingMasks", dynamic);
    }

    private static Dynamic<?> fixChunkSectionList(Dynamic<?> level, String key) {
        List list = level.get(key).orElseEmptyList().asStream().collect(Collectors.toCollection(ArrayList::new));
        if (list.size() == 24) {
            return level;
        }
        Dynamic dynamic = level.emptyList();
        for (int i = 0; i < 4; ++i) {
            list.add(0, dynamic);
            list.add(dynamic);
        }
        return level.set(key, level.createList(list.stream()));
    }

    private static Dynamic<?> fixHeightmaps(Dynamic<?> level) {
        return level.update("Heightmaps", heightmaps -> {
            for (String string : HEIGHTMAP_KEYS) {
                heightmaps = heightmaps.update(string, ChunkHeightAndBiomeFix::fixHeightmap);
            }
            return heightmaps;
        });
    }

    private static Dynamic<?> fixHeightmap(Dynamic<?> heightmap) {
        return heightmap.createLongList(heightmap.asLongStream().map(entry -> {
            long l = 0L;
            int i = 0;
            while (i + 9 <= 64) {
                long m = entry >> i & 0x1FFL;
                long n = m == 0L ? 0L : Math.min(m + 64L, 511L);
                l |= n << i;
                i += 9;
            }
            return l;
        }));
    }

    private static Dynamic<?> fixBiomes(Dynamic<?> level, Int2IntFunction biomeGetter) {
        int j;
        Int2IntLinkedOpenHashMap int2IntMap = new Int2IntLinkedOpenHashMap();
        for (int i = 0; i < 64; ++i) {
            j = biomeGetter.applyAsInt(i);
            if (int2IntMap.containsKey(j)) continue;
            int2IntMap.put(j, int2IntMap.size());
        }
        Dynamic dynamic = level.createList(int2IntMap.keySet().stream().map(rawBiomeId -> level.createString(RAW_BIOME_IDS.getOrDefault((int)rawBiomeId, PLAINS_ID))));
        j = ChunkHeightAndBiomeFix.ceilLog2(int2IntMap.size());
        if (j == 0) {
            return ChunkHeightAndBiomeFix.fixPalette(dynamic);
        }
        int k = 64 / j;
        int l = (64 + k - 1) / k;
        long[] ls = new long[l];
        int m = 0;
        int n = 0;
        for (int o = 0; o < 64; ++o) {
            int p = biomeGetter.applyAsInt(o);
            int n2 = m++;
            ls[n2] = ls[n2] | (long)int2IntMap.get(p) << n;
            if ((n += j) + j <= 64) continue;
            n = 0;
        }
        Dynamic<?> dynamic2 = level.createLongList(Arrays.stream(ls));
        return ChunkHeightAndBiomeFix.fixPaletteWithData(dynamic, dynamic2);
    }

    private static Dynamic<?> fixPalette(Dynamic<?> palette) {
        return palette.createMap(ImmutableMap.of(palette.createString("palette"), palette));
    }

    private static Dynamic<?> fixPaletteWithData(Dynamic<?> palette, Dynamic<?> data) {
        return palette.createMap(ImmutableMap.of(palette.createString("palette"), palette, palette.createString("data"), data));
    }

    private static Dynamic<?> fixPalette(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
        List list = dynamic.asStream().collect(Collectors.toCollection(ArrayList::new));
        if (list.size() == 1) {
            return ChunkHeightAndBiomeFix.fixPalette(dynamic);
        }
        dynamic = ChunkHeightAndBiomeFix.method_39781(dynamic, dynamic2, list);
        return ChunkHeightAndBiomeFix.fixPaletteWithData(dynamic, dynamic2);
    }

    private static Dynamic<?> method_39781(Dynamic<?> dynamic, Dynamic<?> dynamic2, List<Dynamic<?>> list) {
        int i;
        int j;
        long l = dynamic2.asLongStream().count() * 64L;
        long m = l / 4096L;
        if (m > (long)(j = ChunkHeightAndBiomeFix.ceilLog2(i = list.size()))) {
            Dynamic dynamic3 = dynamic.createMap(ImmutableMap.of(dynamic.createString("Name"), dynamic.createString("minecraft:air")));
            int k = (1 << (int)(m - 1L)) + 1;
            int n = k - i;
            for (int o = 0; o < n; ++o) {
                list.add(dynamic3);
            }
            return dynamic.createList(list.stream());
        }
        return dynamic;
    }

    public static int ceilLog2(int value) {
        if (value == 0) {
            return 0;
        }
        return (int)Math.ceil(Math.log(value) / Math.log(2.0));
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

