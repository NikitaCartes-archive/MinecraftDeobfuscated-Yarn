package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.List;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class class_5277 extends ChoiceFix {
	public class_5277(Schema schema) {
		super(schema, false, "EntityShulkerRotationFix", TypeReferences.ENTITY, "minecraft:shulker");
	}

	public Dynamic<?> method_27960(Dynamic<?> dynamic) {
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
		return inputType.update(DSL.remainderFinder(), this::method_27960);
	}
}
