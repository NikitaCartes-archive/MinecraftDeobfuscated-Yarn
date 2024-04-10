package net.minecraft.datafixer;

import com.mojang.datafixers.DSL.TypeReference;

/**
 * Represents all the type references Minecraft's datafixer can fix.
 */
public class TypeReferences {
	public static final TypeReference LEVEL = create("level");
	/**
	 * A type reference which refers to a player.
	 */
	public static final TypeReference PLAYER = create("player");
	/**
	 * A type reference which refers to a chunk.
	 */
	public static final TypeReference CHUNK = create("chunk");
	/**
	 * A type reference which refers to the saved creative hotbars.
	 * 
	 * <p>This type reference is only used on the client.
	 */
	public static final TypeReference HOTBAR = create("hotbar");
	/**
	 * A type reference which refers to client game options.
	 */
	public static final TypeReference OPTIONS = create("options");
	public static final TypeReference STRUCTURE = create("structure");
	public static final TypeReference STATS = create("stats");
	public static final TypeReference SAVED_DATA_COMMAND_STORAGE = create("saved_data/command_storage");
	public static final TypeReference SAVED_DATA_CHUNKS = create("saved_data/chunks");
	public static final TypeReference SAVED_DATA_MAP_DATA = create("saved_data/map_data");
	public static final TypeReference SAVED_DATA_IDCOUNTS = create("saved_data/idcounts");
	public static final TypeReference SAVED_DATA_RAIDS = create("saved_data/raids");
	public static final TypeReference SAVED_DATA_RANDOM_SEQUENCES = create("saved_data/random_sequences");
	public static final TypeReference SAVED_DATA_STRUCTURE_FEATURE_INDICES = create("saved_data/structure_feature_indices");
	public static final TypeReference SAVED_DATA_SCOREBOARD = create("saved_data/scoreboard");
	public static final TypeReference ADVANCEMENTS = create("advancements");
	/**
	 * A type reference which refers to the point of interest data in a chunk.
	 */
	public static final TypeReference POI_CHUNK = create("poi_chunk");
	/**
	 * A type reference which refers to the entity data in a chunk.
	 */
	public static final TypeReference ENTITY_CHUNK = create("entity_chunk");
	/**
	 * A type reference which refers to a block entity.
	 */
	public static final TypeReference BLOCK_ENTITY = create("block_entity");
	/**
	 * A type reference which refers to an item stack.
	 */
	public static final TypeReference ITEM_STACK = create("item_stack");
	/**
	 * A type reference which refers to a block state.
	 */
	public static final TypeReference BLOCK_STATE = create("block_state");
	public static final TypeReference FLAT_BLOCK_STATE = create("flat_block_state");
	public static final TypeReference DATA_COMPONENTS = create("data_components");
	public static final TypeReference VILLAGER_TRADE = create("villager_trade");
	public static final TypeReference PARTICLE = create("particle");
	/**
	 * A type reference which refers to an entity's identifier.
	 */
	public static final TypeReference ENTITY_NAME = create("entity_name");
	/**
	 * A type reference which refers to an entity tree.
	 * 
	 * <p>An entity tree contains the passengers of an entity and their passengers.
	 */
	public static final TypeReference ENTITY_TREE = create("entity_tree");
	/**
	 * A type reference which refers to a type of entity.
	 */
	public static final TypeReference ENTITY = create("entity");
	/**
	 * A type reference which refers to a block's identifier.
	 */
	public static final TypeReference BLOCK_NAME = create("block_name");
	/**
	 * A type reference which refers to an item's identifier.
	 */
	public static final TypeReference ITEM_NAME = create("item_name");
	public static final TypeReference GAME_EVENT_NAME = create("game_event_name");
	public static final TypeReference UNTAGGED_SPAWNER = create("untagged_spawner");
	public static final TypeReference STRUCTURE_FEATURE = create("structure_feature");
	public static final TypeReference OBJECTIVE = create("objective");
	public static final TypeReference TEAM = create("team");
	public static final TypeReference RECIPE = create("recipe");
	/**
	 * A type reference which refers to a biome.
	 */
	public static final TypeReference BIOME = create("biome");
	public static final TypeReference MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = create("multi_noise_biome_source_parameter_list");
	/**
	 * A type reference which refers to world gen settings.
	 */
	public static final TypeReference WORLD_GEN_SETTINGS = create("world_gen_settings");

	public static TypeReference create(String typeName) {
		return new TypeReference() {
			@Override
			public String typeName() {
				return typeName;
			}

			public String toString() {
				return "@" + typeName;
			}
		};
	}
}
