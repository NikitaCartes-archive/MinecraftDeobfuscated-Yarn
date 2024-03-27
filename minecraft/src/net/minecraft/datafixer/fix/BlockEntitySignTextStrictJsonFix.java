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

	private Dynamic<?> fix(Dynamic<?> dynamic, String lineName) {
		return dynamic.update(lineName, TextFixes::text);
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), dynamic -> {
			dynamic = this.fix(dynamic, "Text1");
			dynamic = this.fix(dynamic, "Text2");
			dynamic = this.fix(dynamic, "Text3");
			return this.fix(dynamic, "Text4");
		});
	}
}
