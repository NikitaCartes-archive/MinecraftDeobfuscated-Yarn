package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import org.slf4j.Logger;

public class StructuresToConfiguredStructuresFix extends DataFix {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Map<String, StructuresToConfiguredStructuresFix.Mapping> STRUCTURE_TO_CONFIGURED_STRUCTURES_MAPPING = ImmutableMap.<String, StructuresToConfiguredStructuresFix.Mapping>builder()
		.put(
			"mineshaft",
			StructuresToConfiguredStructuresFix.Mapping.create(
				Map.of(List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands"), "minecraft:mineshaft_mesa"), "minecraft:mineshaft"
			)
		)
		.put(
			"shipwreck",
			StructuresToConfiguredStructuresFix.Mapping.create(
				Map.of(List.of("minecraft:beach", "minecraft:snowy_beach"), "minecraft:shipwreck_beached"), "minecraft:shipwreck"
			)
		)
		.put(
			"ocean_ruin",
			StructuresToConfiguredStructuresFix.Mapping.create(
				Map.of(List.of("minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean"), "minecraft:ocean_ruin_warm"),
				"minecraft:ocean_ruin_cold"
			)
		)
		.put(
			"village",
			StructuresToConfiguredStructuresFix.Mapping.create(
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
			StructuresToConfiguredStructuresFix.Mapping.create(
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
				"minecraft:ruined_portal"
			)
		)
		.put("pillager_outpost", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:pillager_outpost"))
		.put("mansion", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:mansion"))
		.put("jungle_pyramid", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:jungle_pyramid"))
		.put("desert_pyramid", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:desert_pyramid"))
		.put("igloo", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:igloo"))
		.put("swamp_hut", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:swamp_hut"))
		.put("stronghold", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:stronghold"))
		.put("monument", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:monument"))
		.put("fortress", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:fortress"))
		.put("endcity", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:end_city"))
		.put("buried_treasure", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:buried_treasure"))
		.put("nether_fossil", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:nether_fossil"))
		.put("bastion_remnant", StructuresToConfiguredStructuresFix.Mapping.create("minecraft:bastion_remnant"))
		.build();

	public StructuresToConfiguredStructuresFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		Type<?> type2 = this.getInputSchema().getType(TypeReferences.CHUNK);
		return this.writeFixAndRead("StucturesToConfiguredStructures", type, type2, this::fixChunk);
	}

	private Dynamic<?> fixChunk(Dynamic<?> chunkDynamic) {
		return chunkDynamic.update(
			"structures",
			structuresDynamic -> structuresDynamic.update("starts", startsDynamic -> this.fixStructureStarts(startsDynamic, chunkDynamic))
					.update("References", referencesDynamic -> this.fixStructureReferences(referencesDynamic, chunkDynamic))
		);
	}

	private Dynamic<?> fixStructureStarts(Dynamic<?> startsDynamic, Dynamic<?> chunkDynamic) {
		Map<? extends Dynamic<?>, ? extends Dynamic<?>> map = (Map<? extends Dynamic<?>, ? extends Dynamic<?>>)startsDynamic.getMapValues().result().orElse(Map.of());
		HashMap<Dynamic<?>, Dynamic<?>> hashMap = Maps.newHashMap();
		map.forEach((structureId, startDynamic) -> {
			if (!startDynamic.get("id").asString("INVALID").equals("INVALID")) {
				Dynamic<?> dynamic2 = this.mapStructureToConfiguredStructure(structureId, chunkDynamic);
				if (dynamic2 == null) {
					LOGGER.warn("Encountered unknown structure in datafixer: " + structureId.asString("<missing key>"));
				} else {
					hashMap.computeIfAbsent(dynamic2, configuredStructureId -> startDynamic.set("id", dynamic2));
				}
			}
		});
		return chunkDynamic.createMap(hashMap);
	}

	private Dynamic<?> fixStructureReferences(Dynamic<?> referencesDynamic, Dynamic<?> chunkDynamic) {
		Map<? extends Dynamic<?>, ? extends Dynamic<?>> map = (Map<? extends Dynamic<?>, ? extends Dynamic<?>>)referencesDynamic.getMapValues()
			.result()
			.orElse(Map.of());
		HashMap<Dynamic<?>, Dynamic<?>> hashMap = Maps.newHashMap();
		map.forEach(
			(structureId, referenceDynamic) -> {
				if (referenceDynamic.asLongStream().count() != 0L) {
					Dynamic<?> dynamic2 = this.mapStructureToConfiguredStructure(structureId, chunkDynamic);
					if (dynamic2 == null) {
						LOGGER.warn("Encountered unknown structure in datafixer: " + structureId.asString("<missing key>"));
					} else {
						hashMap.compute(
							dynamic2,
							(configuredStructureId, referenceDynamicx) -> referenceDynamicx == null
									? referenceDynamic
									: referenceDynamic.createLongList(LongStream.concat(referenceDynamicx.asLongStream(), referenceDynamic.asLongStream()))
						);
					}
				}
			}
		);
		return chunkDynamic.createMap(hashMap);
	}

	@Nullable
	private Dynamic<?> mapStructureToConfiguredStructure(Dynamic<?> structureIdDynamic, Dynamic<?> chunkDynamic) {
		String string = structureIdDynamic.asString("UNKNOWN").toLowerCase(Locale.ROOT);
		StructuresToConfiguredStructuresFix.Mapping mapping = (StructuresToConfiguredStructuresFix.Mapping)STRUCTURE_TO_CONFIGURED_STRUCTURES_MAPPING.get(string);
		if (mapping == null) {
			return null;
		} else {
			String string2 = mapping.fallback;
			if (!mapping.biomeMapping().isEmpty()) {
				Optional<String> optional = this.getBiomeRepresentativeStructure(chunkDynamic, mapping);
				if (optional.isPresent()) {
					string2 = (String)optional.get();
				}
			}

			return chunkDynamic.createString(string2);
		}
	}

	private Optional<String> getBiomeRepresentativeStructure(Dynamic<?> chunkDynamic, StructuresToConfiguredStructuresFix.Mapping mappingForStructure) {
		Object2IntArrayMap<String> object2IntArrayMap = new Object2IntArrayMap<>();
		chunkDynamic.get("sections")
			.asList(Function.identity())
			.forEach(sectionDynamic -> sectionDynamic.get("biomes").get("palette").asList(Function.identity()).forEach(biomePaletteDynamic -> {
					String string = (String)mappingForStructure.biomeMapping().get(biomePaletteDynamic.asString(""));
					if (string != null) {
						object2IntArrayMap.mergeInt(string, 1, Integer::sum);
					}
				}));
		return object2IntArrayMap.object2IntEntrySet()
			.stream()
			.max(Comparator.comparingInt(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry::getIntValue))
			.map(Entry::getKey);
	}

	static record Mapping(Map<String, String> biomeMapping, String fallback) {

		public static StructuresToConfiguredStructuresFix.Mapping create(String mapping) {
			return new StructuresToConfiguredStructuresFix.Mapping(Map.of(), mapping);
		}

		public static StructuresToConfiguredStructuresFix.Mapping create(Map<List<String>, String> biomeMapping, String fallback) {
			return new StructuresToConfiguredStructuresFix.Mapping(flattenBiomeMapping(biomeMapping), fallback);
		}

		private static Map<String, String> flattenBiomeMapping(Map<List<String>, String> biomeMapping) {
			Builder<String, String> builder = ImmutableMap.builder();

			for (Entry<List<String>, String> entry : biomeMapping.entrySet()) {
				((List)entry.getKey()).forEach(key -> builder.put(key, (String)entry.getValue()));
			}

			return builder.build();
		}
	}
}
