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

public class class_4294 extends DataFix {
	public class_4294(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private Dynamic<?> method_20445(Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = dynamic.get("display").get();
		if (optional.isPresent()) {
			Dynamic<?> dynamic2 = (Dynamic<?>)optional.get();
			Optional<String> optional2 = dynamic2.get("Name").asString();
			if (optional2.isPresent()) {
				String string = (String)optional2.get();
				string = string.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
				dynamic2 = dynamic2.set("Name", dynamic2.createString(string));
			}

			return dynamic.set("display", dynamic2);
		} else {
			return dynamic;
		}
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5712);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder("id", DSL.named(class_1208.field_5713.typeName(), DSL.namespacedString()));
		OpticFinder<?> opticFinder2 = type.findField("tag");
		return this.fixTypeEverywhereTyped("OminousBannerRenameFix", type, typed -> {
			Optional<Pair<String, String>> optional = typed.getOptional(opticFinder);
			if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:white_banner")) {
				Optional<? extends Typed<?>> optional2 = typed.getOptionalTyped(opticFinder2);
				if (optional2.isPresent()) {
					Typed<?> typed2 = (Typed<?>)optional2.get();
					Dynamic<?> dynamic = typed2.get(DSL.remainderFinder());
					return typed.set(opticFinder2, typed2.set(DSL.remainderFinder(), this.method_20445(dynamic)));
				}
			}

			return typed;
		});
	}
}
