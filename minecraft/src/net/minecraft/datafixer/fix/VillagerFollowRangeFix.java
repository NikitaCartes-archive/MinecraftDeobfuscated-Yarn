package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class VillagerFollowRangeFix extends ChoiceFix {
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
							dynamicxx -> ((String)dynamicxx.get("Name").asString().orElse("")).equals("generic.follow_range")
										&& ((Number)dynamicxx.get("Base").asNumber().orElse(0)).doubleValue() == 16.0
									? dynamicxx.set("Base", dynamicxx.createDouble(48.0))
									: dynamicxx
						)
				)
		);
	}
}
