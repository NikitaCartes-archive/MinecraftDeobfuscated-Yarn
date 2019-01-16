package net.minecraft.datafixers;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DSL.TypeReference;

public class TypeReferences {
	public static final TypeReference LEVEL = DataFixTypes.LEVEL;
	public static final TypeReference PLAYER = DataFixTypes.PLAYER;
	public static final TypeReference CHUNK = DataFixTypes.CHUNK;
	public static final TypeReference HOTBAR = DataFixTypes.HOTBAR;
	public static final TypeReference OPTIONS = DataFixTypes.OPTIONS;
	public static final TypeReference STRUCTURE = DataFixTypes.STRUCTURE;
	public static final TypeReference STATS = DataFixTypes.STATS;
	public static final TypeReference SAVED_DATA = DataFixTypes.SAVED_DATA;
	public static final TypeReference ADVANCEMENTS = DataFixTypes.ADVANCEMENTS;
	public static final TypeReference BLOCK_ENTITY = () -> "block_entity";
	public static final TypeReference ITEM_STACK = () -> "item_stack";
	public static final TypeReference BLOCK_STATE = () -> "block_state";
	public static final TypeReference ENTITY_NAME = () -> "entity_name";
	public static final TypeReference ENTITY_TREE = () -> "entity_tree";
	public static final TypeReference ENTITY = () -> "entity";
	public static final TypeReference BLOCK_NAME = () -> "block_name";
	public static final TypeReference ITEM_NAME = () -> "item_name";
	public static final TypeReference UNTAGGED_SPAWNER = () -> "untagged_spawner";
	public static final TypeReference STRUCTURE_FEATURE = () -> "structure_feature";
	public static final TypeReference OBJECTIVE = () -> "objective";
	public static final TypeReference TEAM = () -> "team";
	public static final TypeReference RECIPE = () -> "recipe";
	public static final TypeReference BIOME = () -> "biome";
}
