package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SequencedMap;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3818_3 extends IdentifierNormalizingSchema {
	public Schema3818_3(int i, Schema schema) {
		super(i, schema);
	}

	public static SequencedMap<String, Supplier<TypeTemplate>> method_63573(Schema schema) {
		SequencedMap<String, Supplier<TypeTemplate>> sequencedMap = new LinkedHashMap();
		sequencedMap.put("minecraft:bees", (Supplier)() -> DSL.list(DSL.optionalFields("entity_data", TypeReferences.ENTITY_TREE.in(schema))));
		sequencedMap.put("minecraft:block_entity_data", (Supplier)() -> TypeReferences.BLOCK_ENTITY.in(schema));
		sequencedMap.put("minecraft:bundle_contents", (Supplier)() -> DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		sequencedMap.put(
			"minecraft:can_break",
			(Supplier)() -> DSL.optionalFields(
					"predicates", DSL.list(DSL.optionalFields("blocks", DSL.or(TypeReferences.BLOCK_NAME.in(schema), DSL.list(TypeReferences.BLOCK_NAME.in(schema)))))
				)
		);
		sequencedMap.put(
			"minecraft:can_place_on",
			(Supplier)() -> DSL.optionalFields(
					"predicates", DSL.list(DSL.optionalFields("blocks", DSL.or(TypeReferences.BLOCK_NAME.in(schema), DSL.list(TypeReferences.BLOCK_NAME.in(schema)))))
				)
		);
		sequencedMap.put("minecraft:charged_projectiles", (Supplier)() -> DSL.list(TypeReferences.ITEM_STACK.in(schema)));
		sequencedMap.put("minecraft:container", (Supplier)() -> DSL.list(DSL.optionalFields("item", TypeReferences.ITEM_STACK.in(schema))));
		sequencedMap.put("minecraft:entity_data", (Supplier)() -> TypeReferences.ENTITY_TREE.in(schema));
		sequencedMap.put("minecraft:pot_decorations", (Supplier)() -> DSL.list(TypeReferences.ITEM_NAME.in(schema)));
		sequencedMap.put("minecraft:food", (Supplier)() -> DSL.optionalFields("using_converts_to", TypeReferences.ITEM_STACK.in(schema)));
		return sequencedMap;
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(true, TypeReferences.DATA_COMPONENTS, () -> DSL.optionalFieldsLazy(method_63573(schema)));
	}
}
