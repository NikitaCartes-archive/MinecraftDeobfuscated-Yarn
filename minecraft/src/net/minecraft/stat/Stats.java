package net.minecraft.stat;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Stats {
	public static final StatType<Block> field_15427 = method_15020("mined", Registry.BLOCK);
	public static final StatType<Item> field_15370 = method_15020("crafted", Registry.ITEM);
	public static final StatType<Item> field_15372 = method_15020("used", Registry.ITEM);
	public static final StatType<Item> field_15383 = method_15020("broken", Registry.ITEM);
	public static final StatType<Item> field_15392 = method_15020("picked_up", Registry.ITEM);
	public static final StatType<Item> field_15405 = method_15020("dropped", Registry.ITEM);
	public static final StatType<EntityType<?>> field_15403 = method_15020("killed", Registry.ENTITY_TYPE);
	public static final StatType<EntityType<?>> field_15411 = method_15020("killed_by", Registry.ENTITY_TYPE);
	public static final StatType<Identifier> field_15419 = method_15020("custom", Registry.CUSTOM_STAT);
	public static final Identifier field_15389 = method_15021("leave_game", StatFormatter.DEFAULT);
	public static final Identifier field_15417 = method_15021("play_one_minute", StatFormatter.TIME);
	public static final Identifier field_15400 = method_15021("time_since_death", StatFormatter.TIME);
	public static final Identifier field_15429 = method_15021("time_since_rest", StatFormatter.TIME);
	public static final Identifier field_15422 = method_15021("sneak_time", StatFormatter.TIME);
	public static final Identifier field_15377 = method_15021("walk_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15376 = method_15021("crouch_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15364 = method_15021("sprint_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15394 = method_15021("walk_on_water_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15386 = method_15021("fall_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15413 = method_15021("climb_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15426 = method_15021("fly_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15401 = method_15021("walk_under_water_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15409 = method_15021("minecart_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15415 = method_15021("boat_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15387 = method_15021("pig_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15396 = method_15021("horse_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15374 = method_15021("aviate_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15423 = method_15021("swim_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15428 = method_15021("jump", StatFormatter.DEFAULT);
	public static final Identifier field_15406 = method_15021("drop", StatFormatter.DEFAULT);
	public static final Identifier field_15399 = method_15021("damage_dealt", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15408 = method_15021("damage_dealt_absorbed", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15397 = method_15021("damage_dealt_resisted", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15388 = method_15021("damage_taken", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15380 = method_15021("damage_blocked_by_shield", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15365 = method_15021("damage_absorbed", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15425 = method_15021("damage_resisted", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15421 = method_15021("deaths", StatFormatter.DEFAULT);
	public static final Identifier field_15414 = method_15021("mob_kills", StatFormatter.DEFAULT);
	public static final Identifier field_15410 = method_15021("animals_bred", StatFormatter.DEFAULT);
	public static final Identifier field_15404 = method_15021("player_kills", StatFormatter.DEFAULT);
	public static final Identifier field_15391 = method_15021("fish_caught", StatFormatter.DEFAULT);
	public static final Identifier field_15384 = method_15021("talked_to_villager", StatFormatter.DEFAULT);
	public static final Identifier field_15378 = method_15021("traded_with_villager", StatFormatter.DEFAULT);
	public static final Identifier field_15369 = method_15021("eat_cake_slice", StatFormatter.DEFAULT);
	public static final Identifier field_15430 = method_15021("fill_cauldron", StatFormatter.DEFAULT);
	public static final Identifier field_15373 = method_15021("use_cauldron", StatFormatter.DEFAULT);
	public static final Identifier field_15382 = method_15021("clean_armor", StatFormatter.DEFAULT);
	public static final Identifier field_15390 = method_15021("clean_banner", StatFormatter.DEFAULT);
	public static final Identifier field_15398 = method_15021("clean_shulker_box", StatFormatter.DEFAULT);
	public static final Identifier field_15407 = method_15021("interact_with_brewingstand", StatFormatter.DEFAULT);
	public static final Identifier field_15416 = method_15021("interact_with_beacon", StatFormatter.DEFAULT);
	public static final Identifier field_15367 = method_15021("inspect_dropper", StatFormatter.DEFAULT);
	public static final Identifier field_15366 = method_15021("inspect_hopper", StatFormatter.DEFAULT);
	public static final Identifier field_15371 = method_15021("inspect_dispenser", StatFormatter.DEFAULT);
	public static final Identifier field_15385 = method_15021("play_noteblock", StatFormatter.DEFAULT);
	public static final Identifier field_15393 = method_15021("tune_noteblock", StatFormatter.DEFAULT);
	public static final Identifier field_15412 = method_15021("pot_flower", StatFormatter.DEFAULT);
	public static final Identifier field_15402 = method_15021("trigger_trapped_chest", StatFormatter.DEFAULT);
	public static final Identifier field_15424 = method_15021("open_enderchest", StatFormatter.DEFAULT);
	public static final Identifier field_15420 = method_15021("enchant_item", StatFormatter.DEFAULT);
	public static final Identifier field_15375 = method_15021("play_record", StatFormatter.DEFAULT);
	public static final Identifier field_15379 = method_15021("interact_with_furnace", StatFormatter.DEFAULT);
	public static final Identifier field_15368 = method_15021("interact_with_crafting_table", StatFormatter.DEFAULT);
	public static final Identifier field_15395 = method_15021("open_chest", StatFormatter.DEFAULT);
	public static final Identifier field_15381 = method_15021("sleep_in_bed", StatFormatter.DEFAULT);
	public static final Identifier field_15418 = method_15021("open_shulker_box", StatFormatter.DEFAULT);

	private static Identifier method_15021(String string, StatFormatter statFormatter) {
		Identifier identifier = new Identifier(string);
		Registry.register(Registry.CUSTOM_STAT, string, identifier);
		field_15419.method_14955(identifier, statFormatter);
		return identifier;
	}

	private static <T> StatType<T> method_15020(String string, Registry<T> registry) {
		return Registry.register(Registry.STAT_TYPE, string, new StatType<>(registry));
	}
}
