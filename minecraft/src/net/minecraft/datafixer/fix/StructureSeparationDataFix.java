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
			typed -> typed.update(DSL.remainderFinder(), StructureSeparationDataFix::method_28271)
		);
	}

	private static <T> Dynamic<T> method_28268(long l, DynamicLike<T> dynamicLike, Dynamic<T> dynamic, Dynamic<T> dynamic2) {
		return dynamicLike.createMap(
			ImmutableMap.of(
				dynamicLike.createString("type"),
				dynamicLike.createString("minecraft:noise"),
				dynamicLike.createString("biome_source"),
				dynamic2,
				dynamicLike.createString("seed"),
				dynamicLike.createLong(l),
				dynamicLike.createString("settings"),
				dynamic
			)
		);
	}

	private static <T> Dynamic<T> method_28272(Dynamic<T> dynamic, long l, boolean bl, boolean bl2) {
		Builder<Dynamic<T>, Dynamic<T>> builder = ImmutableMap.<Dynamic<T>, Dynamic<T>>builder()
			.put(dynamic.createString("type"), dynamic.createString("minecraft:vanilla_layered"))
			.put(dynamic.createString("seed"), dynamic.createLong(l))
			.put(dynamic.createString("large_biomes"), dynamic.createBoolean(bl2));
		if (bl) {
			builder.put(dynamic.createString("legacy_biome_init_layer"), dynamic.createBoolean(bl));
		}

		return dynamic.createMap(builder.build());
	}

	private static <T> Dynamic<T> method_28271(Dynamic<T> dynamic) {
		DynamicOps<T> dynamicOps = dynamic.getOps();
		long l = dynamic.get("RandomSeed").asLong(0L);
		Optional<String> optional = dynamic.get("generatorName").asString().<String>map(string -> string.toLowerCase(Locale.ROOT)).result();
		Optional<String> optional2 = (Optional<String>)dynamic.get("legacy_custom_options")
			.asString()
			.result()
			.map(Optional::of)
			.orElseGet(() -> optional.equals(Optional.of("customized")) ? dynamic.get("generatorOptions").asString().result() : Optional.empty());
		boolean bl = false;
		Dynamic<T> dynamic2;
		if (optional.equals(Optional.of("customized"))) {
			dynamic2 = method_29916(dynamic, l);
		} else if (!optional.isPresent()) {
			dynamic2 = method_29916(dynamic, l);
		} else {
			String bl6 = (String)optional.get();
			switch (bl6) {
				case "flat":
					OptionalDynamic<T> optionalDynamic = dynamic.get("generatorOptions");
					Map<Dynamic<T>, Dynamic<T>> map = method_28275(dynamicOps, optionalDynamic);
					dynamic2 = dynamic.createMap(
						ImmutableMap.of(
							dynamic.createString("type"),
							dynamic.createString("minecraft:flat"),
							dynamic.createString("settings"),
							dynamic.createMap(
								ImmutableMap.of(
									dynamic.createString("structures"),
									dynamic.createMap(map),
									dynamic.createString("layers"),
									(Dynamic<?>)optionalDynamic.get("layers")
										.result()
										.orElseGet(
											() -> dynamic.createList(
													Stream.of(
														dynamic.createMap(
															ImmutableMap.of(dynamic.createString("height"), dynamic.createInt(1), dynamic.createString("block"), dynamic.createString("minecraft:bedrock"))
														),
														dynamic.createMap(
															ImmutableMap.of(dynamic.createString("height"), dynamic.createInt(2), dynamic.createString("block"), dynamic.createString("minecraft:dirt"))
														),
														dynamic.createMap(
															ImmutableMap.of(
																dynamic.createString("height"), dynamic.createInt(1), dynamic.createString("block"), dynamic.createString("minecraft:grass_block")
															)
														)
													)
												)
										),
									dynamic.createString("biome"),
									dynamic.createString(optionalDynamic.get("biome").asString("minecraft:plains"))
								)
							)
						)
					);
					break;
				case "debug_all_block_states":
					dynamic2 = dynamic.createMap(ImmutableMap.of(dynamic.createString("type"), dynamic.createString("minecraft:debug")));
					break;
				case "buffet":
					OptionalDynamic<T> optionalDynamic2 = dynamic.get("generatorOptions");
					OptionalDynamic<?> optionalDynamic3 = optionalDynamic2.get("chunk_generator");
					Optional<String> optional3 = optionalDynamic3.get("type").asString().result();
					Dynamic<T> dynamic3;
					if (Objects.equals(optional3, Optional.of("minecraft:caves"))) {
						dynamic3 = dynamic.createString("minecraft:caves");
						bl = true;
					} else if (Objects.equals(optional3, Optional.of("minecraft:floating_islands"))) {
						dynamic3 = dynamic.createString("minecraft:floating_islands");
					} else {
						dynamic3 = dynamic.createString("minecraft:overworld");
					}

					Dynamic<T> dynamic4 = (Dynamic<T>)optionalDynamic2.get("biome_source")
						.result()
						.orElseGet(() -> dynamic.createMap(ImmutableMap.of(dynamic.createString("type"), dynamic.createString("minecraft:fixed"))));
					Dynamic<T> dynamic5;
					if (dynamic4.get("type").asString().result().equals(Optional.of("minecraft:fixed"))) {
						String string = (String)dynamic4.get("options")
							.get("biomes")
							.asStream()
							.findFirst()
							.flatMap(dynamicx -> dynamicx.asString().result())
							.orElse("minecraft:ocean");
						dynamic5 = dynamic4.remove("options").set("biome", dynamic.createString(string));
					} else {
						dynamic5 = dynamic4;
					}

					dynamic2 = method_28268(l, dynamic, dynamic3, dynamic5);
					break;
				default:
					boolean bl2 = ((String)optional.get()).equals("default");
					boolean bl3 = ((String)optional.get()).equals("default_1_1") || bl2 && dynamic.get("generatorVersion").asInt(0) == 0;
					boolean bl4 = ((String)optional.get()).equals("amplified");
					boolean bl5 = ((String)optional.get()).equals("largebiomes");
					dynamic2 = method_28268(l, dynamic, dynamic.createString(bl4 ? "minecraft:amplified" : "minecraft:overworld"), method_28272(dynamic, l, bl3, bl5));
			}
		}

		boolean bl6 = dynamic.get("MapFeatures").asBoolean(true);
		boolean bl7 = dynamic.get("BonusChest").asBoolean(false);
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("seed"), dynamicOps.createLong(l));
		builder.put(dynamicOps.createString("generate_features"), dynamicOps.createBoolean(bl6));
		builder.put(dynamicOps.createString("bonus_chest"), dynamicOps.createBoolean(bl7));
		builder.put(dynamicOps.createString("dimensions"), method_29917(dynamic, l, dynamic2, bl));
		optional2.ifPresent(string -> builder.put(dynamicOps.createString("legacy_custom_options"), dynamicOps.createString(string)));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
	}

	protected static <T> Dynamic<T> method_29916(Dynamic<T> dynamic, long l) {
		return method_28268(l, dynamic, dynamic.createString("minecraft:overworld"), method_28272(dynamic, l, false, false));
	}

	protected static <T> T method_29917(Dynamic<T> dynamic, long l, Dynamic<T> dynamic2, boolean bl) {
		DynamicOps<T> dynamicOps = dynamic.getOps();
		return dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("minecraft:overworld"),
				dynamicOps.createMap(
					ImmutableMap.of(
						dynamicOps.createString("type"),
						dynamicOps.createString("minecraft:overworld" + (bl ? "_caves" : "")),
						dynamicOps.createString("generator"),
						dynamic2.getValue()
					)
				),
				dynamicOps.createString("minecraft:the_nether"),
				dynamicOps.createMap(
					ImmutableMap.of(
						dynamicOps.createString("type"),
						dynamicOps.createString("minecraft:the_nether"),
						dynamicOps.createString("generator"),
						method_28268(
								l,
								dynamic,
								dynamic.createString("minecraft:nether"),
								dynamic.createMap(
									ImmutableMap.of(
										dynamic.createString("type"),
										dynamic.createString("minecraft:multi_noise"),
										dynamic.createString("seed"),
										dynamic.createLong(l),
										dynamic.createString("preset"),
										dynamic.createString("minecraft:nether")
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
						method_28268(
								l,
								dynamic,
								dynamic.createString("minecraft:end"),
								dynamic.createMap(
									ImmutableMap.of(dynamic.createString("type"), dynamic.createString("minecraft:the_end"), dynamic.createString("seed"), dynamic.createLong(l))
								)
							)
							.getValue()
					)
				)
			)
		);
	}

	private static <T> Map<Dynamic<T>, Dynamic<T>> method_28275(DynamicOps<T> dynamicOps, OptionalDynamic<T> optionalDynamic) {
		MutableInt mutableInt = new MutableInt(32);
		MutableInt mutableInt2 = new MutableInt(3);
		MutableInt mutableInt3 = new MutableInt(128);
		MutableBoolean mutableBoolean = new MutableBoolean(false);
		Map<String, StructureSeparationDataFix.Information> map = Maps.<String, StructureSeparationDataFix.Information>newHashMap();
		if (!optionalDynamic.result().isPresent()) {
			mutableBoolean.setTrue();
			map.put("minecraft:village", STRUCTURE_SPACING.get("minecraft:village"));
		}

		optionalDynamic.get("structures")
			.flatMap(Dynamic::getMapValues)
			.result()
			.ifPresent(
				map2 -> map2.forEach(
						(dynamic, dynamic2) -> dynamic2.getMapValues()
								.result()
								.ifPresent(
									map2x -> map2x.forEach(
											(dynamic2x, dynamic3) -> {
												String string = dynamic.asString("");
												String string2 = dynamic2x.asString("");
												String string3 = dynamic3.asString("");
												if ("stronghold".equals(string)) {
													mutableBoolean.setTrue();
													switch (string2) {
														case "distance":
															mutableInt.setValue(method_28280(string3, mutableInt.getValue(), 1));
															return;
														case "spread":
															mutableInt2.setValue(method_28280(string3, mutableInt2.getValue(), 1));
															return;
														case "count":
															mutableInt3.setValue(method_28280(string3, mutableInt3.getValue(), 1));
															return;
													}
												} else {
													switch (string2) {
														case "distance":
															switch (string) {
																case "village":
																	method_28281(map, "minecraft:village", string3, 9);
																	return;
																case "biome_1":
																	method_28281(map, "minecraft:desert_pyramid", string3, 9);
																	method_28281(map, "minecraft:igloo", string3, 9);
																	method_28281(map, "minecraft:jungle_pyramid", string3, 9);
																	method_28281(map, "minecraft:swamp_hut", string3, 9);
																	method_28281(map, "minecraft:pillager_outpost", string3, 9);
																	return;
																case "endcity":
																	method_28281(map, "minecraft:endcity", string3, 1);
																	return;
																case "mansion":
																	method_28281(map, "minecraft:mansion", string3, 1);
																	return;
																default:
																	return;
															}
														case "separation":
															if ("oceanmonument".equals(string)) {
																StructureSeparationDataFix.Information information = (StructureSeparationDataFix.Information)map.getOrDefault(
																	"minecraft:monument", STRUCTURE_SPACING.get("minecraft:monument")
																);
																int i = method_28280(string3, information.separation, 1);
																map.put("minecraft:monument", new StructureSeparationDataFix.Information(i, information.separation, information.salt));
															}

															return;
														case "spacing":
															if ("oceanmonument".equals(string)) {
																method_28281(map, "minecraft:monument", string3, 1);
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
			optionalDynamic.createString("structures"),
			optionalDynamic.createMap(
				(Map<? extends Dynamic<?>, ? extends Dynamic<?>>)map.entrySet()
					.stream()
					.collect(
						Collectors.toMap(
							entry -> optionalDynamic.createString((String)entry.getKey()),
							entry -> ((StructureSeparationDataFix.Information)entry.getValue()).method_28288(dynamicOps)
						)
					)
			)
		);
		if (mutableBoolean.isTrue()) {
			builder.put(
				optionalDynamic.createString("stronghold"),
				optionalDynamic.createMap(
					ImmutableMap.of(
						optionalDynamic.createString("distance"),
						optionalDynamic.createInt(mutableInt.getValue()),
						optionalDynamic.createString("spread"),
						optionalDynamic.createInt(mutableInt2.getValue()),
						optionalDynamic.createString("count"),
						optionalDynamic.createInt(mutableInt3.getValue())
					)
				)
			);
		}

		return builder.build();
	}

	private static int method_28279(String string, int i) {
		return NumberUtils.toInt(string, i);
	}

	private static int method_28280(String string, int i, int j) {
		return Math.max(j, method_28279(string, i));
	}

	private static void method_28281(Map<String, StructureSeparationDataFix.Information> map, String string, String string2, int i) {
		StructureSeparationDataFix.Information information = (StructureSeparationDataFix.Information)map.getOrDefault(string, STRUCTURE_SPACING.get(string));
		int j = method_28280(string2, information.spacing, i);
		map.put(string, new StructureSeparationDataFix.Information(j, information.separation, information.salt));
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
