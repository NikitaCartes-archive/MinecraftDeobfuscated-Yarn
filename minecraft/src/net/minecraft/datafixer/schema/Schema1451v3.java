package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema1451v3 extends IdentifierNormalizingSchema {
	public Schema1451v3(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.registerSimple(map, "minecraft:egg");
		schema.registerSimple(map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:fireball");
		schema.register(map, "minecraft:potion", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(schema))));
		schema.registerSimple(map, "minecraft:small_fireball");
		schema.registerSimple(map, "minecraft:snowball");
		schema.registerSimple(map, "minecraft:wither_skull");
		schema.registerSimple(map, "minecraft:xp_bottle");
		schema.register(map, "minecraft:arrow", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema))));
		schema.register(
			map,
			"minecraft:enderman",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("carriedBlockState", TypeReferences.BLOCK_STATE.in(schema), Schema100.targetItems(schema)))
		);
		schema.register(
			map,
			"minecraft:falling_block",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"BlockState", TypeReferences.BLOCK_STATE.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema)
				))
		);
		schema.register(map, "minecraft:spectral_arrow", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema))));
		schema.register(
			map,
			"minecraft:chest_minecart",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))
				))
		);
		schema.register(
			map, "minecraft:commandblock_minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)))
		);
		schema.register(map, "minecraft:furnace_minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema))));
		schema.register(
			map,
			"minecraft:hopper_minecart",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))
				))
		);
		schema.register(map, "minecraft:minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema))));
		schema.register(
			map,
			"minecraft:spawner_minecart",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema)))
		);
		schema.register(map, "minecraft:tnt_minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema))));
		return map;
	}
}
