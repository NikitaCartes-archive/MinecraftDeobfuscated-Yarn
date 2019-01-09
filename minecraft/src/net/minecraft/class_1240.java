package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class class_1240 extends class_1220 {
	public class_1240(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_5280(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> class_1222.method_5196(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_5280(schema, map, "minecraft:turtle");
		method_5280(schema, map, "minecraft:cod_mob");
		method_5280(schema, map, "minecraft:tropical_fish");
		method_5280(schema, map, "minecraft:salmon_mob");
		method_5280(schema, map, "minecraft:puffer_fish");
		method_5280(schema, map, "minecraft:phantom");
		method_5280(schema, map, "minecraft:dolphin");
		method_5280(schema, map, "minecraft:drowned");
		schema.register(map, "minecraft:trident", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("inBlockState", class_1208.field_5720.in(schema))));
		return map;
	}
}
