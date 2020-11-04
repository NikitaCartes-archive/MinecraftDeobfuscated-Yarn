package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema2509 extends IdentifierNormalizingSchema {
	public Schema2509(int i, Schema schema) {
		super(i, schema);
	}

	protected static void renameZombifiedPiglin(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		map.remove("minecraft:zombie_pigman");
		renameZombifiedPiglin(schema, map, "minecraft:zombified_piglin");
		return map;
	}
}
