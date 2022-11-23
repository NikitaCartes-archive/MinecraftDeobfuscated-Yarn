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

	protected static void targetEntityItems(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {
		schema.register(map, entityId, (Supplier<TypeTemplate>)(() -> targetItems(schema)));
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		targetEntityItems(schema, map, "ArmorStand");
		targetEntityItems(schema, map, "Creeper");
		targetEntityItems(schema, map, "Skeleton");
		targetEntityItems(schema, map, "Spider");
		targetEntityItems(schema, map, "Giant");
		targetEntityItems(schema, map, "Zombie");
		targetEntityItems(schema, map, "Slime");
		targetEntityItems(schema, map, "Ghast");
		targetEntityItems(schema, map, "PigZombie");
		schema.register(
			map, "Enderman", (Function<String, TypeTemplate>)(name -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), targetItems(schema)))
		);
		targetEntityItems(schema, map, "CaveSpider");
		targetEntityItems(schema, map, "Silverfish");
		targetEntityItems(schema, map, "Blaze");
		targetEntityItems(schema, map, "LavaSlime");
		targetEntityItems(schema, map, "EnderDragon");
		targetEntityItems(schema, map, "WitherBoss");
		targetEntityItems(schema, map, "Bat");
		targetEntityItems(schema, map, "Witch");
		targetEntityItems(schema, map, "Endermite");
		targetEntityItems(schema, map, "Guardian");
		targetEntityItems(schema, map, "Pig");
		targetEntityItems(schema, map, "Sheep");
		targetEntityItems(schema, map, "Cow");
		targetEntityItems(schema, map, "Chicken");
		targetEntityItems(schema, map, "Squid");
		targetEntityItems(schema, map, "Wolf");
		targetEntityItems(schema, map, "MushroomCow");
		targetEntityItems(schema, map, "SnowMan");
		targetEntityItems(schema, map, "Ozelot");
		targetEntityItems(schema, map, "VillagerGolem");
		schema.register(
			map,
			"EntityHorse",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields(
					"Items",
					DSL.list(TypeReferences.ITEM_STACK.in(schema)),
					"ArmorItem",
					TypeReferences.ITEM_STACK.in(schema),
					"SaddleItem",
					TypeReferences.ITEM_STACK.in(schema),
					targetItems(schema)
				))
		);
		targetEntityItems(schema, map, "Rabbit");
		schema.register(
			map,
			"Villager",
			(Function<String, TypeTemplate>)(name -> DSL.optionalFields(
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
		targetEntityItems(schema, map, "Shulker");
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
