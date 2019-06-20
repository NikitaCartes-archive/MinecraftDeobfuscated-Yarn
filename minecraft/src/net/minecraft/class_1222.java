package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class class_1222 extends Schema {
	public class_1222(int i, Schema schema) {
		super(i, schema);
	}

	protected static TypeTemplate method_5196(Schema schema) {
		return DSL.optionalFields("ArmorItems", DSL.list(class_1208.field_5712.in(schema)), "HandItems", DSL.list(class_1208.field_5712.in(schema)));
	}

	protected static void method_5195(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> method_5196(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_5195(schema, map, "ArmorStand");
		method_5195(schema, map, "Creeper");
		method_5195(schema, map, "Skeleton");
		method_5195(schema, map, "Spider");
		method_5195(schema, map, "Giant");
		method_5195(schema, map, "Zombie");
		method_5195(schema, map, "Slime");
		method_5195(schema, map, "Ghast");
		method_5195(schema, map, "PigZombie");
		schema.register(
			map, "Enderman", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("carried", class_1208.field_5731.in(schema), method_5196(schema)))
		);
		method_5195(schema, map, "CaveSpider");
		method_5195(schema, map, "Silverfish");
		method_5195(schema, map, "Blaze");
		method_5195(schema, map, "LavaSlime");
		method_5195(schema, map, "EnderDragon");
		method_5195(schema, map, "WitherBoss");
		method_5195(schema, map, "Bat");
		method_5195(schema, map, "Witch");
		method_5195(schema, map, "Endermite");
		method_5195(schema, map, "Guardian");
		method_5195(schema, map, "Pig");
		method_5195(schema, map, "Sheep");
		method_5195(schema, map, "Cow");
		method_5195(schema, map, "Chicken");
		method_5195(schema, map, "Squid");
		method_5195(schema, map, "Wolf");
		method_5195(schema, map, "MushroomCow");
		method_5195(schema, map, "SnowMan");
		method_5195(schema, map, "Ozelot");
		method_5195(schema, map, "VillagerGolem");
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
					method_5196(schema)
				))
		);
		method_5195(schema, map, "Rabbit");
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
					method_5196(schema)
				))
		);
		method_5195(schema, map, "Shulker");
		schema.registerSimple(map, "AreaEffectCloud");
		schema.registerSimple(map, "ShulkerBullet");
		return map;
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
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
		schema.registerType(false, class_1208.field_5720, DSL::remainder);
	}
}
