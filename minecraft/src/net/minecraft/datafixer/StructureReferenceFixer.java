package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class StructureReferenceFixer extends DataFix {
	public StructureReferenceFixer(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
		return this.fixTypeEverywhereTyped("Structure Reference Fix", type, typed -> typed.update(DSL.remainderFinder(), StructureReferenceFixer::updateReferences));
	}

	private static <T> Dynamic<T> updateReferences(Dynamic<T> dynamic) {
		return dynamic.update(
			"references", dynamicx -> dynamicx.createInt((Integer)dynamicx.asNumber().map(Number::intValue).filter(integer -> integer > 0).orElse(1))
		);
	}
}
