package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class FireResistantToDamageResistantComponentFix extends ComponentFix {
	public FireResistantToDamageResistantComponentFix(Schema outputSchema) {
		super(outputSchema, "FireResistantToDamageResistantComponentFix", "minecraft:fire_resistant", "minecraft:damage_resistant");
	}

	@Override
	protected <T> Dynamic<T> fixComponent(Dynamic<T> dynamic) {
		return dynamic.emptyMap().set("types", dynamic.createString("#minecraft:is_fire"));
	}
}
