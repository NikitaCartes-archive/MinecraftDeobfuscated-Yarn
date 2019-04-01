package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class class_3581 extends DataFix {
	public class_3581(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"BlockStateStructureTemplateFix",
			this.getInputSchema().getType(class_1208.field_5720),
			typed -> typed.update(DSL.remainderFinder(), class_3580::method_15597)
		);
	}
}
