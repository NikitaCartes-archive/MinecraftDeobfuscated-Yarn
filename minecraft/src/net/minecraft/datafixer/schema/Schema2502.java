package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema2502 extends IdentifierNormalizingSchema {
	public Schema2502(int i, Schema schema) {
		super(i, schema);
	}

	protected static void targetEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
		schema.register(map, name, (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		targetEntity(schema, map, "minecraft:hoglin");
		return map;
	}
}
