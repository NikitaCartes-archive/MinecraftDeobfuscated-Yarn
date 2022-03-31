package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema3083 extends IdentifierNormalizingSchema {
	public Schema3083(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_42645(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_42645(schema, map, "minecraft:allay");
		return map;
	}
}
