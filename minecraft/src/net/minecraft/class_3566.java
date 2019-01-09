package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class class_3566 extends class_1197 {
	public class_3566(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityBlockStateFix", class_1208.field_5727, "minecraft:piston");
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		Type<?> type = this.getOutputSchema().getChoiceType(class_1208.field_5727, "minecraft:piston");
		Type<?> type2 = type.findFieldType("blockState");
		OpticFinder<?> opticFinder = DSL.fieldFinder("blockState", type2);
		Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
		int i = dynamic.get("blockId").asInt(0);
		dynamic = dynamic.remove("blockId");
		int j = dynamic.get("blockData").asInt(0) & 15;
		dynamic = dynamic.remove("blockData");
		Dynamic<?> dynamic2 = class_3580.method_15594(i << 4 | j);
		Typed<?> typed2 = (Typed<?>)type.pointTyped(typed.getOps()).orElseThrow(() -> new IllegalStateException("Could not create new piston block entity."));
		return typed2.set(DSL.remainderFinder(), dynamic)
			.set(
				opticFinder, (Typed)type2.readTyped(dynamic2).getSecond().orElseThrow(() -> new IllegalStateException("Could not parse newly created block state tag."))
			);
	}
}
