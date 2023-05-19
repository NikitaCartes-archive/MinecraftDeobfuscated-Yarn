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
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), VillagerFollowRangeFix::fix);
	}

	private static Dynamic<?> fix(Dynamic<?> dynamic) {
		return dynamic.update(
			"Attributes",
			dynamic2 -> dynamic.createList(
					dynamic2.asStream()
						.map(
							dynamicxx -> dynamicxx.get("Name").asString("").equals("generic.follow_range") && dynamicxx.get("Base").asDouble(0.0) == 16.0
									? dynamicxx.set("Base", dynamicxx.createDouble(48.0))
									: dynamicxx
						)
				)
		);
	}
}
