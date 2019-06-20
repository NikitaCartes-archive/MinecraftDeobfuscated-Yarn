package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class class_3985 extends class_1220 {
	public class_3985(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.register(
			map,
			"minecraft:wandering_trader",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Inventory",
					DSL.list(class_1208.field_5712.in(schema)),
					"Offers",
					DSL.optionalFields(
						"Recipes",
						DSL.list(DSL.optionalFields("buy", class_1208.field_5712.in(schema), "buyB", class_1208.field_5712.in(schema), "sell", class_1208.field_5712.in(schema)))
					),
					class_1222.method_5196(schema)
				))
		);
		schema.register(
			map,
			"minecraft:trader_llama",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Items",
					DSL.list(class_1208.field_5712.in(schema)),
					"SaddleItem",
					class_1208.field_5712.in(schema),
					"DecorItem",
					class_1208.field_5712.in(schema),
					class_1222.method_5196(schema)
				))
		);
		return map;
	}
}
