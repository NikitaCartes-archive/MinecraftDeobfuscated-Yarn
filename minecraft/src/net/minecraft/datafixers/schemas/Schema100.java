package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixers.TypeReferences;

public class Schema100 extends Schema {
	public Schema100(int i, Schema schema) {
		super(i, schema);
	}

	protected static TypeTemplate method_5196(Schema schema) {
		return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
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
			map, "Enderman", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), method_5196(schema)))
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
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"ArmorItem",
					TypeReferences.ITEM_STACK.in(schema),
					"SaddleItem",
					TypeReferences.ITEM_STACK.in(schema),
					method_5196(schema)
				))
		);
		method_5195(schema, map, "Rabbit");
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
			TypeReferences.STRUCTURE,
			() -> DSL.optionalFields(
					"entities",
					DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema))),
					"blocks",
					DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema))),
					"palette",
					DSL.list(TypeReferences.BLOCK_STATE.in(schema))
				)
		);
		schema.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
	}
}
