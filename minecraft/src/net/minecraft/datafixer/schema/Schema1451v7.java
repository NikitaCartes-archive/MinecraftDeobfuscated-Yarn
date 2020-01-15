package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema1451v7 extends SchemaIdentifierNormalize {
	public Schema1451v7(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			false,
			TypeReferences.STRUCTURE_FEATURE,
			() -> DSL.optionalFields(
					"Children",
					DSL.list(
						DSL.optionalFields(
							"CA",
							TypeReferences.BLOCK_STATE.in(schema),
							"CB",
							TypeReferences.BLOCK_STATE.in(schema),
							"CC",
							TypeReferences.BLOCK_STATE.in(schema),
							"CD",
							TypeReferences.BLOCK_STATE.in(schema)
						)
					)
				)
		);
	}
}
