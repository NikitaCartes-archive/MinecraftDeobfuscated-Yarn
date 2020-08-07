package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema100 extends Schema {
	public Schema100(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	protected static TypeTemplate targetItems(Schema schema) {
		return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
	}

	protected static void targetEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, (Supplier<TypeTemplate>)(() -> targetItems(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		targetEntity(schema, map, "ArmorStand");
		targetEntity(schema, map, "Creeper");
		targetEntity(schema, map, "Skeleton");
		targetEntity(schema, map, "Spider");
		targetEntity(schema, map, "Giant");
		targetEntity(schema, map, "Zombie");
		targetEntity(schema, map, "Slime");
		targetEntity(schema, map, "Ghast");
		targetEntity(schema, map, "PigZombie");
		schema.register(
			map, "Enderman", (Function<String, TypeTemplate>)(string -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), targetItems(schema)))
		);
		targetEntity(schema, map, "CaveSpider");
		targetEntity(schema, map, "Silverfish");
		targetEntity(schema, map, "Blaze");
		targetEntity(schema, map, "LavaSlime");
		targetEntity(schema, map, "EnderDragon");
		targetEntity(schema, map, "WitherBoss");
		targetEntity(schema, map, "Bat");
		targetEntity(schema, map, "Witch");
		targetEntity(schema, map, "Endermite");
		targetEntity(schema, map, "Guardian");
		targetEntity(schema, map, "Pig");
		targetEntity(schema, map, "Sheep");
		targetEntity(schema, map, "Cow");
		targetEntity(schema, map, "Chicken");
		targetEntity(schema, map, "Squid");
		targetEntity(schema, map, "Wolf");
		targetEntity(schema, map, "MushroomCow");
		targetEntity(schema, map, "SnowMan");
		targetEntity(schema, map, "Ozelot");
		targetEntity(schema, map, "VillagerGolem");
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
					targetItems(schema)
				))
		);
		targetEntity(schema, map, "Rabbit");
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
					targetItems(schema)
				))
		);
		targetEntity(schema, map, "Shulker");
		schema.registerSimple(map, "AreaEffectCloud");
		schema.registerSimple(map, "ShulkerBullet");
		return map;
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
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
