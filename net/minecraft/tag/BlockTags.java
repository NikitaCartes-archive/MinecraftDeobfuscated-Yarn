/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;

public final class BlockTags {
    protected static final RequiredTagList<Block> REQUIRED_TAGS = RequiredTagListRegistry.register(new Identifier("block"), TagManager::getBlocks);
    public static final Tag.Identified<Block> WOOL = BlockTags.register("wool");
    public static final Tag.Identified<Block> PLANKS = BlockTags.register("planks");
    public static final Tag.Identified<Block> STONE_BRICKS = BlockTags.register("stone_bricks");
    public static final Tag.Identified<Block> WOODEN_BUTTONS = BlockTags.register("wooden_buttons");
    public static final Tag.Identified<Block> BUTTONS = BlockTags.register("buttons");
    public static final Tag.Identified<Block> CARPETS = BlockTags.register("carpets");
    public static final Tag.Identified<Block> WOODEN_DOORS = BlockTags.register("wooden_doors");
    public static final Tag.Identified<Block> WOODEN_STAIRS = BlockTags.register("wooden_stairs");
    public static final Tag.Identified<Block> WOODEN_SLABS = BlockTags.register("wooden_slabs");
    public static final Tag.Identified<Block> WOODEN_FENCES = BlockTags.register("wooden_fences");
    public static final Tag.Identified<Block> PRESSURE_PLATES = BlockTags.register("pressure_plates");
    public static final Tag.Identified<Block> WOODEN_PRESSURE_PLATES = BlockTags.register("wooden_pressure_plates");
    public static final Tag.Identified<Block> STONE_PRESSURE_PLATES = BlockTags.register("stone_pressure_plates");
    public static final Tag.Identified<Block> WOODEN_TRAPDOORS = BlockTags.register("wooden_trapdoors");
    public static final Tag.Identified<Block> DOORS = BlockTags.register("doors");
    public static final Tag.Identified<Block> SAPLINGS = BlockTags.register("saplings");
    public static final Tag.Identified<Block> LOGS_THAT_BURN = BlockTags.register("logs_that_burn");
    public static final Tag.Identified<Block> LOGS = BlockTags.register("logs");
    public static final Tag.Identified<Block> DARK_OAK_LOGS = BlockTags.register("dark_oak_logs");
    public static final Tag.Identified<Block> OAK_LOGS = BlockTags.register("oak_logs");
    public static final Tag.Identified<Block> BIRCH_LOGS = BlockTags.register("birch_logs");
    public static final Tag.Identified<Block> ACACIA_LOGS = BlockTags.register("acacia_logs");
    public static final Tag.Identified<Block> JUNGLE_LOGS = BlockTags.register("jungle_logs");
    public static final Tag.Identified<Block> SPRUCE_LOGS = BlockTags.register("spruce_logs");
    public static final Tag.Identified<Block> CRIMSON_STEMS = BlockTags.register("crimson_stems");
    public static final Tag.Identified<Block> WARPED_STEMS = BlockTags.register("warped_stems");
    public static final Tag.Identified<Block> BANNERS = BlockTags.register("banners");
    public static final Tag.Identified<Block> SAND = BlockTags.register("sand");
    public static final Tag.Identified<Block> STAIRS = BlockTags.register("stairs");
    public static final Tag.Identified<Block> SLABS = BlockTags.register("slabs");
    public static final Tag.Identified<Block> WALLS = BlockTags.register("walls");
    public static final Tag.Identified<Block> ANVIL = BlockTags.register("anvil");
    public static final Tag.Identified<Block> RAILS = BlockTags.register("rails");
    public static final Tag.Identified<Block> LEAVES = BlockTags.register("leaves");
    public static final Tag.Identified<Block> TRAPDOORS = BlockTags.register("trapdoors");
    public static final Tag.Identified<Block> SMALL_FLOWERS = BlockTags.register("small_flowers");
    public static final Tag.Identified<Block> BEDS = BlockTags.register("beds");
    public static final Tag.Identified<Block> FENCES = BlockTags.register("fences");
    public static final Tag.Identified<Block> TALL_FLOWERS = BlockTags.register("tall_flowers");
    public static final Tag.Identified<Block> FLOWERS = BlockTags.register("flowers");
    public static final Tag.Identified<Block> PIGLIN_REPELLENTS = BlockTags.register("piglin_repellents");
    public static final Tag.Identified<Block> GOLD_ORES = BlockTags.register("gold_ores");
    public static final Tag.Identified<Block> NON_FLAMMABLE_WOOD = BlockTags.register("non_flammable_wood");
    public static final Tag.Identified<Block> CANDLES = BlockTags.register("candles");
    public static final Tag.Identified<Block> FLOWER_POTS = BlockTags.register("flower_pots");
    public static final Tag.Identified<Block> ENDERMAN_HOLDABLE = BlockTags.register("enderman_holdable");
    public static final Tag.Identified<Block> ICE = BlockTags.register("ice");
    public static final Tag.Identified<Block> VALID_SPAWN = BlockTags.register("valid_spawn");
    public static final Tag.Identified<Block> IMPERMEABLE = BlockTags.register("impermeable");
    public static final Tag.Identified<Block> UNDERWATER_BONEMEALS = BlockTags.register("underwater_bonemeals");
    public static final Tag.Identified<Block> CORAL_BLOCKS = BlockTags.register("coral_blocks");
    public static final Tag.Identified<Block> WALL_CORALS = BlockTags.register("wall_corals");
    public static final Tag.Identified<Block> CORAL_PLANTS = BlockTags.register("coral_plants");
    public static final Tag.Identified<Block> CORALS = BlockTags.register("corals");
    public static final Tag.Identified<Block> BAMBOO_PLANTABLE_ON = BlockTags.register("bamboo_plantable_on");
    public static final Tag.Identified<Block> STANDING_SIGNS = BlockTags.register("standing_signs");
    public static final Tag.Identified<Block> WALL_SIGNS = BlockTags.register("wall_signs");
    public static final Tag.Identified<Block> SIGNS = BlockTags.register("signs");
    public static final Tag.Identified<Block> DRAGON_IMMUNE = BlockTags.register("dragon_immune");
    public static final Tag.Identified<Block> WITHER_IMMUNE = BlockTags.register("wither_immune");
    public static final Tag.Identified<Block> WITHER_SUMMON_BASE_BLOCKS = BlockTags.register("wither_summon_base_blocks");
    public static final Tag.Identified<Block> BEEHIVES = BlockTags.register("beehives");
    public static final Tag.Identified<Block> CROPS = BlockTags.register("crops");
    public static final Tag.Identified<Block> BEE_GROWABLES = BlockTags.register("bee_growables");
    public static final Tag.Identified<Block> PORTALS = BlockTags.register("portals");
    public static final Tag.Identified<Block> FIRE = BlockTags.register("fire");
    public static final Tag.Identified<Block> NYLIUM = BlockTags.register("nylium");
    public static final Tag.Identified<Block> WART_BLOCKS = BlockTags.register("wart_blocks");
    public static final Tag.Identified<Block> BEACON_BASE_BLOCKS = BlockTags.register("beacon_base_blocks");
    public static final Tag.Identified<Block> SOUL_SPEED_BLOCKS = BlockTags.register("soul_speed_blocks");
    public static final Tag.Identified<Block> WALL_POST_OVERRIDE = BlockTags.register("wall_post_override");
    public static final Tag.Identified<Block> CLIMBABLE = BlockTags.register("climbable");
    public static final Tag.Identified<Block> SHULKER_BOXES = BlockTags.register("shulker_boxes");
    public static final Tag.Identified<Block> HOGLIN_REPELLENTS = BlockTags.register("hoglin_repellents");
    public static final Tag.Identified<Block> SOUL_FIRE_BASE_BLOCKS = BlockTags.register("soul_fire_base_blocks");
    public static final Tag.Identified<Block> STRIDER_WARM_BLOCKS = BlockTags.register("strider_warm_blocks");
    public static final Tag.Identified<Block> CAMPFIRES = BlockTags.register("campfires");
    public static final Tag.Identified<Block> GUARDED_BY_PIGLINS = BlockTags.register("guarded_by_piglins");
    public static final Tag.Identified<Block> PREVENT_MOB_SPAWNING_INSIDE = BlockTags.register("prevent_mob_spawning_inside");
    public static final Tag.Identified<Block> FENCE_GATES = BlockTags.register("fence_gates");
    public static final Tag.Identified<Block> UNSTABLE_BOTTOM_CENTER = BlockTags.register("unstable_bottom_center");
    public static final Tag.Identified<Block> MUSHROOM_GROW_BLOCK = BlockTags.register("mushroom_grow_block");
    public static final Tag.Identified<Block> INFINIBURN_OVERWORLD = BlockTags.register("infiniburn_overworld");
    public static final Tag.Identified<Block> INFINIBURN_NETHER = BlockTags.register("infiniburn_nether");
    public static final Tag.Identified<Block> INFINIBURN_END = BlockTags.register("infiniburn_end");
    public static final Tag.Identified<Block> BASE_STONE_OVERWORLD = BlockTags.register("base_stone_overworld");
    public static final Tag.Identified<Block> BASE_STONE_NETHER = BlockTags.register("base_stone_nether");
    public static final Tag.Identified<Block> CANDLE_CAKES = BlockTags.register("candle_cakes");
    public static final Tag.Identified<Block> CAULDRONS = BlockTags.register("cauldrons");
    public static final Tag.Identified<Block> CRYSTAL_SOUND_BLOCKS = BlockTags.register("crystal_sound_blocks");

    private static Tag.Identified<Block> register(String id) {
        return REQUIRED_TAGS.add(id);
    }

    public static TagGroup<Block> getTagGroup() {
        return REQUIRED_TAGS.getGroup();
    }

    public static List<? extends Tag.Identified<Block>> getRequiredTags() {
        return REQUIRED_TAGS.getTags();
    }
}

