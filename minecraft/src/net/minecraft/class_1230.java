package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_1230 extends class_1220 {
	public class_1230(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(map, "minecraft:trapped_chest", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("Items", DSL.list(class_1208.field_5712.in(schema)))));
		return map;
	}
}
