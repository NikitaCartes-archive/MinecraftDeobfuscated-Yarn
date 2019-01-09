package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_1169 extends class_1197 {
	public class_1169(Schema schema, boolean bl) {
		super(schema, bl, "EntityShulkerColorFix", class_1208.field_5729, "minecraft:shulker");
	}

	public Dynamic<?> method_4985(Dynamic<?> dynamic) {
		return !dynamic.get("Color").map(Dynamic::asNumber).isPresent() ? dynamic.set("Color", dynamic.createByte((byte)10)) : dynamic;
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_4985);
	}
}
