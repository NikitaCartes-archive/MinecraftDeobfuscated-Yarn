package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_3592 extends class_1197 {
	public class_3592(Schema schema, boolean bl) {
		super(schema, bl, "Colorless shulker entity fix", class_1208.field_5729, "minecraft:shulker");
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), dynamic -> dynamic.get("Color").asInt(0) == 10 ? dynamic.set("Color", dynamic.createByte((byte)16)) : dynamic);
	}
}
