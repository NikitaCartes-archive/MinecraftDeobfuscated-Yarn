package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema135 extends Schema {
	public Schema135(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
		schema.registerType(
			false,
			TypeReferences.PLAYER,
			() -> DSL.optionalFields(
					"RootVehicle",
					DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema)),
					"Inventory",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"EnderItems",
					DSL.list(TypeReferences.ITEM_STACK.in(schema))
				)
		);
		schema.registerType(
			true, TypeReferences.ENTITY_TREE, () -> DSL.optionalFields("Passengers", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), TypeReferences.ENTITY.in(schema))
		);
	}
}
