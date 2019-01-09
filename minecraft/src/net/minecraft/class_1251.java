package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_1251 extends Schema {
	public class_1251(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		map.remove("EntityHorse");
		schema.register(
			map,
			"Horse",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"ArmorItem", class_1208.field_5712.in(schema), "SaddleItem", class_1208.field_5712.in(schema), class_1222.method_5196(schema)
				))
		);
		schema.register(
			map,
			"Donkey",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"Items", DSL.list(class_1208.field_5712.in(schema)), "SaddleItem", class_1208.field_5712.in(schema), class_1222.method_5196(schema)
				))
		);
		schema.register(
			map,
			"Mule",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"Items", DSL.list(class_1208.field_5712.in(schema)), "SaddleItem", class_1208.field_5712.in(schema), class_1222.method_5196(schema)
				))
		);
		schema.register(
			map, "ZombieHorse", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("SaddleItem", class_1208.field_5712.in(schema), class_1222.method_5196(schema)))
		);
		schema.register(
			map, "SkeletonHorse", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("SaddleItem", class_1208.field_5712.in(schema), class_1222.method_5196(schema)))
		);
		return map;
	}
}
