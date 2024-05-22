package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;

public class StructureSettingsFlattenFix extends DataFix {
	public StructureSettingsFlattenFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.WORLD_GEN_SETTINGS);
		OpticFinder<?> opticFinder = type.findField("dimensions");
		return this.fixTypeEverywhereTyped(
			"StructureSettingsFlatten",
			type,
			worldGenSettingsTyped -> worldGenSettingsTyped.updateTyped(
					opticFinder,
					dimensionsTyped -> Util.apply(
							dimensionsTyped, opticFinder.type(), dimensionsDynamic -> dimensionsDynamic.updateMapValues(StructureSettingsFlattenFix::fixDimensionEntry)
						)
				)
		);
	}

	/**
	 * @param dimensionEntry (dimension ID, dimension data)
	 */
	private static Pair<Dynamic<?>, Dynamic<?>> fixDimensionEntry(Pair<Dynamic<?>, Dynamic<?>> dimensionEntry) {
		Dynamic<?> dynamic = dimensionEntry.getSecond();
		return Pair.of(
			dimensionEntry.getFirst(),
			dynamic.update(
				"generator",
				generatorDynamic -> generatorDynamic.update(
						"settings", generatorSettingsDynamic -> generatorSettingsDynamic.update("structures", StructureSettingsFlattenFix::fixStructures)
					)
			)
		);
	}

	private static Dynamic<?> fixStructures(Dynamic<?> structureSettingsDynamic) {
		Dynamic<?> dynamic = structureSettingsDynamic.get("structures")
			.orElseEmptyMap()
			.updateMapValues(
				entry -> entry.mapSecond(structureDynamic -> structureDynamic.set("type", structureSettingsDynamic.createString("minecraft:random_spread")))
			);
		return DataFixUtils.orElse(
			structureSettingsDynamic.get("stronghold")
				.result()
				.map(
					strongholdDynamic -> dynamic.set(
							"minecraft:stronghold", strongholdDynamic.set("type", structureSettingsDynamic.createString("minecraft:concentric_rings"))
						)
				),
			dynamic
		);
	}
}
