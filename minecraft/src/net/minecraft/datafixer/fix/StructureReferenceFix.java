package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class StructureReferenceFix extends DataFix {
	public StructureReferenceFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
		return this.fixTypeEverywhereTyped(
			"Structure Reference Fix", type, structureFeatureTyped -> structureFeatureTyped.update(DSL.remainderFinder(), StructureReferenceFix::updateReferences)
		);
	}

	private static <T> Dynamic<T> updateReferences(Dynamic<T> structureFeatureDynamic) {
		return structureFeatureDynamic.update(
			"references",
			referencesDynamic -> referencesDynamic.createInt(
					(Integer)referencesDynamic.asNumber().map(Number::intValue).result().filter(references -> references > 0).orElse(1)
				)
		);
	}
}
