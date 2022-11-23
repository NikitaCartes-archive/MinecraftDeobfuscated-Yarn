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
			typed -> typed.updateTyped(DSL.remainderFinder(), typedx -> typedx.update(DSL.remainderFinder(), dynamic -> {
						dynamic = this.fixCustomBossEvents(dynamic);
						dynamic = this.fixDragonUuid(dynamic);
						return this.fixWanderingTraderId(dynamic);
					}))
		);
	}

	private Dynamic<?> fixWanderingTraderId(Dynamic<?> dynamic) {
		return (Dynamic<?>)updateStringUuid(dynamic, "WanderingTraderId", "WanderingTraderId").orElse(dynamic);
	}

	private Dynamic<?> fixDragonUuid(Dynamic<?> dynamic) {
		return dynamic.update(
			"DimensionData",
			dynamicx -> dynamicx.updateMapValues(
					pair -> pair.mapSecond(
							dynamicxx -> dynamicxx.update("DragonFight", dynamicxxx -> (Dynamic)updateRegularMostLeast(dynamicxxx, "DragonUUID", "Dragon").orElse(dynamicxxx))
						)
				)
		);
	}

	private Dynamic<?> fixCustomBossEvents(Dynamic<?> dynamic) {
		return dynamic.update(
			"CustomBossEvents",
			dynamicx -> dynamicx.updateMapValues(
					pair -> pair.mapSecond(
							dynamicxx -> dynamicxx.update(
									"Players", dynamic2 -> dynamicxx.createList(dynamic2.asStream().map(dynamicxxxx -> (Dynamic)createArrayFromCompoundUuid(dynamicxxxx).orElseGet(() -> {
												LOGGER.warn("CustomBossEvents contains invalid UUIDs.");
												return dynamicxxxx;
											})))
								)
						)
				)
		);
	}
}
