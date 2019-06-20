package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public class class_3578 extends DataFix {
	public class_3578(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5731);
		Type<?> type2 = this.getOutputSchema().getType(class_1208.field_5731);
		Type<Pair<String, Either<Integer, String>>> type3 = DSL.named(class_1208.field_5731.typeName(), DSL.or(DSL.intType(), DSL.namespacedString()));
		Type<Pair<String, String>> type4 = DSL.named(class_1208.field_5731.typeName(), DSL.namespacedString());
		if (Objects.equals(type, type3) && Objects.equals(type2, type4)) {
			return this.fixTypeEverywhere(
				"BlockNameFlatteningFix",
				type3,
				type4,
				dynamicOps -> pair -> pair.mapSecond(either -> either.map(class_3580::method_15599, string -> class_3580.method_15600(class_1220.method_5193(string))))
			);
		} else {
			throw new IllegalStateException("Expected and actual types don't match.");
		}
	}
}
