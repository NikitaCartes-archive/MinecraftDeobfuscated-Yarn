package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3818_5 extends IdentifierNormalizingSchema {
	public Schema3818_5(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			true,
			TypeReferences.ITEM_STACK,
			() -> DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema), "components", TypeReferences.DATA_COMPONENTS.in(schema))
		);
	}
}
