package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_1235 extends class_1220 {
	public class_1235(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			false,
			class_1208.field_5724,
			() -> DSL.optionalFields(
					"Children",
					DSL.list(
						DSL.optionalFields(
							"CA",
							class_1208.field_5720.in(schema),
							"CB",
							class_1208.field_5720.in(schema),
							"CC",
							class_1208.field_5720.in(schema),
							"CD",
							class_1208.field_5720.in(schema)
						)
					)
				)
		);
	}
}
