package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema4071 extends IdentifierNormalizingSchema {
	public Schema4071(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.register(map, "minecraft:creaking", (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
		schema.register(map, "minecraft:creaking_transient", (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
		return map;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(map, "minecraft:creaking_heart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields()));
		return map;
	}
}
