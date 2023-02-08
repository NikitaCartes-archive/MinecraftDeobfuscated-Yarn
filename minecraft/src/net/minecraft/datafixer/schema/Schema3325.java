package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3325 extends IdentifierNormalizingSchema {
	public Schema3325(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.register(map, "minecraft:item_display", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("item", TypeReferences.ITEM_STACK.in(schema))));
		schema.register(
			map, "minecraft:block_display", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("block_state", TypeReferences.BLOCK_STATE.in(schema)))
		);
		schema.registerSimple(map, "minecraft:text_display");
		return map;
	}
}
