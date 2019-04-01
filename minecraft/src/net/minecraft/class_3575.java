package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_3575 extends class_1197 {
	public class_3575(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityShulkerBoxColorFix", class_1208.field_5727, "minecraft:shulker_box");
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> dynamic.remove("Color"));
	}
}
