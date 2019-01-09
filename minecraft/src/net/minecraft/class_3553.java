package net.minecraft;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;

public class class_3553 extends DataFix {
	private final String field_15779;
	private final TypeReference field_15780;

	public class_3553(Schema schema, String string, TypeReference typeReference) {
		super(schema, true);
		this.field_15779 = string;
		this.field_15780 = typeReference;
	}

	@Override
	public TypeRewriteRule makeRule() {
		TaggedChoiceType<?> taggedChoiceType = this.getInputSchema().findChoiceType(this.field_15780);
		TaggedChoiceType<?> taggedChoiceType2 = this.getOutputSchema().findChoiceType(this.field_15780);
		return this.method_15476(this.field_15779, taggedChoiceType, taggedChoiceType2);
	}

	protected final <K> TypeRewriteRule method_15476(String string, TaggedChoiceType<K> taggedChoiceType, TaggedChoiceType<?> taggedChoiceType2) {
		if (taggedChoiceType.getKeyType() != taggedChoiceType2.getKeyType()) {
			throw new IllegalStateException("Could not inject: key type is not the same");
		} else {
			return this.fixTypeEverywhere(string, taggedChoiceType, (Type<Pair<K, ?>>)taggedChoiceType2, dynamicOps -> pair -> {
					if (!((TaggedChoiceType<Object>)taggedChoiceType2).hasType(pair.getFirst())) {
						throw new IllegalArgumentException(String.format("Unknown type %s in %s ", pair.getFirst(), this.field_15780));
					} else {
						return pair;
					}
				});
		}
	}
}
