package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.datafixer.TypeReferences;

public class EntityShulkerRotationFix extends ChoiceFix {
	public EntityShulkerRotationFix(Schema outputSchema) {
		super(outputSchema, false, "EntityShulkerRotationFix", TypeReferences.ENTITY, "minecraft:shulker");
	}

	public Dynamic<?> fixRotation(Dynamic<?> dynamic) {
		List<Double> list = dynamic.get("Rotation").asList(dynamicx -> dynamicx.asDouble(180.0));
		if (!list.isEmpty()) {
			list.set(0, (Double)list.get(0) - 180.0);
			return dynamic.set("Rotation", dynamic.createList(list.stream().map(dynamic::createDouble)));
		} else {
			return dynamic;
		}
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), this::fixRotation);
	}
}
