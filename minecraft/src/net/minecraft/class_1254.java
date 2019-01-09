package net.minecraft;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1254 extends Schema {
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
			return class_1254.method_5359(new Dynamic<>(dynamicOps, object), class_1254.field_5748, "ArmorStand");
		}
	};

	public class_1254(int i, Schema schema) {
		super(i, schema);
	}

	protected static TypeTemplate method_5353(Schema schema) {
		return DSL.optionalFields("Equipment", DSL.list(class_1208.field_5712.in(schema)));
	}

	protected static void method_5339(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> method_5353(schema)));
	}

	protected static void method_5368(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("inTile", class_1208.field_5731.in(schema))));
	}

	protected static void method_5377(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayTile", class_1208.field_5731.in(schema))));
	}

	protected static void method_5346(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> DSL.optionalFields("Items", DSL.list(class_1208.field_5712.in(schema)))));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = Maps.<String, Supplier<TypeTemplate>>newHashMap();
		schema.register(map, "Item", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Item", class_1208.field_5712.in(schema))));
		schema.registerSimple(map, "XPOrb");
		method_5368(schema, map, "ThrownEgg");
		schema.registerSimple(map, "LeashKnot");
		schema.registerSimple(map, "Painting");
		schema.register(map, "Arrow", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("inTile", class_1208.field_5731.in(schema))));
		schema.register(map, "TippedArrow", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("inTile", class_1208.field_5731.in(schema))));
		schema.register(map, "SpectralArrow", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("inTile", class_1208.field_5731.in(schema))));
		method_5368(schema, map, "Snowball");
		method_5368(schema, map, "Fireball");
		method_5368(schema, map, "SmallFireball");
		method_5368(schema, map, "ThrownEnderpearl");
		schema.registerSimple(map, "EyeOfEnderSignal");
		schema.register(
			map,
			"ThrownPotion",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("inTile", class_1208.field_5731.in(schema), "Potion", class_1208.field_5712.in(schema)))
		);
		method_5368(schema, map, "ThrownExpBottle");
		schema.register(map, "ItemFrame", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("Item", class_1208.field_5712.in(schema))));
		method_5368(schema, map, "WitherSkull");
		schema.registerSimple(map, "PrimedTnt");
		schema.register(
			map,
			"FallingSand",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("Block", class_1208.field_5731.in(schema), "TileEntityData", class_1208.field_5727.in(schema)))
		);
		schema.register(
			map, "FireworksRocketEntity", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("FireworksItem", class_1208.field_5712.in(schema)))
		);
		schema.registerSimple(map, "Boat");
		schema.register(
			map,
			"Minecart",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayTile", class_1208.field_5731.in(schema), "Items", DSL.list(class_1208.field_5712.in(schema))))
		);
		method_5377(schema, map, "MinecartRideable");
		schema.register(
			map,
			"MinecartChest",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"DisplayTile", class_1208.field_5731.in(schema), "Items", DSL.list(class_1208.field_5712.in(schema))
				))
		);
		method_5377(schema, map, "MinecartFurnace");
		method_5377(schema, map, "MinecartTNT");
		schema.register(
			map,
			"MinecartSpawner",
			(Supplier<TypeTemplate>)(() -> DSL.optionalFields("DisplayTile", class_1208.field_5731.in(schema), class_1208.field_5718.in(schema)))
		);
		schema.register(
			map,
			"MinecartHopper",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"DisplayTile", class_1208.field_5731.in(schema), "Items", DSL.list(class_1208.field_5712.in(schema))
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
			map, "Enderman", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("carried", class_1208.field_5731.in(schema), method_5353(schema)))
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
					DSL.list(class_1208.field_5712.in(schema)),
					"ArmorItem",
					class_1208.field_5712.in(schema),
					"SaddleItem",
					class_1208.field_5712.in(schema),
					method_5353(schema)
				))
		);
		method_5339(schema, map, "Rabbit");
		schema.register(
			map,
			"Villager",
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields(
					"Inventory",
					DSL.list(class_1208.field_5712.in(schema)),
					"Offers",
					DSL.optionalFields(
						"Recipes",
						DSL.list(DSL.optionalFields("buy", class_1208.field_5712.in(schema), "buyB", class_1208.field_5712.in(schema), "sell", class_1208.field_5712.in(schema)))
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
		schema.register(map, "RecordPlayer", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("RecordItem", class_1208.field_5712.in(schema))));
		method_5346(schema, map, "Trap");
		method_5346(schema, map, "Dropper");
		schema.registerSimple(map, "Sign");
		schema.register(map, "MobSpawner", (Function<String, TypeTemplate>)(string -> class_1208.field_5718.in(schema)));
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
			(Function<String, TypeTemplate>)(string -> DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), class_1208.field_5713.in(schema))))
		);
		schema.registerSimple(map, "Banner");
		schema.registerSimple(map, "Structure");
		schema.registerSimple(map, "EndGateway");
		return map;
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		schema.registerType(false, class_1208.field_5710, DSL::remainder);
		schema.registerType(
			false,
			class_1208.field_5715,
			() -> DSL.optionalFields("Inventory", DSL.list(class_1208.field_5712.in(schema)), "EnderItems", DSL.list(class_1208.field_5712.in(schema)))
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
						DSL.list(DSL.fields("i", class_1208.field_5731.in(schema)))
					)
				)
		);
		schema.registerType(true, class_1208.field_5727, () -> DSL.taggedChoiceLazy("id", DSL.string(), map2));
		schema.registerType(true, class_1208.field_5723, () -> DSL.optionalFields("Riding", class_1208.field_5723.in(schema), class_1208.field_5729.in(schema)));
		schema.registerType(false, class_1208.field_5719, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(true, class_1208.field_5729, () -> DSL.taggedChoiceLazy("id", DSL.string(), map));
		schema.registerType(
			true,
			class_1208.field_5712,
			() -> DSL.hook(
					DSL.optionalFields(
						"id",
						DSL.or(DSL.constType(DSL.intType()), class_1208.field_5713.in(schema)),
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
					field_5747,
					HookFunction.IDENTITY
				)
		);
		schema.registerType(false, class_1208.field_5717, DSL::remainder);
		schema.registerType(false, class_1208.field_5731, () -> DSL.or(DSL.constType(DSL.intType()), DSL.constType(DSL.namespacedString())));
		schema.registerType(false, class_1208.field_5713, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, class_1208.field_5730, DSL::remainder);
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
		schema.registerType(false, class_1208.field_5724, DSL::remainder);
		schema.registerType(false, class_1208.field_5721, DSL::remainder);
		schema.registerType(false, class_1208.field_5714, DSL::remainder);
		schema.registerType(true, class_1208.field_5718, DSL::remainder);
	}

	protected static <T> T method_5359(Dynamic<T> dynamic, Map<String, String> map, String string) {
		return dynamic.update("tag", dynamic2 -> dynamic2.update("BlockEntityTag", dynamic2x -> {
				String stringxx = dynamic.get("id").asString("");
				String string2 = (String)map.get(class_1220.method_5193(stringxx));
				if (string2 == null) {
					field_5749.warn("Unable to resolve BlockEntity for ItemStack: {}", stringxx);
					return dynamic2x;
				} else {
					return dynamic2x.set("id", dynamic.createString(string2));
				}
			}).update("EntityTag", dynamic2x -> {
				String string2 = dynamic.get("id").asString("");
				return Objects.equals(class_1220.method_5193(string2), "minecraft:armor_stand") ? dynamic2x.set("id", dynamic.createString(string)) : dynamic2x;
			})).getValue();
	}
}
