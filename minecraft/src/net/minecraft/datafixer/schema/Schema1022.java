package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema1022 extends Schema {
	public Schema1022(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
		schema.registerType(false, TypeReferences.RECIPE, () -> DSL.constType(IdentifierNormalizingSchema.getIdentifierType()));
		schema.registerType(
			false,
			TypeReferences.PLAYER,
			() -> DSL.optionalFields(
					Pair.of("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema))),
					Pair.of("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema))),
					Pair.of("EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema))),
					Pair.of("ShoulderEntityLeft", TypeReferences.ENTITY_TREE.in(schema)),
					Pair.of("ShoulderEntityRight", TypeReferences.ENTITY_TREE.in(schema)),
					Pair.of(
						"recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(schema)), "toBeDisplayed", DSL.list(TypeReferences.RECIPE.in(schema)))
					)
				)
		);
		schema.registerType(false, TypeReferences.HOTBAR, () -> DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(schema))));
	}
}
