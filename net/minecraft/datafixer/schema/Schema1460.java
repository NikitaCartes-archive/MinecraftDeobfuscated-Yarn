/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.schema;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema100;
import net.minecraft.datafixer.schema.Schema705;

public class Schema1460
extends IdentifierNormalizingSchema {
    public Schema1460(int i, Schema schema) {
        super(i, schema);
    }

    protected static void method_5232(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> Schema100.targetItems(schema));
    }

    protected static void method_5273(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        HashMap<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
        schema.registerSimple(map, "minecraft:area_effect_cloud");
        Schema1460.method_5232(schema, map, "minecraft:armor_stand");
        schema.register(map, "minecraft:arrow", (String string) -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema)));
        Schema1460.method_5232(schema, map, "minecraft:bat");
        Schema1460.method_5232(schema, map, "minecraft:blaze");
        schema.registerSimple(map, "minecraft:boat");
        Schema1460.method_5232(schema, map, "minecraft:cave_spider");
        schema.register(map, "minecraft:chest_minecart", (String string) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        Schema1460.method_5232(schema, map, "minecraft:chicken");
        schema.register(map, "minecraft:commandblock_minecart", (String string) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        Schema1460.method_5232(schema, map, "minecraft:cow");
        Schema1460.method_5232(schema, map, "minecraft:creeper");
        schema.register(map, "minecraft:donkey", (String string) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)));
        schema.registerSimple(map, "minecraft:dragon_fireball");
        schema.registerSimple(map, "minecraft:egg");
        Schema1460.method_5232(schema, map, "minecraft:elder_guardian");
        schema.registerSimple(map, "minecraft:ender_crystal");
        Schema1460.method_5232(schema, map, "minecraft:ender_dragon");
        schema.register(map, "minecraft:enderman", (String string) -> DSL.optionalFields("carriedBlockState", TypeReferences.BLOCK_STATE.in(schema), Schema100.targetItems(schema)));
        Schema1460.method_5232(schema, map, "minecraft:endermite");
        schema.registerSimple(map, "minecraft:ender_pearl");
        schema.registerSimple(map, "minecraft:evocation_fangs");
        Schema1460.method_5232(schema, map, "minecraft:evocation_illager");
        schema.registerSimple(map, "minecraft:eye_of_ender_signal");
        schema.register(map, "minecraft:falling_block", (String string) -> DSL.optionalFields("BlockState", TypeReferences.BLOCK_STATE.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema)));
        schema.registerSimple(map, "minecraft:fireball");
        schema.register(map, "minecraft:fireworks_rocket", (String string) -> DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema)));
        schema.register(map, "minecraft:furnace_minecart", (String string) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        Schema1460.method_5232(schema, map, "minecraft:ghast");
        Schema1460.method_5232(schema, map, "minecraft:giant");
        Schema1460.method_5232(schema, map, "minecraft:guardian");
        schema.register(map, "minecraft:hopper_minecart", (String string) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        schema.register(map, "minecraft:horse", (String string) -> DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)));
        Schema1460.method_5232(schema, map, "minecraft:husk");
        schema.registerSimple(map, "minecraft:illusion_illager");
        schema.register(map, "minecraft:item", (String string) -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
        schema.register(map, "minecraft:item_frame", (String string) -> DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple(map, "minecraft:leash_knot");
        schema.register(map, "minecraft:llama", (String string) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), "DecorItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)));
        schema.registerSimple(map, "minecraft:llama_spit");
        Schema1460.method_5232(schema, map, "minecraft:magma_cube");
        schema.register(map, "minecraft:minecart", (String string) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        Schema1460.method_5232(schema, map, "minecraft:mooshroom");
        schema.register(map, "minecraft:mule", (String string) -> DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)));
        Schema1460.method_5232(schema, map, "minecraft:ocelot");
        schema.registerSimple(map, "minecraft:painting");
        schema.registerSimple(map, "minecraft:parrot");
        Schema1460.method_5232(schema, map, "minecraft:pig");
        Schema1460.method_5232(schema, map, "minecraft:polar_bear");
        schema.register(map, "minecraft:potion", (String string) -> DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(schema)));
        Schema1460.method_5232(schema, map, "minecraft:rabbit");
        Schema1460.method_5232(schema, map, "minecraft:sheep");
        Schema1460.method_5232(schema, map, "minecraft:shulker");
        schema.registerSimple(map, "minecraft:shulker_bullet");
        Schema1460.method_5232(schema, map, "minecraft:silverfish");
        Schema1460.method_5232(schema, map, "minecraft:skeleton");
        schema.register(map, "minecraft:skeleton_horse", (String string) -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)));
        Schema1460.method_5232(schema, map, "minecraft:slime");
        schema.registerSimple(map, "minecraft:small_fireball");
        schema.registerSimple(map, "minecraft:snowball");
        Schema1460.method_5232(schema, map, "minecraft:snowman");
        schema.register(map, "minecraft:spawner_minecart", (String string) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema)));
        schema.register(map, "minecraft:spectral_arrow", (String string) -> DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema)));
        Schema1460.method_5232(schema, map, "minecraft:spider");
        Schema1460.method_5232(schema, map, "minecraft:squid");
        Schema1460.method_5232(schema, map, "minecraft:stray");
        schema.registerSimple(map, "minecraft:tnt");
        schema.register(map, "minecraft:tnt_minecart", (String string) -> DSL.optionalFields("DisplayState", TypeReferences.BLOCK_STATE.in(schema)));
        Schema1460.method_5232(schema, map, "minecraft:vex");
        schema.register(map, "minecraft:villager", (String string) -> DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema)))), Schema100.targetItems(schema)));
        Schema1460.method_5232(schema, map, "minecraft:villager_golem");
        Schema1460.method_5232(schema, map, "minecraft:vindication_illager");
        Schema1460.method_5232(schema, map, "minecraft:witch");
        Schema1460.method_5232(schema, map, "minecraft:wither");
        Schema1460.method_5232(schema, map, "minecraft:wither_skeleton");
        schema.registerSimple(map, "minecraft:wither_skull");
        Schema1460.method_5232(schema, map, "minecraft:wolf");
        schema.registerSimple(map, "minecraft:xp_bottle");
        schema.registerSimple(map, "minecraft:xp_orb");
        Schema1460.method_5232(schema, map, "minecraft:zombie");
        schema.register(map, "minecraft:zombie_horse", (String string) -> DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema), Schema100.targetItems(schema)));
        Schema1460.method_5232(schema, map, "minecraft:zombie_pigman");
        Schema1460.method_5232(schema, map, "minecraft:zombie_villager");
        return map;
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        HashMap<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
        Schema1460.method_5273(schema, map, "minecraft:furnace");
        Schema1460.method_5273(schema, map, "minecraft:chest");
        Schema1460.method_5273(schema, map, "minecraft:trapped_chest");
        schema.registerSimple(map, "minecraft:ender_chest");
        schema.register(map, "minecraft:jukebox", (String string) -> DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(schema)));
        Schema1460.method_5273(schema, map, "minecraft:dispenser");
        Schema1460.method_5273(schema, map, "minecraft:dropper");
        schema.registerSimple(map, "minecraft:sign");
        schema.register(map, "minecraft:mob_spawner", (String string) -> TypeReferences.UNTAGGED_SPAWNER.in(schema));
        schema.register(map, "minecraft:piston", (String string) -> DSL.optionalFields("blockState", TypeReferences.BLOCK_STATE.in(schema)));
        Schema1460.method_5273(schema, map, "minecraft:brewing_stand");
        schema.registerSimple(map, "minecraft:enchanting_table");
        schema.registerSimple(map, "minecraft:end_portal");
        schema.registerSimple(map, "minecraft:beacon");
        schema.registerSimple(map, "minecraft:skull");
        schema.registerSimple(map, "minecraft:daylight_detector");
        Schema1460.method_5273(schema, map, "minecraft:hopper");
        schema.registerSimple(map, "minecraft:comparator");
        schema.registerSimple(map, "minecraft:banner");
        schema.registerSimple(map, "minecraft:structure_block");
        schema.registerSimple(map, "minecraft:end_gateway");
        schema.registerSimple(map, "minecraft:command_block");
        Schema1460.method_5273(schema, map, "minecraft:shulker_box");
        schema.registerSimple(map, "minecraft:bed");
        return map;
    }

    @Override
    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        schema.registerType(false, TypeReferences.LEVEL, DSL::remainder);
        schema.registerType(false, TypeReferences.RECIPE, () -> DSL.constType(Schema1460.getIdentifierType()));
        schema.registerType(false, TypeReferences.PLAYER, () -> DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema)), "Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)), DSL.optionalFields("ShoulderEntityLeft", TypeReferences.ENTITY_TREE.in(schema), "ShoulderEntityRight", TypeReferences.ENTITY_TREE.in(schema), "recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(schema)), "toBeDisplayed", DSL.list(TypeReferences.RECIPE.in(schema))))));
        schema.registerType(false, TypeReferences.CHUNK, () -> DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(schema)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema))), "Sections", DSL.list(DSL.optionalFields("Palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema)))))));
        schema.registerType(true, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy("id", Schema1460.getIdentifierType(), blockEntityTypes));
        schema.registerType(true, TypeReferences.ENTITY_TREE, () -> DSL.optionalFields("Passengers", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), TypeReferences.ENTITY.in(schema)));
        schema.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy("id", Schema1460.getIdentifierType(), entityTypes));
        schema.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(schema), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(schema), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema)))), Schema705.field_5746, Hook.HookFunction.IDENTITY));
        schema.registerType(false, TypeReferences.HOTBAR, () -> DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(schema))));
        schema.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
        schema.registerType(false, TypeReferences.STRUCTURE, () -> DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema))));
        schema.registerType(false, TypeReferences.BLOCK_NAME, () -> DSL.constType(Schema1460.getIdentifierType()));
        schema.registerType(false, TypeReferences.ITEM_NAME, () -> DSL.constType(Schema1460.getIdentifierType()));
        schema.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
        Supplier<TypeTemplate> supplier = () -> DSL.compoundList(TypeReferences.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
        schema.registerType(false, TypeReferences.STATS, () -> DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(TypeReferences.BLOCK_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate)supplier.get(), "minecraft:used", (TypeTemplate)supplier.get(), "minecraft:broken", (TypeTemplate)supplier.get(), "minecraft:picked_up", (TypeTemplate)supplier.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate)supplier.get(), "minecraft:killed", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(Schema1460.getIdentifierType()), DSL.constType(DSL.intType()))))));
        schema.registerType(false, TypeReferences.SAVED_DATA, () -> DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema)), "Objectives", DSL.list(TypeReferences.OBJECTIVE.in(schema)), "Teams", DSL.list(TypeReferences.TEAM.in(schema)))));
        schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, () -> DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", TypeReferences.BLOCK_STATE.in(schema), "CB", TypeReferences.BLOCK_STATE.in(schema), "CC", TypeReferences.BLOCK_STATE.in(schema), "CD", TypeReferences.BLOCK_STATE.in(schema)))));
        schema.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
        schema.registerType(false, TypeReferences.TEAM, DSL::remainder);
        schema.registerType(true, TypeReferences.UNTAGGED_SPAWNER, () -> DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", TypeReferences.ENTITY_TREE.in(schema))), "SpawnData", TypeReferences.ENTITY_TREE.in(schema)));
        schema.registerType(false, TypeReferences.ADVANCEMENTS, () -> DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.BIOME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(TypeReferences.ENTITY_NAME.in(schema), DSL.constType(DSL.string())))));
        schema.registerType(false, TypeReferences.BIOME, () -> DSL.constType(Schema1460.getIdentifierType()));
        schema.registerType(false, TypeReferences.ENTITY_NAME, () -> DSL.constType(Schema1460.getIdentifierType()));
        schema.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);
        schema.registerType(true, TypeReferences.CHUNK_GENERATOR_SETTINGS, DSL::remainder);
    }
}

