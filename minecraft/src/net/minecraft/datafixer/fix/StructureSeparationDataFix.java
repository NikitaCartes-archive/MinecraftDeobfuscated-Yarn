package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class StructureSeparationDataFix extends DataFix {
	private static final String VILLAGE_STRUCTURE_ID = "minecraft:village";
	private static final String DESERT_PYRAMID_STRUCTURE_ID = "minecraft:desert_pyramid";
	private static final String IGLOO_STRUCTURE_ID = "minecraft:igloo";
	private static final String JUNGLE_PYRAMID_STRUCTURE_ID = "minecraft:jungle_pyramid";
	private static final String SWAMP_HUT_STRUCTURE_ID = "minecraft:swamp_hut";
	private static final String PILLAGER_OUTPOST_STRUCTURE_ID = "minecraft:pillager_outpost";
	private static final String END_CITY_STRUCTURE_ID = "minecraft:endcity";
	private static final String MANSION_STRUCTURE_ID = "minecraft:mansion";
	private static final String MONUMENT_STRUCTURE_ID = "minecraft:monument";
	private static final ImmutableMap<String, StructureSeparationDataFix.Information> STRUCTURE_SPACING = ImmutableMap.<String, StructureSeparationDataFix.Information>builder()
		.put("minecraft:village", new StructureSeparationDataFix.Information(32, 8, 10387312))
		.put("minecraft:desert_pyramid", new StructureSeparationDataFix.Information(32, 8, 14357617))
		.put("minecraft:igloo", new StructureSeparationDataFix.Information(32, 8, 14357618))
		.put("minecraft:jungle_pyramid", new StructureSeparationDataFix.Information(32, 8, 14357619))
		.put("minecraft:swamp_hut", new StructureSeparationDataFix.Information(32, 8, 14357620))
		.put("minecraft:pillager_outpost", new StructureSeparationDataFix.Information(32, 8, 165745296))
		.put("minecraft:monument", new StructureSeparationDataFix.Information(32, 5, 10387313))
		.put("minecraft:endcity", new StructureSeparationDataFix.Information(20, 11, 10387313))
		.put("minecraft:mansion", new StructureSeparationDataFix.Information(80, 20, 10387319))
		.build();

	public StructureSeparationDataFix(Schema outputSchema) {
		super(outputSchema, true);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"WorldGenSettings building",
			this.getInputSchema().getType(TypeReferences.WORLD_GEN_SETTINGS),
			worldGenSettingsTyped -> worldGenSettingsTyped.update(DSL.remainderFinder(), StructureSeparationDataFix::updateWorldGenSettings)
		);
	}

	private static <T> Dynamic<T> createGeneratorSettings(
		long seed, DynamicLike<T> worldGenSettingsDynamic, Dynamic<T> settingsDynamic, Dynamic<T> biomeSourceDynamic
	) {
		return worldGenSettingsDynamic.createMap(
			ImmutableMap.of(
				worldGenSettingsDynamic.createString("type"),
				worldGenSettingsDynamic.createString("minecraft:noise"),
				worldGenSettingsDynamic.createString("biome_source"),
				biomeSourceDynamic,
				worldGenSettingsDynamic.createString("seed"),
				worldGenSettingsDynamic.createLong(seed),
				worldGenSettingsDynamic.createString("settings"),
				settingsDynamic
			)
		);
	}

	private static <T> Dynamic<T> createBiomeSource(Dynamic<T> worldGenSettingsDynamic, long seed, boolean legacyBiomeInitLayer, boolean largeBiomes) {
		Builder<Dynamic<T>, Dynamic<T>> builder = ImmutableMap.<Dynamic<T>, Dynamic<T>>builder()
			.put(worldGenSettingsDynamic.createString("type"), worldGenSettingsDynamic.createString("minecraft:vanilla_layered"))
			.put(worldGenSettingsDynamic.createString("seed"), worldGenSettingsDynamic.createLong(seed))
			.put(worldGenSettingsDynamic.createString("large_biomes"), worldGenSettingsDynamic.createBoolean(largeBiomes));
		if (legacyBiomeInitLayer) {
			builder.put(worldGenSettingsDynamic.createString("legacy_biome_init_layer"), worldGenSettingsDynamic.createBoolean(legacyBiomeInitLayer));
		}

		return worldGenSettingsDynamic.createMap(builder.build());
	}

	private static <T> Dynamic<T> updateWorldGenSettings(Dynamic<T> worldGenSettingsDynamic) {
		DynamicOps<T> dynamicOps = worldGenSettingsDynamic.getOps();
		long l = worldGenSettingsDynamic.get("RandomSeed").asLong(0L);
		Optional<String> optional = worldGenSettingsDynamic.get("generatorName")
			.asString()
			.<String>map(generatorName -> generatorName.toLowerCase(Locale.ROOT))
			.result();
		Optional<String> optional2 = (Optional<String>)worldGenSettingsDynamic.get("legacy_custom_options")
			.asString()
			.result()
			.map(Optional::of)
			.orElseGet(() -> optional.equals(Optional.of("customized")) ? worldGenSettingsDynamic.get("generatorOptions").asString().result() : Optional.empty());
		boolean bl = false;
		Dynamic<T> dynamic;
		if (optional.equals(Optional.of("customized"))) {
			dynamic = createDefaultOverworldGeneratorSettings(worldGenSettingsDynamic, l);
		} else if (optional.isEmpty()) {
			dynamic = createDefaultOverworldGeneratorSettings(worldGenSettingsDynamic, l);
		} else {
			String bl6 = (String)optional.get();
			switch (bl6) {
				case "flat":
					OptionalDynamic<T> optionalDynamic = worldGenSettingsDynamic.get("generatorOptions");
					Map<Dynamic<T>, Dynamic<T>> map = createFlatWorldStructureSettings(dynamicOps, optionalDynamic);
					dynamic = worldGenSettingsDynamic.createMap(
						ImmutableMap.of(
							worldGenSettingsDynamic.createString("type"),
							worldGenSettingsDynamic.createString("minecraft:flat"),
							worldGenSettingsDynamic.createString("settings"),
							worldGenSettingsDynamic.createMap(
								ImmutableMap.of(
									worldGenSettingsDynamic.createString("structures"),
									worldGenSettingsDynamic.createMap(map),
									worldGenSettingsDynamic.createString("layers"),
									(Dynamic<?>)optionalDynamic.get("layers")
										.result()
										.orElseGet(
											() -> worldGenSettingsDynamic.createList(
													Stream.of(
														worldGenSettingsDynamic.createMap(
															ImmutableMap.of(
																worldGenSettingsDynamic.createString("height"),
																worldGenSettingsDynamic.createInt(1),
																worldGenSettingsDynamic.createString("block"),
																worldGenSettingsDynamic.createString("minecraft:bedrock")
															)
														),
														worldGenSettingsDynamic.createMap(
															ImmutableMap.of(
																worldGenSettingsDynamic.createString("height"),
																worldGenSettingsDynamic.createInt(2),
																worldGenSettingsDynamic.createString("block"),
																worldGenSettingsDynamic.createString("minecraft:dirt")
															)
														),
														worldGenSettingsDynamic.createMap(
															ImmutableMap.of(
																worldGenSettingsDynamic.createString("height"),
																worldGenSettingsDynamic.createInt(1),
																worldGenSettingsDynamic.createString("block"),
																worldGenSettingsDynamic.createString("minecraft:grass_block")
															)
														)
													)
												)
										),
									worldGenSettingsDynamic.createString("biome"),
									worldGenSettingsDynamic.createString(optionalDynamic.get("biome").asString("minecraft:plains"))
								)
							)
						)
					);
					break;
				case "debug_all_block_states":
					dynamic = worldGenSettingsDynamic.createMap(
						ImmutableMap.of(worldGenSettingsDynamic.createString("type"), worldGenSettingsDynamic.createString("minecraft:debug"))
					);
					break;
				case "buffet":
					OptionalDynamic<T> optionalDynamic2 = worldGenSettingsDynamic.get("generatorOptions");
					OptionalDynamic<?> optionalDynamic3 = optionalDynamic2.get("chunk_generator");
					Optional<String> optional3 = optionalDynamic3.get("type").asString().result();
					Dynamic<T> dynamic2;
					if (Objects.equals(optional3, Optional.of("minecraft:caves"))) {
						dynamic2 = worldGenSettingsDynamic.createString("minecraft:caves");
						bl = true;
					} else if (Objects.equals(optional3, Optional.of("minecraft:floating_islands"))) {
						dynamic2 = worldGenSettingsDynamic.createString("minecraft:floating_islands");
					} else {
						dynamic2 = worldGenSettingsDynamic.createString("minecraft:overworld");
					}

					Dynamic<T> dynamic3 = (Dynamic<T>)optionalDynamic2.get("biome_source")
						.result()
						.orElseGet(
							() -> worldGenSettingsDynamic.createMap(
									ImmutableMap.of(worldGenSettingsDynamic.createString("type"), worldGenSettingsDynamic.createString("minecraft:fixed"))
								)
						);
					Dynamic<T> dynamic4;
					if (dynamic3.get("type").asString().result().equals(Optional.of("minecraft:fixed"))) {
						String string = (String)dynamic3.get("options")
							.get("biomes")
							.asStream()
							.findFirst()
							.flatMap(biomeDynamic -> biomeDynamic.asString().result())
							.orElse("minecraft:ocean");
						dynamic4 = dynamic3.remove("options").set("biome", worldGenSettingsDynamic.createString(string));
					} else {
						dynamic4 = dynamic3;
					}

					dynamic = createGeneratorSettings(l, worldGenSettingsDynamic, dynamic2, dynamic4);
					break;
				default:
					boolean bl2 = ((String)optional.get()).equals("default");
					boolean bl3 = ((String)optional.get()).equals("default_1_1") || bl2 && worldGenSettingsDynamic.get("generatorVersion").asInt(0) == 0;
					boolean bl4 = ((String)optional.get()).equals("amplified");
					boolean bl5 = ((String)optional.get()).equals("largebiomes");
					dynamic = createGeneratorSettings(
						l,
						worldGenSettingsDynamic,
						worldGenSettingsDynamic.createString(bl4 ? "minecraft:amplified" : "minecraft:overworld"),
						createBiomeSource(worldGenSettingsDynamic, l, bl3, bl5)
					);
			}
		}

		boolean bl6 = worldGenSettingsDynamic.get("MapFeatures").asBoolean(true);
		boolean bl7 = worldGenSettingsDynamic.get("BonusChest").asBoolean(false);
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("seed"), dynamicOps.createLong(l));
		builder.put(dynamicOps.createString("generate_features"), dynamicOps.createBoolean(bl6));
		builder.put(dynamicOps.createString("bonus_chest"), dynamicOps.createBoolean(bl7));
		builder.put(dynamicOps.createString("dimensions"), createDimensionSettings(worldGenSettingsDynamic, l, dynamic, bl));
		optional2.ifPresent(legacyCustomOptions -> builder.put(dynamicOps.createString("legacy_custom_options"), dynamicOps.createString(legacyCustomOptions)));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
	}

	protected static <T> Dynamic<T> createDefaultOverworldGeneratorSettings(Dynamic<T> worldGenSettingsDynamic, long seed) {
		return createGeneratorSettings(
			seed, worldGenSettingsDynamic, worldGenSettingsDynamic.createString("minecraft:overworld"), createBiomeSource(worldGenSettingsDynamic, seed, false, false)
		);
	}

	protected static <T> T createDimensionSettings(Dynamic<T> worldGenSettingsDynamic, long seed, Dynamic<T> generatorSettingsDynamic, boolean caves) {
		DynamicOps<T> dynamicOps = worldGenSettingsDynamic.getOps();
		return dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("minecraft:overworld"),
				dynamicOps.createMap(
					ImmutableMap.of(
						dynamicOps.createString("type"),
						dynamicOps.createString("minecraft:overworld" + (caves ? "_caves" : "")),
						dynamicOps.createString("generator"),
						generatorSettingsDynamic.getValue()
					)
				),
				dynamicOps.createString("minecraft:the_nether"),
				dynamicOps.createMap(
					ImmutableMap.of(
						dynamicOps.createString("type"),
						dynamicOps.createString("minecraft:the_nether"),
						dynamicOps.createString("generator"),
						createGeneratorSettings(
								seed,
								worldGenSettingsDynamic,
								worldGenSettingsDynamic.createString("minecraft:nether"),
								worldGenSettingsDynamic.createMap(
									ImmutableMap.of(
										worldGenSettingsDynamic.createString("type"),
										worldGenSettingsDynamic.createString("minecraft:multi_noise"),
										worldGenSettingsDynamic.createString("seed"),
										worldGenSettingsDynamic.createLong(seed),
										worldGenSettingsDynamic.createString("preset"),
										worldGenSettingsDynamic.createString("minecraft:nether")
									)
								)
							)
							.getValue()
					)
				),
				dynamicOps.createString("minecraft:the_end"),
				dynamicOps.createMap(
					ImmutableMap.of(
						dynamicOps.createString("type"),
						dynamicOps.createString("minecraft:the_end"),
						dynamicOps.createString("generator"),
						createGeneratorSettings(
								seed,
								worldGenSettingsDynamic,
								worldGenSettingsDynamic.createString("minecraft:end"),
								worldGenSettingsDynamic.createMap(
									ImmutableMap.of(
										worldGenSettingsDynamic.createString("type"),
										worldGenSettingsDynamic.createString("minecraft:the_end"),
										worldGenSettingsDynamic.createString("seed"),
										worldGenSettingsDynamic.createLong(seed)
									)
								)
							)
							.getValue()
					)
				)
			)
		);
	}

	private static <T> Map<Dynamic<T>, Dynamic<T>> createFlatWorldStructureSettings(
		DynamicOps<T> worldGenSettingsDynamicOps, OptionalDynamic<T> generatorOptionsDynamic
	) {
		MutableInt mutableInt = new MutableInt(32);
		MutableInt mutableInt2 = new MutableInt(3);
		MutableInt mutableInt3 = new MutableInt(128);
		MutableBoolean mutableBoolean = new MutableBoolean(false);
		Map<String, StructureSeparationDataFix.Information> map = Maps.<String, StructureSeparationDataFix.Information>newHashMap();
		if (generatorOptionsDynamic.result().isEmpty()) {
			mutableBoolean.setTrue();
			map.put("minecraft:village", STRUCTURE_SPACING.get("minecraft:village"));
		}

		generatorOptionsDynamic.get("structures")
			.flatMap(Dynamic::getMapValues)
			.ifSuccess(
				map2 -> map2.forEach(
						(oldStructureName, dynamic) -> dynamic.getMapValues()
								.result()
								.ifPresent(
									map2x -> map2x.forEach(
											(propertyName, spacing) -> {
												String string = oldStructureName.asString("");
												String string2 = propertyName.asString("");
												String string3 = spacing.asString("");
												if ("stronghold".equals(string)) {
													mutableBoolean.setTrue();
													switch (string2) {
														case "distance":
															mutableInt.setValue(parseInt(string3, mutableInt.getValue(), 1));
															return;
														case "spread":
															mutableInt2.setValue(parseInt(string3, mutableInt2.getValue(), 1));
															return;
														case "count":
															mutableInt3.setValue(parseInt(string3, mutableInt3.getValue(), 1));
															return;
													}
												} else {
													switch (string2) {
														case "distance":
															switch (string) {
																case "village":
																	insertStructureSettings(map, "minecraft:village", string3, 9);
																	return;
																case "biome_1":
																	insertStructureSettings(map, "minecraft:desert_pyramid", string3, 9);
																	insertStructureSettings(map, "minecraft:igloo", string3, 9);
																	insertStructureSettings(map, "minecraft:jungle_pyramid", string3, 9);
																	insertStructureSettings(map, "minecraft:swamp_hut", string3, 9);
																	insertStructureSettings(map, "minecraft:pillager_outpost", string3, 9);
																	return;
																case "endcity":
																	insertStructureSettings(map, "minecraft:endcity", string3, 1);
																	return;
																case "mansion":
																	insertStructureSettings(map, "minecraft:mansion", string3, 1);
																	return;
																default:
																	return;
															}
														case "separation":
															if ("oceanmonument".equals(string)) {
																StructureSeparationDataFix.Information information = (StructureSeparationDataFix.Information)map.getOrDefault(
																	"minecraft:monument", STRUCTURE_SPACING.get("minecraft:monument")
																);
																int i = parseInt(string3, information.separation, 1);
																map.put("minecraft:monument", new StructureSeparationDataFix.Information(i, information.separation, information.salt));
															}

															return;
														case "spacing":
															if ("oceanmonument".equals(string)) {
																insertStructureSettings(map, "minecraft:monument", string3, 1);
															}

															return;
													}
												}
											}
										)
								)
					)
			);
		Builder<Dynamic<T>, Dynamic<T>> builder = ImmutableMap.builder();
		builder.put(
			generatorOptionsDynamic.createString("structures"),
			generatorOptionsDynamic.createMap(
				(Map<? extends Dynamic<?>, ? extends Dynamic<?>>)map.entrySet()
					.stream()
					.collect(
						Collectors.toMap(
							entry -> generatorOptionsDynamic.createString((String)entry.getKey()),
							entry -> ((StructureSeparationDataFix.Information)entry.getValue()).method_28288(worldGenSettingsDynamicOps)
						)
					)
			)
		);
		if (mutableBoolean.isTrue()) {
			builder.put(
				generatorOptionsDynamic.createString("stronghold"),
				generatorOptionsDynamic.createMap(
					ImmutableMap.of(
						generatorOptionsDynamic.createString("distance"),
						generatorOptionsDynamic.createInt(mutableInt.getValue()),
						generatorOptionsDynamic.createString("spread"),
						generatorOptionsDynamic.createInt(mutableInt2.getValue()),
						generatorOptionsDynamic.createString("count"),
						generatorOptionsDynamic.createInt(mutableInt3.getValue())
					)
				)
			);
		}

		return builder.build();
	}

	private static int parseInt(String string, int defaultValue) {
		return NumberUtils.toInt(string, defaultValue);
	}

	private static int parseInt(String string, int defaultValue, int minValue) {
		return Math.max(minValue, parseInt(string, defaultValue));
	}

	private static void insertStructureSettings(Map<String, StructureSeparationDataFix.Information> map, String structureId, String spacingStr, int minSpacing) {
		StructureSeparationDataFix.Information information = (StructureSeparationDataFix.Information)map.getOrDefault(structureId, STRUCTURE_SPACING.get(structureId));
		int i = parseInt(spacingStr, information.spacing, minSpacing);
		map.put(structureId, new StructureSeparationDataFix.Information(i, information.separation, information.salt));
	}

	static final class Information {
		public static final Codec<StructureSeparationDataFix.Information> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("spacing").forGetter(information -> information.spacing),
						Codec.INT.fieldOf("separation").forGetter(information -> information.separation),
						Codec.INT.fieldOf("salt").forGetter(information -> information.salt)
					)
					.apply(instance, StructureSeparationDataFix.Information::new)
		);
		final int spacing;
		final int separation;
		final int salt;

		public Information(int spacing, int separation, int salt) {
			this.spacing = spacing;
			this.separation = separation;
			this.salt = salt;
		}

		public <T> Dynamic<T> method_28288(DynamicOps<T> dynamicOps) {
			return new Dynamic<>(dynamicOps, (T)CODEC.encodeStart(dynamicOps, this).result().orElse(dynamicOps.emptyMap()));
		}
	}
}
