package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenSettingsHeightAndBiomeFix extends DataFix {
	private static final String NAME = "WorldGenSettingsHeightAndBiomeFix";
	public static final String HAS_INCREASED_HEIGHT_ALREADY_KEY = "has_increased_height_already";

	public WorldGenSettingsHeightAndBiomeFix(Schema outputSchema) {
		super(outputSchema, true);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.WORLD_GEN_SETTINGS);
		OpticFinder<?> opticFinder = type.findField("dimensions");
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.WORLD_GEN_SETTINGS);
		Type<?> type3 = type2.findFieldType("dimensions");
		return this.fixTypeEverywhereTyped(
			"WorldGenSettingsHeightAndBiomeFix",
			type,
			type2,
			worldGenSettingsTyped -> {
				OptionalDynamic<?> optionalDynamic = worldGenSettingsTyped.get(DSL.remainderFinder()).get("has_increased_height_already");
				boolean bl = optionalDynamic.result().isEmpty();
				boolean bl2 = optionalDynamic.asBoolean(true);
				return worldGenSettingsTyped.update(DSL.remainderFinder(), worldGenSettingsDynamic -> worldGenSettingsDynamic.remove("has_increased_height_already"))
					.updateTyped(
						opticFinder,
						type3,
						dimensionsTyped -> Util.apply(
								dimensionsTyped,
								type3,
								dimensionsDynamic -> dimensionsDynamic.update(
										"minecraft:overworld",
										overworldDimensionDynamic -> overworldDimensionDynamic.update(
												"generator",
												overworldGeneratorDynamic -> {
													String string = overworldGeneratorDynamic.get("type").asString("");
													if ("minecraft:noise".equals(string)) {
														MutableBoolean mutableBoolean = new MutableBoolean();
														overworldGeneratorDynamic = overworldGeneratorDynamic.update(
															"biome_source",
															overworldBiomeSourceDynamic -> {
																String stringx = overworldBiomeSourceDynamic.get("type").asString("");
																if ("minecraft:vanilla_layered".equals(stringx) || bl && "minecraft:multi_noise".equals(stringx)) {
																	if (overworldBiomeSourceDynamic.get("large_biomes").asBoolean(false)) {
																		mutableBoolean.setTrue();
																	}

																	return overworldBiomeSourceDynamic.createMap(
																		ImmutableMap.of(
																			overworldBiomeSourceDynamic.createString("preset"),
																			overworldBiomeSourceDynamic.createString("minecraft:overworld"),
																			overworldBiomeSourceDynamic.createString("type"),
																			overworldBiomeSourceDynamic.createString("minecraft:multi_noise")
																		)
																	);
																} else {
																	return overworldBiomeSourceDynamic;
																}
															}
														);
														return mutableBoolean.booleanValue()
															? overworldGeneratorDynamic.update(
																"settings",
																overworldGeneratorSettingsDynamic -> "minecraft:overworld".equals(overworldGeneratorSettingsDynamic.asString(""))
																		? overworldGeneratorSettingsDynamic.createString("minecraft:large_biomes")
																		: overworldGeneratorSettingsDynamic
															)
															: overworldGeneratorDynamic;
													} else if ("minecraft:flat".equals(string)) {
														return bl2
															? overworldGeneratorDynamic
															: overworldGeneratorDynamic.update(
																"settings",
																overworldGeneratorSettingsDynamic -> overworldGeneratorSettingsDynamic.update("layers", WorldGenSettingsHeightAndBiomeFix::fillWithAir)
															);
													} else {
														return overworldGeneratorDynamic;
													}
												}
											)
									)
							)
					);
			}
		);
	}

	private static Dynamic<?> fillWithAir(Dynamic<?> dynamic) {
		Dynamic<?> dynamic2 = dynamic.createMap(
			ImmutableMap.of(dynamic.createString("height"), dynamic.createInt(64), dynamic.createString("block"), dynamic.createString("minecraft:air"))
		);
		return dynamic.createList(Stream.concat(Stream.of(dynamic2), dynamic.asStream()));
	}
}
