package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3327 extends IdentifierNormalizingSchema {
	public Schema3327(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(map, "minecraft:decorated_pot", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("shards", DSL.list(TypeReferences.ITEM_NAME.in(schema)))));
		schema.register(map, "minecraft:suspicious_sand", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("item", TypeReferences.ITEM_STACK.in(schema))));
		return map;
	}
}
