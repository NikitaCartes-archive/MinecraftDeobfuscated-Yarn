package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3083 extends IdentifierNormalizingSchema {
	public Schema3083(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_42645(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
		schema.register(
			map,
			name,
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"ArmorItems",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"HandItems",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"listener",
					DSL.optionalFields("event", DSL.optionalFields("game_event", TypeReferences.GAME_EVENT_NAME.in(schema)))
				))
		);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_42645(schema, map, "minecraft:allay");
		return map;
	}
}
