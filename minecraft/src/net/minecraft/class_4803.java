package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class class_4803 extends ChoiceFix {
	public class_4803(Schema schema, String string) {
		super(schema, false, "Memory expiry data fix (" + string + ")", TypeReferences.ENTITY, string);
	}

	@Override
	protected Typed<?> transform(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_24506);
	}

	public Dynamic<?> method_24506(Dynamic<?> dynamic) {
		return dynamic.update("Brain", this::method_24508);
	}

	private Dynamic<?> method_24508(Dynamic<?> dynamic) {
		return dynamic.update("memories", this::method_24509);
	}

	private Dynamic<?> method_24509(Dynamic<?> dynamic) {
		return dynamic.updateMapValues(this::method_24507);
	}

	private Pair<Dynamic<?>, Dynamic<?>> method_24507(Pair<Dynamic<?>, Dynamic<?>> pair) {
		return pair.mapSecond(this::method_24510);
	}

	private Dynamic<?> method_24510(Dynamic<?> dynamic) {
		return dynamic.createMap(ImmutableMap.of(dynamic.createString("value"), dynamic));
	}
}
