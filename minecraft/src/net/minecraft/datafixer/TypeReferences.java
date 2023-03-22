package net.minecraft.datafixer;

import com.mojang.datafixers.DSL.TypeReference;

/**
 * Represents all the type references Minecraft's datafixer can fix.
 */
public class TypeReferences {
	public static final TypeReference LEVEL = () -> "level";
	/**
	 * A type reference which refers to a player.
	 */
	public static final TypeReference PLAYER = () -> "player";
	/**
	 * A type reference which refers to a chunk.
	 */
	public static final TypeReference CHUNK = () -> "chunk";
	/**
	 * A type reference which refers to the saved creative hotbars.
	 * 
	 * <p>This type reference is only used on the client.
	 */
	public static final TypeReference HOTBAR = () -> "hotbar";
	/**
	 * A type reference which refers to client game options.
	 */
	public static final TypeReference OPTIONS = () -> "options";
	public static final TypeReference STRUCTURE = () -> "structure";
	public static final TypeReference STATS = () -> "stats";
	public static final TypeReference SAVED_DATA = () -> "saved_data";
	public static final TypeReference ADVANCEMENTS = () -> "advancements";
	/**
	 * A type reference which refers to the point of interest data in a chunk.
	 */
	public static final TypeReference POI_CHUNK = () -> "poi_chunk";
	/**
	 * A type reference which refers to the entity data in a chunk.
	 */
	public static final TypeReference ENTITY_CHUNK = () -> "entity_chunk";
	/**
	 * A type reference which refers to a block entity.
	 */
	public static final TypeReference BLOCK_ENTITY = () -> "block_entity";
	/**
	 * A type reference which refers to an item stack.
	 */
	public static final TypeReference ITEM_STACK = () -> "item_stack";
	/**
	 * A type reference which refers to a block state.
	 */
	public static final TypeReference BLOCK_STATE = () -> "block_state";
	/**
	 * A type reference which refers to an entity's identifier.
	 */
	public static final TypeReference ENTITY_NAME = () -> "entity_name";
	/**
	 * A type reference which refers to an entity tree.
	 * 
	 * <p>An entity tree contains the passengers of an entity and their passengers.
	 */
	public static final TypeReference ENTITY_TREE = () -> "entity_tree";
	/**
	 * A type reference which refers to a type of entity.
	 */
	public static final TypeReference ENTITY = () -> "entity";
	/**
	 * A type reference which refers to a block's identifier.
	 */
	public static final TypeReference BLOCK_NAME = () -> "block_name";
	/**
	 * A type reference which refers to an item's identifier.
	 */
	public static final TypeReference ITEM_NAME = () -> "item_name";
	public static final TypeReference GAME_EVENT_NAME = () -> "game_event_name";
	public static final TypeReference UNTAGGED_SPAWNER = () -> "untagged_spawner";
	public static final TypeReference STRUCTURE_FEATURE = () -> "structure_feature";
	public static final TypeReference OBJECTIVE = () -> "objective";
	public static final TypeReference TEAM = () -> "team";
	public static final TypeReference RECIPE = () -> "recipe";
	/**
	 * A type reference which refers to a biome.
	 */
	public static final TypeReference BIOME = () -> "biome";
	public static final TypeReference MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = () -> "multi_noise_biome_source_parameter_list";
	/**
	 * A type reference which refers to world gen settings.
	 */
	public static final TypeReference WORLD_GEN_SETTINGS = () -> "world_gen_settings";
}
