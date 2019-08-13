package net.minecraft.stat;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Stats {
	public static final StatType<Block> field_15427 = registerType("mined", Registry.BLOCK);
	public static final StatType<Item> field_15370 = registerType("crafted", Registry.ITEM);
	public static final StatType<Item> field_15372 = registerType("used", Registry.ITEM);
	public static final StatType<Item> field_15383 = registerType("broken", Registry.ITEM);
	public static final StatType<Item> field_15392 = registerType("picked_up", Registry.ITEM);
	public static final StatType<Item> field_15405 = registerType("dropped", Registry.ITEM);
	public static final StatType<EntityType<?>> field_15403 = registerType("killed", Registry.ENTITY_TYPE);
	public static final StatType<EntityType<?>> field_15411 = registerType("killed_by", Registry.ENTITY_TYPE);
	public static final StatType<Identifier> field_15419 = registerType("custom", Registry.CUSTOM_STAT);
	public static final Identifier field_15389 = register("leave_game", StatFormatter.DEFAULT);
	public static final Identifier field_15417 = register("play_one_minute", StatFormatter.TIME);
	public static final Identifier field_15400 = register("time_since_death", StatFormatter.TIME);
	public static final Identifier field_15429 = register("time_since_rest", StatFormatter.TIME);
	public static final Identifier field_15422 = register("sneak_time", StatFormatter.TIME);
	public static final Identifier field_15377 = register("walk_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15376 = register("crouch_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15364 = register("sprint_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15394 = register("walk_on_water_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15386 = register("fall_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15413 = register("climb_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15426 = register("fly_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15401 = register("walk_under_water_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15409 = register("minecart_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15415 = register("boat_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15387 = register("pig_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15396 = register("horse_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15374 = register("aviate_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15423 = register("swim_one_cm", StatFormatter.DISTANCE);
	public static final Identifier field_15428 = register("jump", StatFormatter.DEFAULT);
	public static final Identifier field_15406 = register("drop", StatFormatter.DEFAULT);
	public static final Identifier field_15399 = register("damage_dealt", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15408 = register("damage_dealt_absorbed", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15397 = register("damage_dealt_resisted", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15388 = register("damage_taken", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15380 = register("damage_blocked_by_shield", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15365 = register("damage_absorbed", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15425 = register("damage_resisted", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier field_15421 = register("deaths", StatFormatter.DEFAULT);
	public static final Identifier field_15414 = register("mob_kills", StatFormatter.DEFAULT);
	public static final Identifier field_15410 = register("animals_bred", StatFormatter.DEFAULT);
	public static final Identifier field_15404 = register("player_kills", StatFormatter.DEFAULT);
	public static final Identifier field_15391 = register("fish_caught", StatFormatter.DEFAULT);
	public static final Identifier field_15384 = register("talked_to_villager", StatFormatter.DEFAULT);
	public static final Identifier field_15378 = register("traded_with_villager", StatFormatter.DEFAULT);
	public static final Identifier field_15369 = register("eat_cake_slice", StatFormatter.DEFAULT);
	public static final Identifier field_15430 = register("fill_cauldron", StatFormatter.DEFAULT);
	public static final Identifier field_15373 = register("use_cauldron", StatFormatter.DEFAULT);
	public static final Identifier field_15382 = register("clean_armor", StatFormatter.DEFAULT);
	public static final Identifier field_15390 = register("clean_banner", StatFormatter.DEFAULT);
	public static final Identifier field_15398 = register("clean_shulker_box", StatFormatter.DEFAULT);
	public static final Identifier field_15407 = register("interact_with_brewingstand", StatFormatter.DEFAULT);
	public static final Identifier field_15416 = register("interact_with_beacon", StatFormatter.DEFAULT);
	public static final Identifier field_15367 = register("inspect_dropper", StatFormatter.DEFAULT);
	public static final Identifier field_15366 = register("inspect_hopper", StatFormatter.DEFAULT);
	public static final Identifier field_15371 = register("inspect_dispenser", StatFormatter.DEFAULT);
	public static final Identifier field_15385 = register("play_noteblock", StatFormatter.DEFAULT);
	public static final Identifier field_15393 = register("tune_noteblock", StatFormatter.DEFAULT);
	public static final Identifier field_15412 = register("pot_flower", StatFormatter.DEFAULT);
	public static final Identifier field_15402 = register("trigger_trapped_chest", StatFormatter.DEFAULT);
	public static final Identifier field_15424 = register("open_enderchest", StatFormatter.DEFAULT);
	public static final Identifier field_15420 = register("enchant_item", StatFormatter.DEFAULT);
	public static final Identifier field_15375 = register("play_record", StatFormatter.DEFAULT);
	public static final Identifier field_15379 = register("interact_with_furnace", StatFormatter.DEFAULT);
	public static final Identifier field_15368 = register("interact_with_crafting_table", StatFormatter.DEFAULT);
	public static final Identifier field_15395 = register("open_chest", StatFormatter.DEFAULT);
	public static final Identifier field_15381 = register("sleep_in_bed", StatFormatter.DEFAULT);
	public static final Identifier field_15418 = register("open_shulker_box", StatFormatter.DEFAULT);
	public static final Identifier field_17271 = register("open_barrel", StatFormatter.DEFAULT);
	public static final Identifier field_17272 = register("interact_with_blast_furnace", StatFormatter.DEFAULT);
	public static final Identifier field_17273 = register("interact_with_smoker", StatFormatter.DEFAULT);
	public static final Identifier field_17485 = register("interact_with_lectern", StatFormatter.DEFAULT);
	public static final Identifier field_17486 = register("interact_with_campfire", StatFormatter.DEFAULT);
	public static final Identifier field_19252 = register("interact_with_cartography_table", StatFormatter.DEFAULT);
	public static final Identifier field_19253 = register("interact_with_loom", StatFormatter.DEFAULT);
	public static final Identifier field_19254 = register("interact_with_stonecutter", StatFormatter.DEFAULT);
	public static final Identifier field_19255 = register("bell_ring", StatFormatter.DEFAULT);
	public static final Identifier field_19256 = register("raid_trigger", StatFormatter.DEFAULT);
	public static final Identifier field_19257 = register("raid_win", StatFormatter.DEFAULT);

	private static Identifier register(String string, StatFormatter statFormatter) {
		Identifier identifier = new Identifier(string);
		Registry.register(Registry.CUSTOM_STAT, string, identifier);
		field_15419.getOrCreateStat(identifier, statFormatter);
		return identifier;
	}

	private static <T> StatType<T> registerType(String string, Registry<T> registry) {
		return Registry.register(Registry.STAT_TYPE, string, new StatType<>(registry));
	}
}
