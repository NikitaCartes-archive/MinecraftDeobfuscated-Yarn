package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema3819 extends IdentifierNormalizingSchema {
	public Schema3819(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.registerSimple(map, "minecraft:big_brain");
		schema.registerSimple(map, "minecraft:poisonous_potato_cutter");
		schema.registerSimple(map, "minecraft:fletching");
		schema.registerSimple(map, "minecraft:potato_refinery");
		return map;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		Schema1460.targetEntityItems(schema, map, "minecraft:batato");
		Schema1460.targetEntityItems(schema, map, "minecraft:toxifin");
		Schema1460.targetEntityItems(schema, map, "minecraft:plaguewhale");
		Schema1460.targetEntityItems(schema, map, "minecraft:poisonous_potato_zombie");
		Schema1460.targetEntityItems(schema, map, "minecraft:mega_spud");
		schema.registerSimple(map, "minecraft:grid_carrier");
		schema.registerSimple(map, "minecraft:vine_projectile");
		schema.registerSimple(map, "minecraft:eye_of_potato");
		return map;
	}
}
