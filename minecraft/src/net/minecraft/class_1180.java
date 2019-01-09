package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.stream.Stream;

public class class_1180 extends DataFix {
	public class_1180(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5712);
		OpticFinder<?> opticFinder = type.findField("tag");
		return this.fixTypeEverywhereTyped(
			"Item Lore componentize",
			type,
			typed -> typed.updateTyped(
					opticFinder,
					typedx -> typedx.update(
							DSL.remainderFinder(),
							dynamic -> dynamic.update(
									"display",
									dynamicx -> dynamicx.update(
											"Lore", dynamicxx -> DataFixUtils.orElse(dynamicxx.asStreamOpt().map(class_1180::method_5005).map(dynamicxx::createList), dynamicxx)
										)
								)
						)
				)
		);
	}

	private static <T> Stream<Dynamic<T>> method_5005(Stream<Dynamic<T>> stream) {
		return stream.map(dynamic -> DataFixUtils.orElse(dynamic.asString().map(class_1180::method_5012).map(dynamic::createString), dynamic));
	}

	private static String method_5012(String string) {
		return class_2561.class_2562.method_10867(new class_2585(string));
	}
}
