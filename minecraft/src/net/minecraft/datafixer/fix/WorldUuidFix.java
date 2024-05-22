package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import org.slf4j.Logger;

public class WorldUuidFix extends AbstractUuidFix {
	private static final Logger LOGGER = LogUtils.getLogger();

	public WorldUuidFix(Schema outputSchema) {
		super(outputSchema, TypeReferences.LEVEL);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"LevelUUIDFix",
			this.getInputSchema().getType(this.typeReference),
			levelTyped -> levelTyped.updateTyped(DSL.remainderFinder(), levelTyped2 -> levelTyped2.update(DSL.remainderFinder(), levelDynamic -> {
						levelDynamic = this.fixCustomBossEvents(levelDynamic);
						levelDynamic = this.fixDragonUuid(levelDynamic);
						return this.fixWanderingTraderId(levelDynamic);
					}))
		);
	}

	private Dynamic<?> fixWanderingTraderId(Dynamic<?> levelDynamic) {
		return (Dynamic<?>)updateStringUuid(levelDynamic, "WanderingTraderId", "WanderingTraderId").orElse(levelDynamic);
	}

	private Dynamic<?> fixDragonUuid(Dynamic<?> levelDynamic) {
		return levelDynamic.update(
			"DimensionData",
			dimensionDataDynamic -> dimensionDataDynamic.updateMapValues(
					entry -> entry.mapSecond(
							dimensionDataValueDynamic -> dimensionDataValueDynamic.update(
									"DragonFight", dragonFightDynamic -> (Dynamic)updateRegularMostLeast(dragonFightDynamic, "DragonUUID", "Dragon").orElse(dragonFightDynamic)
								)
						)
				)
		);
	}

	private Dynamic<?> fixCustomBossEvents(Dynamic<?> levelDynamic) {
		return levelDynamic.update(
			"CustomBossEvents",
			bossbarsDynamic -> bossbarsDynamic.updateMapValues(
					entry -> entry.mapSecond(
							bossbarDynamic -> bossbarDynamic.update(
									"Players",
									playersDynamic -> bossbarDynamic.createList(
											playersDynamic.asStream().map(playerDynamic -> (Dynamic)createArrayFromCompoundUuid(playerDynamic).orElseGet(() -> {
													LOGGER.warn("CustomBossEvents contains invalid UUIDs.");
													return playerDynamic;
												}))
										)
								)
						)
				)
		);
	}
}
