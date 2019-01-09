package net.minecraft;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;

public abstract class class_1167 extends DataFix {
	protected final String field_5670;

	public class_1167(String string, Schema schema, boolean bl) {
		super(schema, bl);
		this.field_5670 = string;
	}

	@Override
	public TypeRewriteRule makeRule() {
		TaggedChoiceType<String> taggedChoiceType = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(class_1208.field_5729);
		TaggedChoiceType<String> taggedChoiceType2 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(class_1208.field_5729);
		return this.fixTypeEverywhere(this.field_5670, taggedChoiceType, taggedChoiceType2, dynamicOps -> pair -> {
				String string = (String)pair.getFirst();
				Type<?> type = (Type<?>)taggedChoiceType.types().get(string);
				Pair<String, Typed<?>> pair2 = this.method_4982(string, this.method_4983(pair.getSecond(), dynamicOps, type));
				Type<?> type2 = (Type<?>)taggedChoiceType2.types().get(pair2.getFirst());
				if (!type2.equals(pair2.getSecond().getType(), true, true)) {
					throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", type2, pair2.getSecond().getType()));
				} else {
					return Pair.of(pair2.getFirst(), pair2.getSecond().getValue());
				}
			});
	}

	private <A> Typed<A> method_4983(Object object, DynamicOps<?> dynamicOps, Type<A> type) {
		return new Typed<>(type, dynamicOps, (A)object);
	}

	protected abstract Pair<String, Typed<?>> method_4982(String string, Typed<?> typed);
}
