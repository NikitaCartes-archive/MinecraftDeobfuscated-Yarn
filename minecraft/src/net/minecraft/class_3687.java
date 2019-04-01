package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class class_3687 extends class_1220 {
	public class_3687(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		method_16052(schema, map, "minecraft:barrel");
		method_16052(schema, map, "minecraft:smoker");
		method_16052(schema, map, "minecraft:blast_furnace");
		schema.register(map, "minecraft:lectern", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Book", class_1208.field_5712.in(schema))));
		schema.registerSimple(map, "minecraft:bell");
		return map;
	}

	protected static void method_16052(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("Items", DSL.list(class_1208.field_5712.in(schema)))));
	}
}
