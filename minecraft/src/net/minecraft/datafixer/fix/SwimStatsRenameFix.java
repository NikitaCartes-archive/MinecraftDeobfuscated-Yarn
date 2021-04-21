package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Map.Entry;
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
		Type<?> type = this.getOutputSchema().getType(TypeReferences.STATS);
		Type<?> type2 = this.getInputSchema().getType(TypeReferences.STATS);
		OpticFinder<?> opticFinder = type2.findField("stats");
		OpticFinder<?> opticFinder2 = opticFinder.type().findField("minecraft:custom");
		OpticFinder<String> opticFinder3 = IdentifierNormalizingSchema.getIdentifierType().finder();
		return this.fixTypeEverywhereTyped(
			this.field_33560,
			type2,
			type,
			typed -> typed.updateTyped(opticFinder, typedx -> typedx.updateTyped(opticFinder2, typedxx -> typedxx.update(opticFinder3, string -> {
							for (Entry<String, String> entry : this.field_33561.entrySet()) {
								if (string.equals(entry.getKey())) {
									return (String)entry.getValue();
								}
							}

							return string;
						})))
		);
	}
}
