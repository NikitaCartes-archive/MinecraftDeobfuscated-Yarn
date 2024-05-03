package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3938 extends IdentifierNormalizingSchema {
	public Schema3938(int i, Schema schema) {
		super(i, schema);
	}

	protected static TypeTemplate method_59913(Schema schema) {
		return DSL.optionalFields(
			"inBlockState", TypeReferences.BLOCK_STATE.in(schema), "item", TypeReferences.ITEM_STACK.in(schema), "weapon", TypeReferences.ITEM_STACK.in(schema)
		);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.register(map, "minecraft:spectral_arrow", (Supplier<TypeTemplate>)(() -> method_59913(schema)));
		schema.register(map, "minecraft:arrow", (Supplier<TypeTemplate>)(() -> method_59913(schema)));
		return map;
	}
}
