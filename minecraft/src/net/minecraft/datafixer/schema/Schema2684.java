package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema2684 extends IdentifierNormalizingSchema {
	public Schema2684(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
		schema.registerType(false, TypeReferences.GAME_EVENT_NAME, () -> DSL.constType(getIdentifierType()));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(
			map,
			"minecraft:sculk_sensor",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"listener", DSL.optionalFields("event", DSL.optionalFields("game_event", TypeReferences.GAME_EVENT_NAME.in(schema)))
				))
		);
		return map;
	}
}
