package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class OptionsMenuBlurrinessFix extends DataFix {
	public OptionsMenuBlurrinessFix(Schema schema) {
		super(schema, false);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"OptionsMenuBlurrinessFix",
			this.getInputSchema().getType(TypeReferences.OPTIONS),
			typed -> typed.update(
					DSL.remainderFinder(), dynamic -> dynamic.update("menuBackgroundBlurriness", dynamicx -> dynamicx.createInt(this.update(dynamicx.asString("0.5"))))
				)
		);
	}

	private int update(String value) {
		try {
			return Math.round(Float.parseFloat(value) * 10.0F);
		} catch (NumberFormatException var3) {
			return 5;
		}
	}
}
