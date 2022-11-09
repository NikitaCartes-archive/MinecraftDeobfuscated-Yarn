package net.minecraft.stat;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Stats {
	public static final StatType<Block> MINED = registerType("mined", Registries.BLOCK);
	public static final StatType<Item> CRAFTED = registerType("crafted", Registries.ITEM);
	public static final StatType<Item> USED = registerType("used", Registries.ITEM);
	public static final StatType<Item> BROKEN = registerType("broken", Registries.ITEM);
	public static final StatType<Item> PICKED_UP = registerType("picked_up", Registries.ITEM);
	public static final StatType<Item> DROPPED = registerType("dropped", Registries.ITEM);
	public static final StatType<EntityType<?>> KILLED = registerType("killed", Registries.ENTITY_TYPE);
	public static final StatType<EntityType<?>> KILLED_BY = registerType("killed_by", Registries.ENTITY_TYPE);
	public static final StatType<Identifier> CUSTOM = registerType("custom", Registries.CUSTOM_STAT);
	public static final Identifier LEAVE_GAME = register("leave_game", StatFormatter.DEFAULT);
	public static final Identifier PLAY_TIME = register("play_time", StatFormatter.TIME);
	public static final Identifier TOTAL_WORLD_TIME = register("total_world_time", StatFormatter.TIME);
	public static final Identifier TIME_SINCE_DEATH = register("time_since_death", StatFormatter.TIME);
	public static final Identifier TIME_SINCE_REST = register("time_since_rest", StatFormatter.TIME);
	public static final Identifier SNEAK_TIME = register("sneak_time", StatFormatter.TIME);
	public static final Identifier WALK_ONE_CM = register("walk_one_cm", StatFormatter.DISTANCE);
	public static final Identifier CROUCH_ONE_CM = register("crouch_one_cm", StatFormatter.DISTANCE);
	public static final Identifier SPRINT_ONE_CM = register("sprint_one_cm", StatFormatter.DISTANCE);
	public static final Identifier WALK_ON_WATER_ONE_CM = register("walk_on_water_one_cm", StatFormatter.DISTANCE);
	public static final Identifier FALL_ONE_CM = register("fall_one_cm", StatFormatter.DISTANCE);
	public static final Identifier CLIMB_ONE_CM = register("climb_one_cm", StatFormatter.DISTANCE);
	public static final Identifier FLY_ONE_CM = register("fly_one_cm", StatFormatter.DISTANCE);
	public static final Identifier WALK_UNDER_WATER_ONE_CM = register("walk_under_water_one_cm", StatFormatter.DISTANCE);
	public static final Identifier MINECART_ONE_CM = register("minecart_one_cm", StatFormatter.DISTANCE);
	public static final Identifier BOAT_ONE_CM = register("boat_one_cm", StatFormatter.DISTANCE);
	public static final Identifier PIG_ONE_CM = register("pig_one_cm", StatFormatter.DISTANCE);
	public static final Identifier HORSE_ONE_CM = register("horse_one_cm", StatFormatter.DISTANCE);
	public static final Identifier AVIATE_ONE_CM = register("aviate_one_cm", StatFormatter.DISTANCE);
	public static final Identifier SWIM_ONE_CM = register("swim_one_cm", StatFormatter.DISTANCE);
	public static final Identifier STRIDER_ONE_CM = register("strider_one_cm", StatFormatter.DISTANCE);
	public static final Identifier JUMP = register("jump", StatFormatter.DEFAULT);
	public static final Identifier DROP = register("drop", StatFormatter.DEFAULT);
	public static final Identifier DAMAGE_DEALT = register("damage_dealt", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_DEALT_ABSORBED = register("damage_dealt_absorbed", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_DEALT_RESISTED = register("damage_dealt_resisted", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_TAKEN = register("damage_taken", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_BLOCKED_BY_SHIELD = register("damage_blocked_by_shield", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_ABSORBED = register("damage_absorbed", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier DAMAGE_RESISTED = register("damage_resisted", StatFormatter.DIVIDE_BY_TEN);
	public static final Identifier DEATHS = register("deaths", StatFormatter.DEFAULT);
	public static final Identifier MOB_KILLS = register("mob_kills", StatFormatter.DEFAULT);
	public static final Identifier ANIMALS_BRED = register("animals_bred", StatFormatter.DEFAULT);
	public static final Identifier PLAYER_KILLS = register("player_kills", StatFormatter.DEFAULT);
	public static final Identifier FISH_CAUGHT = register("fish_caught", StatFormatter.DEFAULT);
	public static final Identifier TALKED_TO_VILLAGER = register("talked_to_villager", StatFormatter.DEFAULT);
	public static final Identifier TRADED_WITH_VILLAGER = register("traded_with_villager", StatFormatter.DEFAULT);
	public static final Identifier EAT_CAKE_SLICE = register("eat_cake_slice", StatFormatter.DEFAULT);
	public static final Identifier FILL_CAULDRON = register("fill_cauldron", StatFormatter.DEFAULT);
	public static final Identifier USE_CAULDRON = register("use_cauldron", StatFormatter.DEFAULT);
	public static final Identifier CLEAN_ARMOR = register("clean_armor", StatFormatter.DEFAULT);
	public static final Identifier CLEAN_BANNER = register("clean_banner", StatFormatter.DEFAULT);
	public static final Identifier CLEAN_SHULKER_BOX = register("clean_shulker_box", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_BREWINGSTAND = register("interact_with_brewingstand", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_BEACON = register("interact_with_beacon", StatFormatter.DEFAULT);
	public static final Identifier INSPECT_DROPPER = register("inspect_dropper", StatFormatter.DEFAULT);
	public static final Identifier INSPECT_HOPPER = register("inspect_hopper", StatFormatter.DEFAULT);
	public static final Identifier INSPECT_DISPENSER = register("inspect_dispenser", StatFormatter.DEFAULT);
	public static final Identifier PLAY_NOTEBLOCK = register("play_noteblock", StatFormatter.DEFAULT);
	public static final Identifier TUNE_NOTEBLOCK = register("tune_noteblock", StatFormatter.DEFAULT);
	public static final Identifier POT_FLOWER = register("pot_flower", StatFormatter.DEFAULT);
	public static final Identifier TRIGGER_TRAPPED_CHEST = register("trigger_trapped_chest", StatFormatter.DEFAULT);
	public static final Identifier OPEN_ENDERCHEST = register("open_enderchest", StatFormatter.DEFAULT);
	public static final Identifier ENCHANT_ITEM = register("enchant_item", StatFormatter.DEFAULT);
	public static final Identifier PLAY_RECORD = register("play_record", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_FURNACE = register("interact_with_furnace", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_CRAFTING_TABLE = register("interact_with_crafting_table", StatFormatter.DEFAULT);
	public static final Identifier OPEN_CHEST = register("open_chest", StatFormatter.DEFAULT);
	public static final Identifier SLEEP_IN_BED = register("sleep_in_bed", StatFormatter.DEFAULT);
	public static final Identifier OPEN_SHULKER_BOX = register("open_shulker_box", StatFormatter.DEFAULT);
	public static final Identifier OPEN_BARREL = register("open_barrel", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_BLAST_FURNACE = register("interact_with_blast_furnace", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_SMOKER = register("interact_with_smoker", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_LECTERN = register("interact_with_lectern", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_CAMPFIRE = register("interact_with_campfire", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_CARTOGRAPHY_TABLE = register("interact_with_cartography_table", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_LOOM = register("interact_with_loom", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_STONECUTTER = register("interact_with_stonecutter", StatFormatter.DEFAULT);
	public static final Identifier BELL_RING = register("bell_ring", StatFormatter.DEFAULT);
	public static final Identifier RAID_TRIGGER = register("raid_trigger", StatFormatter.DEFAULT);
	public static final Identifier RAID_WIN = register("raid_win", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_ANVIL = register("interact_with_anvil", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_GRINDSTONE = register("interact_with_grindstone", StatFormatter.DEFAULT);
	public static final Identifier TARGET_HIT = register("target_hit", StatFormatter.DEFAULT);
	public static final Identifier INTERACT_WITH_SMITHING_TABLE = register("interact_with_smithing_table", StatFormatter.DEFAULT);

	private static Identifier register(String id, StatFormatter formatter) {
		Identifier identifier = new Identifier(id);
		Registry.register(Registries.CUSTOM_STAT, id, identifier);
		CUSTOM.getOrCreateStat(identifier, formatter);
		return identifier;
	}

	private static <T> StatType<T> registerType(String id, Registry<T> registry) {
		return Registry.register(Registries.STAT_TYPE, id, new StatType<>(registry));
	}
}
