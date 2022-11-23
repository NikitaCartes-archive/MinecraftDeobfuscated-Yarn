package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema1920 extends IdentifierNormalizingSchema {
	public Schema1920(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_17343(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
		schema.register(map, name, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)))));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		method_17343(schema, map, "minecraft:campfire");
		return map;
	}
}
