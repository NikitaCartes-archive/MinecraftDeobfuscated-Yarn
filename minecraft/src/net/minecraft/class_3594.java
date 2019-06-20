package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_3594 extends class_1197 {
	public class_3594(Schema schema, boolean bl) {
		super(schema, bl, "EntityArmorStandSilentFix", class_1208.field_5729, "ArmorStand");
	}

	public Dynamic<?> method_15679(Dynamic<?> dynamic) {
		return dynamic.get("Silent").asBoolean(false) && !dynamic.get("Marker").asBoolean(false) ? dynamic.remove("Silent") : dynamic;
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_15679);
	}
}
