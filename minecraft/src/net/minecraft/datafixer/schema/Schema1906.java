package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema1906 extends SchemaIdentifierNormalize {
	public Schema1906(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		method_16052(schema, map, "minecraft:barrel");
		method_16052(schema, map, "minecraft:smoker");
		method_16052(schema, map, "minecraft:blast_furnace");
		schema.register(map, "minecraft:lectern", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Book", TypeReferences.ITEM_STACK.in(schema))));
		schema.registerSimple(map, "minecraft:bell");
		return map;
	}

	protected static void method_16052(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)))));
	}
}
