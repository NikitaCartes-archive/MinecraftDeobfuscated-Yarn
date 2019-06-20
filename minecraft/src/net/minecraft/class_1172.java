package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_1172 extends class_1197 {
	public class_1172(Schema schema, boolean bl) {
		super(schema, bl, "EntityWolfColorFix", class_1208.field_5729, "minecraft:wolf");
	}

	public Dynamic<?> method_4988(Dynamic<?> dynamic) {
		return dynamic.update("CollarColor", dynamicx -> dynamicx.createByte((byte)(15 - dynamicx.asInt(0))));
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_4988);
	}
}
