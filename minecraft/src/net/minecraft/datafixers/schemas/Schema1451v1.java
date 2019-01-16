package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixers.TypeReferences;

public class Schema1451v1 extends SchemaIdentifierNormalize {
	public Schema1451v1(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			false,
			TypeReferences.CHUNK,
			() -> DSL.fields(
					"Level",
					DSL.optionalFields(
						"Entities",
						DSL.list(TypeReferences.ENTITY_TREE.in(schema)),
						"TileEntities",
						DSL.list(TypeReferences.BLOCK_ENTITY.in(schema)),
						"TileTicks",
						DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema))),
						"Sections",
						DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema))))
					)
				)
		);
	}
}
