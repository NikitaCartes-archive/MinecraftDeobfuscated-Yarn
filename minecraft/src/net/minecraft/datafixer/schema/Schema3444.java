package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema3444 extends IdentifierNormalizingSchema {
	public Schema3444(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.register(map, "minecraft:moon_cow", (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
		schema.register(map, "minecraft:ray_tracing", (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
		schema.registerSimple(map, "minecraft:stencil_display");
		return map;
	}
}
