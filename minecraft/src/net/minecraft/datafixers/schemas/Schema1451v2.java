package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixers.TypeReferences;

public class Schema1451v2 extends SchemaIdentifierNormalize {
	public Schema1451v2(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.register(map, "minecraft:piston", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("blockState", TypeReferences.BLOCK_STATE.in(schema))));
		return map;
	}
}
