package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class BlockStateStructureTemplateFix extends DataFix {
	public BlockStateStructureTemplateFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"BlockStateStructureTemplateFix",
			this.getInputSchema().getType(TypeReferences.BLOCK_STATE),
			typed -> typed.update(DSL.remainderFinder(), BlockStateFlattening::lookupState)
		);
	}
}
