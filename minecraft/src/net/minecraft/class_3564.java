package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class class_3564 extends class_1197 {
	public class_3564(Schema schema, boolean bl) {
		super(schema, bl, "BlockEntityBannerColorFix", class_1208.field_5727, "minecraft:banner");
	}

	public Dynamic<?> method_15546(Dynamic<?> dynamic) {
		dynamic = dynamic.update("Base", dynamicx -> dynamicx.createInt(15 - dynamicx.asInt(0)));
		return dynamic.update(
			"Patterns",
			dynamicx -> DataFixUtils.orElse(
					dynamicx.asStreamOpt()
						.map(stream -> stream.map(dynamicxx -> dynamicxx.update("Color", dynamicxxx -> dynamicxxx.createInt(15 - dynamicxxx.asInt(0)))))
						.map(dynamicx::createList),
					dynamicx
				)
		);
	}

	@Override
	protected Typed<?> method_5105(Typed<?> typed) {
		return typed.update(DSL.remainderFinder(), this::method_15546);
	}
}
