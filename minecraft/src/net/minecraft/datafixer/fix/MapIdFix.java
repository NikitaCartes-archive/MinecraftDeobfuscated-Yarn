package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class MapIdFix extends DataFix {
	public MapIdFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"Map id fix",
			this.getInputSchema().getType(TypeReferences.SAVED_DATA_MAP_DATA),
			mapDatTyped -> mapDatTyped.update(
					DSL.remainderFinder(), mapDatDynamic -> mapDatDynamic.createMap(ImmutableMap.of(mapDatDynamic.createString("data"), mapDatDynamic))
				)
		);
	}
}
