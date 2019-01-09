package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_3574 extends class_1197 {
	public class_3574(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityKeepPacked", class_1208.field_5727, "DUMMY");
	}

	private static Dynamic<?> method_15579(Dynamic<?> dynamic) {
		return dynamic.set("keepPacked", dynamic.createBoolean(true));
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), class_3574::method_15579);
	}
}
