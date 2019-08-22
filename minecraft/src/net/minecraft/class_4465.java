package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.schemas.Schema100;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;

public class class_4465 extends SchemaIdentifierNormalize {
	public class_4465(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_21746(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> Schema100.method_5196(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_21746(schema, map, "minecraft:bee");
		method_21746(schema, map, "minecraft:bee_stinger");
		return map;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(
			map,
			"minecraft:beehive",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Bees", DSL.list(DSL.optionalFields("EntityData", TypeReferences.ENTITY_TREE.in(schema)))
				))
		);
		return map;
	}
}
