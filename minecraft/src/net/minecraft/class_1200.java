package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class class_1200 extends DataFix {
	public class_1200(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, Dynamic<?>>> type = DSL.named(class_1208.field_5721.typeName(), DSL.remainderType());
		if (!Objects.equals(type, this.getInputSchema().getType(class_1208.field_5721))) {
			throw new IllegalStateException("Objective type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(
				"ObjectiveDisplayNameFix",
				type,
				dynamicOps -> pair -> pair.mapSecond(
							dynamic -> dynamic.update(
									"DisplayName",
									dynamic2 -> DataFixUtils.orElse(
											dynamic2.asString().map(string -> class_2561.class_2562.method_10867(new class_2585(string))).map(dynamic::createString), dynamic2
										)
								)
						)
			);
		}
	}
}
