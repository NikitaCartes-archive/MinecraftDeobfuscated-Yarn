package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class OptionsAccessibilityOnboardFix extends DataFix {
	public OptionsAccessibilityOnboardFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"OptionsAccessibilityOnboardFix",
			this.getInputSchema().getType(TypeReferences.OPTIONS),
			typed -> typed.update(DSL.remainderFinder(), options -> options.set("onboardAccessibility", options.createBoolean(false)))
		);
	}
}
