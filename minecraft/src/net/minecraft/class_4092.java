package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class class_4092 extends DataFix {
	public class_4092(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"OptionsAddTextBackgroundFix",
			this.getInputSchema().getType(class_1208.field_5717),
			typed -> typed.update(
					DSL.remainderFinder(),
					dynamic -> DataFixUtils.orElse(
							dynamic.get("chatOpacity").asString().map(string -> dynamic.set("textBackgroundOpacity", dynamic.createDouble(this.method_18853(string)))), dynamic
						)
				)
		);
	}

	private double method_18853(String string) {
		try {
			double d = 0.9 * Double.parseDouble(string) + 0.1;
			return d / 2.0;
		} catch (NumberFormatException var4) {
			return 0.5;
		}
	}
}
