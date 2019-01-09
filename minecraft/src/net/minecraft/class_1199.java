package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;

public class class_1199 extends DataFix {
	public class_1199(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private static class_274.class_275 method_5112(String string) {
		return string.equals("health") ? class_274.class_275.field_1471 : class_274.class_275.field_1472;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<Pair<String, Dynamic<?>>> type = DSL.named(class_1208.field_5721.typeName(), DSL.remainderType());
		if (!Objects.equals(type, this.getInputSchema().getType(class_1208.field_5721))) {
			throw new IllegalStateException("Objective type is not what was expected.");
		} else {
			return this.fixTypeEverywhere("ObjectiveRenderTypeFix", type, dynamicOps -> pair -> pair.mapSecond(dynamic -> {
						Optional<String> optional = dynamic.get("RenderType").asString();
						if (!optional.isPresent()) {
							String string = dynamic.get("CriteriaName").asString("");
							class_274.class_275 lv = method_5112(string);
							return dynamic.set("RenderType", dynamic.createString(lv.method_1228()));
						} else {
							return dynamic;
						}
					}));
		}
	}
}
