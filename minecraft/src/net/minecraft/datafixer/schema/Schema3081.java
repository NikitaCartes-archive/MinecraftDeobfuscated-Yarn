package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema3081 extends IdentifierNormalizingSchema {
	public Schema3081(int i, Schema schema) {
		super(i, schema);
	}

	protected static void register(Schema schema, Map<String, Supplier<TypeTemplate>> map, String id) {
		schema.register(map, id, (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		register(schema, map, "minecraft:warden");
		return map;
	}
}
