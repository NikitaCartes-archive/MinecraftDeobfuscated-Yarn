package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3204 extends IdentifierNormalizingSchema {
	public Schema3204(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(
			map, "minecraft:chiseled_bookshelf", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))))
		);
		return map;
	}
}
