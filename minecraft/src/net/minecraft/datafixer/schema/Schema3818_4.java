package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3818_4 extends IdentifierNormalizingSchema {
	public Schema3818_4(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			true, TypeReferences.PARTICLE, () -> DSL.optionalFields("item", TypeReferences.ITEM_STACK.in(schema), "block_state", TypeReferences.BLOCK_STATE.in(schema))
		);
	}
}
