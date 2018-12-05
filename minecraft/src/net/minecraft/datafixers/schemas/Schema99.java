package net.minecraft.datafixers.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixers.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Schema99 extends Schema {
	private static final Logger field_5749 = LogManager.getLogger();
	private static final Map<String, String> field_5748 = DataFixUtils.make(Maps.<String, String>newHashMap(), hashMap -> {
		hashMap.put("minecraft:furnace", "Furnace");
		hashMap.put("minecraft:lit_furnace", "Furnace");
		hashMap.put("minecraft:chest", "Chest");
		hashMap.put("minecraft:trapped_chest", "Chest");
		hashMap.put("minecraft:ender_chest", "EnderChest");
		hashMap.put("minecraft:jukebox", "RecordPlayer");
		hashMap.put("minecraft:dispenser", "Trap");
		hashMap.put("minecraft:dropper", "Dropper");
		hashMap.put("minecraft:sign", "Sign");
		hashMap.put("minecraft:mob_spawner", "MobSpawner");
		hashMap.put("minecraft:noteblock", "Music");
		hashMap.put("minecraft:brewing_stand", "Cauldron");
		hashMap.put("minecraft:enhanting_table", "EnchantTable");
		hashMap.put("minecraft:command_block", "CommandBlock");
		hashMap.put("minecraft:beacon", "Beacon");
		hashMap.put("minecraft:skull", "Skull");
		hashMap.put("minecraft:daylight_detector", "DLDetector");
		hashMap.put("minecraft:hopper", "Hopper");
		hashMap.put("minecraft:banner", "Banner");
		hashMap.put("minecraft:flower_pot", "FlowerPot");
		hashMap.put("minecraft:repeating_command_block", "CommandBlock");
		hashMap.put("minecraft:chain_command_block", "CommandBlock");
		hashMap.put("minecraft:standing_sign", "Sign");
		hashMap.put("minecraft:wall_sign", "Sign");
		hashMap.put("minecraft:piston_head", "Piston");
		hashMap.put("minecraft:daylight_detector_inverted", "DLDetector");
		hashMap.put("minecraft:unpowered_comparator", "Comparator");
		hashMap.put("minecraft:powered_comparator", "Comparator");
		hashMap.put("minecraft:wall_banner", "Banner");
		hashMap.put("minecraft:standing_banner", "Banner");
		hashMap.put("minecraft:structure_block", "Structure");
		hashMap.put("minecraft:end_portal", "Airportal");
		hashMap.put("minecraft:end_gateway", "EndGateway");
		hashMap.put("minecraft:shield", "Banner");
	});
	protected static final HookFunction field_5747 = new HookFunction() {
		@Override
		public <T> T apply(DynamicOps<T> dynamicOps, T object) {
			return Schema99.method_5359(new Dynamic<>(dynamicOps, object), Schema99.field_5748, "ArmorStand");
		}
	};

	public Schema99(int i, Schema schema) {
		super(i, schema);
	}

	protected static TypeTemplate method_5353(Schema schema) {
		return DSL.optionalFields("Equipment", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
	}

	protected static void method_5339(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> method_5353(schema)));
	}

	protected static void method_5368(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema))));
	}

	protected static void method_5377(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema))));
	}

	protected static void method_5346(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)))));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.<String, Supplier<TypeTemplate>>newHashMap();
		schema.register(map, "Item", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema))));
		schema.registerSimple(map, "XPOrb");
		method_5368(schema, map, "ThrownEgg");
		schema.registerSimple(map, "LeashKnot");
		schema.registerSimple(map, "Painting");
		schema.register(map, "Arrow", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema))));
		schema.register(map, "TippedArrow", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema))));
		schema.register(map, "SpectralArrow", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema))));
		method_5368(schema, map, "Snowball");
		method_5368(schema, map, "Fireball");
		method_5368(schema, map, "SmallFireball");
		method_5368(schema, map, "ThrownEnderpearl");
		schema.registerSimple(map, "EyeOfEnderSignal");
		schema.register(
			map,
			"ThrownPotion",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema), "Potion", TypeReferences.ITEM_STACK.in(schema)))
		);
		method_5368(schema, map, "ThrownExpBottle");
		schema.register(map, "ItemFrame", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema))));
		method_5368(schema, map, "WitherSkull");
		schema.registerSimple(map, "PrimedTnt");
		schema.register(
			map,
			"FallingSand",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Block", TypeReferences.BLOCK_NAME.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema)
				))
		);
		schema.register(
			map, "FireworksRocketEntity", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema)))
		);
		schema.registerSimple(map, "Boat");
		schema.register(
			map,
			"Minecart",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields(
					"DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))
				))
		);
		method_5377(schema, map, "MinecartRideable");
		schema.register(
			map,
			"MinecartChest",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))
				))
		);
		method_5377(schema, map, "MinecartFurnace");
		method_5377(schema, map, "MinecartTNT");
		schema.register(
			map,
			"MinecartSpawner",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema)))
		);
		schema.register(
			map,
			"MinecartHopper",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))
				))
		);
		method_5377(schema, map, "MinecartCommandBlock");
		method_5339(schema, map, "ArmorStand");
		method_5339(schema, map, "Creeper");
		method_5339(schema, map, "Skeleton");
		method_5339(schema, map, "Spider");
		method_5339(schema, map, "Giant");
		method_5339(schema, map, "Zombie");
		method_5339(schema, map, "Slime");
		method_5339(schema, map, "Ghast");
		method_5339(schema, map, "PigZombie");
		schema.register(
			map, "Enderman", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), method_5353(schema)))
		);
		method_5339(schema, map, "CaveSpider");
		method_5339(schema, map, "Silverfish");
		method_5339(schema, map, "Blaze");
		method_5339(schema, map, "LavaSlime");
		method_5339(schema, map, "EnderDragon");
		method_5339(schema, map, "WitherBoss");
		method_5339(schema, map, "Bat");
		method_5339(schema, map, "Witch");
		method_5339(schema, map, "Endermite");
		method_5339(schema, map, "Guardian");
		method_5339(schema, map, "Pig");
		method_5339(schema, map, "Sheep");
		method_5339(schema, map, "Cow");
		method_5339(schema, map, "Chicken");
		method_5339(schema, map, "Squid");
		method_5339(schema, map, "Wolf");
		method_5339(schema, map, "MushroomCow");
		method_5339(schema, map, "SnowMan");
		method_5339(schema, map, "Ozelot");
		method_5339(schema, map, "VillagerGolem");
		schema.register(
			map,
			"EntityHorse",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Items",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"ArmorItem",
					TypeReferences.ITEM_STACK.in(schema),
					"SaddleItem",
					TypeReferences.ITEM_STACK.in(schema),
					method_5353(schema)
				))
		);
		method_5339(schema, map, "Rabbit");
		schema.register(
			map,
			"Villager",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Inventory",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"Offers",
					DSL.optionalFields(
						"Recipes",
						DSL.list(
							DSL.optionalFields(
								"buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)
							)
						)
					),
					method_5353(schema)
				))
		);
		schema.registerSimple(map, "EnderCrystal");
		schema.registerSimple(map, "AreaEffectCloud");
		schema.registerSimple(map, "ShulkerBullet");
		method_5339(schema, map, "Shulker");
		return map;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.<String, Supplier<TypeTemplate>>newHashMap();
		method_5346(schema, map, "Furnace");
		method_5346(schema, map, "Chest");
		schema.registerSimple(map, "EnderChest");
		schema.register(map, "RecordPlayer", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(schema))));
		method_5346(schema, map, "Trap");
		method_5346(schema, map, "Dropper");
		schema.registerSimple(map, "Sign");
		schema.register(map, "MobSpawner", (Function<String, TypeTemplate>)(string -> TypeReferences.UNTAGGED_SPAWNER.in(schema)));
		schema.registerSimple(map, "Music");
		schema.registerSimple(map, "Piston");
		method_5346(schema, map, "Cauldron");
		schema.registerSimple(map, "EnchantTable");
		schema.registerSimple(map, "Airportal");
		schema.registerSimple(map, "Control");
		schema.registerSimple(map, "Beacon");
		schema.registerSimple(map, "Skull");
		schema.registerSimple(map, "DLDetector");
		method_5346(schema, map, "Hopper");
		schema.registerSimple(map, "Comparator");
		schema.register(
			map,
			"FlowerPot",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema))))
		);
		schema.registerSimple(map, "Banner");
		schema.registerSimple(map, "Structure");
		schema.registerSimple(map, "EndGateway");
		return map;
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		schema.registerType(false, TypeReferences.LEVEL, DSL::remainder);
		schema.registerType(
			false,
			TypeReferences.PLAYER,
			() -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)))
		);
		schema.registerType(
			false,
			TypeReferences.CHUNK,
			() -> DSL.fields(
					"Level",
					DSL.optionalFields(
						"Entities",
						DSL.list(TypeReferences.ENTITY_TREE.in(schema)),
						"TileEntities",
						DSL.list(TypeReferences.BLOCK_ENTITY.in(schema)),
						"TileTicks",
						DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema)))
					)
				)
		);
		schema.registerType(true, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.string(), map2));
		schema.registerType(
			true, TypeReferences.ENTITY_TREE, () -> DSL.optionalFields("Riding", TypeReferences.ENTITY_TREE.in(schema), TypeReferences.ENTITY.in(schema))
		);
		schema.registerType(false, TypeReferences.ENTITY_NAME, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", DSL.string(), map));
		schema.registerType(
			true,
			TypeReferences.ITEM_STACK,
			() -> DSL.hook(
					DSL.optionalFields(
						"id",
						DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema)),
						"tag",
						DSL.optionalFields(
							"EntityTag",
							TypeReferences.ENTITY_TREE.in(schema),
							"BlockEntityTag",
							TypeReferences.BLOCK_ENTITY.in(schema),
							"CanDestroy",
							DSL.list(TypeReferences.BLOCK_NAME.in(schema)),
							"CanPlaceOn",
							DSL.list(TypeReferences.BLOCK_NAME.in(schema))
						)
					),
					field_5747,
					HookFunction.IDENTITY
				)
		);
		schema.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
		schema.registerType(false, TypeReferences.BLOCK_NAME, () -> DSL.or(DSL.constType(DSL.intType()), DSL.constType(DSL.namespacedString())));
		schema.registerType(false, TypeReferences.ITEM_NAME, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, TypeReferences.STATS, DSL::remainder);
		schema.registerType(
			false,
			TypeReferences.SAVED_DATA,
			() -> DSL.optionalFields(
					"data",
					DSL.optionalFields(
						"Features",
						DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema)),
						"Objectives",
						DSL.list(TypeReferences.OBJECTIVE.in(schema)),
						"Teams",
						DSL.list(TypeReferences.TEAM.in(schema))
					)
				)
		);
		schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, DSL::remainder);
		schema.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
		schema.registerType(false, TypeReferences.TEAM, DSL::remainder);
		schema.registerType(true, TypeReferences.UNTAGGED_SPAWNER, DSL::remainder);
	}

	protected static <T> T method_5359(Dynamic<T> dynamic, Map<String, String> map, String string) {
		return dynamic.update(
				"tag",
				dynamic2 -> dynamic2.update("BlockEntityTag", dynamic2x -> {
							String stringxx = dynamic.getString("id");
							String string2 = (String)map.get(SchemaIdentifierNormalize.normalize(stringxx));
							if (string2 == null) {
								field_5749.warn("Unable to resolve BlockEntity for ItemStack: {}", stringxx);
								return dynamic2x;
							} else {
								return dynamic2x.set("id", dynamic.createString(string2));
							}
						})
						.update(
							"EntityTag",
							dynamic2x -> {
								String string2 = dynamic.getString("id");
								return Objects.equals(SchemaIdentifierNormalize.normalize(string2), "minecraft:armor_stand")
									? dynamic2x.set("id", dynamic.createString(string))
									: dynamic2x;
							}
						)
			)
			.getValue();
	}
}
