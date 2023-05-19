package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class RemoveFilteredSignTextFix extends ChoiceFix {
	public RemoveFilteredSignTextFix(Schema outputSchema) {
		super(outputSchema, false, "Remove filtered text from signs", TypeReferences.BLOCK_ENTITY, "minecraft:sign");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(
			DSL.remainderFinder(), blockEntity -> blockEntity.remove("FilteredText1").remove("FilteredText2").remove("FilteredText3").remove("FilteredText4")
		);
	}
}
