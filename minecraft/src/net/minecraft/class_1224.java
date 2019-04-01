package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_1224 extends Schema {
	public class_1224(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(false, class_1208.field_5711, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(
			false,
			class_1208.field_5715,
			() -> DSL.optionalFields(
					"RootVehicle",
					DSL.optionalFields("Entity", class_1208.field_5723.in(schema)),
					"Inventory",
					DSL.list(class_1208.field_5712.in(schema)),
					"EnderItems",
					DSL.list(class_1208.field_5712.in(schema)),
					DSL.optionalFields(
						"ShoulderEntityLeft",
						class_1208.field_5723.in(schema),
						"ShoulderEntityRight",
						class_1208.field_5723.in(schema),
						"recipeBook",
						DSL.optionalFields("recipes", DSL.list(class_1208.field_5711.in(schema)), "toBeDisplayed", DSL.list(class_1208.field_5711.in(schema)))
					)
				)
		);
		schema.registerType(false, class_1208.field_5722, () -> DSL.compoundList(DSL.list(class_1208.field_5712.in(schema))));
	}
}
