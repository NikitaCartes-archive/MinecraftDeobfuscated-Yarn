package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;

public class class_3601 extends class_1197 {
	public class_3601(Schema schema, boolean bl) {
		super(schema, bl, "EntityHorseSaddleFix", class_1208.field_5729, "EntityHorse");
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(class_1208.field_5713.typeName(), DSL.namespacedString()));
		Type<?> type = this.getInputSchema().getTypeRaw(class_1208.field_5712);
		OpticFinder<?> opticFinder2 = DSL.fieldFinder("SaddleItem", type);
		Optional<? extends Typed<?>> optional = typed.getOptionalTyped(opticFinder2);
		Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
		if (!optional.isPresent() && dynamic.get("Saddle").asBoolean(false)) {
			Typed<?> typed2 = (Typed<?>)type.pointTyped(typed.getOps()).orElseThrow(IllegalStateException::new);
			typed2 = typed2.set(opticFinder, Pair.of(class_1208.field_5713.typeName(), "minecraft:saddle"));
			Dynamic<?> dynamic2 = dynamic.emptyMap();
			dynamic2 = dynamic2.set("Count", dynamic2.createByte((byte)1));
			dynamic2 = dynamic2.set("Damage", dynamic2.createShort((short)0));
			typed2 = typed2.set(DSL.remainderFinder(), dynamic2);
			dynamic.remove("Saddle");
			return typed.set(opticFinder2, typed2).set(DSL.remainderFinder(), dynamic);
		} else {
			return typed;
		}
	}
}
