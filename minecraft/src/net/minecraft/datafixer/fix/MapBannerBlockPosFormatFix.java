package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;

public class MapBannerBlockPosFormatFix extends DataFix {
	public MapBannerBlockPosFormatFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	private static <T> Dynamic<T> update(Dynamic<T> mapDataDynamic) {
		return mapDataDynamic.update("banners", banners -> banners.createList(banners.asStream().map(banner -> banner.update("Pos", FixUtil::fixBlockPos))));
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"MapBannerBlockPosFormatFix",
			this.getInputSchema().getType(TypeReferences.SAVED_DATA_MAP_DATA),
			mapDatTyped -> mapDatTyped.update(DSL.remainderFinder(), mapDatDynamic -> mapDatDynamic.update("data", MapBannerBlockPosFormatFix::update))
		);
	}
}
