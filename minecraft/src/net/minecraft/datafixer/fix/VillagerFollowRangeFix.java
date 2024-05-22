package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class VillagerFollowRangeFix extends ChoiceFix {
	private static final double OLD_RANGE = 16.0;
	private static final double NEW_RANGE = 48.0;

	public VillagerFollowRangeFix(Schema outputSchema) {
		super(outputSchema, false, "Villager Follow Range Fix", TypeReferences.ENTITY, "minecraft:villager");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), VillagerFollowRangeFix::fix);
	}

	private static Dynamic<?> fix(Dynamic<?> villagerDynamic) {
		return villagerDynamic.update(
			"Attributes",
			attributesDynamic -> villagerDynamic.createList(
					attributesDynamic.asStream()
						.map(
							attributeDynamic -> attributeDynamic.get("Name").asString("").equals("generic.follow_range") && attributeDynamic.get("Base").asDouble(0.0) == 16.0
									? attributeDynamic.set("Base", attributeDynamic.createDouble(48.0))
									: attributeDynamic
						)
				)
		);
	}
}
