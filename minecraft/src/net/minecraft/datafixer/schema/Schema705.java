package net.minecraft.datafixer.schema;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema705 extends IdentifierNormalizingSchema {
	static final Map<String, String> ITEM_TO_ENTITY = ImmutableMap.<String, String>builder()
		.put("minecraft:armor_stand", "minecraft:armor_stand")
		.put("minecraft:painting", "minecraft:painting")
		.put("minecraft:armadillo_spawn_egg", "minecraft:armadillo")
		.put("minecraft:allay_spawn_egg", "minecraft:allay")
		.put("minecraft:axolotl_spawn_egg", "minecraft:axolotl")
		.put("minecraft:bat_spawn_egg", "minecraft:bat")
		.put("minecraft:bee_spawn_egg", "minecraft:bee")
		.put("minecraft:blaze_spawn_egg", "minecraft:blaze")
		.put("minecraft:bogged_spawn_egg", "minecraft:bogged")
		.put("minecraft:breeze_spawn_egg", "minecraft:breeze")
		.put("minecraft:cat_spawn_egg", "minecraft:cat")
		.put("minecraft:camel_spawn_egg", "minecraft:camel")
		.put("minecraft:cave_spider_spawn_egg", "minecraft:cave_spider")
		.put("minecraft:chicken_spawn_egg", "minecraft:chicken")
		.put("minecraft:cod_spawn_egg", "minecraft:cod")
		.put("minecraft:cow_spawn_egg", "minecraft:cow")
		.put("minecraft:creeper_spawn_egg", "minecraft:creeper")
		.put("minecraft:dolphin_spawn_egg", "minecraft:dolphin")
		.put("minecraft:donkey_spawn_egg", "minecraft:donkey")
		.put("minecraft:drowned_spawn_egg", "minecraft:drowned")
		.put("minecraft:elder_guardian_spawn_egg", "minecraft:elder_guardian")
		.put("minecraft:ender_dragon_spawn_egg", "minecraft:ender_dragon")
		.put("minecraft:enderman_spawn_egg", "minecraft:enderman")
		.put("minecraft:endermite_spawn_egg", "minecraft:endermite")
		.put("minecraft:evoker_spawn_egg", "minecraft:evoker")
		.put("minecraft:fox_spawn_egg", "minecraft:fox")
		.put("minecraft:frog_spawn_egg", "minecraft:frog")
		.put("minecraft:ghast_spawn_egg", "minecraft:ghast")
		.put("minecraft:glow_squid_spawn_egg", "minecraft:glow_squid")
		.put("minecraft:goat_spawn_egg", "minecraft:goat")
		.put("minecraft:guardian_spawn_egg", "minecraft:guardian")
		.put("minecraft:hoglin_spawn_egg", "minecraft:hoglin")
		.put("minecraft:horse_spawn_egg", "minecraft:horse")
		.put("minecraft:husk_spawn_egg", "minecraft:husk")
		.put("minecraft:iron_golem_spawn_egg", "minecraft:iron_golem")
		.put("minecraft:llama_spawn_egg", "minecraft:llama")
		.put("minecraft:magma_cube_spawn_egg", "minecraft:magma_cube")
		.put("minecraft:mooshroom_spawn_egg", "minecraft:mooshroom")
		.put("minecraft:mule_spawn_egg", "minecraft:mule")
		.put("minecraft:ocelot_spawn_egg", "minecraft:ocelot")
		.put("minecraft:panda_spawn_egg", "minecraft:panda")
		.put("minecraft:parrot_spawn_egg", "minecraft:parrot")
		.put("minecraft:phantom_spawn_egg", "minecraft:phantom")
		.put("minecraft:pig_spawn_egg", "minecraft:pig")
		.put("minecraft:piglin_spawn_egg", "minecraft:piglin")
		.put("minecraft:piglin_brute_spawn_egg", "minecraft:piglin_brute")
		.put("minecraft:pillager_spawn_egg", "minecraft:pillager")
		.put("minecraft:polar_bear_spawn_egg", "minecraft:polar_bear")
		.put("minecraft:pufferfish_spawn_egg", "minecraft:pufferfish")
		.put("minecraft:rabbit_spawn_egg", "minecraft:rabbit")
		.put("minecraft:ravager_spawn_egg", "minecraft:ravager")
		.put("minecraft:salmon_spawn_egg", "minecraft:salmon")
		.put("minecraft:sheep_spawn_egg", "minecraft:sheep")
		.put("minecraft:shulker_spawn_egg", "minecraft:shulker")
		.put("minecraft:silverfish_spawn_egg", "minecraft:silverfish")
		.put("minecraft:skeleton_spawn_egg", "minecraft:skeleton")
		.put("minecraft:skeleton_horse_spawn_egg", "minecraft:skeleton_horse")
		.put("minecraft:slime_spawn_egg", "minecraft:slime")
		.put("minecraft:sniffer_spawn_egg", "minecraft:sniffer")
		.put("minecraft:snow_golem_spawn_egg", "minecraft:snow_golem")
		.put("minecraft:spider_spawn_egg", "minecraft:spider")
		.put("minecraft:squid_spawn_egg", "minecraft:squid")
		.put("minecraft:stray_spawn_egg", "minecraft:stray")
		.put("minecraft:strider_spawn_egg", "minecraft:strider")
		.put("minecraft:tadpole_spawn_egg", "minecraft:tadpole")
		.put("minecraft:trader_llama_spawn_egg", "minecraft:trader_llama")
		.put("minecraft:tropical_fish_spawn_egg", "minecraft:tropical_fish")
		.put("minecraft:turtle_spawn_egg", "minecraft:turtle")
		.put("minecraft:vex_spawn_egg", "minecraft:vex")
		.put("minecraft:villager_spawn_egg", "minecraft:villager")
		.put("minecraft:vindicator_spawn_egg", "minecraft:vindicator")
		.put("minecraft:wandering_trader_spawn_egg", "minecraft:wandering_trader")
		.put("minecraft:warden_spawn_egg", "minecraft:warden")
		.put("minecraft:witch_spawn_egg", "minecraft:witch")
		.put("minecraft:wither_spawn_egg", "minecraft:wither")
		.put("minecraft:wither_skeleton_spawn_egg", "minecraft:wither_skeleton")
		.put("minecraft:wolf_spawn_egg", "minecraft:wolf")
		.put("minecraft:zoglin_spawn_egg", "minecraft:zoglin")
		.put("minecraft:zombie_spawn_egg", "minecraft:zombie")
		.put("minecraft:zombie_horse_spawn_egg", "minecraft:zombie_horse")
		.put("minecraft:zombie_villager_spawn_egg", "minecraft:zombie_villager")
		.put("minecraft:zombified_piglin_spawn_egg", "minecraft:zombified_piglin")
		.put("minecraft:item_frame", "minecraft:item_frame")
		.put("minecraft:boat", "minecraft:boat")
		.put("minecraft:oak_boat", "minecraft:boat")
		.put("minecraft:oak_chest_boat", "minecraft:chest_boat")
		.put("minecraft:spruce_boat", "minecraft:boat")
		.put("minecraft:spruce_chest_boat", "minecraft:chest_boat")
		.put("minecraft:birch_boat", "minecraft:boat")
		.put("minecraft:birch_chest_boat", "minecraft:chest_boat")
		.put("minecraft:jungle_boat", "minecraft:boat")
		.put("minecraft:jungle_chest_boat", "minecraft:chest_boat")
		.put("minecraft:acacia_boat", "minecraft:boat")
		.put("minecraft:acacia_chest_boat", "minecraft:chest_boat")
		.put("minecraft:cherry_boat", "minecraft:boat")
		.put("minecraft:cherry_chest_boat", "minecraft:chest_boat")
		.put("minecraft:dark_oak_boat", "minecraft:boat")
		.put("minecraft:dark_oak_chest_boat", "minecraft:chest_boat")
		.put("minecraft:mangrove_boat", "minecraft:boat")
		.put("minecraft:mangrove_chest_boat", "minecraft:chest_boat")
		.put("minecraft:bamboo_raft", "minecraft:boat")
		.put("minecraft:bamboo_chest_raft", "minecraft:chest_boat")
		.put("minecraft:minecart", "minecraft:minecart")
		.put("minecraft:chest_minecart", "minecraft:chest_minecart")
		.put("minecraft:furnace_minecart", "minecraft:furnace_minecart")
		.put("minecraft:tnt_minecart", "minecraft:tnt_minecart")
		.put("minecraft:hopper_minecart", "minecraft:hopper_minecart")
		.build();
	protected static final HookFunction field_5746 = new HookFunction() {
		@Override
		public <T> T apply(DynamicOps<T> ops, T value) {
			return Schema99.updateBlockEntityTags(new Dynamic<>(ops, value), Schema704.BLOCK_RENAMES, Schema705.ITEM_TO_ENTITY);
		}
	};

	public Schema705(int i, Schema schema) {
		super(i, schema);
	}

	protected static void targetEntityItems(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
		schema.register(map, entityId, (Supplier<TypeTemplate>)(() -> Schema100.targetItems(schema)));
	}

	protected static void targetInTile(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
		schema.register(map, entityId, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema))));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.<String, Supplier<TypeTemplate>>newHashMap();
		schema.register(
			map, "minecraft:area_effect_cloud", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Particle", TypeReferences.PARTICLE.in(schema)))
		);
		targetEntityItems(schema, map, "minecraft:armor_stand");
		schema.register(map, "minecraft:arrow", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema))));
		targetEntityItems(schema, map, "minecraft:bat");
		targetEntityItems(schema, map, "minecraft:blaze");
		schema.registerSimple(map, "minecraft:boat");
		targetEntityItems(schema, map, "minecraft:cave_spider");
		schema.register(
			map,
			"minecraft:chest_minecart",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields(
					"DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))
				))
		);
		targetEntityItems(schema, map, "minecraft:chicken");
		schema.register(
			map, "minecraft:commandblock_minecart", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)))
		);
		targetEntityItems(schema, map, "minecraft:cow");
		targetEntityItems(schema, map, "minecraft:creeper");
		schema.register(
			map,
			"minecraft:donkey",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields(
					"Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)
				))
		);
		schema.registerSimple(map, "minecraft:dragon_fireball");
		targetInTile(schema, map, "minecraft:egg");
		targetEntityItems(schema, map, "minecraft:elder_guardian");
		schema.registerSimple(map, "minecraft:ender_crystal");
		targetEntityItems(schema, map, "minecraft:ender_dragon");
		schema.register(
			map,
			"minecraft:enderman",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), Schema100.targetItems(schema)))
		);
		targetEntityItems(schema, map, "minecraft:endermite");
		targetInTile(schema, map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:eye_of_ender_signal");
		schema.register(
			map,
			"minecraft:falling_block",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields(
					"Block", TypeReferences.BLOCK_NAME.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema)
				))
		);
		targetInTile(schema, map, "minecraft:fireball");
		schema.register(
			map, "minecraft:fireworks_rocket", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema)))
		);
		schema.register(
			map, "minecraft:furnace_minecart", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)))
		);
		targetEntityItems(schema, map, "minecraft:ghast");
		targetEntityItems(schema, map, "minecraft:giant");
		targetEntityItems(schema, map, "minecraft:guardian");
		schema.register(
			map,
			"minecraft:hopper_minecart",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields(
					"DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))
				))
		);
		schema.register(
			map,
			"minecraft:horse",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields(
					"ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)
				))
		);
		targetEntityItems(schema, map, "minecraft:husk");
		schema.register(map, "minecraft:item", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema))));
		schema.register(map, "minecraft:item_frame", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema))));
		schema.registerSimple(map, "minecraft:leash_knot");
		targetEntityItems(schema, map, "minecraft:magma_cube");
		schema.register(map, "minecraft:minecart", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema))));
		targetEntityItems(schema, map, "minecraft:mooshroom");
		schema.register(
			map,
			"minecraft:mule",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields(
					"Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)
				))
		);
		targetEntityItems(schema, map, "minecraft:ocelot");
		schema.registerSimple(map, "minecraft:painting");
		schema.registerSimple(map, "minecraft:parrot");
		targetEntityItems(schema, map, "minecraft:pig");
		targetEntityItems(schema, map, "minecraft:polar_bear");
		schema.register(
			map,
			"minecraft:potion",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(schema), "inTile", TypeReferences.BLOCK_NAME.in(schema)))
		);
		targetEntityItems(schema, map, "minecraft:rabbit");
		targetEntityItems(schema, map, "minecraft:sheep");
		targetEntityItems(schema, map, "minecraft:shulker");
		schema.registerSimple(map, "minecraft:shulker_bullet");
		targetEntityItems(schema, map, "minecraft:silverfish");
		targetEntityItems(schema, map, "minecraft:skeleton");
		schema.register(
			map,
			"minecraft:skeleton_horse",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)))
		);
		targetEntityItems(schema, map, "minecraft:slime");
		targetInTile(schema, map, "minecraft:small_fireball");
		targetInTile(schema, map, "minecraft:snowball");
		targetEntityItems(schema, map, "minecraft:snowman");
		schema.register(
			map,
			"minecraft:spawner_minecart",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema)))
		);
		schema.register(map, "minecraft:spectral_arrow", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema))));
		targetEntityItems(schema, map, "minecraft:spider");
		targetEntityItems(schema, map, "minecraft:squid");
		targetEntityItems(schema, map, "minecraft:stray");
		schema.registerSimple(map, "minecraft:tnt");
		schema.register(
			map, "minecraft:tnt_minecart", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema)))
		);
		schema.register(
			map,
			"minecraft:villager",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields(
					"Inventory",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"Offers",
					DSL.optionalFields("Recipes", DSL.list(TypeReferences.VILLAGER_TRADE.in(schema))),
					Schema100.targetItems(schema)
				))
		);
		targetEntityItems(schema, map, "minecraft:villager_golem");
		targetEntityItems(schema, map, "minecraft:witch");
		targetEntityItems(schema, map, "minecraft:wither");
		targetEntityItems(schema, map, "minecraft:wither_skeleton");
		targetInTile(schema, map, "minecraft:wither_skull");
		targetEntityItems(schema, map, "minecraft:wolf");
		targetInTile(schema, map, "minecraft:xp_bottle");
		schema.registerSimple(map, "minecraft:xp_orb");
		targetEntityItems(schema, map, "minecraft:zombie");
		schema.register(
			map,
			"minecraft:zombie_horse",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)))
		);
		targetEntityItems(schema, map, "minecraft:zombie_pigman");
		schema.register(
			map,
			"minecraft:zombie_villager",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Offers", DSL.optionalFields("Recipes", DSL.list(TypeReferences.VILLAGER_TRADE.in(schema))), Schema100.targetItems(schema)
				))
		);
		schema.registerSimple(map, "minecraft:evocation_fangs");
		targetEntityItems(schema, map, "minecraft:evocation_illager");
		schema.registerSimple(map, "minecraft:illusion_illager");
		schema.register(
			map,
			"minecraft:llama",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields(
					"Items",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"SaddleItem",
					TypeReferences.ITEM_STACK.in(schema),
					"DecorItem",
					TypeReferences.ITEM_STACK.in(schema),
					Schema100.targetItems(schema)
				))
		);
		schema.registerSimple(map, "minecraft:llama_spit");
		targetEntityItems(schema, map, "minecraft:vex");
		targetEntityItems(schema, map, "minecraft:vindication_illager");
		return map;
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
		schema.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", getIdentifierType(), entityTypes));
		schema.registerType(
			true,
			TypeReferences.ITEM_STACK,
			() -> DSL.hook(
					DSL.optionalFields(
						"id",
						TypeReferences.ITEM_NAME.in(schema),
						"tag",
						DSL.optionalFields(
							Pair.of("EntityTag", TypeReferences.ENTITY_TREE.in(schema)),
							Pair.of("BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema)),
							Pair.of("CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema))),
							Pair.of("CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema))),
							Pair.of("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))),
							Pair.of("ChargedProjectiles", DSL.list(TypeReferences.ITEM_STACK.in(schema)))
						)
					),
					field_5746,
					HookFunction.IDENTITY
				)
		);
	}
}
