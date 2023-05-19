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
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
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
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

public class ChunkHeightAndBiomeFix extends DataFix {
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
	private static final String[] HEIGHTMAP_KEYS = new String[]{
		"WORLD_SURFACE_WG", "WORLD_SURFACE", "WORLD_SURFACE_IGNORE_SNOW", "OCEAN_FLOOR_WG", "OCEAN_FLOOR", "MOTION_BLOCKING", "MOTION_BLOCKING_NO_LEAVES"
	};
	private static final Set<String> STATUSES_TO_SKIP_UPDATE = Set.of("surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
	private static final Set<String> field_35668 = Set.of("noise", "surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
	private static final Set<String> SURFACE_BLOCKS = Set.of(
		"minecraft:air",
		"minecraft:basalt",
		"minecraft:bedrock",
		"minecraft:blackstone",
		"minecraft:calcite",
		"minecraft:cave_air",
		"minecraft:coarse_dirt",
		"minecraft:crimson_nylium",
		"minecraft:dirt",
		"minecraft:end_stone",
		"minecraft:grass_block",
		"minecraft:gravel",
		"minecraft:ice",
		"minecraft:lava",
		"minecraft:mycelium",
		"minecraft:nether_wart_block",
		"minecraft:netherrack",
		"minecraft:orange_terracotta",
		"minecraft:packed_ice",
		"minecraft:podzol",
		"minecraft:powder_snow",
		"minecraft:red_sand",
		"minecraft:red_sandstone",
		"minecraft:sand",
		"minecraft:sandstone",
		"minecraft:snow_block",
		"minecraft:soul_sand",
		"minecraft:soul_soil",
		"minecraft:stone",
		"minecraft:terracotta",
		"minecraft:warped_nylium",
		"minecraft:warped_wart_block",
		"minecraft:water",
		"minecraft:white_terracotta"
	);
	private static final int field_35026 = 16;
	private static final int field_35027 = 64;
	private static final int field_35028 = 1008;
	public static final String PLAINS_ID = "minecraft:plains";
	private static final Int2ObjectMap<String> RAW_BIOME_IDS = new Int2ObjectOpenHashMap<>();

	public ChunkHeightAndBiomeFix(Schema outputSchema) {
		super(outputSchema, true);
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
		return this.fixTypeEverywhereTyped(
			"ChunkHeightAndBiomeFix",
			type,
			type2,
			chunk -> chunk.updateTyped(
					opticFinder,
					type3,
					level -> {
						Dynamic<?> dynamic = level.get(DSL.remainderFinder());
						OptionalDynamic<?> optionalDynamic = chunk.get(DSL.remainderFinder()).get("__context");
						String string = (String)optionalDynamic.get("dimension").asString().result().orElse("");
						String string2 = (String)optionalDynamic.get("generator").asString().result().orElse("");
						boolean bl = "minecraft:overworld".equals(string);
						MutableBoolean mutableBoolean = new MutableBoolean();
						int i = bl ? -4 : 0;
						Dynamic<?>[] dynamics = fixBiomes(dynamic, bl, i, mutableBoolean);
						Dynamic<?> dynamic2 = fixPalette(
							dynamic.createList(Stream.of(dynamic.createMap(ImmutableMap.of(dynamic.createString("Name"), dynamic.createString("minecraft:air")))))
						);
						Set<String> set = Sets.<String>newHashSet();
						MutableObject<Supplier<ProtoChunkTickListFix.class_6741>> mutableObject = new MutableObject<>(() -> null);
						level = level.updateTyped(
							opticFinder2,
							type4,
							sections -> {
								IntSet intSet = new IntOpenHashSet();
								Dynamic<?> dynamic3 = (Dynamic<?>)sections.write().result().orElseThrow(() -> new IllegalStateException("Malformed Chunk.Level.Sections"));
								List<Dynamic<?>> list = (List<Dynamic<?>>)dynamic3.asStream().map(dynamic2xx -> {
									int jx = dynamic2xx.get("Y").asInt(0);
									Dynamic<?> dynamic3x = DataFixUtils.orElse(dynamic2xx.get("Palette").result().flatMap(dynamic2xxx -> {
										dynamic2xxx.asStream().map(dynamicxxxx -> dynamicxxxx.get("Name").asString("minecraft:air")).forEach(set::add);
										return dynamic2xx.get("BlockStates").result().map(dynamic2xxxx -> fixPalette(dynamic2xxx, dynamic2xxxx));
									}), dynamic2);
									Dynamic<?> dynamic4x = dynamic2xx;
									int kx = jx - i;
									if (kx >= 0 && kx < dynamics.length) {
										dynamic4x = dynamic2xx.set("biomes", dynamics[kx]);
									}

									intSet.add(jx);
									if (dynamic2xx.get("Y").asInt(Integer.MAX_VALUE) == 0) {
										mutableObject.setValue(() -> {
											List<? extends Dynamic<?>> listx = dynamic3x.get("palette").asList(Function.identity());
											long[] ls = dynamic3x.get("data").asLongStream().toArray();
											return new ProtoChunkTickListFix.class_6741(listx, ls);
										});
									}

									return dynamic4x.set("block_states", dynamic3x).remove("Palette").remove("BlockStates");
								}).collect(Collectors.toCollection(ArrayList::new));

								for (int j = 0; j < dynamics.length; j++) {
									int k = j + i;
									if (intSet.add(k)) {
										Dynamic<?> dynamic4 = dynamic.createMap(Map.of(dynamic.createString("Y"), dynamic.createInt(k)));
										dynamic4 = dynamic4.set("block_states", dynamic2);
										dynamic4 = dynamic4.set("biomes", dynamics[j]);
										list.add(dynamic4);
									}
								}

								return (Typed)((Pair)type4.readTyped(dynamic.createList(list.stream()))
										.result()
										.orElseThrow(() -> new IllegalStateException("ChunkHeightAndBiomeFix failed.")))
									.getFirst();
							}
						);
						return level.update(DSL.remainderFinder(), level2 -> {
							if (bl) {
								level2 = this.fixStatus(level2, set);
							}

							return fixLevel(level2, bl, mutableBoolean.booleanValue(), "minecraft:noise".equals(string2), mutableObject.getValue());
						});
					}
				)
		);
	}

	private Dynamic<?> fixStatus(Dynamic<?> level, Set<String> blocks) {
		return level.update("Status", status -> {
			String string = status.asString("empty");
			if (STATUSES_TO_SKIP_UPDATE.contains(string)) {
				return status;
			} else {
				blocks.remove("minecraft:air");
				boolean bl = !blocks.isEmpty();
				blocks.removeAll(SURFACE_BLOCKS);
				boolean bl2 = !blocks.isEmpty();
				if (bl2) {
					return status.createString("liquid_carvers");
				} else if ("noise".equals(string) || bl) {
					return status.createString("noise");
				} else {
					return "biomes".equals(string) ? status.createString("structure_references") : status;
				}
			}
		});
	}

	private static Dynamic<?>[] fixBiomes(Dynamic<?> level, boolean overworld, int i, MutableBoolean heightAlreadyUpdated) {
		Dynamic<?>[] dynamics = new Dynamic[overworld ? 24 : 16];
		int[] is = (int[])level.get("Biomes").asIntStreamOpt().result().map(IntStream::toArray).orElse(null);
		if (is != null && is.length == 1536) {
			heightAlreadyUpdated.setValue(true);

			for (int j = 0; j < 24; j++) {
				int k = j;
				dynamics[j] = fixBiomes(level, sectionY -> getClamped(is, k * 64 + sectionY));
			}
		} else if (is != null && is.length == 1024) {
			for (int j = 0; j < 16; j++) {
				int k = j - i;
				dynamics[k] = fixBiomes(level, sectionY -> getClamped(is, j * 64 + sectionY));
			}

			if (overworld) {
				Dynamic<?> dynamic = fixBiomes(level, sectionY -> getClamped(is, sectionY % 16));
				Dynamic<?> dynamic2 = fixBiomes(level, sectionY -> getClamped(is, sectionY % 16 + 1008));

				for (int l = 0; l < 4; l++) {
					dynamics[l] = dynamic;
				}

				for (int l = 20; l < 24; l++) {
					dynamics[l] = dynamic2;
				}
			}
		} else {
			Arrays.fill(dynamics, fixPalette(level.createList(Stream.of(level.createString("minecraft:plains")))));
		}

		return dynamics;
	}

	private static int getClamped(int[] is, int index) {
		return is[index] & 0xFF;
	}

	private static Dynamic<?> fixLevel(
		Dynamic<?> level, boolean overworld, boolean heightAlreadyUpdated, boolean atNoiseStatus, Supplier<ProtoChunkTickListFix.class_6741> supplier
	) {
		level = level.remove("Biomes");
		if (!overworld) {
			return fixCarvingMasks(level, 16, 0);
		} else if (heightAlreadyUpdated) {
			return fixCarvingMasks(level, 24, 0);
		} else {
			level = fixHeightmaps(level);
			level = fixChunkSectionList(level, "LiquidsToBeTicked");
			level = fixChunkSectionList(level, "PostProcessing");
			level = fixChunkSectionList(level, "ToBeTicked");
			level = fixCarvingMasks(level, 24, 4);
			level = level.update("UpgradeData", ChunkHeightAndBiomeFix::fixUpgradeData);
			if (!atNoiseStatus) {
				return level;
			} else {
				Optional<? extends Dynamic<?>> optional = level.get("Status").result();
				if (optional.isPresent()) {
					Dynamic<?> dynamic = (Dynamic<?>)optional.get();
					String string = dynamic.asString("");
					if (!"empty".equals(string)) {
						level = level.set("blending_data", level.createMap(ImmutableMap.of(level.createString("old_noise"), level.createBoolean(field_35668.contains(string)))));
						ProtoChunkTickListFix.class_6741 lv = (ProtoChunkTickListFix.class_6741)supplier.get();
						if (lv != null) {
							BitSet bitSet = new BitSet(256);
							boolean bl = string.equals("noise");

							for (int i = 0; i < 16; i++) {
								for (int j = 0; j < 16; j++) {
									Dynamic<?> dynamic2 = lv.method_39265(j, 0, i);
									boolean bl2 = dynamic2 != null && "minecraft:bedrock".equals(dynamic2.get("Name").asString(""));
									boolean bl3 = dynamic2 != null && "minecraft:air".equals(dynamic2.get("Name").asString(""));
									if (bl3) {
										bitSet.set(i * 16 + j);
									}

									bl |= bl2;
								}
							}

							if (bl && bitSet.cardinality() != bitSet.size()) {
								Dynamic<?> dynamic3 = "full".equals(string) ? level.createString("heightmaps") : dynamic;
								level = level.set(
									"below_zero_retrogen",
									level.createMap(
										ImmutableMap.of(
											level.createString("target_status"), dynamic3, level.createString("missing_bedrock"), level.createLongList(LongStream.of(bitSet.toLongArray()))
										)
									)
								);
								level = level.set("Status", level.createString("empty"));
							}

							level = level.set("isLightOn", level.createBoolean(false));
						}
					}
				}

				return level;
			}
		}
	}

	private static <T> Dynamic<T> fixUpgradeData(Dynamic<T> upgradeData) {
		return upgradeData.update("Indices", indices -> {
			Map<Dynamic<?>, Dynamic<?>> map = new HashMap();
			indices.getMapValues().result().ifPresent(indicesMap -> indicesMap.forEach((key, value) -> {
					try {
						key.asString().result().map(Integer::parseInt).ifPresent(index -> {
							int i = index - -4;
							map.put(key.createString(Integer.toString(i)), value);
						});
					} catch (NumberFormatException var4) {
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
		List<Dynamic<?>> list = (List<Dynamic<?>>)level.get(key).orElseEmptyList().asStream().collect(Collectors.toCollection(ArrayList::new));
		if (list.size() == 24) {
			return level;
		} else {
			Dynamic<?> dynamic = level.emptyList();

			for (int i = 0; i < 4; i++) {
				list.add(0, dynamic);
				list.add(dynamic);
			}

			return level.set(key, level.createList(list.stream()));
		}
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

			for (int i = 0; i + 9 <= 64; i += 9) {
				long m = entry >> i & 511L;
				long n;
				if (m == 0L) {
					n = 0L;
				} else {
					n = Math.min(m + 64L, 511L);
				}

				l |= n << i;
			}

			return l;
		}));
	}

	private static Dynamic<?> fixBiomes(Dynamic<?> level, Int2IntFunction biomeGetter) {
		Int2IntMap int2IntMap = new Int2IntLinkedOpenHashMap();

		for (int i = 0; i < 64; i++) {
			int j = biomeGetter.applyAsInt(i);
			if (!int2IntMap.containsKey(j)) {
				int2IntMap.put(j, int2IntMap.size());
			}
		}

		Dynamic<?> dynamic = level.createList(
			int2IntMap.keySet().stream().map(rawBiomeId -> level.createString(RAW_BIOME_IDS.getOrDefault(rawBiomeId.intValue(), "minecraft:plains")))
		);
		int j = ceilLog2(int2IntMap.size());
		if (j == 0) {
			return fixPalette(dynamic);
		} else {
			int k = 64 / j;
			int l = (64 + k - 1) / k;
			long[] ls = new long[l];
			int m = 0;
			int n = 0;

			for (int o = 0; o < 64; o++) {
				int p = biomeGetter.applyAsInt(o);
				ls[m] |= (long)int2IntMap.get(p) << n;
				n += j;
				if (n + j > 64) {
					m++;
					n = 0;
				}
			}

			Dynamic<?> dynamic2 = level.createLongList(Arrays.stream(ls));
			return fixPaletteWithData(dynamic, dynamic2);
		}
	}

	private static Dynamic<?> fixPalette(Dynamic<?> palette) {
		return palette.createMap(ImmutableMap.of(palette.createString("palette"), palette));
	}

	private static Dynamic<?> fixPaletteWithData(Dynamic<?> palette, Dynamic<?> data) {
		return palette.createMap(ImmutableMap.of(palette.createString("palette"), palette, palette.createString("data"), data));
	}

	private static Dynamic<?> fixPalette(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
		List<Dynamic<?>> list = (List<Dynamic<?>>)dynamic.asStream().collect(Collectors.toCollection(ArrayList::new));
		if (list.size() == 1) {
			return fixPalette(dynamic);
		} else {
			dynamic = method_39781(dynamic, dynamic2, list);
			return fixPaletteWithData(dynamic, dynamic2);
		}
	}

	private static Dynamic<?> method_39781(Dynamic<?> dynamic, Dynamic<?> dynamic2, List<Dynamic<?>> list) {
		long l = dynamic2.asLongStream().count() * 64L;
		long m = l / 4096L;
		int i = list.size();
		int j = ceilLog2(i);
		if (m <= (long)j) {
			return dynamic;
		} else {
			Dynamic<?> dynamic3 = dynamic.createMap(ImmutableMap.of(dynamic.createString("Name"), dynamic.createString("minecraft:air")));
			int k = (1 << (int)(m - 1L)) + 1;
			int n = k - i;

			for (int o = 0; o < n; o++) {
				list.add(dynamic3);
			}

			return dynamic.createList(list.stream());
		}
	}

	public static int ceilLog2(int value) {
		return value == 0 ? 0 : (int)Math.ceil(Math.log((double)value) / Math.log(2.0));
	}

	static {
		RAW_BIOME_IDS.put(0, "minecraft:ocean");
		RAW_BIOME_IDS.put(1, "minecraft:plains");
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
