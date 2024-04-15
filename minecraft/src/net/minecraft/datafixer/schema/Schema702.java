package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema702 extends Schema {
	public Schema702(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.register(
			map,
			"ZombieVillager",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Offers", DSL.optionalFields("Recipes", DSL.list(TypeReferences.VILLAGER_TRADE.in(schema))), Schema100.targetItems(schema)
				))
		);
		schema.register(map, "Husk", (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
		return map;
	}
}
