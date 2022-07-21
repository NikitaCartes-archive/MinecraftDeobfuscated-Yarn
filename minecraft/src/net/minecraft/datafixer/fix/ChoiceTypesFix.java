package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.Locale;

public class ChoiceTypesFix extends DataFix {
	private final String name;
	private final TypeReference types;

	public ChoiceTypesFix(Schema outputSchema, String name, TypeReference types) {
		super(outputSchema, true);
		this.name = name;
		this.types = types;
	}

	@Override
	public TypeRewriteRule makeRule() {
		TaggedChoiceType<?> taggedChoiceType = this.getInputSchema().findChoiceType(this.types);
		TaggedChoiceType<?> taggedChoiceType2 = this.getOutputSchema().findChoiceType(this.types);
		return this.fixChoiceTypes(this.name, taggedChoiceType, taggedChoiceType2);
	}

	protected final <K> TypeRewriteRule fixChoiceTypes(String name, TaggedChoiceType<K> inputChoiceType, TaggedChoiceType<?> outputChoiceType) {
		if (inputChoiceType.getKeyType() != outputChoiceType.getKeyType()) {
			throw new IllegalStateException("Could not inject: key type is not the same");
		} else {
			return this.fixTypeEverywhere(name, inputChoiceType, (Type<Pair<K, ?>>)outputChoiceType, dynamicOps -> pair -> {
					if (!((TaggedChoiceType<Object>)outputChoiceType).hasType(pair.getFirst())) {
						throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown type %s in %s ", pair.getFirst(), this.types));
					} else {
						return pair;
					}
				});
		}
	}
}
