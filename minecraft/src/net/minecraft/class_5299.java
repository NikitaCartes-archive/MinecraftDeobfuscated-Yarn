package net.minecraft;

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
import net.minecraft.datafixer.TypeReferences;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class class_5299 extends DataFix {
	private static final ImmutableMap<String, class_5299.class_5300> field_24647 = ImmutableMap.<String, class_5299.class_5300>builder()
		.put("minecraft:village", new class_5299.class_5300(32, 8, 10387312))
		.put("minecraft:desert_pyramid", new class_5299.class_5300(32, 8, 14357617))
		.put("minecraft:igloo", new class_5299.class_5300(32, 8, 14357618))
		.put("minecraft:jungle_pyramid", new class_5299.class_5300(32, 8, 14357619))
		.put("minecraft:swamp_hut", new class_5299.class_5300(32, 8, 14357620))
		.put("minecraft:pillager_outpost", new class_5299.class_5300(32, 8, 165745296))
		.put("minecraft:monument", new class_5299.class_5300(32, 5, 10387313))
		.put("minecraft:endcity", new class_5299.class_5300(20, 11, 10387313))
		.put("minecraft:mansion", new class_5299.class_5300(80, 20, 10387319))
		.build();

	public class_5299(Schema schema) {
		super(schema, true);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"WorldGenSettings building",
			this.getInputSchema().getType(TypeReferences.CHUNK_GENERATOR_SETTINGS),
			typed -> typed.update(DSL.remainderFinder(), class_5299::method_28271)
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
		Dynamic<T> dynamic2;
		if (optional.equals(Optional.of("customized"))) {
			dynamic2 = method_28268(l, dynamic, dynamic.createString("minecraft:overworld"), method_28272(dynamic, l, false, false));
		} else if (!optional.isPresent()) {
			dynamic2 = method_28268(l, dynamic, dynamic.createString("minecraft:overworld"), method_28272(dynamic, l, false, false));
		} else {
			OptionalDynamic<T> optionalDynamic = dynamic.get("generatorOptions");
			String bl6 = (String)optional.get();
			switch (bl6) {
				case "flat":
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
									optionalDynamic.get("layers").orElseEmptyList(),
									dynamic.createString("biome"),
									dynamic.createString(optionalDynamic.get("biome").asString("plains"))
								)
							)
						)
					);
					break;
				case "debug_all_block_states":
					dynamic2 = dynamic.createMap(ImmutableMap.of(dynamic.createString("type"), dynamic.createString("minecraft:debug")));
					break;
				case "buffet":
					OptionalDynamic<?> optionalDynamic2 = optionalDynamic.get("chunk_generator");
					Optional<String> optional3 = optionalDynamic2.get("type").asString().result();
					Dynamic<T> dynamic3;
					if (Objects.equals(optional3, Optional.of("minecraft:caves"))) {
						dynamic3 = dynamic.createString("minecraft:caves");
					} else if (Objects.equals(optional3, Optional.of("minecraft:floating_islands"))) {
						dynamic3 = dynamic.createString("minecraft:floating_islands");
					} else {
						dynamic3 = dynamic.createString("minecraft:overworld");
					}

					Dynamic<T> dynamic4 = (Dynamic<T>)optionalDynamic.get("biome_source")
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
					boolean bl = ((String)optional.get()).equals("default");
					boolean bl2 = ((String)optional.get()).equals("default_1_1") || bl && dynamic.get("generatorVersion").asInt(0) == 0;
					boolean bl3 = ((String)optional.get()).equals("amplified");
					boolean bl4 = ((String)optional.get()).equals("largebiomes");
					dynamic2 = method_28268(l, dynamic, dynamic.createString(bl3 ? "minecraft:amplified" : "minecraft:overworld"), method_28272(dynamic, l, bl2, bl4));
			}
		}

		boolean bl5 = dynamic.get("MapFeatures").asBoolean(true);
		boolean bl6 = dynamic.get("BonusChest").asBoolean(false);
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("seed"), dynamicOps.createLong(l));
		builder.put(dynamicOps.createString("generate_features"), dynamicOps.createBoolean(bl5));
		builder.put(dynamicOps.createString("bonus_chest"), dynamicOps.createBoolean(bl6));
		builder.put(
			dynamicOps.createString("dimensions"),
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("minecraft:overworld"),
					dynamicOps.createMap(
						ImmutableMap.of(
							dynamicOps.createString("type"), dynamicOps.createString("minecraft:overworld"), dynamicOps.createString("generator"), dynamic2.getValue()
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
			)
		);
		optional2.ifPresent(string -> builder.put(dynamicOps.createString("legacy_custom_options"), dynamicOps.createString(string)));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
	}

	private static <T> Map<Dynamic<T>, Dynamic<T>> method_28275(DynamicOps<T> dynamicOps, OptionalDynamic<T> optionalDynamic) {
		MutableInt mutableInt = new MutableInt(32);
		MutableInt mutableInt2 = new MutableInt(3);
		MutableInt mutableInt3 = new MutableInt(128);
		MutableBoolean mutableBoolean = new MutableBoolean(false);
		Map<String, class_5299.class_5300> map = Maps.<String, class_5299.class_5300>newHashMap();
		optionalDynamic.get("structures")
			.flatMap(Dynamic::getMapValues)
			.result()
			.ifPresent(map2 -> map2.forEach((dynamic, dynamic2) -> dynamic2.getMapValues().result().ifPresent(map2x -> map2x.forEach((dynamic2x, dynamic3) -> {
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
											class_5299.class_5300 lv = (class_5299.class_5300)map.getOrDefault("minecraft:monument", field_24647.get("minecraft:monument"));
											int i = method_28280(string3, lv.field_24650, 1);
											map.put("minecraft:monument", new class_5299.class_5300(i, lv.field_24650, lv.field_24651));
										}

										return;
									case "spacing":
										if ("oceanmonument".equals(string)) {
											method_28281(map, "minecraft:monument", string3, 1);
										}

										return;
								}
							}
						}))));
		Builder<Dynamic<T>, Dynamic<T>> builder = ImmutableMap.builder();
		builder.put(
			optionalDynamic.createString("structures"),
			optionalDynamic.createMap(
				(Map<? extends Dynamic<?>, ? extends Dynamic<?>>)map.entrySet()
					.stream()
					.collect(
						Collectors.toMap(
							entry -> optionalDynamic.createString((String)entry.getKey()), entry -> ((class_5299.class_5300)entry.getValue()).method_28288(dynamicOps)
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

	private static void method_28281(Map<String, class_5299.class_5300> map, String string, String string2, int i) {
		class_5299.class_5300 lv = (class_5299.class_5300)map.getOrDefault(string, field_24647.get(string));
		int j = method_28280(string2, lv.field_24649, i);
		map.put(string, new class_5299.class_5300(j, lv.field_24650, lv.field_24651));
	}

	static final class class_5300 {
		public static final Codec<class_5299.class_5300> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("spacing").forGetter(arg -> arg.field_24649),
						Codec.INT.fieldOf("separation").forGetter(arg -> arg.field_24650),
						Codec.INT.fieldOf("salt").forGetter(arg -> arg.field_24651)
					)
					.apply(instance, class_5299.class_5300::new)
		);
		private final int field_24649;
		private final int field_24650;
		private final int field_24651;

		public class_5300(int i, int j, int k) {
			this.field_24649 = i;
			this.field_24650 = j;
			this.field_24651 = k;
		}

		public <T> Dynamic<T> method_28288(DynamicOps<T> dynamicOps) {
			return new Dynamic<>(dynamicOps, (T)CODEC.encodeStart(dynamicOps, this).result().orElse(dynamicOps.emptyMap()));
		}
	}
}
