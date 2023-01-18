package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema2688 extends IdentifierNormalizingSchema {
	public Schema2688(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.register(map, "minecraft:glow_squid", (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
		schema.register(
			map, "minecraft:glow_item_frame", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)))
		);
		return map;
	}
}
