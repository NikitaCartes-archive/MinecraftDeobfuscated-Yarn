package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema2100 extends IdentifierNormalizingSchema {
	public Schema2100(int i, Schema schema) {
		super(i, schema);
	}

	protected static void registerEntity(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, String name) {
		schema.register(entityTypes, name, (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		registerEntity(schema, map, "minecraft:bee");
		registerEntity(schema, map, "minecraft:bee_stinger");
		return map;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(
			map,
			"minecraft:beehive",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("Bees", DSL.list(DSL.optionalFields("EntityData", TypeReferences.ENTITY_TREE.in(schema)))))
		);
		return map;
	}
}
