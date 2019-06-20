package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class class_1238 extends class_1220 {
	public class_1238(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_5232(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> class_1222.method_5196(schema)));
	}

	protected static void method_5273(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("Items", DSL.list(class_1208.field_5712.in(schema)))));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.<String, Supplier<TypeTemplate>>newHashMap();
		schema.registerSimple(map, "minecraft:area_effect_cloud");
		method_5232(schema, map, "minecraft:armor_stand");
		schema.register(map, "minecraft:arrow", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("inBlockState", class_1208.field_5720.in(schema))));
		method_5232(schema, map, "minecraft:bat");
		method_5232(schema, map, "minecraft:blaze");
		schema.registerSimple(map, "minecraft:boat");
		method_5232(schema, map, "minecraft:cave_spider");
		schema.register(
			map,
			"minecraft:chest_minecart",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"DisplayState", class_1208.field_5720.in(schema), "Items", DSL.list(class_1208.field_5712.in(schema))
				))
		);
		method_5232(schema, map, "minecraft:chicken");
		schema.register(
			map, "minecraft:commandblock_minecart", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema)))
		);
		method_5232(schema, map, "minecraft:cow");
		method_5232(schema, map, "minecraft:creeper");
		schema.register(
			map,
			"minecraft:donkey",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Items", DSL.list(class_1208.field_5712.in(schema)), "SaddleItem", class_1208.field_5712.in(schema), class_1222.method_5196(schema)
				))
		);
		schema.registerSimple(map, "minecraft:dragon_fireball");
		schema.registerSimple(map, "minecraft:egg");
		method_5232(schema, map, "minecraft:elder_guardian");
		schema.registerSimple(map, "minecraft:ender_crystal");
		method_5232(schema, map, "minecraft:ender_dragon");
		schema.register(
			map,
			"minecraft:enderman",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("carriedBlockState", class_1208.field_5720.in(schema), class_1222.method_5196(schema)))
		);
		method_5232(schema, map, "minecraft:endermite");
		schema.registerSimple(map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:evocation_fangs");
		method_5232(schema, map, "minecraft:evocation_illager");
		schema.registerSimple(map, "minecraft:eye_of_ender_signal");
		schema.register(
			map,
			"minecraft:falling_block",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"BlockState", class_1208.field_5720.in(schema), "TileEntityData", class_1208.field_5727.in(schema)
				))
		);
		schema.registerSimple(map, "minecraft:fireball");
		schema.register(
			map, "minecraft:fireworks_rocket", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("FireworksItem", class_1208.field_5712.in(schema)))
		);
		schema.register(
			map, "minecraft:furnace_minecart", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema)))
		);
		method_5232(schema, map, "minecraft:ghast");
		method_5232(schema, map, "minecraft:giant");
		method_5232(schema, map, "minecraft:guardian");
		schema.register(
			map,
			"minecraft:hopper_minecart",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"DisplayState", class_1208.field_5720.in(schema), "Items", DSL.list(class_1208.field_5712.in(schema))
				))
		);
		schema.register(
			map,
			"minecraft:horse",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"ArmorItem", class_1208.field_5712.in(schema), "SaddleItem", class_1208.field_5712.in(schema), class_1222.method_5196(schema)
				))
		);
		method_5232(schema, map, "minecraft:husk");
		schema.registerSimple(map, "minecraft:illusion_illager");
		schema.register(map, "minecraft:item", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Item", class_1208.field_5712.in(schema))));
		schema.register(map, "minecraft:item_frame", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Item", class_1208.field_5712.in(schema))));
		schema.registerSimple(map, "minecraft:leash_knot");
		schema.register(
			map,
			"minecraft:llama",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Items",
					DSL.list(class_1208.field_5712.in(schema)),
					"SaddleItem",
					class_1208.field_5712.in(schema),
					"DecorItem",
					class_1208.field_5712.in(schema),
					class_1222.method_5196(schema)
				))
		);
		schema.registerSimple(map, "minecraft:llama_spit");
		method_5232(schema, map, "minecraft:magma_cube");
		schema.register(map, "minecraft:minecart", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema))));
		method_5232(schema, map, "minecraft:mooshroom");
		schema.register(
			map,
			"minecraft:mule",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Items", DSL.list(class_1208.field_5712.in(schema)), "SaddleItem", class_1208.field_5712.in(schema), class_1222.method_5196(schema)
				))
		);
		method_5232(schema, map, "minecraft:ocelot");
		schema.registerSimple(map, "minecraft:painting");
		schema.registerSimple(map, "minecraft:parrot");
		method_5232(schema, map, "minecraft:pig");
		method_5232(schema, map, "minecraft:polar_bear");
		schema.register(map, "minecraft:potion", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Potion", class_1208.field_5712.in(schema))));
		method_5232(schema, map, "minecraft:rabbit");
		method_5232(schema, map, "minecraft:sheep");
		method_5232(schema, map, "minecraft:shulker");
		schema.registerSimple(map, "minecraft:shulker_bullet");
		method_5232(schema, map, "minecraft:silverfish");
		method_5232(schema, map, "minecraft:skeleton");
		schema.register(
			map,
			"minecraft:skeleton_horse",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("SaddleItem", class_1208.field_5712.in(schema), class_1222.method_5196(schema)))
		);
		method_5232(schema, map, "minecraft:slime");
		schema.registerSimple(map, "minecraft:small_fireball");
		schema.registerSimple(map, "minecraft:snowball");
		method_5232(schema, map, "minecraft:snowman");
		schema.register(
			map,
			"minecraft:spawner_minecart",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema), class_1208.field_5718.in(schema)))
		);
		schema.register(
			map, "minecraft:spectral_arrow", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("inBlockState", class_1208.field_5720.in(schema)))
		);
		method_5232(schema, map, "minecraft:spider");
		method_5232(schema, map, "minecraft:squid");
		method_5232(schema, map, "minecraft:stray");
		schema.registerSimple(map, "minecraft:tnt");
		schema.register(
			map, "minecraft:tnt_minecart", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("DisplayState", class_1208.field_5720.in(schema)))
		);
		method_5232(schema, map, "minecraft:vex");
		schema.register(
			map,
			"minecraft:villager",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Inventory",
					DSL.list(class_1208.field_5712.in(schema)),
					"Offers",
					DSL.optionalFields(
						"Recipes",
						DSL.list(DSL.optionalFields("buy", class_1208.field_5712.in(schema), "buyB", class_1208.field_5712.in(schema), "sell", class_1208.field_5712.in(schema)))
					),
					class_1222.method_5196(schema)
				))
		);
		method_5232(schema, map, "minecraft:villager_golem");
		method_5232(schema, map, "minecraft:vindication_illager");
		method_5232(schema, map, "minecraft:witch");
		method_5232(schema, map, "minecraft:wither");
		method_5232(schema, map, "minecraft:wither_skeleton");
		schema.registerSimple(map, "minecraft:wither_skull");
		method_5232(schema, map, "minecraft:wolf");
		schema.registerSimple(map, "minecraft:xp_bottle");
		schema.registerSimple(map, "minecraft:xp_orb");
		method_5232(schema, map, "minecraft:zombie");
		schema.register(
			map,
			"minecraft:zombie_horse",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("SaddleItem", class_1208.field_5712.in(schema), class_1222.method_5196(schema)))
		);
		method_5232(schema, map, "minecraft:zombie_pigman");
		method_5232(schema, map, "minecraft:zombie_villager");
		return map;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.<String, Supplier<TypeTemplate>>newHashMap();
		method_5273(schema, map, "minecraft:furnace");
		method_5273(schema, map, "minecraft:chest");
		method_5273(schema, map, "minecraft:trapped_chest");
		schema.registerSimple(map, "minecraft:ender_chest");
		schema.register(map, "minecraft:jukebox", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("RecordItem", class_1208.field_5712.in(schema))));
		method_5273(schema, map, "minecraft:dispenser");
		method_5273(schema, map, "minecraft:dropper");
		schema.registerSimple(map, "minecraft:sign");
		schema.register(map, "minecraft:mob_spawner", (Function<String, TypeTemplate>)(string -> class_1208.field_5718.in(schema)));
		schema.register(map, "minecraft:piston", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("blockState", class_1208.field_5720.in(schema))));
		method_5273(schema, map, "minecraft:brewing_stand");
		schema.registerSimple(map, "minecraft:enchanting_table");
		schema.registerSimple(map, "minecraft:end_portal");
		schema.registerSimple(map, "minecraft:beacon");
		schema.registerSimple(map, "minecraft:skull");
		schema.registerSimple(map, "minecraft:daylight_detector");
		method_5273(schema, map, "minecraft:hopper");
		schema.registerSimple(map, "minecraft:comparator");
		schema.registerSimple(map, "minecraft:banner");
		schema.registerSimple(map, "minecraft:structure_block");
		schema.registerSimple(map, "minecraft:end_gateway");
		schema.registerSimple(map, "minecraft:command_block");
		method_5273(schema, map, "minecraft:shulker_box");
		schema.registerSimple(map, "minecraft:bed");
		return map;
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		schema.registerType(false, class_1208.field_5710, DSL::remainder);
		schema.registerType(false, class_1208.field_5711, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(
			false,
			class_1208.field_5715,
			() -> DSL.optionalFields(
					"RootVehicle",
					DSL.optionalFields("Entity", class_1208.field_5723.in(schema)),
					"Inventory",
					DSL.list(class_1208.field_5712.in(schema)),
					"EnderItems",
					DSL.list(class_1208.field_5712.in(schema)),
					DSL.optionalFields(
						"ShoulderEntityLeft",
						class_1208.field_5723.in(schema),
						"ShoulderEntityRight",
						class_1208.field_5723.in(schema),
						"recipeBook",
						DSL.optionalFields("recipes", DSL.list(class_1208.field_5711.in(schema)), "toBeDisplayed", DSL.list(class_1208.field_5711.in(schema)))
					)
				)
		);
		schema.registerType(
			false,
			class_1208.field_5726,
			() -> DSL.fields(
					"Level",
					DSL.optionalFields(
						"Entities",
						DSL.list(class_1208.field_5723.in(schema)),
						"TileEntities",
						DSL.list(class_1208.field_5727.in(schema)),
						"TileTicks",
						DSL.list(DSL.fields("i", class_1208.field_5731.in(schema))),
						"Sections",
						DSL.list(DSL.optionalFields("Palette", DSL.list(class_1208.field_5720.in(schema))))
					)
				)
		);
		schema.registerType(true, class_1208.field_5727, () -> DSL.taggedChoiceLazy("id", DSL.namespacedString(), map2));
		schema.registerType(
			true, class_1208.field_5723, () -> DSL.optionalFields("Passengers", DSL.list(class_1208.field_5723.in(schema)), class_1208.field_5729.in(schema))
		);
		schema.registerType(true, class_1208.field_5729, () -> DSL.taggedChoiceLazy("id", DSL.namespacedString(), map));
		schema.registerType(
			true,
			class_1208.field_5712,
			() -> DSL.hook(
					DSL.optionalFields(
						"id",
						class_1208.field_5713.in(schema),
						"tag",
						DSL.optionalFields(
							"EntityTag",
							class_1208.field_5723.in(schema),
							"BlockEntityTag",
							class_1208.field_5727.in(schema),
							"CanDestroy",
							DSL.list(class_1208.field_5731.in(schema)),
							"CanPlaceOn",
							DSL.list(class_1208.field_5731.in(schema))
						)
					),
					class_1253.field_5746,
					HookFunction.IDENTITY
				)
		);
		schema.registerType(false, class_1208.field_5722, () -> DSL.compoundList(DSL.list(class_1208.field_5712.in(schema))));
		schema.registerType(false, class_1208.field_5717, DSL::remainder);
		schema.registerType(
			false,
			class_1208.field_5716,
			() -> DSL.optionalFields(
					"entities",
					DSL.list(DSL.optionalFields("nbt", class_1208.field_5723.in(schema))),
					"blocks",
					DSL.list(DSL.optionalFields("nbt", class_1208.field_5727.in(schema))),
					"palette",
					DSL.list(class_1208.field_5720.in(schema))
				)
		);
		schema.registerType(false, class_1208.field_5731, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, class_1208.field_5713, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, class_1208.field_5720, DSL::remainder);
		Supplier<TypeTemplate> supplier = () -> DSL.compoundList(class_1208.field_5713.in(schema), DSL.constType(DSL.intType()));
		schema.registerType(
			false,
			class_1208.field_5730,
			() -> DSL.optionalFields(
					"stats",
					DSL.optionalFields(
						"minecraft:mined",
						DSL.compoundList(class_1208.field_5731.in(schema), DSL.constType(DSL.intType())),
						"minecraft:crafted",
						(TypeTemplate)supplier.get(),
						"minecraft:used",
						(TypeTemplate)supplier.get(),
						"minecraft:broken",
						(TypeTemplate)supplier.get(),
						"minecraft:picked_up",
						(TypeTemplate)supplier.get(),
						DSL.optionalFields(
							"minecraft:dropped",
							(TypeTemplate)supplier.get(),
							"minecraft:killed",
							DSL.compoundList(class_1208.field_5719.in(schema), DSL.constType(DSL.intType())),
							"minecraft:killed_by",
							DSL.compoundList(class_1208.field_5719.in(schema), DSL.constType(DSL.intType())),
							"minecraft:custom",
							DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType()))
						)
					)
				)
		);
		schema.registerType(
			false,
			class_1208.field_5732,
			() -> DSL.optionalFields(
					"data",
					DSL.optionalFields(
						"Features",
						DSL.compoundList(class_1208.field_5724.in(schema)),
						"Objectives",
						DSL.list(class_1208.field_5721.in(schema)),
						"Teams",
						DSL.list(class_1208.field_5714.in(schema))
					)
				)
		);
		schema.registerType(
			false,
			class_1208.field_5724,
			() -> DSL.optionalFields(
					"Children",
					DSL.list(
						DSL.optionalFields(
							"CA",
							class_1208.field_5720.in(schema),
							"CB",
							class_1208.field_5720.in(schema),
							"CC",
							class_1208.field_5720.in(schema),
							"CD",
							class_1208.field_5720.in(schema)
						)
					)
				)
		);
		schema.registerType(false, class_1208.field_5721, DSL::remainder);
		schema.registerType(false, class_1208.field_5714, DSL::remainder);
		schema.registerType(
			true,
			class_1208.field_5718,
			() -> DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", class_1208.field_5723.in(schema))), "SpawnData", class_1208.field_5723.in(schema))
		);
		schema.registerType(
			false,
			class_1208.field_5725,
			() -> DSL.optionalFields(
					"minecraft:adventure/adventuring_time",
					DSL.optionalFields("criteria", DSL.compoundList(class_1208.field_5728.in(schema), DSL.constType(DSL.string()))),
					"minecraft:adventure/kill_a_mob",
					DSL.optionalFields("criteria", DSL.compoundList(class_1208.field_5719.in(schema), DSL.constType(DSL.string()))),
					"minecraft:adventure/kill_all_mobs",
					DSL.optionalFields("criteria", DSL.compoundList(class_1208.field_5719.in(schema), DSL.constType(DSL.string()))),
					"minecraft:husbandry/bred_all_animals",
					DSL.optionalFields("criteria", DSL.compoundList(class_1208.field_5719.in(schema), DSL.constType(DSL.string())))
				)
		);
		schema.registerType(false, class_1208.field_5728, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, class_1208.field_5719, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, class_1208.field_19224, DSL::remainder);
	}
}
