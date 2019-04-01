package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public abstract class class_1211 extends DataFix {
	private final String field_5734;

	public class_1211(String string, Schema schema, boolean bl) {
		super(schema, bl);
		this.field_5734 = string;
	}

	@Override
	public TypeRewriteRule makeRule() {
		TaggedChoiceType<String> taggedChoiceType = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(class_1208.field_5729);
		TaggedChoiceType<String> taggedChoiceType2 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(class_1208.field_5729);
		Type<Pair<String, String>> type = DSL.named(class_1208.field_5719.typeName(), DSL.namespacedString());
		if (!Objects.equals(this.getOutputSchema().getType(class_1208.field_5719), type)) {
			throw new IllegalStateException("Entity name type is not what was expected.");
		} else {
			return TypeRewriteRule.seq(this.fixTypeEverywhere(this.field_5734, taggedChoiceType, taggedChoiceType2, dynamicOps -> pair -> pair.mapFirst(string -> {
						String string2 = this.method_5163(string);
						Type<?> typex = (Type<?>)taggedChoiceType.types().get(string);
						Type<?> type2 = (Type<?>)taggedChoiceType2.types().get(string2);
						if (!type2.equals(typex, true, true)) {
							throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", type2, typex));
						} else {
							return string2;
						}
					})), this.fixTypeEverywhere(this.field_5734 + " for entity name", type, dynamicOps -> pair -> pair.mapSecond(this::method_5163)));
		}
	}

	protected abstract String method_5163(String string);
}
