package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema4070 extends IdentifierNormalizingSchema {
	public Schema4070(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.registerSimple(map, "minecraft:pale_oak_boat");
		schema.register(
			map,
			"minecraft:pale_oak_chest_boat",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))))
		);
		return map;
	}
}
