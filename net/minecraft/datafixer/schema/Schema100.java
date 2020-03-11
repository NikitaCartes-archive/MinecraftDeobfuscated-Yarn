/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema100
extends Schema {
    public Schema100(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static TypeTemplate targetItems(Schema schema) {
        return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
    }

    protected static void targetEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> Schema100.targetItems(schema));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
        Schema100.targetEntity(schema, map, "ArmorStand");
        Schema100.targetEntity(schema, map, "Creeper");
        Schema100.targetEntity(schema, map, "Skeleton");
        Schema100.targetEntity(schema, map, "Spider");
        Schema100.targetEntity(schema, map, "Giant");
        Schema100.targetEntity(schema, map, "Zombie");
        Schema100.targetEntity(schema, map, "Slime");
        Schema100.targetEntity(schema, map, "Ghast");
        Schema100.targetEntity(schema, map, "PigZombie");
        schema.register(map, "Enderman", (String string) -> DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema), Schema100.targetItems(schema)));
        Schema100.targetEntity(schema, map, "CaveSpider");
        Schema100.targetEntity(schema, map, "Silverfish");
        Schema100.targetEntity(schema, map, "Blaze");
        Schema100.targetEntity(schema, map, "LavaSlime");
        Schema100.targetEntity(schema, map, "EnderDragon");
        Schema100.targetEntity(schema, map, "WitherBoss");
        Schema100.targetEntity(schema, map, "Bat");
        Schema100.targetEntity(schema, map, "Witch");
        Schema100.targetEntity(schema, map, "Endermite");
        Schema100.targetEntity(schema, map, "Guardian");
        Schema100.targetEntity(schema, map, "Pig");
        Schema100.targetEntity(schema, map, "Sheep");
        Schema100.targetEntity(schema, map, "Cow");
        Schema100.targetEntity(schema, map, "Chicken");
        Schema100.targetEntity(schema, map, "Squid");
        Schema100.targetEntity(schema, map, "Wolf");
        Schema100.targetEntity(schema, map, "MushroomCow");
        Schema100.targetEntity(schema, map, "SnowMan");
        Schema100.targetEntity(schema, map, "Ozelot");
        Schema100.targetEntity(schema, map, "VillagerGolem");
        schema.register(map, "EntityHorse", (String string) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)));
        Schema100.targetEntity(schema, map, "Rabbit");
        schema.register(map, "Villager", (String string) -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), Schema100.targetItems(schema)));
        Schema100.targetEntity(schema, map, "Shulker");
        schema.registerSimple(map, "AreaEffectCloud");
        schema.registerSimple(map, "ShulkerBullet");
        return map;
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        super.registerTypes(schema, entityTypes, blockEntityTypes);
        schema.registerType(false, TypeReferences.STRUCTURE, () -> DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema))));
        schema.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
    }
}

