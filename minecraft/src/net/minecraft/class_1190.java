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
import java.util.Optional;

public class class_1190 extends DataFix {
	public class_1190(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5712);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(class_1208.field_5713.typeName(), DSL.namespacedString()));
		OpticFinder<?> opticFinder2 = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemWaterPotionFix",
			type,
			typed -> {
				Optional<Pair<String, String>> optional = typed.getOptional(opticFinder);
				if (optional.isPresent()) {
					String string = (String)((Pair)optional.get()).getSecond();
					if ("minecraft:potion".equals(string)
						|| "minecraft:splash_potion".equals(string)
						|| "minecraft:lingering_potion".equals(string)
						|| "minecraft:tipped_arrow".equals(string)) {
						Typed<?> typed2 = typed.getOrCreateTyped(opticFinder2);
						Dynamic<?> dynamic = typed2.get(DSL.remainderFinder());
						if (!dynamic.get("Potion").asString().isPresent()) {
							dynamic = dynamic.set("Potion", dynamic.createString("minecraft:water"));
						}

						return typed.set(opticFinder2, typed2.set(DSL.remainderFinder(), dynamic));
					}
				}

				return typed;
			}
		);
	}
}
