package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3807 extends IdentifierNormalizingSchema {
	public Schema3807(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(
			map,
			"minecraft:vault",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"config",
					DSL.optionalFields("key_item", TypeReferences.ITEM_STACK.in(schema)),
					"server_data",
					DSL.optionalFields("items_to_eject", DSL.list(TypeReferences.ITEM_STACK.in(schema))),
					"shared_data",
					DSL.optionalFields("display_item", TypeReferences.ITEM_STACK.in(schema))
				))
		);
		return map;
	}
}
