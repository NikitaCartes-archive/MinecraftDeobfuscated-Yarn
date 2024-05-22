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

	public Dynamic<?> fixRotation(Dynamic<?> shulkerDynamic) {
		List<Double> list = shulkerDynamic.get("Rotation").asList(rotationDynamic -> rotationDynamic.asDouble(180.0));
		if (!list.isEmpty()) {
			list.set(0, (Double)list.get(0) - 180.0);
			return shulkerDynamic.set("Rotation", shulkerDynamic.createList(list.stream().map(shulkerDynamic::createDouble)));
		} else {
			return shulkerDynamic;
		}
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), this::fixRotation);
	}
}
