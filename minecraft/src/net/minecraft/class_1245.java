package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class class_1245 extends class_1220 {
	public class_1245(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_5285(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> class_1222.method_5196(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_5285(schema, map, "minecraft:panda");
		schema.register(
			map,
			"minecraft:pillager",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("Inventory", DSL.list(class_1208.field_5712.in(schema)), class_1222.method_5196(schema)))
		);
		return map;
	}
}
