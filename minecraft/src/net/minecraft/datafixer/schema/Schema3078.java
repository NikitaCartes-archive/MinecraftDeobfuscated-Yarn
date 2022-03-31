package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3078 extends IdentifierNormalizingSchema {
	public Schema3078(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_41318(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_41318(schema, map, "minecraft:frog");
		method_41318(schema, map, "minecraft:tadpole");
		return map;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(
			map,
			"minecraft:sculk_shrieker",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"listener", DSL.optionalFields("event", DSL.optionalFields("game_event", TypeReferences.GAME_EVENT_NAME.in(schema)))
				))
		);
		return map;
	}
}
