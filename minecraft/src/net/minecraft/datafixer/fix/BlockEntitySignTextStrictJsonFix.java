package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class BlockEntitySignTextStrictJsonFix extends ChoiceFix {
	public BlockEntitySignTextStrictJsonFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "BlockEntitySignTextStrictJsonFix", TypeReferences.BLOCK_ENTITY, "Sign");
	}

	private Dynamic<?> fix(Dynamic<?> signDynamic, String lineName) {
		return signDynamic.update(lineName, TextFixes::text);
	}

	@Override
	protected Typed<?> transform(Typed<?> inputTyped) {
		return inputTyped.update(DSL.remainderFinder(), linesDynamic -> {
			linesDynamic = this.fix(linesDynamic, "Text1");
			linesDynamic = this.fix(linesDynamic, "Text2");
			linesDynamic = this.fix(linesDynamic, "Text3");
			return this.fix(linesDynamic, "Text4");
		});
	}
}
