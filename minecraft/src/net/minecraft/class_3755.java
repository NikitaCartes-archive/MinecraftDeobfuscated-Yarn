package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class class_3755 extends DataFix {
	public class_3755(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5726);
		Type<?> type2 = type.findFieldType("Level");
		OpticFinder<?> opticFinder = DSL.fieldFinder("Level", type2);
		return this.fixTypeEverywhereTyped(
			"ChunkLightRemoveFix",
			type,
			this.getOutputSchema().getType(class_1208.field_5726),
			typed -> typed.updateTyped(opticFinder, typedx -> typedx.update(DSL.remainderFinder(), dynamic -> dynamic.remove("isLightOn")))
		);
	}
}
