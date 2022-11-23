package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema1470 extends IdentifierNormalizingSchema {
	public Schema1470(int i, Schema schema) {
		super(i, schema);
	}

	protected static void targetEntityItems(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
		schema.register(map, entityId, (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		targetEntityItems(schema, map, "minecraft:turtle");
		targetEntityItems(schema, map, "minecraft:cod_mob");
		targetEntityItems(schema, map, "minecraft:tropical_fish");
		targetEntityItems(schema, map, "minecraft:salmon_mob");
		targetEntityItems(schema, map, "minecraft:puffer_fish");
		targetEntityItems(schema, map, "minecraft:phantom");
		targetEntityItems(schema, map, "minecraft:dolphin");
		targetEntityItems(schema, map, "minecraft:drowned");
		schema.register(map, "minecraft:trident", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema))));
		return map;
	}
}
