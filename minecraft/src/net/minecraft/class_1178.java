package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;

public class class_1178 extends DataFix {
	public class_1178(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private Dynamic<?> method_5001(Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = dynamic.get("display").get();
		if (optional.isPresent()) {
			Dynamic<?> dynamic2 = (Dynamic<?>)optional.get();
			Optional<String> optional2 = dynamic2.get("Name").asString();
			if (optional2.isPresent()) {
				dynamic2 = dynamic2.set("Name", dynamic2.createString(class_2561.class_2562.method_10867(new class_2585((String)optional2.get()))));
			} else {
				Optional<String> optional3 = dynamic2.get("LocName").asString();
				if (optional3.isPresent()) {
					dynamic2 = dynamic2.set("Name", dynamic2.createString(class_2561.class_2562.method_10867(new class_2588((String)optional3.get()))));
					dynamic2 = dynamic2.remove("LocName");
				}
			}

			return dynamic.set("display", dynamic2);
		} else {
			return dynamic;
		}
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5712);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"ItemCustomNameToComponentFix", type, typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), this::method_5001))
		);
	}
}
