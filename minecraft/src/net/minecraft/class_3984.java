package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;

public class class_3984 extends SchemaIdentifierNormalize {
	public class_3984(int i, Schema schema) {
		super(i, schema);
	}

	protected static TypeTemplate method_17997(Schema schema) {
		return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
	}

	protected static void method_17998(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> method_17997(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		map.remove("minecraft:illager_beast");
		method_17998(schema, map, "minecraft:ravager");
		return map;
	}
}
