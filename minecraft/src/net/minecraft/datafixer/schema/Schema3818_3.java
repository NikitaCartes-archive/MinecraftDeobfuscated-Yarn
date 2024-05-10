package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3818_3 extends IdentifierNormalizingSchema {
	public Schema3818_3(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			true,
			TypeReferences.DATA_COMPONENTS,
			() -> DSL.optionalFields(
					Pair.of("minecraft:bees", DSL.list(DSL.optionalFields("entity_data", TypeReferences.ENTITY_TREE.in(schema)))),
					Pair.of("minecraft:block_entity_data", TypeReferences.BLOCK_ENTITY.in(schema)),
					Pair.of("minecraft:bundle_contents", DSL.list(TypeReferences.ITEM_STACK.in(schema))),
					Pair.of(
						"minecraft:can_break",
						DSL.optionalFields(
							"predicates", DSL.list(DSL.optionalFields("blocks", DSL.or(TypeReferences.BLOCK_NAME.in(schema), DSL.list(TypeReferences.BLOCK_NAME.in(schema)))))
						)
					),
					Pair.of(
						"minecraft:can_place_on",
						DSL.optionalFields(
							"predicates", DSL.list(DSL.optionalFields("blocks", DSL.or(TypeReferences.BLOCK_NAME.in(schema), DSL.list(TypeReferences.BLOCK_NAME.in(schema)))))
						)
					),
					Pair.of("minecraft:charged_projectiles", DSL.list(TypeReferences.ITEM_STACK.in(schema))),
					Pair.of("minecraft:container", DSL.list(DSL.optionalFields("item", TypeReferences.ITEM_STACK.in(schema)))),
					Pair.of("minecraft:entity_data", TypeReferences.ENTITY_TREE.in(schema)),
					Pair.of("minecraft:pot_decorations", DSL.list(TypeReferences.ITEM_NAME.in(schema))),
					Pair.of("minecraft:food", DSL.optionalFields("using_converts_to", TypeReferences.ITEM_STACK.in(schema)))
				)
		);
	}
}
