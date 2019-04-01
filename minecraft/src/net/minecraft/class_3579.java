package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class class_3579 extends DataFix {
	private final String field_15828;

	public class_3579(Schema schema, String string) {
		super(schema, false);
		this.field_15828 = string;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5731);
		Type<Pair<String, String>> type2 = DSL.named(class_1208.field_5731.typeName(), DSL.namespacedString());
		if (!Objects.equals(type, type2)) {
			throw new IllegalStateException("block type is not what was expected.");
		} else {
			TypeRewriteRule typeRewriteRule = this.fixTypeEverywhere(this.field_15828 + " for block", type2, dynamicOps -> pair -> pair.mapSecond(this::method_15593));
			TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(
				this.field_15828 + " for block_state", this.getInputSchema().getType(class_1208.field_5720), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
						Optional<String> optional = dynamic.get("Name").asString();
						return optional.isPresent() ? dynamic.set("Name", dynamic.createString(this.method_15593((String)optional.get()))) : dynamic;
					})
			);
			return TypeRewriteRule.seq(typeRewriteRule, typeRewriteRule2);
		}
	}

	protected abstract String method_15593(String string);

	public static DataFix method_15589(Schema schema, String string, Function<String, String> function) {
		return new class_3579(schema, string) {
			@Override
			protected String method_15593(String string) {
				return (String)function.apply(string);
			}
		};
	}
}
