package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class class_1231 extends class_1220 {
	public class_1231(int i, Schema schema) {
		super(i, schema);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.registerSimple(map, "minecraft:egg");
		schema.registerSimple(map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:fireball");
		schema.register(map, "minecraft:potion", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Potion", class_1208.field_5712.in(schema))));
		schema.registerSimple(map, "minecraft:small_fireball");
		schema.registerSimple(map, "minecraft:snowball");
		schema.registerSimple(map, "minecraft:wither_skull");
		schema.registerSimple(map, "minecraft:xp_bottle");
		schema.register(map, "minecraft:arrow", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("inBlockState", class_1208.field_5720.in(schema))));
		schema.register(
			map,
			"minecraft:enderman",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("carriedBlockState", class_1208.field_5720.in(schema), class_1222.method_5196(schema)))
		);
		schema.register(
			map,
			"minecraft:falling_block",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("BlockState", class_1208.field_5720.in(schema), "TileEntityData", class_1208.field_5727.in(schema)))
		);
		schema.register(map, "minecraft:spectral_arrow", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("inBlockState", class_1208.field_5720.in(schema))));
		schema.register(
			map,
			"minecraft:chest_minecart",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema), "Items", DSL.list(class_1208.field_5712.in(schema))))
		);
		schema.register(map, "minecraft:commandblock_minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema))));
		schema.register(map, "minecraft:furnace_minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema))));
		schema.register(
			map,
			"minecraft:hopper_minecart",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema), "Items", DSL.list(class_1208.field_5712.in(schema))))
		);
		schema.register(map, "minecraft:minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema))));
		schema.register(
			map,
			"minecraft:spawner_minecart",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema), class_1208.field_5718.in(schema)))
		);
		schema.register(map, "minecraft:tnt_minecart", (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema))));
		return map;
	}
}
