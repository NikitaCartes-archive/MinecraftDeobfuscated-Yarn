package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixers.TypeReferences;

public class Schema1800 extends SchemaIdentifierNormalize {
	public Schema1800(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_5285(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> Schema100.method_5196(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_5285(schema, map, "minecraft:panda");
		schema.register(
			map,
			"minecraft:pillager",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), Schema100.method_5196(schema)))
		);
		return map;
	}
}
