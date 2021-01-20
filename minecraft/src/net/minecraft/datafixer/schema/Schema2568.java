package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema2568 extends IdentifierNormalizingSchema {
	public Schema2568(int i, Schema schema) {
		super(i, schema);
	}

	protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, String name) {
		schema.register(entityTypes, name, (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		registerEntity(schema, map, "minecraft:piglin_brute");
		return map;
	}
}
