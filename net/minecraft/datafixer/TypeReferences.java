/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DSL;

/**
 * Represents all the type references Minecraft's datafixer can fix.
 */
public class TypeReferences {
    public static final DSL.TypeReference LEVEL = () -> "level";
    /**
     * A type reference which refers to a player.
     */
    public static final DSL.TypeReference PLAYER = () -> "player";
    /**
     * A type reference which refers to a chunk.
     */
    public static final DSL.TypeReference CHUNK = () -> "chunk";
    /**
     * A type reference which refers to the saved creative hotbars.
     * 
     * <p>This type reference is only used on the client.
     */
    public static final DSL.TypeReference HOTBAR = () -> "hotbar";
    /**
     * A type reference which refers to client game options.
     */
    public static final DSL.TypeReference OPTIONS = () -> "options";
    public static final DSL.TypeReference STRUCTURE = () -> "structure";
    public static final DSL.TypeReference STATS = () -> "stats";
    public static final DSL.TypeReference SAVED_DATA = () -> "saved_data";
    public static final DSL.TypeReference ADVANCEMENTS = () -> "advancements";
    /**
     * A type reference which refers to the point of interest data in a chunk.
     */
    public static final DSL.TypeReference POI_CHUNK = () -> "poi_chunk";
    /**
     * A type reference which refers to the entity data in a chunk.
     */
    public static final DSL.TypeReference ENTITY_CHUNK = () -> "entity_chunk";
    /**
     * A type reference which refers to a block entity.
     */
    public static final DSL.TypeReference BLOCK_ENTITY = () -> "block_entity";
    /**
     * A type reference which refers to an item stack.
     */
    public static final DSL.TypeReference ITEM_STACK = () -> "item_stack";
    /**
     * A type reference which refers to a block state.
     */
    public static final DSL.TypeReference BLOCK_STATE = () -> "block_state";
    /**
     * A type reference which refers to an entity's identifier.
     */
    public static final DSL.TypeReference ENTITY_NAME = () -> "entity_name";
    /**
     * A type reference which refers to an entity tree.
     * 
     * <p>An entity tree contains the passengers of an entity and their passengers.
     */
    public static final DSL.TypeReference ENTITY_TREE = () -> "entity_tree";
    /**
     * A type reference which refers to a type of entity.
     */
    public static final DSL.TypeReference ENTITY = () -> "entity";
    /**
     * A type reference which refers to a block's identifier.
     */
    public static final DSL.TypeReference BLOCK_NAME = () -> "block_name";
    /**
     * A type reference which refers to an item's identifier.
     */
    public static final DSL.TypeReference ITEM_NAME = () -> "item_name";
    public static final DSL.TypeReference GAME_EVENT_NAME = () -> "game_event_name";
    public static final DSL.TypeReference UNTAGGED_SPAWNER = () -> "untagged_spawner";
    public static final DSL.TypeReference STRUCTURE_FEATURE = () -> "structure_feature";
    public static final DSL.TypeReference OBJECTIVE = () -> "objective";
    public static final DSL.TypeReference TEAM = () -> "team";
    public static final DSL.TypeReference RECIPE = () -> "recipe";
    /**
     * A type reference which refers to a biome.
     */
    public static final DSL.TypeReference BIOME = () -> "biome";
    /**
     * A type reference which refers to world gen settings.
     */
    public static final DSL.TypeReference WORLD_GEN_SETTINGS = () -> "world_gen_settings";
}

