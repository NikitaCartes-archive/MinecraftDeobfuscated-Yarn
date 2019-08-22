/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixers.TypeReferences;

public class Schema100
extends Schema {
    public Schema100(int i, Schema schema) {
        super(i, schema);
    }

    protected static TypeTemplate method_5196(Schema schema) {
        return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
    }

    protected static void method_5195(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> Schema100.method_5196(schema));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
        Schema100.method_5195(schema, map, "ArmorStand");
        Schema100.method_5195(schema, map, "Creeper");
        Schema100.method_5195(schema, map, "Skeleton");
        Schema100.method_5195(schema, map, "Spider");
        Schema100.method_5195(schema, map, "Giant");
        Schema100.method_5195(schema, map, "Zombie");
        Schema100.method_5195(schema, map, "Slime");
        Schema100.method_5195(schema, map, "Ghast");
        Schema100.method_5195(schema, map, "PigZombie");
        schema.register(map, "Enderman", (String string) -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), Schema100.method_5196(schema)));
        Schema100.method_5195(schema, map, "CaveSpider");
        Schema100.method_5195(schema, map, "Silverfish");
        Schema100.method_5195(schema, map, "Blaze");
        Schema100.method_5195(schema, map, "LavaSlime");
        Schema100.method_5195(schema, map, "EnderDragon");
        Schema100.method_5195(schema, map, "WitherBoss");
        Schema100.method_5195(schema, map, "Bat");
        Schema100.method_5195(schema, map, "Witch");
        Schema100.method_5195(schema, map, "Endermite");
        Schema100.method_5195(schema, map, "Guardian");
        Schema100.method_5195(schema, map, "Pig");
        Schema100.method_5195(schema, map, "Sheep");
        Schema100.method_5195(schema, map, "Cow");
        Schema100.method_5195(schema, map, "Chicken");
        Schema100.method_5195(schema, map, "Squid");
        Schema100.method_5195(schema, map, "Wolf");
        Schema100.method_5195(schema, map, "MushroomCow");
        Schema100.method_5195(schema, map, "SnowMan");
        Schema100.method_5195(schema, map, "Ozelot");
        Schema100.method_5195(schema, map, "VillagerGolem");
        schema.register(map, "EntityHorse", (String string) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.method_5196(schema)));
        Schema100.method_5195(schema, map, "Rabbit");
        schema.register(map, "Villager", (String string) -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), Schema100.method_5196(schema)));
        Schema100.method_5195(schema, map, "Shulker");
        schema.registerSimple(map, "AreaEffectCloud");
        schema.registerSimple(map, "ShulkerBullet");
        return map;
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        super.registerTypes(schema, map, map2);
        schema.registerType(false, TypeReferences.STRUCTURE, () -> DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema))));
        schema.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
    }
}

