package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_1228 extends Schema {
	public class_1228(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			false,
			class_1208.field_5715,
			() -> DSL.optionalFields(
					"RootVehicle",
					DSL.optionalFields("Entity", class_1208.field_5723.in(schema)),
					"Inventory",
					DSL.list(class_1208.field_5712.in(schema)),
					"EnderItems",
					DSL.list(class_1208.field_5712.in(schema))
				)
		);
		schema.registerType(
			true, class_1208.field_5723, () -> DSL.optionalFields("Passengers", DSL.list(class_1208.field_5723.in(schema)), class_1208.field_5729.in(schema))
		);
	}
}
