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
	private static final int field_35021 = 64;
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
		return this.fixTypeEverywhereTyped(
			"ChunkHeightAndBiomeFix",
			type,
			type2,
			typed -> typed.updateTyped(
					opticFinder,
					type3,
					typed2 -> {
						Dynamic<?> dynamic = typed2.get(DSL.remainderFinder());
						OptionalDynamic<?> optionalDynamic = typed.get(DSL.remainderFinder()).get("__context");
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
						typed2 = typed2.updateTyped(
							opticFinder2,
							type4,
							typedxx -> {
								IntSet intSet = new IntOpenHashSet();
								Dynamic<?> dynamic3 = (Dynamic<?>)typedxx.write().result().orElseThrow(() -> new IllegalStateException("Malformed Chunk.Level.Sections"));
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
						return typed2.update(DSL.remainderFinder(), dynamicx -> {
							if (bl) {
								dynamicx = this.fixStatus(dynamicx, set);
							}

							return fixLevel(dynamicx, bl, mutableBoolean.booleanValue(), "minecraft:noise".equals(string2), mutableObject.getValue());
						});
					}
				)
		);
	}

	private Dynamic<?> fixStatus(Dynamic<?> dynamic, Set<String> set) {
		return dynamic.update("Status", dynamicx -> {
			String string = dynamicx.asString("empty");
			if (STATUSES_TO_SKIP_UPDATE.contains(string)) {
				return dynamicx;
			} else {
				set.remove("minecraft:air");
				boolean bl = !set.isEmpty();
				set.removeAll(SURFACE_BLOCKS);
				boolean bl2 = !set.isEmpty();
				if (bl2) {
					return dynamicx.createString("liquid_carvers");
				} else if ("noise".equals(string) || bl) {
					return dynamicx.createString("noise");
				} else {
					return "biomes".equals(string) ? dynamicx.createString("structure_references") : dynamicx;
				}
			}
		});
	}

	private static Dynamic<?>[] fixBiomes(Dynamic<?> dynamic, boolean bl, int i, MutableBoolean mutableBoolean) {
		Dynamic<?>[] dynamics = new Dynamic[bl ? 24 : 16];
		int[] is = (int[])dynamic.get("Biomes").asIntStreamOpt().result().map(IntStream::toArray).orElse(null);
		if (is != null && is.length == 1536) {
			mutableBoolean.setValue(true);

			for (int j = 0; j < 24; j++) {
				int k = j;
				dynamics[j] = fixBiomes(dynamic, jx -> method_39519(is, k * 64 + jx));
			}
		} else if (is != null && is.length == 1024) {
			for (int j = 0; j < 16; j++) {
				int k = j - i;
				dynamics[k] = fixBiomes(dynamic, jx -> method_39519(is, j * 64 + jx));
			}

			if (bl) {
				Dynamic<?> dynamic2 = fixBiomes(dynamic, ix -> method_39519(is, ix % 16));
				Dynamic<?> dynamic3 = fixBiomes(dynamic, ix -> method_39519(is, ix % 16 + 1008));

				for (int l = 0; l < 4; l++) {
					dynamics[l] = dynamic2;
				}

				for (int l = 20; l < 24; l++) {
					dynamics[l] = dynamic3;
				}
			}
		} else {
			Arrays.fill(dynamics, fixPalette(dynamic.createList(Stream.of(dynamic.createString("minecraft:plains")))));
		}

		return dynamics;
	}

	private static int method_39519(int[] is, int i) {
		return is[i] & 0xFF;
	}

	private static Dynamic<?> fixLevel(Dynamic<?> dynamic, boolean bl, boolean bl2, boolean bl3, Supplier<ProtoChunkTickListFix.class_6741> supplier) {
		dynamic = dynamic.remove("Biomes");
		if (!bl) {
			return fixCarvingMasks(dynamic, 16, 0);
		} else if (bl2) {
			return fixCarvingMasks(dynamic, 24, 0);
		} else {
			dynamic = fixHeightmaps(dynamic);
			dynamic = fixChunkSectionList(dynamic, "Lights");
			dynamic = fixChunkSectionList(dynamic, "LiquidsToBeTicked");
			dynamic = fixChunkSectionList(dynamic, "PostProcessing");
			dynamic = fixChunkSectionList(dynamic, "ToBeTicked");
			dynamic = fixCarvingMasks(dynamic, 24, 4);
			if (!bl3) {
				return dynamic;
			} else {
				Optional<? extends Dynamic<?>> optional = dynamic.get("Status").result();
				if (optional.isPresent()) {
					Dynamic<?> dynamic2 = (Dynamic<?>)optional.get();
					String string = dynamic2.asString("");
					if (!"empty".equals(string)) {
						dynamic = dynamic.set(
							"blending_data", dynamic.createMap(ImmutableMap.of(dynamic.createString("old_noise"), dynamic.createBoolean(field_35668.contains(string))))
						);
						ProtoChunkTickListFix.class_6741 lv = (ProtoChunkTickListFix.class_6741)supplier.get();
						if (lv != null) {
							BitSet bitSet = new BitSet(256);

							for (int i = 0; i < 16; i++) {
								for (int j = 0; j < 16; j++) {
									Dynamic<?> dynamic3 = lv.method_39265(j, 0, i);
									boolean bl4 = dynamic3 != null && "minecraft:bedrock".equals(dynamic3.get("Name").asString(""));
									if (!bl4) {
										bitSet.set(i * 16 + j);
									}
								}
							}

							boolean bl5 = bitSet.cardinality() != bitSet.size();
							if (bl5) {
								Dynamic<?> dynamic4 = "full".equals(string) ? dynamic.createString("heightmaps") : dynamic2;
								dynamic = dynamic.set(
									"below_zero_retrogen",
									dynamic.createMap(
										ImmutableMap.of(
											dynamic.createString("target_status"),
											dynamic4,
											dynamic.createString("missing_bedrock"),
											dynamic.createLongList(LongStream.of(bitSet.toLongArray()))
										)
									)
								);
								dynamic = dynamic.set("Status", dynamic.createString("empty"));
								dynamic = dynamic.set("isLightOn", dynamic.createBoolean(false));
							}
						}
					}
				}

				return dynamic;
			}
		}
	}

	private static Dynamic<?> fixCarvingMasks(Dynamic<?> dynamic, int i, int j) {
		Dynamic<?> dynamic2 = dynamic.get("CarvingMasks").orElseEmptyMap();
		dynamic2 = dynamic2.updateMapValues(pair -> {
			long[] ls = BitSet.valueOf(((Dynamic)pair.getSecond()).asByteBuffer().array()).toLongArray();
			long[] ms = new long[64 * i];
			System.arraycopy(ls, 0, ms, 64 * j, ls.length);
			return Pair.of((Dynamic)pair.getFirst(), dynamic.createLongList(LongStream.of(ms)));
		});
		return dynamic.set("CarvingMasks", dynamic2);
	}

	private static Dynamic<?> fixChunkSectionList(Dynamic<?> dynamic, String string) {
		List<Dynamic<?>> list = (List<Dynamic<?>>)dynamic.get(string).orElseEmptyList().asStream().collect(Collectors.toCollection(ArrayList::new));
		if (list.size() == 24) {
			return dynamic;
		} else {
			Dynamic<?> dynamic2 = dynamic.emptyList();

			for (int i = 0; i < 4; i++) {
				list.add(0, dynamic2);
				list.add(dynamic2);
			}

			return dynamic.set(string, dynamic.createList(list.stream()));
		}
	}

	private static Dynamic<?> fixHeightmaps(Dynamic<?> dynamic) {
		return dynamic.update("Heightmaps", dynamicx -> {
			for (String string : HEIGHTMAP_KEYS) {
				dynamicx = dynamicx.update(string, ChunkHeightAndBiomeFix::fixHeightmap);
			}

			return dynamicx;
		});
	}

	private static Dynamic<?> fixHeightmap(Dynamic<?> dynamic) {
		return dynamic.createLongList(dynamic.asLongStream().map(l -> {
			long m = 0L;

			for (int i = 0; i + 9 <= 64; i += 9) {
				long n = l >> i & 511L;
				long o;
				if (n == 0L) {
					o = 0L;
				} else {
					o = Math.min(n + 64L, 511L);
				}

				m |= o << i;
			}

			return m;
		}));
	}

	private static Dynamic<?> fixBiomes(Dynamic<?> dynamic, Int2IntFunction int2IntFunction) {
		Int2IntMap int2IntMap = new Int2IntLinkedOpenHashMap();

		for (int i = 0; i < 64; i++) {
			int j = int2IntFunction.applyAsInt(i);
			if (!int2IntMap.containsKey(j)) {
				int2IntMap.put(j, int2IntMap.size());
			}
		}

		Dynamic<?> dynamic2 = dynamic.createList(
			int2IntMap.keySet().stream().map(integer -> dynamic.createString(RAW_BIOME_IDS.getOrDefault(integer.intValue(), "minecraft:plains")))
		);
		int j = ceilLog2(int2IntMap.size());
		if (j == 0) {
			return fixPalette(dynamic2);
		} else {
			int k = 64 / j;
			int l = (64 + k - 1) / k;
			long[] ls = new long[l];
			int m = 0;
			int n = 0;

			for (int o = 0; o < 64; o++) {
				int p = int2IntFunction.applyAsInt(o);
				ls[m] |= (long)int2IntMap.get(p) << n;
				n += j;
				if (n + j > 64) {
					m++;
					n = 0;
				}
			}

			Dynamic<?> dynamic3 = dynamic.createLongList(Arrays.stream(ls));
			return fixPaletteWithData(dynamic2, dynamic3);
		}
	}

	private static Dynamic<?> fixPalette(Dynamic<?> dynamic) {
		return dynamic.createMap(ImmutableMap.of(dynamic.createString("palette"), dynamic));
	}

	private static Dynamic<?> fixPaletteWithData(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
		return dynamic.createMap(ImmutableMap.of(dynamic.createString("palette"), dynamic, dynamic.createString("data"), dynamic2));
	}

	private static Dynamic<?> fixPalette(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
		return dynamic.asStream().count() == 1L ? fixPalette(dynamic) : fixPaletteWithData(dynamic, dynamic2);
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
