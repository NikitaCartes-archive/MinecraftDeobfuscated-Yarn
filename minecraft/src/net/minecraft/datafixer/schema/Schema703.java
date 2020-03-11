package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema703 extends Schema {
	public Schema703(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		map.remove("EntityHorse");
		schema.register(
			map,
			"Horse",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)
				))
		);
		schema.register(
			map,
			"Donkey",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)
				))
		);
		schema.register(
			map,
			"Mule",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)
				))
		);
		schema.register(
			map, "ZombieHorse", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)))
		);
		schema.register(
			map, "SkeletonHorse", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)))
		);
		return map;
	}
}
