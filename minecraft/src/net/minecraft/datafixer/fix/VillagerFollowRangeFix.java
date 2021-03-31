package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class VillagerFollowRangeFix extends ChoiceFix {
	private static final double field_29912 = 16.0;
	private static final double field_29913 = 48.0;

	public VillagerFollowRangeFix(Schema schema) {
		super(schema, false, "Villager Follow Range Fix", TypeReferences.ENTITY, "minecraft:villager");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), VillagerFollowRangeFix::method_27914);
	}

	private static Dynamic<?> method_27914(Dynamic<?> dynamic) {
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
