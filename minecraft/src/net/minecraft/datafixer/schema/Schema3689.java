package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema3689 extends IdentifierNormalizingSchema {
	public Schema3689(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.register(map, "minecraft:breeze", (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
		schema.registerSimple(map, "minecraft:wind_charge");
		schema.registerSimple(map, "minecraft:breeze_wind_charge");
		return map;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(
			map,
			"minecraft:trial_spawner",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"spawn_potentials",
					DSL.list(DSL.fields("data", DSL.fields("entity", TypeReferences.ENTITY_TREE.in(schema)))),
					"spawn_data",
					DSL.fields("entity", TypeReferences.ENTITY_TREE.in(schema))
				))
		);
		return map;
	}
}
