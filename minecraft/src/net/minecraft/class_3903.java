package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_3903 extends class_1197 {
	public class_3903(Schema schema, boolean bl) {
		super(schema, bl, "CatTypeFix", class_1208.field_5729, "minecraft:cat");
	}

	public Dynamic<?> method_17325(Dynamic<?> dynamic) {
		return dynamic.get("CatType").asInt(0) == 9 ? dynamic.set("CatType", dynamic.createInt(10)) : dynamic;
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_17325);
	}
}
