package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_1223 extends Schema {
	public class_1223(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			true,
			class_1208.field_5718,
			() -> DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", class_1208.field_5723.in(schema))), "SpawnData", class_1208.field_5723.in(schema))
		);
	}
}
