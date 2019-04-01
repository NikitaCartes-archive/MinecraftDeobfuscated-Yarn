package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;

public class class_1186 extends DataFix {
	public class_1186(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5712);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(class_1208.field_5713.typeName(), DSL.namespacedString()));
		OpticFinder<?> opticFinder2 = type.findField("tag");
		return this.fixTypeEverywhereTyped("ItemInstanceMapIdFix", type, typed -> {
			Optional<Pair<String, String>> optional = typed.getOptional(opticFinder);
			if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:filled_map")) {
				Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
				Typed<?> typed2 = typed.getOrCreateTyped(opticFinder2);
				Dynamic<?> dynamic2 = typed2.get(DSL.remainderFinder());
				dynamic2 = dynamic2.set("map", dynamic2.createInt(dynamic.get("Damage").asInt(0)));
				return typed.set(opticFinder2, typed2.set(DSL.remainderFinder(), dynamic2));
			} else {
				return typed;
			}
		});
	}
}
