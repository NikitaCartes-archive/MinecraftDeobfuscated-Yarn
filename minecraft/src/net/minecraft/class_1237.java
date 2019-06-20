package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_1237 extends class_1220 {
	public class_1237(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			false,
			class_1208.field_5726,
			() -> DSL.fields(
					"Level",
					DSL.optionalFields(
						"Entities",
						DSL.list(class_1208.field_5723.in(schema)),
						"TileEntities",
						DSL.list(class_1208.field_5727.in(schema)),
						"TileTicks",
						DSL.list(DSL.fields("i", class_1208.field_5731.in(schema))),
						"Sections",
						DSL.list(DSL.optionalFields("Palette", DSL.list(class_1208.field_5720.in(schema)))),
						"Structures",
						DSL.optionalFields("Starts", DSL.compoundList(class_1208.field_5724.in(schema)))
					)
				)
		);
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
					),
					"biome",
					class_1208.field_5728.in(schema)
				)
		);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		map.put("DUMMY", DSL::remainder);
		return map;
	}
}
