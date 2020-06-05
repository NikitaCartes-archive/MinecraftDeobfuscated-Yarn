package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class SwimStatsRenameFix extends DataFix {
	public SwimStatsRenameFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getOutputSchema().getType(TypeReferences.STATS);
		Type<?> type2 = this.getInputSchema().getType(TypeReferences.STATS);
		OpticFinder<?> opticFinder = type2.findField("stats");
		OpticFinder<?> opticFinder2 = opticFinder.type().findField("minecraft:custom");
		OpticFinder<String> opticFinder3 = IdentifierNormalizingSchema.getIdentifierType().finder();
		return this.fixTypeEverywhereTyped(
			"SwimStatsRenameFix",
			type2,
			type,
			typed -> typed.updateTyped(opticFinder, typedx -> typedx.updateTyped(opticFinder2, typedxx -> typedxx.update(opticFinder3, string -> {
							if (string.equals("minecraft:swim_one_cm")) {
								return "minecraft:walk_on_water_one_cm";
							} else {
								return string.equals("minecraft:dive_one_cm") ? "minecraft:walk_under_water_one_cm" : string;
							}
						})))
		);
	}
}
