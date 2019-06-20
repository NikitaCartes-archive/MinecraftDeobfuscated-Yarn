package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class class_3573 extends class_1197 {
	public class_3573(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityJukeboxFix", class_1208.field_5727, "minecraft:jukebox");
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		Type<?> type = this.getInputSchema().getChoiceType(class_1208.field_5727, "minecraft:jukebox");
		Type<?> type2 = type.findFieldType("RecordItem");
		OpticFinder<?> opticFinder = DSL.fieldFinder("RecordItem", type2);
		Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
		int i = dynamic.get("Record").asInt(0);
		if (i > 0) {
			dynamic.remove("Record");
			String string = class_1188.method_5042(class_1181.method_5018(i), 0);
			if (string != null) {
				Dynamic<?> dynamic2 = dynamic.emptyMap();
				dynamic2 = dynamic2.set("id", dynamic2.createString(string));
				dynamic2 = dynamic2.set("Count", dynamic2.createByte((byte)1));
				return typed.set(
						opticFinder, (Typed)type2.readTyped(dynamic2).getSecond().orElseThrow(() -> new IllegalStateException("Could not create record item stack."))
					)
					.set(DSL.remainderFinder(), dynamic);
			}
		}

		return typed;
	}
}
