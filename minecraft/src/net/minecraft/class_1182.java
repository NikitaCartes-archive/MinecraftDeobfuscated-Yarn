package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;

public abstract class class_1182 extends DataFix {
	private final String field_5676;

	public class_1182(Schema schema, String string) {
		super(schema, false);
		this.field_5676 = string;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<Pair<String, String>> type = DSL.named(class_1208.field_5713.typeName(), DSL.namespacedString());
		if (!Objects.equals(this.getInputSchema().getType(class_1208.field_5713), type)) {
			throw new IllegalStateException("item name type is not what was expected.");
		} else {
			return this.fixTypeEverywhere(this.field_5676, type, dynamicOps -> pair -> pair.mapSecond(this::method_5022));
		}
	}

	protected abstract String method_5022(String string);

	public static DataFix method_5019(Schema schema, String string, Function<String, String> function) {
		return new class_1182(schema, string) {
			@Override
			protected String method_5022(String string) {
				return (String)function.apply(string);
			}
		};
	}
}
