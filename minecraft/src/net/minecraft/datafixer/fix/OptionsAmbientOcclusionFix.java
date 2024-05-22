package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class OptionsAmbientOcclusionFix extends DataFix {
	public OptionsAmbientOcclusionFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"OptionsAmbientOcclusionFix",
			this.getInputSchema().getType(TypeReferences.OPTIONS),
			optionsTyped -> optionsTyped.update(
					DSL.remainderFinder(),
					optionsdynamic -> DataFixUtils.orElse(
							optionsdynamic.get("ao").asString().map(setting -> optionsdynamic.set("ao", optionsdynamic.createString(fixValue(setting)))).result(), optionsdynamic
						)
				)
		);
	}

	private static String fixValue(String oldValue) {
		return switch (oldValue) {
			case "0" -> "false";
			case "1", "2" -> "true";
			default -> oldValue;
		};
	}
}
