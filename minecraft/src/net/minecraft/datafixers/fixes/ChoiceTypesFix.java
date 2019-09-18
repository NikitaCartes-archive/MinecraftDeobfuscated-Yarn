package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;

public class ChoiceTypesFix extends DataFix {
	private final String name;
	private final TypeReference types;

	public ChoiceTypesFix(Schema schema, String string, TypeReference typeReference) {
		super(schema, true);
		this.name = string;
		this.types = typeReference;
	}

	@Override
	public TypeRewriteRule makeRule() {
		TaggedChoiceType<?> taggedChoiceType = this.getInputSchema().findChoiceType(this.types);
		TaggedChoiceType<?> taggedChoiceType2 = this.getOutputSchema().findChoiceType(this.types);
		return this.method_15476(this.name, taggedChoiceType, taggedChoiceType2);
	}

	protected final <K> TypeRewriteRule method_15476(String string, TaggedChoiceType<K> taggedChoiceType, TaggedChoiceType<?> taggedChoiceType2) {
		if (taggedChoiceType.getKeyType() != taggedChoiceType2.getKeyType()) {
			throw new IllegalStateException("Could not inject: key type is not the same");
		} else {
			return this.fixTypeEverywhere(string, taggedChoiceType, (Type<Pair<K, ?>>)taggedChoiceType2, dynamicOps -> pair -> {
					if (!((TaggedChoiceType<Object>)taggedChoiceType2).hasType(pair.getFirst())) {
						throw new IllegalArgumentException(String.format("Unknown type %s in %s ", pair.getFirst(), this.types));
					} else {
						return pair;
					}
				});
		}
	}
}
