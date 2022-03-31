package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3081 extends IdentifierNormalizingSchema {
	public Schema3081(int i, Schema schema) {
		super(i, schema);
	}

	protected static void register(Schema schema, Map<String, Supplier<TypeTemplate>> map, String id) {
		schema.register(map, id, (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
		schema.register(
			map,
			"minecraft:warden",
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
		register(schema, map, "minecraft:warden");
		return map;
	}
}
