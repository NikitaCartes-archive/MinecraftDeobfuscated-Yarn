package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class BlockEntityShulkerBoxColorFix extends ChoiceFix {
	public BlockEntityShulkerBoxColorFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType, "BlockEntityShulkerBoxColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:shulker_box");
	}

	@Override
	protected Typed<?> transform(Typed<?> inputType) {
		return inputType.update(DSL.remainderFinder(), dynamic -> dynamic.remove("Color"));
	}
}
