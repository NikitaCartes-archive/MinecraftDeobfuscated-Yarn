package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;

public class BlockEntityShulkerBoxColorFix extends ChoiceFix {
	public BlockEntityShulkerBoxColorFix(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityShulkerBoxColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:shulker_box");
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> dynamic.remove("Color"));
	}
}
