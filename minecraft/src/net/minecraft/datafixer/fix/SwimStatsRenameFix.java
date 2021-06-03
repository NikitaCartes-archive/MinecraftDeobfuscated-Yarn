package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class SwimStatsRenameFix extends DataFix {
	private final String field_33560;
	private final Map<String, String> field_33561;

	public SwimStatsRenameFix(Schema outputSchema, String string, Map<String, String> map) {
		super(outputSchema, false);
		this.field_33560 = string;
		this.field_33561 = map;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return TypeRewriteRule.seq(this.method_37383(), this.method_37378());
	}

	private TypeRewriteRule method_37378() {
		Type<?> type = this.getOutputSchema().getType(TypeReferences.OBJECTIVE);
		Type<?> type2 = this.getInputSchema().getType(TypeReferences.OBJECTIVE);
		OpticFinder<?> opticFinder = type2.findField("CriteriaType");
		TaggedChoiceType<?> taggedChoiceType = (TaggedChoiceType<?>)opticFinder.type()
			.findChoiceType("type", -1)
			.orElseThrow(() -> new IllegalStateException("Can't find choice type for criteria"));
		Type<?> type3 = (Type<?>)taggedChoiceType.types().get("minecraft:custom");
		if (type3 == null) {
			throw new IllegalStateException("Failed to find custom criterion type variant");
		} else {
			OpticFinder<?> opticFinder2 = DSL.namedChoice("minecraft:custom", type3);
			OpticFinder<String> opticFinder3 = DSL.fieldFinder("id", IdentifierNormalizingSchema.getIdentifierType());
			return this.fixTypeEverywhereTyped(
				this.field_33560,
				type2,
				type,
				typed -> typed.updateTyped(
						opticFinder,
						typedx -> typedx.updateTyped(opticFinder2, typedxx -> typedxx.update(opticFinder3, string -> (String)this.field_33561.getOrDefault(string, string)))
					)
			);
		}
	}

	private TypeRewriteRule method_37383() {
		Type<?> type = this.getOutputSchema().getType(TypeReferences.STATS);
		Type<?> type2 = this.getInputSchema().getType(TypeReferences.STATS);
		OpticFinder<?> opticFinder = type2.findField("stats");
		OpticFinder<?> opticFinder2 = opticFinder.type().findField("minecraft:custom");
		OpticFinder<String> opticFinder3 = IdentifierNormalizingSchema.getIdentifierType().finder();
		return this.fixTypeEverywhereTyped(
			this.field_33560,
			type2,
			type,
			typed -> typed.updateTyped(
					opticFinder,
					typedx -> typedx.updateTyped(opticFinder2, typedxx -> typedxx.update(opticFinder3, string -> (String)this.field_33561.getOrDefault(string, string)))
				)
		);
	}
}
