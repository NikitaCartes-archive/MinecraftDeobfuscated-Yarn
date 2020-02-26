/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.stat;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Stats {
    public static final StatType<Block> MINED = Stats.registerType("mined", Registry.BLOCK);
    public static final StatType<Item> CRAFTED = Stats.registerType("crafted", Registry.ITEM);
    public static final StatType<Item> USED = Stats.registerType("used", Registry.ITEM);
    public static final StatType<Item> BROKEN = Stats.registerType("broken", Registry.ITEM);
    public static final StatType<Item> PICKED_UP = Stats.registerType("picked_up", Registry.ITEM);
    public static final StatType<Item> DROPPED = Stats.registerType("dropped", Registry.ITEM);
    public static final StatType<EntityType<?>> KILLED = Stats.registerType("killed", Registry.ENTITY_TYPE);
    public static final StatType<EntityType<?>> KILLED_BY = Stats.registerType("killed_by", Registry.ENTITY_TYPE);
    public static final StatType<Identifier> CUSTOM = Stats.registerType("custom", Registry.CUSTOM_STAT);
    public static final Identifier LEAVE_GAME = Stats.register("leave_game", StatFormatter.DEFAULT);
    public static final Identifier PLAY_ONE_MINUTE = Stats.register("play_one_minute", StatFormatter.TIME);
    public static final Identifier TIME_SINCE_DEATH = Stats.register("time_since_death", StatFormatter.TIME);
    public static final Identifier TIME_SINCE_REST = Stats.register("time_since_rest", StatFormatter.TIME);
    public static final Identifier SNEAK_TIME = Stats.register("sneak_time", StatFormatter.TIME);
    public static final Identifier WALK_ONE_CM = Stats.register("walk_one_cm", StatFormatter.DISTANCE);
    public static final Identifier CROUCH_ONE_CM = Stats.register("crouch_one_cm", StatFormatter.DISTANCE);
    public static final Identifier SPRINT_ONE_CM = Stats.register("sprint_one_cm", StatFormatter.DISTANCE);
    public static final Identifier WALK_ON_WATER_ONE_CM = Stats.register("walk_on_water_one_cm", StatFormatter.DISTANCE);
    public static final Identifier FALL_ONE_CM = Stats.register("fall_one_cm", StatFormatter.DISTANCE);
    public static final Identifier CLIMB_ONE_CM = Stats.register("climb_one_cm", StatFormatter.DISTANCE);
    public static final Identifier FLY_ONE_CM = Stats.register("fly_one_cm", StatFormatter.DISTANCE);
    public static final Identifier WALK_UNDER_WATER_ONE_CM = Stats.register("walk_under_water_one_cm", StatFormatter.DISTANCE);
    public static final Identifier MINECART_ONE_CM = Stats.register("minecart_one_cm", StatFormatter.DISTANCE);
    public static final Identifier BOAT_ONE_CM = Stats.register("boat_one_cm", StatFormatter.DISTANCE);
    public static final Identifier PIG_ONE_CM = Stats.register("pig_one_cm", StatFormatter.DISTANCE);
    public static final Identifier HORSE_ONE_CM = Stats.register("horse_one_cm", StatFormatter.DISTANCE);
    public static final Identifier AVIATE_ONE_CM = Stats.register("aviate_one_cm", StatFormatter.DISTANCE);
    public static final Identifier SWIM_ONE_CM = Stats.register("swim_one_cm", StatFormatter.DISTANCE);
    public static final Identifier JUMP = Stats.register("jump", StatFormatter.DEFAULT);
    public static final Identifier DROP = Stats.register("drop", StatFormatter.DEFAULT);
    public static final Identifier DAMAGE_DEALT = Stats.register("damage_dealt", StatFormatter.DIVIDE_BY_TEN);
    public static final Identifier DAMAGE_DEALT_ABSORBED = Stats.register("damage_dealt_absorbed", StatFormatter.DIVIDE_BY_TEN);
    public static final Identifier DAMAGE_DEALT_RESISTED = Stats.register("damage_dealt_resisted", StatFormatter.DIVIDE_BY_TEN);
    public static final Identifier DAMAGE_TAKEN = Stats.register("damage_taken", StatFormatter.DIVIDE_BY_TEN);
    public static final Identifier DAMAGE_BLOCKED_BY_SHIELD = Stats.register("damage_blocked_by_shield", StatFormatter.DIVIDE_BY_TEN);
    public static final Identifier DAMAGE_ABSORBED = Stats.register("damage_absorbed", StatFormatter.DIVIDE_BY_TEN);
    public static final Identifier DAMAGE_RESISTED = Stats.register("damage_resisted", StatFormatter.DIVIDE_BY_TEN);
    public static final Identifier DEATHS = Stats.register("deaths", StatFormatter.DEFAULT);
    public static final Identifier MOB_KILLS = Stats.register("mob_kills", StatFormatter.DEFAULT);
    public static final Identifier ANIMALS_BRED = Stats.register("animals_bred", StatFormatter.DEFAULT);
    public static final Identifier PLAYER_KILLS = Stats.register("player_kills", StatFormatter.DEFAULT);
    public static final Identifier FISH_CAUGHT = Stats.register("fish_caught", StatFormatter.DEFAULT);
    public static final Identifier TALKED_TO_VILLAGER = Stats.register("talked_to_villager", StatFormatter.DEFAULT);
    public static final Identifier TRADED_WITH_VILLAGER = Stats.register("traded_with_villager", StatFormatter.DEFAULT);
    public static final Identifier EAT_CAKE_SLICE = Stats.register("eat_cake_slice", StatFormatter.DEFAULT);
    public static final Identifier FILL_CAULDRON = Stats.register("fill_cauldron", StatFormatter.DEFAULT);
    public static final Identifier USE_CAULDRON = Stats.register("use_cauldron", StatFormatter.DEFAULT);
    public static final Identifier CLEAN_ARMOR = Stats.register("clean_armor", StatFormatter.DEFAULT);
    public static final Identifier CLEAN_BANNER = Stats.register("clean_banner", StatFormatter.DEFAULT);
    public static final Identifier CLEAN_SHULKER_BOX = Stats.register("clean_shulker_box", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_BREWINGSTAND = Stats.register("interact_with_brewingstand", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_BEACON = Stats.register("interact_with_beacon", StatFormatter.DEFAULT);
    public static final Identifier INSPECT_DROPPER = Stats.register("inspect_dropper", StatFormatter.DEFAULT);
    public static final Identifier INSPECT_HOPPER = Stats.register("inspect_hopper", StatFormatter.DEFAULT);
    public static final Identifier INSPECT_DISPENSER = Stats.register("inspect_dispenser", StatFormatter.DEFAULT);
    public static final Identifier PLAY_NOTEBLOCK = Stats.register("play_noteblock", StatFormatter.DEFAULT);
    public static final Identifier TUNE_NOTEBLOCK = Stats.register("tune_noteblock", StatFormatter.DEFAULT);
    public static final Identifier POT_FLOWER = Stats.register("pot_flower", StatFormatter.DEFAULT);
    public static final Identifier TRIGGER_TRAPPED_CHEST = Stats.register("trigger_trapped_chest", StatFormatter.DEFAULT);
    public static final Identifier OPEN_ENDERCHEST = Stats.register("open_enderchest", StatFormatter.DEFAULT);
    public static final Identifier ENCHANT_ITEM = Stats.register("enchant_item", StatFormatter.DEFAULT);
    public static final Identifier PLAY_RECORD = Stats.register("play_record", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_FURNACE = Stats.register("interact_with_furnace", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_CRAFTING_TABLE = Stats.register("interact_with_crafting_table", StatFormatter.DEFAULT);
    public static final Identifier OPEN_CHEST = Stats.register("open_chest", StatFormatter.DEFAULT);
    public static final Identifier SLEEP_IN_BED = Stats.register("sleep_in_bed", StatFormatter.DEFAULT);
    public static final Identifier OPEN_SHULKER_BOX = Stats.register("open_shulker_box", StatFormatter.DEFAULT);
    public static final Identifier OPEN_BARREL = Stats.register("open_barrel", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_BLAST_FURNACE = Stats.register("interact_with_blast_furnace", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_SMOKER = Stats.register("interact_with_smoker", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_LECTERN = Stats.register("interact_with_lectern", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_CAMPFIRE = Stats.register("interact_with_campfire", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_CARTOGRAPHY_TABLE = Stats.register("interact_with_cartography_table", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_LOOM = Stats.register("interact_with_loom", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_STONECUTTER = Stats.register("interact_with_stonecutter", StatFormatter.DEFAULT);
    public static final Identifier BELL_RING = Stats.register("bell_ring", StatFormatter.DEFAULT);
    public static final Identifier RAID_TRIGGER = Stats.register("raid_trigger", StatFormatter.DEFAULT);
    public static final Identifier RAID_WIN = Stats.register("raid_win", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_ANVIL = Stats.register("interact_with_anvil", StatFormatter.DEFAULT);
    public static final Identifier INTERACT_WITH_GRINDSTONE = Stats.register("interact_with_grindstone", StatFormatter.DEFAULT);
    public static final Identifier TARGET_HIT = Stats.register("target_hit", StatFormatter.DEFAULT);

    private static Identifier register(String string, StatFormatter statFormatter) {
        Identifier identifier = new Identifier(string);
        Registry.register(Registry.CUSTOM_STAT, string, identifier);
        CUSTOM.getOrCreateStat(identifier, statFormatter);
        return identifier;
    }

    private static <T> StatType<T> registerType(String string, Registry<T> registry) {
        return Registry.register(Registry.STAT_TYPE, string, new StatType<T>(registry));
    }
}

