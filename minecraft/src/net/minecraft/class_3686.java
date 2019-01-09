package net.minecraft;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3686 extends class_1220 {
	public class_3686(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_16050(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> class_1222.method_5196(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_16050(schema, map, "minecraft:cat");
		return map;
	}
}
