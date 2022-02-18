package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class class_7046 extends DataFix {
	private static final Map<String, class_7046.class_7047> field_37050 = ImmutableMap.<String, class_7046.class_7047>builder()
		.put(
			"mineshaft",
			class_7046.class_7047.method_41029(
				Map.of(List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands"), "minecraft:mineshaft_mesa"), "minecraft:mineshaft"
			)
		)
		.put(
			"shipwreck",
			class_7046.class_7047.method_41029(Map.of(List.of("minecraft:beach", "minecraft:snowy_beach"), "minecraft:shipwreck_beached"), "minecraft:shipwreck")
		)
		.put(
			"ocean_ruin",
			class_7046.class_7047.method_41029(
				Map.of(List.of("minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean"), "minecraft:ocean_ruin_warm"),
				"minecraft:ocean_ruin_cold"
			)
		)
		.put(
			"village",
			class_7046.class_7047.method_41029(
				Map.of(
					List.of("minecraft:desert"),
					"minecraft:village_desert",
					List.of("minecraft:savanna"),
					"minecraft:village_savanna",
					List.of("minecraft:snowy_plains"),
					"minecraft:village_snowy",
					List.of("minecraft:taiga"),
					"minecraft:village_taiga"
				),
				"minecraft:village_plains"
			)
		)
		.put(
			"ruined_portal",
			class_7046.class_7047.method_41029(
				Map.of(
					List.of("minecraft:desert"),
					"minecraft:ruined_portal_desert",
					List.of(
						"minecraft:badlands",
						"minecraft:eroded_badlands",
						"minecraft:wooded_badlands",
						"minecraft:windswept_hills",
						"minecraft:windswept_forest",
						"minecraft:windswept_gravelly_hills",
						"minecraft:savanna_plateau",
						"minecraft:windswept_savanna",
						"minecraft:stony_shore",
						"minecraft:meadow",
						"minecraft:frozen_peaks",
						"minecraft:jagged_peaks",
						"minecraft:stony_peaks",
						"minecraft:snowy_slopes"
					),
					"minecraft:ruined_portal_mountain",
					List.of("minecraft:bamboo_jungle", "minecraft:jungle", "minecraft:sparse_jungle"),
					"minecraft:ruined_portal_jungle",
					List.of(
						"minecraft:deep_frozen_ocean",
						"minecraft:deep_cold_ocean",
						"minecraft:deep_ocean",
						"minecraft:deep_lukewarm_ocean",
						"minecraft:frozen_ocean",
						"minecraft:ocean",
						"minecraft:cold_ocean",
						"minecraft:lukewarm_ocean",
						"minecraft:warm_ocean"
					),
					"minecraft:ruined_portal_ocean"
				),
				"minecraft:ruined_portal_standard"
			)
		)
		.put("pillager_outpost", class_7046.class_7047.method_41027("minecraft:pillager_outpost"))
		.put("mansion", class_7046.class_7047.method_41027("minecraft:mansion"))
		.put("jungle_pyramid", class_7046.class_7047.method_41027("minecraft:jungle_pyramid"))
		.put("desert_pyramid", class_7046.class_7047.method_41027("minecraft:desert_pyramid"))
		.put("igloo", class_7046.class_7047.method_41027("minecraft:igloo"))
		.put("swamp_hut", class_7046.class_7047.method_41027("minecraft:swamp_hut"))
		.put("stronghold", class_7046.class_7047.method_41027("minecraft:stronghold"))
		.put("monument", class_7046.class_7047.method_41027("minecraft:monument"))
		.put("fortress", class_7046.class_7047.method_41027("minecraft:fortress"))
		.put("endcity", class_7046.class_7047.method_41027("minecraft:end_city"))
		.put("buried_treasure", class_7046.class_7047.method_41027("minecraft:buried_treasure"))
		.put("nether_fossil", class_7046.class_7047.method_41027("minecraft:nether_fossil"))
		.put("bastion_remnant", class_7046.class_7047.method_41027("minecraft:bastion_remnant"))
		.build();

	public class_7046(Schema schema) {
		super(schema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		Type<?> type2 = this.getInputSchema().getType(TypeReferences.CHUNK);
		return this.writeFixAndRead("StucturesToConfiguredStructures", type, type2, this::method_41012);
	}

	private Dynamic<?> method_41012(Dynamic<?> dynamic) {
		return dynamic.update(
			"structures",
			dynamic2 -> dynamic2.update("starts", dynamic2x -> this.method_41015(dynamic2x, dynamic))
					.update("References", dynamic2x -> this.method_41020(dynamic2x, dynamic))
		);
	}

	private Dynamic<?> method_41015(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
		Map<? extends Dynamic<?>, ? extends Dynamic<?>> map = (Map<? extends Dynamic<?>, ? extends Dynamic<?>>)dynamic.getMapValues().result().get();
		List<Dynamic<?>> list = new ArrayList();
		map.forEach((dynamicx, dynamic2x) -> {
			if (dynamic2x.get("id").asString("INVALID").equals("INVALID")) {
				list.add(dynamicx);
			}
		});

		for (Dynamic<?> dynamic3 : list) {
			dynamic = dynamic.remove(dynamic3.asString(""));
		}

		return dynamic.updateMapValues(pair -> this.method_41010(pair, dynamic2));
	}

	private Pair<Dynamic<?>, Dynamic<?>> method_41010(Pair<Dynamic<?>, Dynamic<?>> pair, Dynamic<?> dynamic) {
		Dynamic<?> dynamic2 = this.method_41022(pair, dynamic);
		return new Pair<>(dynamic2, pair.getSecond().set("id", dynamic2));
	}

	private Dynamic<?> method_41020(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
		Map<? extends Dynamic<?>, ? extends Dynamic<?>> map = (Map<? extends Dynamic<?>, ? extends Dynamic<?>>)dynamic.getMapValues().result().get();
		List<Dynamic<?>> list = new ArrayList();
		map.forEach((dynamicx, dynamic2x) -> {
			if (dynamic2x.asLongStream().count() == 0L) {
				list.add(dynamicx);
			}
		});

		for (Dynamic<?> dynamic3 : list) {
			dynamic = dynamic.remove(dynamic3.asString(""));
		}

		return dynamic.updateMapValues(pair -> this.method_41018(pair, dynamic2));
	}

	private Pair<Dynamic<?>, Dynamic<?>> method_41018(Pair<Dynamic<?>, Dynamic<?>> pair, Dynamic<?> dynamic) {
		return pair.mapFirst(dynamic2 -> this.method_41022(pair, dynamic));
	}

	private Dynamic<?> method_41022(Pair<Dynamic<?>, Dynamic<?>> pair, Dynamic<?> dynamic) {
		String string = pair.getFirst().asString("UNKNOWN").toLowerCase(Locale.ROOT);
		class_7046.class_7047 lv = (class_7046.class_7047)field_37050.get(string);
		if (lv == null) {
			throw new IllegalStateException("Found unknown structure: " + string);
		} else {
			Dynamic<?> dynamic2 = pair.getSecond();
			String string2 = lv.fallback;
			if (!lv.biomeMapping().isEmpty()) {
				Optional<String> optional = this.method_41013(dynamic, lv);
				if (optional.isPresent()) {
					string2 = (String)optional.get();
				}
			}

			return dynamic2.createString(string2);
		}
	}

	private Optional<String> method_41013(Dynamic<?> dynamic, class_7046.class_7047 arg) {
		Object2IntArrayMap<String> object2IntArrayMap = new Object2IntArrayMap<>();
		dynamic.get("sections")
			.asList(Function.identity())
			.forEach(dynamicx -> dynamicx.get("biomes").get("palette").asList(Function.identity()).forEach(dynamicxx -> {
					String string = (String)arg.biomeMapping().get(dynamicxx.asString(""));
					if (string != null) {
						object2IntArrayMap.mergeInt(string, 1, Integer::sum);
					}
				}));
		return object2IntArrayMap.object2IntEntrySet()
			.stream()
			.max(Comparator.comparingInt(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry::getIntValue))
			.map(Entry::getKey);
	}

	static record class_7047(Map<String, String> biomeMapping, String fallback) {

		public static class_7046.class_7047 method_41027(String string) {
			return new class_7046.class_7047(Map.of(), string);
		}

		public static class_7046.class_7047 method_41029(Map<List<String>, String> map, String string) {
			return new class_7046.class_7047(method_41028(map), string);
		}

		private static Map<String, String> method_41028(Map<List<String>, String> map) {
			Builder<String, String> builder = ImmutableMap.builder();

			for (Entry<List<String>, String> entry : map.entrySet()) {
				((List)entry.getKey()).forEach(string -> builder.put(string, (String)entry.getValue()));
			}

			return builder.build();
		}
	}
}
